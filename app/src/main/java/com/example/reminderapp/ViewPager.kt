package com.example.reminderapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ViewPager : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)
        val fragmentList  = arrayListOf<Fragment>(
            FirstScreen(),SecondFragment()
        )
        val adapter = ViewPagerAdapter(
            fragmentList,requireActivity().supportFragmentManager,lifecycle
        )
        view.rootView.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager).adapter = adapter
        return view

    }

}