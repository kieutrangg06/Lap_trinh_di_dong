package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.entity.HocKyEntity
import com.example.matestudy.data.entity.MonHocEntity
import com.example.matestudy.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    val hocKyList: StateFlow<List<HocKyEntity>> = scheduleRepository.getAllHocKy()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedHocKy = MutableStateFlow<HocKyEntity?>(null)
    val selectedHocKy: StateFlow<HocKyEntity?> = _selectedHocKy.asStateFlow()

    val monHocList: StateFlow<List<MonHocEntity>> = _selectedHocKy
        .flatMapLatest { hk ->
            if (hk != null) scheduleRepository.getMonHocByHocKy(hk.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectHocKy(hocKy: HocKyEntity) {
        _selectedHocKy.value = hocKy
    }

    fun addHocKy(hocKy: HocKyEntity) {
        viewModelScope.launch {
            scheduleRepository.insertHocKy(hocKy)
        }
    }

    fun deleteHocKy(hocKy: HocKyEntity) {
        // TODO: triển khai xóa (cần xóa cả môn học liên quan nếu muốn)
        // Hiện tại FirestoreDataSource chưa có hàm deleteHocKy → cần thêm
    }

    fun addMonHoc(monHoc: MonHocEntity) {
        viewModelScope.launch {
            scheduleRepository.insertMonHoc(monHoc)
        }
    }

    fun deleteMonHoc(monHoc: MonHocEntity) {
        // TODO: triển khai delete nếu cần
    }
}