package com.safwa.newsappcleanarcheithphilipp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.safwa.newsappcleanarcheithphilipp.databinding.FragmentSavedNewsBinding
import com.safwa.newsappcleanarcheithphilipp.ui.viewmodels.SavedViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SavedFragment : Fragment() {

    private var _binding: FragmentSavedNewsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val savedViewModel =
            ViewModelProvider(this).get(SavedViewModel::class.java)

        _binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}