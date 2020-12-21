package com.example.inspired

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.DialogFavourite
import com.example.inspired.viewModel.QuoteFavouriteViewModel
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class FragmentFavourite : Fragment(), ClickedQuote{
    private val dialog = DialogFavourite()
    private val viewModel by lazy {
        ViewModelProvider(this, object: ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return QuoteFavouriteViewModel(QuoteRepositoryImpl(requireContext()), Dispatchers.IO + Job() ) as T
            }
        })[QuoteFavouriteViewModel::class.java]
    }
    private val adapterFavourites = FavouriteList(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite,container,false)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_view_favourite)

        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }
        recycler.adapter = adapterFavourites




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.favouriteMutableLiveData.observe(viewLifecycleOwner, Observer {
            adapterFavourites.setList(it)
        })
    }

    override fun sendQuote(quote: QuoteResponse.Quote) {
        dialog.quote(quote)
        dialog.show(childFragmentManager,"tag")
    }
}