# Project Guidelines

## Architecture
- This repository is a DevSecOps demo built around three Spring Boot microservices: `user-service`, `order-service`, and `inventory-service`.
- CI/CD changes must keep local Jenkins flows in `jenkins/Jenkinsfile`, GitHub Actions workflows in `.github/workflows/`, and Kubernetes manifests in `k8s/` aligned.
- Prefer updating existing manifests and workflows over introducing parallel paths unless the task explicitly requires a dedicated workflow.

## Build And Test
- Java services use Maven and target Java 17.
- For service-level validation, prefer running commands from the specific service folder under `microservices/<service>/`.
- For `user-service`, preserve unit-test and JaCoCo quality gates already configured in the Maven build and GitHub workflow.

## Jenkins And Local CI/CD
- Treat `.jenkins-home/` as live Jenkins state. Read current contents before changing any job config because Jenkins job XML can be edited outside chat.
- When fixing the `DemoCICD-feature` job, keep the inline pipeline synchronized with `jenkins/Jenkinsfile` and prefer full-file regeneration over partial regex-style XML surgery if the config is corrupted.
- On Windows, assume Jenkins commonly runs from `jenkins/jenkins.war` with `JENKINS_HOME` set to `.jenkins-home` and may need larger heap settings for stable startup.

## Workflow Conventions
- Prefer OIDC-based AWS authentication in GitHub Actions over static credentials.
- For container deploy changes, keep image names, namespaces, and ports explicit instead of relying on implicit defaults.
- Fail fast on quality-gate issues when the pipeline stage is intended to be blocking.

## Editing Expectations
- Make focused changes. Do not reformat unrelated YAML, XML, or Jenkins pipeline sections.
- When touching GitHub Actions or Jenkins pipeline steps, preserve Windows and Linux behavior unless the task is explicitly platform-specific.
- Treat new feature work as additive by default: do not change or regress existing behavior, pipelines, manifests, or interfaces unless the request explicitly asks for a modification.
