package com.example.inspired

import DailyQuote
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.inspired.repository.Repository
import com.example.inspired.util.SharedPrefUtil
import com.example.inspired.util.Util
import com.example.inspired.viewmodel.QuoteViewModel
import kotlinx.coroutines.*

class MainActivity : VisibleActivity() {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inspireMe = findViewById(R.id.inspireMe)
        quoteText = findViewById(R.id.quote_text)
        button = findViewById(R.id.button)
        quoteViewModel = ViewModelProvider(this)[QuoteViewModel::class.java]
        animStart = AnimationUtils.loadAnimation(this, R.anim.anim_inspire)
        animStop = AnimationUtils.loadAnimation(this, R.anim.stop_anim)
        animOffline = AnimationUtils.loadAnimation(this, R.anim.anim_offline)
        util = Util(this)
        repository = Repository.get()!!


        if (!util.isInternetConnected()) {
            util.dialogNoInternet()
            GlobalScope.launch(Dispatchers.Main) {
                quoteText.text = repository.getRandomQuote().quoteText
            }
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
                        repository.insertQuote(quoteModel)
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
        val uploadWorkRequest: WorkRequest = OneTimeWorkRequest.from(DailyQuote::class.java)
        WorkManager.getInstance(this@MainActivity).enqueue(uploadWorkRequest)
        quoteText.text = SharedPrefUtil.getNotifAuthor(this).toString()
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            runOnUiThread{
                inspireMe.setOnClickListener {
                    inspireMe.apply {
                        setTextColor(resources.getColor(R.color.colorPrimary))
                        startAnimation(animStart)
                        isEnabled = false
                        quoteViewModel.fetchQuote()
                        SharedPrefUtil.setClickedOnNotif(this@MainActivity,true)
                    }
                }
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            util.dialogNoInternet()
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
    }
    override fun onPause() {
        super.onPause()
        connectivityManager.unregisterNetworkCallback(networkCallback)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu,menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings ->{ startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}



