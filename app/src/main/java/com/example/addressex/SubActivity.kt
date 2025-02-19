package com.example.addressex

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import com.example.addressex.databinding.ActivitySubBinding
import timber.log.Timber

class SubActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPrivacyUrlToWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPrivacyUrlToWebView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
//            settings.allowFileAccessFromFileURLs = true // 🔹 file:// 접근 허용
//            settings.allowUniversalAccessFromFileURLs = true // 🔹 file://에서 JS 호출 허용
            addJavascriptInterface(BridgeInterface(), "Android")
            webViewClient = MyWebViewClient()
            webChromeClient = MyWebChromeClient()
            //loadUrl("file:///android_asset/index.html")
            loadUrl("https://appassets.androidplatform.net/assets/index.html")
        }
    }

    inner class BridgeInterface {
        @JavascriptInterface
        fun processDATA(data: String) {
            //주소 검색 API 검색 결과 값이 브릿지 통로를 통해 전달 받는다 (from JS)
            Timber.d("processDATA() data:$data")

            val resultIntent = Intent().apply {
                putExtra("ADDRESS", data)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this@SubActivity))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(this@SubActivity))
            .build()

        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
            Timber.d("shouldInterceptRequest() WebViewRequest: URL: ${request?.url}")

            if (request == null) return null
            return assetLoader.shouldInterceptRequest(request.url)
            //return super.shouldInterceptRequest(view, request)
        }
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            Timber.d("WebView shouldOverrideUrlLoading() url: ${request?.url}")
            return true
        }
        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
            Timber.d("WebView onPageStarted() url: $url")
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            Timber.d("WebView onPageFinished() url: $url")
            //Android -> Javascript
            binding.webView.loadUrl("javascript:sample2_execDaumPostcode();")
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress == 100) {

            }
        }

        /**
         * JavaScript의 console.log 메시지를 Logcat으로 출력
         */
        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            Timber.d("WebViewConsole: ${consoleMessage.message()} (Line: ${consoleMessage.lineNumber()})")
            return super.onConsoleMessage(consoleMessage)
        }
    }
}


