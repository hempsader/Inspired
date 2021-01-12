package com.ionut.grigore.inspired.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.ionut.grigore.inspired.R
import com.ionut.grigore.inspired.model.QuoteResponse
import com.ionut.grigore.inspired.repository.QuoteRepositoryImpl
import com.ionut.grigore.inspired.util.*
import com.ionut.grigore.inspired.viewModel.QuoteViewModel
import com.ionut.grigore.inspired.viewModel.fetching.NotificationWorkStart
import com.judemanutd.autostarter.AutoStartPermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random


class FragmentRandom : VisibleFragment() {
    private lateinit var inspireMeButton: MaterialButton
    private lateinit var shareImage: MaterialButton
    private lateinit var progress: ProgressBar
    private lateinit var favouriteImageView: MaterialButton
    private lateinit var quoteText: TextView
    private lateinit var quoteAuthor: TextView
    private lateinit var cardViewMainText: CardView
    private lateinit var cardVIewButtons: CardView
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var category: TextView
    private var quoteBuffer: QuoteResponse.Quote? = null


    private val viewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return QuoteViewModel(
                    QuoteRepositoryImpl(),
                    Dispatchers.IO + Job()
                ) as T
            }
        })[QuoteViewModel::class.java]
    }



    @RequiresApi(Build.VERSION_CODES.M)
    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        inspireMeButton = view.findViewById(R.id.inspireMe)
        shareImage = view.findViewById(R.id.imageShare)
        progress = view.findViewById(R.id.progressBar)
        favouriteImageView = view.findViewById(R.id.favButton)
        quoteText = view.findViewById(R.id.quote_text)
        quoteAuthor = view.findViewById(R.id.author)
        cardViewMainText = view.findViewById(R.id.cardView)
        cardVIewButtons = view.findViewById(R.id.cardView2)
        mainLayout = view.findViewById(R.id.mainLayout)
        category = view.findViewById(R.id.category)


            viewModel.observeRemoteQuote().observe(viewLifecycleOwner, Observer {
                if (it is ResponseQuoteRandom.ResponseSuccesfull) {
                    if (it.quote != null) {
                        quoteBuffer = it.quote
                        quoteUI(it.quote)
                        favouriteImageView.setOnClickListener {view->
                            favourite(it.quote)
                        }
                        shareImage.setOnClickListener {view->
                            shareQuote(it.quote)
                        }
                        if (UtilPreferences.roomEnable()) {
                            if (!lowMemoryDetect()) {
                                viewModel.insertOfflineQuote(it.quote)
                            } else {
                                snack("Low memory, fetching offline!")
                                viewModel.fetchLocalQuote()
                            }
                        }
                        if (it.quote.favourite) favouriteImageView.setIconResource(R.drawable.ic_baseline_favorite_24_true) else favouriteImageView.setIconResource(
                            R.drawable.ic_outline_favorite_border_24_false
                        )
                    }
                }
                if (it is ResponseQuoteRandom.ResponseUnsuccessfull) {
                    snack(getString(R.string.snack_server_problem))
                    viewModel.fetchLocalQuote()
                }
            })

        viewModel.observerLocalQuote().observe(viewLifecycleOwner, Observer {
            if (it is ResponseQuoteRandom.ResponseSuccesfull) {
                quoteBuffer = it.quote
                quoteUI(it.quote!!)
                favouriteImageView.setOnClickListener {view->
                    favourite(it.quote)
                }
                if (it.quote.favourite) favouriteImageView.setIconResource(R.drawable.ic_baseline_favorite_24_true) else favouriteImageView.setIconResource(
                    R.drawable.ic_outline_favorite_border_24_false
                )
            }
            if (it is ResponseQuoteRandom.ResponseUnsuccessfull) {
                val quote = QuoteResponse.Quote()
                if(quoteBuffer != null) {
                    quoteUI(quoteBuffer!!)
                }else{
                    quoteUI(quote)
                    quoteText.text = getString(R.string.no_fetched_quotes)
                }



            }
        })

        inspireMeButton.setClickDebounce(1000L) {
            fetchclick()
        }




        unfavouriteUI()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(!UtilPreferences.termsAndConditions()){
            termsAndCondition()
            mainLayout.visibility = View.GONE
        }else{
            if (savedInstanceState != null) {
                val id = savedInstanceState.getString("quote_id")
                val text = savedInstanceState.getString("quote_text")
                val author = savedInstanceState.getString("quote_author")
                val favourite = savedInstanceState.getBoolean("quote_favourite")
                val category = savedInstanceState.getString("quote_category")
                val quote = QuoteResponse.Quote(id ?: "0", text ?: "No fetched quote..please go online!", author ?: "Unknown", favourite , category ?: "Unknown")
                quoteUI(quote)
            } else {
                firstTimeFetch()
            }
            firstTimeRunNotif()
            randomGradient()
        }
    }

    @InternalCoroutinesApi
    private fun unfavouriteUI() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            UnfavouriteFlow.readFavourite().asFlow().collect {
                if (quoteBuffer?.id == it.id) {
                    quoteBuffer?.favourite = false
                    favouriteImageView.setIconResource(R.drawable.ic_outline_favorite_border_24_false)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (quoteBuffer != null){
            outState?.apply {
                putString("quote_id", quoteBuffer?.id ?: "id")
                putString("quote_author", quoteBuffer?.author ?: "Unknown")
                putString("quote_text", quoteBuffer?.text ?: "Ooop..something went wrong.")
                putBoolean("quote_favourite", quoteBuffer?.favourite ?: false)
                putString("quote_category", quoteBuffer?.category ?: "Unknown")
            }
        }
        super.onSaveInstanceState(outState)
    }



    private fun lowMemoryDetect(): Boolean {

        return requireContext().cacheDir.usableSpace * 100 / requireContext().cacheDir.totalSpace <= 10
    }

    private fun loadingQuoteUI() {
        progress.visibility = View.VISIBLE
        inspireMeButton.visibility = View.GONE
        shareImage.visibility = View.GONE
        favouriteImageView.visibility = View.GONE
        quoteText.visibility = View.GONE
        quoteAuthor.visibility = View.GONE
        cardViewMainText.visibility = View.GONE
        cardVIewButtons.visibility = View.GONE
        category.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun quoteUI(quote: QuoteResponse.Quote) {
        progress.visibility = View.GONE
        inspireMeButton.visibility = View.VISIBLE
        category.visibility = View.VISIBLE
        shareImage.visibility = View.VISIBLE
        favouriteImageView.visibility = View.VISIBLE
        quoteText.visibility = View.VISIBLE
        quoteAuthor.visibility = View.VISIBLE
        cardVIewButtons.visibility = View.VISIBLE
        cardViewMainText.visibility = View.VISIBLE
        randomGradient()
        quoteText.text = quote.text
        quoteAuthor.text = quote.author
        category.text = "#${quote.category}"
    }

    private fun randomGradient() {
        val rnd = Random
        val colors = IntArray(3)
        colors[0] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        colors[1] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        colors[2] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, colors
        ).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }
        mainLayout.background = gd
    }

    private fun favourite(quote: QuoteResponse.Quote) {
            if (!lowMemoryDetect()) {
                quote.favourite = !quote.favourite
                if (quote.favourite) favouriteImageView.setIconResource(R.drawable.ic_baseline_favorite_24_true) else favouriteImageView.setIconResource(
                    R.drawable.ic_outline_favorite_border_24_false
                )
                viewModel.insertOfflineQuote(quote)
                viewModel.favouriteQuote(quote)
            } else {
                snack(R.string.snack_cannot_insert_quote.toString())
            }
    }


    private fun shareQuote(quote: QuoteResponse.Quote) {
            val shareQuote = Intent().apply{
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${quote.text} - ${quote.author}")
                type = "text/plain"
            }
            requireContext().startActivity(Intent.createChooser(shareQuote, getString(R.string.chooser_title)))
        }


    private fun snack(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }


    private fun firstTimeFetch() {
        val manager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        when (manager.activeNetworkInfo != null && manager.activeNetworkInfo?.isConnected!!) {
            true -> {
                if (!UtilPreferences.offlineFetch()!!) {
                    viewModel.fetchQuoteRemote()
                    loadingQuoteUI()
                } else {
                    viewModel.fetchLocalQuote()
                    loadingQuoteUI()
                }
            }
            false -> {
                viewModel.fetchLocalQuote()
                loadingQuoteUI()
            }
        }
    }



    private fun termsAndCondition(){
            AlertDialog.Builder(context)
                .setTitle("Terms & Conditions")
                .setMessage(R.string.terms_and_conditions)
                .setPositiveButton("Accept"){dialog, _ ->
                    UtilPreferences.termsAndConditions(true)
                    dialog.dismiss()
                    firstTimeFetch()
                    firstTimeRunNotif()
                    randomGradient()
                    mainLayout.visibility = View.VISIBLE
                }
                .setOnCancelListener {
                    requireActivity().finishAffinity()
                }
                .setNegativeButton("Decline"){dialog, _ ->
                 requireActivity().finish()
                }.show()
        }


    private fun firstTimeRunNotif() {
        if (UtilPreferences.scheduleNewWork()) {
            UtilPreferences.dailyMinuteSet( 0)
            val randomHour = Random.nextInt(8, 20)
            val randomMinute = Random.nextInt(0, 59)
            UtilPreferences.dailyHourSet( randomHour)
            UtilPreferences.dailyMinuteSet( randomMinute)
            if (UtilPreferences.dailyEnable()) {
                NotificationWorkStart.cancelFetchJob(requireContext())
                NotificationWorkStart.start(
                    requireContext(),
                    UtilPreferences.dailyHour(),
                    UtilPreferences.dailyMinute()
                )
            }

            if (!AutoStartPermissionHelper.getInstance()
                    .isAutoStartPermissionAvailable(requireContext()) && Build.VERSION.SDK_INT < 23
            ){
                UtilPreferences.scheduleNewWorkSet( false)
            }
            if (AutoStartPermissionHelper.getInstance()
                    .isAutoStartPermissionAvailable(requireContext())
            ) {
                PowerOptimisationForNotif.enableAutoStart(
                    requireContext(),
                    UtilPreferences.scheduleNewWork()
                )
            }
            PowerOptimisationForNotif.disableBatterySaverForThisApp(
                requireContext(),
                UtilPreferences.scheduleNewWork()
            )
        }
    }

    @InternalCoroutinesApi
    private fun fetchclick() {
        val manager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        when(manager.activeNetworkInfo != null && manager.activeNetworkInfo?.isConnected!!){
                        true-> {
                            if (!UtilPreferences.offlineFetch()!!) {
                                viewModel.fetchQuoteRemote()
                                loadingQuoteUI()
                            } else {
                                viewModel.fetchLocalQuote()
                                loadingQuoteUI()
                            }
                        }
                        false -> {
                            loadingQuoteUI()
                            viewModel.fetchLocalQuote()
                        }
                    }
    }
}







