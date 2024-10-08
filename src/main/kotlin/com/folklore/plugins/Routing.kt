package com.folklore.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import aws.sdk.kotlin.services.s3.*
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import com.folklore.domain.LoreFile
import com.folklore.persistence.LoreFileReader
import com.folklore.persistence.LoreFileWriter
import db.Database
import io.ktor.http.*
import io.ktor.server.request.*
import kotlin.time.Duration.Companion.minutes

val REGION = "us-west-1"
val BUCKET = "folklorearchive"

fun Application.configureRouting(db: Database) {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/{folder}/{item}") {
            val bucketKey = "${call.parameters["folder"]}/${call.parameters["item"]}"
            val request = GetObjectRequest {
                bucket = BUCKET
                key = bucketKey
            }

            val presignedRequest = S3Client
                .fromEnvironment { region = REGION }
                .use { s3 ->
                    s3.presignGetObject(request, 5.minutes)
                }

            println("Successfully made download link for $BUCKET/$request")
            call.respondRedirect(presignedRequest.url.toString())
            /* TODO: use search term to query elasticsearch and return results */
        }

        get("/listitems") {
            call.respondText(LoreFileReader.readAll().toString())
        }

        get("/query") {
            val queryString = call.request.queryParameters["textsearch"]
            call.respondText(LoreFileReader.textSearch(queryString).toString())
        }

        post("/lorefile") {
            val loreFile = call.receive<LoreFile>()
            LoreFileWriter.write(loreFile)
            call.respondText("LoreFile stored correctly", status = HttpStatusCode.Created)
        }
    }
}
