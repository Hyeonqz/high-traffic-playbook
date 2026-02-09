#!/bin/bash

# ============================================
# MySQL Master-Replica Replication 자동 설정 스크립트
# ============================================

echo "🔄 MySQL Replication 설정을 시작합니다..."

# Master와 Replica가 완전히 시작될 때까지 대기
echo "⏳ MySQL 서버들이 준비될 때까지 대기 중..."
sleep 30

# Master 상태 확인
echo "📊 Master 상태 확인 중..."
MASTER_STATUS=$(docker exec mysql-master mysql -uroot -proot1234 -e "SHOW MASTER STATUS\G")
echo "$MASTER_STATUS"

# Replica에서 Replication 설정
echo "🔧 Replica Replication 설정 중..."
docker exec mysql-replica mysql -uroot -proot1234 <<-EOSQL
    STOP SLAVE;

    CHANGE MASTER TO
        MASTER_HOST='mysql-master',
        MASTER_PORT=3306,
        MASTER_USER='repl_user',
        MASTER_PASSWORD='repl1234',
        MASTER_AUTO_POSITION=1;

    START SLAVE;
EOSQL

# Replication 상태 확인
sleep 5
echo "📊 Replica Replication 상태 확인..."
SLAVE_STATUS=$(docker exec mysql-replica mysql -uroot -proot1234 -e "SHOW SLAVE STATUS\G")
echo "$SLAVE_STATUS"

# IO와 SQL 스레드 상태 체크
IO_RUNNING=$(echo "$SLAVE_STATUS" | grep "Slave_IO_Running:" | awk '{print $2}')
SQL_RUNNING=$(echo "$SLAVE_STATUS" | grep "Slave_SQL_Running:" | awk '{print $2}')

echo ""
echo "======================================"
if [ "$IO_RUNNING" = "Yes" ] && [ "$SQL_RUNNING" = "Yes" ]; then
    echo "✅ Replication 설정 완료!"
    echo "   - Slave_IO_Running: $IO_RUNNING"
    echo "   - Slave_SQL_Running: $SQL_RUNNING"
else
    echo "❌ Replication 설정 실패"
    echo "   - Slave_IO_Running: $IO_RUNNING"
    echo "   - Slave_SQL_Running: $SQL_RUNNING"
    echo ""
    echo "상세 로그:"
    echo "$SLAVE_STATUS"
fi
echo "======================================"
