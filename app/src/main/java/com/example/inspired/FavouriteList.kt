package com.example.inspired

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inspired.model.QuoteResponse

class FavouriteList(private val favouriteQuotes: List<QuoteResponse.Quote>) : RecyclerView.Adapter<FavouriteList.FavouriteHolder>(){

    class FavouriteHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var text: TextView
        private var favourite: ImageView
        private var author: TextView

        init {
            text = itemView.findViewById(R.id.text_favourite)
            favourite = itemView.findViewById(R.id.imageView_favourite)
            author = itemView.findViewById(R.id.author_favourite)
        }

        fun setup(quote: QuoteResponse.Quote){
            text.text = quote.text
            favourite.setImageResource(R.drawable.ic_baseline_favorite_24_true)
            author.text = quote.author
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouriteHolder {
      return  FavouriteHolder(LayoutInflater.from(parent.context).inflate(R.layout.favourite_list,parent,false))
    }

    override fun getItemCount(): Int = favouriteQuotes.size

    override fun onBindViewHolder(holder: FavouriteHolder, position: Int) {
        holder.setup(favouriteQuotes[position])
    }

}