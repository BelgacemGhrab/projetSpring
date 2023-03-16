pipeline{
  agent any
  stages{
    stage("hello"){
        when{
            branch "master"
        }
      steps{
        sh "echo Hello from master"
      }
    }
  }
}
