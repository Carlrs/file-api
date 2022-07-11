package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.data.entities.FileEntity
import com.hrblizz.fileapi.data.repository.EntityRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity as SpringResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.URLConnection

@RestController
class FileDownloadController(private val entityRepository: EntityRepository<FileEntity>) {

    @GetMapping("/file/{token}",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getFile(@PathVariable token: String): SpringResponseEntity<Any> {
        val file = entityRepository.findByIdOrNull(token) as FileEntity?
        return if (file != null) {
            val headers = generateHeaders(file)
            SpringResponseEntity(
                file.content.data,
                headers,
                HttpStatus.OK.value()
            )
        } else {
            SpringResponseEntity("File not found", HttpStatus.NOT_FOUND)
        }
    }

    fun generateHeaders(file: FileEntity): HttpHeaders {
        val headers = HttpHeaders()
        val fileType = URLConnection.guessContentTypeFromName(file.value)
        headers.add(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=${file.value}"
        )
        headers.add(
            HttpHeaders.CONTENT_TYPE,
            fileType
        )
        headers.add("X-Filename", file.value)
        headers.add("X-Filesize", file.getSizeString())
        headers.add("X-Createtime", file.getDateString())
        return headers
    }
}