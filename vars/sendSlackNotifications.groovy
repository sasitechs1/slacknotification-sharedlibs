
node {
    
try{

  
 
  
  echo "the build name is: ${env.JOB_NAME} "
  echo "the build  number is: ${env.BUILD_NUMBER}"
  
  properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), [$class: 'JobLocalConfiguration', changeReasonComment: ''], pipelineTriggers([pollSCM('* * * * *')])])
  
  def  MavenHome = tool name : 'maven3.8.5'
  stage('getcode'){  
   git branch: 'development', credentialsId: 'f2b0ec8f-8deb-4583-bc32-20f466db601d', url: 'https://github.com/sasitechs1/maven-web-application.git'
  }  

   stage('buildTheJob') {
        sh "${MavenHome}/bin/mvn clean package"  
  }
    stage('sonarcloud') {
        sh "${MavenHome}/bin/mvn clean sonar:sonar "
   }   
    stage ('nexusartifact') {
         sh "${MavenHome}/bin/mvn clean deploy"
    }
     stage('deploy to tomcat') {
    sshagent(['980302f0-5430-4688-8f76-1057023e3baa']) {
    sh "scp -o StrictHostKeyChecking=no target/maven-web-application.war ec2-user@172.31.41.1:/opt/apache-tomcat-9.0.65/webapps/" 
    }
 }
  
 } //try closing
  catch(e){
  currentBuild.result="FAILURE"
  throw e
  }//catch closing
  finally{
  slackNotifications(currentBuild.result)
  }def slackNotifications(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus =  buildStatus ?: 'SUCCESS'
  //buildStatus = buildStatus ? "SUCCESS":"FAILURE"

  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"

  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    colorName = 'ORANGE'
    colorCode = '#FFA500'
  } else if (buildStatus == 'SUCCESS') {
    colorName = 'GREEN'
    colorCode = '#00FF00'
  } else {
    colorName = 'RED'
    colorCode = '#FF0000'
  }
 

  // Send notifications
  slackSend (color: colorCode, message: summary, channel: "#devops")
}
