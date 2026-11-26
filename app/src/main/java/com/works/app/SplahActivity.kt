package com.works.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.works.app.pages.LoginActivity
import com.works.app.pages.ProductActivity
import com.works.app.services.Authentication
import com.works.app.utils.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplahActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splah)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // actionbar hide
        supportActionBar?.hide()

        progressBar = findViewById(R.id.progressBar)
        // progressBar show

        sharedPreferences = getSharedPreferences("app", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val token = sharedPreferences.getString("token", null)
        if (token == null) {
            val intent = Intent(this, LoginActivity::class.java)
            gotoPage(intent)
        }else {
            // token servise gönder
            RetrofitClient.initialize(this)
            val api = RetrofitClient.apiService.create(Authentication::class.java)
            api.profileMe().enqueue(object : Callback<Any> {
                override fun onResponse(
                    call: Call<in Any>,
                    response: Response<in Any>
                ) {
                    if (response.isSuccessful) {
                        Log.d("TAG", "jwt başarılı")
                        // token geçerli
                        val i = Intent(this@SplahActivity, ProductActivity::class.java)
                        gotoPage(i)
                    }else {
                        // token geçersiz
                        editor.remove("token")
                        editor.apply()
                        val i = Intent(this@SplahActivity, LoginActivity::class.java)
                        gotoPage(i)
                    }
                }

                override fun onFailure(call: Call<in Any>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    fun gotoPage(intent: Intent) {
        lifecycleScope.launch {
            delay(2000)
            progressBar.visibility = ProgressBar.INVISIBLE
            startActivity(intent)
        }
    }

}