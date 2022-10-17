pipeline {
 agent any;
 tools {
  maven 'maven3.8.5'
}

 stages{
 stage ( 'checkout code') {
 steps { 
   git branch: 'development', credentialsId: 'f2b0ec8f-8deb-4583-bc32-20f466db601d', url: 'https://github.com/sasitechs1/maven-web-application.git'
 } 
}

 stage ('build') {
 steps{
  sh "mvn clean package" 
  }
}
stage ('sonar repo'){
    steps{
     sh "mvn sonar:sonar deploy"
    }
}
stage('deploy to tomcat') {
    steps {
    sshagent(['980302f0-5430-4688-8f76-1057023e3baa']) {
    sh "scp -o StrictHostKeyChecking=no target/maven-web-application.war ec2-user@172.31.41.1:/opt/apache-tomcat-9.0.65/webapps/" 
   }
   }
}
 }    
}
