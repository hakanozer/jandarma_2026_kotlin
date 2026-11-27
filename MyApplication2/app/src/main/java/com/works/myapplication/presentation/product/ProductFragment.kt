package com.works.myapplication.presentation.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.gson.Gson
import com.works.myapplication.R
import com.works.myapplication.databinding.FragmentProductBinding
import com.works.myapplication.domain.model.Product
import com.works.myapplication.utils.hide
import com.works.myapplication.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var adapter: ProductPagingAdapter

    @Inject
    lateinit var gson: Gson

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductPagingAdapter { product ->
            onProductClicked(product)
        }
        binding.rvProducts.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            val isRefreshing = loadState.refresh is LoadState.Loading
            binding.swipeRefresh.isRefreshing = isRefreshing

            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            if (isListEmpty) {
                binding.layoutEmpty.show()
                binding.rvProducts.hide()
            } else {
                binding.layoutEmpty.hide()
                binding.rvProducts.show()
            }

            val errorState = loadState.refresh as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error

            if (errorState != null && adapter.itemCount == 0) {
                binding.tvErrorText.text = errorState.error.localizedMessage
                    ?: getString(R.string.error_generic)
                binding.layoutError.show()
                binding.rvProducts.hide()
            } else {
                binding.layoutError.hide()
            }
        }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }

        binding.btnRetry.setOnClickListener {
            adapter.retry()
        }
    }

    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productsPagingStream.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    private fun onProductClicked(product: Product) {
        val productJson = gson.toJson(product)
        val bundle = bundleOf(
            "productId" to product.id,
            "productJson" to productJson
        )
        findNavController().navigate(
            R.id.action_productFragment_to_productDetailFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}