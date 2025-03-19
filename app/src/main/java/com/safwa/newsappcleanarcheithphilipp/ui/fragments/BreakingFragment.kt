package com.safwa.newsappcleanarcheithphilipp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresExtension
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.Article
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.databinding.FragmentBreakingNewsBinding
import com.safwa.newsappcleanarcheithphilipp.ui.adapters.ArticleAdapters
import com.safwa.newsappcleanarcheithphilipp.ui.viewmodels.BreakingViewModel
import com.safwa.newsappcleanarcheithphilipp.utils.Result
import com.safwa.newsappcleanarcheithphilipp.utils.Result.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class BreakingFragment : Fragment() {

    private var _binding: FragmentBreakingNewsBinding? = null
    private lateinit var  adapter: ArticleAdapters
    private val viewModel: BreakingViewModel by viewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        getData()
        getDataUsingFlow()
        getDataUsingStateFlow()

    }

    private fun getDataUsingStateFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newsState.collect { result ->
                    fetchDataInViews(result)
                }
            }
        }
    }

    private fun getDataUsingFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newsFlow.collect { result ->
                    fetchDataInViews(result)
                }
            }
        }
    }



    private fun getData() {
        viewModel.getBreakingNews("us",1,"publishedAt")
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            fetchDataInViews(response)
        })
    }

    private fun setupViews() {

        adapter = ArticleAdapters()
        val manger = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rv.layoutManager = manger
        binding.rv.adapter = adapter
        binding.rv.setHasFixedSize(true)
    }

    private fun fetchDataInViews(result: Result<NewsModel>) {

        Timber.e("resultxxx is ${Gson().toJson(result)}")
        when (result) {

            is Loading -> {
                binding.progress.visibility = View.VISIBLE
                binding.txtError.visibility = View.GONE
                binding.rv.visibility = View.GONE
            }
            is Success -> {
                binding.progress.visibility = View.GONE
                binding.txtError.visibility = View.GONE
                binding.rv.visibility = View.VISIBLE
                adapter.submitList(result.data.articles)
            }
            is Error -> {
                binding.progress.visibility = View.GONE
                binding.txtError.visibility = View.VISIBLE
                binding.rv.visibility = View.GONE
                binding.txtError.text = result.message
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}