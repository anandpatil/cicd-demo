#!/bin/bash

# DevSecOps CI/CD Demo - Interactive Demo Script
# Usage: ./demo.sh [scenario]

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m'

# Functions
print_header() {
    echo ""
    echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║ ${CYAN}DevSecOps CI/CD Pipeline Demo${BLUE}         ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
    echo ""
}

print_section() {
    echo ""
    echo -e "${MAGENTA}▶ $1${NC}"
    echo -e "${MAGENTA}$(printf '─%.0s' {1..40})${NC}"
    echo ""
}

print_step() {
    echo -e "${CYAN}  ➜ $1${NC}"
}

print_success() {
    echo -e "${GREEN}  ✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}  ⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}  ✗ $1${NC}"
}

pause_demo() {
    if [ "$INTERACTIVE" = "true" ]; then
        echo ""
        read -p "Press Enter to continue..." < /dev/tty
    fi
}

# Check prerequisites
check_prerequisites() {
    print_section "Checking Prerequisites"
    
    local missing=0
    local optional_missing=0
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker not found"
        missing=$((missing+1))
    else
        print_success "Docker $(docker --version | cut -d' ' -f3)"
    fi
    
    # Check Java
    if ! command -v java &> /dev/null; then
        print_error "Java not found"
        missing=$((missing+1))
    else
        print_success "Java $(java -version 2>&1 | head -1 | tail -1)"
    fi
    
    # Check Maven (Optional - only needed for build scenarios)
    if ! command -v mvn &> /dev/null; then
        print_warning "Maven not found (optional for non-build scenarios)"
        optional_missing=$((optional_missing+1))
    else
        print_success "Maven available"
    fi
    
    # Check git
    if ! command -v git &> /dev/null; then
        print_error "Git not found"
        missing=$((missing+1))
    else
        print_success "Git available"
    fi
    
    if [ $missing -gt 0 ]; then
        print_error "Missing $missing required prerequisites"
        return 1
    fi
    
    if [ $optional_missing -gt 0 ]; then
        print_warning "Some features may be limited without Maven"
    fi
}

# Demo 1: Show Pipeline Structure
demo_pipeline_structure() {
    print_section "GitHub Actions Pipeline Structure"
    
    print_step "Workflow files:"
    echo ""
    ls -lh .github/workflows/
    echo ""
    
    print_step "Pipeline stages:"
    echo ""
    echo -e "${CYAN}ci-cd-pipeline.yml:${NC}"
    echo "  1. 🔨 build-and-test          - Maven build & unit tests"
    echo "  2. 🔒 security-scan            - Trivy, OWASP, CodeQL"
    echo "  3. 🐳 docker-build             - Build & push images"
    echo "  4. 📤 deploy-staging           - Deploy to staging"
    echo "  5. 💨 smoke-tests              - API health checks"
    echo "  6. 🔔 notify                   - Slack/Email alerts"
    echo ""
    
    print_step "View workflow:"
    cat .github/workflows/ci-cd-pipeline.yml | head -20
}

# Demo 2: Build Microservices Locally
demo_build_microservices() {
    print_section "Building Microservices Locally"
    
    print_step "Building User Service..."
    if [ -d "microservices/user-service" ]; then
        cd microservices/user-service
        if [ -f "pom.xml" ]; then
            mvn clean package -DskipTests=false -q 2>/dev/null || print_warning "Build skipped"
            print_success "User Service built"
        fi
        cd ../../
    fi
    
    print_step "Building Order Service..."
    if [ -d "microservices/order-service" ]; then
        cd microservices/order-service
        if [ -f "pom.xml" ]; then
            mvn clean package -DskipTests=false -q 2>/dev/null || print_warning "Build skipped"
            print_success "Order Service built"
        fi
        cd ../../
    fi
    
    print_step "Building Inventory Service..."
    if [ -d "microservices/inventory-service" ]; then
        cd microservices/inventory-service
        if [ -f "pom.xml" ]; then
            mvn clean package -DskipTests=false -q 2>/dev/null || print_warning "Build skipped"
            print_success "Inventory Service built"
        fi
        cd ../../
    fi
}

