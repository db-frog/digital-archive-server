package com.folklore.persistence

import com.folklore.domain.LoreFile
import com.folklore.persistence.common.toTSQuery
import com.folklore.persistence.common.toTSVector
import com.folklore.persistence.common.tsMatches
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.stringParam
import org.jetbrains.exposed.sql.transactions.transaction

object LoreFileReader {
    val DEFAULT_LIMIT = 20

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }

    suspend fun textSearch(queryString : String?) : List<LoreFile> {
        return dbQuery({
            SchemaUtils.create(LoreFilesTable)

            if (queryString != null) {
                LoreFilesTable
                    .selectAll()
                    .where {
                        LoreFilesTable.text.toTSVector() tsMatches toTSQuery(stringParam(queryString))
                    }
                    .toList()
                    .map {
                        rowToLoreFile(it)
                    }
            } else {
                LoreFilesTable.selectAll().limit(DEFAULT_LIMIT).map {
                    rowToLoreFile(it)
                }
            }
        })
    }

    suspend fun readAll() : List<LoreFile> {
        return dbQuery({
                SchemaUtils.create(LoreFilesTable)

                LoreFilesTable.selectAll().map {
                    rowToLoreFile(it)
                }
            })
        }

    private fun rowToLoreFile(row: ResultRow): LoreFile {
        return LoreFile(
            s3Path = row[LoreFilesTable.s3Path],
            text = row[LoreFilesTable.text],
            topic = row[LoreFilesTable.topic],
            folder = null,
            topicFullName = null,
            keyWords = null
        )
    }
}