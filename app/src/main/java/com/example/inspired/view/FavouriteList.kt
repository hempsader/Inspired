package com.example.inspired.view

import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.inspired.R
import com.example.inspired.model.QuoteResponse
import com.example.inspired.util.UnfavouriteFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavouriteList(private val clickedQuote: ClickedQuote?, private val clickFavourite: ClickFavourite) : RecyclerView.Adapter<FavouriteList.FavouriteHolder>(){


    private var listQuotes = ArrayList<QuoteResponse.Quote>()
    fun setList(newQuoteList: List<QuoteResponse.Quote>){
        listQuotes = newQuoteList as ArrayList<QuoteResponse.Quote>
        notifyDataSetChanged()
    }

   inner class FavouriteHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var clicked: ClickedQuote? = clickedQuote
        private var clickedFavourite: ClickFavourite = clickFavourite
        private var text: TextView
        private var favourite: ImageView
        private var author: TextView
        private var category: TextView

        init {
            category = itemView.findViewById(R.id.category_favourite)
            text = itemView.findViewById(R.id.text_favourite)
            text.setOnClickListener(this)
            favourite = itemView.findViewById(R.id.imageView_favourite)
            favourite.setOnClickListener {
                clickedFavourite.sendQuoteFavourite(listQuotes[adapterPosition])
                UnfavouriteFlow.initialise()
                UnfavouriteFlow.emitFavourite(listQuotes[adapterPosition])
                GlobalScope.launch {
                    UnfavouriteFlow.readFavourite().collect {
                        Log.d("xx", it.toString())
                    }
                }
            }
            author = itemView.findViewById(R.id.author_favourite)
            author.setOnClickListener(this)
            itemView.setOnClickListener(this)

        }

        fun setup(quote: QuoteResponse.Quote){
            text.text = quote.text
            favourite.setImageResource(R.drawable.ic_baseline_favorite_24_true)
            author.text = quote.author
            category.text = "#${quote.category}"
        }

        override fun onClick(v: View?) {
            clicked?.sendQuote(listQuotes[adapterPosition])
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

class FavListDiffUtill(private val quoteListOld: List<QuoteResponse.Quote>,private val quoteListNew: List<QuoteResponse.Quote>): DiffUtil.Callback(){
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return quoteListOld[oldItemPosition].id == quoteListNew[newItemPosition].id
    }

    override fun getOldListSize(): Int {
        return quoteListOld.size
    }

    override fun getNewListSize(): Int {
        return quoteListNew.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return quoteListOld[oldItemPosition] == quoteListNew[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

}




