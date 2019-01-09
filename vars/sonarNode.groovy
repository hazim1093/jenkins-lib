#!/usr/bin/groovy
def call(Map parameters = [:], body) {

    def defaultLabel = buildId('sonar')
    def label = parameters.get('label', defaultLabel)

    sonarTemplate(parameters) {
        node(label) {
            body()
        }
    }
}
