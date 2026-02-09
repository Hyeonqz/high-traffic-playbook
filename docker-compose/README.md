# Infrastructure Setup Guide

대용량 티켓 예매 시스템을 위한 인프라 구성 가이드입니다.

## 📋 구성 요소

### 파일 구조
```
docker-compose/
├── docker-compose-kraft-kafka.yml  # Kafka 클러스터 (3 nodes)
├── docker-compose-redis.yml        # Redis (Queue + Cache)
├── docker-compose-mysql.yml        # MySQL (Master-Replica)
├── start-all.sh                    # 전체 시작 스크립트
├── stop-all.sh                     # 전체 종료 스크립트
├── clean-all.sh                    # 완전 삭제 스크립트
└── mysql/
    ├── init-master.sql             # MySQL Master 초기화
    ├── init-replica.sql            # MySQL Replica 초기화
    └── setup-replication.sh        # Replication 자동 설정
```

### 1. Kafka Cluster (3 nodes)
- **kafka00**: localhost:9092
- **kafka01**: localhost:9093
- **kafka02**: localhost:9094
- **kafka-ui**: http://localhost:8989

### 2. Redis (2 instances)
- **redis-queue**: localhost:6379 (대기열 전용)
- **redis-cache**: localhost:6380 (캐싱 전용)
- **redis-commander**: http://localhost:8081

### 3. MySQL (Master-Replica)
- **mysql-master**: localhost:3306 (Write DB)
- **mysql-replica**: localhost:3307 (Read DB)
- **phpmyadmin**: http://localhost:8082

---

## 🚀 Quick Start

### 방법 1: 전체 자동 시작 (권장)

```bash
cd docker-compose
./start-all.sh
```

이 스크립트는 다음을 자동으로 수행합니다:
1. Docker Network 생성
2. Kafka 클러스터 시작
3. Redis 인스턴스 시작
4. MySQL Master-Replica 시작
5. MySQL Replication 자동 설정

### 방법 2: 개별 시작

#### 1단계: Docker Network 생성
```bash
docker network create high-traffic-network
```

#### 2단계: Kafka 클러스터 시작
```bash
cd docker-compose
docker compose -f docker-compose-kraft-kafka.yml up -d

# 상태 확인
docker compose -f docker-compose-kraft-kafka.yml ps

# Kafka UI 접속: http://localhost:8989
```

#### 3단계: Redis 시작
```bash
docker compose -f docker-compose-redis.yml up -d

# 상태 확인
docker compose -f docker-compose-redis.yml ps

# Redis Commander 접속: http://localhost:8081
```

#### 4단계: MySQL 시작
```bash
docker compose -f docker-compose-mysql.yml up -d

# 상태 확인
docker compose -f docker-compose-mysql.yml ps

# phpMyAdmin 접속: http://localhost:8082
```

#### 5단계: MySQL Replication 설정
```bash
# 서비스가 완전히 시작될 때까지 대기 (약 60초)
sleep 60

# Replication 자동 설정 실행
./mysql/setup-replication.sh
```

---

## 🔍 Health Check

### 전체 상태 확인
```bash
# Kafka 상태
docker compose -f docker-compose-kraft-kafka.yml ps

# Redis 상태
docker compose -f docker-compose-redis.yml ps

# MySQL 상태
docker compose -f docker-compose-mysql.yml ps
```

### Kafka 연결 테스트
```bash
# Kafka 브로커 확인
docker exec kafka00 kafka-broker-api-versions --bootstrap-server localhost:9092

# Topic 생성 테스트
docker exec kafka00 kafka-topics --create \
  --topic test-topic \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 3

# Topic 목록 확인
docker exec kafka00 kafka-topics --list --bootstrap-server localhost:9092
```

### Redis 연결 테스트
```bash
# Redis Queue 테스트
docker exec redis-queue redis-cli ping
# 응답: PONG

# Redis Cache 테스트
docker exec redis-cache redis-cli ping
# 응답: PONG

# Redis에 데이터 저장 테스트
docker exec redis-queue redis-cli SET test "Hello Redis"
docker exec redis-queue redis-cli GET test
```

