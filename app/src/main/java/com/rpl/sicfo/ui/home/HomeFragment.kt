package com.rpl.sicfo.ui.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rpl.sicfo.R
import com.rpl.sicfo.adapter.BeritaOrganisasiAdapter
import com.rpl.sicfo.adapter.KlubFikomAdapter
import com.rpl.sicfo.adapter.OrganisasiFikomAdapter
import com.rpl.sicfo.data.Berita
import com.rpl.sicfo.data.KlubFikom
import com.rpl.sicfo.data.Organisasi
import com.rpl.sicfo.databinding.FragmentHomeBinding
import com.rpl.sicfo.ui.berita.DetailBeritaKegiatan
import com.rpl.sicfo.ui.home.pencarian.SearchActivity
import com.rpl.sicfo.ui.klubFikom.DetailKlubFikomActivity
import com.rpl.sicfo.ui.notifikasi.NotifikasiActivity
import com.rpl.sicfo.ui.organisasiFikom.DetailOrganisasiFikomActivity
import java.util.Timer
import java.util.TimerTask


class HomeFragment : Fragment(),
    BeritaOrganisasiAdapter.OnItemClickListener,
    OrganisasiFikomAdapter.OnItemClickListener,
    KlubFikomAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapterberita: BeritaOrganisasiAdapter
    private lateinit var adapterorganisasi : OrganisasiFikomAdapter
    private lateinit var adapterKlub : KlubFikomAdapter
    private val beritaList = mutableListOf<Berita>()
    private val organisasiList = mutableListOf<Organisasi>()
    private val klubList = mutableListOf<KlubFikom>()
    private lateinit var auth: FirebaseAuth
    private var timer: Timer? = null

    override fun onItemClick(berita: Berita) {
        val intent = Intent(requireContext(), DetailBeritaKegiatan::class.java).apply {
            putExtra("title", berita.title)
            putExtra("imageUrl", berita.imageUrl)
            putExtra("waktu", berita.waktu)
            putExtra("information", berita.information)
        }
        startActivity(intent)
    }

    override fun onItemClick(organisasi: Organisasi) {
        val intent = Intent(requireContext(), DetailOrganisasiFikomActivity::class.java).apply {
            putExtra("title", organisasi.title)
            putExtra("logo", organisasi.logo)
            putExtra("detailProfil", organisasi.detailProfil)
            putExtra("image1", organisasi.image1)
            putExtra("image2", organisasi.image2)
            putExtra("image3", organisasi.image3)
            putExtra("visiMisi", organisasi.visiMisi)
            putExtra("fakultas", organisasi.fakultas)
            putExtra("strukturalImage", organisasi.strukturalImage)
            putExtra("detailTitle", organisasi.detailTitle)
            putExtra("tvProfil1", organisasi.tvProfil1)
            putExtra("tvProfil2", organisasi.tvProfil2)
            putExtra("tvProfil3", organisasi.tvProfil3)
            putExtra("tvProfil4", organisasi.tvProfil4)
            putExtra("tvProfil5", organisasi.tvProfil5)
            putExtra("tvDetailVisi", organisasi.tvDetailVisi)
            putExtra("tvDetailMisi1", organisasi.tvDetailMisi1)
            putExtra("tvDetailMisi2", organisasi.tvDetailMisi2)
            putExtra("tvDetailMisi3", organisasi.tvDetailMisi3)
            putExtra("tvDetailMisi4", organisasi.tvDetailMisi4)
            putExtra("tvDetailMisi5", organisasi.tvDetailMisi5)
            putExtra("tvAjakanProfil", organisasi.tvAjakanProfil)
        }
        startActivity(intent)
    }

    override fun onItemClick(klub: KlubFikom) {
        val intent = Intent(requireContext(), DetailKlubFikomActivity::class.java).apply {
            putExtra("title", klub.title)
            putExtra("logo", klub.logo)
            putExtra("detailProfil", klub.detailProfil)
            putExtra("image1", klub.image1)
            putExtra("image2", klub.image2)
            putExtra("image3", klub.image3)
            putExtra("visiMisi", klub.visiMisi)
            putExtra("fakultas", klub.fakultas)
            putExtra("strukturalImage", klub.strukturalImage)
            putExtra("detailTitle", klub.title)
            putExtra("tvProfil1", klub.tvProfil1)
            putExtra("tvProfil2", klub.tvProfil2)
            putExtra("tvProfil3", klub.tvProfil3)
            putExtra("tvProfil4", klub.tvProfil4)
            putExtra("tvProfil5", klub.tvProfil5)
            putExtra("tvDetailVisi", klub.tvDetailVisi)
            putExtra("tvDetailMisi1", klub.tvDetailMisi1)
            putExtra("tvDetailMisi2", klub.tvDetailMisi2)
            putExtra("tvDetailMisi3", klub.tvDetailMisi3)
            putExtra("tvDetailMisi4", klub.tvDetailMisi4)
            putExtra("tvDetailMisi5", klub.tvDetailMisi5)
            putExtra("tvAjakanProfil", klub.tvAjakanProfil)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupRVBeritaKegiatan()
        setupRVOrganisasiFikom()
        setupRVKlubFikom()
        fetchDataBeritaFromFirebase()
        fetchOrganisasiDataFromFirebase()
        fetchUserFullName()
        fetchDataKlubFromFirebase()
        runNotifikasi()
        setupSearch()
        fetchUserImage(binding.imgProfile)
        return binding.root
    }


    private fun setupSearch() {
        binding.searchBar.setOnClickListener {
            startActivity(Intent(requireActivity(), SearchActivity::class.java))
        }
    }

    private fun runNotifikasi() {
        binding.imgNotifikasi.setOnClickListener {
            startActivity(Intent(requireContext(), NotifikasiActivity::class.java))
        }
    }

    private fun setupRVKlubFikom() {
        adapterKlub = KlubFikomAdapter(klubList, this)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvKlub.layoutManager = layoutManager
        binding.rvKlub.adapter = adapterKlub

    }

    private fun setupRVOrganisasiFikom() {
        adapterorganisasi = OrganisasiFikomAdapter(organisasiList, this)
        val layoutManagerOrganisasi = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvOrganisasi.layoutManager = layoutManagerOrganisasi
        binding.rvOrganisasi.adapter = adapterorganisasi

//        setupSliderOrganisasi(layoutManagerOrganisasi)
    }

    private fun setupRVBeritaKegiatan() {
        adapterberita = BeritaOrganisasiAdapter(beritaList, this)
        val layoutManagerBerita = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvBeritaOrganisasi.layoutManager = layoutManagerBerita
        binding.rvBeritaOrganisasi.adapter = adapterberita

        setupSliderBerita(layoutManagerBerita)
    }

    private fun setupSliderBerita(layoutManagerBerita: LinearLayoutManager) {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvBeritaOrganisasi)

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentPosition = layoutManagerBerita.findLastCompletelyVisibleItemPosition()
                Log.d(TAG, "posisi sekarang: $currentPosition , jumlah data: ${adapterberita.itemCount}")

                if (currentPosition < (adapterberita.itemCount - 1)) {
                    layoutManagerBerita.smoothScrollToPosition(binding.rvBeritaOrganisasi, RecyclerView.State(), currentPosition + 1)
                } else {
                    layoutManagerBerita.smoothScrollToPosition(binding.rvBeritaOrganisasi, RecyclerView.State(), 0)
                }
            }
        }, 0, 3000)
    }

    private fun fetchDataBeritaFromFirebase() {
        database.child("BeritaKegiatan").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                beritaList.clear()
                for (dataSnapshot in snapshot.children) {
                    val title = dataSnapshot.child("title").getValue(String::class.java)
                    val imageUrl = dataSnapshot.child("image").getValue(String::class.java)
                    val waktu = dataSnapshot.child("waktu").getValue(String::class.java)
                    val information = dataSnapshot.child("information").getValue(String::class.java)
                    if (title != null && imageUrl != null && waktu != null && information != null) {
                        val berita = Berita(title, imageUrl, waktu, information)
                        beritaList.add(berita)
                    }
                }
                adapterberita.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun fetchDataKlubFromFirebase() {
        database.child("KlubFikom").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                klubList.clear()
                for (dataSnapshot in snapshot.children) {
                    val title = dataSnapshot.child("title").getValue(String::class.java)
                    val logo = dataSnapshot.child("logo").getValue(String::class.java)
                    val detailProfil = dataSnapshot.child("detailProfil").getValue(String::class.java)
                    val visiMisi = dataSnapshot.child("visiMisi").getValue(String::class.java)
                    val strukturalImage = dataSnapshot.child("strukturalImage").getValue(String::class.java)
                    val detailTitle = dataSnapshot.child("detailTitle").getValue(String::class.java)
                    val fakultas = dataSnapshot.child("fakultas").getValue(String::class.java)
                    val image1 = dataSnapshot.child("image1").getValue(String::class.java)
                    val image2 = dataSnapshot.child("image2").getValue(String::class.java)
                    val image3 = dataSnapshot.child("image3").getValue(String::class.java)
                    val tvAjakanProfil = dataSnapshot.child("tvAjakanProfil").getValue(String::class.java)
                    val tvDetailVisi = dataSnapshot.child("tvDetailVisi").getValue(String::class.java)
                    val tvDetailMisi1 = dataSnapshot.child("tvDetailMisi1").getValue(String::class.java)
                    val tvDetailMisi2 = dataSnapshot.child("tvDetailMisi2").getValue(String::class.java)
                    val tvDetailMisi3 = dataSnapshot.child("tvDetailMisi3").getValue(String::class.java)
                    val tvDetailMisi4 = dataSnapshot.child("tvDetailMisi4").getValue(String::class.java)
                    val tvDetailMisi5 = dataSnapshot.child("tvDetailMisi5").getValue(String::class.java)
                    val tvProfil1 = dataSnapshot.child("tvProfil1").getValue(String::class.java)
                    val tvProfil2 = dataSnapshot.child("tvProfil2").getValue(String::class.java)
                    val tvProfil3 = dataSnapshot.child("tvProfil3").getValue(String::class.java)
                    val tvProfil4 = dataSnapshot.child("tvProfil4").getValue(String::class.java)
                    val tvProfil5 = dataSnapshot.child("tvProfil5").getValue(String::class.java)

                    if (title != null && logo != null && fakultas != null ) {
                        val klub = KlubFikom(
                            title = title,
                            logo = logo,
                            detailProfil = detailProfil ?: "",
                            visiMisi = visiMisi ?: "",
                            strukturalImage = strukturalImage ?: "",
                            detailTitle = detailTitle ?: "",
                            fakultas = fakultas,
                            image1 = image1 ?: "",
                            image2 = image2 ?: "",
                            image3 = image3 ?: "",
                            tvAjakanProfil = tvAjakanProfil ?: "",
                            tvDetailVisi = tvDetailVisi ?: "",
                            tvDetailMisi1 = tvDetailMisi1 ?: "",
                            tvDetailMisi2 = tvDetailMisi2 ?: "",
                            tvDetailMisi3 = tvDetailMisi3 ?: "",
                            tvDetailMisi4 = tvDetailMisi4 ?: "",
                            tvDetailMisi5 = tvDetailMisi5 ?: "",
                            tvProfil1 = tvProfil1 ?: "",
                            tvProfil2 = tvProfil2 ?: "",
                            tvProfil3 = tvProfil3 ?: "",
                            tvProfil4 = tvProfil4 ?: "",
                            tvProfil5 = tvProfil5 ?: ""
                        )
                        klubList.add(klub)
                    }
                }
                adapterKlub.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }



    private fun fetchOrganisasiDataFromFirebase() {
        database.child("OrganisasiFikom").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                organisasiList.clear()
                for (dataSnapshot in snapshot.children) {
                    val title = dataSnapshot.child("title").getValue(String::class.java)
                    val logo = dataSnapshot.child("logo").getValue(String::class.java)
                    val profil = dataSnapshot.child("detailProfil").getValue(String::class.java)
                    val fakultas = dataSnapshot.child("fakultas").getValue(String::class.java)
                    val image1 = dataSnapshot.child("image1").getValue(String::class.java)
                    val image2 = dataSnapshot.child("image2").getValue(String::class.java)
                    val image3 = dataSnapshot.child("image3").getValue(String::class.java)
                    val strukturalImage = dataSnapshot.child("strukturalImage").getValue(String::class.java)
                    val visiMisi = dataSnapshot.child("visiMisi").getValue(String::class.java)
                    val detailTitle = dataSnapshot.child("detailTitle").getValue(String::class.java)
                    val tvProfil1 = dataSnapshot.child("tvProfil1").getValue(String::class.java)
                    val tvProfil2 = dataSnapshot.child("tvProfil2").getValue(String::class.java)
                    val tvProfil3 = dataSnapshot.child("tvProfil3").getValue(String::class.java)
                    val tvProfil4 = dataSnapshot.child("tvProfil4").getValue(String::class.java)
                    val tvProfil5 = dataSnapshot.child("tvProfil5").getValue(String::class.java)
                    val ajakanProfil = dataSnapshot.child("tvAjakanProfil").getValue(String::class.java)
                    val detailVisi = dataSnapshot.child("tvDetailVisi").getValue(String::class.java)
                    val tvDetailMisi1 = dataSnapshot.child("tvDetailMisi1").getValue(String::class.java)
                    val tvDetailMisi2 = dataSnapshot.child("tvDetailMisi2").getValue(String::class.java)
                    val tvDetailMisi3 = dataSnapshot.child("tvDetailMisi3").getValue(String::class.java)
                    val tvDetailMisi4 = dataSnapshot.child("tvDetailMisi4").getValue(String::class.java)
                    val tvDetailMisi5 = dataSnapshot.child("tvDetailMisi5").getValue(String::class.java)

                    if (title != null && logo != null && detailTitle != null && fakultas != null) {
                        val organisasi = Organisasi(
                            title = title,
                            logo = logo,
                            detailProfil = profil ?: "",
                            fakultas = fakultas,
                            image1 = image1 ?: "",
                            image2 = image2 ?: "",
                            image3 = image3 ?: "",
                            strukturalImage = strukturalImage ?: "",
                            visiMisi = visiMisi ?: "",
                            detailTitle = detailTitle,
                            tvProfil1 = tvProfil1 ?: "",
                            tvProfil2 = tvProfil2 ?: "",
                            tvProfil3 = tvProfil3 ?: "",
                            tvProfil4 = tvProfil4 ?: "",
                            tvProfil5 = tvProfil5 ?: "",
                            tvAjakanProfil = ajakanProfil ?: "",
                            tvDetailVisi = detailVisi ?: "",
                            tvDetailMisi1 = tvDetailMisi1 ?: "",
                            tvDetailMisi2 = tvDetailMisi2 ?: "",
                            tvDetailMisi3 = tvDetailMisi3 ?: "",
                            tvDetailMisi4 = tvDetailMisi4 ?: "",
                            tvDetailMisi5 = tvDetailMisi5 ?: ""
                        )
                        organisasiList.add(organisasi)
                    }
                }
                Log.d("HomeFragment", "Total organisasi: ${organisasiList.size}")
                adapterorganisasi.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun fetchUserFullName() {
        val user = auth.currentUser
        if (user != null) {
            val userRef = database.child("Users").child(user.uid)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val namaLengkap = snapshot.child("namaLengkap").getValue(String::class.java)
                        val kataPertama = namaLengkap!!.split(" ")[0]
                        if (kataPertama != null) {
                            binding.tvUsername.text = kataPertama
                        } else {
                            binding.tvUsername.text = "Nama tidak ditemukan"
                        }
                    } else {
                        binding.tvUsername.text = "Pengguna tidak ditemukan"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    binding.tvUsername.text = "Gagal mengambil data"
                }
            })
        } else {
            binding.tvUsername.text = "Pengguna tidak login"
        }
    }

    private fun fetchUserImage(imageView: ImageView) {
        val user = auth.currentUser
        if (user != null) {
            val userRef = database.child("Users").child(user.uid)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val imageUrl = snapshot.child("imageUrl").getValue(String::class.java)
                        if (imageUrl != null && imageUrl.isNotEmpty()) {
                            // Load the image using Glide
                            Glide.with(imageView.context)
                                .load(imageUrl)
                                .into(imageView)
                        } else {
                            imageView.setImageResource(R.drawable.avatar)
                        }
                    } else {

                        imageView.setImageResource(R.drawable.avatar)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to fetch user image: ${error.message}")
                    imageView.setImageResource(R.drawable.avatar)
                }
            })
        } else {
            imageView.setImageResource(R.drawable.avatar)
        }
    }

}