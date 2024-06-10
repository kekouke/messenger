package com.kekouke.tfsspring

import android.app.Application
import com.kekouke.tfsspring.di.components.AppComponent
import com.kekouke.tfsspring.di.components.DaggerAppComponent

open class App : Application() {
    open val appComponent: AppComponent = DaggerAppComponent.factory().create(this)
}