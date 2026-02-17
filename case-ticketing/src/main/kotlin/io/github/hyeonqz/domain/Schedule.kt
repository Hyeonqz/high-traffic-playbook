package io.github.hyeonqz.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(
    name = "schedule",
    indexes = [Index(name = "idx_schedule_search", columnList = "departure_date,departure_station,arrival_station")]
)
class Schedule(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    val train: Train,

    @Column(name = "departure_date", nullable = false)
    val departureDate: LocalDate,

    @Column(name = "departure_time", nullable = false)
    val departureTime: LocalTime,

    @Column(name = "arrival_time", nullable = false)
    val arrivalTime: LocalTime,

    @Column(name = "departure_station", nullable = false, length = 50)
    val departureStation: String,

    @Column(name = "arrival_station", nullable = false, length = 50)
    val arrivalStation: String,
)
