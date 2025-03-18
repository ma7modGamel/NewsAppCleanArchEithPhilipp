package com.safwa.newsappcleanarcheithphilipp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.safwa.newsappcleanarcheithphilipp.databinding.FragmentBreakingNewsBinding
import com.safwa.newsappcleanarcheithphilipp.ui.adapters.ArticleAdapters
import com.safwa.newsappcleanarcheithphilipp.ui.viewmodels.BreakingViewModel

class BreakingFragment : Fragment() {

    private var _binding: FragmentBreakingNewsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: BreakingViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this)[BreakingViewModel::class.java]
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()



    }

    private fun getData() {
        viewModel.getBreakingNews()

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