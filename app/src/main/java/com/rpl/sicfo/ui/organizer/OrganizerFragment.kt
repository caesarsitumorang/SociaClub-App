package com.rpl.sicfo.ui.organizer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rpl.sicfo.databinding.FragmentOrganizerBinding
import com.rpl.sicfo.databinding.LayoutOrganizerBinding

class OrganizerFragment : Fragment() {

    private lateinit var binding: FragmentOrganizerBinding
    private lateinit var alternateBinding: LayoutOrganizerBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the main binding layout
        binding = FragmentOrganizerBinding.inflate(inflater, container, false)
        // Inflate the alternate binding layout
        alternateBinding = LayoutOrganizerBinding.inflate(inflater, container, false)

        // Add both layouts to the container, only one will be visible at a time
        val rootView = FrameLayout(requireContext())
        rootView.addView(binding.root)
        rootView.addView(alternateBinding.root)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE)

        fetchNpmFromUsers()
    }

    private fun fetchNpmFromUsers() {
        val userId = auth.currentUser?.uid ?: return
        database.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val npm = snapshot.child("npm").getValue(String::class.java) ?: return
                fetchOrganisasiDataFromFirebase(npm)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrganizerFragment", "Gagal mengambil data user: ${error.message}")
            }
        })
    }

    private fun fetchOrganisasiDataFromFirebase(npm: String) {
        database.child("OrganisasiFikom").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isMember = false

                for (dataSnapshot in snapshot.children) {
                    val anggotaPath = dataSnapshot.child("anggota")
                    if (anggotaPath.exists()) {
                        for (anggotaSnapshot in anggotaPath.children) {
                            val anggotaNpm = anggotaSnapshot.getValue(String::class.java)
                            if (anggotaNpm == npm) {
                                isMember = true
                                val title = dataSnapshot.child("title").getValue(String::class.java) ?: ""
                                val logo = dataSnapshot.child("logo").getValue(String::class.java) ?: ""
                                val profil = dataSnapshot.child("profil").getValue(String::class.java) ?: ""
                                val fakultas = dataSnapshot.child("fakultas").getValue(String::class.java) ?: ""
                                val image1 = dataSnapshot.child("image1").getValue(String::class.java) ?: ""
                                val strukturalImage = dataSnapshot.child("strukturalImage").getValue(String::class.java) ?: ""
                                val visiMisi = dataSnapshot.child("visiMisi").getValue(String::class.java) ?: ""
                                val detailTitle = dataSnapshot.child("detailTitle").getValue(String::class.java) ?: ""

                                displayOrganisasiData(title, logo, profil, fakultas, image1, strukturalImage, visiMisi, detailTitle)
                                break
                            }
                        }
                    }
                }

                if (!isMember) {
                    displayAlternateLayout()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrganizerFragment", "Gagal mengambil data organisasi: ${error.message}")
            }
        })
    }

    private fun displayOrganisasiData(
        title: String, logo: String, profil: String, fakultas: String,
        image1: String, strukturalImage: String, visiMisi: String, detailTitle: String
    ) {
        binding.root.visibility = View.VISIBLE
        alternateBinding.root.visibility = View.GONE

        binding.tvTitleOrganisasi.text = title
        binding.tvFakultas.text = fakultas
//        binding.tvProfilLengkap.text = profil
//        binding.tvProfil.text = detailTitle
        Glide.with(requireContext()).load(logo).into(binding.imgLogo)
        Glide.with(requireContext()).load(image1).into(binding.imgDetail1)
//        Glide.with(requireContext()).load(visiMisi).into(binding.imgDetailMisi)
//        Glide.with(requireContext()).load(strukturalImage).into(binding.imgDetailMisi)
    }

    private fun displayAlternateLayout() {
        binding.root.visibility = View.GONE
        alternateBinding.root.visibility = View.VISIBLE
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarOrganizer.visibility = View.VISIBLE
        } else {
            binding.progressBarOrganizer.visibility = View.GONE
        }
    }
}
