package com.example.inventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.inventory.data.AppDatabase
import com.example.inventory.data.repository.ItemRepository
import com.example.inventory.ui.screen.AddEditScreen
import com.example.inventory.ui.screen.InventoryScreen
import com.example.inventory.ui.viewmodel.InventoryViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: InventoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = ItemRepository(database.itemDao())
        viewModel = InventoryViewModel(repository)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "inventory"
                ) {
                    composable("inventory") {
                        InventoryScreen(
                            viewModel = viewModel,
                            onAddClick = { navController.navigate("add_edit") },
                            onEditClick = { item ->
                                viewModel.loadItemForEdit(item)
                                navController.navigate("add_edit")
                            }
                        )
                    }

                    composable("add_edit") {
                        AddEditScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                viewModel.resetForm()
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}