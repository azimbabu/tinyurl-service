pipeline {
    agent { docker { image 'gradle:5.4.1' } }
    stages {
        stage('build') {
            steps {
                sh 'gradle -v'
            }
        }
    }
}