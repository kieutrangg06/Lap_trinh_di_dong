package com.example.createrealtime

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val database = Firebase.database
    val myRef = database.getReference("message")

    var text by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(16.dp) // dùng modifier từ ngoài
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter your data") }
        )

        Button(
            onClick = { myRef.setValue(text) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Submit")
        }
    }
}