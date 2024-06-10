package com.kekouke.tfsspring

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.kekouke.tfsspring.navigation.OnBackPressedListener
import javax.inject.Inject
import com.kekouke.tfsspring.R as TfSpringR

class MainActivity : AppCompatActivity(TfSpringR.layout.activity_main) {

    private val navigator = AppNavigator(this, TfSpringR.id.main_fragment_container)

    @Inject lateinit var navigation: Cicerone<Router>

    private val navigationHolder get() = navigation.getNavigatorHolder()

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent().inject(this)

        super.onCreate(savedInstanceState)

        addOnBackPressedCallback()
    }

    private fun addOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val fragment = supportFragmentManager.findFragmentById(
                    TfSpringR.id.main_fragment_container
                )

                if (
                    fragment == null ||
                    fragment !is OnBackPressedListener ||
                    !fragment.onBackPressed()
                ) {
                    finish()
                }
            }
        }

        onBackPressedDispatcher.addCallback(callback)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigationHolder.removeNavigator()
        super.onPause()

    }
}