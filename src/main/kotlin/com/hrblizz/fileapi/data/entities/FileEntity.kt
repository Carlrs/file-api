package com.hrblizz.fileapi.data.entities

import org.bson.types.Binary
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "files")
class FileEntity : Entity(
) {
    lateinit var content: Binary
    lateinit var contentType: String
    lateinit var meta: String
    lateinit var source: String
    lateinit var createTime: LocalDate
    lateinit var expireTime: LocalDate
    fun getSizeString(): String {
        return content.data.size.toString()
    }
    fun getDateString(): String {
        return createTime.toString()
    }
}
