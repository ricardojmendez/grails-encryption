
class CryptoGrailsPlugin {
	def version = "2.3"
	    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.2.3 > *"
	def dependsOn = [:]

  // TODO Add accent once Grail's UTF-8 bug is fixed
	def author = "Ricardo J. Mendez and Robert Fischer"
  def authorEmail = "ricardo@arquetipos.co.cr"
  def title = "Encryption plugin for Grails"
  def description = '''\
Masks encryption and decryption functions, currently integrated with Bouncy
Castle. This version provides classes for PGP and Blowfish, as well as a
tool for both generating random passwords and salting / verifying salted
passwords.
'''

}
