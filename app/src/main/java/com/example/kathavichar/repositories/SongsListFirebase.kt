package com.example.kathavichar.repositories

import android.util.Log
import com.example.kathavichar.model.Song
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import io.reactivex.Single
import org.koin.java.KoinJavaComponent

class SongsListFirebase {

    var firebaseDatabase: FirebaseDatabase? = null

    private val gson: Gson by KoinJavaComponent.inject(Gson::class.java)
    var databaseReference: DatabaseReference? = null

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.reference
    }

    fun getSongsList(): Single<MutableList<Song>> = Single.create { emitter ->
        val list = mutableListOf<Song>()
        Log.i("ssdffff", list.toString())
        try {
            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        Log.i("ghsdgdf", it.toString())



                    }

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
