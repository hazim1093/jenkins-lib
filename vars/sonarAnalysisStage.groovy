import groovy.json.JsonOutput

def call(projectName, projectVersion, sonarSources = ".", 
        javaBinaries = "target/classes", junitRpoertPaths = "target/surefire-reports",
        jacocoReportPaths = "target/jacoco.exec") {

    stage("Sonar Analysis") {
        sonarNode(sonarScannerImage: 'newtmitch/sonar-scanner:3.2.0') {
                sh "/root/sonar-scanner/bin/sonar-scanner "
        }

        stage("Quality Gate"){
            timeout(time: 1, unit: 'HOURS') {
                def qg = waitForQualityGate()
                if (qg.status != 'OK') {
                    error "Pipeline aborted due to quality gate failure: ${qg.status}"
                }
            }
        }

        if (testJob.getResult() != "SUCCESS") {
            error "System test failed"
        }
    }
}
