package com.example.inventory.data.repository

import com.example.inventory.data.dao.ItemDao
import com.example.inventory.data.entity.Item
import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) {

    val allItems: Flow<List<Item>> = itemDao.getAllItems()

    suspend fun insert(item: Item) = itemDao.insert(item)
    suspend fun update(item: Item) = itemDao.update(item)
    suspend fun delete(item: Item) = itemDao.delete(item)
    fun getItemById(id: Int): Flow<Item?> = itemDao.getItemById(id)
}