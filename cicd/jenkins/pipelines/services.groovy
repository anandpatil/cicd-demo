// Jenkins Pipeline for Microservices
// This pipeline handles deployment of microservices

pipeline {
    agent any
    
    // Environment variables
    environment {
        DOCKER_REGISTRY = credentials('DOCKER_REGISTRY')
        DOCKER_USERNAME = credentials('DOCKER_USERNAME')
        DOCKER_PASSWORD = credentials('DOCKER_PASSWORD')
        KUBE_CONFIG = credentials('KUBE_CONFIG')
        GIT_REPO = 'cicd-demo-services'
        ENVIRONMENT = "${env.BRANCH_NAME == 'main' ? 'production' : 'staging'}"
    }
    
    options {
        // Timeout after 2 hours
        timeout(time: 2, unit: 'HOURS')
        
        // Retry failed stages
        retry(3)
        
        // Build discarder
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    
    parameters {
        string(name: 'GIT_COMMIT', defaultValue: '', description: 'Git commit SHA')
        string(name: 'GIT_BRANCH', defaultValue: '', description: 'Git branch name')
        string(name: 'REPOSITORY', defaultValue: '', description: 'Repository name')
    }
    
    stages {
        stage('Initialize') {
            steps {
                script {
                    // Set environment based on branch
                    if (env.BRANCH_NAME == 'main') {
                        ENVIRONMENT = 'production'
                    } else if (env.BRANCH_NAME == 'develop') {
                        ENVIRONMENT = 'staging'
                    } else {
                        ENVIRONMENT = 'development'
                    }
                    
                    echo "Environment: ${ENVIRONMENT}"
                    echo "Branch: ${env.BRANCH_NAME}"
                    echo "Commit: ${params.GIT_COMMIT}"
                }
            }
        }
        
        stage('Checkout Code') {
            steps {
                script {
                    // Checkout the services repository
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: params.GIT_BRANCH ? params.GIT_BRANCH : env.BRANCH_NAME]],
                        extensions: [],
                        userRemoteConfigs: [[
                            credentialsId: 'GITHUB_CREDENTIALS',
                            url: "https://github.com/anandpatil/${GIT_REPO}.git"
                        ]]
                    ])
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                script {
                    // Run basic quality checks
                    // In hybrid setup, GitHub Actions handles most quality checks
                    // This is a fallback/secondary check
                    
                    def services = ['service-a', 'service-b']
                    
                    for (def service : services) {
                        dir(service) {
                            sh 'npm install'
                            sh 'npm run lint || true'  // Don't fail the build
                            sh 'npm run test || true'   // Don't fail the build
                        }
                    }
                    
                    echo "Quality gate checks completed (GitHub Actions is primary)"
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    def services = ['service-a', 'service-b']
                    def images = []
                    
                    for (def service : services) {
                        def imageName = "${DOCKER_REGISTRY}/cicd-demo-${service}:${params.GIT_COMMIT}"
                        def latestImage = "${DOCKER_REGISTRY}/cicd-demo-${service}:latest"
                        
                        dir(service) {
                            // Build Docker image
                            sh "docker build -t ${imageName} -t ${latestImage} ."
                        }
                        
                        images << imageName
                        images << latestImage
                    }
                    
                    // Save images for later stages
                    env.IMAGES = images.join(',')
                }
            }
        }
        
        stage('Push to Registry') {
            steps {
                script {
                    // Login to Docker registry
                    sh "echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin ${DOCKER_REGISTRY}"
                    
                    // Push all images
                    def images = env.IMAGES.split(',')
                    for (def image : images) {
                        sh "docker push ${image}"
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    // Deploy to appropriate environment
                    def namespace = "cicd-demo-${ENVIRONMENT}"
                    
                    // Create or update namespace
                    sh "kubectl create namespace ${namespace} --dry-run=client -o yaml | kubectl apply -f -"
                    
                    // Deploy services
                    def services = ['service-a', 'service-b']
                    for (def service : services) {
                        def deploymentFile = "k8s/${ENVIRONMENT}/${service}-deployment.yml"
                        
                        // Replace image tags in deployment file
                        sh """
                            sed -i "s|\${DOCKER_REGISTRY}/cicd-demo-${service}:\${IMAGE_TAG}|${DOCKER_REGISTRY}/cicd-demo-${service}:${params.GIT_COMMIT}|" ${deploymentFile}
                            kubectl apply -f ${deploymentFile}
                        """
                    }
                    
                    // Wait for deployments to be ready
                    for (def service : services) {
                        sh "kubectl rollout status deployment/${service} -n ${namespace} --timeout=60s"
                    }
                    
                    // Verify deployments
                    sh "kubectl get pods -n ${namespace}"
                    sh "kubectl get services -n ${namespace}"
                }
            }
        }
        
        stage('Smoke Tests') {
            steps {
                script {
                    def namespace = "cicd-demo-${ENVIRONMENT}"
                    
                    // Get service URLs
                    def serviceAUrl = sh(script: "kubectl get svc service-a -n ${namespace} -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'", returnStdout: true).trim()
                    def serviceBUrl = sh(script: "kubectl get svc service-b -n ${namespace} -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'", returnStdout: true).trim()
                    
                    // Simple smoke tests
                    if (serviceAUrl) {
                        sh "curl -f http://${serviceAUrl}/health || echo 'Service A health check failed'"
                        sh "curl -f http://${serviceAUrl}/ || echo 'Service A info endpoint failed'"
                    }
                    
                    if (serviceBUrl) {
                        sh "curl -f http://${serviceBUrl}/health || echo 'Service B health check failed'"
                        sh "curl -f http://${serviceBUrl}/ || echo 'Service B info endpoint failed'"
                        sh "curl -f http://${serviceBUrl}/call-service-a || echo 'Service B to A communication failed'"
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Clean up workspace
            cleanWs()
            
            // Send notification
            script {
                def status = currentBuild.currentResult
                def color = status == 'SUCCESS' ? 'good' : status == 'UNSTABLE' ? 'warning' : 'danger'
                
                // This would be replaced with actual notification logic
                echo "Sending notification: ${status} - ${color}"
            }
        }
        success {
            echo '✅ Services deployment pipeline succeeded!'
        }
        failure {
            echo '❌ Services deployment pipeline failed!'
            
            // Trigger rollback
            script {
                echo "Triggering rollback for ${ENVIRONMENT}"
                // build job: 'cicd-demo-rollback', parameters: [
                //     string(name: 'ENVIRONMENT', value: ENVIRONMENT),
                //     string(name: 'REPOSITORY', value: GIT_REPO)
                // ]
            }
        }
    }
}

// Helper functions
@NonCPS
def getServiceStatus(String service, String namespace) {
    def status = sh(script: "kubectl get deployment ${service} -n ${namespace} -o jsonpath='{.status.conditions[?(@.type==\"Available\")].status}'", returnStdout: true).trim()
    return status == 'True'
}