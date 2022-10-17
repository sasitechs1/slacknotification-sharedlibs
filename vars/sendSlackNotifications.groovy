node {
  def maven : tool name ="maven3.8.5"
     stage('checkout the code') {
      git branch: 'development', credentialsId: 'f2b0ec8f-8deb-4583-bc32-20f466db601d', url: 'https://github.com/sasitechs1/maven-web-application.git'
  }  
     stage ('build.) {
      sh "${maven}/bin/mvn clean package"
      }
      }
