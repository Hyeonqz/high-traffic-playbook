#!/bin/sh

# Redis Sentinel 시작 스크립트
# Master IP를 동적으로 resolve하여 Sentinel 설정 생성

echo "🔍 Redis Master IP를 찾는 중..."

# Master가 준비될 때까지 대기
until getent hosts redis-master > /dev/null 2>&1; do
    echo "⏳ redis-master 대기 중..."
    sleep 2
done

# Master IP 가져오기
MASTER_IP=$(getent hosts redis-master | awk '{ print $1 }')

echo "✅ Redis Master IP: $MASTER_IP"

# Sentinel 설정 파일 생성
cat > /tmp/sentinel.conf <<EOF
port 26379

# Master 모니터링 (IP 주소 사용)
sentinel monitor mymaster $MASTER_IP 6379 1

# Master 다운 감지 시간 (5초)
sentinel down-after-milliseconds mymaster 5000

# Failover 시 동시 재동기화 수
sentinel parallel-syncs mymaster 1

# Failover 타임아웃 (10초)
sentinel failover-timeout mymaster 10000

# 로그 레벨
loglevel notice

# Sentinel이 Master/Slave에 접근할 때 사용할 정보
# sentinel auth-pass mymaster <password>  # 비밀번호 사용 시
EOF

echo "📝 Sentinel 설정 파일 생성 완료"
cat /tmp/sentinel.conf

echo ""
echo "🚀 Redis Sentinel 시작 중..."
exec redis-sentinel /tmp/sentinel.conf
