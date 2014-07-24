grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

//grails.project.fork = [
//        // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
//        //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
//
//        // configure settings for the test-app JVM, uses the daemon by default
//        test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
//        // configure settings for the run-app JVM
//        run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
//        // configure settings for the run-war JVM
//        war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
//        // configure settings for the Console UI JVM
//        console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
//]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()

        // use mavenRepo for maven-based repositories
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        build('org.bouncycastle:bcpg-jdk15on:1.47') {
            excludes 'bcprov-jdk15on'
            export = true
        }
        build('org.bouncycastle:bcprov-ext-jdk15on:1.47') {
            export = true
        }

    }
    plugins {
        build ':tomcat:7.0.52.1'
        compile ':hibernate4:4.3.5.2'

        build(':release:3.0.1', ':rest-client-builder:2.0.1') {
            export = false
        }
    }

    plugins {
        runtime ":hibernate4:4.3.5.3" // or ":hibernate:3.6.10.15"
        build(":release:3.0.1",
                ":rest-client-builder:1.0.3") {
            export = false
        }
    }
}
