package com.example.inspired.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.inspired.R
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.*
import com.example.inspired.viewModel.QuoteViewModel
import com.example.inspired.viewModel.fetching.NotificationWorkStart
import com.google.android.material.snackbar.Snackbar
import com.judemanutd.autostarter.AutoStartPermissionHelper
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

class FragmentRandom : VisibleFragment() {
    private lateinit var inspireMeButton: TextView
    private lateinit var shareImage: ImageView
    private lateinit var progress: ProgressBar
    private lateinit var favouriteImageView: ImageView
    private lateinit var quoteText: TextView
    private lateinit var quoteAuthor: TextView

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

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        inspireMeButton = view.findViewById(R.id.inspireMe)
        shareImage = view.findViewById(R.id.imageShare)
        progress = view.findViewById<ProgressBar>(R.id.progressBar)
        favouriteImageView = view.findViewById(R.id.favouriteImage)
        quoteText = view.findViewById(R.id.quote_text)
        quoteAuthor = view.findViewById(R.id.author)
        viewModel.observeRemoteQuote().observe(viewLifecycleOwner, Observer {
            if (it is ResponseQuoteRandom.ResponseSuccesfull) {
                if (it.quote != null) {
                    quoteUI(it.quote)
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
                quoteUI(it.quote!!)
                favourite(it.quote)
                if (it.quote?.favourite!!) favouriteImage.setImageResource(R.drawable.ic_baseline_favorite_24_true) else favouriteImage.setImageResource(
                    R.drawable.ic_outline_favorite_border_24_false
                )
            }
            if (it is ResponseQuoteRandom.ResponseUnsuccessfull) {
                quoteText.visibility = View.VISIBLE
                progress.visibility = View.GONE
                inspireMeButton.visibility = View.VISIBLE
                quoteText.text = "Ooops......No fetched quotes, please go online and try again!"
                snack(it.string)
            }
        })


        firstTimeFetch()
        firstTimeRunNotif()
        fetchclick()
        return view
    }

    private fun loadingQuoteUI(){
        progress.visibility = View.VISIBLE
        inspireMeButton.visibility = View.GONE
        shareImage.visibility = View.GONE
        favouriteImageView.visibility = View.GONE
        quoteText.visibility = View.GONE
        quoteAuthor.visibility = View.GONE
    }

    private fun quoteUI(quote: QuoteResponse.Quote){
        progress.visibility = View.GONE
        inspireMeButton.visibility = View.VISIBLE
        shareImage.visibility = View.VISIBLE
        favouriteImageView.visibility = View.VISIBLE
        quoteText.visibility = View.VISIBLE
        quoteAuthor.visibility = View.VISIBLE
        if(quote != null){
            quoteText.text = quote.text
            quoteAuthor.text = quote.author
        }
    }





    private fun favourite(quote: QuoteResponse.Quote) {
        favouriteImage.setOnClickListener {
            quote.favourite = !quote.favourite
            if (quote.favourite) favouriteImage.setImageResource(R.drawable.ic_baseline_favorite_24_true) else favouriteImage.setImageResource(
                R.drawable.ic_outline_favorite_border_24_false
            )
            viewModel.insertOfflineQuote(quote)
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


    private fun firstTimeFetch(){
        val manager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        when(manager.activeNetworkInfo != null && manager.activeNetworkInfo?.isConnected!!){
            true -> {
                viewModel.fetchQuoteRemote()
                loadingQuoteUI()
            }
            false -> {
                viewModel.fetchLocalQuote()
                loadingQuoteUI()
            }
        }
    }

    private fun firstTimeRunNotif(){
        if(UtilPreferences.scheduleNewWork(requireContext())) {
            UtilPreferences.dailyMinuteSet(requireContext(),0)
            val randomHour = Random.nextInt(8, 20)
            UtilPreferences.dailyHourSet(requireContext(), randomHour)
            if(UtilPreferences.dailyEnable(requireContext())) {
                NotificationWorkStart.cancelFetchJob(requireContext())
                NotificationWorkStart.start(
                    requireContext(),
                    UtilPreferences.dailyHour(requireContext()),
                    UtilPreferences.dailyMinute(requireContext())
                )
            }
            if(AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(requireContext())) {
                PowerOptimisationForNotif.enableAutoStart(requireContext(),UtilPreferences.scheduleNewWork(requireContext()))
            }
            PowerOptimisationForNotif.disableBatterySaverForThisApp(requireContext(), UtilPreferences.scheduleNewWork(requireContext()))
            UtilPreferences.scheduleNewWorkSet(requireContext(), false)
        }
    }



    @InternalCoroutinesApi
    private fun fetchclick() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            InternetUtil.getState().collectLatest {state ->
                inspireMeButton.setOnClickListener {
                    when (state) {
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
}







