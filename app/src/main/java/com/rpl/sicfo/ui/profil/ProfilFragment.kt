package com.rpl.sicfo.ui.profil

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rpl.sicfo.R
import com.rpl.sicfo.adapter.OrganisasiUserAdapter
import com.rpl.sicfo.data.Organisasi
import com.rpl.sicfo.databinding.FragmentProfilBinding
import com.rpl.sicfo.ui.login.LoginActivity
import com.rpl.sicfo.ui.organisasiFikom.DetailOrganisasiFikomActivity
import com.rpl.sicfo.ui.profil.pengaturan.PengaturanActivity

class ProfilFragment : Fragment(), ButtonSheetPicture.OnImageSelectedListener, OrganisasiUserAdapter.OnItemClickListener {
    private lateinit var binding: FragmentProfilBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val TAG = "ProfileFragment"
    private lateinit var sharedPreferences: SharedPreferences
    private var buttonSheetPicture: ButtonSheetPicture? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var organisasiUserAdapter: OrganisasiUserAdapter
    private val organisasiList = mutableListOf<Organisasi>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilBinding.inflate(inflater, container, false)

        // Initialize FirebaseAuth and FirebaseDatabase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE)

        // Set flag to show options menu
        setHasOptionsMenu(true)
        binding.btnAddImageUser.setOnClickListener { onClickImportImage() }

        // Setup RecyclerView
        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        setupToolbar()
    }

    private fun setupRecyclerView() {
        organisasiUserAdapter = OrganisasiUserAdapter(organisasiList, this)
        binding.rvOrganisasiUser.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = organisasiUserAdapter
        }
    }

    private fun initData() {
        // Get user's full name from Firebase Realtime Database
        val userRef = database.getReference("Users").child(auth.currentUser?.uid ?: "")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val namaLengkap = snapshot.child("namaLengkap").value?.toString() ?: ""
                val npm = snapshot.child("npm").value?.toString() ?: ""
                val imageUrl = snapshot.child("imageUrl").value?.toString() ?: ""

                binding.tvNamalengkapProfil.text = namaLengkap
                binding.tvNpm.text = npm

                if (imageUrl.isNotEmpty()) {
                    // Load the image using Glide
                    Glide.with(this@ProfilFragment).load(imageUrl).into(binding.imgUser)
                }

                // Save npm in SharedPreferences
                sharedPreferences.edit().putString("NPM", npm).apply()

                // Fetch user's organizations
                fetchOrganisasiDataFromFirebase(npm)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to get user data: ${error.message}")
            }
        })
    }

    private fun fetchOrganisasiDataFromFirebase(npm: String) {
        database.getReference("OrganisasiFikom").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                organisasiList.clear()

                for (dataSnapshot in snapshot.children) {
                    val anggotaPath = dataSnapshot.child("anggota")
                    if (anggotaPath.exists()) {
                        for (anggotaSnapshot in anggotaPath.children) {
                            val anggotaNpm = anggotaSnapshot.getValue(String::class.java)
                            if (anggotaNpm == npm) {
                                val title = dataSnapshot.child("title").getValue(String::class.java)
                                val logo = dataSnapshot.child("logo").getValue(String::class.java)
                                val profil =
                                    dataSnapshot.child("profil").getValue(String::class.java)
                                val fakultas =
                                    dataSnapshot.child("fakultas").getValue(String::class.java)
                                val image1 =
                                    dataSnapshot.child("image1").getValue(String::class.java)
                                val image2 =
                                    dataSnapshot.child("image2").getValue(String::class.java)
                                val image3 =
                                    dataSnapshot.child("image3").getValue(String::class.java)
                                val strukturalImage = dataSnapshot.child("strukturalImage")
                                    .getValue(String::class.java)
                                val visiMisi =
                                    dataSnapshot.child("visiMisi").getValue(String::class.java)
//                    val anggota = dataSnapshot.child("anggota").getValue(String::class.java)
                                val detailTitle =
                                    dataSnapshot.child("detailTitle").getValue(String::class.java)

                                if (title != null && logo != null && detailTitle != null && fakultas != null) {
                                    val organisasi = Organisasi(
                                        title = title,
                                        logo = logo,
                                        profil = profil ?: "",
                                        fakultas = fakultas,
                                        image1 = image1 ?: "",
                                        image2 = image2 ?: "",
                                        image3 = image3 ?: "",
                                        strukturalImage = strukturalImage ?: "",
                                        visiMisi = visiMisi ?: "",
//                            anggota = anggota ?: "",
                                        detailTitle = detailTitle
                                    )
                                    organisasiList.add(organisasi)
                                    break
                                }
                            }
                        }
                    }
                }
                organisasiUserAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch organisasi data: ${error.message}")
            }
        })
    }

    private fun setupToolbar() {
        binding.appBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit_profile -> {
                    onClickPengaturan()
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

    private fun onClickPengaturan() {
        val intent = Intent(requireContext(), PengaturanActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        auth.signOut()
        // Clear login status from SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("IS_LOGGED_IN", false)
        editor.apply()
    }

    private fun onClickLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Keluar")
            .setMessage("Anda yakin ingin Keluar?")
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

    override fun onImageSelected(uri: Uri) {
        // Show confirmation dialog
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Konfirmasi")
        alertDialogBuilder.setMessage("Apa Kamu Yakin Untuk Mengganti Foto Profil?")
        alertDialogBuilder.setPositiveButton("Ya") { _, _ ->
            uploadImageToFirebase(uri)
            buttonSheetPicture?.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }

    private fun onClickImportImage() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        } else {
            buttonSheetPicture = ButtonSheetPicture()
            buttonSheetPicture?.setOnImageSelectedListener(this)
            buttonSheetPicture?.show(childFragmentManager, ButtonSheetPicture.TAG)
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val imageRef = storageRef.child("users/$userId/profile.jpg")

        // Show the progress bar
        binding.progressBarProfile.visibility = View.VISIBLE

        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                saveImageUrlToDatabase(downloadUri.toString())
                // Load the new image using Glide
                Glide.with(this).load(downloadUri).into(binding.imgUser)
                // Hide the progress bar
                binding.progressBarProfile.visibility = View.GONE
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Gagal Memasukkan Gambar: ${exception.message}")
            Toast.makeText(requireContext(), "Gagal Memasukkan Gambar", Toast.LENGTH_SHORT).show()
            // Hide the progress bar
            binding.progressBarProfile.visibility = View.GONE
        }.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            binding.progressBarProfile.progress = progress.toInt()
        }
    }

    private fun saveImageUrlToDatabase(imageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.getReference("Users").child(userId)
        userRef.child("imageUrl").setValue(imageUrl).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Foto Profil Berhasil Diubah", Toast.LENGTH_SHORT).show()
            } else {
                Log.e(TAG, "Failed to update image URL in database")
                Toast.makeText(requireContext(), "Gagal Memasukkan Gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemClick(organisasi: Organisasi) {
        val intent = Intent(requireContext(), DetailOrganisasiFikomActivity::class.java).apply {
            putExtra("title", organisasi.title)
            putExtra("logo", organisasi.logo)
            putExtra("profil", organisasi.profil)
            putExtra("image1", organisasi.image1)
            putExtra("image2", organisasi.image2)
            putExtra("image3", organisasi.image3)
            putExtra("visiMisi", organisasi.visiMisi)
            putExtra("fakultas", organisasi.fakultas)
            putExtra("strukturalImage", organisasi.strukturalImage)
            putExtra("anggota", organisasi.anggota)
            putExtra("detailTitle", organisasi.detailTitle)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_PROFILE = "ProfileFragment"
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
