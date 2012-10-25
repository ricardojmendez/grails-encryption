
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

ant.echo(message:"As of Crypto 2.0, the dependency on commons-codec 1.4-SNAPSHOT has been abandoned in favor of the stable 1.3 release.", level:'warning')
ant.echo(message:"This means SHA-256 support has been reverted back to SHA-1.  IF YOU USED THE 1.X RELEASE OF CRYPTO OR", level:'warning')
ant.echo(message:"SUPPLY COMMONS-CODEC 1.4 YOURSELF, set the config property 'crypto.useSha256' to true in Config.groovy.", level:'warning')

