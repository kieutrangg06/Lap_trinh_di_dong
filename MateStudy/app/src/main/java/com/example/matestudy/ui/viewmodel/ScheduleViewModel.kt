package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.Event
import com.example.matestudy.data.repository.AuthRepository
import com.example.matestudy.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.LocalTime
import com.example.matestudy.data.entity.LichCaNhanEntity
import com.example.matestudy.data.entity.SkCaNhanEntity

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
            authRepository.getCurrentUserFlow().collect { userEntity ->
                if (userEntity != null) {
                    loadEvents(userEntity.id)
                }
            }
        }
    }

    fun changeMonth(delta: Int) {
        _currentMonth.value = _currentMonth.value.plusMonths(delta.toLong())
        viewModelScope.launch { loadEvents(authRepository.getCurrentUserFlow().first()!!.id) }
    }

    private suspend fun loadEvents(sinhVienId: Long) {
        val lichList = scheduleRepository.getLichCaNhan(sinhVienId).firstOrNull() ?: emptyList()
        val eventList = mutableListOf<Event>()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dayMap = mapOf("2" to 1, "3" to 2, "4" to 3, "5" to 4, "6" to 5, "7" to 6, "CN" to 7) // DayOfWeek: 1=Monday, 7=Sunday

        for (lich in lichList) {
            if (lich.loai == "lop_chinh_thuc" && lich.mon_hoc_id != null) {
                val mon = scheduleRepository.getMonHocById(lich.mon_hoc_id).firstOrNull() ?: continue
                var date = LocalDate.parse(mon.ngay_bat_dau, formatter)
                val end = LocalDate.parse(mon.ngay_ket_thuc, formatter)

                while (!date.isAfter(end)) {
                    val thuInt = dayMap[mon.thu] ?: continue
                    if (date.dayOfWeek.value == thuInt) { // So sánh DayOfWeek (1=Mon,7=Sun)
                        eventList.add(
                            Event(
                                id = lich.id,
                                title = mon.ten_mon,
                                location = mon.dia_diem,
                                teacher = mon.ten_gv,
                                startTime = if (mon.gio_bat_dau != null) LocalTime.parse(mon.gio_bat_dau) else null,
                                endTime = if (mon.gio_ket_thuc != null) LocalTime.parse(mon.gio_ket_thuc) else null,
                                date = date,
                                color = lich.mau_sac,
                                loai = lich.loai,
                                originalId = mon.id
                            )
                        )
                    }
                    date = date.plusDays(1)
                }
            } else if (lich.loai == "su_kien_rieng" && lich.sk_id != null) {
                val sk = scheduleRepository.getSkById(lich.sk_id).firstOrNull() ?: continue
                var date = LocalDate.parse(sk.ngay_bat_dau, formatter)
                val end = LocalDate.parse(sk.ngay_ket_thuc, formatter)

                while (!date.isAfter(end)) {
                    eventList.add(
                        Event(
                            id = lich.id,
                            title = sk.tieu_de,
                            location = sk.dia_diem,
                            startTime = if (sk.gio_bat_dau != null) LocalTime.parse(sk.gio_bat_dau) else null,
                            endTime = if (sk.gio_ket_thuc != null) LocalTime.parse(sk.gio_ket_thuc) else null,
                            date = date,
                            color = lich.mau_sac,
                            loai = lich.loai,
                            repeat = sk.lap_lai,
                            originalId = sk.id
                        )
                    )
                    when (sk.lap_lai) {
                        "hang_ngay" -> date = date.plusDays(1)
                        "hang_tuan" -> date = date.plusWeeks(1)
                        else -> break
                    }
                }
            }
        }

        // Filter cho tháng hiện tại (bao gồm padding ngày trước/sau)
        val firstOfMonth = _currentMonth.value.withDayOfMonth(1)
        val lastOfMonth = firstOfMonth.plusMonths(1).minusDays(1)
        _events.value = eventList.filter { it.date.isAfter(firstOfMonth.minusDays(7)) && it.date.isBefore(lastOfMonth.plusDays(7)) }
    }

    fun selectEvent(event: Event) {
        _selectedEvent.value = event
    }

    fun clearSelectedEvent() {
        _selectedEvent.value = null
    }

    suspend fun addClass(monHocId: Long, color: String = "#3788d8") {
        val userId = authRepository.getCurrentUserFlow().first()?.id ?: return
        val lich = LichCaNhanEntity(
            sinh_vien_id = userId,
            loai = "lop_chinh_thuc",
            mon_hoc_id = monHocId,
            sk_id = null,           // fix lỗi
            mau_sac = color
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
        val userId = authRepository.getCurrentUserFlow().first()?.id ?: return
        val sk = SkCaNhanEntity(
            sinh_vien_id = userId,
            tieu_de = tieuDe,
            dia_diem = diaDiem,
            thu = thu,
            lap_lai = lapLai,
            gio_bat_dau = gioBd,
            gio_ket_thuc = gioKt,
            ngay_bat_dau = ngayBd,
            ngay_ket_thuc = ngayKt
        )
        val skId = scheduleRepository.insertSk(sk)

        val lich = LichCaNhanEntity(
            sinh_vien_id = userId,
            loai = "su_kien_rieng",
            mon_hoc_id = null,      // fix lỗi
            sk_id = skId,
            mau_sac = color
        )
        scheduleRepository.insertLich(lich)
        loadEvents(userId)
    }

    suspend fun deleteEvent(event: Event) {
        val lich = scheduleRepository.getLichById(event.id).firstOrNull() ?: return
        scheduleRepository.deleteLich(lich)
        if (event.loai == "su_kien_rieng") {
            val sk = scheduleRepository.getSkById(event.originalId).firstOrNull() ?: return
            scheduleRepository.deleteSk(sk)
        } // Lớp chính thức không xóa mon_hoc, chỉ xóa lich
        loadEvents(lich.sinh_vien_id)
    }

    // Tương tự cho update (bạn có thể mở rộng)
}