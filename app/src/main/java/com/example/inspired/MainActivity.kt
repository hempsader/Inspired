package com.example.inspired

import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.inspired.model.Quote
import com.example.inspired.repository.Repository
import com.example.inspired.util.SharedPrefUtil
import com.example.inspired.util.Util
import com.example.inspired.viewmodel.QuoteViewModel
import kotlinx.coroutines.*

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inspireMe = findViewById(R.id.inspireMe)
        quoteText = findViewById(R.id.quote_text)
        button = findViewById(R.id.button)
        util = Util(this)
        repository = Repository.get()!!

        if(!util.isInternetConnected()){
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
        val quote = Quote(
            "5eb17aadb69dc744b4e7258c",
            "Why don't you start believing that no matter what you have or haven't done, that your best days are still out in front of you.",
            "Joel Osteen"
        )
        val quote2 = Quote("5eb17asdasadb69dc744b4e7258c","aici ii distractie, sa dansez, sa mananc","draga")
        repository.insertQuote(quote)

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
        if(SharedPrefUtil.getDailyEnabled(this)!!){
            Log.d("aa",true.toString())
        }else{
            Log.d("aa",false.toString())
        }
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



