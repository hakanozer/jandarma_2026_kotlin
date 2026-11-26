package com.works.app.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.works.app.R
import com.works.app.SplahActivity
import com.works.app.adapters.ProductAdapter
import com.works.app.models.ProductReponse
import com.works.app.services.Authentication
import com.works.app.services.ProductService
import com.works.app.utils.ImageLoader
import com.works.app.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductActivity : AppCompatActivity() {

    private lateinit var rvProducts: RecyclerView
    private lateinit var pbInitial: ProgressBar
    private lateinit var pbMore: ProgressBar
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var productAdapter: ProductAdapter

    private val service: ProductService by lazy {
        RetrofitClient.apiService.create(ProductService::class.java)
    }

    private var currentPage = 0
    private var isLoading = false
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product)

        RetrofitClient.initialize(applicationContext)
        ImageLoader.initialize(applicationContext)

        rvProducts = findViewById(R.id.rvProducts)
        pbInitial = findViewById(R.id.pbInitial)
        pbMore = findViewById(R.id.pbMore)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter()

        rvProducts.layoutManager = layoutManager
        rvProducts.adapter = productAdapter
        rvProducts.itemAnimator = null

        rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val total = productAdapter.itemCount
                if (!isLoading && !isLastPage && total > 0 && lastVisible >= total - 3) {
                    loadPage(currentPage + 1)
                }
            }
        })

        loadPage(1)
    }

    private fun loadPage(page: Int) {
        isLoading = true
        if (page == 1) {
            pbInitial.visibility = View.VISIBLE
        } else {
            pbMore.visibility = View.VISIBLE
        }

        service.allProducts(page = page, perPage = PER_PAGE).enqueue(
            object : Callback<ProductReponse> {
                override fun onResponse(
                    call: Call<ProductReponse>,
                    response: Response<ProductReponse>
                ) {
                    isLoading = false
                    hideProgress()

                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        showError()
                        return
                    }

                    if (page == 1) {
                        productAdapter.submitProducts(body.data)
                    } else {
                        productAdapter.appendProducts(body.data)
                    }

                    currentPage = page
                    if (body.meta.pagination != null) {
                        isLastPage = body.data.isEmpty() ||
                                page.toLong() >= body.meta.pagination.total_pages
                    }
                }

                override fun onFailure(call: Call<ProductReponse>, t: Throwable) {
                    isLoading = false
                    hideProgress()
                    showError()
                }
            }
        )
    }

    private fun hideProgress() {
        pbInitial.visibility = View.GONE
        pbMore.visibility = View.GONE
    }

    private fun showError() {
        Toast.makeText(
            this@ProductActivity,
            getString(R.string.products_load_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        private const val PER_PAGE = 10
    }

    // Menüyü sayfada göster
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // menüdeki itemlardan hangisine tıklanıldı
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.m_notlarim -> {
                Log.d("TAG", "m_notlarim")
            }
            R.id.m_profile -> {
                Log.d("TAG", "m_profile")
            }
            R.id.m_logout -> {
                RetrofitClient.initialize(this)
                val api = RetrofitClient.apiService.create(Authentication::class.java)
                api.logout()
                val sharedPreferences = getSharedPreferences("app", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("token")
                editor.apply()
                val i = Intent(this, SplahActivity::class.java)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
