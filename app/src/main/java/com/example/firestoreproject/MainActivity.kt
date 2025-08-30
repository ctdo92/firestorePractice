package com.example.firestoreproject

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.StateSet
import androidx.constraintlayout.widget.StateSet.TAG
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.TAG
import com.example.firestoreproject.classes.Note
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextPriority: EditText
    private lateinit var saveButton: Button
    private lateinit var loadButton: Button

    private lateinit var textViewData: TextView

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val docRef: DocumentReference = db.collection("Notebook").document("My first note")
    private val noteBookRef: CollectionReference = db.collection("Notebook")
    private var lastResult: DocumentSnapshot? = null


    private val KEY_TITLE = "title"
    private val KEY_DESCRIPTION = "description"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        saveButton = findViewById(R.id.addButton)
        loadButton = findViewById(R.id.loadButton)
        //deleteDescriptionButton = findViewById(R.id.button_delete_description)
        //deleteNoteButton = findViewById(R.id.button_delete_note)
        textViewData = findViewById(R.id.textViewData)
        editTextPriority = findViewById(R.id.editTextPriority)
        //updateTitleButton = findViewById(R.id.button_update_title)

        saveButton.setOnClickListener {
            addNote()
        }
        loadButton.setOnClickListener {
            loadNote()
        }
        executeBatch()


    }

    override fun onStart() {
        super.onStart()




//        noteBookRef.orderBy("priority")
//            .addSnapshotListener(this) {snapshot, error ->
//                error?.let {
//                    return@addSnapshotListener
//                }
//                snapshot?.let {
//                    for(dc in it.documentChanges) {
//                        val id = dc.document.id
//                        val oldIndex = dc.oldIndex
//                        val newIndex = dc.newIndex
//
//                        when(dc.type) {
//                            DocumentChange.Type.ADDED -> {
//                                textViewData.append("\nAdded: $id" +
//                                                "\nOld Index: $oldIndex New Index: $newIndex")
//                            }
//                            DocumentChange.Type.REMOVED -> {
//                                textViewData.append("\nRemoved: $id" +
//                                        "\nOld Index: $oldIndex New Index: $newIndex")
//                            }
//                            DocumentChange.Type.MODIFIED -> {
//                                textViewData.append("\nModified: $id" +
//                                        "\nOld Index: $oldIndex New Index: $newIndex")
//                            }
//
//                        }
//                    }
//                }
//            }


    }



    private fun addNote() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()

        if(editTextPriority.text.toString().isEmpty()) {
            editTextPriority.setText("0")
        }

        val priority = editTextPriority.text.toString().toInt()

        val note = Note(title, description, priority)
        noteBookRef.add(note)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note added to DataBase", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
                Toast.makeText(
                    this@MainActivity,
                    "Error note was not added to DataBase",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun loadNote() {
        val query = if (lastResult == null){
            noteBookRef.orderBy("priority").limit(3)
        } else {
            noteBookRef.orderBy("priority")
                .startAfter(lastResult as DocumentSnapshot).limit(3)
        }

        query.get().addOnSuccessListener {
                var data = ""
                for (queryDocument in it) {
                    val note = queryDocument.toObject(Note::class.java)
                    note.id = queryDocument.id
                    val title = note.title
                    val description = note.description
                    val priority = note.priority
                    val id = note.id

                    data += "ID: $id\nTitle: $title \nDescription: $description \nPriority: $priority\n\n"
                }
                if (it.size() > 0) {
                    data += "--------------\n\n"
                    textViewData.append(data)
                    lastResult = it.documents[it.size() - 1]
                }
            }
    }

    private fun executeBatch(){
        val batch = db.batch()

        val doc1 = noteBookRef.document("New note")
        batch.set(doc1, Note("New note", "New Description", priority = 1))

        val doc2 = noteBookRef.document("A3p5wykwddfXCu4ygpfN")
        batch.update(doc2, "Title", "Updated note")

        val doc3 = noteBookRef.document("ChT0qOXfsazSkE5mCUJ7")
        batch.delete(doc3)

        val doc4 = noteBookRef.document()
        batch.set(doc4, Note("added note", "added description", priority = 1))

        batch.commit().addOnFailureListener {
            textViewData.text = it.toString()
        }
    }

}