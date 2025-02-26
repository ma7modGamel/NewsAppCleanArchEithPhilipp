package com.safwa.newsappcleanarcheithphilipp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.safwa.newsappcleanarcheithphilipp.databinding.FragmentSearchNewsBinding
import com.safwa.newsappcleanarcheithphilipp.ui.viewmodels.SearchNewsViewModel


class SearchNewsFragment : Fragment() {

    private var _binding: FragmentSearchNewsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val searchNewsViewModel =
            ViewModelProvider(this).get(SearchNewsViewModel::class.java)

        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        searchNewsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}