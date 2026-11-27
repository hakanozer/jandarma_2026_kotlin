package com.works.myapplication.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.works.myapplication.domain.model.Product
import com.works.myapplication.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    val productsPagingStream: Flow<PagingData<Product>> =
        productRepository.getProductsStream()
            .cachedIn(viewModelScope)
}