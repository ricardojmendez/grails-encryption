
class CryptoGrailsPlugin {
	def version = "1.0.3"
	def dependsOn = [:]

    // TODO Add accent once Grail's UTF-8 bug is fixed
	def author = "Ricardo J. Mendez"
    def authorEmail = "ricardo@arquetipos.co.cr"
    def title = "Encryption plugin for Grails"
    def description = '''\
Masks encryption and decryption functions, currently integrated with Bouncy
Castle. This version provides classes for PGP and Blowfish, as well as a
tool for both generating random passwords and salting / verifying salted
passwords.
'''
	
	def doWithSpring = {
		// TODO Implement runtime spring config (optional)
	}   
	def doWithApplicationContext = { applicationContext ->
		// TODO Implement post initialization spring config (optional)		
	}
	def doWithWebDescriptor = { xml ->
		// TODO Implement additions to web.xml (optional)
	}	                                      
	def doWithDynamicMethods = { ctx ->
		// TODO Implement additions to web.xml (optional)
	}	
	def onChange = { event ->
		// TODO Implement code that is executed when this class plugin class is changed  
		// the event contains: event.application and event.applicationContext objects
	}                                                                                  
	def onApplicationChange = { event ->
		// TODO Implement code that is executed when any class in a GrailsApplication changes
		// the event contain: event.source, event.application and event.applicationContext objects
	}
}
