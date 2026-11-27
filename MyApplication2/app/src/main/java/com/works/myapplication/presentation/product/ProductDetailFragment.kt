package com.works.myapplication.presentation.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.google.gson.Gson
import com.works.myapplication.R
import com.works.myapplication.databinding.FragmentProductDetailBinding
import com.works.myapplication.domain.model.Product
import com.works.myapplication.utils.formatCurrency
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var gson: Gson

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productJson = arguments?.getString("productJson")
        if (!productJson.isNullOrEmpty()) {
            try {
                val product = gson.fromJson(productJson, Product::class.java)
                bindProductDetails(product)
            } catch (e: Exception) {
                // Ignore parse error
            }
        }
    }

    private fun bindProductDetails(product: Product) {
        binding.tvDetailTitle.text = product.title
        binding.tvDetailBrandCategory.text = "Marka: ${product.brand} | Kategori: ${product.category}"
        binding.tvDetailPrice.text = product.price.formatCurrency()

        if (product.discountPercentage > 0) {
            binding.tvDetailDiscount.text = "%${product.discountPercentage.toInt()} İndirim"
        } else {
            binding.tvDetailDiscount.text = "İndirim Yok"
        }

        binding.tvDetailRating.text = "${product.rating} / 5.0"

        if (product.stock > 0) {
            binding.tvDetailStock.text = "Stokta Var (${product.stock} Adet)"
            binding.tvDetailStock.setTextColor(requireContext().getColor(R.color.md_theme_success))
        } else {
            binding.tvDetailStock.text = "Stokta Yok"
            binding.tvDetailStock.setTextColor(requireContext().getColor(R.color.md_theme_error))
        }

        binding.tvDetailDescription.text = product.description.ifEmpty { "Açıklama bulunmuyor." }
        binding.tvDetailSku.text = "SKU Kodu: ${product.sku.ifEmpty { "Belirtilmemiş" }}"
        binding.tvDetailMinOrder.text = "Minimum Sipariş Miktarı: ${product.minimumOrderQuantity} Adet"

        val imageUrl = product.images.firstOrNull() ?: ""
        binding.ivDetailImage.load(imageUrl) {
            placeholder(R.drawable.placeholder)
            error(R.drawable.placeholder)
            crossfade(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}