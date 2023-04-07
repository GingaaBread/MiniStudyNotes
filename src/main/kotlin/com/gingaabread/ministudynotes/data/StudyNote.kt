package com.gingaabread.ministudynotes.data

import java.time.LocalDate

data class StudyNote (

    private val creationDate: LocalDate = LocalDate.now(),

    var isFavourite: Boolean = false,

    var content: String,

    var colour: String

)
