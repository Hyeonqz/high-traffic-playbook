#!/bin/bash

# ============================================
# 대용량 트래픽 처리 시스템 인프라 종료 스크립트
# ============================================

echo "🛑 대용량 트래픽 처리 시스템 인프라를 종료합니다..."
echo ""

# MySQL 종료
echo "🗄️  MySQL 종료 중..."
docker compose -f docker-compose-mysql.yml down
echo "   ✅ MySQL stopped"
echo ""

# Redis 종료
echo "💾 Redis 종료 중..."
docker compose -f docker-compose-redis.yml down
echo "   ✅ Redis stopped"
echo ""

# Kafka 종료
echo "📦 Kafka 종료 중..."
docker compose -f docker-compose-kraft-kafka.yml down
echo "   ✅ Kafka stopped"
echo ""

echo "======================================"
echo "✅ 모든 인프라가 종료되었습니다!"
echo "======================================"
echo ""
echo "💡 팁:"
echo "   - 데이터 유지: 볼륨이 보존되어 있습니다."
echo "   - 완전 삭제: ./clean-all.sh 실행"
echo ""
