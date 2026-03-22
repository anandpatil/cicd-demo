# GitHub Branch Protection Rules Configuration Guide
# This file documents the recommended protection settings for main branch

## File Location
Create this in `.github/branch-protection.json` for documentation

## Recommended Settings for 'main' Branch

### Basic Settings
- Branch name pattern: `main`

### Require pull request reviews before merging
✅ Require pull request reviews before merging
- Number of approving reviews required: 1
- Require review from code owners: ✅
- Require approval of reviews before merging stale pull requests: ✅
- Require status checks to pass before merging: ✅
  - CodeQL Analysis
  - Build & Test Microservices  
  - Security Scanning
  - Trivy Vulnerability Scan
  - Docker Build & Push

### Require status checks to pass
✅ Enable
- Required status checks to pass before merging:
  - build-and-test
  - security-scan (sast-scan, trivy-scan, dependency-check)
  - docker-build
  
### Require branches to be up to date
✅ Enable - Require branches to be up to date before merging

### Require code owner review
✅ Enable - Require review from Code Owners

### Require approval of reviews
✅ Enable - Require approval when the default branch or a protected branch is changed

### Require status checks to pass
✅ Enable - Require status checks to pass before merging

### Require signed commits
Optional: ✅ Enable - Require commits to be signed

### Restrict who can push to matching branches
✅ Restrict pushes that create matching branches
- Allow specified actors to bypass required pull requests: None

### Require conversation resolution before merging
✅ Enable - Require that conversations are resolved before merging

## Setup Instructions

### Via GitHub UI:
1. Go to Repository Settings
2. Left sidebar → Branches
3. Click "Add rule"
4. Configure as documented above
5. Click "Create"

### Via GitHub CLI:
```bash
# Note: Direct API required for full automation
gh api repos/OWNER/REPO/branches/main/protection \
  -f required_status_checks='{"strict":true,"contexts":["build-and-test","security-scan","docker-build"]}' \
  -f enforce_admins=true \
  -f required_pull_request_reviews='{"required_approving_review_count":1}' \
  -f require_code_owner_reviews=true
```
