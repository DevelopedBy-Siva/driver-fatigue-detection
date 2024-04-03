package com.driver.drowsiness.detection.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.driver.drowsiness.detection.R
import com.driver.drowsiness.detection.components.InputField

@Composable
fun UserSigninScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<Boolean>(false) }
    var passwordError by remember { mutableStateOf<Boolean>(false) }

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 8
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            modifier = Modifier
                .size(100.dp)
                .padding(10.dp)
        )
        Text(
            text = "Welcome Back !",
            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(start = 10.dp, top = 50.dp)
        )
        Text(
            text = "Sign In to Continue",
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 20.dp)
        )

        InputField(
            value = email,
            onValueChange = {
                email = it
                emailError = !validateEmail(email)
            },
            icon = Icons.Default.Email,
            hint = "Email",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardType = KeyboardType.Email
        )
        if (emailError) {
            Text(
                text = "Invalid Email",
                style = TextStyle(color = Color.Red),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 1.dp)
            )
        }

        // Password input field with validation error
        InputField(
            value = password,
            onValueChange = {
                password = it
                passwordError = !validatePassword(password)
            },
            icon = Icons.Default.Lock,
            hint = "Password",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardType = KeyboardType.Password
        )

        if (passwordError) {
            Text(
                text = "Password must be at least 8 characters",
                style = TextStyle(color = Color.Red),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp, top = 30.dp, end = 10.dp)
        ) {
            Text(
                text = "Don’t have an account? ",
                style = TextStyle(fontSize = 16.sp)
            )

            Text(
                text = "Sign Up",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .clickable {
                        navController.navigate(Routes.USER_SIGNUP_SCREEN)
                    }
            )
        }

        Button(
            onClick = {
                if (validateEmail(email) && validatePassword(password)) {
                    navController.navigate(Routes.HOME_SCREEN)
                } else {
                    emailError = !validateEmail(email)
                    passwordError = !validatePassword(password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(text = "Sign In")
        }
    }
}

@Preview
@Composable
fun UserSigninScreenPreview() {
    UserSigninScreen(rememberNavController())
}
