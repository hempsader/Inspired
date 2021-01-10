package com.ionut.grigore.inspired.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroCustomLayoutFragment
import com.github.appintro.AppIntroPageTransformerType
import com.ionut.grigore.inspired.R
import com.ionut.grigore.inspired.util.UtilPreferences


class OnBoardActivity: AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransformer(AppIntroPageTransformerType.Depth)
        // Toggle Indicator Visibility
        isIndicatorEnabled = true

        setIndicatorColor(
            selectedIndicatorColor = Color.RED,
            unselectedIndicatorColor = Color.BLUE
        )

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.slide_one))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.slide_two))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.slide_three))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.slide_four))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        UtilPreferences.tutorialSet(true)
        finish()
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        UtilPreferences.tutorialSet(true)
        finish()
    }

}