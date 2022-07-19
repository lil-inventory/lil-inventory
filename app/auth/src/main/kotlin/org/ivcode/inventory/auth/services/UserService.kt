package org.ivcode.inventory.auth.services

import org.ivcode.inventory.auth.repositories.UserDao
import org.ivcode.inventory.auth.services.model.User
import org.ivcode.inventory.auth.utils.toUser
import org.ivcode.inventory.common.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userDao: UserDao
) {

    fun getUser(email: String): User {
        val userEntity = userDao.readUserByEmail(email) ?: throw NotFoundException()
        return userEntity.toUser()
    }
}