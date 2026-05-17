package com.mkarshnas6.karenstudio.worldskill.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.mkarshnas6.karenstudio.worldskill.R

class SimpleAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // روی همه ویجت‌های فعال حلقه بزن
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // ۱. RemoteViews رو از layout بساز
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // ۲. متن‌ها رو آپدیت کن
            views.setTextViewText(R.id.widget_title, "فروش امروز")
            views.setTextViewText(R.id.widget_data, "۱۲,۵۰۰,۰۰۰ تومان")

            // ۳. کلیک روی ویجت (اختیاری)
            val intent = context.packageManager
                .getLaunchIntentForPackage(context.packageName)
            val pendingIntent = android.app.PendingIntent.getActivity(
                context, 0, intent,
                android.app.PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_data, pendingIntent)

            // ۴. آپدیت ویجت
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}