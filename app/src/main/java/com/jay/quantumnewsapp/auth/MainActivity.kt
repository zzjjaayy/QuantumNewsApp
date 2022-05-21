package com.jay.quantumnewsapp.auth

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.doOnPreDraw
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.jay.quantumnewsapp.databinding.ActivityMainBinding
import com.jay.quantumnewsapp.auth.fragments.TabAdapter
import com.jay.quantumnewsapp.news.NewsActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        installSplashScreen().setOnExitAnimationListener { splashScreenView ->
            ObjectAnimator.ofFloat(splashScreenView.iconView, View.TRANSLATION_X,
                0f, -splashScreenView.iconView.height.toFloat()
            ).apply {
                interpolator = AnticipateInterpolator()
                duration = 300L
                doOnEnd { splashScreenView.remove() }
                start()
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.doOnPreDraw {
            if(auth.currentUser == null) {
                setupTabLayoutWithViewPager()
            } else {
                goToHomePage()
            }
        }
    }

    fun changeToTab(position: Int) {
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
    }

    fun goToHomePage() {
        startActivity(Intent(this, NewsActivity::class.java))
        finish()
    }

    private fun setupTabLayoutWithViewPager() {
        binding.tabLayout.apply {
            addTab(this.newTab().setText("Login"))
            addTab(this.newTab().setText("Sign up"))
            addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    binding.viewPager.currentItem = tab.position
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }

        val adapter = TabAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.apply {
            this.adapter = adapter
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
                }
            })
        }
    }
}