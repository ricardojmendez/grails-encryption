class CryptoGrailsPlugin {
    // the plugin version
    def version = "2.1"
    def dependsOn = [:]
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def title = "Encryption plugin for Grails"
	def author = "Ricardo J. Mendez and Robert Fischer"
    def authorEmail = "ricardo@arquetipos.co.cr"

    def description = '''
                    Masks encryption and decryption functions, currently integrated with Bouncy
                    Castle. This version provides classes for PGP and Blowfish, as well as a
                    tool for both generating random passwords and salting / verifying salted
                    passwords.
                    '''

}
