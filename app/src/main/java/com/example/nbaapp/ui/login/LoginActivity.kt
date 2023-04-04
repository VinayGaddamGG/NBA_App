package com.example.nbaapp.ui.login


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.example.nbaapp.R
import com.example.nbaapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.runBlocking
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading




        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful

        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }


        }
    }



    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }


    /**
     * Extension function to simplify setting an afterTextChanged action to EditText components.
     */
    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    fun validCredentials(view: View) = runBlocking{

        val data = Database()

        val staticCredentials = StaticCredentialsProvider {
            accessKeyId = "AKIAWKMHHQ5B4WNHUC4N"
            secretAccessKey = "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"
        }

        val ddb = DynamoDbClient {
            region = "us-east-1"
            credentialsProvider = staticCredentials
        }

        val tablename = "userDB"
        val key = "userKey"
        val userNameText = findViewById<EditText>(R.id.username)
        val passwordText = findViewById<EditText>(R.id.password)

        val username = userNameText.text.toString()
        val password = passwordText.text.toString()

        if (password.isBlank() || username.isBlank()){
            Toast.makeText(applicationContext, "Empty fields!" , Toast.LENGTH_SHORT).show()
        }

        val exists = data.getItem(
            ddb,
            tablename,
            key,
            username,
            password
        )
        if (!exists){
            Toast.makeText(applicationContext, "Invalid Credentials, try again!" , Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Welcome $username", Toast.LENGTH_SHORT).show()
            navTo(view)

        }









        


    }





    fun navPage(view: View) {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }

    fun navTo(view: View){
        val intent = Intent(this, NBAPage::class.java)
        startActivity(intent)
    }







}
