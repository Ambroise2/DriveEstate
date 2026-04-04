#!/bin/bash
# ─────────────────────────────────────────────────────────────────────────────
#  DriveEstate — Kenya's Premier Cars & Land Marketplace
#  Java 21 + Spring Boot 3.2 Enterprise Application
# ─────────────────────────────────────────────────────────────────────────────

set -e

GREEN='\033[0;32m'
GOLD='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m'

echo ""
echo -e "${GOLD}╔══════════════════════════════════════════════════════╗${NC}"
echo -e "${GOLD}║          DriveEstate — Kenya Marketplace             ║${NC}"
echo -e "${GOLD}║          Java 21 + Spring Boot 3.2 Enterprise        ║${NC}"
echo -e "${GOLD}╚══════════════════════════════════════════════════════╝${NC}"
echo ""

# Check Java
if ! command -v java &>/dev/null; then
    echo -e "${RED}❌ Java not found. Install Java 21+: https://adoptium.net${NC}"; exit 1
fi

JAVA_VER=$(java -version 2>&1 | head -1 | grep -oP '(?<=")\d+')
if [ "$JAVA_VER" -lt 17 ]; then
    echo -e "${RED}❌ Java 17+ required. Found Java $JAVA_VER.${NC}"; exit 1
fi
echo -e "${GREEN}✅ Java $JAVA_VER detected${NC}"

# Check Maven
if ! command -v mvn &>/dev/null; then
    echo -e "${RED}❌ Maven not found.${NC}"
    echo "   Install: https://maven.apache.org/install.html"
    echo "   Or: brew install maven  (macOS)"
    echo "   Or: sudo apt install maven  (Ubuntu/Debian)"
    exit 1
fi
echo -e "${GREEN}✅ Maven detected${NC}"

# Build
echo ""
echo -e "${GOLD}🔨 Building DriveEstate...${NC}"
mvn package -DskipTests -q

echo ""
echo -e "${GREEN}✅ Build successful!${NC}"
echo ""
echo -e "${GOLD}🚀 Starting DriveEstate on port 8080...${NC}"
echo ""
echo -e "  ${GREEN}📌 Client Portal:   ${NC}http://localhost:8080"
echo -e "  ${GREEN}🔐 Admin Panel:     ${NC}http://localhost:8080/admin"
echo -e "  ${GREEN}🗄️  DB Console:      ${NC}http://localhost:8080/h2-console"
echo ""
echo -e "  ${GOLD}Admin login:  ${NC}admin@driveestate.co.ke  /  Admin@1234"
echo -e "  ${GOLD}Seller login: ${NC}james@example.com        /  Password@123"
echo ""
echo "  Press Ctrl+C to stop"
echo "─────────────────────────────────────────────────────────────────────────"

java -jar target/driveestate-1.0.0.jar
