package net.kisacasi.myarchive

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.error_layout.*
import net.kisacasi.myarchive.db.MovieDataBase
import net.kisacasi.myarchive.repository.MovieRepository
import net.kisacasi.myarchive.ui.MovieViewModel
import net.kisacasi.myarchive.ui.MovieViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModelFirstSettings()
        if (!viewModel.PublicNetControl()) {
            error_view.visibility = View.VISIBLE
            error_text.text = getString(R.string.no_internet)
            bottomNavigationView.visibility = View.GONE
            bottomNavigationView.menu.forEach {
                it.isEnabled = false
            }
        } else {
            bottomNavigationView.setupWithNavController(movieNavHostFragment.findNavController())
        }

    }

    private fun viewModelFirstSettings() {
        val movieRepository = MovieRepository(MovieDataBase(this))
        val viewModelProviderFactory = MovieViewModelProviderFactory(application, movieRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MovieViewModel::class.java)
    }

}
