package com.bangkit.sibisa.ui.lesson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.ActivityLessonBinding

class LessonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLessonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}