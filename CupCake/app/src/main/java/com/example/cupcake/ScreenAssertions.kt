package com.example.cupcake

import androidx.navigation.NavController
import org.junit.Assert.assertEquals

/**
 * Extension kiểm tra route hiện tại của NavController (dùng trong mọi test điều hướng)
 */
fun NavController.assertCurrentRouteName(expectedRouteName: String) {
    assertEquals(expectedRouteName, currentBackStackEntry?.destination?.route)
}