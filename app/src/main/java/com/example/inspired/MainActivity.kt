package com.example.inspired

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.inspired.model.ViewModelTest
import com.example.inspired.util.InternetUtil
import com.example.inspired.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume


class MainActivity : UtilActivity() {
    private var job: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
     job  =   GlobalScope.launch(Dispatchers.IO) {
            internetFlow().asFlow().collectLatest {
                Log.d("aa", it.toString())
            }
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
        job = null
    }
}




