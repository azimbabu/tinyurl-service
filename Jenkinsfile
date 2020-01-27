//pipeline {
//    agent { docker { image 'gradle:5.4.1' } }
//    stages {
//        stage('build') {
//            steps {
//                sh 'gradle -v'
//            }
//        }
//    }
//}

pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'echo "Hello World"'
                sh '''
                    echo "Multiline shell steps works too"
                    ls -lah
                '''
            }
        }

        stage('Deploy') {
            steps {
                retry(3) {
                    sh 'echo "Fake deploy"'
                }

                timeout(time: 1, unit: 'MINUTES') {
                    sh 'echo "Fake timeout"'
                    sh 'sleep 70'
                }
            }
        }
    }
}