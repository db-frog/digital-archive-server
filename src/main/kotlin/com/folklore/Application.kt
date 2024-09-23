package com.folklore

import com.folklore.plugins.*
import db.Database
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val db = Database()
    configureSecurity()
    configureSerialization()
    configureRouting(db)
}
