package io.github.hyeonqz.exception

import org.springframework.http.HttpStatus

/**
 * 모든 모듈에서 공통으로 발생하는 예외 코드.
 *
 * 코드 체계:
 *   COMMON-001 ~ 099 : 클라이언트 오류 (4xx)
 *   COMMON-900 ~ 999 : 서버 오류 (5xx)
 */
enum class CommonErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : ErrorCode {

    // ── 4xx Client Error ──────────────────────────────────────────────
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST,          "COMMON-001", "잘못된 요청입니다."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST,            "COMMON-002", "요청 형식이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND,          "COMMON-003", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON-004", "허용되지 않는 HTTP 메서드입니다."),

    // ── 5xx Server Error ──────────────────────────────────────────────
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,  "COMMON-900", "서버 내부 오류가 발생했습니다."),
    RUNTIME_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,   "COMMON-901", "서버 런타임 오류가 발생했습니다."),
    NULL_POINTER(HttpStatus.INTERNAL_SERVER_ERROR,    "COMMON-902", "서버 내부 오류가 발생했습니다."),
}
