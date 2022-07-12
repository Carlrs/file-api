package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.controller.exception.BadRequestException
import com.hrblizz.fileapi.data.entities.FileEntity
import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.rest.ErrorMessage
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
class FileUploadController(
    private val entityRepository: EntityRepository<FileEntity>
) {
    @PostMapping("/files", consumes = ["multipart/form-data"] )
    fun postFile(@RequestPart("content") content: MultipartFile?,
                 @RequestPart("contentType") contentType: String?,
                 @RequestPart("source") source: String?,
                 @RequestPart("expireTime") expireTime: String?,
                 @RequestPart("meta") meta: String?,
                 @RequestPart("name") fileName: String?
                ): ResponseEntity<Map<String, Any>> {
        var status = HttpStatus.CREATED
        val token = UUID.randomUUID().toString()
        val errorList = ArrayList<ErrorMessage>()

        try {
            entityRepository.save(FileEntity().also {
                if (isNull(content)) throw BadRequestException("Missing file")
                it.name = token
                it.value = fileName ?: content?.originalFilename ?: token
                it.contentType = contentType ?:
                    throw BadRequestException("Missing content type")
                it.source = source ?:
                    throw BadRequestException("Missing file source")
                it.expireTime = LocalDate.parse(expireTime ?: "1970-01-01")
                it.createTime = LocalDate.now()
                it.meta = meta ?:
                    throw BadRequestException("Missing file metadata")
                it.content = Binary(BsonBinarySubType.BINARY, content!!.bytes)
            })
        } catch (e: BadRequestException) {
            status = HttpStatus.BAD_REQUEST
            errorList.add(ErrorMessage(e.message ?: "Unknown Issue with request"))
        }
        catch (e: Exception) {
            status = HttpStatus.SERVICE_UNAVAILABLE
            errorList.add(ErrorMessage(e.message ?: "Unknown Error"))
        }

        return ResponseEntity(
            mapOf(
                "ok" to (status == HttpStatus.CREATED),
                "token" to if (status == HttpStatus.CREATED) token else "null",
            ),
            errorList,
            status.value()
        )
    }
}
