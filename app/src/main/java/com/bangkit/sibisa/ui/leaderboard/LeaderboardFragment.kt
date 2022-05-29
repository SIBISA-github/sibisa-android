package com.bangkit.sibisa.ui.leaderboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.adapter.ProfilesAdapter
import com.bangkit.sibisa.databinding.FragmentLeaderboardBinding
import com.bangkit.sibisa.factory.ViewModelFactory
import com.bangkit.sibisa.models.profile.Profile
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.pref.UserPreference
import com.bangkit.sibisa.ui.MainActivity
import com.bangkit.sibisa.utils.showToast
import com.bumptech.glide.Glide

class LeaderboardFragment : Fragment() {
    private lateinit var profilesAdapter: ProfilesAdapter
    private var _binding: FragmentLeaderboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: LeaderboardViewModel by lazy {
        val factory = ViewModelFactory(requireContext())

        ViewModelProvider(this, factory)[LeaderboardViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        viewModel.getAllUserProfiles().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is NetworkResult.Success -> {
                        binding.progressBar.visibility = View.GONE

                        result.data.let {
                            if (it != null) {
                                setupTop3UI(it)
                                setupRecyclerView(it.slice(2 until it.size - 1))
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showToast(requireContext(), "Login error, please try again")
                    }
                }
            }
        }
    }

    private fun setupTop3UI(profiles: List<Profile?>) {
        with(binding) {
            // no. 1
            Glide.with(requireContext()).load(profiles[0]?.image).into(this.rank1Image)

            // no. 2
            Glide.with(requireContext()).load(profiles[1]?.image).into(this.rank1Image)

            // no. 3
            Glide.with(requireContext()).load(profiles[2]?.image).into(this.rank1Image)
        }
    }

    private fun setupRecyclerView(profiles: List<Profile?>) {
        profilesAdapter = ProfilesAdapter(requireContext())
        binding.leaderboard.apply {
            adapter = profilesAdapter
        }
    }
}