# Demo 3: Docker Build
demo_docker_build() {
    print_section "Docker Image Building"
    
    print_step "Building User Service Docker image..."
    docker build -t user-service:latest -q ./microservices/user-service 2>/dev/null && \
        print_success "user-service:latest built" || print_warning "Docker build skipped"
    
    print_step "Building Order Service Docker image..."
    docker build -t order-service:latest -q ./microservices/order-service 2>/dev/null && \
        print_success "order-service:latest built" || print_warning "Docker build skipped"
    
    print_step "Building Inventory Service Docker image..."
    docker build -t inventory-service:latest -q ./microservices/inventory-service 2>/dev/null && \
        print_success "inventory-service:latest built" || print_warning "Docker build skipped"
    
    echo ""
    print_step "Built images:"
    docker images | grep -E "user-service|order-service|inventory-service" || print_warning "No images built"
}

# Demo 4: Security Scanning
demo_security_scan() {
    print_section "Security Scanning"
    
    print_step "Trivy vulnerability scan:"
    echo -e "${YELLOW}(Demo mode - showing scan structure)${NC}"
    echo ""
    echo "  Scan types:"
    echo "    • File system vulnerabilities"
    echo "    • Dockerfile analysis"
    echo "    • Base image scanning"
    echo "    • Dependency analysis"
    echo ""
    
    print_step "GitHub CodeQL SAST:"
    echo -e "${YELLOW}(Requires CodeQL initialization)${NC}"
    echo ""
    echo "  Checks:"
    echo "    • SQL Injection risks"
    echo "    • Cross-site scripting (XSS)"
    echo "    • Command injection"
    echo "    • Insecure randomness"
    echo ""
}

# Demo 5: Show Workflows
demo_show_workflows() {
    print_section "GitHub Workflows Overview"
    
    print_step "Main CI/CD Pipeline:"
    echo ""
    gh workflow list --all 2>/dev/null || echo -e "${YELLOW}gh CLI not authenticated${NC}"
    echo ""
}

# Demo 6: Trigger GitHub Action
demo_trigger_action() {
    print_section "Triggering GitHub Actions"
    
    print_step "Available workflows:"
    echo "  • demo-build.yml        - Quick demo build"
    echo "  • ci-cd-pipeline.yml    - Full CI/CD pipeline"
    echo "  • security-scan.yml     - Security scanning"
    echo ""
    
    if command -v gh &> /dev/null; then
        print_step "Checking GitHub CLI authentication..."
        if gh auth status &>/dev/null; then
            print_success "GitHub CLI authenticated"
            echo ""
            print_step "Trigger options:"
            echo "  • gh workflow run demo-build.yml"
            echo "  • gh workflow run security-scan.yml"
            echo "  • gh workflow run ci-cd-pipeline.yml"
        else
            print_warning "GitHub CLI not authenticated"
            echo "  Authenticate with: gh auth login"
        fi
    else
        print_warning "GitHub CLI (gh) not installed"
    fi
    echo ""
}

# Demo 7: Deploy with Docker Compose
demo_deploy_compose() {
    print_section "Deploying with Docker Compose"
    
    if [ -f "docker-compose.yml" ]; then
        print_step "Starting services..."
        docker-compose up -d 2>/dev/null && \
            print_success "Services started" || print_warning "Docker Compose failed"
        
        echo ""
        print_step "Service URLs:"
        echo "  • User Service:      http://localhost:8081"
        echo "  • Order Service:     http://localhost:8082"
        echo "  • Inventory Service: http://localhost:8083"
        echo ""
        
        print_step "Health checks:"
        sleep 2
        
        if curl -s http://localhost:8081 > /dev/null 2>&1; then
            print_success "User Service responding"
        else
            print_warning "User Service not ready yet"
        fi
        
        print_step "Running services:"
        docker-compose ps 2>/dev/null || true
    else
        print_warning "docker-compose.yml not found"
    fi
    echo ""
}

