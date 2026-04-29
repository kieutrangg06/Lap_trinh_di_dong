package com.example.inventory.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.inventory.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: InventoryViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showNumberPad by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.currentItem == null) "Add Item" else "Edit Item") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Item Name
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.updateForm(it, state.price, state.quantity) },
                label = { Text("Item Name*") },
                modifier = Modifier.fillMaxWidth()
            )

            // Price
            OutlinedTextField(
                value = state.price,
                onValueChange = { viewModel.updateForm(state.name, it, state.quantity) },
                label = { Text("Item Price*") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            // Quantity with Custom Number Pad
            OutlinedTextField(
                value = state.quantity,
                onValueChange = {}, // Không cho gõ tay
                label = { Text("Quantity in Stock*") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showNumberPad = true }) {
                        Text("⌨️")
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (state.name.isNotBlank()) {
                        viewModel.addOrUpdateItem()
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.name.isNotBlank() && state.price.isNotBlank() && state.quantity.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }

    // Custom Number Pad Dialog
    if (showNumberPad) {
        NumberPadDialog(
            currentValue = state.quantity,
            onValueChange = { newValue ->
                viewModel.updateForm(state.name, state.price, newValue)
            },
            onDismiss = { showNumberPad = false }
        )
    }
}