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
            bat '''
                @echo off

                echo Logging in to Docker Hub...

                echo %DOCKERHUB_TOKEN% > docker-token.txt

                type docker-token.txt | "%DOCKER_EXE%" login ^
                    --username "%DOCKERHUB_USERNAME%" ^
                    --password-stdin

                set LOGIN_RESULT=%ERRORLEVEL%

                del docker-token.txt

                if not "%LOGIN_RESULT%"=="0" (
                    echo Docker Hub login failed.
                    exit /b %LOGIN_RESULT%
                )

                echo Docker Hub login successful.
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