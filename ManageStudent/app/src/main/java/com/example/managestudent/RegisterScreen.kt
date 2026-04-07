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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("student") }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val realtimeDb = Firebase.database.reference
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner
        Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            Image(
                painter = painterResource(id = R.drawable.baner),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
            )
        }

        // Tiêu đề
        Text(
            text = buildAnnotatedString {
                append("Tham gia ")
                withStyle(SpanStyle(color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)) {
                    append("QUẢN LÝ SINH VIÊN")
                }
            },
            modifier = Modifier.padding(top = 20.dp),
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(20.dp))

        // Chọn vai trò
        Text("Vai trò:", color = Color(0xFFB71C1C), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Button(
                onClick = { selectedRole = "admin" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == "admin") Color(0xFFB71C1C) else Color.LightGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Admin", color = if (selectedRole == "admin") Color.White else Color.Black)
            }
            Button(
                onClick = { selectedRole = "student" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == "student") Color(0xFFB71C1C) else Color.LightGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Sinh viên", color = if (selectedRole == "student") Color.White else Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Nút Đăng ký
        Button(
            onClick = {
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (password.length < 6) {
                    Toast.makeText(context, "Mật khẩu phải ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val emailForAuth = "$username@managestudent.com"

                // Kiểm tra username đã tồn tại
                db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            Toast.makeText(context, "Username đã tồn tại!", Toast.LENGTH_LONG).show()
                            return@addOnSuccessListener
                        }

                        // Tạo tài khoản trên Firebase Authentication
                        auth.createUserWithEmailAndPassword(emailForAuth, password)
                            .addOnSuccessListener { authResult ->
                                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                                val userMap = hashMapOf(
                                    "username" to username,
                                    "password" to password,
                                    "role" to selectedRole,
                                    "imageUrl" to ""
                                )

                                // Lưu vào Firestore
                                db.collection("users").document(userId).set(userMap)
                                    .addOnSuccessListener {
                                        // Đồng bộ dữ liệu lên Realtime Database
                                        val realtimeRef = realtimeDb.child("users").child(userId)
                                        realtimeRef.setValue(userMap)

                                        Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

                                        // Điều hướng theo vai trò
                                        if (selectedRole == "admin") {
                                            navController.navigate("student_list") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Lưu dữ liệu thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Đăng ký lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Kiểm tra username thất bại", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth(0.85f).height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text("Tạo Tài Khoản", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Chuyển đến màn hình đăng nhập
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Đã có tài khoản? Đăng nhập", color = Color.Black)
        }
    }
}