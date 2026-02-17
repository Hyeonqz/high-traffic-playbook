-- =====================================================
-- Stage 1: Ticketing 기본 스키마
-- 동시성 제어: seat.version(낙관적 락) + booking.idempotency_key(멱등성)
-- =====================================================

-- 기차 (열차 기본 정보)
CREATE TABLE train (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    train_number VARCHAR(20)  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    train_type   VARCHAR(20)  NOT NULL,  -- KTX, SRT
    PRIMARY KEY (id),
    UNIQUE KEY uk_train_number (train_number)
);

-- 운행 스케줄 (날짜별 운행 편)
CREATE TABLE schedule (
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    train_id          BIGINT      NOT NULL,
    departure_date    DATE        NOT NULL,
    departure_time    TIME        NOT NULL,
    arrival_time      TIME        NOT NULL,
    departure_station VARCHAR(50) NOT NULL,
    arrival_station   VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_schedule_train FOREIGN KEY (train_id) REFERENCES train (id),
    INDEX idx_schedule_search (departure_date, departure_station, arrival_station)
);

-- 좌석 (스케줄별 실제 좌석)
-- version 컬럼: 낙관적 락(@Version) — 동시 UPDATE 시 1건만 성공
CREATE TABLE seat (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    schedule_id BIGINT      NOT NULL,
    car_number  INT         NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    seat_class  VARCHAR(20) NOT NULL,  -- FIRST, STANDARD
    status      VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',  -- AVAILABLE, BOOKED
    version     BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_seat_schedule FOREIGN KEY (schedule_id) REFERENCES schedule (id),
    UNIQUE KEY uk_seat_position (schedule_id, car_number, seat_number)
);

-- 예약
-- uk_booking_idempotency: 동일 사용자의 동일 요청 중복 처리 방지
-- seat_id 에는 UNIQUE를 두지 않음 → 취소(CANCELLED) 후 재예약 허용
CREATE TABLE booking (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    seat_id          BIGINT       NOT NULL,
    user_id          VARCHAR(100) NOT NULL,
    idempotency_key  VARCHAR(100) NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'CONFIRMED',  -- CONFIRMED, CANCELLED
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_booking_seat FOREIGN KEY (seat_id) REFERENCES seat (id),
    UNIQUE KEY uk_booking_idempotency (user_id, idempotency_key)
);
