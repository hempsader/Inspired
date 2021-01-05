package com.example.inspired.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.inspired.R
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.DialogFavourite
import com.example.inspired.util.UtilPreferences
import com.example.inspired.viewModel.QuoteFavouriteViewModel
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
                       adapterFavourites?.setList(list)
            swipeLayout?.setOnRefreshListener(object: SwipeRefreshLayout.OnRefreshListener{
                override fun onRefresh() {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Unconfined){
                        sort(UtilPreferences.sortType(), list)
                        withContext(Dispatchers.Main){
                            swipeLayout?.isRefreshing = false
                            adapterFavourites?.setList(list)
                        }
                    }
                }
            })
        })
    }



    override fun sendQuote(quote: QuoteResponse.Quote) {
        var dialog: DialogFavourite? = null
        if (quote != null) {
            dialog = DialogFavourite()
            dialog?.quote(quote)
            dialog?.show(childFragmentManager, "tag")
        }
    }



    override fun sendQuoteFavourite(quote: QuoteResponse.Quote) {
        quote.favourite = !quote.favourite
        viewModel.favouriteUpdate(quote)
    }


}

