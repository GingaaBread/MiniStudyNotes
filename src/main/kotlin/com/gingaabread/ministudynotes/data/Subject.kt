package com.gingaabread.ministudynotes.data

import org.bson.types.ObjectId

/**
 *  The [Subject] class is used to provide a group for the user to manage different subjects (for example, different
 *  languages, scientific disciplines, programming study sets, etc.), which are parent objects containing the actual
 *  [StudyNote] objects.
 */
data class Subject(

    /**
     *  The automatically generated MongoDB object id identifying the subject
     */
    private val id: ObjectId = ObjectId(),

    /**
     *  The name of the subject specified by the user.
     *  At the moment, there are no restrictions to the name
     */
    var name: String,

    /**
     *  The list of notes that the user has saved in the subject.
     *  When creating a new subject, the list is initially empty.
     */
    val notes: List<StudyNote> = emptyList()

)
