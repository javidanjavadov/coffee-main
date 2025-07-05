package com.motycka.edu.user

import com.motycka.edu.RepositoryTestBase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryTest : RepositoryTestBase(UserTable) {
    private val repository = UserRepositoryImpl()

    init {
        beforeTest {
            transaction {
                // Create test users
                UserDAO.new {
                    username = "admin"
                    password = "password"
                    role = UserRole.STAFF
                }

                UserDAO.new {
                    username = "user"
                    password = "password"
                    role = UserRole.CUSTOMER
                }
            }
        }

        test("selectByUsername should return the correct user when username exists") {
            // Act
            val user = repository.selectByUsername("admin")

            // Assert
            user shouldNotBe null
            user?.username shouldBe "admin"
            user?.role shouldBe UserRole.STAFF
        }

        test("selectByUsername should return null when username does not exist") {
            // Act
            val user = repository.selectByUsername("nonexistent")

            // Assert
            user shouldBe null
        }

        test("selectByUsername should be case-sensitive") {
            // Act
            val user = repository.selectByUsername("ADMIN")

            // Assert
            user shouldBe null
        }
    }
}
