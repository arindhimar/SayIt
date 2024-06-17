package com.example.dailyquotes

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

fun isAppInstalled(context: Context, packageName: String): Boolean {
    val pm: PackageManager = context.packageManager
    return try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun shareOnSocialMedia(context: Context, packageName: String, fallbackUrl: String, message: String) {
    var intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, message)

    if (isAppInstalled(context, packageName)) {
        intent.setPackage(packageName)
    } else {
        intent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
    }

    context.startActivity(Intent.createChooser(intent, "Share via"))
}
