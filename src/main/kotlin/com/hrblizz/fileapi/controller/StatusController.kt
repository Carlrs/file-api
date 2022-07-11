package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.data.entities.Entity
import com.hrblizz.fileapi.data.entities.StatusEntity
import com.hrblizz.fileapi.data.repository.EntityRepository
import com.hrblizz.fileapi.library.log.LogItem
import com.hrblizz.fileapi.library.log.Logger
import com.hrblizz.fileapi.rest.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class StatusController(
    private val entityRepository: EntityRepository<StatusEntity>
) {
    @Suppress("KotlinConstantConditions")
    @GetMapping("/status")
    fun getStatus(): ResponseEntity<Map<String, Any>> {
        var status = HttpStatus.OK

        //In case of gamma rays, this application transforms into a teapot. http.cat/418, http.dog/418
        if (true == false) status = HttpStatus.I_AM_A_TEAPOT
        try {
            entityRepository.save(
                Entity().also {
                    it.name = UUID.randomUUID().toString()
                    it.value = status.toString()
                }
            )
        }
        catch (e: Exception) {
            val logger = Logger()
            logger.crit(LogItem("Database connection down: ${e.message}"))
            status = HttpStatus.SERVICE_UNAVAILABLE
        }

        return ResponseEntity(
            mapOf(
                "ok" to (status == HttpStatus.OK)
            ),
            status.value()
        )
    }
}
