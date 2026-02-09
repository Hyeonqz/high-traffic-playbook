# MCP (Model Context Protocol) 사용 가이드

이 프로젝트는 Claude Code의 MCP를 통해 Figma, GitHub 등의 외부 서비스와 통합됩니다.

## 빠른 시작

### 1. 환경변수 로드

Claude Code를 실행하기 전에 환경변수를 로드하세요:

```bash
# 방법 1: source 사용 (현재 셸에 환경변수 export)
source scripts/load-env.sh

# 방법 2: 수동으로 export
export $(cat .env | grep -v '^#' | xargs)
```

### 2. Claude Code 실행

환경변수가 로드된 상태에서 Claude Code를 실행:

```bash
# 환경변수 로드 후 Claude Code 실행
source scripts/load-env.sh && claude
```

### 3. MCP 서버 확인

Claude Code 내에서 MCP 서버 목록 확인:

```
/mcp
```

사용 가능한 MCP 서버:
- **figma**: Figma 디자인 파일 접근
- **github**: GitHub 저장소 및 이슈 관리
- **everything-mcp**: 파일 시스템, 웹 검색 등

## 설정된 MCP 서버

### Figma MCP

Figma 디자인 파일에 접근하고 정보를 추출합니다.

**사용 예시:**
```
"Figma 파일 https://www.figma.com/file/[FILE_ID]의 컴포넌트를 보여줘"
"Figma 파일의 컬러 팔레트를 추출해줘"
```

**필요한 환경변수:**
- `FIGMA_PERSONAL_ACCESS_TOKEN`: Figma Personal Access Token

**토큰 발급:**
1. https://www.figma.com/settings 접속
2. Personal access tokens 섹션
3. Generate new token 클릭
4. 권한 선택 후 생성
5. `.env` 파일에 저장

### GitHub MCP

GitHub 저장소, 이슈, PR 등을 관리합니다.

**사용 예시:**
```
"이 저장소의 최근 이슈를 보여줘"
"PR #123의 상태를 확인해줘"
```

**필요한 환경변수:**
- `GITHUB_PERSONAL_ACCESS_TOKEN`: GitHub Personal Access Token

**토큰 발급:**
1. https://github.com/settings/tokens 접속
2. Generate new token (classic) 클릭
3. 필요한 scope 선택 (repo, read:org 등)
4. 생성 후 `.env` 파일에 저장

### Everything MCP

파일 시스템, 웹 검색, 데이터베이스 등 다양한 기능을 제공합니다.

**사용 예시:**
```
"현재 디렉토리의 파일 구조를 보여줘"
"웹에서 Kotlin Coroutines 최신 정보를 검색해줘"
```

## 환경변수 파일 (.env)

프로젝트 루트의 `.env` 파일에 토큰을 저장합니다:

```env
FIGMA_PERSONAL_ACCESS_TOKEN=figd_xxxxxxxxxxxxx
GITHUB_PERSONAL_ACCESS_TOKEN=github_pat_xxxxxxxxxxxxx
```

⚠️ **보안 주의사항:**
- `.env` 파일은 `.gitignore`에 포함되어 Git에 커밋되지 않습니다
- 토큰을 절대 코드에 직접 작성하지 마세요
- 팀원과 토큰을 공유하지 마세요
- 정기적으로 토큰을 갱신하세요

## MCP 설정 파일

MCP 서버 설정은 `.claude/mcp-configs/local-mcp.json`에 저장됩니다:

```json
{
  "mcpServers": {
    "figma": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-figma"],
      "env": {
        "FIGMA_PERSONAL_ACCESS_TOKEN": "${FIGMA_PERSONAL_ACCESS_TOKEN}"
      }
    }
  }
}
```

## 문제 해결

### MCP 서버가 목록에 표시되지 않는 경우

1. Claude Code 재시작
2. MCP 설정 파일 확인: `.claude/mcp-configs/local-mcp.json`
3. JSON 문법 오류 확인

### 환경변수가 로드되지 않는 경우

```bash
# 환경변수 확인
echo $FIGMA_PERSONAL_ACCESS_TOKEN

# .env 파일 확인
cat .env

# 다시 로드
source scripts/load-env.sh
```

### Figma/GitHub API 인증 실패

1. 토큰이 만료되지 않았는지 확인
2. 토큰에 필요한 권한이 있는지 확인
3. 환경변수가 올바르게 로드되었는지 확인

## 추가 리소스

- [Claude Code MCP 문서](https://code.claude.com/docs/en/mcp)
- [MCP 프로토콜 문서](https://modelcontextprotocol.io/)
- [Figma API 문서](https://www.figma.com/developers/api)
- [GitHub API 문서](https://docs.github.com/en/rest)
