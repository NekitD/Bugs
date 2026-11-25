package com.example.myapplication.gold

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.myapplication.R
import com.example.myapplication.gold.GoldRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class GoldWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        refresh(context, manager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, GoldWidgetProvider::class.java))
            refresh(context, manager, ids)
        }
    }

    private fun refresh(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val pending = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cached = GoldRepository.cachedRate(appContext)
                ids.forEach { manager.updateAppWidget(it, buildViews(appContext, cached)) }
                val rate = GoldRepository.fetchGoldRate(appContext)
                ids.forEach { manager.updateAppWidget(it, buildViews(appContext, rate)) }
            } finally {
                pending.finish()
            }
        }
    }

    private fun buildViews(context: Context, rate: Double?): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_gold)
        views.setTextViewText(
            R.id.tv_rate,
            rate?.let { String.format(Locale("ru", "RU"), "%,.2f", it) } ?: "—"
        )
        val intent = Intent(context, GoldWidgetProvider::class.java).setAction(ACTION_REFRESH)
        val pi = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pi) // тап по шару — обновить
        return views
    }

    companion object {
        const val ACTION_REFRESH = "com.example.myapplication.widget.ACTION_REFRESH"
    }
}
