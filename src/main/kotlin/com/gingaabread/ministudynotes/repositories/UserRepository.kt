package com.gingaabread.ministudynotes.repositories

import com.gingaabread.ministudynotes.data.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, ObjectId> {

    fun findByUsername(username: String) : User?

    fun findByEmail(email: String) : User?

    fun existsByUsername(username: String) : Boolean

    fun existsByEmail(email: String) : Boolean

}