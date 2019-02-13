pipeline {
  agent none
  options { skipDefaultCheckout() }
  environment {
    BRANCH_NAME = 'master'
  }
  stages {
    stage('Build Trigger') {
      steps {
        build 'asterics-docs/master'
      }
    }
  }
}
