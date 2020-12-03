package com.example.inspired

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inspired.api.QuoteApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch {
                val x = QuoteApi.get().getRandomQuote()
                if(x.isSuccessful){
                    Log.d("aa", x.body()?.quote?.author!!)
                }
                Log.d("aa", "dsa")
        }
    }
}



