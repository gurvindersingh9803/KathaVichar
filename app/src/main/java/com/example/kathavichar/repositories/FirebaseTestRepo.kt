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
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import org.koin.java.KoinJavaComponent.inject

class FirebaseTestRepo {
    var firebaseDatabase: FirebaseDatabase? = null

    private val gson: Gson by inject(Gson::class.java)
    var databaseReference: DatabaseReference? = null
    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("")
    }

    fun getdata(): Single<Section> = Single.create { emiter ->
        val list = mutableListOf<SectionData>()

        try {
            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                     val jsonString = gson.toJson(snapshot.value)
                    Log.i("esssf", gson.fromJson(jsonString, Data::class.java).toString())


                    val a = snapshot.children.forEach {
                        // Log.i("sdfsadv", gson.toJson(it.getValue()) .toString())
                        Log.i("ef", gson.fromJson(gson.toJson(it.getValue()) .toString(), SectionData::class.java).toString())

                        val a= gson.fromJson(gson.toJson(it.getValue()) .toString(), SectionData::class.java)
                        list.add(a)
                    }

                    println(list)



                    // val artistsResponseList = Gson().fromJson(jsonString, ArtistsResponse::class.java)
                    Log.i("ghghgh", a.toString())

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

data class Artist(var image: String = "", var name: String = "")

data class SectionData(var sectionName: String = "", var data: List<Artist> = listOf())

data class Data(var data: List<SectionData> = listOf())


