package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.Event
import com.example.matestudy.data.entity.LichCaNhanEntity
import com.example.matestudy.data.entity.SkCaNhanEntity
import com.example.matestudy.data.repository.AuthRepository
import com.example.matestudy.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleViewModel(
    val scheduleRepository: ScheduleRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // ────────────────────────────────────────────────
    // 1. STATE - TRẠNG THÁI THỜI GIAN & DỮ LIỆU
    // ────────────────────────────────────────────────

    private val _currentMonth = MutableStateFlow(LocalDate.now())
    val currentMonth: StateFlow<LocalDate> = _currentMonth.asStateFlow()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _selectedEvent = MutableStateFlow<Event?>(null)
    val selectedEvent: StateFlow<Event?> = _selectedEvent.asStateFlow()

    private val _eventToEdit = MutableStateFlow<Event?>(null)
    val eventToEdit: StateFlow<Event?> = _eventToEdit.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ────────────────────────────────────────────────
    // 2. KHỞI TẠO & THEO DÕI USER
    // ────────────────────────────────────────────────

    init {
        viewModelScope.launch {
            authRepository.getCurrentUserFlow().collect { user ->
                if (user != null) {
                    loadEvents(user.id)
                }
            }
        }
    }

    // ────────────────────────────────────────────────
    // 3. XỬ LÝ TẢI & PHÂN TÍCH DỮ LIỆU SỰ KIỆN
    // ────────────────────────────────────────────────

    private suspend fun loadEvents(sinhVienId: Long) {
        val lichList = scheduleRepository.getLichCaNhan(sinhVienId).firstOrNull() ?: emptyList()
        val eventList = mutableListOf<Event>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dayMap = mapOf("2" to 1, "3" to 2, "4" to 3, "5" to 4, "6" to 5, "7" to 6, "CN" to 7)

        for (lich in lichList) {
            if (lich.loai == "lop_chinh_thuc" && lich.monHocId != null) {
                val mon = scheduleRepository.getMonHocById(lich.monHocId)
                mon?.let {
                    var tempDate = LocalDate.parse(it.ngayBatDau, formatter)
                    val endDate = LocalDate.parse(it.ngayKetThuc, formatter)
                    val thuInt = dayMap[it.thu] ?: -1

                    // Thuật toán quét: Chạy vòng lặp từ ngày bắt đầu đến ngày kết thúc môn học
                    //đến tempDate vượt quá ngày endDate
                    while (!tempDate.isAfter(endDate)) {
                        // Nếu ngày hiện tại trùng với Thứ của môn học đó
                        if (tempDate.dayOfWeek.value == thuInt) {
                            eventList.add(
                                createEventFromLich(
                                    lich, it.tenMon, it.diaDiem, tempDate,
                                    it.gioBatDau, it.gioKetThuc, it.tenGv
                                )
                            )
                        }
                        // Tăng thêm 1 ngày để kiểm tra ngày tiếp theo
                        tempDate = tempDate.plusDays(1)
                    }
                }
            } else if (lich.loai == "su_kien_rieng" && lich.skId != null) {
                val sk = scheduleRepository.getSkById(lich.skId)
                sk?.let {
                    var tempDate = LocalDate.parse(it.ngayBatDau, formatter)
                    val endDate = LocalDate.parse(it.ngayKetThuc, formatter)

                    if (it.lapLai == "khong" || it.lapLai == null) {
                        eventList.add(createEventFromSk(lich, it, tempDate))
                    } else {
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

        // Lấy ngày đầu tiên của tháng hiện tại đang xem trên giao diện
        val firstOfMonth = _currentMonth.value.withDayOfMonth(1)
        // Lấy ngày cuối cùng của tháng hiện tại đó
        val lastOfMonth = firstOfMonth.plusMonths(1).minusDays(1)

        // Chỉ lấy những sự kiện nằm trong khoảng từ (Đầu tháng - 7 ngày) đến (Cuối tháng + 7 ngày)
        // Việc này giúp lịch hiển thị mượt mà các ngày dư của tuần trước/tuần sau ở rìa giao diện tháng.
        _events.value = eventList.filter {
            !it.date.isBefore(firstOfMonth.minusDays(7)) &&
                    !it.date.isAfter(lastOfMonth.plusDays(7))
        }
    }

    // ────────────────────────────────────────────────
    // 4. FACTORY METHODS (OBJECT MAPPING)
    // ────────────────────────────────────────────────

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

    private fun createEventFromSk(
        lich: LichCaNhanEntity,
        sk: SkCaNhanEntity,
        date: LocalDate
    ): Event {
        return Event(
            id = lich.id,
            title = sk.tieuDe,
            location = sk.diaDiem,
            startTime = sk.gioBatDau?.let { LocalTime.parse(it) },
            endTime = sk.gioKetThuc?.let { LocalTime.parse(it) },
            date = date,
            color = lich.mauSac ?: "#3788d8",
            loai = lich.loai,
            originalId = sk.id
        )
    }

    // ────────────────────────────────────────────────
    // 5. PUBLIC ACTIONS - TƯƠNG TÁC UI & THỜI GIAN
    // ────────────────────────────────────────────────

    /**
     * Chuyển đổi tháng hiển thị khi nhấn nút Next/Previous (Thay đổi số delta là +1 hoặc -1)
     */
    fun changeMonth(delta: Int) {
        // Cập nhật giá trị tháng hiện tại bằng cách cộng/trừ số tháng tương ứng
        _currentMonth.value = _currentMonth.value.plusMonths(delta.toLong())
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserFlow().firstOrNull()?.id ?: return@launch
            loadEvents(userId)
        }
    }

    fun selectEvent(event: Event) {
        _selectedEvent.value = event
    }

    fun clearSelectedEvent() {
        _selectedEvent.value = null
    }

    // ────────────────────────────────────────────────
    // 6. CÁC THAO TÁC DỮ LIỆU (ADD, UPDATE, DELETE)
    // ────────────────────────────────────────────────

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

    suspend fun updateEvent(
        eventId: Long,
        originalId: Long,
        tieuDe: String,
        diaDiem: String?,
        thu: String?,
        lapLai: String,
        gioBd: String?,
        gioKt: String?,
        ngayBd: String,
        ngayKt: String,
        color: String
    ) {
        val userId = authRepository.getCurrentUserFlow().firstOrNull()?.id ?: return

        val sk = SkCaNhanEntity(
            id = originalId,
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
        scheduleRepository.updateSk(sk)

        val lich = scheduleRepository.getLichById(eventId)
        lich?.let {
            scheduleRepository.updateLich(it.copy(mauSac = color))
        }

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

    fun setEventToEdit(event: Event) {
        _eventToEdit.value = event
    }

    fun clearEventToEdit() {
        _eventToEdit.value = null
    }

    fun changeWeek(delta: Int) {
        _currentMonth.value = _currentMonth.value.plusWeeks(delta.toLong())

        viewModelScope.launch {
            val userId = authRepository.getCurrentUserFlow().firstOrNull()?.id ?: return@launch
            loadEvents(userId)
        }
    }
}