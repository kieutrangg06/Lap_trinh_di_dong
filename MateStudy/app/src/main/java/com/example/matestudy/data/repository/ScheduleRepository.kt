package com.example.matestudy.data.repository

import com.example.matestudy.data.AppDatabase
import com.example.matestudy.data.entity.*
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val db: AppDatabase) {
    private val hocKyDao = db.hocKyDao()
    private val monHocDao = db.monHocDao()
    private val lichCaNhanDao = db.lichCaNhanDao()
    private val skCaNhanDao = db.skCaNhanDao()

    // HocKy
    fun getAllHocKy(): Flow<List<HocKyEntity>> = hocKyDao.getAllHocKy()
    suspend fun insertHocKy(hocKy: HocKyEntity) = hocKyDao.insertHocKy(hocKy)

    // MonHoc
    fun getMonHocByHocKy(hocKyId: Long): Flow<List<MonHocEntity>> = monHocDao.getMonHocByHocKy(hocKyId)
    fun getMonHocById(id: Long): Flow<MonHocEntity?> = monHocDao.getMonHocById(id)
    suspend fun insertMonHoc(monHoc: MonHocEntity): Long = monHocDao.insertMonHoc(monHoc)

    // LichCaNhan
    fun getLichCaNhan(sinhVienId: Long): Flow<List<LichCaNhanEntity>> = lichCaNhanDao.getLichBySinhVien(sinhVienId)
    fun getLichById(id: Long): Flow<LichCaNhanEntity?> = lichCaNhanDao.getLichById(id)
    suspend fun insertLich(lich: LichCaNhanEntity): Long = lichCaNhanDao.insertLich(lich)
    suspend fun updateLich(lich: LichCaNhanEntity) = lichCaNhanDao.updateLich(lich)
    suspend fun deleteLich(lich: LichCaNhanEntity) = lichCaNhanDao.deleteLich(lich)

    // SkCaNhan
    fun getSkById(id: Long): Flow<SkCaNhanEntity?> = skCaNhanDao.getSkById(id)
    suspend fun insertSk(sk: SkCaNhanEntity): Long = skCaNhanDao.insertSk(sk)
    suspend fun updateSk(sk: SkCaNhanEntity) = skCaNhanDao.updateSk(sk)
    suspend fun deleteSk(sk: SkCaNhanEntity) = skCaNhanDao.deleteSk(sk)
}