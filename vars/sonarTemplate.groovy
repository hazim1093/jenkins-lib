#!/usr/bin/groovy
import io.fabric8.Fabric8Commands

def call(Map parameters = [:], body) {
    def flow = new Fabric8Commands()

    def defaultLabel = buildId('sonar')
    def label = parameters.get('label', defaultLabel)

 //TODO: Update image
    def sonarScannerImage = parameters.get('sonarScannerImage', 'stakater/pipeline-tools:v1.16.4')
    def inheritFrom = parameters.get('inheritFrom', 'base')

    def cloud = flow.getCloudConfig()

    echo 'Using sonarScannerImage : ' + sonarScannerImage
    echo 'Mounting docker socket to build docker images'
    podTemplate(cloud: cloud, label: label, serviceAccount: 'jenkins', inheritFrom: "${inheritFrom}",
            envVars: [
                secretEnvVar(key: 'SONARQUBE_TOKEN', secretName: 'jenkins-sonarqube', secretKey: 'token'),
                envVar(key: 'SONARQUBE_HOST_URL', value: 'https://sonarqube.tools.tools178.k8syard.com/')
            ],
            containers: [
                    containerTemplate(
                            name: 'sonarscanner',
                            image: "${sonarScannerImage}",
                            command: '/bin/sh -c',
                            args: 'cat',
                            privileged: true,
                            workingDir: '/home/jenkins/',
                            ttyEnabled: true,
                            envVars: [
                                    envVar(key: 'DOCKER_API_VERSION', value: '1.32'),
                                    envVar(key: 'DOCKER_CONFIG', value: '/home/jenkins/.docker/')]
                    )],
            volumes: [
                    hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')]) {
        
        TODO: //if(Files.exists(bin/sonarcubcli ?))
        // write sonr.login file
        body()
    }
}
