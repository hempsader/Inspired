package com.ionut.grigore.inspired.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ionut.grigore.inspired.R
import com.ionut.grigore.inspired.model.QuoteResponse
import com.ionut.grigore.inspired.repository.QuoteRepositoryImpl
import com.ionut.grigore.inspired.util.DialogFavourite
import com.ionut.grigore.inspired.util.UtilPreferences
import com.ionut.grigore.inspired.viewModel.QuoteFavouriteViewModel


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentFavourite : VisibleFragment(), ClickedQuote,
    ClickFavourite {
    private val viewModel by lazy {
        ViewModelProvider(this, object: ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return QuoteFavouriteViewModel(QuoteRepositoryImpl(), Dispatchers.IO + Job() ) as T
            }
        })[QuoteFavouriteViewModel::class.java]
    }
    private var adapterFavourites : FavouriteList? = null
    private var swipeLayout: SwipeRefreshLayout? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite,container,false)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_view_favourite)
        swipeLayout = view.findViewById(R.id.swipeLayout)
        adapterFavourites = FavouriteList(this, this)
        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }
        recycler.adapter = adapterFavourites
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.favouriteMutableLiveData.observe(viewLifecycleOwner, Observer {list->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Unconfined){
                sort(UtilPreferences.sortType(), list as java.util.ArrayList<QuoteResponse.Quote>)
                withContext(Dispatchers.Main){
                    adapterFavourites?.setList(list)
                }
            }
                    sort(UtilPreferences.sortType(), list as ArrayList)
                       adapterFavourites?.setList(list)
            swipeLayout?.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Unconfined){
                    withContext(Dispatchers.IO){
                        sort(UtilPreferences.sortType(), list)
                        swipeLayout?.isRefreshing = false
                        withContext(Dispatchers.Main) {
                            adapterFavourites?.setList(list)
                        }
                    }
                }
            }
        })
    }



    override fun sendQuote(quote: QuoteResponse.Quote) {
        if (quote != null) {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(DialogFavourite(quote), "tag")
                .commit()
        }
    }



    override fun sendQuoteFavourite(quote: QuoteResponse.Quote) {
        quote.favourite = !quote.favourite
        viewModel.favouriteUpdate(quote)
    }


}

