package com.gingaabread.ministudynotes.data

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate

/**
 *  The [User] class describes the user of the application who can create any amount of subjects and study notes.
 *  When instantiating a user, the [id] is generated automatically. The current local date on the server is
 *  also taken as the [creationDate]. During the creation of the account, the user merely has to provide a
 *  [username], an [email] and a password.
 *
 *  TODO: add password
 */
@Document("User")
data class User(

    /**
     *  The automatically generated MongoDB object id identifying the user.
     *  Created on instantiation.
     */
    @Id
    private val id: ObjectId = ObjectId(),

    /**
     *  The date of the account creation.
     *  Created automatically on instantiation using the server's local date.
     */
    private val creationDate: LocalDate = LocalDate.now(),

    /**
     *  The unique name of the user provided during account creation.
     *  At the moment, there are no restrictions to the username except for uniqueness.
     */
    val username: String,

    /**
     *  The email address of the user provided during account creation.
     *  For UX reasons, there is no pattern matched against the user input.
     *  Instead, the user will have to confirm their mail address by the use of a confirmation mail.
     */
    val email: String

)
