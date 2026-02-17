package io.github.hyeonqz.concurrency

import io.github.hyeonqz.domain.*
import io.github.hyeonqz.dto.request.BookingRequest
import io.github.hyeonqz.exception.TicketingException
import io.github.hyeonqz.repository.BookingRepository
import io.github.hyeonqz.repository.ScheduleRepository
import io.github.hyeonqz.repository.SeatRepository
import io.github.hyeonqz.repository.TrainRepository
import io.github.hyeonqz.service.BookingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * 동시성 검증 통합 테스트
 *
 * 사전 조건: Docker MySQL (localhost:3306/ticketing) 실행 필요
 * 실행: ./gradlew :case-ticketing:test --tests "*.BookingConcurrencyTest"
 *
 * 핵심 검증:
 *   1,000건 동시 요청 → 단 1건만 예약 성공
 *   나머지 999건 → SEAT_ALREADY_BOOKED or ObjectOptimisticLockingFailureException (→ 409)
 */
@SpringBootTest
@ActiveProfiles("local")
class BookingConcurrencyTest {

    @Autowired
    private lateinit var bookingService: BookingService

    @Autowired
    private lateinit var trainRepository: TrainRepository

    @Autowired
    private lateinit var scheduleRepository: ScheduleRepository

    @Autowired
    private lateinit var seatRepository: SeatRepository

    @Autowired
    private lateinit var bookingRepository: BookingRepository

    private var seatId: Long = 0

    @BeforeEach
    fun setUp() {
        // 테스트 데이터 초기화 (매 테스트마다 새 seat 생성)
        bookingRepository.deleteAll()
        seatRepository.deleteAll()
        scheduleRepository.deleteAll()
        trainRepository.deleteAll()

        val train = trainRepository.save(
            Train(trainNumber = "KTX-TEST-${UUID.randomUUID()}", name = "Test KTX", trainType = TrainType.KTX)
        )
        val schedule = scheduleRepository.save(
            Schedule(
                train = train,
                departureDate = LocalDate.of(2025, 1, 25),
                departureTime = LocalTime.of(8, 0),
                arrivalTime = LocalTime.of(10, 30),
                departureStation = "서울",
                arrivalStation = "부산",
            )
        )
        val seat = seatRepository.save(
            Seat(schedule = schedule, carNumber = 1, seatNumber = "1A", seatClass = SeatClass.FIRST)
        )
        seatId = seat.id
    }

    @Test
    @DisplayName("1,000 동시 요청 → 예약 성공 반드시 1건")
    fun `동시 1000건 요청 시 단 1건만 성공`() {
        val totalRequests = 1000
        val threadPoolSize = 100
        val latch = CountDownLatch(1)
        val completeLatch = CountDownLatch(totalRequests)
        val executor = Executors.newFixedThreadPool(threadPoolSize)
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        repeat(totalRequests) { index ->
            executor.submit {
                try {
                    latch.await()  // 모든 스레드가 동시에 출발
                    bookingService.reserve(
                        BookingRequest(
                            seatId = seatId,
                            userId = "user-$index",
                            idempotencyKey = UUID.randomUUID().toString(),
                        )
                    )
                    successCount.incrementAndGet()
                } catch (e: TicketingException) {
                    failCount.incrementAndGet()
                } catch (e: ObjectOptimisticLockingFailureException) {
                    failCount.incrementAndGet()
                } finally {
                    completeLatch.countDown()
                }
            }
        }

        latch.countDown()                    // 일제히 출발
        completeLatch.await()                // 모든 요청 완료 대기
        executor.shutdown()

        // 검증: 성공은 반드시 1건
        assertThat(successCount.get())
            .withFailMessage("동시 요청에서 성공이 1건이 아님: successCount=%d", successCount.get())
            .isEqualTo(1)

        assertThat(failCount.get()).isEqualTo(totalRequests - 1)

        // 좌석 상태도 BOOKED 확인
        val finalSeat = seatRepository.findById(seatId).orElseThrow()
        assertThat(finalSeat.status).isEqualTo(SeatStatus.BOOKED)

        // DB 예약 레코드도 1건
        val bookingCount = bookingRepository.findAll().count { it.status == BookingStatus.CONFIRMED }
        assertThat(bookingCount).isEqualTo(1)
    }

    @Test
    @DisplayName("동일 idempotencyKey 100번 재요청 → 기존 결과 반환, 예약은 1건")
    fun `동일 idempotencyKey 재요청은 멱등`() {
        val idempotencyKey = UUID.randomUUID().toString()
        val userId = "user-idempotency"
        val latch = CountDownLatch(1)
        val completeLatch = CountDownLatch(100)
        val executor = Executors.newFixedThreadPool(20)
        val successCount = AtomicInteger(0)

        // 첫 번째 요청으로 예약 생성
        bookingService.reserve(BookingRequest(seatId = seatId, userId = userId, idempotencyKey = idempotencyKey))

        // 동일 key로 100번 재요청
        repeat(100) {
            executor.submit {
                try {
                    latch.await()
                    bookingService.reserve(
                        BookingRequest(seatId = seatId, userId = userId, idempotencyKey = idempotencyKey)
                    )
                    successCount.incrementAndGet()
                } finally {
                    completeLatch.countDown()
                }
            }
        }

        latch.countDown()
        completeLatch.await()
        executor.shutdown()

        // 모든 재요청이 성공(기존 결과 반환)
        assertThat(successCount.get()).isEqualTo(100)

        // DB 예약 레코드는 여전히 1건
        val confirmedCount = bookingRepository.findAll().count { it.status == BookingStatus.CONFIRMED }
        assertThat(confirmedCount).isEqualTo(1)
    }
}
