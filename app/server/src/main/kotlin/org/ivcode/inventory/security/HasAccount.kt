package org.ivcode.inventory.security

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@PreAuthorize("T(org.ivcode.inventory.auth.utils.AuthenticationUtil).hasAccount()")
annotation class HasAccount
