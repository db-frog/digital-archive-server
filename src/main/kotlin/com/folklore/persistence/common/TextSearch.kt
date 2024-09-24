package com.folklore.persistence.common

import org.jetbrains.exposed.sql.ComparisonOp
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.TextColumnType


class TSMatchOp<T : String?>(
    expr1: Expression<T>,
    expr2: Expression<T>
) : ComparisonOp(expr1, expr2, "@@")

infix fun <T : String?> Expression<T>.tsMatches(other: Expression<T>) = TSMatchOp(this, other)

class ToTSVector<T : String?>(
    config: Expression<T>?,
    document: Expression<T>
) : CustomFunction<String?>(
    "to_tsvector",
    TextColumnType(),
    *config?.let { arrayOf(config, document) } ?: arrayOf(document)
)

fun <T : String?> Expression<T>.toTSVector(config: Expression<T>? = null) = ToTSVector(config, this)

class ToTSQuery<T : String?>(
    config: Expression<T>?,
    query: Expression<T>
) : CustomFunction<String?>(
    "to_tsquery",
    TextColumnType(),
    *config?.let { arrayOf(config, query) } ?: arrayOf(query)
)

fun <T : String?> toTSQuery(query: Expression<T>, config: Expression<T>? = null) = ToTSQuery(config, query)