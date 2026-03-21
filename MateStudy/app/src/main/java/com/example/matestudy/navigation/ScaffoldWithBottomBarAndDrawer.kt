package com.example.matestudy.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.matestudy.ui.theme.PrimaryPink
import com.example.matestudy.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithBottomBarAndDrawer(
    bottomNavController: NavHostController,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentUser by authViewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.vaiTro == "admin"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerTonalElevation = 0.dp
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    if (isAdmin) "Quản trị MATestudy" else "MATestudy",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = PrimaryPink
                    )
                )
                HorizontalDivider(Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                Spacer(Modifier.height(16.dp))

                if (!isAdmin) {
                    DrawerItem(
                        icon = Icons.Default.Person,
                        label = "Hồ sơ cá nhân",
                        selected = currentRoute == BottomNavItem.Profile.route
                    ) {
                        bottomNavController.navigate(BottomNavItem.Profile.route)
                        scope.launch { drawerState.close() }
                    }

                    DrawerItem(
                        icon = Icons.Default.Lock,
                        label = "Đổi mật khẩu",
                        selected = currentRoute == "change_password"
                    ) {
                        bottomNavController.navigate("change_password")
                        scope.launch { drawerState.close() }
                    }

                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                }

                Spacer(Modifier.weight(1f))

                DrawerItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = "Đăng xuất",
                    selected = false,
                    color = MaterialTheme.colorScheme.error
                ) {
                    onLogout()
                    scope.launch { drawerState.close() }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            if (isAdmin) "Quản trị MATestudy" else "MATestudy",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Notifications */ }) {
                            Icon(Icons.Default.NotificationsNone, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    if (isAdmin) {
                        val adminItems = listOf(
                            BottomNavItem.AdminForum,
                            BottomNavItem.AdminStudyData,
                            BottomNavItem.AdminRating,
                            BottomNavItem.AdminUsers
                        )
                        adminItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                                selected = selected,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryPink,
                                    indicatorColor = Color(0xFFFFEBF2)
                                ),
                                onClick = {
                                    bottomNavController.navigate(item.route) {
                                        popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    } else {
                        val userItems = listOf(
                            BottomNavItem.Home,
                            BottomNavItem.Schedule,
                            BottomNavItem.Rating,
                            BottomNavItem.Group,
                            BottomNavItem.Notification,
                            BottomNavItem.Profile
                        )
                        userItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                                selected = selected,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryPink,
                                    indicatorColor = Color(0xFFFFEBF2)
                                ),
                                onClick = {
                                    bottomNavController.navigate(item.route) {
                                        popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            content(padding)
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    color: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = FontWeight.Medium) },
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                icon,
                contentDescription = null,
                tint = if (color != Color.Unspecified) color
                else if (selected) PrimaryPink else Color.Gray
            )
        },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = Color(0xFFFFEBF2),
            selectedTextColor = PrimaryPink
        )
    )}