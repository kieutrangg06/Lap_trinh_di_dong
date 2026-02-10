package com.example.todolistapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(navController: NavController, taskId: Int) {
    val viewModel: TaskViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    var task by remember { mutableStateOf(Task(id = taskId, title = "", description = "")) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        val tasks = viewModel.allTasks.first()
        task = tasks.find { it.id == taskId } ?: task
        title = task.title
        description = task.description
        isCompleted = task.isCompleted
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Edit Task") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isCompleted, onCheckedChange = { isCompleted = it })
                Text("Completed")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = {
                    coroutineScope.launch {
                        viewModel.updateTask(task.copy(title = title, description = description, isCompleted = isCompleted))
                        navController.popBackStack()
                    }
                }) {
                    Text("Update")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        viewModel.deleteTask(task)
                        navController.popBackStack()
                    }
                }) {
                    Text("Delete")
                }
            }
        }
    }
}