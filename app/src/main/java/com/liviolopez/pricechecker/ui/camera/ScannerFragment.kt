package com.liviolopez.pricechecker.ui.camera

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.liviolopez.pricechecker.R
import com.liviolopez.pricechecker.databinding.FragmentScannerBinding
import dagger.hilt.android.AndroidEntryPoint
import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScannerFragment : Fragment(R.layout.fragment_scanner) {
    private val TAG = "CameraFragment"

    private val viewModel: ScannerViewModel by activityViewModels()

    private lateinit var binding: FragmentScannerBinding

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraSelector: CameraSelector

    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null

    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScannerBinding.bind(view)

        cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        viewModel.startScannerCodeCollector()
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.scannerCodeCollector
                .debounce(500)
                .filterNotNull()
                .distinctUntilChanged()
                .collectLatest { provider ->
                    cameraProvider = provider
                    setCameraPreview()
                    analyseQrCode()
                }
        }
    }

    private fun setCameraPreview() {
        previewUseCase?.let { cameraProvider.unbind(it) }

        previewUseCase = Preview.Builder()
            .setTargetRotation(binding.pvCamera.display.rotation)
            .build()

        previewUseCase?.setSurfaceProvider(binding.pvCamera.surfaceProvider)

        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase)
        } catch (e: Exception) { }
    }

    private fun analyseQrCode() {
        val barcodeScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

        analysisUseCase?.let { cameraProvider.unbind(it) }

        analysisUseCase = ImageAnalysis.Builder()
            .setTargetRotation(binding.pvCamera.display.rotation)
            .build()

        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(
            cameraExecutor, { imageProxy ->
                processImageProxy(barcodeScanner, imageProxy)
            }
        )

        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase)
        } catch (e: Exception) { }
    }


    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { qrCodes ->
                backStackArg(qrCodes.firstOrNull()?.rawValue?.trim())
            }.addOnFailureListener {
                backStackArg(null)
            }.addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun backStackArg(codeScanned: String?){
        if(!codeScanned.isNullOrEmpty()) {
            val _this = this
            lifecycleScope.launchWhenResumed {
                findNavController(_this).apply {
                    previousBackStackEntry?.savedStateHandle?.set("codeScanned", codeScanned)
                    popBackStack()
                }
            }
        }
    }
}