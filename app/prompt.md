Role: Sen bir androd kotlin yazılım uzmanısın, e - ticaret üzerine uygulamalar yazıyor.

Bağlam: aşağıdaki versionlara uygun bir şekilde kodlamayı yap.
[versions]
agp = "9.5.2"
coreKtx = "1.19.1"
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
appcompat = "1.8.0"
kotlinxCoroutinesAndroid = "1.10.2"
material = "1.14.0"
activityKtx = "1.13.0"
constraintlayout = "2.2.2"
okhttp = "4.12.0"
retrofit = "3.0.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
converter-gson = { module = "com.squareup.retrofit2:converter-gson", version.ref = "retrofit" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
kotlinx-coroutines-android = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "kotlinxCoroutinesAndroid" }
logging-interceptor = { module = "com.squareup.okhttp3:logging-interceptor", version.ref = "okhttp" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-activity-ktx = { group = "androidx.activity", name = "activity-ktx", version.ref = "activityKtx" }
androidx-constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }
okhttp = { module = "com.squareup.okhttp3:okhttp", version.ref = "okhttp" }
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }


package com.works.app.utils

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.works.app.BuildConfig

object RetrofitClient {

    private const val BASE_URL = "https://jsonbulut.com/api/"

    private lateinit var appContext: Context

    // Uygulama Context'ini bir kez tanımlıyoruz.
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(appContext))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val apiService: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

package com.works.app.pages

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.works.app.R

class ProductActivity : AppCompatActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
enableEdgeToEdge()
setContentView(R.layout.activity_product)
ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
insets
}
}
}


package com.works.app.services

import com.works.app.models.ProductReponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductService {

    // products?page=1&per_page=10
    @GET("products")
    fun allProducts(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 10
    ) : Call<ProductReponse>

}


package com.works.app.models

data class ProductReponse (
val meta: ProductMeta,
val data: List<ProductModel>
)

data class ProductModel (
val id: Long,
val title: String,
val description: String,
val category: String,
val price: Double,
val discountPercentage: Double,
val rating: Double,
val stock: Long,
val tags: List<String>,
val brand: String,
val sku: String,
val minimumOrderQuantity: Long,
val images: List<String>
)


data class ProductMeta (
val status: Long,
val message: String,
val pagination: Pagination
)

data class Pagination (
val page: Long,
val per_page: Long,
val total_items: Long,
val total_pages: Long
)

Görev: 
- ProductActivity.kt içerisinde bir RecyclerView ile cardview itemlarını kullanarak bir ürün gösterim listesi hazırla, 
- kullanıcı aşağıda doğru(bottom) doğru geldikçe diğer 10 ürün yüklenecek şekilde olsun.
- ön yükleme gösterimi(progressbar) yükleyici olarak görünsün
- her resim cache ile cachlensin
- flat bir tasarım uygula


Kısıt:
- Yukarıda anlattıklarım dışında birşey yapma.

Format:
- Flat beyaz ağırlıklı bir tasarım  yap
- her ürün item içeriği şöyle olsun:
  - Solda resim
    - her resim için ince bir border uygula
    - ürünün resim sayısı kadar resimleri cardView içinde slider olacak şekilde kullanıcı sağa - sola kaydırma yaparak resimleri görebilmeyi uygun hale getir.
  - sağda:
    - ürün başlığı
    - fiyat
  - yükseklik ideal boyuta göre olsun
  - her sayfa içinde max 5 ürün kadar ürün görünsün
  - ilk yüklemde 10 ürün yüklensin
- mavi ve turuncu renkleri kendine baz al
- responsive bir tasarım uygulama
- modern bir tasarım yap
- 