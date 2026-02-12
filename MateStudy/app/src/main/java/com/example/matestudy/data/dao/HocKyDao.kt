package com.example.matestudy.data.dao

import androidx.room.*
import com.example.matestudy.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HocKyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHocKy(hocKy: HocKyEntity)

    @Query("SELECT * FROM hoc_ky")
    fun getAllHocKy(): Flow<List<HocKyEntity>>

    @Delete
    suspend fun deleteHocKy(hocKy: HocKyEntity)
}

@Dao
interface MonHocDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonHoc(monHoc: MonHocEntity): Long // Trả id mới

    @Query("SELECT * FROM mon_hoc WHERE hoc_ky_id = :hocKyId")
    fun getMonHocByHocKy(hocKyId: Long): Flow<List<MonHocEntity>>

    @Query("SELECT * FROM mon_hoc WHERE id = :id")
    fun getMonHocById(id: Long): Flow<MonHocEntity?>

    @Update
    suspend fun updateMonHoc(monHoc: MonHocEntity)

    @Delete
    suspend fun deleteMonHoc(monHoc: MonHocEntity)
}

@Dao
interface LichCaNhanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLich(lich: LichCaNhanEntity): Long

    @Query("SELECT * FROM lich_ca_nhan WHERE sinh_vien_id = :sinhVienId")
    fun getLichBySinhVien(sinhVienId: Long): Flow<List<LichCaNhanEntity>>

    @Query("SELECT * FROM lich_ca_nhan WHERE id = :id")
    fun getLichById(id: Long): Flow<LichCaNhanEntity?>

    @Update
    suspend fun updateLich(lich: LichCaNhanEntity)

    @Delete
    suspend fun deleteLich(lich: LichCaNhanEntity)
}

@Dao
interface SkCaNhanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSk(sk: SkCaNhanEntity): Long

    @Query("SELECT * FROM sk_ca_nhan WHERE sinh_vien_id = :sinhVienId")
    fun getSkBySinhVien(sinhVienId: Long): Flow<List<SkCaNhanEntity>>

    @Query("SELECT * FROM sk_ca_nhan WHERE id = :id")
    fun getSkById(id: Long): Flow<SkCaNhanEntity?>

    @Update
    suspend fun updateSk(sk: SkCaNhanEntity)

    @Delete
    suspend fun deleteSk(sk: SkCaNhanEntity)
}