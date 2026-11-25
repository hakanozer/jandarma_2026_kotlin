package com.works.app.pages

import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.works.app.R
import com.works.app.adapters.SliderImageAdapter
import com.works.app.models.ProductModel
import com.works.app.models.ProductSingleReponse
import com.works.app.services.ProductService
import com.works.app.utils.ImageLoader
import com.works.app.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import kotlin.math.roundToInt

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var scrollContent: NestedScrollView
    private lateinit var pbDetail: ProgressBar
    private lateinit var rvImages: RecyclerView
    private lateinit var layoutDots: LinearLayout
    private lateinit var txtCategory: TextView
    private lateinit var txtTitle: TextView
    private lateinit var txtRating: TextView
    private lateinit var txtStock: TextView
    private lateinit var txtBrandTop: TextView
    private lateinit var txtPrice: TextView
    private lateinit var txtOldPrice: TextView
    private lateinit var txtDiscount: TextView
    private lateinit var txtDiscountBadge: TextView
    private lateinit var txtBrand: TextView
    private lateinit var txtSku: TextView
    private lateinit var txtMinOrder: TextView
    private lateinit var txtStockInfo: TextView
    private lateinit var txtDescription: TextView
    private lateinit var lblDescription: TextView
    private lateinit var lblTags: TextView
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var txtBottomPrice: TextView
    private lateinit var btnBack: ImageButton

    private val imageAdapter = SliderImageAdapter()
    private val snapHelper = PagerSnapHelper()
    private var dotCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)

        RetrofitClient.initialize(applicationContext)
        ImageLoader.initialize(applicationContext)

        bindViews()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnBack.setOnClickListener { finish() }

        setupImageSlider()

        val id = intent.getLongExtra("id", 0)
        if (id > 0) {
            loadProduct(id)
        } else {
            finish()
        }
    }

    private fun bindViews() {
        scrollContent = findViewById(R.id.scrollContent)
        pbDetail = findViewById(R.id.pbDetail)
        rvImages = findViewById(R.id.rvImages)
        layoutDots = findViewById(R.id.layoutDots)
        txtCategory = findViewById(R.id.txtCategory)
        txtTitle = findViewById(R.id.txtTitle)
        txtRating = findViewById(R.id.txtRating)
        txtStock = findViewById(R.id.txtStock)
        txtBrandTop = findViewById(R.id.txtBrandTop)
        txtPrice = findViewById(R.id.txtPrice)
        txtOldPrice = findViewById(R.id.txtOldPrice)
        txtDiscount = findViewById(R.id.txtDiscount)
        txtDiscountBadge = findViewById(R.id.txtDiscountBadge)
        txtBrand = findViewById(R.id.txtBrand)
        txtSku = findViewById(R.id.txtSku)
        txtMinOrder = findViewById(R.id.txtMinOrder)
        txtStockInfo = findViewById(R.id.txtStockInfo)
        txtDescription = findViewById(R.id.txtDescription)
        lblDescription = findViewById(R.id.lblDescription)
        lblTags = findViewById(R.id.lblTags)
        chipGroupTags = findViewById(R.id.chipGroupTags)
        txtBottomPrice = findViewById(R.id.txtBottomPrice)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupImageSlider() {
        rvImages.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rvImages.adapter = imageAdapter
        snapHelper.attachToRecyclerView(rvImages)
        rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateDots()
                }
            }
        })
    }

    private fun loadProduct(id: Long) {
        pbDetail.visibility = View.VISIBLE
        scrollContent.visibility = View.INVISIBLE

        val api = RetrofitClient.apiService.create(ProductService::class.java)
        api.getSingleProduct(id).enqueue(object : Callback<ProductSingleReponse> {
            override fun onResponse(
                call: Call<ProductSingleReponse>,
                response: Response<ProductSingleReponse>
            ) {
                pbDetail.visibility = View.GONE

                val item = response.body()?.data
                if (!response.isSuccessful || item == null) {
                    showError()
                    return
                }

                bindProduct(item)
                scrollContent.visibility = View.VISIBLE
            }

            override fun onFailure(call: Call<ProductSingleReponse>, t: Throwable) {
                pbDetail.visibility = View.GONE
                showError()
            }
        })
    }

    private fun bindProduct(product: ProductModel) {
        txtCategory.text = product.category
        txtTitle.text = product.title
        txtRating.text = "★ ${String.format(Locale.US, "%.1f", product.rating)}"

        txtStock.text = if (product.stock > 0) {
            getString(R.string.stock_in, product.stock)
        } else {
            getString(R.string.stock_out)
        }

        txtStockInfo.text = if (product.stock > 0) {
            getString(R.string.quantity_unit, product.stock)
        } else {
            getString(R.string.stock_out)
        }

        txtBrand.text = product.brand.ifBlank { "-" }
        txtBrandTop.text = product.brand
        txtBrandTop.visibility = if (product.brand.isBlank()) View.GONE else View.VISIBLE
        txtSku.text = product.sku.ifBlank { "-" }
        txtMinOrder.text = getString(R.string.quantity_unit, product.minimumOrderQuantity)

        val priceText = formatPrice(product.price)
        txtPrice.text = priceText
        txtBottomPrice.text = priceText

        if (product.discountPercentage > 0 && product.discountPercentage < 100) {
            val originalPrice = product.price / (1 - product.discountPercentage / 100.0)
            txtOldPrice.text = formatPrice(originalPrice)
            txtOldPrice.paintFlags = txtOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            txtOldPrice.visibility = View.VISIBLE

            val discountText = "-${product.discountPercentage.roundToInt()}%"
            txtDiscount.text = discountText
            txtDiscount.visibility = View.VISIBLE

            txtDiscountBadge.text = discountText
            txtDiscountBadge.visibility = View.VISIBLE
        } else {
            txtOldPrice.visibility = View.GONE
            txtDiscount.visibility = View.GONE
            txtDiscountBadge.visibility = View.GONE
        }

        if (product.description.isBlank()) {
            lblDescription.visibility = View.GONE
            txtDescription.visibility = View.GONE
        } else {
            txtDescription.text = product.description
        }

        bindTags(product.tags)

        imageAdapter.submitImages(product.images)
        rvImages.scrollToPosition(0)
        buildDots(product.images.size)
    }

    private fun bindTags(tags: List<String>) {
        chipGroupTags.removeAllViews()

        if (tags.isEmpty()) {
            lblTags.visibility = View.GONE
            chipGroupTags.visibility = View.GONE
            return
        }

        lblTags.visibility = View.VISIBLE
        chipGroupTags.visibility = View.VISIBLE

        tags.forEach { tag ->
            val chip = Chip(this).apply {
                text = tag
                isClickable = false
                isCheckable = false
                setTextColor(ContextCompat.getColor(this@ProductDetailActivity, R.color.brand_blue))
                chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(this@ProductDetailActivity, R.color.brand_blue_light)
                )
                chipStrokeWidth = 0f
                chipCornerRadius = 8 * resources.displayMetrics.density
                textSize = 12f
            }
            chipGroupTags.addView(chip)
        }
    }

    private fun buildDots(count: Int) {
        layoutDots.removeAllViews()
        dotCount = count
        layoutDots.visibility = if (count > 1) View.VISIBLE else View.GONE
        if (count <= 1) return

        val dotSize = dp(5)
        val dotMargin = dp(3)
        repeat(count) { index ->
            val dot = View(this)
            val params = LinearLayout.LayoutParams(dotSize, dotSize)
            params.marginStart = dotMargin
            params.marginEnd = dotMargin
            dot.layoutParams = params
            dot.setBackgroundResource(
                if (index == 0) R.drawable.bg_slider_dot_active
                else R.drawable.bg_slider_dot_inactive
            )
            layoutDots.addView(dot)
        }
    }

    private fun updateDots() {
        if (dotCount <= 1) return
        val layoutManager = rvImages.layoutManager as? LinearLayoutManager ?: return
        val snapView = snapHelper.findSnapView(layoutManager) ?: return
        val position = layoutManager.getPosition(snapView)
        for (index in 0 until layoutDots.childCount) {
            layoutDots.getChildAt(index).setBackgroundResource(
                if (index == position) R.drawable.bg_slider_dot_active
                else R.drawable.bg_slider_dot_inactive
            )
        }
    }

    private fun showError() {
        Toast.makeText(
            this,
            getString(R.string.product_detail_load_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun formatPrice(price: Double): String = String.format(Locale.US, "$%.2f", price)

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
