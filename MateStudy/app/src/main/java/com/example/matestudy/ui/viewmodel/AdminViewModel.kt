package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.entity.HocKyEntity
import com.example.matestudy.data.entity.MonHocEntity
import com.example.matestudy.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel(private val scheduleRepository: ScheduleRepository) : ViewModel() {
    val hocKyList = scheduleRepository.getAllHocKy()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedHocKy = MutableStateFlow<HocKyEntity?>(null)
    val selectedHocKy = _selectedHocKy.asStateFlow()

    // Filter môn học theo searchQuery và selectedHocKy
    val filteredMonHocList = combine(_selectedHocKy, _searchQuery) { hk, query ->
        hk to query
    }.flatMapLatest { (hk, query) ->
        val flow = if (hk != null) scheduleRepository.getMonHocByHocKy(hk.id)
        else flowOf(emptyList())
        flow.map { list ->
            list.filter { it.tenMon.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun selectHocKy(hocKy: HocKyEntity?) { _selectedHocKy.value = hocKy }

    // Thêm/Sửa/Xóa
    fun saveHocKy(hocKy: HocKyEntity) {
        viewModelScope.launch {
            if (hocKy.id == 0L) scheduleRepository.insertHocKy(hocKy)
            else scheduleRepository.updateHocKy(hocKy)
        }
    }

    fun deleteHocKy(id: Long) {
        viewModelScope.launch { scheduleRepository.deleteHocKy(id) }
    }

    fun saveMonHoc(monHoc: MonHocEntity) {
        viewModelScope.launch {
            if (monHoc.id == 0L) scheduleRepository.insertMonHoc(monHoc)
            else scheduleRepository.updateMonHoc(monHoc)
        }
    }

    fun deleteMonHoc(id: Long) {
        viewModelScope.launch { scheduleRepository.deleteMonHoc(id) }
    }
}