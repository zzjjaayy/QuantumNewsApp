package com.jay.quantumnewsapp.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun CharSequence?.isValidEmail() =
    !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun EditText.hasErrorOrEmpty() = !error.isNullOrBlank() || text.toString().isBlank()

fun openWebpage(context: Context?, pageUrl: String) {
    val builder = CustomTabsIntent.Builder()
    try {
        Log.d(TAG, "openWebpage: $pageUrl")
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context!!, Uri.parse(pageUrl))
    } catch (e: Exception) {
        Log.d(TAG, "openWebpage: " + e.message)
        Toast.makeText(context, "Unable to open webpage!", Toast.LENGTH_SHORT).show()
    }
}

fun dateToTimeFormat(oldstringDate: String?): String? {
    val p = PrettyTime(Locale(getCountry()))
    var isTime: String? = null
    try {
        val sdf = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            Locale.ENGLISH
        )
        val date = sdf.parse(oldstringDate)
        isTime = p.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return isTime
}

fun getCountry(): String {
    val locale = Locale.getDefault()
    val country = java.lang.String.valueOf(locale.country)
    return country.lowercase(Locale.getDefault())
}