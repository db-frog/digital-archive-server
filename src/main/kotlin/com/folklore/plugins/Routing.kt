package com.folklore.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import aws.sdk.kotlin.services.s3.*
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import io.ktor.http.*
import java.io.File
import java.net.URL
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

val REGION = "us-west-1"
val BUCKET = "folklorearchive"

fun Application.configureRouting() {
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
    }
}
