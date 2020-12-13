package com.example.inspired

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.ResponseQuoteRandom
import com.example.inspired.viewModel.QuoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext


class MainActivity : UtilActivity() {
    private var job: Job? = null

    private val viewModel by lazy {
        ViewModelProvider(this, object: ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return QuoteViewModel(QuoteRepositoryImpl(this@MainActivity),Dispatchers.IO + Job()) as T
            }
        })[QuoteViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


       viewModel.observeRemoteQuote().observe(this, Observer {
           if(it is ResponseQuoteRandom.ResponseSuccesfull){
               quote_text.text = it.quote?.text
           }
       })
        getQuoteButton()
    }

    private fun getQuoteButton(){
        inspireMe.setOnClickListener {
            viewModel.fetchQuoteRemote()
        }
    }

    override fun onResume() {
     job  =   GlobalScope.launch(Dispatchers.IO) {
            internetFlow().asFlow().collectLatest {
                Log.d("aa", it.toString())
            }
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
        job = null
    }
}




