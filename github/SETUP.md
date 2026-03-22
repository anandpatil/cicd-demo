# GitHub Repository Setup Guide for CDAC DevSecOps Demo

## Step 1: Create GitHub Repository

1. Go to https://github.com
2. Click "New repository"
3. Settings:
   - Name: `cicd-demo`
   - Description: "DevSecOps CI/CD Pipeline Demo - Jenkins, Docker, GitHub Actions"
   - Public or Private
   - ✅ Add README
   - ✅ Add .gitignore (Maven)

## Step 2: Push Local Code to GitHub

```bash
cd cicd-demo
git remote add origin https://github.com/anandpatil/cicd-demo.git
git branch -M main
git push -u origin main
```

## Step 3: Configure GitHub Secrets

Go to Repository → Settings → Secrets and Variables → Actions

Add these secrets:

| Secret Name | Value | Description |
|------------|-------|-------------|
| `DOCKER_USERNAME` | your-docker-id | Docker Hub username |
| `DOCKER_TOKEN` | your-token | Docker Hub access token |
| `DOCKER_ORG` | cdacdemo | Docker organization |
| `SLACK_WEBHOOK_URL` | https://hooks.slack.com/... | Slack webhook |
| `EMAIL_USERNAME` | your-email@gmail.com | SMTP username |
| `EMAIL_PASSWORD` | app-password | SMTP app password |
| `ALERT_EMAIL` | devops@cdac.com | Alert recipient |

## Step 4: Enable GitHub Actions

1. Go to Repository → Actions tab
2. You should see the workflows already configured
3. Click "I understand my workflows" to enable them

## Step 5: Configure Webhook (Optional - for Jenkins)

Go to Repository → Settings → Webhooks → Add webhook:

- Payload URL: `https://your-jenkins.com/github-webhook/`
- Content type: `application/json`
- Events: ✅ Push, ✅ Pull requests

## Step 6: Branch Protection (Optional)

Go to Repository → Settings → Branches → Add rule:

- Branch name pattern: `main`
- ✅ Require pull request reviews
- ✅ Require status checks to pass
- ✅ Do not allow bypassing

## Step 7: Set Up GitHub Pages (for Presentation)

1. Go to Repository → Settings → Pages
2. Source: Deploy from branch
3. Branch: `gh-pages` / `root`
4. Your presentation will be at: `https://anandpatil.github.io/cicd-demo/`

## Demo Workflows

### 1. Main CI/CD Pipeline
```bash
# Triggers on push to main/develop or PR
# Runs: Build → Test → Security Scan → Docker Build → Deploy
```

### 2. Security Scan (Scheduled)
```bash
# Runs weekly
# Trivy, OWASP Dependency Check, CodeQL
```

### 3. Demo Build (Manual)
```bash
# Can be triggered manually from Actions tab
# Quick build for demo purposes
```

## Verifying the Setup

### Check GitHub Actions
```bash
# View workflow runs
gh run list
```

### Trigger a Test Build
```bash
# Push a change
echo "# Test" >> test.md
git add .
git commit -m "Test commit"
git push origin main
```

## Repository Structure
```
cicd-demo/
├── .github/
│   └── workflows/
│       ├── ci-cd-pipeline.yml    # Main CI/CD workflow
│       ├── security-scan.yml     # Scheduled security scans
│       └── demo-build.yml        # Quick demo build
├── microservices/
│   ├── user-service/
│   ├── order-service/
│   └── inventory-service/
├── jenkins/
│   └── Jenkinsfile               # Jenkins pipeline (alternative)
├── docker/
├── alerts/
├── k8s/
└── docs/
```

## GitHub CLI Commands

```bash
# Check status
gh run status

# Watch a run
gh run watch

# Download artifacts
gh run download

# View logs
gh run view --log

# Trigger workflow manually
gh workflow run demo-build.yml
```
