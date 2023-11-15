package com.example.kathavichar.repositories

import com.example.kathavichar.model.Category
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.gson.Gson
import io.reactivex.Single
import org.koin.java.KoinJavaComponent.inject

class FirebaseTestRepo {
    var firebaseDatabase: FirebaseDatabase? = null

    private val gson: Gson by inject(Gson::class.java)
    var databaseReference: DatabaseReference? = null
    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("categories")
    }

    fun getdata(): Single<List<Category>> = Single.create { emiter ->
        val list = mutableListOf<Category>()

        try {
            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val a = it.getValue(Category::class.java)
                        if (a != null) {
                            list.add(a)
                        }
                    }
                    emiter.onSuccess(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    // calling on cancelled method when we receive
                    // any error or we are not able to get the data.
                    emiter.onError(Throwable(error.message))
                }
            })
        }catch (e: Exception){
            emiter.onError(e)
        }
    }
}
