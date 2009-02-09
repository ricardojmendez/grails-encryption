
//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'Ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
// Ant.mkdir(dir:"/Users/ricardo/Sources/grails-plugins/crypto/grails-app/jobs")
//

Ant.property(environment:"env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"

def grailsLib = new File(grailsHome, "lib")
if(!grailsLib.exists()) {
  ant.fail("Cannot find the Grails library directory (looked in ${grailsLib})")
}

// Upgrade commons-codec
// (Yes, this is a dangerous way to do things.  But we have to do it until Grails
// supports Ivy for its core libraries, too.)
ant.delete(verbose:'true', failonerror:'false', quiet:'true') {
  fileset(dir:"$grailsHome/lib", includes:'**/commons-codec*.jar')
}

ant.copy(toDir:"$grailsHome/lib", flatten:'true', verbose:'true') {
  fileset(dir:cryptoPluginDir.absolutePath, includes:'**/commons-codec*.jar') 
}
