package com.example.inventory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.entity.Item
import com.example.inventory.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InventoryUiState(
    val items: List<Item> = emptyList(),
    val currentItem: Item? = null,           // Dùng khi Edit
    val name: String = "",
    val price: String = "",
    val quantity: String = ""
)

class InventoryViewModel(private val repository: ItemRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allItems.collect { items ->
                _uiState.value = _uiState.value.copy(items = items)
            }
        }
    }

    fun updateForm(name: String, price: String, quantity: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            price = price,
            quantity = quantity
        )
    }

    fun loadItemForEdit(item: Item) {
        _uiState.value = _uiState.value.copy(
            currentItem = item,
            name = item.name,
            price = item.price.toString(),
            quantity = item.quantity.toString()
        )
    }

    fun addOrUpdateItem() {
        val state = _uiState.value
        val price = state.price.toDoubleOrNull() ?: 0.0
        val quantity = state.quantity.toIntOrNull() ?: 0

        viewModelScope.launch {
            if (state.currentItem == null) {
                // Add new
                val newItem = Item(name = state.name, price = price, quantity = quantity)
                repository.insert(newItem)
            } else {
                // Update
                val updatedItem = state.currentItem.copy(
                    name = state.name,
                    price = price,
                    quantity = quantity
                )
                repository.update(updatedItem)
            }
            resetForm()
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun resetForm() {
        _uiState.value = InventoryUiState()
    }
}