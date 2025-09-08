package com.safwa.newsappcleanarcheithphilipp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsResponse
import com.safwa.newsappcleanarcheithphilipp.databinding.FragmentSearchNewsBinding
import com.safwa.newsappcleanarcheithphilipp.ui.adapters.ArticleAdapters
import com.safwa.newsappcleanarcheithphilipp.ui.adapters.ArticlePagingAdapter
import com.safwa.newsappcleanarcheithphilipp.ui.viewmodels.SearchNewsViewModel
import com.safwa.newsappcleanarcheithphilipp.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint

class SearchNewsFragment : Fragment() {

    private var _binding: FragmentSearchNewsBinding? = null
    private val viewModel: SearchNewsViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var adapter: ArticlePagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupSearchView()


    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    getResultSearchData(query) // إرسال الاستعلام للـ ViewModel
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let {
                    getResultSearchData(query) // إرسال الاستعلام للـ ViewModel
                }
                return true
            }

        })
    }

    private fun getResultSearchData(query: String) {
        viewModel.searchNews(query)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseSearchNews.collectLatest { result ->
                    updateUI(result)
                }
            }
        }
    }

    private fun updateUI(data: PagingData<NewsResponse>) {
        // مراقبة حالات التحميل (Loading, Success, Error)
        // مراقبة حالات التحميل
        adapter.addLoadStateListener { loadStates ->
            val refreshState = loadStates.refresh
            when (refreshState) {
                is LoadState.Loading -> {
                    binding.rv.visibility = View.GONE
                    binding.progress.visibility = View.VISIBLE
                    binding.txtError.visibility = View.GONE
                    Log.d("SearchNewsFragment", "Loading state triggered")
                }
                is LoadState.NotLoading -> {
                    binding.rv.visibility = View.VISIBLE
                    binding.progress.visibility = View.GONE
                    binding.txtError.visibility = View.GONE
                    Log.d("SearchNewsFragment", "Data loaded, item count: ${adapter.itemCount}")
                    if (adapter.itemCount == 0) {
                        binding.rv.visibility = View.GONE
                        binding.txtError.apply {
                            visibility = View.VISIBLE
                            text = "No results found"
                        }
                        Log.d("SearchNewsFragment", "No data returned")
                    } else {
                        // طباعة أول عنصر من الداتا للتأكد
                        adapter.snapshot().items.take(1).forEach {
                            Log.d("SearchNewsFragment", "First item: $it")
                        }
                    }
                }
                is LoadState.Error -> {
                    binding.rv.visibility = View.GONE
                    binding.progress.visibility = View.GONE
                    binding.txtError.apply {
                        visibility = View.VISIBLE
                        text = refreshState.error.message ?: "Unknown error"
                    }
                    Log.e("SearchNewsFragment", "Error: ${refreshState.error.message}")
                }
            }

            // مراقبة تحميل الصفحات الإضافية (Append)
            val appendState = loadStates.append
            if (appendState is LoadState.Loading) {
                Log.d("SearchNewsFragment", "Loading next page...")
            } else if (appendState is LoadState.NotLoading && appendState.endOfPaginationReached) {
                Log.d("SearchNewsFragment", "End of pagination reached")
            }
        }
    }

    private fun initViews() {

        adapter = ArticlePagingAdapter()
        binding.rv.adapter = adapter
        binding.rv.setHasFixedSize(true)
        binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.txtError.apply {
            visibility = View.VISIBLE
            text = "Enter key search"
        }

        binding.progress.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    private fun updateUI(result: Result<NewsModel>) {

        when (result) {
            is Result.Error -> {
                binding.rv.visibility = View.GONE
                binding.progress.visibility = View.GONE
                binding.txtError.apply {
                    visibility = View.VISIBLE
                    text=result.message
                }

            }

            is Result.Loading -> {
                binding.rv.visibility = View.GONE
                binding.progress.visibility = View.VISIBLE
                binding.txtError.visibility = View.GONE
            }

            is Result.Success -> {
                binding.rv.visibility = View.VISIBLE
                binding.progress.visibility = View.GONE
                binding.txtError.visibility = View.GONE
                adapter.submitList(result.data.articles)
                if (result.data.articles?.size ==0) {
                    binding.rv.visibility = View.GONE
                    binding.progress.visibility = View.GONE
                    binding.txtError.apply {
                        visibility = View.VISIBLE
                        text="result not found"
                    }
                }
            }
        }

    }


 */


}