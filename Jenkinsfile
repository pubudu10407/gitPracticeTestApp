pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'pubudu10407/student-registry'

        DOCKER_EXE = 'C:\\Users\\GTC\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe'
    }

    stages {
        stage('Build Docker Image') {
            steps {
                bat '''
                    @echo off

                    echo Building Docker image...

                    "%DOCKER_EXE%" build ^
                      -t %DOCKER_IMAGE%:%BUILD_NUMBER% ^
                      -t %DOCKER_IMAGE%:latest ^
                      .
                '''
            }
        }

        stage('Verify Image') {
            steps {
                bat '''
                    @echo off

                    echo Verifying Docker image...
                    "%DOCKER_EXE%" image inspect %DOCKER_IMAGE%:%BUILD_NUMBER%
                '''
            }
        }

        stage('Docker Hub Login') {
    steps {
        withCredentials([
            usernamePassword(
                credentialsId: 'dockerhub-credentials',
                usernameVariable: 'DOCKERHUB_USERNAME',
                passwordVariable: 'DOCKERHUB_TOKEN'
            )
        ]) {
            powershell '''
                Write-Host "Logging in to Docker Hub..."

                $env:DOCKERHUB_TOKEN |
                    & $env:DOCKER_EXE login `
                        --username $env:DOCKERHUB_USERNAME `
                        --password-stdin

                if ($LASTEXITCODE -ne 0) {
                    throw "Docker Hub login failed with exit code $LASTEXITCODE"
                }

                Write-Host "Docker Hub login successful."
            '''
        }
    }
}

        stage('Push Docker Image') {
            steps {
                bat '''
                    @echo off

                    echo Pushing numbered image...
                    "%DOCKER_EXE%" push %DOCKER_IMAGE%:%BUILD_NUMBER%

                    echo Pushing latest image...
                    "%DOCKER_EXE%" push %DOCKER_IMAGE%:latest
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Docker image built and pushed to Docker Hub.'
        }

        failure {
            echo 'FAILED: Check the failed pipeline stage.'
        }

        always {
            script {
                bat(
                    returnStatus: true,
                    script: '''
                        @echo off
                        "%DOCKER_EXE%" logout
                    '''
                )
            }
        }
    }
}