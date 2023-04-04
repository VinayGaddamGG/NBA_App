package com.example.nbaapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.example.nbaapp.R
import kotlinx.coroutines.runBlocking


class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_dialog)
    }

    fun navBack(view: View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }



    fun addUser(view: View) = runBlocking {




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
        val usernameDB = "Username"
        val passwordDB = "Password"
        val userEditText = findViewById<EditText>(R.id.username_edittext)
        val passwordEditText = findViewById<EditText>(R.id.password_edittext)
        val passwordConfirmEditText = findViewById<EditText>(R.id.password_confirm)

        val username = userEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = passwordConfirmEditText.text.toString()

        var count = 0;
        if (password.isNotBlank() && username.isNotBlank() && confirmPassword.isNotBlank()){
            count = data.scanForUsername(ddb, tablename, key, username, "#a")
        } else {
            Toast.makeText(applicationContext, "Empty fields!" , Toast.LENGTH_SHORT).show()
            return@runBlocking
        }



        if (password != confirmPassword){
            Toast.makeText(applicationContext, "Passwords do not match" , Toast.LENGTH_SHORT).show()
        } else if (count > 0){
            Toast.makeText(applicationContext, "Username exists" , Toast.LENGTH_SHORT).show()
        } else {
            data.addUser1(
                ddb,
                tablename,
                key,
                username,
                usernameDB,
                username,
                passwordDB,
                password
            )
            Toast.makeText(applicationContext, "Username Entered!" , Toast.LENGTH_SHORT).show()
            navBack(view)

        }





    }


}