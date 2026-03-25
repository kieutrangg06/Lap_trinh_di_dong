package com.example.firebasestore

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebasestore.ui.theme.FirebaseStoreTheme
import com.google.firebase.firestore.FirebaseFirestore

class UpdateCourse : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseStoreTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50)),
                                title = { Text("GFG", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.White) }
                            )
                        }
                    ) { innerPadding ->
                        FirebaseUI(
                            LocalContext.current,
                            intent.getStringExtra("courseName"),
                            intent.getStringExtra("courseDuration"),
                            intent.getStringExtra("courseDescription"),
                            intent.getStringExtra("courseID"),
                            innerPadding
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FirebaseUI(context: Context, name: String?, duration: String?, description: String?, courseID: String?, padding: PaddingValues) {
    val courseName = remember { mutableStateOf(name ?: "") }
    val courseDuration = remember { mutableStateOf(duration ?: "") }
    val courseDescription = remember { mutableStateOf(description ?: "") }

    Column(
        modifier = Modifier.padding(padding).fillMaxHeight().fillMaxWidth().background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = courseName.value,
            onValueChange = { courseName.value = it },
            placeholder = { Text("Enter your course name") },
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = courseDuration.value,
            onValueChange = { courseDuration.value = it },
            placeholder = { Text("Enter your course duration") },
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = courseDescription.value,
            onValueChange = { courseDescription.value = it },
            placeholder = { Text("Enter your course description") },
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                if (TextUtils.isEmpty(courseName.value)) {
                    Toast.makeText(context, "Please enter course name", Toast.LENGTH_SHORT).show()
                } else {
                    updateDataToFirebase(courseID, courseName.value, courseDuration.value, courseDescription.value, context)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(text = "Update Data", modifier = Modifier.padding(8.dp))
        }
    }
}

private fun updateDataToFirebase(courseID: String?, name: String?, duration: String?, description: String?, context: Context) {
    val updatedCourse = Course(courseID, name, duration, description)
    val db = FirebaseFirestore.getInstance()
    db.collection("Courses").document(courseID.toString()).set(updatedCourse)
        .addOnSuccessListener {
            Toast.makeText(context, "Course Updated successfully..", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, CourseDetailsActivity::class.java))
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Fail to update course " + e.message, Toast.LENGTH_SHORT).show()
        }
}