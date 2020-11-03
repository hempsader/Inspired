import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.inspired.MainActivity
import com.example.inspired.R
import com.example.inspired.api.QuoteFetch
import com.example.inspired.model.Quote
import com.example.inspired.util.SharedPrefUtil
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DailyQuote(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Inspired"
            val descriptionText = "Inspired Quote"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channelId", name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        QuoteFetch().fetchQuoteAsync().enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                val jsonOBj = response.body()?.get("quote")
                val quote = Quote(
                    jsonOBj?.asJsonObject?.get("_id")!!.toString(),
                    jsonOBj.asJsonObject["quoteText"]!!.toString(),
                    jsonOBj.asJsonObject["quoteAuthor"]!!.toString()
                )

                SharedPrefUtil.saveNotifAuthor(context,quote.quoteAuthor!!)
                SharedPrefUtil.saveNotifQuote(context,quote.quoteText!!)
                SharedPrefUtil.setClickedOnNotif(context,true)

                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("extra",true)
                }
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val notif = NotificationCompat.Builder(context, "channelId")
                    .setSmallIcon(R.drawable.ic_baseline_settings_24)
                    .setContentTitle(quote.quoteAuthor)
                    .setContentText(quote.quoteText)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()
                showBackgroundNotification(0, notif)
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }
        })
        return Result.success()
    }

    companion object{
        const val ACTION_SEND_NOTIF = "com.example.inspired.ACTION_SEND_NOTIF"
        const val PERM_PRIVATE = "com.example.inspired.PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
    }

    private fun showBackgroundNotification(requestCode: Int, notification: Notification){
        val intent = Intent(ACTION_SEND_NOTIF).apply {
            putExtra(REQUEST_CODE, requestCode)
            putExtra(NOTIFICATION,notification)
        }
        context.sendOrderedBroadcast(intent, PERM_PRIVATE)
    }

}