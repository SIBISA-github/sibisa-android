package com.bangkit.sibisa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.LeaderboardItemBinding
import com.bangkit.sibisa.models.profile.Profile
import com.bangkit.sibisa.utils.ProfileDiffCallback
import com.bumptech.glide.Glide


class ProfilesAdapter(private val appContext: Context) :
    RecyclerView.Adapter<ProfilesAdapter.ViewHolder>() {
    private lateinit var onClickedCallback: OnClickedCallback
    private var profiles = ArrayList<Profile?>()

    fun setProfiles(profiles: List<Profile?>?) {
        profiles?.let {
            val diffCallback = ProfileDiffCallback(this.profiles.toList(), profiles)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            this.profiles.clear()
            this.profiles.addAll(profiles)
            diffResult.dispatchUpdatesTo(this)
        }
    }

    inner class ViewHolder(val binding: LeaderboardItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LeaderboardItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val profile = profiles[position]

        with(viewHolder.binding) {
            tvRank.text = "${position + 4}"
            tvUsername.text = profile?.name
            tvExp.text = appContext.getString(R.string.text_exp, profile?.exp.toString())
            if (profile?.image != null && profile.image != IMAGE_PLACEHOLDER) {
                Glide.with(appContext).load(profile.image).into(ivProfile)
            }
        }
    }

    override fun getItemCount() = profiles.size

    interface OnClickedCallback {
    }

    companion object {
        const val IMAGE_PLACEHOLDER = "https://storage.googleapis.com/sibisa_bucket/default.jpg"
    }
}