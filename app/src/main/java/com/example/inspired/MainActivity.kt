package com.example.inspired

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.graphics.Color
import android.net.Network
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    private lateinit var util: Util
    private lateinit var inspireMe: TextView
    private lateinit var quoteText: TextView
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inspireMe = findViewById(R.id.inspireMe)
        quoteText = findViewById(R.id.quote_text)
        button = findViewById(R.id.button)
        util = Util(this)

        quoteViewModel = ViewModelProvider(this)[QuoteViewModel::class.java]


        quoteViewModel.quoteViewModel.observe(this,{quoteModel ->
            GlobalScope.launch(Dispatchers.Main){
                GlobalScope.async {
                    withContext(Dispatchers.Main){
                        val random = java.util.Random()
                        val color = Color.argb(255,random.nextInt(256), random.nextInt(256),random.nextInt(256))
                        quoteText.setTextColor(color)
                        quoteText.text = quoteModel.quoteText
                    }
                }.await()
            }
            inspireMe.isEnabled = true
            inspireMe.setTextColor(Color.GRAY)
        })
        inspireMe.setOnClickListener {
            inspireMe.setTextColor(resources.getColor(R.color.colorPrimary))
            quoteViewModel.fetchQuote()
            inspireMe.isEnabled = false
            inspireMe.startAnimation(AnimationUtils.loadAnimation(this,R.anim.anim_inspire))
        }

        button.setOnClickListener {
            quoteViewModel.fetchQuote()
            button.isEnabled = false
            button.startAnimation(AnimationUtils.loadAnimation(this,R.anim.anim_inspire))
        }
    }

}

