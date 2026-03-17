package com.example.matestudy.data.repository

import com.example.matestudy.data.entity.ThongBaoEntity
import com.example.matestudy.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.Flow

class ThongBaoRepository(private val firestore: FirestoreDataSource) {

    fun getThongBaoFlow(sinhVienId: Long): Flow<List<ThongBaoEntity>> =
        firestore.getThongBaoCuaToi(sinhVienId)

    suspend fun markAsRead(id: Long) = firestore.markAsRead(id)

    suspend fun markAllAsRead(sinhVienId: Long) = firestore.markAllAsRead(sinhVienId)

    suspend fun getUnreadCount(sinhVienId: Long): Int = firestore.getUnreadCount(sinhVienId)

    suspend fun createThongBao(thongBao: ThongBaoEntity) = firestore.insertThongBao(thongBao)
}