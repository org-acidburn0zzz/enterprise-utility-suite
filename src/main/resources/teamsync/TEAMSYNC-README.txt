TeamSync Utility
----------------
This utility will, given a list of newly-created apps, adjust the new apps' teams
so that users already given access to the appIdentifier will have access to the new apps
as well. It does this by, for each new app, finding all -CURRENT apps for the
appIdentifier, calculating the union of those app's role assignments, and applying
that union to the new app.

The list of new apps is provided in a text file (one application name per line).
The path to this file is specified in the configuration file.

Execution
---------
To run the utility:
	Linux:   bin/TeamSync.sh  -config <config filename>
	Windows: bin\TeamSync.bat -config <config filename>
	
The configuration file must set the following properties:

cc.server.name=<Code Center URL>
cc.user.name=<Code Center username>
cc.password=<password>
cc.password.isencrypted=<true or false>

# The file specified in this property contains the names of applications to update
new.app.list.filename=<path to text file containing list of new applications>

app.version=<the version of each app to be processed; default: Unspecified>

# The following regex patterns specify the application name format
# <appidentifier>-<description>-<suffix0>-<suffix1>...
appname.pattern.withdescriptionformat=\[0-9\]\[0-9\]\[0-9\]+-.*-(PROD|RC1|RC2|RC3|RC4|RC5)-(CURRENT|[0-1][0-9]-[0-3][0-9]-20[0-9][0-9]-[0-2][0-9]:[0-5][0-9]:[0-5][0-9])
appname.pattern.withoutdescriptionformat=\[0-9\]\[0-9\]\[0-9\]+-(PROD|RC1|RC2|RC3|RC4|RC5)-(CURRENT|[0-1][0-9]-[0-3][0-9]-20[0-9][0-9]-[0-2][0-9]:[0-5][0-9]:[0-5][0-9])
appname.pattern.followsdescription=-\(PROD|RC1|RC2|RC3|RC4|RC5\)-(CURRENT|[0-1][0-9]-[0-3][0-9]-20[0-9][0-9]-[0-2][0-9]:[0-5][0-9]:[0-5][0-9])
appname.pattern.appidentifier=\[0-9\]\[0-9\]\[0-9\]+
appname.pattern.suffix.0=\(PROD|RC1|RC2|RC3|RC4|RC5\)
appname.pattern.suffix.1=\(CURRENT|\[0-1\]\[0-9\]-\[0-3\]\[0-9\]-20\[0-9\]\[0-9\]-\[0-2\]\[0-9\]:\[0-5\]\[0-9\]:\[0-5\]\[0-9\]\)
appname.separator=-

Password Encryption

Configuration files can contain passwords, configured via properties of the form:
	<prefix>.password=<value>
Where <prefix> indicates what the password is for.

Passwords are entered in plain text, and then (usually) automatically encrypted in-place by the 
utility the next time it reads the configuration file. When creating a configuration file, 
set the value of each password to the�password�in plain text. The first time the utility reads 
the file, if the property <prefix>.password.isencrypted is not present, it will encrypt the 
password and insert the property <prefix>.password.isencrypted=true. To change the password, 
remove the <prefix>.password.isencrypted property, and set <prefix>.password to the password 
in plain text. To tell the utility to leave the password as plain text, insert 
<prefix>.password.isencrypted=false.