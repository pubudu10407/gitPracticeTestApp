pipeline {
    agent any

    stages {
        stage('Checkout Confirmation') {
            steps {
                bat '''
                    echo ==============================
                    echo GITHUB CHECKOUT SUCCESSFUL
                    echo ==============================

                    echo Current directory:
                    cd

                    echo.
                    echo Repository files:
                    dir

                    echo.
                    echo Current Git commit:
                    git log -1 --oneline
                '''
            }
        }
    }
}