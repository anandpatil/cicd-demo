# Jenkins Email Configuration
# Configure in Jenkins > Manage Jenkins > Configure System > E-mail Notification

# SMTP Server Settings
JENKINS_SMTP_SERVER=smtp.gmail.com
JENKINS_SMTP_PORT=587
JENKINS_SMTP_USE_TLS=true

# Email Recipients
EMAIL_TO=devops-team@cdac.com,developers@cdac.com
EMAIL_FROM=jenkins@cdac-demo.com

# Email Templates
# Configure in Jenkins > Manage Jenkins > Configure System > Extended E-mail Notification

# Failure Email Template
FAILURE_EMAIL_TEMPLATE = '''
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
        .header { background-color: #dc3545; color: white; padding: 15px; border-radius: 5px 5px 0 0; }
        .content { background-color: #f8f9fa; padding: 20px; border: 1px solid #dee2e6; }
        .footer { margin-top: 20px; font-size: 12px; color: #6c757d; }
        .build-info { margin: 10px 0; }
        .btn { display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="header">
        <h2>🚨 BUILD FAILURE ALERT</h2>
    </div>
    <div class="content">
        <h3>${env.JOB_NAME} - Build #${env.BUILD_NUMBER}</h3>
        <div class="build-info">
            <p><strong>Branch:</strong> ${env.GIT_BRANCH}</p>
            <p><strong>Commit:</strong> ${env.GIT_COMMIT?.take(7)}</p>
            <p><strong>Failed Stage:</strong> ${env.STAGE_NAME}</p>
            <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
            <p><strong>Failed By:</strong> ${causedBy}</p>
        </div>
        <p><a href="${env.BUILD_URL}" class="btn">View Build Logs</a></p>
    </div>
    <div class="footer">
        <p>This is an automated alert from CDAC DevSecOps Pipeline.</p>
        <p>Please check the build logs and fix the issue.</p>
    </div>
</body>
</html>
'''

# Success Email Template
SUCCESS_EMAIL_TEMPLATE = '''
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
        .header { background-color: #28a745; color: white; padding: 15px; border-radius: 5px 5px 0 0; }
        .content { background-color: #f8f9fa; padding: 20px; border: 1px solid #dee2e6; }
        .footer { margin-top: 20px; font-size: 12px; color: #6c757d; }
        .btn { display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="header">
        <h2>✅ BUILD SUCCESS</h2>
    </div>
    <div class="content">
        <h3>${env.JOB_NAME} - Build #${env.BUILD_NUMBER}</h3>
        <p><strong>Branch:</strong> ${env.GIT_BRANCH}</p>
        <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
        <p><a href="${env.BUILD_URL}" class="btn">View Build Details</a></p>
    </div>
    <div class="footer">
        <p>CDAC DevSecOps Pipeline</p>
    </div>
</body>
</html>
'''

# Configuration for Jenkins Email Extension Plugin
emailExt {
    recipients 'devops-team@cdac.com developers@cdac.com'
    replyTo 'jenkins@cdac-demo.com'
    subject '$DEFAULT_SUBJECT'
    body '${DEFAULT_CONTENT}'
    attachLog true
    compressLog false
    alwaysUseBody true
}
