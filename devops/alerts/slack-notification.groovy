pipeline {
    agent any

    environment {
        SLACK_WEBHOOK_URL = credentials('slack-webhook-url')
        SLACK_CHANNEL = '#cicd-alerts'
    }

    stages {
        stage('Build') {
            steps {
                echo 'Building...'
            }
        }
    }

    post {
        failure {
            steps {
                script {
                    def color = "#FF0000"
                    def title = "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
                    def buildUrl = "${env.BUILD_URL}"
                    def branch = "${env.GIT_BRANCH ?: 'N/A'}"
                    def commit = "${env.GIT_COMMIT?.take(7) ?: 'N/A'}"
                    def stageName = "${env.STAGE_NAME ?: 'Unknown'}"
                    
                    def payload = [
                        channel: SLACK_CHANNEL,
                        username: "Jenkins Bot",
                        icon_emoji: ":jenkins:",
                        attachments: [
                            [
                                color: color,
                                title: title,
                                title_link: buildUrl,
                                fields: [
                                    [title: "Branch", value: branch, short: true],
                                    [title: "Commit", value: commit, short: true],
                                    [title: "Failed Stage", value: stageName, short: true],
                                    [title: "Duration", value: currentBuild.durationString, short: true]
                                ],
                                footer: "CDAC DevSecOps Pipeline",
                                ts: System.currentTimeMillis() / 1000
                            ]
                        ]
                    ]

                    sh """
                        curl -X POST \
                        -H 'Content-type: application/json' \
                        -d '${groovy.json.JsonOutput.toJson(payload)}' \
                        "${SLACK_WEBHOOK_URL}"
                    """
                }
            }
        }
        
        success {
            steps {
                script {
                    def payload = [
                        channel: SLACK_CHANNEL,
                        username: "Jenkins Bot",
                        icon_emoji: ":jenkins:",
                        attachments: [
                            [
                                color: "#36a64f",
                                title: "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                                title_link: env.BUILD_URL,
                                fields: [
                                    [title: "Branch", value: env.GIT_BRANCH ?: 'N/A', short: true],
                                    [title: "Duration", value: currentBuild.durationString, short: true]
                                ],
                                footer: "CDAC DevSecOps Pipeline",
                                ts: System.currentTimeMillis() / 1000
                            ]
                        ]
                    ]
                    
                    sh """
                        curl -X POST \
                        -H 'Content-type: application/json' \
                        -d '${groovy.json.JsonOutput.toJson(payload)}' \
                        "${SLACK_WEBHOOK_URL}"
                    """
                }
            }
        }
    }
}
