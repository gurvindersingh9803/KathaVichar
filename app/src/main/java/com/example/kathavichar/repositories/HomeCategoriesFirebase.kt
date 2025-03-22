package com.example.kathavichar.repositories

import com.example.kathavichar.model.ArtistData
import com.example.kathavichar.model.ArtistSummary
import com.example.kathavichar.model.Section
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import org.koin.java.KoinJavaComponent.inject
import java.lang.reflect.Type

class HomeCategoriesFirebase {
    var firebaseDatabase: FirebaseDatabase? = null

    private lateinit var artistsDataRepository: ArtistsDataRepository
    private val gson: Gson by inject(Gson::class.java)
    var databaseReference: DatabaseReference? = null

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.reference
       // artistsDataRepository.fetchArtists()
    }

    fun getdata(): Single<MutableList<Section>> =
        Single.create { emitter ->
            try {
                databaseReference!!.addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val gson = GsonBuilder()
                                .registerTypeAdapter(ArtistData::class.java, ArtistDataDeserializer())
                                .create()

                            val list = mutableListOf<Section>() // Ensure the list is re-initialized each time.

                            snapshot.children.forEach { child ->
                                try {
                                    val json = gson.toJson(child.value) // Convert the child value to JSON string.
                                    val section = gson.fromJson(json, Section::class.java) // Deserialize JSON to Section.
                                    list.add(section) // Add to the list.
                                } catch (e: Exception) {
                                    println("Error parsing section: ${e.message}")
                                }
                            }

                            println("Parsed List: $list")
                            emitter.onSuccess(list) // Emit the final list.
                        }

                        override fun onCancelled(error: DatabaseError) {
                            emitter.onError(Throwable(error.message)) // Handle errors.
                        }
                    },
                )
            } catch (e: Exception) {
                println("Unexpected Error: ${e.message}")
                emitter.onError(e) // Handle unexpected errors.
            }
        }
}

class ArtistDataDeserializer : JsonDeserializer<ArtistData> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): ArtistData {
        return if (json.isJsonObject) {
            val artistsMap = context.deserialize<Map<String, ArtistSummary>>(
                json,
                object : TypeToken<Map<String, ArtistSummary>>() {}.type,
            )
            ArtistData.ArtistsMap(artistsMap)
        } else if (json.isJsonArray) {
            val othersList = context.deserialize<List<ArtistSummary>>(
                json,
                object : TypeToken<List<ArtistSummary>>() {}.type,
            )
            ArtistData.OthersList(othersList)
        } else {
            throw JsonParseException("Unexpected JSON type for ArtistData")
        }
    }
}
