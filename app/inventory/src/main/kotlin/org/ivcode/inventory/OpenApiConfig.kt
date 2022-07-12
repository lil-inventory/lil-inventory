package org.ivcode.inventory

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "\${application.title}",
        description = "inventory management",
        version = "\${application.version}",
    )
)
class OpenApiConfig