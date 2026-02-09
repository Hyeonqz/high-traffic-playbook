# IntelliJ IDEA 설정 파일 Git 관리 제외 가이드

현재 프로젝트에서 `.idea` 디렉토리 내의 설정 파일들이 커밋 로그에 포함되지 않도록 설정하는 방법입니다.

## 1. .gitignore 설정
`.gitignore` 파일에 다음 내용을 추가하여 `.idea` 디렉토리 전체를 무시하도록 설정합니다.

```gitignore
### IntelliJ IDEA ###
.idea/
*.iws
*.iml
*.ipr
```

## 2. 이미 추적 중인 파일 제거
만약 `.idea` 폴더의 파일들이 이미 Git에 의해 추적되고 있거나(staged), 이미 커밋된 적이 있다면 다음 명령어를 통해 Git 인덱스에서 제거해야 합니다.

```bash
# Git 캐시에서 .idea 폴더 삭제 (실제 파일은 삭제되지 않음)
git rm -r --cached .idea/
```

## 3. 변경 사항 커밋
위 작업들을 완료한 후, 변경된 `.gitignore`와 인덱스에서 제거된 사항을 커밋합니다.

```bash
git add .gitignore
git commit -m "docs: .idea 디렉토리 git 추적 제외 설정"
```

## 주의 사항
- `git rm -r --cached` 명령어는 Git의 관리 대상에서만 제외할 뿐, 로컬의 실제 파일을 삭제하지는 않습니다.
- 팀 프로젝트의 경우, 개인적인 설정 파일이 공유되지 않도록 주의해야 합니다.
