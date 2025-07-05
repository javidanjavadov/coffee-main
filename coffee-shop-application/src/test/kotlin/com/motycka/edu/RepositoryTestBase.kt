package com.motycka.edu

import io.kotest.core.spec.style.FunSpec
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

abstract class RepositoryTestBase(
    vararg tables: Table
) : FunSpec() {

    init {

        beforeTest {
            Database.Companion.connect(
                url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                driver = "org.h2.Driver",
                user = "root",
                password = ""
            )

            transaction {
                tables.forEach { table ->
                    SchemaUtils.create(table)
                }
            }
        }

        afterTest {
            transaction {
                tables.forEach { table ->
                    SchemaUtils.create(table)
                }
            }
        }
    }
}
