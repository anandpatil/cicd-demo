// Jenkins Pipeline for Infrastructure
// This pipeline handles infrastructure changes

pipeline {
    agent any
    
    // Environment variables
    environment {
        DOCKER_REGISTRY = credentials('DOCKER_REGISTRY')
        DOCKER_USERNAME = credentials('DOCKER_USERNAME')
        DOCKER_PASSWORD = credentials('DOCKER_PASSWORD')
        KUBE_CONFIG = credentials('KUBE_CONFIG')
        TERRAFORM_BACKEND_ADDRESS = credentials('TERRAFORM_BACKEND_ADDRESS')
        TERRAFORM_BACKEND_LOCK = credentials('TERRAFORM_BACKEND_LOCK')
        GIT_REPO = 'cicd-demo-infra'
        ENVIRONMENT = "${env.BRANCH_NAME == 'main' ? 'production' : 'staging'}"
    }
    
    options {
        // Timeout after 3 hours (Terraform can take time)
        timeout(time: 3, unit: 'HOURS')
        
        // Retry failed stages
        retry(2)
        
        // Build discarder
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    
    parameters {
        string(name: 'GIT_COMMIT', defaultValue: '', description: 'Git commit SHA')
        string(name: 'GIT_BRANCH', defaultValue: '', description: 'Git branch name')
        string(name: 'REPOSITORY', defaultValue: '', description: 'Repository name')
        booleanParam(name: 'DESTROY', defaultValue: false, description: 'Destroy infrastructure instead of applying')
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
                    echo "Action: ${params.DESTROY ? 'DESTROY' : 'APPLY'}"
                }
            }
        }
        
        stage('Checkout Code') {
            steps {
                script {
                    // Checkout the infrastructure repository
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
        
        stage('Validate Infrastructure') {
            steps {
                script {
                    // Validate Kubernetes manifests
                    def k8sFiles = findFiles glob: "k8s/${ENVIRONMENT}/**/*.yml", excludes: '**/node_modules/**'
                    for (def file : k8sFiles) {
                        sh "kubectl create --dry-run=client -f ${file.path} || true"
                    }
                    
                    // Validate Terraform
                    dir('terraform') {
                        sh '''
                            terraform init -backend=false
                            terraform validate
                            terraform fmt -check
                        '''
                    }
                }
            }
        }
        
        stage('Build Base Docker Image') {
            when {
                changeset "docker/**"
            }
            steps {
                script {
                    def imageName = "${DOCKER_REGISTRY}/cicd-demo-base:${params.GIT_COMMIT}"
                    def latestImage = "${DOCKER_REGISTRY}/cicd-demo-base:latest"
                    
                    dir('docker') {
                        // Build base Docker image
                        sh "docker build -t ${imageName} -t ${latestImage} -f Dockerfile.base ."
                    }
                    
                    // Login and push
                    sh "echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin ${DOCKER_REGISTRY}"
                    sh "docker push ${imageName}"
                    sh "docker push ${latestImage}"
                }
            }
        }
        
        stage('Terraform Plan') {
            when {
                not { expression { params.DESTROY } }
            }
            steps {
                script {
                    dir('terraform') {
                        // Initialize Terraform with backend
                        sh '''
                            terraform init \
                                -backend-config="address=${TERRAFORM_BACKEND_ADDRESS}" \
                                -backend-config="lock_table=${TERRAFORM_BACKEND_LOCK}"
                        '''
                        
                        // Select workspace
                        sh "terraform workspace select ${ENVIRONMENT} || terraform workspace new ${ENVIRONMENT}"
                        
                        // Plan changes
                        sh "terraform plan -out=tfplan -var='environment=${ENVIRONMENT}'"
                        
                        // Save plan artifact
                        archiveArtifacts artifacts: 'terraform/tfplan', fingerprint: true
                    }
                }
            }
        }
        
        stage('Terraform Destroy Plan') {
            when {
                expression { params.DESTROY }
            }
            steps {
                script {
                    dir('terraform') {
                        // Initialize Terraform with backend
                        sh '''
                            terraform init \
                                -backend-config="address=${TERRAFORM_BACKEND_ADDRESS}" \
                                -backend-config="lock_table=${TERRAFORM_BACKEND_LOCK}"
                        '''
                        
                        // Select workspace
                        sh "terraform workspace select ${ENVIRONMENT}"
                        
                        // Plan destroy
                        sh "terraform plan -destroy -out=tfplan -var='environment=${ENVIRONMENT}'"
                        
                        // Save plan artifact
                        archiveArtifacts artifacts: 'terraform/tfplan', fingerprint: true
                    }
                }
            }
        }
        
        stage('Approval for Terraform Apply') {
            when {
                not { expression { params.DESTROY } }
                branch 'main'
            }
            steps {
                timeout(time: 24, unit: 'HOURS') {
                    input message: "Apply Terraform changes to ${ENVIRONMENT}?", ok: 'Apply'
                }
            }
        }
        
        stage('Approval for Terraform Destroy') {
            when {
                expression { params.DESTROY }
                branch 'main'
            }
            steps {
                timeout(time: 24, unit: 'HOURS') {
                    input message: "DESTROY Terraform infrastructure in ${ENVIRONMENT}? This cannot be undone!", ok: 'Destroy'
                }
            }
        }
        
        stage('Terraform Apply') {
            when {
                not { expression { params.DESTROY } }
                branch 'main'
            }
            steps {
                script {
                    dir('terraform') {
                        // Apply Terraform changes
                        sh "terraform apply -auto-approve tfplan"
                    }
                }
            }
        }
        
        stage('Terraform Destroy') {
            when {
                expression { params.DESTROY }
                branch 'main'
            }
            steps {
                script {
                    dir('terraform') {
                        // Apply Terraform destroy
                        sh "terraform apply -auto-approve tfplan"
                    }
                }
            }
        }
        
        stage('Deploy Kubernetes Manifests') {
            when {
                not { expression { params.DESTROY } }
                changeset "k8s/**"
            }
            steps {
                script {
                    def namespace = "cicd-demo-${ENVIRONMENT}"
                    
                    // Create or update namespace
                    sh "kubectl create namespace ${namespace} --dry-run=client -o yaml | kubectl apply -f -"
                    
                    // Deploy Kubernetes manifests
                    sh "kubectl apply -f k8s/${ENVIRONMENT}/"
                    
                    // Wait for deployments to be ready
                    def deployments = sh(script: "kubectl get deployments -n ${namespace} -o jsonpath='{.items[*].metadata.name}'", returnStdout: true).trim().split()
                    for (def deployment : deployments) {
                        sh "kubectl rollout status deployment/${deployment} -n ${namespace} --timeout=60s"
                    }
                    
                    // Verify deployments
                    sh "kubectl get pods -n ${namespace}"
                    sh "kubectl get services -n ${namespace}"
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
                def action = params.DESTROY ? 'DESTROY' : 'APPLY'
                
                echo "Sending notification: ${status} - ${action} - ${color}"
            }
        }
        success {
            echo "✅ Infrastructure ${params.DESTROY ? 'destruction' : 'deployment'} pipeline succeeded!"
        }
        failure {
            echo "❌ Infrastructure ${params.DESTROY ? 'destruction' : 'deployment'} pipeline failed!"
        }
    }
}

// Helper functions
@NonCPS
def getTerraformOutput(String key) {
    def output = sh(script: "terraform output -json", returnStdout: true).trim()
    def json = new groovy.json.JsonSlurper().parseText(output)
    return json[key]?.value ?: null
}