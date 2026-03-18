package com.example.cupcake

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun CupcakeApp(
    navController: NavHostController = rememberNavController(),
    viewModel: OrderViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = CupcakeScreen.Start.name
    ) {
        composable(route = CupcakeScreen.Start.name) {
            StartOrderScreen(
                onNextButtonClicked = {
                    viewModel.setQuantity(it)
                    navController.navigate(CupcakeScreen.Flavor.name)
                }
            )
        }

        composable(route = CupcakeScreen.Flavor.name) {
            SelectOptionScreen(
                subtotal = viewModel.getSubtotal(),
                options = viewModel.flavors,
                onSelectionChanged = { viewModel.setFlavor(it) },
                onCancelButtonClicked = { navController.popBackStack(CupcakeScreen.Start.name, false) },
                onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },
                onNavigateUp = { navController.popBackStack() }
            )
        }

        composable(route = CupcakeScreen.Pickup.name) {
            SelectPickupDateScreen(
                subtotal = viewModel.getSubtotal(),
                options = viewModel.getPickupOptions(),
                onSelectionChanged = { viewModel.setDate(it) },
                onCancelButtonClicked = { navController.popBackStack(CupcakeScreen.Start.name, false) },
                onNextButtonClicked = { navController.navigate(CupcakeScreen.Summary.name) },
                onNavigateUp = { navController.popBackStack() }
            )
        }

        composable(route = CupcakeScreen.Summary.name) {
            OrderSummaryScreen(
                orderUiState = viewModel.uiState.value,
                onCancelButtonClicked = { navController.popBackStack(CupcakeScreen.Start.name, false) },
                onSendButtonClicked = {
                    // Optional: reset order sau khi share
                    // viewModel.resetOrder()
                    // navController.popBackStack(CupcakeScreen.Start.name, false)
                }
            )
        }
    }
}