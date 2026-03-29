---
name: aws-eks-release
description: 'Release user-service or other microservices from GitHub Actions to AWS ECR and EKS. Use for OIDC authentication, ECR push, kubectl deploy/update, LoadBalancer exposure, kubeconfig updates, and production namespace rollout verification.'
argument-hint: 'Describe the service, AWS account/region, ECR repository, EKS cluster, and whether the workflow is new or being fixed.'
user-invocable: true
---

# AWS ECR/EKS Release

## When To Use
- Creating or fixing GitHub Actions that publish Docker images to Amazon ECR.
- Deploying `user-service` or other services to EKS.
- Replacing ad hoc local `kubectl create deployment` commands with repeatable CI/CD automation.

## Procedure
1. Confirm the service folder, Docker context, target AWS region, ECR repository, EKS cluster name, and namespace.
2. Use GitHub OIDC with `aws-actions/configure-aws-credentials` instead of long-lived AWS keys.
3. Log in to ECR, ensure the repository exists, build the image, and push with an explicit tag.
4. Update kubeconfig for the target cluster, apply deployment and service manifests, and wait for rollout completion.
5. Print deployment and service status so the workflow output is actionable.

## Repo-Specific Rules
- Prefer separate workflows for focused deployments when only one service is being shipped.
- Keep exposed service ports explicit; for `user-service`, container and service traffic should map to port 8081 unless the task explicitly changes it.
- If a manifest already exists in `k8s/`, prefer aligning it with the workflow rather than inventing conflicting deployment definitions.
- When introducing a new release feature, keep existing workflows and deployment behavior unchanged by default; make additive changes unless a breaking or modifying change is explicitly requested.
