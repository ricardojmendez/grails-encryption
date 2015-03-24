ant.echo(message:"As of Crypto 2.0, the dependency on commons-codec 1.4-SNAPSHOT has been abandoned in favor of the stable 1.3 release.", level:'warning')
ant.echo(message:"This means SHA-256 support has been reverted back to SHA-1.  IF YOU USED THE 1.X RELEASE OF CRYPTO OR", level:'warning')
ant.echo(message:"SUPPLY COMMONS-CODEC 1.4 YOURSELF, set the config property 'crypto.useSha256' to true in Config.groovy.", level:'warning')
