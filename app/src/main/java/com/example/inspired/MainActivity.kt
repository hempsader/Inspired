package com.example.inspired

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.inspired.api.QuoteFetch
import com.example.inspired.viewmodel.QuoteViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var quoteViewModel: QuoteViewModel
    private lateinit var util: Util
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        quoteViewModel = ViewModelProvider(this)[QuoteViewModel::class.java]

        quoteViewModel.quoteViewModel.observe(this,{
            Log.d("aa",it.toString())
        })
    }
}