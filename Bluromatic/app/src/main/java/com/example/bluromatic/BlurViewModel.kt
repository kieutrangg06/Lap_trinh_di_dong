package com.example.bluromatic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.bluromatic.data.BluromaticRepository
import com.example.bluromatic.data.WorkManagerBluromaticRepository
import kotlinx.coroutines.launch

class BlurViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)
    private val repository: BluromaticRepository = WorkManagerBluromaticRepository(application)

    private val _blurUiState = MutableLiveData<BlurUiState>(BlurUiState.Initial)
    val blurUiState: LiveData<BlurUiState> = _blurUiState

    private val _outputWorkInfo = MutableLiveData<WorkInfo?>()
    val outputWorkInfo: LiveData<WorkInfo?> = _outputWorkInfo

    fun applyBlur(blurLevel: Int) {
        _blurUiState.value = BlurUiState.Loading

        repository.applyBlur(blurLevel)

        // Observe chỉ work có tag save_image
        workManager.getWorkInfosByTagLiveData("save_image")
            .observeForever { list ->
                if (list.isNotEmpty()) {
                    val workInfo = list[0]
                    _outputWorkInfo.value = workInfo

                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            val outputUri = workInfo.outputData.getString(KEY_IMAGE_URI)
                            if (!outputUri.isNullOrBlank()) {
                                _blurUiState.value = BlurUiState.Success(outputUri)
                            }
                        }
                        WorkInfo.State.FAILED -> {
                            _blurUiState.value = BlurUiState.Error("Failed to process image")
                        }
                        else -> {}
                    }
                }
            }
    }
}