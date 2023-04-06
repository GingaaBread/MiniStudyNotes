package com.gingaabread.ministudynotes.data

import jakarta.persistence.Id
import org.bson.types.ObjectId
import java.awt.Color
import java.time.LocalDate
import java.util.Date

data class StudyNote (

    @Id
    private val id: ObjectId = ObjectId(),

    private val creationDate: LocalDate = LocalDate.now(),

    private val isFavourite: Boolean = false,

    val content: String,

    val colour: Color

)
