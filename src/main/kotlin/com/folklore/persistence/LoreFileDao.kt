package com.folklore.persistence

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object LoreFilesTable : Table() {
    val s3Path: Column<String> = varchar("s3_path", 512)
    val text: Column<String> = text("text")
    val topic: Column<Int?> = (integer("topic")).nullable()

    override val primaryKey = PrimaryKey(s3Path)
}