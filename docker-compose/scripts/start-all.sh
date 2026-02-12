#!/bin/bash

# ============================================
# 대용량 트래픽 처리 시스템 인프라 시작 스크립트
# ============================================

set -e  # 에러 발생 시 스크립트 중단

# [수정] 스크립트 위치(/docker-compose/scripts)를 기준으로 상위 폴더(/docker-compose)로 이동
BASE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$BASE_DIR"

echo "🚀 대용량 트래픽 처리 시스템 인프라를 시작합니다..."
echo "📂 작업 디렉토리: $BASE_DIR"
echo ""

# ============================================
# 1. Docker Network 생성
# ============================================
echo "📡 Docker Network 생성 중..."
if docker network inspect high-traffic-network >/dev/null 2>&1; then
    echo "   ✅ Network 'high-traffic-network' 이미 존재"
else
    docker network create high-traffic-network
    echo "   ✅ Network 'high-traffic-network' 생성 완료"
fi
echo ""

# ============================================
# 2. Kafka 클러스터 시작
# ============================================
echo "📦 Kafka 클러스터 시작 중..."
docker compose -f docker-compose-kraft-kafka.yml up -d
echo "   ✅ Kafka 3-node cluster started"
echo "   - kafka00: localhost:9092"
echo "   - kafka01: localhost:9093"
echo "   - kafka02: localhost:9094"
echo "   - kafka-ui: http://localhost:8989"
echo ""

# ============================================
# 3. Redis Master-Slave + Sentinel 시작
# ============================================
echo "💾 Redis Master-Slave 클러스터 시작 중..."
docker compose -f docker-compose-redis.yml up -d
echo "   ✅ Redis cluster started"
echo "   - redis-master: localhost:6379 (읽기/쓰기)"
echo "   - redis-slave: localhost:6380 (읽기 전용)"
echo "   - redis-sentinel: localhost:26379 (자동 Failover)"
echo "   - redis-commander: http://localhost:8081"
echo ""

# ============================================
# 4. MySQL Master-Replica 시작
# ============================================
echo "🗄️  MySQL Master-Replica 시작 중..."
docker compose -f docker-compose-mysql.yml up -d
echo "   ✅ MySQL instances started"
echo "   - mysql-master: localhost:3306 (Write DB)"
echo "   - mysql-replica: localhost:3307 (Read DB)"
echo "   - phpmyadmin: http://localhost:8082"
echo ""

# ============================================
# 5. 서비스 Health Check 대기
# ============================================
echo "⏳ 서비스들이 준비될 때까지 대기 중 (약 60초)..."
sleep 60

# ============================================
# 6. MySQL Replication 설정
# ============================================
echo ""
echo "🔄 MySQL Replication 설정 중..."
./mysql/setup-replication.sh

echo ""
echo "======================================"
echo "✅ 모든 인프라가 시작되었습니다!"
echo "======================================"
echo ""
echo "📊 관리 도구 URL:"
echo "   - Kafka UI:        http://localhost:8989"
echo "   - Redis Commander: http://localhost:8081"
echo "   - phpMyAdmin:      http://localhost:8082"
echo ""
echo "🔍 상태 확인:"
echo "   docker compose -f docker-compose-kraft-kafka.yml ps"
echo "   docker compose -f docker-compose-redis-replication.yml ps"
echo "   docker compose -f docker-compose-mysql.yml ps"
echo ""
echo "🛑 전체 종료:"
echo "   ./stop-all.sh"
echo ""
