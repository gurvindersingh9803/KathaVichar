package com.example.kathavichar.repositories

import com.example.kathavichar.model.SectionData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import io.reactivex.Single
import org.koin.java.KoinJavaComponent.inject

class HomeCategoriesFirebase {
    var firebaseDatabase: FirebaseDatabase? = null

    private val gson: Gson by inject(Gson::class.java)
    var databaseReference: DatabaseReference? = null
    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.reference
    }

    fun getdata(): Single<MutableList<SectionData>> = Single.create { emitter ->
        val list = mutableListOf<SectionData>()
        try {
            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val a = gson.fromJson(gson.toJson(it.getValue()).toString(), SectionData::class.java)
                        list.add(a)
                    }
                    println(list)
                    emitter.onSuccess(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    // calling on cancelled method when we receive
                    // any error or we are not able to get the data.
                    emitter.onError(Throwable(error.message))
                }
            })
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }
}
