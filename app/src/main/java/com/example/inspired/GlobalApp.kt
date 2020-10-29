package com.example.inspired

import android.app.Application
import com.example.inspired.repository.Repository

class GlobalApp : Application(){
    override fun onCreate() {
        super.onCreate()
        Repository.init(this)
    }
}