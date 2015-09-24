AppAdjuster Utility
-------------------
This utility will adjust the custom attribute values of an applicationby applying a given set of
custom attribute name/value pairs. The application name is passed on the command line (along
with the configuration file path). The application version, the custom attribute name/value
pairs, and the Code Center server information, are specified in the configuration file.

Password Encryption

The configuration file contains one password property: cc.password

Passwords are entered in plain text, and then (usually) automatically encrypted in-place by the
utility the next time it reads the configuration file. When creating a configuration file, 
set the value of each password to the password in plain text. The first time the utility 
reads the file, if the property cc.password.isencrypted is not present, it will encrypt 
the password and insert the property cc.password.isencrypted=true. To change the password, 
remove the cc.password.isencrypted property, and set cc.password to the password in 
plain text. To tell the utility to leave the password as plain text, insert 
cc.password.isencrypted=false.

Execution
---------
To run the utility:
	Linux:   bin/AppAdjuster.sh  -config <config filename> <application name>
	Windows: bin\AppAdjuster.bat -config <config filename> <application name>
	
The configuration file must set the following properties:

cc.server.name=<Code Center URL>
cc.user.name=<Code Center username>
cc.password=<password>

app.version=<the version of each app to be processed; default: Unspecified>

# Custom attribute name/value pairs
# Include as many of these as you need
# To clear an attribute's value, set it equal to nothing
# (nothing after the "=")
app.attr.0.name=<attr name>
app.attr.0.value=<attr value
app.attr.1.name=<attr name>
app.attr.1.value=<attr value

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