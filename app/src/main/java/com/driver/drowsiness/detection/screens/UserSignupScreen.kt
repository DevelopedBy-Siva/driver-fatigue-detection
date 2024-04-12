package com.driver.drowsiness.detection.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.driver.drowsiness.detection.R
import com.driver.drowsiness.detection.components.InputField
import com.driver.drowsiness.detection.constants.Routes
import com.driver.drowsiness.detection.models.SignUpCredentials
import com.driver.drowsiness.detection.models.UserDetails
import com.driver.drowsiness.detection.services.CloudServer
import com.driver.drowsiness.detection.ui.theme.DarkColor
import com.driver.drowsiness.detection.ui.theme.poppinsFontFamily
import com.driver.drowsiness.detection.utils.storeCredentials
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun UserSignupScreen(navController: NavController) {

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<Boolean>(false) }
    var passwordError by remember { mutableStateOf<Boolean>(false) }
    var nameError by remember { mutableStateOf<Boolean>(false) }
    var confirmPasswordError by remember { mutableStateOf<Boolean>(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 8
    }

    fun validateName(name: String): Boolean {
        return name.length > 3
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Icon(painter = painterResource(id = R.drawable.goback),
            contentDescription = "",
            tint = DarkColor,
            modifier = Modifier
                .size(40.dp)
                .padding(10.dp)
                .clickable {
                    navController.navigate(Routes.USER_SIGNIN_SCREEN)
                })
        Text(
            text = "Almost There !", style = TextStyle(
                fontSize = 32.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = DarkColor
            ), modifier = Modifier.padding(start = 10.dp, top = 50.dp)
        )
        Text(
            text = "We are excited to see you here...!", style = TextStyle(
                fontSize = 16.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                color = DarkColor
            ), modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 20.dp)
        )

        InputField(
            value = name,
            onValueChange = {
                name = it
                nameError = !validateName(name)
            },
            icon = Icons.Default.Person,
            hint = "Name",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardType = KeyboardType.Text
        )
        if (nameError) {
            Text(
                text = "Name must be at least 4 characters", style = TextStyle(
                    color = Color.Red,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Normal
                ), modifier = Modifier.padding(horizontal = 16.dp, vertical = 1.dp)
            )
        }

        // Email input field with validation error
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
                text = "Invalid Email", style = TextStyle(
                    color = Color.Red,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Normal
                ), modifier = Modifier.padding(horizontal = 16.dp, vertical = 1.dp)
            )
        }

        // Password input field with validation error
        InputField(
            hide = true,
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
                text = "Password must be at least 8 characters", style = TextStyle(
                    color = Color.Red,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Normal
                ), modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // Confirm Password input field with validation error
        InputField(
            hide = true,
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = it != password
            },
            icon = Icons.Default.Lock,
            hint = "Confirm Password",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardType = KeyboardType.Password
        )

        if (confirmPasswordError) {
            Text(
                text = "Passwords do not match", style = TextStyle(
                    color = Color.Red,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Normal
                ), modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (validateEmail(email) && validatePassword(password) && validateName(name) && password == confirmPassword) {
                    isLoading = true
                    signInUser(name, email, password, onSuccess = { it ->
                        storeCredentials(context, email, password, name = it.name)
                        navController.navigate(Routes.HOME_SCREEN)
                        isLoading = false
                    }, onError = { msg ->
                        msg?.let {
                            errorMessage = it
                        }
                        isLoading = false
                    })
                } else {
                    emailError = !validateEmail(email)
                    passwordError = !validatePassword(password)
                    nameError = !validateName(name)
                    confirmPasswordError = password != confirmPassword
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DarkColor),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            val buttonText = if (isLoading) {
                "Please wait..."
            } else {
                "SIGN UP"
            }
            Text(
                text = buttonText,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }

        errorMessage?.let { message ->
            Text(
                text = message, style = TextStyle(
                    color = Color.Red,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                ), modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }
    }
}

@Preview
@Composable
fun UserSignupScreenPreview() {
    UserSignupScreen(rememberNavController())
}

fun signInUser(
    name: String,
    email: String,
    password: String,
    onSuccess: (UserDetails) -> Unit,
    onError: (String?) -> Unit
) {
    val signUpCredentials = SignUpCredentials(name = name, email = email, password = password)
    println(signUpCredentials)
    val call = CloudServer.apiService.signUp(signUpCredentials)

    call.enqueue(object : Callback<UserDetails> {
        override fun onResponse(call: Call<UserDetails>, response: Response<UserDetails>) {
            if (response.isSuccessful) {
                val userDetails = response.body()
                if (userDetails != null) {
                    onSuccess(userDetails)
                }
            } else {
                val errorMessage = try {
                    val errorJson =
                        JsonParser.parseString(response.errorBody()?.string()).asJsonObject
                    errorJson.get("error").asString
                } catch (e: Exception) {
                    "Unknown Server Error. Try again later."
                }
                onError(errorMessage)
            }
        }

        override fun onFailure(call: Call<UserDetails>, t: Throwable) {
            t.printStackTrace()
            onError("Unknown Server Error. Try again later.")
        }
    })
}