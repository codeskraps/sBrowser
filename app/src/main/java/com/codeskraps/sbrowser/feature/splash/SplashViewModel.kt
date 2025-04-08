package com.codeskraps.sbrowser.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeskraps.sbrowser.umami.domain.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        initializeAnalytics()
    }

    private fun initializeAnalytics() {
        try {
            viewModelScope.launch {
                analyticsRepository.initialize()
            }
            _isReady.value = true
        } catch (e: Exception) {
            // If analytics fails to initialize, we still want to proceed with the app
            _isReady.value = true
        }
    }
} 