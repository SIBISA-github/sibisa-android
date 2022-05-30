package com.bangkit.sibisa.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.FragmentProfileBinding
import com.bangkit.sibisa.factory.ViewModelFactory
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.pref.UserPreference
import com.bangkit.sibisa.ui.login.LoginActivity
import com.bangkit.sibisa.utils.showToast
import com.bumptech.glide.Glide
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by lazy {
        val factory = ViewModelFactory(requireContext())

        ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_menu -> {
                // save profile changes
                true
            }
            R.id.logout_menu -> {
                showDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.cameraButton.setOnClickListener {
            showToast(requireContext(), "Test camera button")
        }

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

    private fun logout() {
        val pref = UserPreference(requireContext())
        pref.clearUser()

        startActivity(Intent(requireContext(), LoginActivity::class.java))
        activity?.finish()
    }

    private fun showDialog() {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        builder?.setMessage(R.string.logout_dialog_message)?.setPositiveButton(
            R.string.logout_dialog_message_yes
        ) { dialog, _ ->
            dialog.dismiss()
            logout()
        }?.setNegativeButton(R.string.logout_dialog_message_no) { dialog, _ ->
            dialog.cancel()
        }

        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
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
                        with(binding) {
                            Glide.with(requireContext()).load(profile.image).into(profileImage)
                            textName.text = profile.name
                            textUsername.text = profile.username
                            textLevel.text = profile.idlevel.toString()
                            textExp.text = getString(R.string.text_exp, profile.exp.toString())
                            val parsedDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                LocalDateTime.parse(
                                    profile.createdAt,
                                    DateTimeFormatter.ISO_DATE_TIME
                                )
                            } else {
                                TODO("VERSION.SDK_INT < O")
                            }
                            val stringDate =
                                parsedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                            textJoinDate.text = stringDate
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showToast(requireContext(), "Error fetching profile, please try again")
                    }
                }
            }
        }
    }
}