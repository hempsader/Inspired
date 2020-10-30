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
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.inspired.api.QuoteFetch
import com.example.inspired.model.Quote
import com.example.inspired.repository.Repository
import com.example.inspired.viewmodel.QuoteViewModel

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
        util = Util()

        quoteViewModel = ViewModelProvider(this)[QuoteViewModel::class.java]
        var quote: Quote? = null

        quoteViewModel.quoteViewModel.observe(this,{quoteModel ->
                quoteText.text = quoteModel.quoteText
        })
        inspireMe.setOnClickListener {
            quoteViewModel.fetchQuote()
        }

        button.setOnClickListener {
            quoteViewModel.fetchQuote()
        }
    }
}

