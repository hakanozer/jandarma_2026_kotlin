package com.works.myapplication.presentation.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.works.myapplication.R
import com.works.myapplication.databinding.ItemProductBinding
import com.works.myapplication.domain.model.Product
import com.works.myapplication.utils.formatCurrency
import com.works.myapplication.utils.hide
import com.works.myapplication.utils.show

class ProductPagingAdapter(
    private val onItemClick: (Product) -> Unit
) : PagingDataAdapter<Product, ProductPagingAdapter.ProductViewHolder>(PRODUCT_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        if (product != null) {
            holder.bind(product)
        }
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductTitle.text = product.title
            binding.tvBrandCategory.text = "${product.brand} • ${product.category}"
            binding.tvRating.text = product.rating.toString()
            binding.tvPrice.text = product.price.formatCurrency()

            if (product.discountPercentage > 0) {
                binding.tvDiscount.text = "%${product.discountPercentage.toInt()} İndirim"
                binding.tvDiscount.show()
            } else {
                binding.tvDiscount.hide()
            }

            if (product.stock > 0) {
                binding.tvStockStatus.text = "• Stokta Var (${product.stock})"
                binding.tvStockStatus.setTextColor(
                    binding.root.context.getColor(R.color.md_theme_success)
                )
            } else {
                binding.tvStockStatus.text = "• Stokta Yok"
                binding.tvStockStatus.setTextColor(
                    binding.root.context.getColor(R.color.md_theme_error)
                )
            }

            val imageUrl = product.images.firstOrNull() ?: ""
            binding.ivProductImage.load(imageUrl) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
                crossfade(true)
            }

            binding.root.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    companion object {
        private val PRODUCT_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }
}