package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.data.entities.FileEntity
import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.library.JsonUtil
import com.hrblizz.fileapi.library.log.LogItem
import com.hrblizz.fileapi.library.log.Logger
import com.hrblizz.fileapi.rest.ResponseEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
class FileMetaController(private val entityRepository: EntityRepository<FileEntity>) {
    @RequestMapping("/files/metas",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
        )
    fun getMetas(@RequestBody tokenJson: TokenList): ResponseEntity<String> {
        val tokenList = tokenJson.tokens
        var metaMap = HashMap<String, FileMetaData>()
        for (token in tokenList) {
            val file = entityRepository.findByIdOrNull(token) as FileEntity?
            if (file != null) {
                val metaData = FileMetaData(file)
                metaMap[token] = metaData
            }
        }
        try {
            return ResponseEntity(
                JsonUtil.toJson(metaMap, usePrettyWriter = true),
                HttpStatus.OK.value()
            )
        }
        catch (e: Exception) {
            val logger = Logger()
            logger.error(LogItem("Failed to serialize metadata: ${e.message}"))
        }
        return ResponseEntity(
            "Failed to get metadata",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
    }
    class FileMetaData() {
        lateinit var fileName: String
        var size = 0
        lateinit var contentType: String
        lateinit var createTime: String
        lateinit var meta: String
        constructor(file: FileEntity) : this() {
            this.fileName = file.value
            this.size = file.content.length()
            this.contentType = file.contentType
            this.createTime = file.createTime.toString()
            this.meta = file.meta
        }
    }
    class TokenList() {
        lateinit var tokens: List<String>
    }
}
