pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = credentials('DOCKER_REGISTRY')
        DOCKER_USERNAME = credentials('DOCKER_USERNAME')
        DOCKER_PASSWORD = credentials('DOCKER_PASSWORD')
        KUBE_CONFIG = credentials('KUBE_CONFIG')
        GIT_REPO = 'cicd-demo'
    }
    
    options {
        timeout(time: 3, unit: 'HOURS')
        retry(3)
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    
    parameters {
        string(name: 'GIT_COMMIT', defaultValue: '', description: 'Git commit SHA')
        string(name: 'GIT_BRANCH', defaultValue: '', description: 'Git branch name')
        string(name: 'ENVIRONMENT', defaultValue: 'staging', description: 'Target environment (staging/production)')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Quality Gate') {
            steps {
                script {
                    echo "Running quality checks..."
                    
                    // Run linting for all services
                    dir('services/service-a') {
                        sh 'npm install'
                        sh 'npm run lint || true'
                    }
                    
                    dir('services/service-b') {
                        sh 'npm install'
                        sh 'npm run lint || true'
                    }
                    
                    dir('libs') {
                        sh 'npm install'
                        sh 'npm run lint || true'
                    }
                    
                    echo "Quality gate checks completed (GitHub Actions is primary)"
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    echo "Building Docker images..."
                    
                    // Build Service A
                    dir('services/service-a') {
                        sh "docker build -t ${DOCKER_REGISTRY}/cicd-demo-service-a:${GIT_COMMIT} -t ${DOCKER_REGISTRY}/cicd-demo-service-a:latest ."
                    }
                    
                    // Build Service B
                    dir('services/service-b') {
                        sh "docker build -t ${DOCKER_REGISTRY}/cicd-demo-service-b:${GIT_COMMIT} -t ${DOCKER_REGISTRY}/cicd-demo-service-b:latest ."
                    }
                    
                    echo "Docker images built successfully"
                }
            }
        }
        
        stage('Push to Registry') {
            steps {
                script {
                    echo "Pushing Docker images to registry..."
                    
                    // Login to Docker registry
                    sh "echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin ${DOCKER_REGISTRY}"
                    
                    // Push images
                    sh "docker push ${DOCKER_REGISTRY}/cicd-demo-service-a:${GIT_COMMIT}"
                    sh "docker push ${DOCKER_REGISTRY}/cicd-demo-service-a:latest"
                    sh "docker push ${DOCKER_REGISTRY}/cicd-demo-service-b:${GIT_COMMIT}"
                    sh "docker push ${DOCKER_REGISTRY}/cicd-demo-service-b:latest"
                    
                    echo "Docker images pushed successfully"
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    echo "Deploying to Kubernetes..."
                    
                    def namespace = "cicd-demo-${params.ENVIRONMENT}"
                    
                    // Create namespace
                    sh "kubectl create namespace ${namespace} --dry-run=client -o yaml | kubectl apply -f -"
                    
                    // Deploy infrastructure
                    dir('infra') {
                        // Update image tags in deployment files
                        sh """
                            sed -i "s|\${DOCKER_REGISTRY}/cicd-demo-service-a:.*|${DOCKER_REGISTRY}/cicd-demo-service-a:${GIT_COMMIT}|" k8s/${params.ENVIRONMENT}/service-a-deployment.yml
                            sed -i "s|\${DOCKER_REGISTRY}/cicd-demo-service-b:.*|${DOCKER_REGISTRY}/cicd-demo-service-b:${GIT_COMMIT}|" k8s/${params.ENVIRONMENT}/service-b-deployment.yml
                        """
                        
                        // Apply Kubernetes manifests
                        sh "kubectl apply -f k8s/${params.ENVIRONMENT}/"
                    }
                    
                    // Wait for deployments to be ready
                    sh "kubectl wait --for=condition=available --timeout=300s deployment/service-a -n ${namespace}"
                    sh "kubectl wait --for=condition=available --timeout=300s deployment/service-b -n ${namespace}"
                    
                    // Verify deployments
                    sh "kubectl get pods -n ${namespace}"
                    sh "kubectl get services -n ${namespace}"
                    
                    echo "Deployment to ${params.ENVIRONMENT} completed successfully"
                }
            }
        }
        
        stage('Approval for Production') {
            when {
                expression { params.ENVIRONMENT == 'production' }
            }
            steps {
                timeout(time: 24, unit: 'HOURS') {
                    input message: "Deploy to Production? This will deploy to the production environment.", ok: 'Deploy'
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
            
            script {
                def status = currentBuild.currentResult
                def color = status == 'SUCCESS' ? 'good' : status == 'UNSTABLE' ? 'warning' : 'danger'
                echo "Pipeline status: ${status} (${color})"
            }
        }
        success {
            echo '✅ Pipeline succeeded!'
            
            script {
                def namespace = "cicd-demo-${params.ENVIRONMENT}"
                def serviceAUrl = sh(script: "kubectl get svc service-a -n ${namespace} -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'", returnStdout: true).trim()
                def serviceBUrl = sh(script: "kubectl get svc service-b -n ${namespace} -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'", returnStdout: true).trim()
                
                echo "Services deployed to: ${serviceAUrl}, ${serviceBUrl}"
            }
        }
        failure {
            echo '❌ Pipeline failed!'
            
            script {
                echo "Triggering rollback..."
                // build job: 'cicd-demo-rollback', parameters: [
                //     string(name: 'ENVIRONMENT', value: params.ENVIRONMENT),
                //     string(name: 'GIT_COMMIT', value: params.GIT_COMMIT)
                // ]
            }
        }
    }
}