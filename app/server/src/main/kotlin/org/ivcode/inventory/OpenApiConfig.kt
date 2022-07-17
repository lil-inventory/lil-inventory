package org.ivcode.inventory

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.OAuthFlow
import io.swagger.v3.oas.annotations.security.OAuthFlows
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme

@OpenAPIDefinition(
    info = Info (
        title = "\${application.title}",
        description = "inventory management",
        version = "\${application.version}",
    ),
    security = [SecurityRequirement(name="Auth")]
)
@SecurityScheme(
    name = "Auth",
    type = SecuritySchemeType.OAUTH2,
    flows = OAuthFlows(
        password = OAuthFlow(
            tokenUrl = "/auth/token"
        )
    )
)
class OpenApiConfig