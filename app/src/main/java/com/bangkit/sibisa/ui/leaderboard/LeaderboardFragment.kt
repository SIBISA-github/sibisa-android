package com.bangkit.sibisa.ui.leaderboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.adapter.ProfilesAdapter
import com.bangkit.sibisa.databinding.FragmentLeaderboardBinding
import com.bangkit.sibisa.factory.ViewModelFactory
import com.bangkit.sibisa.models.profile.Profile
import com.bangkit.sibisa.models.result.NetworkResult
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
                            if (!it.isNullOrEmpty()) {
                                setupTop3UI(it)
                                setupRecyclerView(it.slice(3 until it.size))
                            } else {
                                binding.textLeaderboardError.visibility = View.VISIBLE
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.textLeaderboardError.visibility = View.VISIBLE
                        showToast(requireContext(), result.error.uppercase())
                    }
                }
            }
        }
    }

    private fun setupTop3UI(profiles: List<Profile?>) {
        Log.d("PROFILES", profiles.toString())
        // no. 1
        if (profiles[0]?.image != null) {
            Glide.with(requireContext()).load(profiles[0]?.image).into(binding.rank1Image)
        }
        if (!profiles[0]?.name.isNullOrEmpty()) {
            binding.textRank1Name.text = profiles[0]?.name
        }

        // no. 2
        if (profiles[1]?.image != null) {
            Glide.with(requireContext()).load(profiles[1]?.image).into(binding.rank2Image)
        }
        if (!profiles[1]?.name.isNullOrEmpty()) {
            binding.textRank2Name.text = profiles[1]?.name
        }

        // no. 3
        if (profiles[2]?.image != null) {
            Glide.with(requireContext()).load(profiles[2]?.image).into(binding.rank3Image)
        }
        if (!profiles[2]?.name.isNullOrEmpty()) {
            binding.textRank3Name.text = profiles[2]?.name
        }

        binding.bannerLeaderboard.visibility = View.VISIBLE

        binding.rank1Image.visibility = View.VISIBLE
        binding.rank2Image.visibility = View.VISIBLE
        binding.rank3Image.visibility = View.VISIBLE

        binding.textRank1Name.visibility = View.VISIBLE
        binding.textRank2Name.visibility = View.VISIBLE
        binding.textRank3Name.visibility = View.VISIBLE

        binding.textRank1Number.visibility = View.VISIBLE
        binding.textRank2Number.visibility = View.VISIBLE
        binding.textRank3Number.visibility = View.VISIBLE
    }

    private fun setupRecyclerView(profiles: List<Profile?>) {
        Log.d("PROFILES", profiles.toString())
        profilesAdapter = ProfilesAdapter(requireContext())
        profilesAdapter.setProfiles(profiles)
        binding.leaderboard.apply {
            adapter = profilesAdapter
        }
    }
}