package org.ivcode.inventory.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundException(message: String? = null, cause: Throwable? = null): RuntimeException(message, cause)