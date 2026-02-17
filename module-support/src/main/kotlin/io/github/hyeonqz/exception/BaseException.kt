package io.github.hyeonqz.exception

/**
 * 모든 도메인 예외의 기반 클래스.
 *
 * 도메인 예외는 이 클래스를 상속하고,
 * ErrorCode 구현체(Enum)를 통해 코드·메시지·HTTP 상태를 일관되게 전달한다.
 *
 * 사용 예:
 *   class TicketingException(errorCode: TicketingErrorCode) : BaseException(errorCode)
 */
open class BaseException(
    val errorCode: ErrorCode,
    message: String = errorCode.message,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
