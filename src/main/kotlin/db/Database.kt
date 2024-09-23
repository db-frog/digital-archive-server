package db

import com.folklore.persistence.LoreFileWriter
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Files
import java.nio.file.Paths

operator fun <T> List<T>.component6() = this[5]

fun getResourceFilePath(dataFileName: String) =
    Paths.get(Paths.get("").toAbsolutePath().toString(), "/src/main/resources/db/data/$dataFileName")

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

        //LoreFileWriter.writeFromCsv(Files.newInputStream(getResourceFilePath("filetexttopic.csv")))
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
