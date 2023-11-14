package com.example.kathavichar.repositories

import com.example.kathavichar.model.Category
import com.example.kathavichar.network.ServerResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class FirebaseTestRepo {
    var firebaseDatabase: FirebaseDatabase? = null

    private val gson: Gson by inject(Gson::class.java)
    var databaseReference: DatabaseReference? = null

    private val _uiState: MutableStateFlow<ServerResponse<List<Category>>> = MutableStateFlow(ServerResponse.isLoading(null))
    val uiState: StateFlow<ServerResponse<List<Category>>> = _uiState

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("categories")
    }

    suspend fun getdata(){
        val list = mutableListOf<Category>()

        withContext(Dispatchers.Default) {
            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val a = it.getValue(Category::class.java)
                        list.add(a!!)
                    }

                    CoroutineScope(Dispatchers.Default).launch {
                        _uiState.emit(ServerResponse.onSuccess(list))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // calling on cancelled method when we receive
                    // any error or we are not able to get the data.
                }
            })
        }
    }
}
