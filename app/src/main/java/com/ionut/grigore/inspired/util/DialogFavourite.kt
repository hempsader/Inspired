package com.ionut.grigore.inspired.util

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

import com.google.android.material.button.MaterialButton
import com.ionut.grigore.inspired.R
import com.ionut.grigore.inspired.model.QuoteResponse

class DialogFavourite(private val quote: QuoteResponse.Quote): DialogFragment(){


    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels*0.85).toInt()
        val height = (resources.displayMetrics.heightPixels*0.8).toInt()
        dialog?.window?.setLayout(width,height)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_shape)
        val view = inflater.inflate(R.layout.dialog_favourite,container,false)
        val dialogText = view.findViewById<TextView>(R.id.dialog_text)
        val dialogAuthor = view.findViewById<TextView>(R.id.dialog_author)
        val dialogCategory = view.findViewById<TextView>(R.id.dialog_category)
        view.findViewById<MaterialButton>(R.id.imageButton_share).apply {
            setOnClickListener {
                val shareQuote = Intent().apply{
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, quote.text + " - " + quote.author)
                    type = "text/plain"
                }
                requireContext().startActivity(Intent.createChooser(shareQuote,getString(R.string.chooser_title)))
            }
        }
        view.findViewById<Button>(R.id.button_dismiss).apply {
            setOnClickListener {
                dialog?.dismiss()
            }
        }
        dialogText.text = quote.text
        dialogAuthor.text = quote.author
        dialogCategory.text = "#"+quote.category

        return view
    }
}