package org.ivcode.inventory.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FORBIDDEN)
class ForbiddenException(message: String? = null, cause: Throwable? = null): RuntimeException(message, cause)