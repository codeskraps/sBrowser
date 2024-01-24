package com.codeskraps.sbrowser.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BackgroundStatus {

    private val _status by lazy { MutableStateFlow(false) }
    val status = _status.asStateFlow()

    fun setValue(value: Boolean) {
        CoroutineScope(Dispatchers.IO).run {
            launch { _status.emit(value) }
        }
    }
}