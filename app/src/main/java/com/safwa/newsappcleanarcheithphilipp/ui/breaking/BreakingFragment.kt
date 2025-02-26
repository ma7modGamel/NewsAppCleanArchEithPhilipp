package com.safwa.newsappcleanarcheithphilipp.ui.breaking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.safwa.newsappcleanarcheithphilipp.databinding.FragmentBreakingNewsBinding

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

        val textView: TextView = binding.textHome
        breakingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}