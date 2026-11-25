package com.works.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.works.app.R
import com.works.app.utils.ImageLoader

/**
 * Ürün detay sayfasındaki büyük görsel slider'ı için basit adapter.
 */
class SliderImageAdapter : RecyclerView.Adapter<SliderImageAdapter.ImageViewHolder>() {

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
