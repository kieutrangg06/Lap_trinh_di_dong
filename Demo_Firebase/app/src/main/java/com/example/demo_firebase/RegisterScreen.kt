package com.example.demo_firebase

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. Banner ---
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

        // --- 2. Tiêu đề ---
        Text(
            text = buildAnnotatedString {
                append("Join the ")
                withStyle(SpanStyle(color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)) {
                    append("PIZZERIA!")
                }
            },
            modifier = Modifier.padding(top = 20.dp),
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- 3. Input Email ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color(0xFFB71C1C)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFB71C1C)) },
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFB71C1C))
        )

        Spacer(modifier = Modifier.height(15.dp))

        // --- 4. Input Password ---
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

        // --- 5. Nút Register chính ---
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    if (password.length < 6) {
                        Toast.makeText(context, "Mật khẩu tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { authResult ->
                            val userId = authResult.user?.uid
                            val userMap = hashMapOf(
                                "uid" to userId,
                                "email" to email,
                                "role" to "customer",
                                "createdAt" to com.google.firebase.Timestamp.now()
                            )
                            if (userId != null) {
                                db.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Đăng ký & Lưu dữ liệu thành công!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("home")
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Lưu CSDL lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Đăng ký lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(0.85f).height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text("Create Account", fontSize = 18.sp, color = Color.White)
        }

        // --- 6. PHẦN SOCIAL REGISTER (Bổ sung tại đây) ---
        Spacer(modifier = Modifier.height(20.dp))
        Text("Or Register with", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SocialIconButton(iconRes = R.drawable.google) {
                /* Xử lý đăng ký Google sau này */
                Toast.makeText(context, "Đang phát triển Google Login", Toast.LENGTH_SHORT).show()
            }
            SocialIconButton(iconRes = R.drawable.facebook) {
                /* Xử lý đăng ký Facebook sau này */
                Toast.makeText(context, "Đang phát triển Facebook Login", Toast.LENGTH_SHORT).show()
            }
        }

        // --- 7. Chuyển sang Login ---
        Spacer(modifier = Modifier.height(15.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Already have an account? Sign In", color = Color.Black)
        }
    }
}

@Composable
fun SocialIconButton(iconRes: Int, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(50.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}