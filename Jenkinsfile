pipeline {
  agent none
  options { skipDefaultCheckout() }
  stages {
    stage('Build Trigger') {
      steps {
        build 'asterics-docs/master'
      }
    }
  }
}