### MySQL 연결 테스트
```bash
# MySQL Master 테스트
docker exec mysql-master mysql -uroot -proot1234 -e "SELECT 'Master OK' AS status;"

# MySQL Replica 테스트
docker exec mysql-replica mysql -uroot -proot1234 -e "SELECT 'Replica OK' AS status;"

# 데이터베이스 확인
docker exec mysql-master mysql -uroot -proot1234 -e "SHOW DATABASES;"
```

### MySQL Replication 상태 확인
```bash
# Replica 상태 확인
docker exec mysql-replica mysql -uroot -proot1234 -e "SHOW SLAVE STATUS\G" | grep Running

# 정상 출력:
# Slave_IO_Running: Yes
# Slave_SQL_Running: Yes
```

---

## ✅ Replication 동작 검증

### Master에서 데이터 삽입 후 Replica에서 확인
```bash
# 1. Master에 데이터 삽입
docker exec mysql-master mysql -uroot -proot1234 ticketing -e \
  "INSERT INTO users (username, email, phone) VALUES ('repl_test', 'repl@test.com', '010-0000-0000');"

# 2. Replica에서 데이터 확인 (약 1초 후)
docker exec mysql-replica mysql -uroot -proot1234 ticketing -e \
  "SELECT * FROM users WHERE username='repl_test';"

# ✅ 데이터가 조회되면 Replication 정상 동작
```

---

## 📊 관리 도구 접속

### Kafka UI
- **URL**: http://localhost:8989
- **기능**:
  - Topic 관리
  - 메시지 조회
  - Consumer Group 모니터링
  - 브로커 상태 확인

### Redis Commander
- **URL**: http://localhost:8081
- **기능**:
  - Redis 데이터 조회/수정
  - 메모리 사용량 모니터링
  - Key 검색 및 관리
- **Connections**:
  - `queue`: redis-queue (대기열 데이터)
  - `cache`: redis-cache (캐시 데이터)

### phpMyAdmin
- **URL**: http://localhost:8082
- **계정**: root / root1234
- **서버**:
  - `mysql-master` (Write)
  - `mysql-replica` (Read)

---

## 📈 성능 확인

### Kafka 성능 테스트
```bash
# Producer 성능 테스트
docker exec kafka00 kafka-producer-perf-test \
  --topic test-topic \
  --num-records 10000 \
  --record-size 1024 \
  --throughput -1 \
  --producer-props bootstrap.servers=localhost:9092

# Consumer 성능 테스트
docker exec kafka00 kafka-consumer-perf-test \
  --topic test-topic \
  --messages 10000 \
  --bootstrap-server localhost:9092
```

### Redis 메모리 사용량 확인
```bash
# Redis Queue 메모리 정보
docker exec redis-queue redis-cli INFO memory | grep used_memory_human

# Redis Cache 메모리 정보
docker exec redis-cache redis-cli INFO memory | grep used_memory_human
```

### MySQL Connection 확인
```bash
# 현재 연결 수
docker exec mysql-master mysql -uroot -proot1234 -e \
  "SHOW STATUS LIKE 'Threads_connected';"

# Max connections 설정
docker exec mysql-master mysql -uroot -proot1234 -e \
  "SHOW VARIABLES LIKE 'max_connections';"
```

---

## 🛑 종료 및 정리

### 전체 종료 (데이터 유지)
```bash
./stop-all.sh
```

### 개별 종료
```bash
# MySQL 종료
docker compose -f docker-compose-mysql.yml down

# Redis 종료
docker compose -f docker-compose-redis.yml down

# Kafka 종료
docker compose -f docker-compose-kraft-kafka.yml down
```

### 완전 삭제 (데이터 포함)
```bash
./clean-all.sh
```

또는 개별 삭제:
```bash
# MySQL 완전 삭제
docker compose -f docker-compose-mysql.yml down -v

# Redis 완전 삭제
docker compose -f docker-compose-redis.yml down -v

# Kafka 완전 삭제
docker compose -f docker-compose-kraft-kafka.yml down -v

# Network 삭제
docker network rm high-traffic-network
```

