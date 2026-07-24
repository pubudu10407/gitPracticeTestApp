pipeline {
    agent any

    options {
        // Jenkins already checks out the repository automatically.
        skipDefaultCheckout(false)

        // Adds timestamps to Console Output.
        timestamps()
    }

    environment {
        // Docker Hub username/repository
        DOCKER_IMAGE = 'pubudu10407/student-registry'

        // Exact Docker executable path on your computer
        DOCKER_EXE = 'C:/Users/GTC/AppData/Local/Programs/DockerDesktop/resources/bin/docker.exe'
    }

    stages {
        stage('Verify Project Files') {
            steps {
                bat '''
                    @echo off

                    echo ========================================
                    echo PROJECT FILES
                    echo ========================================

                    cd
                    dir

                    if not exist Dockerfile (
                        echo ERROR: Dockerfile was not found.
                        exit /b 1
                    )

                    echo Dockerfile found successfully.
                '''
            }
        }

        stage('Verify Docker') {
            steps {
                bat '''
                    @echo off

                    echo ========================================
                    echo VERIFY DOCKER
                    echo ========================================

                    "%DOCKER_EXE%" version

                    if errorlevel 1 (
                        echo ERROR: Jenkins cannot access Docker.
                        exit /b 1
                    )
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                bat '''
                    @echo off

                    echo ========================================
                    echo BUILD DOCKER IMAGE
                    echo ========================================

                    "%DOCKER_EXE%" build ^
                        --tag "%DOCKER_IMAGE%:%BUILD_NUMBER%" ^
                        --tag "%DOCKER_IMAGE%:latest" ^
                        .

                    if errorlevel 1 (
                        echo ERROR: Docker image build failed.
                        exit /b 1
                    )
                '''
            }
        }

        stage('Verify Docker Image') {
            steps {
                bat '''
                    @echo off

                    echo ========================================
                    echo VERIFY CREATED IMAGE
                    echo ========================================

                    "%DOCKER_EXE%" image inspect "%DOCKER_IMAGE%:%BUILD_NUMBER%" > nul

                    if errorlevel 1 (
                        echo ERROR: The Docker image was not created.
                        exit /b 1
                    )

                    "%DOCKER_EXE%" image ls "%DOCKER_IMAGE%"
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
                        Write-Host "========================================"
                        Write-Host "DOCKER HUB LOGIN"
                        Write-Host "========================================"

                        if ([string]::IsNullOrWhiteSpace($env:DOCKERHUB_USERNAME)) {
                            throw "Docker Hub username was not loaded from Jenkins credentials."
                        }

                        if ([string]::IsNullOrWhiteSpace($env:DOCKERHUB_TOKEN)) {
                            throw "Docker Hub token was not loaded from Jenkins credentials."
                        }

                        Write-Host "Logging in as $env:DOCKERHUB_USERNAME..."

                        $env:DOCKERHUB_TOKEN |
                            & "$env:DOCKER_EXE" login `
                                --username "$env:DOCKERHUB_USERNAME" `
                                --password-stdin

                        if ($LASTEXITCODE -ne 0) {
                            throw "Docker Hub login failed with exit code $LASTEXITCODE."
                        }

                        Write-Host "Docker Hub login succeeded."
                    '''
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                bat '''
                    @echo off

                    echo ========================================
                    echo PUSH VERSIONED IMAGE
                    echo ========================================

                    "%DOCKER_EXE%" push "%DOCKER_IMAGE%:%BUILD_NUMBER%"

                    if errorlevel 1 (
                        echo ERROR: Failed to push numbered image.
                        exit /b 1
                    )

                    echo.
                    echo ========================================
                    echo PUSH LATEST IMAGE
                    echo ========================================

                    "%DOCKER_EXE%" push "%DOCKER_IMAGE%:latest"

                    if errorlevel 1 (
                        echo ERROR: Failed to push latest image.
                        exit /b 1
                    )
                '''
            }
        }
    }

    post {
        success {
            echo "SUCCESS: ${env.DOCKER_IMAGE}:${env.BUILD_NUMBER} was pushed to Docker Hub."
            echo "SUCCESS: ${env.DOCKER_IMAGE}:latest was pushed to Docker Hub."
        }

        failure {
            echo 'PIPELINE FAILED: Check the first failed stage in Console Output.'
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
