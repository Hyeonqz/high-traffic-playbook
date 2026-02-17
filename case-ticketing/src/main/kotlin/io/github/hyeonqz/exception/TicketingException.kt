package io.github.hyeonqz.exception

class TicketingException(
    errorCode: TicketingErrorCode,
    message: String = errorCode.message,
) : BaseException(errorCode, message)
