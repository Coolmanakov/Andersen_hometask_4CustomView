package com.example.andersen_hometask_4customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.*
import androidx.core.animation.doOnRepeat

class MainActivity : AppCompatActivity() {
    companion object{
        private val LOG_TAG = MainActivity::class.java.canonicalName
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}