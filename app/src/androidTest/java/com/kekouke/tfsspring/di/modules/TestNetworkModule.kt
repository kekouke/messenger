package com.kekouke.tfsspring.di.modules

import dagger.Module

@Module
class TestNetworkModule : NetworkModule() {
    override fun baseUrl() = "http://localhost:8080"
}