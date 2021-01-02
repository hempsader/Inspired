package com.example.inspired.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.inspired.R
import com.example.inspired.model.QuoteResponse
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.dialog_favourite.*

class DialogFavourite: DialogFragment(){

    private var quote: QuoteResponse.Quote? = null
    fun quote(quote: QuoteResponse.Quote){
        this.quote = quote
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels*0.85).toInt()
        val height = (resources.displayMetrics.heightPixels*0.8).toInt()
        dialog?.window?.setLayout(width,height)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog_shape);
        val view = inflater.inflate(R.layout.dialog_favourite,container,false)

        val dialogText = view.findViewById<TextView>(R.id.dialog_text)
        val dialogAuthor = view.findViewById<TextView>(R.id.dialog_author)
        val dialogCategory = view.findViewById<TextView>(R.id.dialog_category)
        view.findViewById<MaterialButton>(R.id.imageButton_share).apply {
            setOnClickListener {
                quote?.let { ShareQuote(requireContext()).quote(it) }
            }
        }
        view.findViewById<Button>(R.id.button_dismiss).apply {
            setOnClickListener {
                dialog?.dismiss()
            }
        }
        dialogText.text = quote?.text
        dialogAuthor.text = quote?.author
        dialogCategory.text = "#"+quote?.category

        return view
    }
}