package com.example.firestoreproject.classes

import com.google.firebase.firestore.Exclude

data class Note(val title:String, val description:String, val priority: Int) {
    constructor(): this("","", 0) //Firebase needs public no-argument constructor

    @Exclude
    var id: String = ""
}