package com.rpl.sicfo.ui.profil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.FragmentProfilBinding
import com.rpl.sicfo.ui.login.LoginActivity


class ProfilFragment : Fragment() {
    private lateinit var binding: FragmentProfilBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val TAG = "ProfileFragment"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilBinding.inflate(inflater, container, false)

        // Inisialisasi FirebaseAuth dan FirebaseDatabase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Inisialisasi SharedPreferences
        sharedPreferences =
            requireActivity().getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE)

        // Set flag agar fragment dapat menampilkan option menu
        setHasOptionsMenu(true)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        setupToolbar()
    }

    private fun initData() {

        // Dapatkan data nama lengkap dari Firebase Realtime Database
        val userRef = database.getReference("Users").child(auth.currentUser?.uid ?: "")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val namaLengkap = snapshot.child("namaLengkap").value?.toString() ?: ""
                val npm = snapshot.child("npm").value?.toString() ?: ""
                binding.tvNamalengkapProfil.text = namaLengkap // Tampilkan nama lengkap di TextView
                binding.tvNpm.text = npm
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Gagal mengambil data pengguna: ${error.message}")
            }
        })
    }

    private fun setupToolbar() {
        binding.appBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit_profile -> {
                    true
                }

                R.id.menu_logout -> {
                    onClickLogout()
                    true
                }

                else -> false
            }
        }
    }


    private fun logout() {
        auth.signOut()
        // Hapus status login dari SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("IS_LOGGED_IN", false)
        editor.apply()
    }

    private fun onClickLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Keluar")
            .setMessage("Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                logout()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
