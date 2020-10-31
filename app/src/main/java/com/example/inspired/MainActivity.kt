package com.example.inspired

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.inspired.api.QuoteFetch
import com.example.inspired.model.Quote
import com.example.inspired.repository.Repository
import com.example.inspired.viewmodel.QuoteViewModel
import kotlinx.coroutines.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var quoteViewModel: QuoteViewModel
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var util: Util
    private lateinit var inspireMe: TextView
    private lateinit var quoteText: TextView
    private lateinit var button: Button
    private lateinit var repository: Repository
    private lateinit var animStart: Animation
    private lateinit var animStop: Animation
    private lateinit var animOffline: Animation
    private var quotes = mutableListOf<Quote>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inspireMe = findViewById(R.id.inspireMe)
        quoteText = findViewById(R.id.quote_text)
        button = findViewById(R.id.button)
        util = Util(this)
        repository = Repository.get()!!

        val quote = Quote(
            "5eb17aadb69dc744b4e7258c",
            "Why don't you start believing that no matter what you have or haven't done, that your best days are still out in front of you.",
            "Joel Osteen"
        )
        val quote2 = Quote("5eb17asdasadb69dc744b4e7258c","aici ii distractie, sa dansez, sa mananc","draga")
     //   repository.insertQuote(quote2)

        quoteViewModel = ViewModelProvider(this)[QuoteViewModel::class.java]
         animStart = AnimationUtils.loadAnimation(this, R.anim.anim_inspire)
         animStop = AnimationUtils.loadAnimation(this, R.anim.stop_anim)
        animOffline = AnimationUtils.loadAnimation(this,R.anim.anim_offline)
        quoteViewModel.quoteViewModel.observe(this, { quoteModel ->
            GlobalScope.launch(Dispatchers.Main) {
                GlobalScope.async {
                    withContext(Dispatchers.Main) {
                        val random = java.util.Random()
                        val color = Color.argb(
                            255,
                            random.nextInt(256),
                            random.nextInt(256),
                            random.nextInt(256)
                        )
                        quoteText.setTextColor(color)
                            quoteText.text = quoteModel.quoteText
                        inspireMe.isEnabled = true
                    }
                }.await()
                inspireMe.startAnimation(animStop)
            }
            inspireMe.isEnabled = true
            inspireMe.setTextColor(Color.GRAY)
        })

        }


    override fun onResume() {
        super.onResume()
        connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private val networkCallback = object :  ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            runOnUiThread{
                inspireMe.setOnClickListener {
                    inspireMe.apply {
                        setTextColor(resources.getColor(R.color.colorPrimary))
                        startAnimation(animStart)
                        isEnabled = false
                        quoteViewModel.fetchQuote()
                    }
                }
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            inspireMe.setOnClickListener {
                    inspireMe.apply {
                        setTextColor(resources.getColor(R.color.colorPrimary))
                        startAnimation(animOffline)
                        GlobalScope.launch(Dispatchers.Main) {
                            quoteText.text = repository.getRandomQuote().quoteText
                        }
                    }
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {

        }

    }

    override fun onPause() {
        super.onPause()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}