# Demo 8: View Logs
demo_view_logs() {
    print_section "Viewing Pipeline Logs"
    
    if command -v gh &> /dev/null; then
        print_step "Recent workflow runs:"
        gh run list --limit 10 2>/dev/null || print_warning "No runs available"
        echo ""
    else
        print_warning "GitHub CLI not available"
    fi
}

# Demo 9: Interactive Menu
interactive_menu() {
    print_header
    
    while true; do
        echo -e "${YELLOW}Select Demo:${NC}"
        echo ""
        echo "  1. Pipeline Structure"
        echo "  2. Build Microservices"
        echo "  3. Docker Build"
        echo "  4. Security Scanning"
        echo "  5. GitHub Workflows"
        echo "  6. Trigger GitHub Action"
        echo "  7. Deploy with Docker Compose"
        echo "  8. View Logs"
        echo "  9. Run All Demos"
        echo "  0. Exit"
        echo ""
        read -p "Enter choice [0-9]: " choice
        
        case $choice in
            1) demo_pipeline_structure && pause_demo ;;
            2) demo_build_microservices && pause_demo ;;
            3) demo_docker_build && pause_demo ;;
            4) demo_security_scan && pause_demo ;;
            5) demo_show_workflows && pause_demo ;;
            6) demo_trigger_action && pause_demo ;;
            7) demo_deploy_compose && pause_demo ;;
            8) demo_view_logs && pause_demo ;;
            9) 
                demo_pipeline_structure
                demo_build_microservices
                demo_docker_build
                demo_security_scan
                demo_show_workflows
                demo_trigger_action
                demo_deploy_compose
                ;;
            0) 
                echo ""
                echo -e "${GREEN}Thanks for watching! 👋${NC}"
                exit 0
                ;;
            *) 
                print_error "Invalid choice"
                ;;
        esac
    done
}

# Main
main() {
    print_header
    
    # Check prerequisites
    if ! check_prerequisites; then
        print_error "Prerequisites check failed"
        exit 1
    fi
    
    pause_demo
    
    # Parse arguments
    INTERACTIVE=${INTERACTIVE:-true}
    
    case "${1:-menu}" in
        structure)
            demo_pipeline_structure
            ;;
        build)
            demo_build_microservices
            ;;
        docker)
            demo_docker_build
            ;;
        security)
            demo_security_scan
            ;;
        workflows)
            demo_show_workflows
            ;;
        trigger)
            demo_trigger_action
            ;;
        deploy)
            demo_deploy_compose
            ;;
        logs)
            demo_view_logs
            ;;
        all)
            demo_pipeline_structure && pause_demo
            demo_build_microservices && pause_demo
            demo_docker_build && pause_demo
            demo_security_scan && pause_demo
            demo_show_workflows && pause_demo
            demo_trigger_action && pause_demo
            demo_deploy_compose && pause_demo
            demo_view_logs
            ;;
        menu|interactive)
            interactive_menu
            ;;
        *)
            echo -e "${RED}Unknown scenario: $1${NC}"
            echo ""
            echo "Usage: $0 [scenario]"
            echo ""
            echo "Scenarios:"
            echo "  structure   - Show pipeline structure"
            echo "  build       - Build microservices"
            echo "  docker      - Build Docker images"
            echo "  security    - Show security scanning"
            echo "  workflows   - Show GitHub workflows"
            echo "  trigger     - Trigger GitHub Actions"
            echo "  deploy      - Deploy with Docker Compose"
            echo "  logs        - View logs"
            echo "  all         - Run all demos"
            echo "  menu        - Interactive menu (default)"
            exit 1
            ;;
    esac
}

main "$@"
