pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'pubudu10407/student-registry'
	DOCKER_EXE = 'C:\Users\GTC\AppData\Local\Programs\DockerDesktop\resources\bin\docker.exe'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Downloading source code from GitHub...'
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                bat '''
                    @echo off

                    echo Building Docker image...

                    docker build ^
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

                    echo Checking the created image...
                    docker image inspect %DOCKER_IMAGE%:%BUILD_NUMBER%
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
                        echo %DOCKERHUB_TOKEN% | docker login ^
                          --username %DOCKERHUB_USERNAME% ^
                          --password-stdin
                    '''
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                bat '''
                    @echo off

                    echo Pushing version %BUILD_NUMBER%...
                    docker push %DOCKER_IMAGE%:%BUILD_NUMBER%

                    echo Pushing latest version...
                    docker push %DOCKER_IMAGE%:latest
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Docker image built and pushed to Docker Hub.'
        }

        failure {
            echo 'FAILED: Open Console Output and check the failed stage.'
        }

        always {
            bat '''
                @echo off
                docker logout
            '''
        }
    }
}