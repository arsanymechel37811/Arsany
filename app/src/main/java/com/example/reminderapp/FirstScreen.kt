package com.example.reminderapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2

class FirstScreen : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first_screen, container, false)
        val viewpager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        view.rootView.findViewById<TextView>(R.id.textView2).setOnClickListener{
            viewpager?.currentItem = 1
        }
        return view
    }
}