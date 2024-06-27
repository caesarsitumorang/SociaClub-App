package com.rpl.sicfo.ui.voting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rpl.sicfo.R
import com.rpl.sicfo.adapter.OrganisasiFikomAdapter
import com.rpl.sicfo.adapter.PendaftaranAdapter
import com.rpl.sicfo.data.KlubFikom
import com.rpl.sicfo.data.Organisasi
import com.rpl.sicfo.databinding.FragmentVotingBinding
import com.rpl.sicfo.ui.voting.detailVoting.VotingActivity
import com.rpl.sicfo.ui.voting.pendaftaran.PendaftaranActivity

class VotingFragment : Fragment(), OrganisasiFikomAdapter.OnItemClickListener, PendaftaranAdapter.OnItemClickListener {

    private lateinit var binding: FragmentVotingBinding
    private val organisasiList = mutableListOf<Organisasi>()
    private val klubList = mutableListOf<KlubFikom>()
    private lateinit var auth: FirebaseAuth
    private lateinit var adapterOrganisasi: OrganisasiFikomAdapter
    private lateinit var database: DatabaseReference
    private lateinit var pendaftaranAdapter: PendaftaranAdapter
    private val dataList = mutableListOf<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentVotingBinding.inflate(inflater, container, false)

        setupRVOrganisasiFikom()
        setupRVPendaftaran()
        fetchDataFromFirebase()
        return binding.root
    }

    private fun setupRVOrganisasiFikom() {
        adapterOrganisasi = OrganisasiFikomAdapter(organisasiList, this)
        val layoutManagerOrganisasi =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvVoting.layoutManager = layoutManagerOrganisasi
        binding.rvVoting.adapter = adapterOrganisasi
    }

    private fun setupRVPendaftaran() {
        pendaftaranAdapter = PendaftaranAdapter(dataList, this)
        val layoutManagerPendaftaran =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvPendaftaran.layoutManager = layoutManagerPendaftaran
        binding.rvPendaftaran.adapter = pendaftaranAdapter
    }


    private fun fetchDataFromFirebase() {
        fetchOrganisasiDataFromFirebase()
        fetchKlubDataFromFirebase()
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
                    val strukturalImage =
                        dataSnapshot.child("strukturalImage").getValue(String::class.java)
                    val visiMisi = dataSnapshot.child("visiMisi").getValue(String::class.java)
                    val detailTitle = dataSnapshot.child("detailTitle").getValue(String::class.java)

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
                            detailTitle = detailTitle
                        )
                        organisasiList.add(organisasi)
                        dataList.add(organisasi)
                    }
                }
                Log.d("VotingFragment", "Total organisasi: ${organisasiList.size}")
                adapterOrganisasi.notifyDataSetChanged()
                pendaftaranAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun fetchKlubDataFromFirebase() {
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
                        dataList.add(klub) // Tambahkan ke dataList untuk pendaftaran
                    }
                }
                Log.d("VotingFragment", "Total klub: ${klubList.size}")
                pendaftaranAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }


    override fun onItemClick(item: Any) {
        when (item) {
            is Organisasi -> {
                val intent = Intent(context, PendaftaranActivity::class.java)
                intent.putExtra("organisasi_title", item.title)
                intent.putExtra("logo_organisasi", item.logo)
                startActivity(intent)
            }
            is KlubFikom -> {
                val intent = Intent(context, PendaftaranActivity::class.java)
                intent.putExtra("organisasi_title", item.title)
                intent.putExtra("logo_organisasi", item.logo)
                startActivity(intent)
            }
            else -> {
                // Handle jika tipe item tidak dikenali
                Log.e("VotingFragment", "Unknown item type clicked: ${item.javaClass.simpleName}")
            }
        }
    }


    override fun onItemClick(organisasi: Organisasi) {
        if (organisasi is Organisasi) {
            val intent = Intent(context, VotingActivity::class.java)
            intent.putExtra("organisasi_title", organisasi.title)
            startActivity(intent)
        }
    }
}
