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
                $ErrorActionPreference = "Stop"

                Write-Host "Logging in to Docker Hub as $env:DOCKERHUB_USERNAME..."

                if ([string]::IsNullOrWhiteSpace($env:DOCKERHUB_USERNAME)) {
                    throw "Docker Hub username is empty."
                }

                if ([string]::IsNullOrWhiteSpace($env:DOCKERHUB_TOKEN)) {
                    throw "Docker Hub token is empty."
                }

                # Remove accidental spaces or line breaks.
                $token = $env:DOCKERHUB_TOKEN.Trim()

                # Write token without BOM and without a newline.
                $tokenFile = Join-Path $env:TEMP "jenkins-docker-token.txt"
                $utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
                [System.IO.File]::WriteAllText(
                    $tokenFile,
                    $token,
                    $utf8WithoutBom
                )

                try {
                    cmd.exe /d /c "type `"$tokenFile`" | `"$env:DOCKER_EXE`" login --username `"$env:DOCKERHUB_USERNAME`" --password-stdin"

                    if ($LASTEXITCODE -ne 0) {
                        throw "Docker Hub login failed with exit code $LASTEXITCODE."
                    }

                    Write-Host "Docker Hub login successful."
                }
                finally {
                    Remove-Item $tokenFile -Force -ErrorAction SilentlyContinue
                }
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
