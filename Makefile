.PHONY: help setup start-demo stop-demo demo-build demo-logs services-start services-stop services-ps services-logs dashboard build clean test security-scan docker-build

# Colors
BLUE := \033[0;34m
GREEN := \033[0;32m
YELLOW := \033[1;33m
NC := \033[0m

help:
	@echo "$(BLUE)╔════════════════════════════════════════════════╗$(NC)"
	@echo "$(BLUE)║  DevSecOps CI/CD Demo - Makefile               ║$(NC)"
	@echo "$(BLUE)╚════════════════════════════════════════════════╝$(NC)"
	@echo ""
	@echo "$(GREEN)Setup & Environment:$(NC)"
	@echo "  make setup              - Setup Codespaces environment"
	@echo "  make clean              - Clean build artifacts"
	@echo ""
	@echo "$(GREEN)Demo Commands:$(NC)"
	@echo "  make start-demo         - Start interactive demo"
	@echo "  make demo-build         - Trigger demo build"
	@echo "  make demo-logs          - Show demo build logs"
	@echo ""
	@echo "$(GREEN)Local Services:$(NC)"
	@echo "  make services-start     - Start Docker Compose services"
	@echo "  make services-stop      - Stop Docker Compose services"
	@echo "  make services-ps        - Show running services"
	@echo "  make services-logs      - Show service logs"
	@echo ""
	@echo "$(GREEN)Dashboard:$(NC)"
	@echo "  make dashboard          - Start web dashboard (port 5000)"
	@echo ""
	@echo "$(GREEN)Build & Test:$(NC)"
	@echo "  make build              - Build all microservices"
	@echo "  make test               - Run all tests"
	@echo "  make docker-build       - Build Docker images"
	@echo ""
	@echo "$(GREEN)Security:$(NC)"
	@echo "  make security-scan      - Run security scanning"
	@echo "  make trivy-scan         - Run Trivy scanner"
	@echo ""

## Setup & Environment

setup:
	@echo "$(BLUE)Setting up Codespaces environment...$(NC)"
	@bash scripts/setup-codespaces.sh

clean:
	@echo "$(BLUE)Cleaning build artifacts...$(NC)"
	@for service in microservices/*/; do \
		cd $$service && mvn clean > /dev/null 2>&1; \
		cd ../../; \
	done
	@find . -type d -name target -exec rm -rf {} + 2>/dev/null || true
	@docker system prune -f 2>/dev/null || true
	@echo "$(GREEN)✓ Cleanup complete$(NC)"

## Demo Commands

start-demo:
	@bash scripts/demo.sh menu

demo-build:
	@bash scripts/demo.sh build

demo-logs:
	@bash scripts/demo.sh logs

## Local Services

services-start:
	@echo "$(BLUE)Starting Docker Compose services...$(NC)"
	@docker-compose up -d
	@echo "$(GREEN)✓ Services started$(NC)"
	@echo ""
	@echo "Service URLs:"
	@echo "  • User Service:      http://localhost:8081"
	@echo "  • Order Service:     http://localhost:8082"
	@echo "  • Inventory Service: http://localhost:8083"

services-stop:
	@echo "$(BLUE)Stopping Docker Compose services...$(NC)"
	@docker-compose down
	@echo "$(GREEN)✓ Services stopped$(NC)"

services-ps:
	@docker-compose ps

services-logs:
	@docker-compose logs -f

## Dashboard

dashboard:
	@echo "$(BLUE)Starting dashboard...$(NC)"
	@echo "$(GREEN)✓ Dashboard available at: http://localhost:5000$(NC)"
	@cd scripts && python3 -m flask --app dashboard run --host=0.0.0.0

## Build & Test

build:
	@echo "$(BLUE)Building all microservices...$(NC)"
	@for service in user-service order-service inventory-service; do \
		echo "$(BLUE)Building $$service...$(NC)"; \
		cd microservices/$$service && \
		mvn clean package -DskipTests=true -q && \
		echo "$(GREEN)✓ $$service built$(NC)" || echo "✗ Build failed"; \
		cd ../../; \
	done

test:
	@echo "$(BLUE)Running tests...$(NC)"
	@for service in user-service order-service inventory-service; do \
		echo "$(BLUE)Testing $$service...$(NC)"; \
		cd microservices/$$service && \
		mvn test -q && \
		echo "$(GREEN)✓ $$service tests passed$(NC)" || echo "✗ Tests failed"; \
		cd ../../; \
	done

docker-build:
	@echo "$(BLUE)Building Docker images...$(NC)"
	@docker build -t user-service:latest -q ./microservices/user-service
	@echo "$(GREEN)✓ user-service:latest$(NC)"
	@docker build -t order-service:latest -q ./microservices/order-service
	@echo "$(GREEN)✓ order-service:latest$(NC)"
	@docker build -t inventory-service:latest -q ./microservices/inventory-service
	@echo "$(GREEN)✓ inventory-service:latest$(NC)"

## Security

security-scan:
	@echo "$(BLUE)Running security scan...$(NC)"
	@gh workflow run security-scan.yml
	@echo "$(GREEN)✓ Security scan triggered$(NC)"

trivy-scan:
	@echo "$(BLUE)Running Trivy scanner...$(NC)"
	@trivy fs --format json . > trivy-results.json && \
	echo "$(GREEN)✓ Scan complete: trivy-results.json$(NC)" || \
	echo "$(YELLOW)⚠ Trivy not available$(NC)"

## GitHub Actions

workflow-run:
	@echo "$(BLUE)Available workflows:$(NC)"
	@gh workflow list --all

workflow-demo:
	@echo "$(BLUE)Triggering demo build...$(NC)"
	@gh workflow run demo-build.yml
	@echo "$(GREEN)✓ Workflow triggered$(NC)"
	@echo "$(YELLOW)Watch at: https://github.com/$(shell git remote get-url origin | sed 's/.*github.com\///;s/\.git$$//')/actions$(NC)"

workflow-watch:
	@gh run watch

workflow-logs:
	@gh run view --log

## Info

status:
	@echo "$(BLUE)System Status:$(NC)"
	@echo ""
	@echo "Docker:"
	@docker --version
	@echo ""
	@echo "Java:"
	@java -version 2>&1 | head -1
	@echo ""
	@echo "Maven:"
	@mvn -v | head -1
	@echo ""
	@echo "Git:"
	@git --version
	@echo ""

info:
	@echo "$(BLUE)Project Information:$(NC)"
	@echo ""
	@echo "Repository:"
	@git remote -v | head -1
	@echo ""
	@echo "Current Branch:"
	@git rev-parse --abbrev-ref HEAD
	@echo ""
	@echo "Microservices:"
	@ls microservices/
	@echo ""
	@echo "Workflows:"
	@ls .github/workflows/
	@echo ""
