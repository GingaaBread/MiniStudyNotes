package com.gingaabread.ministudynotes.repositories

import com.gingaabread.ministudynotes.data.Subject
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SubjectRepository : MongoRepository<Subject, ObjectId> {
    fun findById() : Subject?
}