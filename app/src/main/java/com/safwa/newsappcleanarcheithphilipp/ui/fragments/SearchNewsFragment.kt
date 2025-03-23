package com.safwa.newsappcleanarcheithphilipp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.databinding.FragmentSearchNewsBinding
import com.safwa.newsappcleanarcheithphilipp.ui.adapters.ArticleAdapters
import com.safwa.newsappcleanarcheithphilipp.ui.viewmodels.SearchNewsViewModel
import com.safwa.newsappcleanarcheithphilipp.utils.Result
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment() {

    private var _binding: FragmentSearchNewsBinding? = null
    val viewModel: SearchNewsViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    getData(query) // إرسال الاستعلام للـ ViewModel
                }
                return true
            }
            override fun onQueryTextChange(query: String?): Boolean {
                query?.let {
                    getData(query) // إرسال الاستعلام للـ ViewModel
                }
                return true
            }

        })


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle (Lifecycle.State.STARTED) {
                viewModel.responseSearchNews.collect { result ->
                    fetchDataInViews(result)
                }
            }
        }

    }

    private fun getData(query: String) {
        viewModel.searchNews(query)
    }






    lateinit var adapter: ArticleAdapters
    private fun initViews() {
        adapter = ArticleAdapters()
        binding.rv.adapter = adapter
        binding.rv.setHasFixedSize(true)
        binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun fetchDataInViews(result: Result<NewsModel>) {
        when (result) {
            is Result.Error -> {
                binding.rv.visibility = View.GONE
                binding.progress.visibility = View.GONE
                binding.txtError.visibility=View.VISIBLE

            }
            is Result.Loading -> {
                binding.rv.visibility = View.GONE
                binding.progress.visibility = View.VISIBLE
                binding.txtError.visibility=View.GONE
            }
            is Result.Success -> {
                binding.rv.visibility = View.VISIBLE
                binding.progress.visibility = View.GONE
                binding.txtError.visibility=View.GONE
                adapter.submitList(result.data.articles)
            }
        }

    }
}
