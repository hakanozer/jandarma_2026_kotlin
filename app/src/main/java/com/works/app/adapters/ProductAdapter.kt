package com.works.app.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.works.app.R
import com.works.app.models.ProductModel
import com.works.app.utils.ImageLoader
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val items = mutableListOf<ProductModel>()

    fun submitProducts(products: List<ProductModel>) {
        items.clear()
        items.addAll(products)
        notifyDataSetChanged()
    }

    fun appendProducts(products: List<ProductModel>) {
        if (products.isEmpty()) return
        val start = items.size
        items.addAll(products)
        notifyItemRangeInserted(start, products.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)

        applyItemHeight(view, parent)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    /**
     * Responsive yükseklik: ekranda tam olarak 5 ürün görünecek şekilde hesaplanır.
     */
    private fun applyItemHeight(view: View, parent: ViewGroup) {
        val recyclerView = parent as? RecyclerView ?: return
        if (recyclerView.height <= 0) return

        val params = view.layoutParams as RecyclerView.LayoutParams
        val available = recyclerView.height - recyclerView.paddingTop - recyclerView.paddingBottom
        val target = available / MAX_VISIBLE_ITEMS - params.topMargin - params.bottomMargin
        params.height = max(target, view.dp(112))
        view.layoutParams = params
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val rvImages: RecyclerView = itemView.findViewById(R.id.rvImages)
        private val layoutDots: LinearLayout = itemView.findViewById(R.id.layoutDots)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        private val txtOldPrice: TextView = itemView.findViewById(R.id.txtOldPrice)
        private val txtDiscount: TextView = itemView.findViewById(R.id.txtDiscount)

        private val imageAdapter = ProductImageAdapter()
        private val snapHelper = PagerSnapHelper()
        private var dotCount = 0

        init {
            rvImages.layoutManager = LinearLayoutManager(
                itemView.context,
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

        fun bind(product: ProductModel) {
            txtTitle.text = product.title
            txtPrice.text = formatPrice(product.price)

            if (product.discountPercentage > 0) {
                val originalPrice = product.price / (1 - product.discountPercentage / 100.0)
                txtOldPrice.text = formatPrice(originalPrice)
                txtOldPrice.paintFlags =
                    txtOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                txtOldPrice.visibility = View.VISIBLE

                txtDiscount.text = "-${product.discountPercentage.roundToInt()}%"
                txtDiscount.visibility = View.VISIBLE
            } else {
                txtOldPrice.visibility = View.GONE
                txtDiscount.visibility = View.GONE
            }

            imageAdapter.submitImages(product.images)
            rvImages.scrollToPosition(0)
            buildDots(product.images.size)
        }

        private fun buildDots(count: Int) {
            layoutDots.removeAllViews()
            dotCount = count
            layoutDots.visibility = if (count > 1) View.VISIBLE else View.GONE
            if (count <= 1) return

            val dotSize = itemView.dp(5)
            val dotMargin = itemView.dp(3)
            repeat(count) { index ->
                val dot = View(itemView.context)
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
    }

    private class ProductImageAdapter : RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {

        private val images = mutableListOf<String>()

        fun submitImages(list: List<String>) {
            images.clear()
            images.addAll(list)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product_image, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            ImageLoader.load(holder.image, images[position])
        }

        override fun getItemCount(): Int = images.size

        class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView = view.findViewById(R.id.imgProduct)
        }
    }

    companion object {
        private const val MAX_VISIBLE_ITEMS = 5
    }
}

private fun formatPrice(price: Double): String = String.format(Locale.US, "$%.2f", price)

private fun View.dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
