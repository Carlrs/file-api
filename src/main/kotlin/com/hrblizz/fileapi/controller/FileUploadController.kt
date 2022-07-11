package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.controller.exception.BadRequestException
import com.hrblizz.fileapi.data.entities.FileEntity
import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.rest.ResponseEntity
import org.bson.BsonBinarySubType
import org.bson.types.Binary
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.*
import java.util.Objects.isNull

@RestController
class FileController(
    private val entityRepository: EntityRepository<Any?>
) {
    @PostMapping("/files", consumes = ["multipart/form-data"] )
    fun postFile(@RequestPart("content") content: MultipartFile?,
                 @RequestPart("contentType") contentType: String?,
                 @RequestPart("source") source: String?,
                 @RequestPart("expireTime") expireTime: String?,
                 @RequestPart("meta") meta: String?
                ): ResponseEntity<Map<String, Any>> {
        var status = HttpStatus.CREATED
        val token = UUID.randomUUID().toString()
        var errorText = "No errors"

        try {
            entityRepository.save(FileEntity().also {
                if (isNull(content)) throw BadRequestException("Missing file")
                it.name = token
                it.value = content?.name ?: token
                it.contentType = contentType ?:
                    throw BadRequestException("Missing content type")
                it.source = source ?:
                    throw BadRequestException("Missing file source")
                it.expireTime = LocalDate.parse(expireTime ?: "1970-01-01")
                it.meta = meta ?:
                    throw BadRequestException("Missing file metadata")
                it.content = Binary(BsonBinarySubType.BINARY, content!!.bytes)
            })
        } catch (e: BadRequestException) {
            status = HttpStatus.BAD_REQUEST
            errorText = e.message ?: "Unknown Error"
        }
        catch (e: Exception) {
            status = HttpStatus.SERVICE_UNAVAILABLE
            errorText = e.message ?: "Unknown Error"
        }

        return ResponseEntity(
            mapOf(
                "ok" to (status == HttpStatus.CREATED),
                "token" to if (status == HttpStatus.CREATED) token else "null",
                "error" to errorText
            ),
            status.value()
        )
    }
    @GetMapping("/file/{token}")
    fun getFile(@PathVariable token: String): ResponseEntity<Map<String, Any>> {
        val file: FileEntity = entityRepository.findByUUID(token) as FileEntity
        return ResponseEntity(
            mapOf(
                "ok" to true,
                "file" to file.content
            ),
            HttpStatus.OK.value()
        )
    }
}
