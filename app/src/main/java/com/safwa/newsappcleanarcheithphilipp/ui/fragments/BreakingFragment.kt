package com.safwa.newsappcleanarcheithphilipp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.safwa.newsappcleanarcheithphilipp.databinding.FragmentBreakingNewsBinding
import com.safwa.newsappcleanarcheithphilipp.ui.viewmodels.BreakingViewModel

class BreakingFragment : Fragment() {

    private var _binding: FragmentBreakingNewsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val breakingViewModel =
            ViewModelProvider(this).get(BreakingViewModel::class.java)

        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}