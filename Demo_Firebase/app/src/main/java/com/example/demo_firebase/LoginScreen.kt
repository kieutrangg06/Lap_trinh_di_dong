package com.example.demo_firebase

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

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. Banner Banner bo góc dưới ---
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

        // --- 2. Tiêu đề ---
        Text(
            text = buildAnnotatedString {
                append("Welcome Back to ")
                withStyle(SpanStyle(color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)) {
                    append("PIZZERIA!")
                }
            },
            modifier = Modifier.padding(top = 25.dp),
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

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

        // --- 5. Nút Sign In ---
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                        navController.navigate("home")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.85f).height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
            shape = RoundedCornerShape(15.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text("Sign In", fontSize = 18.sp, color = Color.White)
        }

        // --- 6. Social Login Section ---
        Spacer(modifier = Modifier.height(20.dp))
        Text("Or Sign In with", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SocialIconButton(iconRes = R.drawable.google) { /* Google Login */ }
            SocialIconButton(iconRes = R.drawable.facebook) { /* Facebook Login */ }
        }

        // --- 7. Chuyển sang Register ---
        TextButton(onClick = { navController.navigate("register") }) {
            Text("New here? Create an account", color = Color.Black)
        }
    }
}