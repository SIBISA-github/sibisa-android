package com.bangkit.sibisa.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.databinding.FragmentHomeBinding
import com.bangkit.sibisa.ui.lesson.LessonActivity
import com.bangkit.sibisa.ui.quiz.QuizActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.levelBtn1.setOnClickListener {
            val intent = Intent(requireContext(), LessonActivity::class.java)
            intent.putExtra(LessonActivity.LEVEL, 1)
            startActivity(intent)
        }
        binding.levelBtn2.setOnClickListener {
            val intent = Intent(requireContext(), LessonActivity::class.java)
            intent.putExtra(LessonActivity.LEVEL, 2)
            startActivity(intent)
        }
        binding.levelBtn3.setOnClickListener {
            val intent = Intent(requireContext(), LessonActivity::class.java)
            intent.putExtra(LessonActivity.LEVEL, 3)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}