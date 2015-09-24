Snapshot Utility
----------------
This utility takes a snapshot of a Protex Project and the Code Center application of the same name.
The snapshot project/application have a name constructed as follows:
	<original name>_<date/timestamp>
	
The utility returns the following error codes:
	0: success
	1: Protex project not found (which may be normal and treated as success)
	2: Other error

Execution
---------
To run the utility:
	Linux:   bin/Snapshot.sh  -config <config filename> <project/application name>
	Windows: bin\Snapshot.bat -config <config filename> <project/application name>
	
The configuration file must set the following properties:

protex.server.name=<protex URL>
protex.user.name=<protex username>
protex.password=<protex password>

cc.server.name=<codecenter URL>
cc.user.name=<codecenter username>
cc.password=<codecenter password>

cc.app.version=<application version to use for all applications>
cc.cloned.app.workflow=<workflow name for cloned applications>

# Set skip.non.kb.components to false to include custom and local components in snapshots
skip.non.kb.components=false

There are also several optional properties:

suffix.to.remove: If set, the snapshot utility checks to see if the project/app name
	ends in the value of this property. If it does, that part of the name is removed
	before the date suffix is appended to generate the snapshot name.

associate.project.with.app: If set to true, no Protex project snapshot will be
	created (only the Code Center application snapshot).

snapshot.suffix.dateformat: If set, this controls the suffix (including leading character
    that separates the date/timestamp from the name, and the date/time format)
	used in the snapshot project/application name.
	The suffix will be generated using the included pattern string, passed to java.tex.SimpleDateFormat
	(http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html).
	The default is: "_yyyyMMdd_HHmmss". To, for example, include milliseconds,
	set this property to "_yyyyMMdd_HHmmss.SSS" (without the quotes).
	To use a separating character of "-" and a different date format, you could
	set it to "-MM-dd-yyyy-HH:mm:ss".

To tell the snapshot utility to set some application custom attributes
to given values, specify those name/value pairs here. The index
number at the end of the property name (.0, .1, etc.) must
be the same for a given name/value pair, and you must
use sequential index values (no gaps in the numbering;
use as many name/value pairs as you need):

cc.app.attr.name.0=<First Attribute Name, such as Project Status>
cc.app.attr.value.0=<First Attribute Value, such as CURRENT>
cc.app.attr.name.1=<Second Attribute Name>
cc.app.attr.value.1=<Second Attribute Value>

WARNING: The following feature has not been thoroughly tested, and
could have unforseen side-effects, including changes to Code Center application
snapshots after the snapshot is created. Do not use this in production unless/until you've 
verified in a test environment that there are no undesirable side-effects:
To tell the snapshot utility to associate the corresponding Protex project clone
with the Code Center application clone, set the following attribute to true.
It will only make the association if the original (cloned) application was
associated with a Protex project.

associate.project.with.app=<true or false>
	
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