package com.reysl.uroboros.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reysl.uroboros.components.MainApplication
import com.reysl.uroboros.data.Note
import com.reysl.uroboros.notification.ReminderIntentParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReminderNavigationViewModel : ViewModel() {

    private val _pendingNoteOpen = MutableLiveData<Note?>()
    val pendingNoteOpen: LiveData<Note?> = _pendingNoteOpen

    private val _openRequestId = MutableLiveData(0L)
    val openRequestId: LiveData<Long> = _openRequestId

    private val _noteNotFound = MutableLiveData(false)
    val noteNotFound: LiveData<Boolean> = _noteNotFound

    fun handleIntent(intent: Intent?) {
        val noteId = ReminderIntentParser.parseNoteId(intent) ?: return

        viewModelScope.launch {
            val note = withContext(Dispatchers.IO) {
                MainApplication.noteDatabase.getNoteDao().getNoteById(noteId)
            }
            if (note != null) {
                _noteNotFound.value = false
                _pendingNoteOpen.value = note
                _openRequestId.value = (_openRequestId.value ?: 0L) + 1L
            } else {
                _noteNotFound.value = true
            }
        }
    }

    fun consumePendingOpen() {
        _pendingNoteOpen.value = null
    }

    fun consumeNoteNotFound() {
        _noteNotFound.value = false
    }
}
