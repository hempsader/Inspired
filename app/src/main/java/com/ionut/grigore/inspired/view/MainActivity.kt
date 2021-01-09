package com.ionut.grigore.inspired.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.ionut.grigore.inspired.R
import com.ionut.grigore.inspired.util.UtilPreferences
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.IllegalStateException


class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.viewPager)
        FirebaseAnalytics.getInstance(this)
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab?.position!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }
        })
        setupTabs()
    }





    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
      menuInflater.inflate(R.menu.menu,menu)
        val settings = menu?.findItem(R.id.settings)
        settings?.setOnMenuItemClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
             true
        }
        return true
    }



    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            finishAfterTransition()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }


    private inner class ScreenSlidePagerAdapter(fa: FragmentManager) : FragmentStatePagerAdapter(fa) {
        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> FragmentRandom()
                1 -> FragmentFavourite()
                else -> FragmentRandom()
            }
        }
        override fun getCount(): Int = 2

    }

    private fun setupTabs(){
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)?.text = "Random"
        tabLayout.getTabAt(1)?.text = "Favourite"
        tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_outline_favorite_border_24_false)
        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_outline_transform_24)
    }


}





