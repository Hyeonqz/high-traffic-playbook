#!/bin/bash

# ============================================
# 대용량 트래픽 처리 시스템 인프라 완전 삭제 스크립트
# ============================================

echo "⚠️  경고: 모든 데이터가 삭제됩니다!"
read -p "정말로 삭제하시겠습니까? y/N): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ 취소되었습니다."
    exit 1
fi

# [수정] 스크립트 위치(/docker-compose/scripts)를 기준으로 상위 폴더(/docker-compose)로 이동
BASE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$BASE_DIR"

echo ""
echo "🗑️  대용량 트래픽 처리 시스템 인프라를 완전 삭제합니다..."
echo "📂 작업 디렉토리: $BASE_DIR"
echo ""

# MySQL 완전 삭제
echo "🗄️  MySQL 완전 삭제 중..."
docker compose -f docker-compose-mysql.yml down -v
echo "   ✅ MySQL removed with volumes"
echo ""

# Redis 완전 삭제
echo "💾 Redis 완전 삭제 중..."
docker compose -f docker-compose-redis.yml down -v
echo "   ✅ Redis Master-Slave cluster removed with volumes"
echo ""

# Kafka 완전 삭제
echo "📦 Kafka 완전 삭제 중..."
docker compose -f docker-compose-kraft-kafka.yml down -v
echo "   ✅ Kafka removed with volumes"
echo ""

# Network 삭제
echo "📡 Network 삭제 중..."
docker network rm high-traffic-network 2>/dev/null || echo "   ℹ️  Network already removed"
echo ""

echo "======================================"
echo "✅ 모든 인프라와 데이터가 삭제되었습니다!"
echo "======================================"
echo ""
