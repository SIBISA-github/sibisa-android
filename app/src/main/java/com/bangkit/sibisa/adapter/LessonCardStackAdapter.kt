package com.bangkit.sibisa.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.sibisa.databinding.ItemCardBinding
import com.bangkit.sibisa.databinding.LeaderboardItemBinding
import com.bangkit.sibisa.models.lesson.Lesson
import com.bangkit.sibisa.models.profile.Profile
import com.bumptech.glide.Glide

class LessonCardStackAdapter(private val appContext: Context) :
    RecyclerView.Adapter<LessonCardStackAdapter.ViewHolder>() {
    private var lessons = arrayListOf<Lesson>()

    fun setLessons(lessons: List<Lesson>) {
        this.lessons = ArrayList(lessons)
    }

    inner class ViewHolder(val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemCardBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LessonCardStackAdapter.ViewHolder, position: Int) {
        val lesson = lessons[position]

        with(holder.binding) {
            itemLesson.text = lesson.title
            Glide.with(appContext).load(lesson.image).into(itemImage)
            itemImage.contentDescription = lesson.title
        }
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reAddItem(position: Int) {
        val item = lessons[position]
        lessons.removeAt(position)
        lessons.add(item)
        notifyDataSetChanged()
    }
}