package com.folklore.persistence

import com.folklore.domain.LoreFile
import db.component6
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

object LoreFileWriter {
    private fun readCsv(inputStream: InputStream): List<LoreFile> {
        val reader = inputStream.bufferedReader()
        return reader.lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val (s3Path, folder, topic, topicFullName,  keyWords, text) = it.split(',', ignoreCase = false, limit = 6)
                LoreFile(
                    s3Path = s3Path,
                    text = text,
                    topic = topic.toIntOrNull(),
                    folder = folder,
                    topicFullName = topicFullName,
                    keyWords = keyWords
                )
            }.toList()
    }

    fun write(loreFile: LoreFile) = transaction {
        SchemaUtils.create(LoreFilesTable)
        LoreFilesTable.insert {
            it[s3Path] = loreFile.s3Path
            it[text] = loreFile.text
            it[topic] = loreFile.topic
        }
    }

    fun writeFromCsv(dataFile: InputStream) = readCsv(dataFile).forEach { loreFile ->
        transaction {
            write(loreFile)
        }
    }
}
