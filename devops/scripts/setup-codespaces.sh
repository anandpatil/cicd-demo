#!/bin/bash

# DevSecOps CI/CD Demo - Codespaces Setup Script
set -e

echo "========================================="
echo "   🚀 DevSecOps CI/CD Demo Setup"
echo "   GitHub Codespaces Edition"
echo "========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if running in Codespaces
if [ "$CODESPACES" = "true" ]; then
    echo -e "${BLUE}✓ Running in GitHub Codespaces${NC}"
else
    echo -e "${YELLOW}⚠ Not running in GitHub Codespaces${NC}"
fi

echo ""
echo -e "${BLUE}1. Checking system dependencies...${NC}"

# Check Docker
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version | cut -d' ' -f3)
    echo -e "${GREEN}✓ Docker ${DOCKER_VERSION}${NC}"
else
    echo -e "${RED}✗ Docker not found${NC}"
fi

# Check Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=")\d+' | head -1)
    echo -e "${GREEN}✓ Java ${JAVA_VERSION}${NC}"
else
    echo -e "${RED}✗ Java not found${NC}"
fi

# Check Maven
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -v 2>&1 | head -1 | cut -d' ' -f3)
    echo -e "${GREEN}✓ Maven ${MVN_VERSION}${NC}"
else
    echo -e "${RED}✗ Maven not found${NC}"
fi

# Check git
if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version | cut -d' ' -f3)
    echo -e "${GREEN}✓ Git ${GIT_VERSION}${NC}"
else
    echo -e "${RED}✗ Git not found${NC}"
fi

# Check gh CLI
if command -v gh &> /dev/null; then
    GH_VERSION=$(gh --version | cut -d' ' -f3)
    echo -e "${GREEN}✓ GitHub CLI ${GH_VERSION}${NC}"
else
    echo -e "${RED}✗ GitHub CLI not found${NC}"
fi

echo ""
echo -e "${BLUE}2. Installing additional tools...${NC}"

# Install utilities
apt-get update -qq
apt-get install -y -qq \
    curl \
    wget \
    jq \
    yq \
    net-tools \
    iputils-ping \
    htop \
    tree \
    vim \
    nano \
    > /dev/null 2>&1

echo -e "${GREEN}✓ Utilities installed${NC}"

# Install Node.js packages for dashboard
if command -v npm &> /dev/null; then
    echo -e "${BLUE}Installing Node.js dependencies...${NC}"
    cd /workspaces/cicd-demo
    
    # Create package.json if doesn't exist
    if [ ! -f "package.json" ]; then
        npm init -y > /dev/null 2>&1
        npm install --save-dev \
            express \
            socket.io \
            morgan \
            body-parser \
            cors \
            dotenv \
            > /dev/null 2>&1
        echo -e "${GREEN}✓ Node.js packages installed${NC}"
    fi
fi

echo ""
echo -e "${BLUE}3. Setting up scripts...${NC}"

# Make scripts executable
chmod +x scripts/*.sh 2>/dev/null || true
echo -e "${GREEN}✓ Scripts are executable${NC}"

echo ""
echo -e "${BLUE}4. Building microservices (optional, skipping for speed)...${NC}"
echo -e "${YELLOW}   Run 'make build' to build all services manually${NC}"

echo ""
echo -e "${BLUE}5. Configuring environment...${NC}"

# Create .env file if doesn't exist
if [ ! -f ".env" ]; then
    cat > .env << 'EOF'
# GitHub Codespaces Environment
CODESPACES=true
CODESPACE_NAME=${CODESPACE_NAME:-local-demo}

# Service Ports
USER_SERVICE_PORT=8081
ORDER_SERVICE_PORT=8082
INVENTORY_SERVICE_PORT=8083

# Docker
DOCKER_REGISTRY=docker.io
DOCKER_ORG=cdacdemo

# Java
JAVA_VERSION=17
SPRING_PROFILES_ACTIVE=demo

# Database (optional)
DATABASE_URL=jdbc:h2:mem:testdb
DATABASE_USER=sa
DATABASE_PASSWORD=

# Demo Mode
DEMO_MODE=true
ENABLE_MOCK_DATA=true
EOF
    echo -e "${GREEN}✓ .env file created${NC}"
else
    echo -e "${GREEN}✓ .env file exists${NC}"
fi

echo ""
echo -e "${BLUE}6. Setting up Git configuration...${NC}"

# Configure git for demos
git config --local user.name "Demo User" 2>/dev/null || true
git config --local user.email "demo@cdac.com" 2>/dev/null || true

echo -e "${GREEN}✓ Git configured${NC}"

echo ""
echo -e "${BLUE}7. Creating helpful aliases...${NC}"

# Add to bashrc
if ! grep -q "CICD_DEMO_ALIASES" ~/.bashrc; then
    cat >> ~/.bashrc << 'EOF'

# CICD_DEMO_ALIASES
alias demo-build='gh workflow run demo-build.yml'
alias demo-status='gh run list --limit 5'
alias demo-watch='gh run watch'
alias demo-logs='gh run view --log'
alias services-start='docker-compose up -d'
alias services-stop='docker-compose down'
alias services-logs='docker-compose logs -f'
alias services-ps='docker-compose ps'
EOF
    echo -e "${GREEN}✓ Aliases created${NC}"
fi

echo ""
echo "========================================="
echo -e "${GREEN}✅ Setup Complete!${NC}"
echo "========================================="
echo ""
echo -e "${BLUE}📚 Available Commands:${NC}"
echo "   demo-build        # Trigger demo build"
echo "   demo-status       # Show recent runs"
echo "   demo-watch        # Watch current run"
echo "   services-start    # Start Docker services"
echo "   services-stop     # Stop Docker services"
echo "   services-ps       # Show running services"
echo ""
echo -e "${BLUE}📖 Quick Start:${NC}"
echo "   1. Run: make start-demo"
echo "   2. Open forwarded ports in VS Code"
echo "   3. View dashboard at http://localhost:5000"
echo "   4. Trigger build: demo-build"
echo "   5. Watch progress: demo-watch"
echo ""
echo -e "${BLUE}🌐 Forwarded Ports:${NC}"
echo "   8081 - User Service"
echo "   8082 - Order Service"
echo "   8083 - Inventory Service"
echo "   3000 - Grafana"
echo "   9090 - Prometheus"
echo "   5000 - Dashboard"
echo ""

# Source bashrc to load aliases
source ~/.bashrc 2>/dev/null || true

echo -e "${GREEN}Ready to demo! 🎉${NC}"
