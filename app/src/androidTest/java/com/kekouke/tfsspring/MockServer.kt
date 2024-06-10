package com.kekouke.tfsspring

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.badRequest
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.kekouke.tfsspring.AssetsUtils.fromAssets

private const val SINGLE_MESSAGE_LIST = "single_message_list.json"
private const val EMPTY_MESSAGE_LIST = "empty_message_list.json"

class MockServer(private val wireMockServer: WireMockServer) {

    fun withOneMessageList() {
        wireMockServer.stubFor(
            WireMock.post(registerEventQueueUrlPattern)
                .willReturn(ok())
        )

        wireMockServer.stubFor(
            WireMock.get(getMessagesUrlPattern)
                .willReturn(ok(fromAssets(SINGLE_MESSAGE_LIST)))
        )
    }

    fun withEmptyMessagesList() {
        wireMockServer.stubFor(
            WireMock.post(registerEventQueueUrlPattern)
                .willReturn(ok())
        )

        wireMockServer.stubFor(
            WireMock.get(getMessagesUrlPattern)
                .willReturn(ok(fromAssets(EMPTY_MESSAGE_LIST)))
        )
    }

    fun withErrorOnLoadMessages() {
        wireMockServer.stubFor(WireMock.get(getMessagesUrlPattern).willReturn(badRequest()))
    }

    companion object {

        private val getMessagesUrlPattern = urlPathMatching("/messages")
        private val registerEventQueueUrlPattern = urlPathMatching("/register")

        fun WireMockServer.stubWith(block: MockServer.() -> Unit) {
            MockServer(this).apply(block)
        }
    }

}