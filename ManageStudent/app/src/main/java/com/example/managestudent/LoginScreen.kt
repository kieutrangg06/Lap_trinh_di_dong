package com.example.managestudent

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner
        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            Image(
                painter = painterResource(id = R.drawable.baner),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
            )
        }

        // Tiêu đề chào mừng
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)) {
                    append("QUẢN LÝ SINH VIÊN")
                }
            },
            modifier = Modifier.padding(top = 25.dp),
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Ô nhập Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username", color = Color(0xFFB71C1C)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFB71C1C)) },
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFB71C1C))
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Ô nhập Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color(0xFFB71C1C)) },
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFB71C1C)) },
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFB71C1C))
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Nút Đăng nhập
        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    val emailForAuth = "$username@managestudent.com"
                    auth.signInWithEmailAndPassword(emailForAuth, password)
                        .addOnSuccessListener {
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(auth.currentUser!!.uid).get()
                                .addOnSuccessListener { doc ->
                                    val role = doc.getString("role") ?: "student"
                                    if (role == "admin") {
                                        navController.navigate("student_list") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Đăng nhập lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(0.85f).height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
            shape = RoundedCornerShape(15.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text("Đăng Nhập", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Chuyển đến màn hình Đăng ký
        TextButton(onClick = { navController.navigate("register") }) {
            Text("Chưa có tài khoản? Đăng ký ngay", color = Color.Black)
        }
    }
}