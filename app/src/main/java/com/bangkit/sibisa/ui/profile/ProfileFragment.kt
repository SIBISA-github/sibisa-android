package com.bangkit.sibisa.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.FragmentProfileBinding
import com.bangkit.sibisa.factory.ViewModelFactory
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.pref.UserPreference
import com.bangkit.sibisa.ui.login.LoginActivity
import com.bangkit.sibisa.utils.showToast
import com.bangkit.sibisa.utils.showYesNoDialog
import com.bangkit.sibisa.utils.uriToFile
import com.bumptech.glide.Glide
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.properties.Delegates

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by lazy {
        val factory = ViewModelFactory(requireContext())

        ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private var getFile: File? = null
    private var userID by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

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

    private fun logout() {
        val pref = UserPreference(requireContext())
        pref.clearUser()

        startActivity(Intent(requireContext(), LoginActivity::class.java))
        activity?.finish()
    }

    private fun setupUI() {
        val pref = UserPreference(requireContext())
        userID = pref.getUserID()
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
                            if (profile.image != null) {
                                Glide.with(requireContext()).load(profile.image)
                                    .placeholder(R.drawable.ic_person).error(R.drawable.ic_person)
                                    .into(profileImage)
                            }
                            textName.text = profile.name
                            textUsername.text = profile.username

                            val level = profile.exp?.div(50)
                            textLevel.text = level.toString()
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
                        showToast(requireContext(), result.error.uppercase())
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.cameraButton.setOnClickListener {
            startGallery()
        }

        binding.logoutButton.setOnClickListener {
            showLogoutDialog()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())

            getFile = myFile

            showProfilePictureDialog(selectedImg)
        }
    }

    private fun uploadImage(selectedImg: Uri) {
        viewModel.updateProfile(userID, getFile)?.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is NetworkResult.Success -> {
                        binding.progressBar.visibility = View.GONE

                        binding.profileImage.setImageURI(selectedImg)
                        showToast(requireContext(), "Foto profil telah diganti!")
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d("USER_PROFILE", result.error)
                        showToast(requireContext(), result.error.uppercase())
                    }
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun showProfilePictureDialog(selectedImg: Uri) {
        showYesNoDialog(
            requireContext(), "PERHATIAN", getString(R.string.picture_dialog_message)
        ) { uploadImage(selectedImg) }
    }

    private fun showLogoutDialog() {
        showYesNoDialog(
            requireContext(), "PERHATIAN", getString(R.string.logout_dialog_message)
        ) { logout() }
    }
}