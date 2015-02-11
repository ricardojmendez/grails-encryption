grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        runtime('org.bouncycastle:bcpg-jdk15on:1.47') {
            excludes 'bcprov-jdk15on'
            export = true
        }
        runtime('org.bouncycastle:bcprov-ext-jdk15on:1.47') {
            export = true
        }
        compile("org.springframework:spring-aop:4.0.7.RELEASE")
        compile("org.springframework:spring-expression:4.0.7.RELEASE")

    }
    plugins {
        build ':tomcat:7.0.55'
        compile ':hibernate4:4.3.6.1'

        build(':release:3.0.1', ':rest-client-builder:2.0.1') {
            export = false
        }
    }
}
