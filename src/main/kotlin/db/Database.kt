package db

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object File : Table() {
    val s3Path: Column<String> = varchar("s3_path", 512)
    val text: Column<String> = text("text")
    val topic: Column<Int?> = (integer("topic")).nullable()

    override val primaryKey = PrimaryKey(s3Path) // name is optional here
}

operator fun <T> List<T>.component6() = this[5]

data class LoreFile(
    val s3Path: String,
    val folder: String,
    val topic: Int?,
    val topicFullName: String,
    val keyWords: String,
    val text: String
)

fun getResourceFilePath(dataFileName: String) =
    Paths.get(Paths.get("").toAbsolutePath().toString(), "/src/main/resources/db/data/$dataFileName")

fun readCsv(inputStream: InputStream): List<LoreFile> {
    val reader = inputStream.bufferedReader()
    return reader.lineSequence()
        .filter { it.isNotBlank() }
        .map {
            val (s3Path, folder, topic, topicFullName,  keyWords, text) = it.split(',', ignoreCase = false, limit = 6)
            LoreFile(s3Path, folder, topic.toIntOrNull(), topicFullName, keyWords, text)
        }.toList()
}

class Database {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())

    fun getProperty(key: String): String? = appConfig.propertyOrNull(key)?.getString()

    private val dbUrl = getProperty("flyway.url") ?: "jdbc:postgresql://db:5432/archive"
    private val dbUser = getProperty("flyway.user") ?: "test"
    private val dbPassword = getProperty("flyway.password") ?: "test123"

    init {
        Database.connect(hikari())

        val flyway = Flyway.configure().dataSource(dbUrl, dbUser, dbPassword).load()
        flyway.migrate()

        /*transaction {
            SchemaUtils.create(File)
            val dataFile = Files.newInputStream(getResourceFilePath("filetexttopic.csv"))
            readCsv(dataFile).forEach { loreFile ->
                File.insert {
                    it[s3Path] = loreFile.s3Path
                    it[text] = loreFile.text
                    it[topic] = loreFile.topic
                }
            }
        }*/
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = dbUrl
        config.username = dbUser
        config.password = dbPassword
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}
