---
description: "Use when editing Jenkins pipelines, GitHub Actions workflows, Docker build steps, ECR/EKS deploy logic, or Kubernetes delivery automation in this repo. Covers Windows Jenkins behavior, local .jenkins-home state, and keeping local and GitHub CI/CD flows aligned."
applyTo: jenkins/Jenkinsfile,.github/workflows/**,.jenkins-home/jobs/**/config.xml
---

# CI/CD Pipeline Guidance

- Keep `jenkins/Jenkinsfile` as the source of truth for local Jenkins behavior.
- If a live Jenkins job uses inline XML pipeline content, mirror important fixes into both the repository Jenkinsfile and the live job config only when the task requires the live job to work immediately.
- On Windows Jenkins nodes, be careful with Groovy string escaping, batch quoting, PowerShell quoting, and `hudson.util.Secret` password parameters.
- Prefer commands that are deterministic on clean runners: explicit tool setup, explicit paths, explicit namespaces, and explicit rollout checks.
- For Kubernetes deploy steps, include rollout verification and useful diagnostics on failure.
- For AWS delivery, prefer GitHub OIDC plus `aws-actions/configure-aws-credentials` and avoid embedding static AWS credentials in workflow files.
