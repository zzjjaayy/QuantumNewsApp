package com.jay.quantumnewsapp.news

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.jay.quantumnewsapp.R
import com.jay.quantumnewsapp.auth.MainActivity
import com.jay.quantumnewsapp.databinding.ActivityNewsBinding
import com.jay.quantumnewsapp.utils.TAG
import com.jay.quantumnewsapp.utils.openWebpage

class NewsActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityNewsBinding

    private lateinit var searchView: SearchView
    private lateinit var auth: FirebaseAuth
    private lateinit var newsAdapter: NewsAdapter
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsAdapter = NewsAdapter {
            openWebpage(this, it.url)
        }
        binding.newsRecyclerView.adapter = newsAdapter

        viewModel.getTopHeadlines {
            Log.d(TAG, "onCreate: There are ${it?.size} news items!")
            binding.progressBar.isVisible = false
            if(it == null) {
                binding.errorImage.isVisible = true
                Toast.makeText(this, "Error getting news!", Toast.LENGTH_SHORT).show()
            } else {
                newsAdapter.submitList(it)
                binding.errorImage.isVisible = false
            }
        }
        binding.progressBar.isVisible = true

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(!searchView.isIconified) {
                    searchView.isIconified = true
                    newsAdapter.submitList(viewModel.topHeadlines)
                } else this@NewsActivity.finish()
            }
        })
    }

    private fun confirmLogout() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        alertDialogBuilder.setNegativeButton("No") {_,_ -> } // Nothing should happen
        alertDialogBuilder.setTitle("Logout?")
        alertDialogBuilder.setMessage("You will need to log in again to use the app!")
        alertDialogBuilder.create().show()
    }

    /**
     * MENU AND SEARCH OPTIONS
     */

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.news_menu, menu)

        val search = menu.findItem(R.id.menu_search) ?: run {
            return false
        }
        searchView = (search.actionView as? SearchView)!!
        searchView.setOnQueryTextListener(this)

        searchView.setOnSearchClickListener {}
        searchView.setOnCloseListener {
            false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout) confirmLogout()
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        p0?.let {
            viewModel.searchNews(p0) {
                Log.d(TAG, "onCreate: There are ${it?.size} news items!")
                binding.progressBar.isVisible = false
                if(it == null) {
                    binding.errorImage.isVisible = true
                    Toast.makeText(this, "Error getting news!", Toast.LENGTH_SHORT).show()
                } else {
                    newsAdapter.submitList(it)
                    binding.errorImage.isVisible = false
                }
            }
            binding.progressBar.isVisible = true
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        // do nothing
        return true
    }
}