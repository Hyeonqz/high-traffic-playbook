package io.github.hyeonqz.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
class CorrelationIdFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val correlationId = request.getHeader(CORRELATION_ID_HEADER)
            ?.takeIf { it.isNotBlank() }
            ?: UUID.randomUUID().toString()

        MDC.put(MDC_CORRELATION_ID, correlationId)
        MDC.put(MDC_HTTP_METHOD, request.method)
        MDC.put(MDC_REQUEST_URI, request.requestURI)
        response.setHeader(CORRELATION_ID_HEADER, correlationId)

        try {
            filterChain.doFilter(request, response)
        } finally {
            MDC.remove(MDC_CORRELATION_ID)
            MDC.remove(MDC_HTTP_METHOD)
            MDC.remove(MDC_REQUEST_URI)
        }
    }

    companion object {
        private const val CORRELATION_ID_HEADER = "X-Correlation-Id"
        private const val MDC_CORRELATION_ID = "correlationId"
        private const val MDC_HTTP_METHOD = "httpMethod"
        private const val MDC_REQUEST_URI = "requestUri"
    }
}
