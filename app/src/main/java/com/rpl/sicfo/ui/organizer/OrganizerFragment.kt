package com.rpl.sicfo.ui.organizer

import android.content.Context
import android.content.Intent
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
    private var organizationId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentOrganizerBinding.inflate(inflater, container, false)
        alternateBinding = LayoutOrganizerBinding.inflate(inflater, container, false)

        val rootView = FrameLayout(requireContext())
        rootView.addView(binding.root)
        rootView.addView(alternateBinding.root)

        runForumChat()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE)

        fetchNpmFromUsers()
    }

    private fun runForumChat() {
        binding.imgDetail1.setOnClickListener {
            val npm = sharedPreferences.getString("npm", "") ?: ""
            if (npm.isNotEmpty() && organizationId != null) {
                startActivity(Intent(requireContext(), ForumChatActivity::class.java).apply {
                    putExtra("npm", npm)
                    putExtra("organizationId", organizationId)
                })
            } else {
                Log.e("OrganizerFragment", "NPM atau Organization ID tidak ditemukan")
            }
        }
    }

    private fun fetchNpmFromUsers() {
        val userId = auth.currentUser?.uid ?: return
        database.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val npm = snapshot.child("npm").getValue(String::class.java) ?: ""
                sharedPreferences.edit().putString("npm", npm).apply()
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
                                organizationId = dataSnapshot.key
                                val title = dataSnapshot.child("title").getValue(String::class.java) ?: ""
                                val logo = dataSnapshot.child("logo").getValue(String::class.java) ?: ""
                                val fakultas = dataSnapshot.child("fakultas").getValue(String::class.java) ?: ""

                                // ambil data struktural
                                val strukturalPath = dataSnapshot.child("struktural")
                                val ketua = strukturalPath.child("ketua").getValue(String::class.java) ?: ""
                                val wakilKetua = strukturalPath.child("wakilKetua").getValue(String::class.java) ?: ""
                                val sekretaris = strukturalPath.child("sekretaris").getValue(String::class.java) ?: ""
                                val bendahara = strukturalPath.child("bendahara").getValue(String::class.java) ?: ""
                                val wakilSekretaris = strukturalPath.child("wakilSekretaris").getValue(String::class.java) ?: ""

                                // ambil data teamInternal
                                val teamInternalPath = dataSnapshot.child("teamInternal")
                                val ketuaInternal = teamInternalPath.child("ketua").getValue(String::class.java) ?: ""
                                val anggota1 = teamInternalPath.child("anggota1").getValue(String::class.java) ?: ""
                                val anggota2 = teamInternalPath.child("anggota2").getValue(String::class.java) ?: ""
                                val anggota3 = teamInternalPath.child("anggota3").getValue(String::class.java) ?: ""
                                val anggota4 = teamInternalPath.child("anggota4").getValue(String::class.java) ?: ""
                                val anggota5 = teamInternalPath.child("anggota5").getValue(String::class.java) ?: ""
                                val anggota6 = teamInternalPath.child("anggota6").getValue(String::class.java) ?: ""
                                val anggota7 = teamInternalPath.child("anggota7").getValue(String::class.java) ?: ""

                                // ambil data teamExternal
                                val teamExternalPath = dataSnapshot.child("teamExternal")
                                val ketuaExternal = teamExternalPath.child("ketua").getValue(String::class.java) ?: ""
                                val anggota1_external = teamExternalPath.child("anggota1").getValue(String::class.java) ?: ""
                                val anggota2_external = teamExternalPath.child("anggota2").getValue(String::class.java) ?: ""
                                val anggota3_external = teamExternalPath.child("anggota3").getValue(String::class.java) ?: ""
                                val anggota4_external = teamExternalPath.child("anggota4").getValue(String::class.java) ?: ""

                                displayOrganisasiData(title, logo, fakultas, ketua, wakilKetua, sekretaris, bendahara, wakilSekretaris,ketuaInternal,anggota1,
                                    anggota2,anggota3,anggota4,anggota5,anggota6,anggota7,ketuaExternal,anggota1_external,anggota2_external,anggota3_external,anggota4_external)
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
        title: String,
        logo: String,
        fakultas: String,
        ketua: String,
        wakilKetua: String,
        sekretaris: String,
        bendahara: String,
        wakilSekretaris: String,
        ketuaInternal : String,
        anggota1 :String,
        anggota2:String,
        anggota3:String,
        anggota4:String,
        anggota5:String,
        anggota6:String,
        anggota7:String,
        ketuaExternal:String,
        anggota1_external:String,
        anggota2_external:String,
        anggota3_external:String,
        anggota4_external:String
    ) {
        binding.root.visibility = View.VISIBLE
        alternateBinding.root.visibility = View.GONE


        // data utama organisasi
        binding.tvTitleOrganisasi.text = title
        binding.tvFakultas.text = fakultas
        Glide.with(requireContext()).load(logo).into(binding.imgLogo)

        // data stuktural organisasi
        binding.tvDescKetua.text = ketua
        binding.tvDescWakil.text = wakilKetua
        binding.tvDescSekretaris.text = sekretaris
        binding.tvDescWakilSekretaris.text = wakilSekretaris
        binding.tvDescBendahara.text = bendahara

        // data team internal organisasi
        binding.tvDescKetuaInternal.text = ketuaInternal
        binding.tvDescAnggota1.text = anggota1
        binding.tvDescAnggota2.text = anggota2
        binding.tvDescAnggota3.text = anggota3
        binding.tvDescAnggota4.text = anggota4
        binding.tvDescAnggota5.text = anggota5
        binding.tvDescAnggota6.text = anggota6
        binding.tvDescAnggota7.text = anggota7

        // ambil data external organisasi
        binding.tvDescKetuaExternal.text = ketuaExternal
        binding.tvDescAnggota1Exteral.text = anggota1_external
        binding.tvDescAnggota2Exteral.text = anggota2_external
        binding.tvDescAnggota3Exteral.text = anggota3_external
        binding.tvDescAnggota4Exteral.text = anggota4_external

    }
    private fun displayAlternateLayout() {
        binding.root.visibility = View.GONE
        alternateBinding.root.visibility = View.VISIBLE
    }
}
