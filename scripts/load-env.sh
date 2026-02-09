#!/bin/bash

# .env 파일에서 환경변수 로드
if [ -f .env ]; then
    echo "📦 Loading environment variables from .env..."
    export $(cat .env | grep -v '^#' | xargs)
    echo "✅ Environment variables loaded successfully!"
    echo ""
    echo "Available tokens:"
    echo "  - FIGMA_PERSONAL_ACCESS_TOKEN: ${FIGMA_PERSONAL_ACCESS_TOKEN:0:20}..."
    echo "  - GITHUB_PERSONAL_ACCESS_TOKEN: ${GITHUB_PERSONAL_ACCESS_TOKEN:0:20}..."
else
    echo "❌ .env file not found!"
    exit 1
fi
