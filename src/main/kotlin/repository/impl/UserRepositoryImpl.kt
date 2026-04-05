package com.prac.repository.impl

import com.prac.model.User
import com.prac.repository.UserRepository
import com.prac.tables.UsersTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class UserRepositoryImpl : UserRepository {
    override fun findByUsername(username: String): User? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.username eq username }
            .singleOrNull()
            ?.toUser()
    }

    override fun findById(id: String): User? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .singleOrNull()
            ?.toUser()
    }

    override fun createDefaultUsersIfMissing() {
        seedUserIfMissing("admin", "admin", "ADMIN")
        seedUserIfMissing("user", "user", "USER")
    }

    private fun seedUserIfMissing(username: String, password: String, role: String) {
        if (findByUsername(username) != null) {
            return
        }

        transaction {
            UsersTable.insert {
                it[id] = UUID.randomUUID().toString()
                it[this.username] = username
                it[passwordHash] = BCrypt.hashpw(password, BCrypt.gensalt())
                it[this.role] = role
            }
        }
    }

    private fun ResultRow.toUser(): User = User(
        id = this[UsersTable.id],
        username = this[UsersTable.username],
        passwordHash = this[UsersTable.passwordHash],
        role = this[UsersTable.role],
    )
}
