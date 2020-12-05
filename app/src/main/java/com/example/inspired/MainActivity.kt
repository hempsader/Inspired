package com.example.inspired

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.inspired.api.QuoteApi
import com.example.inspired.model.ViewModelTest
import com.example.inspired.util.InternetUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull


class MainActivity : AppCompatActivity() {
    val viewModel by lazy {
        ViewModelProvider(this, object: ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ViewModelTest(this@MainActivity) as T
            }
        })[ViewModelTest::class.java]
    }
    private val internetUtil = InternetUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onResume() {
        GlobalScope.launch {
            val x =  internetUtil.openChannel().consumeAsFlow()
            x.collect {
                Log.d("aa", it.toString())
            }
        }
        super.onResume()
    }

    override fun onPause() {
        GlobalScope.launch {
            internetUtil.cancelInternetCheck()
        }
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        internetUtil.cancelInternetCheck()
    }
}




