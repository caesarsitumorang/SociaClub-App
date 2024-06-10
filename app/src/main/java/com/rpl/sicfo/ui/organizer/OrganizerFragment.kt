package com.rpl.sicfo.ui.organizer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.FragmentOrganizerBinding


class OrganizerFragment : Fragment() {

    private lateinit var binding : FragmentOrganizerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentOrganizerBinding.inflate(layoutInflater)
        return binding.root
    }
}