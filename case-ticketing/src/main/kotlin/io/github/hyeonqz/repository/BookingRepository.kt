package io.github.hyeonqz.repository

import io.github.hyeonqz.domain.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BookingRepository : JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.idempotencyKey = :key")
    fun findByUserIdAndIdempotencyKey(
        @Param("userId") userId: String,
        @Param("key") idempotencyKey: String,
    ): Booking?
}
