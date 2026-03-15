package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.Event
import com.example.matestudy.data.entity.LichCaNhanEntity
import com.example.matestudy.data.entity.MonHocEntity
import com.example.matestudy.data.entity.SkCaNhanEntity
import com.example.matestudy.data.repository.AuthRepository
import com.example.matestudy.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.LocalTime

class ScheduleViewModel(
    val scheduleRepository: ScheduleRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(LocalDate.now())
    val currentMonth: StateFlow<LocalDate> = _currentMonth.asStateFlow()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _selectedEvent = MutableStateFlow<Event?>(null)
    val selectedEvent: StateFlow<Event?> = _selectedEvent.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getCurrentUserFlow().collect { user ->
                if (user != null) {
                    loadEvents(user.id)
                }
            }
        }
    }

    fun changeMonth(delta: Int) {
        _currentMonth.value = _currentMonth.value.plusMonths(delta.toLong())
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserFlow().firstOrNull()?.id ?: return@launch
            loadEvents(userId)
        }
    }

    private suspend fun loadEvents(sinhVienId: Long) {
        val lichList = scheduleRepository.getLichCaNhan(sinhVienId).firstOrNull() ?: emptyList()
        val eventList = mutableListOf<Event>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dayMap = mapOf("2" to 1, "3" to 2, "4" to 3, "5" to 4, "6" to 5, "7" to 6, "CN" to 7)

        for (lich in lichList) {
            if (lich.loai == "lop_chinh_thuc" && lich.monHocId != null) {
                val mon: MonHocEntity? = scheduleRepository.getMonHocById(lich.monHocId)
                mon?.let {
                    var date = LocalDate.parse(it.ngayBatDau, formatter)
                    val end = LocalDate.parse(it.ngayKetThuc, formatter)

                    while (!date.isAfter(end)) {
                        val thuInt = dayMap[it.thu] ?: continue
                        if (date.dayOfWeek.value == thuInt) {
                            eventList.add(
                                Event(
                                    id = lich.id,
                                    title = it.tenMon,
                                    location = it.diaDiem,
                                    teacher = it.tenGv,
                                    startTime = it.gioBatDau?.let { time -> LocalTime.parse(time) },
                                    endTime = it.gioKetThuc?.let { time -> LocalTime.parse(time) },
                                    date = date,
                                    color = lich.mauSac,
                                    loai = lich.loai,
                                    originalId = it.id
                                )
                            )
                        }
                        date = date.plusDays(1)
                    }
                }
            } else if (lich.loai == "su_kien_rieng" && lich.skId != null) {
                val sk: SkCaNhanEntity? = scheduleRepository.getSkById(lich.skId)
                sk?.let {
                    var date = LocalDate.parse(it.ngayBatDau, formatter)
                    val end = LocalDate.parse(it.ngayKetThuc, formatter)

                    while (!date.isAfter(end)) {
                        eventList.add(
                            Event(
                                id = lich.id,
                                title = it.tieuDe,
                                location = it.diaDiem,
                                startTime = it.gioBatDau?.let { time -> LocalTime.parse(time) },
                                endTime = it.gioKetThuc?.let { time -> LocalTime.parse(time) },
                                date = date,
                                color = lich.mauSac,
                                loai = lich.loai,
                                repeat = it.lapLai,
                                originalId = it.id
                            )
                        )
                        when (it.lapLai) {
                            "hang_ngay" -> date = date.plusDays(1)
                            "hang_tuan" -> date = date.plusWeeks(1)
                            else -> break
                        }
                    }
                }
            }
        }

        val firstOfMonth = _currentMonth.value.withDayOfMonth(1)
        val lastOfMonth = firstOfMonth.plusMonths(1).minusDays(1)
        _events.value = eventList.filter {
            !it.date.isBefore(firstOfMonth.minusDays(7)) && !it.date.isAfter(lastOfMonth.plusDays(7))
        }
    }

    fun selectEvent(event: Event) {
        _selectedEvent.value = event
    }

    fun clearSelectedEvent() {
        _selectedEvent.value = null
    }

    suspend fun addClass(monHocId: Long, color: String = "#3788d8") {
        val userId = authRepository.getCurrentUserFlow().firstOrNull()?.id ?: return
        val lich = LichCaNhanEntity(
            sinhVienId = userId,
            loai = "lop_chinh_thuc",
            monHocId = monHocId,
            skId = null,
            mauSac = color
        )
        scheduleRepository.insertLich(lich)
        loadEvents(userId)
    }

    suspend fun addEvent(
        tieuDe: String,
        diaDiem: String?,
        thu: String?,
        lapLai: String,
        gioBd: String?,
        gioKt: String?,
        ngayBd: String,
        ngayKt: String,
        color: String = "#3788d8"
    ) {
        val userId = authRepository.getCurrentUserFlow().firstOrNull()?.id ?: return

        val sk = SkCaNhanEntity(
            sinhVienId = userId,
            tieuDe = tieuDe.trim(),
            diaDiem = diaDiem,
            thu = thu,
            lapLai = lapLai,
            gioBatDau = gioBd,
            gioKetThuc = gioKt,
            ngayBatDau = ngayBd.trim(),
            ngayKetThuc = ngayKt.trim()
        )

        val skId = scheduleRepository.insertSk(sk)

        val lich = LichCaNhanEntity(
            sinhVienId = userId,
            loai = "su_kien_rieng",
            monHocId = null,
            skId = skId,
            mauSac = color
        )

        scheduleRepository.insertLich(lich)
        loadEvents(userId)
    }

    suspend fun deleteEvent(event: Event) {
        val lich = scheduleRepository.getLichById(event.id) ?: return
        scheduleRepository.deleteLich(lich)

        if (event.loai == "su_kien_rieng") {
            val sk = scheduleRepository.getSkById(event.originalId)
            sk?.let { scheduleRepository.deleteSk(it) }
        }

        loadEvents(lich.sinhVienId)
    }
}