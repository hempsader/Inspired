package com.example.inspired.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inspired.R
import com.example.inspired.model.QuoteResponse

class FavouriteList(private val clickedQuote: ClickedQuote, private val clickFavourite: ClickFavourite) : RecyclerView.Adapter<FavouriteList.FavouriteHolder>(){
        private var listQuotes = ArrayList<QuoteResponse.Quote>()


    fun setList(list: List<QuoteResponse.Quote>){
        listQuotes = list as ArrayList<QuoteResponse.Quote>
        notifyDataSetChanged()
    }

   inner class FavouriteHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var clicked: ClickedQuote = clickedQuote
       private var clickedFavourite: ClickFavourite = clickFavourite
       private var text: TextView
        private var favourite: ImageView
        private var author: TextView

        init {
            text = itemView.findViewById(R.id.text_favourite)
            text.setOnClickListener(this)
            favourite = itemView.findViewById(R.id.imageView_favourite)
            favourite.setOnClickListener {
                clickedFavourite.sendQuoteFavourite(listQuotes[adapterPosition])
            }
            author = itemView.findViewById(R.id.author_favourite)
            author.setOnClickListener(this)
        }

        fun setup(quote: QuoteResponse.Quote){
            text.text = quote.text
            favourite.setImageResource(R.drawable.ic_baseline_favorite_24_true)
            author.text = quote.author
        }

        override fun onClick(v: View?) {
            clicked.sendQuote(listQuotes[adapterPosition])
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouriteHolder {
      return  FavouriteHolder(LayoutInflater.from(parent.context).inflate(R.layout.favourite_list,parent,false))
    }

    override fun getItemCount(): Int = listQuotes.size

    override fun onBindViewHolder(holder: FavouriteHolder, position: Int) {
        holder.setup(listQuotes[position])
    }

}

interface ClickedQuote{
    fun sendQuote(quote: QuoteResponse.Quote)
}

interface ClickFavourite{
    fun sendQuoteFavourite(quote: QuoteResponse.Quote)
}




