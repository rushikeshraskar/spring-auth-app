# Jenkins CI/CD Setup Guide

This guide explains how to set up Jenkins for continuous integration with your GitHub repository.

## Prerequisites

- Docker and Docker Compose installed
- GitHub account and repository access
- Personal Access Token (PAT) from GitHub

## Starting Jenkins

### 1. Start Jenkins with Docker Compose

```bash
docker-compose up -d jenkins
```

Jenkins will start on `http://localhost:8081`

### 2. Get Initial Admin Password

```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Copy this password for the initial setup.

### 3. Access Jenkins Web Interface

1. Open `http://localhost:8081` in your browser
2. Paste the initial admin password
3. Click "Continue"
4. Select "Install suggested plugins"
5. Create your first admin user
6. Configure Jenkins URL as `http://localhost:8081`
7. Click "Save and Finish"

## Configuring GitHub Integration

### 1. Create GitHub Personal Access Token (PAT)

1. Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Click "Generate new token"
3. Give it a name: "Jenkins CI"
4. Select scopes:
   - `repo` (full control of private repositories)
   - `admin:repo_hook` (write access to hooks)
   - `admin:org_hook` (if using organization repos)
5. Click "Generate token" and copy it

### 2. Add GitHub Credentials to Jenkins

1. Go to Jenkins → Manage Jenkins → Credentials
2. Click "System"
3. Click "Global credentials (unrestricted)"
4. Click "Add Credentials"
5. Select Kind: **Username with password**
6. Username: `<your-github-username>`
7. Password: `<your-github-PAT>`
8. ID: `github-credentials`
9. Click "Create"

### 3. Create New Pipeline Job

1. Click "New Item"
2. Enter job name: `spring-auth-app`
3. Select "Pipeline"
4. Click "OK"
5. Configure the job:

   **General:**
   - ✓ Check "GitHub project"
   - Project URL: `https://github.com/rushikeshraskar/spring-auth-app`

   **Build Triggers:**
   - ✓ Check "GitHub hook trigger for GITScm polling"

   **Pipeline:**
   - Definition: "Pipeline script from SCM"
   - SCM: "Git"
   - Repository URL: `https://github.com/rushikeshraskar/spring-auth-app.git`
   - Credentials: Select `github-credentials`
   - Branch Specifier: `*/main`
   - Script Path: `Jenkinsfile`

6. Click "Save"

### 4. Configure GitHub Webhook

1. Go to your GitHub repository
2. Settings → Webhooks
3. Click "Add webhook"
4. Payload URL: `http://<your-jenkins-ip>:8081/github-webhook/`
   - For local testing: `http://localhost:8081/github-webhook/`
5. Content type: `application/json`
6. Events: Select "Push events" and "Pull request events"
7. ✓ Check "Active"
8. Click "Add webhook"

**Note:** For local Jenkins behind a router/firewall, you'll need to expose Jenkins to the internet or use ngrok:

```bash
# Install ngrok and create a tunnel
ngrok http 8081
# Use the provided ngrok URL in the webhook: https://<ngrok-id>.ngrok.io/github-webhook/
```

## Pipeline Stages

The Jenkinsfile defines two main stages:

### 1. Build Stage
- Checks out source code from GitHub
- Runs `mvn clean package` to compile and package the application
- Skips tests to speed up the build

### 2. Integration Tests Stage
- Runs `mvn verify` to execute all integration tests
- Publishes test results to Jenkins
- Collects JUnit test reports

## Manual Trigger

You can also trigger builds manually:

1. Go to your pipeline job
2. Click "Build Now"

## Viewing Build Results

1. Click on a build number in "Build History"
2. View:
   - Build logs in "Console Output"
   - Test results in "Test Result"
   - Build artifacts if generated

## Troubleshooting

### Jenkins won't connect to GitHub

- Verify your PAT has correct scopes
- Check that the credentials are properly saved in Jenkins
- Ensure your repository URL is correct

### Webhook not triggering builds

- Verify the webhook is active in GitHub
- Check Jenkins logs: `docker logs jenkins`
- For local setup, consider using ngrok for external access

### Tests failing

- Check the console output for error messages
- Verify MSSQL database is running: `docker-compose ps`
- Run tests locally: `mvn verify`

## Monitoring and Logs

### View Jenkins Logs

```bash
docker logs -f jenkins
```

### View Build Logs

- Through Jenkins web interface: Job → Build Number → Console Output

### Check Jenkins Home Directory

```bash
docker exec jenkins ls -la /var/jenkins_home/
```

## Stopping Jenkins

```bash
docker-compose down
```

To preserve configuration:

```bash
docker stop jenkins
```

To completely remove Jenkins (including configuration):

```bash
docker-compose down -v
```

## Next Steps

1. Test the webhook by making a commit to the main branch
2. Verify that Jenkins automatically triggers a build
3. Check the build results and test reports
4. Configure additional features like:
   - Email notifications
   - Slack notifications
   - Code coverage analysis
   - Performance metrics

## Additional Resources

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [GitHub Plugin](https://plugins.jenkins.io/github/)
- [Pipeline Documentation](https://www.jenkins.io/doc/book/pipeline/)
