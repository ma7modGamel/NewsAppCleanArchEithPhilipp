package com.safwa.newsappcleanarcheithphilipp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.safwa.newsappcleanarcheithphilipp.databinding.FragmentBreakingNewsBinding
import com.safwa.newsappcleanarcheithphilipp.ui.adapters.ArticleAdapters
import com.safwa.newsappcleanarcheithphilipp.ui.viewmodels.BreakingViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class BreakingFragment : Fragment() {

    private var _binding: FragmentBreakingNewsBinding? = null

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


    }

    private fun getData() {
        viewModel.getBreakingNews("us",1,"publishedAt")
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            Timber.e("getData:XxX:" + Gson().toJson(response))
        })

    }

    private fun setupViews() {
        val adapter = ArticleAdapters()
        val manger = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rv.layoutManager = manger
        binding.rv.adapter = adapter
        binding.rv.setHasFixedSize(true)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}