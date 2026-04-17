package com.example.matestudy.data.repository

import com.example.matestudy.data.entity.*
import com.example.matestudy.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val firestore: FirestoreDataSource) {

    // ────────────────────────────────────────────────
    // 1. HỌC KỲ (HOCKY)
    // ────────────────────────────────────────────────

    fun getAllHocKy(): Flow<List<HocKyEntity>> = firestore.getAllHocKy()

    suspend fun insertHocKy(hocKy: HocKyEntity) {
        firestore.insertHocKy(hocKy)
    }

    suspend fun deleteHocKy(id: Long) = firestore.deleteHocKy(id)

    // ────────────────────────────────────────────────
    // 2. MÔN HỌC (MONHOC)
    // ────────────────────────────────────────────────

    fun getAllMonHoc(): Flow<List<MonHocEntity>> = firestore.getAllMonHoc()

    fun getMonHocByHocKy(hocKyId: Long): Flow<List<MonHocEntity>> =
        firestore.getMonHocByHocKy(hocKyId)

    suspend fun getMonHocById(id: Long): MonHocEntity? =
        firestore.getMonHocById(id)

    suspend fun insertMonHoc(monHoc: MonHocEntity): Long =
        firestore.insertMonHoc(monHoc)

    suspend fun deleteMonHoc(id: Long) = firestore.deleteMonHoc(id)

    // ────────────────────────────────────────────────
    // 3. LỊCH CÁ NHÂN (LICHCANHAN)
    // ────────────────────────────────────────────────

    fun getLichCaNhan(sinhVienId: Long): Flow<List<LichCaNhanEntity>> =
        firestore.getLichBySinhVien(sinhVienId)

    suspend fun getLichById(id: Long): LichCaNhanEntity? =
        firestore.getLichById(id)

    suspend fun insertLich(lich: LichCaNhanEntity): Long =
        firestore.insertLich(lich)

    suspend fun updateLich(lich: LichCaNhanEntity) {
        firestore.updateLich(lich)
    }

    suspend fun deleteLich(lich: LichCaNhanEntity) {
        firestore.deleteLich(lich)
    }

    // ────────────────────────────────────────────────
    // 4. SỰ KIỆN CÁ NHÂN (SKCANHAN)
    // ────────────────────────────────────────────────

    suspend fun getSkById(id: Long): SkCaNhanEntity? =
        firestore.getSkById(id)

    suspend fun insertSk(sk: SkCaNhanEntity): Long =
        firestore.insertSk(sk)

    suspend fun updateSk(sk: SkCaNhanEntity) {
        firestore.updateSk(sk)
    }

    suspend fun deleteSk(sk: SkCaNhanEntity) {
        firestore.deleteSk(sk)
    }
}