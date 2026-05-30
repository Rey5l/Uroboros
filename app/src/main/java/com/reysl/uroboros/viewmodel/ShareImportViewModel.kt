package com.reysl.uroboros.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reysl.uroboros.utils.ArticleContentFetcher
import com.reysl.uroboros.utils.ShareIntentParser
import com.reysl.uroboros.utils.SharePayload
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ShareImportViewModel : ViewModel() {

    private val _pendingShare = MutableLiveData<SharePayload?>()
    val pendingShare: LiveData<SharePayload?> = _pendingShare

    private val _isLoadingPage = MutableLiveData(false)
    val isLoadingPage: LiveData<Boolean> = _isLoadingPage

    private var fetchJob: Job? = null

    fun handleIntent(intent: Intent?) {
        fetchJob?.cancel()
        val parsed = ShareIntentParser.parse(intent) ?: return

        val url = parsed.sourceUrl ?: ShareIntentParser.extractUrl(parsed.rawText)
        if (url == null) {
            _isLoadingPage.value = false
            _pendingShare.value = parsed
            return
        }

        fetchJob = viewModelScope.launch {
            _isLoadingPage.value = true
            _pendingShare.value = null

            val enriched = try {
                val pageContent = ArticleContentFetcher.fetch(url)
                parsed.withPageContent(pageContent)
            } catch (_: Exception) {
                parsed
            }

            _pendingShare.value = enriched
            _isLoadingPage.value = false
        }
    }

    fun consumeShare() {
        fetchJob?.cancel()
        _isLoadingPage.value = false
        _pendingShare.value = null
    }
}
