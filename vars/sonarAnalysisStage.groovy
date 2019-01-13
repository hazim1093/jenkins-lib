import groovy.json.JsonOutput

def call(projectName, projectVersion, queryInterval = 5000, queryMaxAttempts = 120, sonarSources = ".",
        javaBinaries = "target/classes", junitReportPaths = "target/surefire-reports",
        jacocoReportPaths = "target/jacoco.exec") {

    stage("Sonar Analysis") {
        sonarNode(sonarScannerImage: 'stakater/pipeline-tools:SNAPSHOT-PR-6-9') {
            sh """
                /bin/sonar-scanner \
                    -Dsonar.host.url=${SONARQUBE_HOST_URL} \
                    -Dsonar.login=${SONARQUBE_TOKEN} \
                    -Dsonar.projectKey=${projectName} \
                    -Dsonar.projectVersion=${projectVersion} \
                    -Dsonar.sources=${sonarSources} \
                    -Dsonar.java.binaries=${javaBinaries} \
                    -Dsonar.junit.reportPaths=${junitReportPaths} \
                    -Dsonar.jacoco.reportPaths=${jacocoReportPaths} \
                    -Dsonar.buildbreaker.queryInterval=${queryInterval} \
                    -Dsonar.buildbreaker.queryMaxAttempts=${queryMaxAttempts}
                """
        }
    }
}
