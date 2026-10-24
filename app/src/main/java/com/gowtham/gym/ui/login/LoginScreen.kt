package com.gowtham.gym.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowtham.gym.ThemeColors
import java.security.MessageDigest

private const val ACCESS_SALT = "gym_offline_gate_v1:"
private const val ACCESS_PASSWORD_SHA256 = "0cbf1f0817169d0cd6e32fe8de7db1ef8daeaf576920363abafd7c3da143dd99"

private fun verifyAccessPassword(input: String): Boolean {
    val digest = MessageDigest.getInstance("SHA-256")
        .digest((ACCESS_SALT + input).toByteArray(Charsets.UTF_8))
    val hex = digest.joinToString("") { "%02x".format(it) }
    return hex == ACCESS_PASSWORD_SHA256
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ThemeColors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Gym",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in with your email and access password",
                fontSize = 15.sp,
                color = ThemeColors.textSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = ThemeColors.accent,
                    unfocusedBorderColor = ThemeColors.borderStrong,
                    focusedLabelColor = ThemeColors.accent,
                    unfocusedLabelColor = ThemeColors.textSecondary,
                    cursorColor = ThemeColors.accent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text("Access Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = ThemeColors.accent,
                    unfocusedBorderColor = ThemeColors.borderStrong,
                    focusedLabelColor = ThemeColors.accent,
                    unfocusedLabelColor = ThemeColors.textSecondary,
                    cursorColor = ThemeColors.accent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val trimmedEmail = email.trim()
                    when {
                        trimmedEmail.isBlank() || !trimmedEmail.contains("@") -> {
                            errorMessage = "Please enter a valid email address."
                        }
                        !verifyAccessPassword(password) -> {
                            errorMessage = "Invalid access password."
                        }
                        else -> {
                            onLoginSuccess(trimmedEmail)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeColors.accent,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
