package com.bangkit.sibisa.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.FragmentHomeBinding
import com.bangkit.sibisa.factory.ViewModelFactory
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.pref.UserPreference
import com.bangkit.sibisa.ui.lesson.LessonActivity
import com.bangkit.sibisa.ui.profile.ProfileViewModel
import com.bangkit.sibisa.utils.showHelpDialog
import com.bangkit.sibisa.utils.showToast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlin.properties.Delegates

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by lazy {
        val factory = ViewModelFactory(requireContext())

        ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private var level by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupAction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        val pref = UserPreference(requireContext())
        val userID = pref.getUserID()
        viewModel.getUserProfile(userID).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is NetworkResult.Success -> {
                        binding.progressBar.visibility = View.GONE

                        val profile = result.data
                        level = profile.idlevel ?: 1

                        // level 1
                        Glide.with(this).load(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_level__g1011,
                                null
                            )
                        )
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(binding.levelBtn1)

                        // level 2
                        if (level != null) {
                            if (level >= 2) {
                                Glide.with(this).load(
                                    ResourcesCompat.getDrawable(
                                        resources,
                                        R.drawable.ic_level__g1017,
                                        null
                                    )
                                )
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(binding.levelBtn2)

                                setButtonAsAble(binding.levelBtn2)
                            }

                            if (level >= 3) {
                                Glide.with(this).load(
                                    ResourcesCompat.getDrawable(
                                        resources,
                                        R.drawable.ic_level__g1023,
                                        null
                                    )
                                )
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(binding.levelBtn3)

                                setButtonAsAble(binding.levelBtn3)
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showToast(requireContext(), result.error.uppercase())
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.levelBtn1.setOnClickListener {
            goToLesson(1)
        }
        binding.levelBtn2.setOnClickListener {
            if (level >= 2) {
                goToLesson(2)
            } else {
                showLevelNotSufficientDialog()
            }
        }
        binding.levelBtn3.setOnClickListener {
            if (level >= 3) {
                goToLesson(3)
            } else {
                showLevelNotSufficientDialog()
            }
        }
    }

    private fun setButtonAsAble(button: ImageButton) {
        button.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.circle_button,
            null
        )
    }

    private fun goToLesson(level: Int) {
        val intent = Intent(requireContext(), LessonActivity::class.java)
        intent.putExtra(LessonActivity.LEVEL, level)
        startActivity(intent)
    }

    private fun showLevelNotSufficientDialog() {
        showHelpDialog(
            requireContext(),
            "UPSSS...",
            "Selesaikan kuis sesuai urutan untuk mengakses materi ini!"
        )
    }
}