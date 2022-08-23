package org.ivcode.inventory.security

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@PreAuthorize("hasRole('SUPER_ADMIN')")
annotation class SuperAdmin
