package io.github.hyeonqz.repository

import io.github.hyeonqz.domain.Train
import org.springframework.data.jpa.repository.JpaRepository

interface TrainRepository : JpaRepository<Train, Long>
