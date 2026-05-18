package com.example.matestudy.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.matestudy.data.remote.FirestoreDataSource
import com.example.matestudy.data.repository.*
import com.example.matestudy.ui.screen.*
import com.example.matestudy.ui.viewmodel.*

@Composable
fun AppNavGraph(authViewModel: AuthViewModel) {
    val rootNavController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        MainAppScreen(rootNavController, authViewModel)
    } else {
        AuthNavGraph(rootNavController, authViewModel)
    }
}

@Composable
private fun AuthNavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("register") },
                viewModel = authViewModel
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("login") },
                onLoginClick = { navController.navigate("login") },
                viewModel = authViewModel
            )
        }
        composable("main") {
            MainAppScreen(navController, authViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppScreen(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel
) {
    val bottomNavController = rememberNavController()
    val firestoreDataSource = remember { FirestoreDataSource() }

    val thongBaoRepo = remember { ThongBaoRepository(firestoreDataSource) }
    val authRepo = remember { AuthRepository(firestoreDataSource) }
    val forumRepo = remember { ForumRepository(firestoreDataSource, thongBaoRepo) }
    val reviewRepo = remember { ReviewRepository(firestoreDataSource, thongBaoRepo) }
    val scheduleRepo = remember { ScheduleRepository(firestoreDataSource) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.vaiTro == "admin"

    // ✅ FIX: DÙNG CHUNG 1 VIEWMODEL
    val scheduleViewModel: ScheduleViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScheduleViewModel(scheduleRepo, authRepo) as T
            }
        }
    )

    val forumViewModel: ForumViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ForumViewModel(forumRepo, authRepo) as T
            }
        }
    )

    ScaffoldWithBottomBarAndDrawer(
        bottomNavController = bottomNavController,
        authViewModel = authViewModel,
        onLogout = {
            authViewModel.logout()
            rootNavController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = bottomNavController,
            startDestination = if (isAdmin) BottomNavItem.AdminForum.route else BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // ───────── COMMON ─────────

            composable(
                "post_detail/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.LongType })
            ) {
                val postId = it.arguments?.getLong("postId") ?: 0L
                PostDetailScreen(
                    viewModel = forumViewModel,
                    postId = postId,
                    onBack = { bottomNavController.popBackStack() }
                )
            }

            composable(
                "review_detail/{monHocId}",
                arguments = listOf(navArgument("monHocId") { type = NavType.LongType })
            ) {
                val monHocId = it.arguments?.getLong("monHocId") ?: 0L
                val vm: ReviewViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        ReviewViewModel(reviewRepo, authRepo, scheduleRepo) as T
                })
                ReviewDetailScreen(
                    viewModel = vm,
                    monHocId = monHocId,
                    onBack = { bottomNavController.popBackStack() }
                )
            }

            // ───────── USER ─────────

            if (!isAdmin) {

                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        forumViewModel = forumViewModel,
                        onNavigateToDetail = { bottomNavController.navigate("post_detail/$it") },
                        onNavigateToNewPost = { bottomNavController.navigate("new_post") }
                    )
                }

                composable("new_post") {
                    NewPostScreen(
                        viewModel = forumViewModel,
                        onBack = { bottomNavController.popBackStack() },
                        onPostSuccess = { bottomNavController.popBackStack() }
                    )
                }

                composable(BottomNavItem.Notification.route) {
                    val vm: ThongBaoViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ThongBaoViewModel(thongBaoRepo, authRepo) as T
                    })
                    NotificationScreen(
                        viewModel = vm,
                        onNavigateToPost = { bottomNavController.navigate("post_detail/$it") },
                        onNavigateToReview = { bottomNavController.navigate("review_detail/$it") },
                        onBack = { bottomNavController.popBackStack() }
                    )
                }

                composable(BottomNavItem.Rating.route) {
                    val vm: ReviewViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ReviewViewModel(reviewRepo, authRepo, scheduleRepo) as T
                    })
                    RatingScreen(
                        viewModel = vm,
                        onNavigateToAddReview = { bottomNavController.navigate("add_review") }
                    )
                }

                composable("add_review") {
                    val vm: ReviewViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ReviewViewModel(reviewRepo, authRepo, scheduleRepo) as T
                    })
                    AddReviewScreen(
                        viewModel = vm,
                        onBack = { bottomNavController.popBackStack() },
                        onReviewSubmitted = { bottomNavController.popBackStack() }
                    )
                }

                // ✅ SCHEDULE FIX
                composable(BottomNavItem.Schedule.route) {
                    ScheduleScreen(scheduleViewModel, bottomNavController)
                }

                composable("choose_class") {
                    ChooseClassScreen(
                        viewModel = scheduleViewModel,
                        onBack = { bottomNavController.popBackStack() }
                    )
                }

                composable("add_personal_event") {
                    AddEventScreen(
                        viewModel = scheduleViewModel,
                        onBack = { bottomNavController.popBackStack() },
                        onSuccess = { bottomNavController.popBackStack() }
                    )
                }

                composable("edit_event") {
                    val eventToEdit by scheduleViewModel.eventToEdit.collectAsState()

                    AddEventScreen(
                        viewModel = scheduleViewModel,
                        eventToEdit = eventToEdit,
                        onBack = { bottomNavController.popBackStack() },
                        onSuccess = {
                            scheduleViewModel.clearEventToEdit()
                            bottomNavController.popBackStack()
                        }
                    )
                }

                composable(BottomNavItem.Profile.route) {
                    ProfileScreen(
                        onChangePasswordClick = { bottomNavController.navigate("change_password") },
                        viewModel = authViewModel
                    )
                }

                composable("change_password") {
                    ChangePasswordScreen(
                        onBack = { bottomNavController.popBackStack() },
                        viewModel = authViewModel
                    )
                }
            }

            // ───────── ADMIN ─────────

            if (isAdmin) {

                composable(BottomNavItem.AdminForum.route) {
                    val vm: AdminForumViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminForumViewModel(forumRepo) as T
                            }
                        }
                    )
                    AdminForumScreen(vm)
                }

                composable(BottomNavItem.AdminStudyData.route) {
                    val vm: AdminViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminViewModel(scheduleRepo) as T
                            }
                        }
                    )
                    AdminManageScreen(vm)
                }

                composable(BottomNavItem.AdminRating.route) {
                    val vm: AdminReviewViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminReviewViewModel(reviewRepo, scheduleRepo) as T
                            }
                        }
                    )
                    AdminRatingScreen(vm)
                }

                composable(BottomNavItem.AdminUsers.route) {
                    val vm: AdminUserViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminUserViewModel(authRepo) as T
                            }
                        }
                    )
                    AdminUsersScreen(vm)
                }
            }
        }
    }
}