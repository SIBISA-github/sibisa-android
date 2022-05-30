package com.bangkit.sibisa.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.FragmentProfileBinding
import com.bangkit.sibisa.pref.UserPreference
import com.bangkit.sibisa.ui.login.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        val profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
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
}