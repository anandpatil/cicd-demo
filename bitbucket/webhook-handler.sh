#!/bin/bash

# Bitbucket Webhook Handler for Jenkins
# This script processes incoming webhook payloads from Bitbucket

set -e

WEBHOOK_SECRET="${WEBHOOK_SECRET:-your-webhook-secret}"
JENKINS_URL="${JENKINS_URL:-http://jenkins:8080}"
JENKINS_USER="${JENKINS_USER:-admin}"
JENKINS_TOKEN="${JENKINS_TOKEN:-your-jenkins-api-token}"

verify_webhook_signature() {
    local signature=$(echo "$HTTP_X_HUB_SIGNATURE" | sed 's/sha1=//')
    local body=$(cat)
    local expected=$(echo -n "sha1=${body}" | openssl dgst -sha1 -hmac "$WEBHOOK_SECRET" | sed 's/^.* //')
    
    [ "$signature" == "$expected" ]
}

parse_push_event() {
    local payload="$1"
    
    echo "BITBUCKET_PUSH=true"
    echo "GIT_BRANCH=$(echo "$payload" | jq -r '.push.changes[0].new.name')"
    echo "GIT_COMMIT=$(echo "$payload" | jq -r '.push.changes[0].new.target.hash')"
    echo "GIT_AUTHOR=$(echo "$payload" | jq -r '.actor.display_name')"
    echo "REPO_FULL_NAME=$(echo "$payload" | jq -r '.repository.full_name')"
}

parse_pullrequest_event() {
    local payload="$1"
    
    echo "BITBUCKET_PR=true"
    echo "PR_NUMBER=$(echo "$payload" | jq -r '.pullrequest.id')"
    echo "PR_TITLE=$(echo "$payload" | jq -r '.pullrequest.title')"
    echo "PR_SOURCE_BRANCH=$(echo "$payload" | jq -r '.pullrequest.source.branch.name')"
    echo "PR_TARGET_BRANCH=$(echo "$payload" | jq -r '.pullrequest.destination.branch.name')"
    echo "PR_AUTHOR=$(echo "$payload" | jq -r '.pullrequest.author.display_name')"
}

trigger_jenkins_build() {
    local job_name="$1"
    local token="$2"
    
    curl -s -X POST \
        "${JENKINS_URL}/job/${job_name}/buildWithParameters" \
        -u "${JENKINS_USER}:${JENKINS_TOKEN}" \
        --data "token=${token}" \
        --data "GIT_BRANCH=${GIT_BRANCH}" \
        --data "GIT_COMMIT=${GIT_COMMIT}"
}

main() {
    local event_type="${HTTP_X_EVENT_KEY:-Unknown}"
    local payload=$(cat)
    
    echo "Received Bitbucket event: $event_type"
    
    case "$event_type" in
        "repo:push")
            export $(parse_push_event "$payload")
            trigger_jenkins_build "cicd-microservices-pipeline" "bitbucket-token"
            ;;
        "pullrequest:created"|"pullrequest:updated")
            export $(parse_pullrequest_event "$payload")
            trigger_jenkins_build "cicd-microservices-pr-pipeline" "pr-token"
            ;;
        *)
            echo "Unhandled event type: $event_type"
            ;;
    esac
    
    echo "Webhook processed successfully"
}

if [ "$REQUEST_METHOD" == "POST" ]; then
    main
else
    echo "This endpoint only accepts POST requests"
    exit 1
fi
