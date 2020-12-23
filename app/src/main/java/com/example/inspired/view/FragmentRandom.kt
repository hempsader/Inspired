package com.example.inspired.view

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
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.inspired.R
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.*
import com.example.inspired.viewModel.QuoteViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toList

class FragmentRandom : Fragment() {
    private lateinit var inspireMeButton: TextView
    private lateinit var shareImage: ImageView
    private val viewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return QuoteViewModel(
                    QuoteRepositoryImpl(requireContext()),
                    Dispatchers.IO + Job()
                ) as T
            }
        })[QuoteViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        inspireMeButton = view.findViewById(R.id.inspireMe)
        shareImage = view.findViewById(R.id.imageShare)
        Log.d("bb", UtilPreferences.dailyHour(requireContext()).toString())

        viewModel.observeRemoteQuote().observe(viewLifecycleOwner, Observer {
            if (it is ResponseQuoteRandom.ResponseSuccesfull) {
                if (it.quote != null) {
                    uiCheck(it.quote)
                    favourite(it.quote!!)
                    shareQuote(it.quote)
                    if (UtilPreferences.roomEnable(requireContext())) {
                        viewModel.insertOfflineQuote(it.quote)
                    }
                    if (it.quote?.favourite!!) favouriteImage.setImageResource(R.drawable.ic_baseline_favorite_24_true) else favouriteImage.setImageResource(
                        R.drawable.ic_outline_favorite_border_24_false
                    )
                }
            }
            if (it is ResponseQuoteRandom.ResponseUnsuccessfull) {
                snack(it.string)
                viewModel.fetchLocalQuote()
            }
        })

        viewModel.observerLocalQuote().observe(viewLifecycleOwner, Observer {
            if (it is ResponseQuoteRandom.ResponseSuccesfull) {
                uiCheck(it.quote!!)
                favourite(it.quote)
                if (it.quote?.favourite!!) favouriteImage.setImageResource(R.drawable.ic_baseline_favorite_24_true) else favouriteImage.setImageResource(
                    R.drawable.ic_outline_favorite_border_24_false
                )
            }
            if (it is ResponseQuoteRandom.ResponseUnsuccessfull) {
                progressBar.visibility = View.GONE
                shareImage.visibility = View.GONE
                favouriteImage.visibility = View.GONE
                quote_text.text = "Ooops......No fetched quotes, please go online and try again!"
                snack(it.string)
            }
        })
        //fetchingClick()

        FetchFragment().fetchQuoteFragment()
        return view
    }

    private fun uiCheck(quote: QuoteResponse.Quote) {
        if (quote != null) {
            favouriteImage.visibility = View.VISIBLE
            shareImage.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            quote_text.text = quote.text
            author.text = quote.author
        }
    }

    private fun favourite(quote: QuoteResponse.Quote) {
        favouriteImage.setOnClickListener {
            quote.favourite = !quote.favourite
            if (quote.favourite) favouriteImage.setImageResource(R.drawable.ic_baseline_favorite_24_true) else favouriteImage.setImageResource(
                R.drawable.ic_outline_favorite_border_24_false
            )
            viewModel.favouriteQuote(quote)
        }
    }



    private fun shareQuote(quote: QuoteResponse.Quote) {
        imageShare.setOnClickListener {
            val share = ShareQuote(requireContext())
            share.quote(quote)
        }
    }

    private fun snack(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun loadingQuoteUI(){
               quote_text.text = ""
               author.text = ""
               progressBar.visibility = View.VISIBLE
    }
            inner class FetchFragment(){
                @InternalCoroutinesApi
                fun fetchQuoteFragment(){
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        InternetUtil.getState().collectLatest {
                            when (it){
                                State.CONNECTED -> {
                                    loadingQuoteUI()
                                    viewModel.fetchQuoteRemote()
                                }
                                State.DISSCONNECTED -> {
                                    loadingQuoteUI()
                                    viewModel.fetchLocalQuote()
                                }
                            }
                        }
                    }
                    }
            }
            inner class FetchOutsite(){
                    @InternalCoroutinesApi
                    fun fetchQuoteFragment(){
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            InternetUtil.getState().collectLatest {
                                when (it){
                                    State.CONNECTED -> {
                                        viewModel.fetchQuoteRemote()
                                    }
                                    State.DISSCONNECTED -> {
                                        viewModel.fetchLocalQuote()
                                    }
                                }
                            }
                        }
                }
            }

}





