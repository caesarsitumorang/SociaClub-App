package com.rpl.sicfo.ui.home.pencarian

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.TextAppearanceSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.rpl.sicfo.R
import com.rpl.sicfo.adapter.SearchAdapter
import com.rpl.sicfo.data.KlubFikom
import com.rpl.sicfo.data.Organisasi
import com.rpl.sicfo.databinding.ActivitySearchBinding
import com.rpl.sicfo.ui.klubFikom.DetailKlubFikomActivity
import com.rpl.sicfo.ui.organisasiFikom.DetailOrganisasiFikomActivity
import java.util.*

class SearchActivity : AppCompatActivity(), SearchAdapter.OnItemClickListener {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private val dataList = mutableListOf<Any>()
    private val originalList = mutableListOf<Any>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        setupToolbar()
        setupRecyclerView()
        setupSearchBar()
        fetchDataFromFirebase()
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter(dataList, this)
        binding.rvPencarian.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchAdapter
        }
    }

    private fun setupSearchBar() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            val title = "Pencarian"
            val spannableTitle = SpannableString(title)
            spannableTitle.setSpan(
                TextAppearanceSpan(this@SearchActivity, R.style.textColorTitleWelcome),
                0,
                title.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            supportActionBar?.title = spannableTitle
        }
    }

    private fun fetchDataFromFirebase() {
        fetchDataOrganisasiFromFirebase()
        fetchDataKlubFromFirebase()
    }

    private fun fetchDataOrganisasiFromFirebase() {
        database.child("OrganisasiFikom").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                originalList.clear()
                for (dataSnapshot in snapshot.children) {
                    val title = dataSnapshot.child("title").getValue(String::class.java)
                    val logo = dataSnapshot.child("logo").getValue(String::class.java)
                    val profil = dataSnapshot.child("profil").getValue(String::class.java)
                    val fakultas = dataSnapshot.child("fakultas").getValue(String::class.java)
                    val image1 = dataSnapshot.child("image1").getValue(String::class.java)
                    val image2 = dataSnapshot.child("image2").getValue(String::class.java)
                    val image3 = dataSnapshot.child("image3").getValue(String::class.java)
                    val strukturalImage = dataSnapshot.child("strukturalImage").getValue(String::class.java)
                    val visiMisi = dataSnapshot.child("visiMisi").getValue(String::class.java)
                    val detailTitle = dataSnapshot.child("detailTitle").getValue(String::class.java)

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
                            detailTitle = detailTitle
                        )
                        originalList.add(organisasi)
                    }
                }
                updateDataList()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun fetchDataKlubFromFirebase() {
        database.child("KlubFikom").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val title = dataSnapshot.child("title").getValue(String::class.java)
                    val imageUrl = dataSnapshot.child("image").getValue(String::class.java)
                    val image1 = dataSnapshot.child("image1").getValue(String::class.java)
                    if (title != null && imageUrl != null && image1 != null) {
                        val klub = KlubFikom(title, imageUrl, image1)
                        originalList.add(klub)
                    }
                }
                updateDataList()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun updateDataList() {
        dataList.clear()
        dataList.addAll(originalList)
        searchAdapter.notifyDataSetChanged()
    }

    private fun performSearch(query: String) {
        val searchQuery = query.toLowerCase(Locale.getDefault())
        dataList.clear()
        if (searchQuery.isEmpty()) {
            dataList.addAll(originalList)
        } else {
            val filteredList = originalList.filter {
                when (it) {
                    is KlubFikom -> it.title.toLowerCase(Locale.getDefault()).contains(searchQuery)
                    is Organisasi -> it.title.toLowerCase(Locale.getDefault()).contains(searchQuery)
                    else -> false
                }
            }
            dataList.addAll(filteredList)
        }
        searchAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(item: Any) {
        when (item) {
            is KlubFikom -> {
                val intent = Intent(this, DetailKlubFikomActivity::class.java).apply {
                    putExtra("title", item.title)
                    putExtra("image", item.image)
                    putExtra("image1", item.image1)
                }
                startActivity(intent)
            }
            is Organisasi -> {
                val intent = Intent(this, DetailOrganisasiFikomActivity::class.java).apply {
                    putExtra("title", item.title)
                    putExtra("logo", item.logo)
                    putExtra("profil", item.profil)
                    putExtra("image1", item.image1)
                    putExtra("image2", item.image2)
                    putExtra("image3", item.image3)
                    putExtra("visiMisi", item.visiMisi)
                    putExtra("fakultas", item.fakultas)
                    putExtra("strukturalImage", item.strukturalImage)
                    putExtra("anggota", item.anggota)
                    putExtra("detailTitle", item.detailTitle)
                }
                startActivity(intent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
