pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    parameters {
        choice(name: 'BROWSER', choices: ['chromium', 'firefox', 'webkit'], description: 'Browser to run tests on')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run in headless mode')
    }

    environment {
        BROWSER = "${params.BROWSER ?: 'chromium'}"
        HEADLESS = "${params.HEADLESS ?: true}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install Dependencies') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Install Playwright Browsers') {
            steps {
                sh 'mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"'
            }
        }

        stage('Run Tests') {
            steps {
                sh "mvn test -Dbrowser=${BROWSER} -Dheadless=${HEADLESS}"
            }
            post {
                always {
                    junit 'target/cucumber-reports/cucumber.xml'
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                sh 'mvn allure:report'
            }
        }
    }

    post {
        always {
            allure([
                includeProperties: false,
                jdk: '',
                results: [[path: 'target/allure-results']]
            ])

            archiveArtifacts artifacts: 'target/cucumber-reports/**', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/logs/**', allowEmptyArchive: true
        }

        failure {
            echo 'Tests failed! Check Allure report for details.'
        }

        success {
            echo 'All tests passed successfully!'
        }
    }
}
