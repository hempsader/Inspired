package com.example.inspired

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.InternetUtil
import com.example.inspired.util.ResponseQuoteRandom
import com.example.inspired.util.State
import com.example.inspired.viewModel.QuoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

class FragmentRandom : Fragment(){

    private var job: Job? = null
    private val broadcast = BroadCastInsternet()
    private val channel = ConflatedBroadcastChannel<State>()



    private val viewModel by lazy {
        ViewModelProvider(this, object: ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return QuoteViewModel(QuoteRepositoryImpl(requireContext()),Dispatchers.IO + Job()) as T
            }
        })[QuoteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_fragment,container,false)

        fetching()
        viewModel.observeRemoteQuote().observe(viewLifecycleOwner, Observer {
            if(it is ResponseQuoteRandom.ResponseSuccesfull ){
                if(it.quote?.text != null) {
                    progressBar.visibility = View.GONE
                    quote_text.text = it.quote?.text
                }
                viewModel.insertOfflineQuote(it.quote)
            }
        })

        viewModel.observerLocalQuote().observe(viewLifecycleOwner, Observer {
            if(it != null) {
                progressBar.visibility = View.GONE
                quote_text.text = it.text
            }
        })
        fetchingClick()


        return view
    }

    override fun onStop() {
        job?.cancel()
        job = null
        if (Build.VERSION.SDK_INT < 24 ) {
          context?.unregisterReceiver(broadcast)
        }
        super.onStop()
    }

    override fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            job = GlobalScope.launch(Dispatchers.Unconfined) {
                InternetUtil.checkInternet(requireContext(), Dispatchers.IO)
                    .collect {
                        channel.send(it)
                    }
            }
        } else {
           context?.registerReceiver(
                broadcast,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        super.onResume()
    }


    private fun fetching(){
        job  =   GlobalScope.launch(Dispatchers.IO) {
            channel.asFlow().collectLatest {
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
        job  =   GlobalScope.launch(Dispatchers.Main) {
            channel.asFlow().collectLatest { state ->
                inspireMe.setOnClickListener {
                    GlobalScope.launch(Dispatchers.IO) {
                            if (state == State.CONNECTED) {
                                withContext(Dispatchers.Main) {
                                quote_text.text = ""
                                progressBar.visibility = View.VISIBLE
                            }
                            viewModel.fetchQuoteRemote()
                        }
                        if (state == State.DISSCONNECTED) {
                            withContext(Dispatchers.Main) {
                                quote_text.text = ""
                                progressBar.visibility = View.VISIBLE
                            }
                            viewModel.fetchLocalQuote()
                        }
                    }
                }
            }
        }
    }

    inner  class BroadCastInsternet: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (context?.let { InternetUtil.checkInternetBroadcast(it)}) {
                State.CONNECTED -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        channel.send(State.CONNECTED)
                    }
                }
                State.DISSCONNECTED -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        channel.send(State.DISSCONNECTED)
                    }
                }
            }
        }

    }
}