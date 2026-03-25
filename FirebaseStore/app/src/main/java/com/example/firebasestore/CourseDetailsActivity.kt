package com.example.firebasestore

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebasestore.ui.theme.FirebaseStoreTheme
import com.google.firebase.firestore.FirebaseFirestore

class CourseDetailsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseStoreTheme {
                val courseList = remember { mutableStateListOf<Course?>() }
                val db = FirebaseFirestore.getInstance()

                db.collection("Courses").get().addOnSuccessListener { queryDocumentSnapshots ->
                    if (!queryDocumentSnapshots.isEmpty) {
                        val list = queryDocumentSnapshots.documents
                        courseList.clear()
                        for (d in list) {
                            val c = d.toObject(Course::class.java)
                            courseList.add(c)
                        }
                    } else {
                        Toast.makeText(this, "No data found in Database", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Fail to get the data.", Toast.LENGTH_SHORT).show()
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50)),
                                title = { Text("GFG", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.White) }
                            )
                        }
                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            FirebaseUI(LocalContext.current, courseList)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FirebaseUI(context: Context, courseList: SnapshotStateList<Course?>) {
    Column(
        modifier = Modifier.fillMaxHeight().fillMaxWidth().background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            itemsIndexed(courseList) { _, item ->
                Card(
                    onClick = {
                        val i = Intent(context, UpdateCourse::class.java)
                        i.putExtra("courseName", item?.courseName)
                        i.putExtra("courseDuration", item?.courseDuration)
                        i.putExtra("courseDescription", item?.courseDescription)
                        i.putExtra("courseID", item?.courseID)
                        context.startActivity(i)
                    },
                    modifier = Modifier.padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                        Text(text = item?.courseName ?: "", color = Color(0xFF4CAF50), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = item?.courseDuration ?: "", color = Color.Black, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = item?.courseDescription ?: "", color = Color.Black, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}