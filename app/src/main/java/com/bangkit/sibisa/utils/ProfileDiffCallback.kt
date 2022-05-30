package com.bangkit.sibisa.utils

import androidx.recyclerview.widget.DiffUtil
import com.bangkit.sibisa.models.profile.Profile

class ProfileDiffCallback(
    private val oldProfileList: List<Profile?>,
    private val newProfileList: List<Profile?>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldProfileList.size
    }

    override fun getNewListSize(): Int {
        return newProfileList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldProfileList[oldItemPosition]?.id == newProfileList[newItemPosition]?.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldProfile = oldProfileList[oldItemPosition]
        val newProfile = newProfileList[newItemPosition]

        return if (oldProfile !== null && newProfile !== null) {
            oldProfile.id == newProfile.id && oldProfile.createdAt == newProfile.createdAt
        } else !(oldProfile == null || newProfile == null)
    }
}