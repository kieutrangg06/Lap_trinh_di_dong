package com.example.todolistapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val viewModel: TaskViewModel = viewModel()
    var tasks by remember { mutableStateOf(listOf<Task>()) }

    LaunchedEffect(Unit) {
        viewModel.allTasks.collectLatest { taskList ->
            tasks = taskList
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("ToDo List") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(tasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate("edit/${task.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(task.title, style = MaterialTheme.typography.titleMedium)
                        Text(task.description)
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { isChecked ->
                                viewModel.updateTask(task.copy(isCompleted = isChecked))
                            }
                        )
                    }
                }
            }
        }
    }
}