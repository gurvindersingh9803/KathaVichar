package com.example.kathavichar.repositories

import android.util.Log
import com.example.kathavichar.model.Artist
import com.example.kathavichar.model.DataModel
import com.example.kathavichar.model.Section
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
        databaseReference = firebaseDatabase!!.getReference("items")
    }

    fun getdata(): Single<Section> = Single.create { emiter ->
        val list = mutableListOf<Artist>()

        try {
            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                  /*  snapshot.children.forEach {
                        val a = it.

                        Log.i("sghsdgh", a.toString())

                        *//*if (a != null) {
                            list.add(a)
                        }*//*
                    }*/
                    // Log.i("sghsdgh", "")
                    // emiter.onSuccess(list)

                    val a1 = snapshot.value

                    Log.i("ghghgh", a1.toString())

                    val a = snapshot.children.forEach {
                        Log.i("sghsdgh", it.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // calling on cancelled method when we receive
                    // any error or we are not able to get the data.
                    emiter.onError(Throwable(error.message))
                }
            })
        } catch (e: Exception) {
            emiter.onError(e)
        }
    }
}
