import groovy.json.JsonOutput

def call(projectName, projectVersion, sonarSources = ".", 
        javaBinaries = "target/classes", junitReportPaths = "target/surefire-reports",
        jacocoReportPaths = "target/jacoco.exec") {

    stage("Sonar Analysis") {
        sonarNode(sonarScannerImage: 'stakater/pipeline-tools:SNAPSHOT-PR-6-9') {
            sh """
                sonar-scanner \
                    -Dsonar.host.url=${SONARQUBE_HOST_URL} \
                    -Dsonar.login=${SONARQUBE_TOKEN} \
                    -Dsonar.projectKey=${projectName} \
                    -Dsonar.projectVersion=${projectVersion} \
                    -Dsonar.sources=${sonarSources} \
                    -Dsonar.java.binaries=${javaBinaries} \
                    -Dsonar.junit.reportPaths=${junitReportPaths} \
                    -Dsonar.jacoco.reportPaths=${jacocoReportPaths}
                """
        }

        timeout(time: 1, unit: 'HOURS') {
            def qg = waitForQualityGate()
            if (qg.status != 'OK') {
                error "Pipeline aborted due to quality gate failure: ${qg.status}"
            }
        }
    }
}
