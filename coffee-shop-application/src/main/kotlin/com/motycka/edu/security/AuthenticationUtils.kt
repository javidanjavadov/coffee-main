package com.motycka.edu.security

import com.motycka.edu.error.UnauthorizedException
import com.motycka.edu.user.UserId
import com.motycka.edu.user.UserRole
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.pipeline.*

fun PipelineContext<*, ApplicationCall>.getUserIdentity(): IdentityDTO {

    val jwt = call.principal<JWTPrincipal>()

    return if (jwt != null) {
        val userId = jwt.getClaim(Claims.USER_ID, UserId::class)
        val role = jwt.getClaim(Claims.ROLE, String::class)
        IdentityDTO(
            userId = requireNotNull(userId) { "UserId claim is missing in JWT" },
            customerId = requireNotNull(userId) { "CustomerId claim is missing in JWT" },
            role = requireNotNull(role) { "Role claim is missing in JWT" }.let { UserRole.valueOf(it) }
        )
    } else {
        throw UnauthorizedException("Unauthorized access: No user identity found")
    }
}
