package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.entity.HocKyEntity
import com.example.matestudy.data.entity.MonHocEntity
import com.example.matestudy.data.repository.ScheduleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel(private val scheduleRepository: ScheduleRepository) : ViewModel() {

    // ────────────────────────────────────────────────
    // 1. STATE & FLOWS DỮ LIỆU
    // ────────────────────────────────────────────────

    val hocKyList = scheduleRepository.getAllHocKy()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedHocKy = MutableStateFlow<HocKyEntity?>(null)
    val selectedHocKy = _selectedHocKy.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredMonHocList = combine(_selectedHocKy, _searchQuery) { hk, query ->
        hk to query
    }.flatMapLatest { (hk, query) ->
        if (hk == null) {
            flowOf(emptyList())
        } else {
            scheduleRepository.getMonHocByHocKy(hk.id).map { list ->
                list.filter { it.tenMon.contains(query, ignoreCase = true) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ────────────────────────────────────────────────
    // 2. XỬ LÝ TƯƠNG TÁC UI
    // ────────────────────────────────────────────────

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun selectHocKy(hocKy: HocKyEntity?) {
        _selectedHocKy.value = hocKy
    }

    // ────────────────────────────────────────────────
    // 3. THAO TÁC HỌC KỲ (HOCKY)
    // ────────────────────────────────────────────────

    fun saveHocKy(hocKy: HocKyEntity) {
        viewModelScope.launch {
            scheduleRepository.insertHocKy(hocKy)
        }
    }

    fun deleteHocKy(id: Long) {
        viewModelScope.launch {
            scheduleRepository.deleteHocKy(id)
            if (_selectedHocKy.value?.id == id) _selectedHocKy.value = null
        }
    }

    // ────────────────────────────────────────────────
    // 4. THAO TÁC MÔN HỌC (MONHOC)
    // ────────────────────────────────────────────────

    fun saveMonHoc(monHoc: MonHocEntity) {
        viewModelScope.launch {
            scheduleRepository.insertMonHoc(monHoc)
        }
    }

    fun deleteMonHoc(id: Long) {
        viewModelScope.launch {
            scheduleRepository.deleteMonHoc(id)
        }
    }
}