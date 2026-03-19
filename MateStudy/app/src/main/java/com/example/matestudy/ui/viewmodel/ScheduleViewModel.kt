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
        // 1. Lấy toàn bộ danh sách lịch từ Repository
        val lichList = scheduleRepository.getLichCaNhan(sinhVienId).firstOrNull() ?: emptyList()
        val eventList = mutableListOf<Event>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Bản đồ chuyển đổi Thứ từ DB sang DayOfWeek của hệ thống
        val dayMap = mapOf("2" to 1, "3" to 2, "4" to 3, "5" to 4, "6" to 5, "7" to 6, "CN" to 7)

        for (lich in lichList) {
            // --- XỬ LÝ LỚP HỌC (Giữ nguyên hoặc tối ưu) ---
            if (lich.loai == "lop_chinh_thuc" && lich.monHocId != null) {
                val mon = scheduleRepository.getMonHocById(lich.monHocId)
                mon?.let {
                    var tempDate = LocalDate.parse(it.ngayBatDau, formatter)
                    val endDate = LocalDate.parse(it.ngayKetThuc, formatter)
                    val thuInt = dayMap[it.thu] ?: -1

                    while (!tempDate.isAfter(endDate)) {
                        if (tempDate.dayOfWeek.value == thuInt) {
                            eventList.add(createEventFromLich(lich, it.tenMon, it.diaDiem, tempDate, it.gioBatDau, it.gioKetThuc, it.tenGv))
                        }
                        tempDate = tempDate.plusDays(1)
                    }
                }
            }
            // --- XỬ LÝ SỰ KIỆN RIÊNG (Phần quan trọng) ---
            else if (lich.loai == "su_kien_rieng" && lich.skId != null) {
                val sk = scheduleRepository.getSkById(lich.skId)
                sk?.let {
                    var tempDate = LocalDate.parse(it.ngayBatDau, formatter)
                    val endDate = LocalDate.parse(it.ngayKetThuc, formatter)

                    // Quan trọng: Nếu không lặp, chỉ add 1 lần rồi thoát
                    if (it.lapLai == "khong" || it.lapLai == null) {
                        eventList.add(createEventFromSk(lich, it, tempDate))
                    } else {
                        // Nếu có lặp (hàng ngày hoặc hàng tuần)
                        while (!tempDate.isAfter(endDate)) {
                            eventList.add(createEventFromSk(lich, it, tempDate))

                            when (it.lapLai) {
                                "hang_ngay" -> tempDate = tempDate.plusDays(1)
                                "hang_tuan" -> tempDate = tempDate.plusWeeks(1)
                                else -> break
                            }
                        }
                    }
                }
            }
        }

        // 2. Cập nhật State Flow (Bỏ bớt filter khắt khe để dễ debug)
        val firstOfMonth = _currentMonth.value.withDayOfMonth(1)
        val lastOfMonth = firstOfMonth.plusMonths(1).minusDays(1)

        // Mở rộng phạm vi filter để bao phủ cả các ô trống ở đầu/cuối Grid lịch (42 ô)
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

    // Thêm các hàm này vào cuối class ScheduleViewModel

    private fun createEventFromSk(lich: LichCaNhanEntity, sk: SkCaNhanEntity, date: LocalDate): Event {
        return Event(
            id = lich.id, // ID của dòng trong bảng LichCaNhan
            title = sk.tieuDe,
            location = sk.diaDiem,
            startTime = sk.gioBatDau?.let { LocalTime.parse(it) },
            endTime = sk.gioKetThuc?.let { LocalTime.parse(it) },
            date = date,
            color = lich.mauSac ?: "#3788d8",
            loai = lich.loai,
            originalId = sk.id // ID của sự kiện gốc trong bảng SkCaNhan
        )
    }

    private fun createEventFromLich(
        lich: LichCaNhanEntity,
        tenMon: String,
        diaDiem: String?,
        date: LocalDate,
        gioBd: String?,
        gioKt: String?,
        tenGv: String?
    ): Event {
        return Event(
            id = lich.id,
            title = tenMon,
            location = diaDiem,
            teacher = tenGv,
            startTime = gioBd?.let { LocalTime.parse(it) },
            endTime = gioKt?.let { LocalTime.parse(it) },
            date = date,
            color = lich.mauSac ?: "#3788d8",
            loai = lich.loai,
            originalId = lich.monHocId ?: 0L
        )
    }
}