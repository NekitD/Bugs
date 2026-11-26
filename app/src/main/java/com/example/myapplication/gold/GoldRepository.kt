package com.example.myapplication.gold

import android.content.Context
import android.util.Log
import android.util.Xml
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.core.content.edit
import java.net.URL
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

interface CbrApi {
    @GET("scripts/xml_metall.asp")
    suspend fun getMetals(
        @Query(value = "date_req1", encoded = true) from: String,
        @Query(value = "date_req2", encoded = true) to: String
    ): ResponseBody
}

object GoldRepository {
    private const val TAG = "GoldRepository"
    private const val BASE_URL = "https://www.cbr.ru/"
    private const val PREFS = "gold_prefs"
    private const val KEY_RATE = "rate"
    private const val GOLD_CODE = "1" // 1 — золото, 2 — серебро, 3 — платина, 4 — палладий

    private val api: CbrApi by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Android) MyApplication")
                        .build()
                )
            }
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .build()
            .create(CbrApi::class.java)
    }
    suspend fun fetchGoldRate(context: Context): Double? = withContext(Dispatchers.IO) {
        try {
            val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val to = fmt.format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, -12)
            val from = fmt.format(cal.time)

            val bytes = api.getMetals(from, to).use { it.bytes() }
            val rate = parseGold(bytes.inputStream())
            if (rate != null) {
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit { putString(KEY_RATE, rate.toString()) }
            }
            rate ?: cachedRate(context)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Не удалось загрузить курс золота", e)
            cachedRate(context)
        }
    }

    fun cachedRate(context: Context): Double? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_RATE, null)?.toDoubleOrNull()

    private fun parseGold(stream: InputStream): Double? {
        val parser = Xml.newPullParser()
        parser.setInput(stream, null)
        var code: String? = null
        var result: Double? = null
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "Record" -> code = parser.getAttributeValue(null, "Code")
                    "Buy" -> if (code == GOLD_CODE) {
                        parser.nextText()
                            .replace(Regex("[\\s\\u00A0]"), "")
                            .replace(',', '.')
                            .toDoubleOrNull()
                            ?.let { result = it }
                    }
                }
            }
            event = parser.next()
        }
        return result
    }
}
