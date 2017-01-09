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
        compile "org.bouncycastle:bcpg-jdk15on:1.47"
        compile "org.bouncycastle:bcprov-ext-jdk15on:1.47"
    }
    plugins {
        build ":release:3.1.2"
    }
}
