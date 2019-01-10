import groovy.json.JsonOutput

def call(projectName, projectVersion, sonarSources = ".", 
        javaBinaries = "target/classes", junitReportPaths = "target/surefire-reports",
        jacocoReportPaths = "target/jacoco.exec") {

    stage("Sonar Analysis") {
        sonarNode(sonarScannerImage: 'newtmitch/sonar-scanner:3.2.0') {
            bash "sonar-scanner
                    -Dsonar.host.url=${SONARQUBE_HOST_URL}
                    -Dsonar.login=${SONARQUBE_TOKEN}
                    -Dsonar.projectKey=${projectName}
                    -Dsonar.projectVersion=${projectVersuib}
                    -Dsonar.sources=${sonarSources}
                    -Dsonar.java.binaries=${javaBinaries}
                    -Dsonar.junit.reportPaths=${junitReportPaths}
                    -Dsonar.jacoco.reportPaths=${jacocoReportPaths}"
        }

        timeout(time: 1, unit: 'HOURS') {
            def qg = waitForQualityGate()
            if (qg.status != 'OK') {
                error "Pipeline aborted due to quality gate failure: ${qg.status}"
            }
        }
    }
}
