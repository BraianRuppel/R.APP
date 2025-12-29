package com.example.rapp.ui.landing

import android.os.Bundle
import androidx.fragment.R.layout.fragment_landing.xml


class LandingFragment : Fragment(R.layout.fragment_landing) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnGoList = view.findViewById<Button>(R.id.btnGoList)

        btnGoList.setOnClickListener {
            findNavController().navigate(
                R.id.action_landing_to_list
            )
        }
    }
}
