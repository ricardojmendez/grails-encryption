grails.project.work.dir = 'target'

grails.project.dependency.resolver = 'maven'
grails.project.dependency.resolution = {
    inherits 'global'
    log 'warn'
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        build('org.bouncycastle:bcpg-jdk15on:1.47') {
            excludes 'bcprov-jdk15on'
        }
        build 'org.bouncycastle:bcprov-ext-jdk15on:1.47'
    }
    plugins {
        build ':tomcat:7.0.55.3', {
            export = false
        }
        compile ':hibernate4:4.3.5.2', {
            export = false
        }

        build(':release:3.0.1', ':rest-client-builder:2.0.3') {
            export = false
        }
    }
}
