package com.kekouke.tfsspring

import com.kekouke.tfsspring.di.DaggerTestAppComponent
import com.kekouke.tfsspring.di.TestAppComponent

class TestApp : App() {
    override val appComponent: TestAppComponent = DaggerTestAppComponent.factory().create(this)
}