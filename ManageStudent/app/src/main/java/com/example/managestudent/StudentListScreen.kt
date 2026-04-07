package com.example.managestudent

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.managestudent.ui.theme.MainRed
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val realtimeDb = Firebase.database.reference
    val context = LocalContext.current

    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }

    // Tải dữ liệu realtime từ Firestore
    LaunchedEffect(Unit) {
        db.collection("users").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(context, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            val studentList = snapshot?.documents?.map { doc ->
                Student(
                    id = doc.id,
                    username = doc.getString("username") ?: "",
                    password = doc.getString("password") ?: "",
                    role = doc.getString("role") ?: "student",
                    imageUrl = doc.getString("imageUrl")
                )
            } ?: emptyList()

            students = studentList
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản Lý Sinh Viên", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFB71C1C)),
                actions = {
                    IconButton(onClick = {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, "Đăng xuất", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFB71C1C)
            ) {
                Text("+", color = Color.White, fontSize = 28.sp)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFFFDF5))
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MainRed)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(students) { student ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = student.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, MainRed, CircleShape),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                                    error = painterResource(R.drawable.ic_launcher_foreground)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(student.username, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                                    Text("Role: ${student.role}", color = MainRed, fontSize = 15.sp)
                                    Text("Pass: ${student.password}", fontSize = 13.sp, color = Color.Gray)
                                }

                                Row {
                                    IconButton(onClick = {
                                        editingStudent = student
                                        showEditDialog = true
                                    }) {
                                        Icon(Icons.Default.Edit, null, tint = Color.Blue)
                                    }
                                    IconButton(onClick = {
                                        // Xóa trên Firestore
                                        db.collection("users").document(student.id).delete()
                                        // Xóa trên Realtime Database
                                        realtimeDb.child("users").child(student.id).removeValue()
                                    }) {
                                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog thêm sinh viên mới
    if (showAddDialog) {
        var user by remember { mutableStateOf("") }
        var pass by remember { mutableStateOf("") }
        var imgUrl by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("student") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Thêm Sinh Viên Mới") },
            text = {
                Column {
                    OutlinedTextField(value = user, onValueChange = { user = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = imgUrl, onValueChange = { imgUrl = it }, label = { Text("Link ảnh đại diện") }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("https://...") })
                    Spacer(Modifier.height(12.dp))
                    Text("Vai trò:", fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = role == "admin", onClick = { role = "admin" })
                        Text("Admin")
                        Spacer(Modifier.width(16.dp))
                        RadioButton(selected = role == "student", onClick = { role = "student" })
                        Text("Sinh viên")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (user.isNotEmpty() && pass.isNotEmpty()) {
                        val email = "$user@managestudent.com"
                        auth.createUserWithEmailAndPassword(email, pass)
                            .addOnSuccessListener { res ->
                                val userId = res.user?.uid ?: return@addOnSuccessListener

                                val data = hashMapOf<String, Any>(
                                    "username" to user,
                                    "password" to pass,
                                    "imageUrl" to imgUrl,
                                    "role" to role
                                )

                                // Lưu vào Firestore
                                db.collection("users").document(userId).set(data)
                                    .addOnSuccessListener {
                                        // Đồng bộ lên Realtime Database
                                        realtimeDb.child("users").child(userId).setValue(data)
                                        showAddDialog = false
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Lưu dữ liệu thất bại", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Tạo tài khoản thất bại: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                }) { Text("Thêm") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Hủy") } }
        )
    }

    // Dialog chỉnh sửa thông tin sinh viên
    if (showEditDialog && editingStudent != null) {
        var editUsername by remember { mutableStateOf(editingStudent!!.username) }
        var editPassword by remember { mutableStateOf(editingStudent!!.password) }
        var editImageUrl by remember { mutableStateOf(editingStudent!!.imageUrl ?: "") }
        var editRole by remember { mutableStateOf(editingStudent!!.role) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Chỉnh Sửa Thông Tin Sinh Viên") },
            text = {
                Column {
                    OutlinedTextField(value = editUsername, onValueChange = { editUsername = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = editPassword, onValueChange = { editPassword = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = editImageUrl, onValueChange = { editImageUrl = it }, label = { Text("Link ảnh đại diện") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    Text("Vai trò:", fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = editRole == "admin", onClick = { editRole = "admin" })
                        Text("Admin")
                        Spacer(Modifier.width(16.dp))
                        RadioButton(selected = editRole == "student", onClick = { editRole = "student" })
                        Text("Sinh viên")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val updates = mapOf(
                        "username" to editUsername,
                        "password" to editPassword,
                        "imageUrl" to editImageUrl,
                        "role" to editRole
                    )

                    // Cập nhật Firestore
                    db.collection("users").document(editingStudent!!.id)
                        .update(updates)
                        .addOnSuccessListener {
                            // Cập nhật Realtime Database
                            realtimeDb.child("users").child(editingStudent!!.id).updateChildren(updates)
                            showEditDialog = false
                            Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                        }
                }) { Text("Cập nhật") }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Hủy") } }
        )
    }
}