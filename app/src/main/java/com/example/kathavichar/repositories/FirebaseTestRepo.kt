package com.example.kathavichar.repositories

import android.util.Log
import com.example.kathavichar.model.Category
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class FirebaseTestRepo {
    var firebaseDatabase: FirebaseDatabase? = null

    private val gson: Gson by inject(Gson::class.java)

    // creating a variable for our
    // Database Reference for Firebase.
    var databaseReference: DatabaseReference? = null

    init {
        // below line is used to get the instance
        // of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance()

        // below line is used to get
        // reference for our database.
        databaseReference = firebaseDatabase!!.getReference("categories")
    }

    suspend fun getdata(): List<Category> {
        // calling add value event listener method
        // for getting the values from database.
        val list: MutableList<Category> = mutableListOf()

        withContext(Dispatchers.Default) {
            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach {
                        val a = it.getValue(Category::class.java)

                        list.add(a!!)
                    }
                    Log.i("gfhteryhtrhyde", list.toString())

                    // after getting the value we are setting
                    // our value to our text view in below line.
                }

                override fun onCancelled(error: DatabaseError) {
                    // calling on cancelled method when we receive
                    // any error or we are not able to get the data.
                }

            })

        }
        return list

    }
}
