package com.example.cupcake

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class OrderUiState(
    val quantity: Int = 0,
    val flavor: String = "",
    val date: String = "",
    val price: String = "$0.00"
)

class OrderViewModel : ViewModel() {
    val uiState = mutableStateOf(OrderUiState())

    val flavors = listOf("Vanilla", "Chocolate", "Red Velvet", "Salted Caramel", "Coffee")

    fun setQuantity(quantity: Int) {
        uiState.value = uiState.value.copy(quantity = quantity)
    }

    fun setFlavor(flavor: String) {
        uiState.value = uiState.value.copy(flavor = flavor)
    }

    fun setDate(date: String) {
        uiState.value = uiState.value.copy(date = date)
    }

    fun getSubtotal(): String {
        val pricePerCupcake = 2.00
        return "$${(uiState.value.quantity * pricePerCupcake).toInt()}.00"
    }

    fun getPickupOptions(): List<String> {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        return List(4) {
            formatter.format(calendar.time).also { calendar.add(Calendar.DATE, 1) }
        }
    }
}