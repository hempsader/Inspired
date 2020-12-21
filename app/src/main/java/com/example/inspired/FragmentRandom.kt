package com.example.inspired

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.*
import com.example.inspired.viewModel.QuoteViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.favourite_list.*
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
    private lateinit var inspireMeButton: TextView
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
        inspireMeButton = view.findViewById(R.id.inspireMe)


        viewModel.observeRemoteQuote().observe(viewLifecycleOwner, Observer {
            if(it is ResponseQuoteRandom.ResponseSuccesfull ) {
                if (it.quote != null) {
                    progressBar.visibility = View.GONE
                    quote_text.text = it.quote?.text
                    author.text = it.quote?.author
                    viewModel.insertOfflineQuote(it.quote)
                    favouriteImage.visibility = View.VISIBLE
                }
                    favourite(it.quote!!)
                    if (it.quote?.favourite!!) favouriteImage.setImageResource(R.drawable.ic_baseline_favorite_24_true) else favouriteImage.setImageResource(
                        R.drawable.ic_outline_favorite_border_24_false
                    )

            }
            if(it is ResponseQuoteRandom.ResponseUnsuccessfull){
                Snackbar.make(view, it.string, Snackbar.LENGTH_SHORT).show()
                viewModel.fetchLocalQuote()
            }
        })

        viewModel.test().observe(viewLifecycleOwner, Observer {
            it.forEach {
                Log.d("aa", it.author + " " + it.favourite.toString())
            }
        })

        viewModel.observerLocalQuote().observe(viewLifecycleOwner, Observer {
            if(it != null) {
                favouriteImage.visibility = View.VISIBLE
                favourite(it)
                if(it.favourite) favouriteImage.setImageResource(R.drawable.ic_baseline_favorite_24_true) else favouriteImage.setImageResource(R.drawable.ic_outline_favorite_border_24_false)
                progressBar.visibility = View.GONE
                quote_text.text = it.text
                author.text = it.author
            }
        })
        fetchingClick()
        internetCheck()
        fetching()
        return view
    }

    private fun favourite(quote: QuoteResponse.Quote){
        favouriteImage.setOnClickListener {
            quote.favourite = !quote.favourite
            if(quote.favourite) favouriteImage.setImageResource(R.drawable.ic_baseline_favorite_24_true) else favouriteImage.setImageResource(R.drawable.ic_outline_favorite_border_24_false)
            viewModel.favouriteQuote(quote)
        }
    }


    override fun onDestroy() {
        job?.cancel()
        job = null
        if (Build.VERSION.SDK_INT < 24 ) {
            context?.unregisterReceiver(broadcast)
        }
        super.onDestroy()
    }




    private fun fetching(){
        job  =   viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            channel.asFlow().collectLatest {
                if(it == State.CONNECTED){
                    withContext(Dispatchers.Main) {
                        quote_text.text = ""
                        author.text = ""
                        progressBar.visibility = View.VISIBLE
                    }
                    viewModel.fetchQuoteRemote()
                }
                if(it == State.DISSCONNECTED){
                    withContext(Dispatchers.Main) {
                        quote_text.text = ""
                        author.text = ""
                        progressBar.visibility = View.VISIBLE
                    }
                    viewModel.fetchLocalQuote()
                }
            }
        }
    }

    private fun fetchingClick(){
        job  =   viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            channel.asFlow().collectLatest { state ->
                inspireMeButton.setOnClickListener {
                    GlobalScope.launch(Dispatchers.IO) {
                            if (state == State.CONNECTED) {
                                withContext(Dispatchers.Main) {
                                quote_text.text = ""
                                    author.text = ""
                                progressBar.visibility = View.VISIBLE
                                    favouriteImage.visibility = View.GONE
                            }
                            viewModel.fetchQuoteRemote()
                        }
                        if (state == State.DISSCONNECTED) {
                            withContext(Dispatchers.Main) {
                                quote_text.text = ""
                                author.text = ""
                                progressBar.visibility = View.VISIBLE
                                favouriteImage.visibility = View.GONE
                            }
                            viewModel.fetchLocalQuote()
                        }
                    }
                }
            }
        }
    }
    private fun internetCheck(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            job = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Unconfined) {
                InternetUtil.checkInternet(requireContext(), viewLifecycleOwner.lifecycleScope)
                    .collect {
                        channel.send(it)
                    }
            }
        } else {
            requireContext()?.registerReceiver(
                broadcast,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    inner  class BroadCastInsternet: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (requireContext()?.let { InternetUtil.checkInternetBroadcast(it)}) {

                State.CONNECTED -> {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        channel.send(State.CONNECTED)
                    }
                }
                State.DISSCONNECTED -> {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        channel.send(State.DISSCONNECTED)
                    }
                }
            }
        }
    }
}