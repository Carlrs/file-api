package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.data.entities.FileEntity
import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.library.log.LogItem
import com.hrblizz.fileapi.library.log.Logger
import com.hrblizz.fileapi.rest.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FileDeleteController(private val entityRepository: EntityRepository<FileEntity>) {
    @DeleteMapping("/file/{token}")
    fun deleteFile(@PathVariable token: String): ResponseEntity<Map<String, Any?>> {
        var success = false
        var errorText = "No errors"
        try {
            entityRepository.deleteById(token)
            success = true
        }
        catch (e: Exception) {
            val logger = Logger()
            logger.error(LogItem("Failed to delete file: ${e.message}"))
            errorText = "Failed to delete file."
        }
        return ResponseEntity(
            mapOf(
                "ok" to success,
                "error" to errorText
            ), if (success) HttpStatus.OK.value() else HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
    }
}