---

## 🐛 트러블슈팅

### Kafka 브로커가 시작되지 않는 경우
```bash
# 로그 확인
docker compose -f docker-compose-kraft-kafka.yml logs kafka00

# 볼륨 삭제 후 재시작
docker compose -f docker-compose-kraft-kafka.yml down -v
docker compose -f docker-compose-kraft-kafka.yml up -d
```

### MySQL Replication이 동작하지 않는 경우
```bash
# Replica 상태 확인
docker exec mysql-replica mysql -uroot -proot1234 -e "SHOW SLAVE STATUS\G"

# Last_Error 확인
docker exec mysql-replica mysql -uroot -proot1234 -e "SHOW SLAVE STATUS\G" | grep Last_Error

# Replication 재설정
docker exec mysql-replica mysql -uroot -proot1234 -e "STOP SLAVE; RESET SLAVE; START SLAVE;"

# 또는 setup 스크립트 재실행
./mysql/setup-replication.sh
```

### Redis 메모리 부족
```bash
# 현재 메모리 사용량 확인
docker exec redis-queue redis-cli INFO memory | grep used_memory_human

# 캐시 전체 삭제 (주의!)
docker exec redis-cache redis-cli FLUSHALL

# 특정 패턴 삭제
docker exec redis-cache redis-cli --scan --pattern "cache:*" | xargs docker exec -i redis-cache redis-cli DEL
```

### Network 연결 문제
```bash
# Network 상태 확인
docker network inspect high-traffic-network

# Network 재생성
docker network rm high-traffic-network
docker network create high-traffic-network

# 서비스 재시작
./stop-all.sh
./start-all.sh
```

---

## 📝 참고사항

### 리소스 요구사항
- **CPU**: 최소 4 cores (권장 8 cores)
- **Memory**: 최소 8GB (권장 16GB)
  - Kafka: 3GB (1GB × 3)
  - MySQL: 2GB (1GB × 2)
  - Redis: 4GB (2GB × 2)
  - 기타: 1GB

### 포트 사용 현황
| 서비스 | 포트 | 용도 |
|--------|------|------|
| redis-queue | 6379 | 대기열 Redis |
| redis-cache | 6380 | 캐시 Redis |
| mysql-master | 3306 | Write DB |
| mysql-replica | 3307 | Read DB |
| kafka00 | 9092 | Kafka Broker |
| kafka01 | 9093 | Kafka Broker |
| kafka02 | 9094 | Kafka Broker |
| redis-commander | 8081 | Redis GUI |
| phpmyadmin | 8082 | MySQL GUI |
| kafka-ui | 8989 | Kafka GUI |

### 데이터 영속성
- **Redis Queue**: AOF (Append Only File) 활성화 - 데이터 영속성 보장
- **Redis Cache**: 영속성 비활성화 - 재시작 시 데이터 손실
- **MySQL**: 볼륨 마운트를 통한 데이터 영속성 보장
- **Kafka**: 볼륨 마운트를 통한 메시지 영속성 보장

### 초기 데이터
MySQL Master에는 다음 샘플 데이터가 자동으로 생성됩니다:
- 사용자 4명 (user001, user002, user003, test_user)
- 설날 특별 열차 7편 (KTX, SRT)
- KTX-001 열차의 좌석 정보 (1호차 80석)

### Docker Compose Name
모든 docker-compose 파일은 `high-traffic-infra` name을 공유하며, `high-traffic-network` 네트워크를 사용합니다.
이를 통해 모든 서비스가 서로 통신할 수 있습니다.

---

## 🎯 다음 단계

인프라 구성이 완료되면 다음 단계로 진행합니다:

1. **Phase 1**: Redis 대기열 시스템 구현
2. **Phase 2**: CQRS 패턴 티켓팅 서비스 개발
3. **Phase 3**: Kafka 이벤트 기반 비동기 처리
4. **Phase 4**: 성능 최적화 및 캐싱
5. **Phase 5**: 부하 테스트 및 모니터링

자세한 내용은 `.claude/request/feature/ticketing-v1.0-high-traffic-handling.md` 참조.
