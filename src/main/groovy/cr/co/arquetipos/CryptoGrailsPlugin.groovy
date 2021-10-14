package cr.co.arquetipos


import grails.plugins.Plugin

class CryptoGrailsPlugin extends Plugin {
    def grailsVersion = "4.0.0 > *"
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def title = "Encryption plugin for Grails"
    def author = "Ricardo J. Mendez and Robert Fischer"
    def authorEmail = "ricardo@arquetipos.co.cr"
    def description = '''\
Masks encryption and decryption functions, currently integrated with Bouncy Castle. This version provides classes for PGP and Blowfish, as well as a tool for both generating random passwords and salting / verifying salted passwords.
'''
    def profiles = ['web']
    def documentation = "http://grails.org/plugin/@plugin.short.name@"
    def license = "APACHE"
    def issueManagement = [url: 'https://github.com/ricardojmendez/grails-encryption/issues']
    def scm = [url: 'https://github.com/ricardojmendez/grails-encryption']
}