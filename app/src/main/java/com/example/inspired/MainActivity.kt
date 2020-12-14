package com.example.inspired

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.ResponseQuoteRandom
import com.example.inspired.util.State
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
        fetching()

       viewModel.observeRemoteQuote().observe(this, Observer {
           if(it is ResponseQuoteRandom.ResponseSuccesfull ){
               if(it.quote?.text != null) {
                       progressBar.visibility = View.GONE
                       quote_text.text = it.quote?.text
                   }
                   viewModel.insertOfflineQuote(it.quote)
           }
       })

        viewModel.observerLocalQuote().observe(this, Observer {
            if(it != null) {
                    progressBar.visibility = View.GONE
                    quote_text.text = it.text
            }
        })


        fetchingClick()
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onStop() {
        job?.cancel()
        job = null
        super.onStop()
    }

    private fun fetching(){
        job  =   GlobalScope.launch(Dispatchers.IO) {
            internetFlow().asFlow().collectLatest {
                if(it == State.CONNECTED){
                    withContext(Dispatchers.Main) {
                        quote_text.text = ""
                        progressBar.visibility = View.VISIBLE
                    }
                    viewModel.fetchQuoteRemote()
                }
                if(it == State.DISSCONNECTED){
                    withContext(Dispatchers.Main) {
                        quote_text.text = ""
                        progressBar.visibility = View.VISIBLE
                    }
                    viewModel.fetchLocalQuote()
                }
            }
        }
    }

    private fun fetchingClick(){
        job  =   GlobalScope.launch(Dispatchers.IO) {
            internetFlow().asFlow().collectLatest { state ->
                inspireMe.setOnClickListener {
                    if (state == State.CONNECTED) {
                            quote_text.text = ""
                            progressBar.visibility = View.VISIBLE
                        viewModel.fetchQuoteRemote()
                    }
                    if (state == State.DISSCONNECTED) {
                            quote_text.text = ""
                            progressBar.visibility = View.VISIBLE
                        viewModel.fetchLocalQuote()
                    }
                }
            }
            }
        }
    }





