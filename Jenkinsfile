pipeline {
  agent none
  options { skipDefaultCheckout() }
  stages {
    stage('Build Trigger') {
      steps {
        env.BRANCH_NAME = 'master'
        build 'asterics-docs/master'
      }
    }
  }
}
