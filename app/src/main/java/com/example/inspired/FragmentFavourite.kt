package com.example.inspired

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.viewModel.QuoteFavouriteViewModel
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class FragmentFavourite : Fragment(){
    private val viewModel by lazy {
        ViewModelProvider(this, object: ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return QuoteFavouriteViewModel(QuoteRepositoryImpl(requireContext()), Dispatchers.IO + Job()) as T
            }
        })[QuoteFavouriteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite,container,false)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_view_favourite)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel.favouriteQuotes().observe(viewLifecycleOwner, Observer {
            recycler.adapter = FavouriteList(it)
        })


        return view
    }
}