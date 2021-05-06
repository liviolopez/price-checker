package com.liviolopez.pricechecker.ui.camera

import android.app.Application
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {

    private val _scannerCodeCollector = MutableStateFlow<ProcessCameraProvider?>(null)
    val scannerCodeCollector = _scannerCodeCollector.asStateFlow()

    fun startScannerCodeCollector(){
        viewModelScope.launch {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(app)
            cameraProviderFuture.addListener(
                Runnable { _scannerCodeCollector.value = cameraProviderFuture.get() },
                ContextCompat.getMainExecutor(app)
            )
        }
    }
}