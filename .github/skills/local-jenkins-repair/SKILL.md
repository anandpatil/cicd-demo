---
name: local-jenkins-repair
description: 'Repair local Jenkins issues in this repository. Use for jenkins.war startup failures, port 8080 conflicts, DemoCICD-feature job corruption, .jenkins-home cleanup, inline job config regeneration, and Windows-specific Jenkins pipeline problems.'
argument-hint: 'Describe the Jenkins symptom, job name, and any recent pipeline or XML error.'
user-invocable: true
---

# Local Jenkins Repair

## When To Use
- Jenkins started with `jenkins/jenkins.war` does not come up cleanly.
- Port 8080 conflicts or stale Java processes block startup.
- `.jenkins-home/jobs/DemoCICD-feature/config.xml` is missing or corrupted.
- Windows-specific pipeline issues appear in `bat`, PowerShell, Groovy quoting, or secret parameter handling.

## Procedure
1. Check whether Jenkins is already running and whether port 8080 is occupied.
2. Inspect startup output before changing files; many failures are runtime issues, not config issues.
3. If job XML is broken, compare live job config with `jenkins/Jenkinsfile` and prefer a full, clean rewrite of the job config.
4. If Jenkins state is stale, clear only safe transient directories such as workspace or logs, not secrets or plugins.
5. Restart Jenkins with `JENKINS_HOME` pointing at `.jenkins-home` and verify initialization completes.

## Repo-Specific Rules
- `DemoCICD-feature` is known to use inline pipeline XML and may drift from `jenkins/Jenkinsfile`.
- Startup on Windows may require explicit heap settings when plugin loading stalls.
- Password parameters like `SONAR_TOKEN` can be `hudson.util.Secret`; convert to string before trimming or concatenating.
