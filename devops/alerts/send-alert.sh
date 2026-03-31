#!/bin/bash

# Alert Script for CI/CD Pipeline
# Sends notifications to multiple channels on build events

WEBHOOK_URL="${SLACK_WEBHOOK_URL}"
PAGERDUTY_KEY="${PAGERDUTY_KEY}"
TEAMS_WEBHOOK="${TEAMS_WEBHOOK_URL}"
EMAIL_TO="devops-team@cdac.com"

send_slack_alert() {
    local status=$1
    local color=$2
    local title=$3
    local message=$4
    
    if [ -z "$WEBHOOK_URL" ]; then
        echo "Slack webhook not configured"
        return 1
    fi
    
    curl -s -X POST "$WEBHOOK_URL" \
        -H 'Content-Type: application/json' \
        -d "{
            \"channel\": \"#cicd-alerts\",
            \"username\": \"CI/CD Alert Bot\",
            \"icon_emoji\": \":jenkins:\",
            \"attachments\": [{
                \"color\": \"$color\",
                \"title\": \"$title\",
                \"text\": \"$message\",
                \"footer\": \"CDAC DevSecOps Pipeline\",
                \"ts\": $(date +%s)
            }]
        }"
}

send_teams_alert() {
    local status=$1
    local title=$2
    local message=$3
    
    if [ -z "$TEAMS_WEBHOOK" ]; then
        echo "Teams webhook not configured"
        return 1
    fi
    
    curl -s -X POST "$TEAMS_WEBHOOK" \
        -H 'Content-Type: application/json' \
        -d "{
            \"@type\": \"MessageCard\",
            \"@context\": \"http://schema.org/extensions\",
            \"themeColor\": \"${status}\",
            \"summary\": \"$title\",
            \"sections\": [{
                \"activityTitle\": \"$title\",
                \"activitySubtitle\": \"CDAC DevSecOps Pipeline\",
                \"facts\": [{
                    \"name\": \"Build\",
                    \"value\": \"$message\"
                }],
                \"markdown\": true
            }]
        }"
}

send_pagerduty_alert() {
    local title=$1
    local message=$2
    
    if [ -z "$PAGERDUTY_KEY" ]; then
        echo "PagerDuty key not configured"
        return 1
    fi
    
    curl -s -X POST "https://events.pagerduty.com/v2/enqueue" \
        -H 'Content-Type: application/json' \
        -d "{
            \"routing_key\": \"$PAGERDUTY_KEY\",
            \"event_action\": \"trigger\",
            \"payload\": {
                \"summary\": \"$title\",
                \"severity\": \"critical\",
                \"source\": \"jenkins-cicd\",
                \"custom_details\": {
                    \"message\": \"$message\"
                }
            }
        }"
}

send_build_failure_alert() {
    local build_number=$1
    local branch=$2
    local stage=$3
    
    local message="Build #$build_number failed at stage: $stage on branch: $branch"
    
    echo "Sending failure alerts..."
    send_slack_alert "failed" "#FF0000" "Build #$build_number Failed" "$message"
    send_teams_alert "FF0000" "Build #$build_number Failed" "$message"
    send_pagerduty_alert "Build #$build_number Failed" "$message"
}

send_build_success_alert() {
    local build_number=$1
    local branch=$2
    
    local message="Build #$build_number succeeded on branch: $branch"
    
    echo "Sending success notifications..."
    send_slack_alert "success" "#36a64f" "Build #$build_number Success" "$message"
    send_teams_alert "36a64f" "Build #$build_number Success" "$message"
}

# Example usage
if [ "$1" == "failure" ]; then
    send_build_failure_alert "${BUILD_NUMBER:-1}" "${GIT_BRANCH:-main}" "${STAGE_NAME:-Unknown}"
elif [ "$1" == "success" ]; then
    send_build_success_alert "${BUILD_NUMBER:-1}" "${GIT_BRANCH:-main}"
fi
