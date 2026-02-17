package io.github.hyeonqz.domain

import jakarta.persistence.*

@Entity
@Table(name = "train")
class Train(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "train_number", nullable = false, unique = true, length = 20)
    val trainNumber: String,

    @Column(nullable = false, length = 100)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "train_type", nullable = false, length = 20)
    val trainType: TrainType,
)

enum class TrainType {
    KTX, SRT
}
