# Bitbucket Repository Setup Guide
# =================================

## Step 1: Create Repository on Bitbucket

1. Go to https://bitbucket.org
2. Click "Create Repository"
3. Fill in details:
   - Workspace: cdac-demo
   - Repository Name: cicd-microservices
   - Access Level: Private (or Public for demo)
   - Version Control: Git

## Step 2: Clone and Configure

```bash
# Clone the repository
git clone https://bitbucket.org/cdac-demo/cicd-microservices.git
cd cicd-microservices

# Add all microservices
cp -r ../microservices/* ./microservices/

# Create initial commit
git add .
git commit -m "Initial commit: Microservices with CI/CD pipeline"

# Push to Bitbucket
git push -u origin main develop
```

## Step 3: Configure Webhooks

1. Go to Repository Settings > Webhooks
2. Click "Add Webhook"
3. Configure:
   - Title: Jenkins CI/CD Pipeline
   - URL: https://jenkins.cdac-demo.com/bitbucket-hook/
   - Secret: (generate a secure secret)
   - Triggers:
     - Repository > Push
     - Pull Request > Created
     - Pull Request > Updated

## Step 4: Configure Jenkins

1. Install Bitbucket Branch Source Plugin
2. Create Multibranch Pipeline Job
3. Configure:
   - Source: Bitbucket
   - Credentials: Add Bitbucket App Password
   - Owner: cdac-demo
   - Repository: cicd-microservices
   - Auto scan triggers: Periodically (5 minutes)

## Step 5: Configure Credentials

In Jenkins > Manage Jenkins > Manage Credentials:

1. Bitbucket App Password:
   - Username: (your Bitbucket username)
   - Password: (App password from Bitbucket settings)

2. Docker Hub Credentials:
   - Username: (Docker ID)
   - Password: (Docker password)

3. Slack Webhook:
   - Add Slack Webhook URL as Secret Text

## Step 6: Environment Variables

Configure in Jenkins > Manage Jenkins > Configure System:

```
DOCKER_REGISTRY = docker.io
DOCKER_ORG = cdacdemo
SLACK_CHANNEL = #cicd-alerts
SONAR_HOST_URL = http://sonarqube:9000
```
