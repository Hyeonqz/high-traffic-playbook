package io.github.hyeonqz.exception

import org.springframework.http.HttpStatus

/**
 * 모든 에러 코드의 계약(Contract) 인터페이스.
 *
 * - module-support: CommonErrorCode 구현
 * - 각 도메인 모듈: {도메인}ErrorCode Enum 으로 구현
 *
 * 코드 체계: {PREFIX}-{3자리 숫자}
 *   - COMMON-0xx : 공통 클라이언트 오류 (4xx)
 *   - COMMON-9xx : 공통 서버 오류 (5xx)
 *   - TKT-0xx    : Ticketing 클라이언트 오류
 *   - TKT-1xx    : Ticketing 인증/인가 오류
 */
interface ErrorCode {
    val code: String
    val message: String
    val status: HttpStatus
}
