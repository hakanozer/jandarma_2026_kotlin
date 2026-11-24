package com.works.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.works.app.action.MainKotlin
import com.works.app.models.LoginReponse
import com.works.app.models.LoginRequest
import com.works.app.pages.Register
import com.works.app.services.Authentication
import com.works.app.utils.RetrofitClient
import com.works.app.utils.Validation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var txtEmail: EditText
    lateinit var txtPassword: EditText
    lateinit var btnLogin: Button
    lateinit var btnRegister: Button

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("app", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val action = MainKotlin() // Sınıf üretimi
        action.call() // fonksiyon tetiklnmesi

        btnLogin.setOnClickListener {
            fncLogin()
        }

        btnRegister.setOnClickListener {
            fncRegister()
        }

    }

    fun fncLogin() {
        val email = txtEmail.text.toString()
        val password = txtPassword.text.toString()
        when {
            !Validation.isValidEmail(email) -> {
                Toast.makeText(this, "Geçersiz Email", Toast.LENGTH_SHORT).show()
            }
            !Validation.isValidPassword(password) -> {
                Toast.makeText(this, "Geçersiz Şifre", Toast.LENGTH_SHORT).show()
            }
            else -> {

                RetrofitClient.initialize(applicationContext)
                val api = RetrofitClient.apiService.create(Authentication::class.java)
                val request = LoginRequest(email, password)
                api.login(request).enqueue( object: Callback<LoginReponse> {
                    override fun onResponse(
                        call: Call<LoginReponse?>,
                        response: Response<LoginReponse?>
                    ) {
                        val body = response.body()
                        if (response.isSuccessful && body != null) {
                            Log.d("Durum", "Giriş Başarılı")
                            Log.d("token", body.data.access_token )
                            editor.putString("token", body.data.access_token)
                            editor.apply()
                            RetrofitClient.initialize(applicationContext)
                        }else {
                            Log.d("Durum", "Giriş Başarısız")
                        }

                    }

                    override fun onFailure(
                        call: Call<LoginReponse?>,
                        t: Throwable
                    ) {
                        Log.e("Durum", "Bir Hata oluştu!")
                    }
                })

            }
        }
    }

    fun fncRegister() {
        val intent = Intent(this, Register::class.java)
        intent.putExtra("email", txtEmail.text.toString())
        startActivity(intent)
    }
}