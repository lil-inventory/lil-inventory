package org.ivcode.inventory.security

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@PreAuthorize("hasAuthority('ACCOUNT_ADMIN')")
annotation class AccountAdmin
