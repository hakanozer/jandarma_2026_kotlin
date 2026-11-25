package com.works.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.widget.ImageView
import com.works.app.R
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Ürün görselleri için bellek (LruCache) + disk (file) cache kullanan basit görsel yükleyici.
 */
object ImageLoader {

    private const val MEMORY_CACHE_SIZE = 24 * 1024 * 1024 // 24 MB
    private const val DISK_CACHE_SIZE = 80L * 1024 * 1024 // 80 MB
    private const val TIMEOUT_SECONDS = 20L

    private val memoryCache = object : LruCache<String, Bitmap>(MEMORY_CACHE_SIZE) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
    }

    private val executor = Executors.newFixedThreadPool(4)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val inFlight = HashSet<String>()

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    private lateinit var appContext: Context

    fun initialize(context: Context) {
        if (::appContext.isInitialized) return
        appContext = context.applicationContext
        cacheDir().mkdirs()
        executor.execute { trimDiskCache() }
    }

    fun load(imageView: ImageView, url: String?) {
        if (url.isNullOrBlank()) {
            imageView.tag = null
            imageView.setImageResource(R.drawable.bg_image_placeholder)
            return
        }

        imageView.tag = url

        val cached = memoryCache.get(url)
        if (cached != null) {
            imageView.setImageBitmap(cached)
            return
        }

        imageView.setImageResource(R.drawable.bg_image_placeholder)

        val reqWidth = if (imageView.width > 0) imageView.width else 512
        val reqHeight = if (imageView.height > 0) imageView.height else 512

        executor.execute {
            val bitmap = readOrDownload(url, reqWidth, reqHeight)
            if (bitmap != null) {
                memoryCache.put(url, bitmap)
            }
            mainHandler.post {
                if (imageView.tag == url) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap)
                    } else {
                        imageView.setImageResource(R.drawable.bg_image_placeholder)
                    }
                }
            }
        }
    }

    private fun readOrDownload(url: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        val file = cacheFile(url)

        if (file.exists() && file.length() > 0) {
            val bitmap = decode(file, reqWidth, reqHeight)
            if (bitmap != null) return bitmap
            file.delete()
        }

        synchronized(inFlight) {
            if (inFlight.contains(url)) return null
            inFlight.add(url)
        }

        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body ?: return null
                val temp = File(cacheDir(), "${hash(url)}.tmp")
                body.byteStream().use { input ->
                    FileOutputStream(temp).use { output -> input.copyTo(output) }
                }
                if (!temp.renameTo(file)) {
                    temp.copyTo(file, overwrite = true)
                    temp.delete()
                }
            }
            return decode(file, reqWidth, reqHeight)
        } catch (e: Exception) {
            return null
        } finally {
            synchronized(inFlight) { inFlight.remove(url) }
        }
    }

    private fun decode(file: File, reqWidth: Int, reqHeight: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, reqWidth, reqHeight)
            inPreferredConfig = Bitmap.Config.RGB_565
        }
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var sampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / sampleSize) >= reqHeight && (halfWidth / sampleSize) >= reqWidth) {
                sampleSize *= 2
            }
        }
        return sampleSize
    }

    private fun cacheDir(): File = File(appContext.cacheDir, "product_images")

    private fun cacheFile(url: String): File = File(cacheDir(), hash(url))

    private fun hash(value: String): String {
        val digest = MessageDigest.getInstance("SHA-1").digest(value.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun trimDiskCache() {
        val files = cacheDir().listFiles() ?: return
        var total = files.sumOf { it.length() }
        if (total <= DISK_CACHE_SIZE) return

        files.sortedBy { it.lastModified() }.forEach { file ->
            if (total <= DISK_CACHE_SIZE) return
            total -= file.length()
            file.delete()
        }
    }
}
