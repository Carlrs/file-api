package com.hrblizz.fileapi.data.repository

import com.hrblizz.fileapi.data.entities.Entity
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EntityRepository<T> : MongoRepository<Entity, String> {
    @Query(value = "{ '_id' : {'\$in' : ?0 } }")
    fun findByIdIn(UUIDs: Iterable<ObjectId>) : Iterable<T?>
}

