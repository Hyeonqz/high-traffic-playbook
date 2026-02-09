-- ============================================
-- MySQL Replica 초기화 스크립트
-- ============================================

-- Replica는 Master의 binlog를 재생하므로 별도 초기화가 필요 없습니다.
-- 모든 데이터, 사용자, 테이블은 Replication을 통해 자동으로 복제됩니다.

SELECT 'MySQL Replica initialized - waiting for replication setup' AS message;
