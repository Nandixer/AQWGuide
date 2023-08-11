package ml.nandixer.aqwguide.presentation

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun UltrasScreen(viewModel: MainViewModel){
    var backEnable by remember{ mutableStateOf(false) }
    var webView: WebView? = null
    
    AndroidView(
        factory = {context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = object : WebViewClient(){
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        if (view != null) {
                            backEnable = view.canGoBack()
                        }
                    }
                }
                settings.javaScriptEnabled = true
                loadUrl("https://nandixer.github.io/tags/ultra")
                webView = this
            }

        }
    )


}