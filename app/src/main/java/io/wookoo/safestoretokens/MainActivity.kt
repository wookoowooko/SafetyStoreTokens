package io.wookoo.safestoretokens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.wookoo.safestoretokens.ui.theme.TestAutenticationDeliveryTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val dataStoreManager: DataStoreManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestAutenticationDeliveryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var username by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var encryptedJwt by remember { mutableStateOf("") }
                    var originalJwt by remember { mutableStateOf("") }
                    var errorMessage by remember { mutableStateOf("") }

                    // Retrieve the JWT from DataStore on launch
                    LaunchedEffect(Unit) {
                        dataStoreManager.getSettings().collect {
                            encryptedJwt = it.jwt.orEmpty() // Set encrypted JWT from DataStore
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Username") }
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Spacer


                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation() // Hide password
                        )
                        Spacer(modifier = Modifier.height(16.dp))


                        Button(onClick = {
                            try {

                                if (username.isBlank() || password.isBlank()) {
                                    errorMessage = "Username and password must not be empty."
                                    return@Button
                                }

                                // Simulate an API call to return a JWT
                                val fakeApiRepo = FakeApiRepo()
                                val jwt = fakeApiRepo.returnJwt()
                                originalJwt = jwt // Save the JWT

                                // Save the JWT to DataStore
                                CoroutineScope(Dispatchers.IO).launch {
                                    dataStoreManager.saveData(jwt)
                                }

                                errorMessage = ""
                            } catch (e: Exception) {

                                e.printStackTrace()
                                errorMessage = "An error occurred: ${e.localizedMessage}"
                            }
                        }) {
                            Text("Login")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display the encrypted JWT from DataStore
                        if (encryptedJwt.isNotBlank()) {
                            Text(
                                "Encrypted JWT from DataStore: $encryptedJwt",
                                textAlign = TextAlign.Center
                            )
                        }

                        // Display the original JWT
                        if (originalJwt.isNotBlank()) {
                            Text(
                                "Original JWT from API: $originalJwt",
                                textAlign = TextAlign.Center
                            )
                        }

                        // Compare the original and encrypted JWTs
                        val equals = originalJwt == encryptedJwt
                        if (originalJwt.isNotBlank()) {
                            Text("JWTs are equal: $equals")
                        }


                        if (errorMessage.isNotBlank()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


