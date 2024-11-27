package com.example.eggenda.ui.ranks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.databinding.FragmentRankBinding
import com.example.eggenda.ui.database.userDatabase.UserFB
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RankFragment: Fragment() {
    private var _binding: FragmentRankBinding? = null
    private val binding get() = _binding!!

    private lateinit var FBdatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val list = mutableListOf<UserFB>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRankBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adapter = UserAdapter(list)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter


        //load database to retrieve gained points
        FBdatabase = FirebaseDatabase.getInstance()
        myRef = FBdatabase.reference.child("users")

        viewLifecycleOwner.lifecycleScope.launch {
            getUsers()
        }

        return root
    }

    private suspend fun getUsers() {
        val query = myRef.orderByChild("points")
        suspendCancellableCoroutine { continuation ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(UserFB::class.java)
                        if (user != null) {
                            list.add(user)
                        }
                    }
                    list.sortByDescending { it.points }  // Sort by points in descending order
                    adapter.notifyDataSetChanged()
                    continuation.resume(Unit)  // Resume when the data is loaded
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(Exception("Failed to get users: ${error.message}"))
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}