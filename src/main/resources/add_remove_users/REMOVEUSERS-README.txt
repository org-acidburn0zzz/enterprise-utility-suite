Summary:

  Remove users from Code Center applications' teams.

  The users specified are removed from applications as specified (any/all
  application roles they hold), but their user accounts are not deleted.

  Limitation: An application's owner cannot be removed from the application
  via this utility.

Usage:

  Using RemoveUsers is very similar to using AddUsers in AppIdentifiers per user mode. The
  command line arguments are identical, and the AppIdentifiers_per_User.txt input file
  format is identical. The differences are the command itself (RemoveUsers.sh vs.
  AddUsers.sh) and the effect on applications (removing users vs. adding them).

  The utility expects, as a program argument a file path to a 
  plain text file that contains on line per user involved. Blank lines and
  lines that start with '#' are ignored. Each line consists
  of:
  	- The user's username
  	- One or more AppIdentifiers

  There must be a semicolon (;) between each field. For example, the following
  line specifies that user a123456 should be removed from applications with
  the applications that have AppIdentifiers 456 and 789:
  
  	a123456;456;789
  
  Invoke this mode with a command line such as the following:

    bin\RemoveUsers.bat -config <properties file> -app-identifiers-per-user-file <path to app-identifiers-per-user text file>

  For example:
    bin\RemoveUsers.bat -config adduser.properties -app-identifiers-per-user-file AppIdentifiers_per_User.txt
    
  A configuration file for this utility will typically look something like this:
  
  cc.server.name=<Code Center URL>
  cc.user.name=<username>
  cc.password=<password>

  app.version=Unspecified

  report.dir=.
  num.threads=2

  # Username format (regex)
  username.pattern=\[a-zA-Z\]\[0-9\]\[0-9\]\[0-9\]\[0-9\]\[0-9\]\[0-9\]

  # The following regex patterns specify the application name format
  # <appidentifier>-<description>-<suffix0>-<suffix1>...
  appname.pattern.withdescriptionformat=\[0-9\]\[0-9\]\[0-9\]+-.*-(PROD|RC1|RC2|RC3|RC4|RC5)-(CURRENT|[0-1][0-9]-[0-3][0-9]-20[0-9][0-9]-[0-2][0-9]:[0-5][0-9]:[0-5][0-9])
  appname.pattern.withoutdescriptionformat=\[0-9\]\[0-9\]\[0-9\]+-(PROD|RC1|RC2|RC3|RC4|RC5)-(CURRENT|[0-1][0-9]-[0-3][0-9]-20[0-9][0-9]-[0-2][0-9]:[0-5][0-9]:[0-5][0-9])
  appname.pattern.followsdescription=-\(PROD|RC1|RC2|RC3|RC4|RC5\)-(CURRENT|[0-1][0-9]-[0-3][0-9]-20[0-9][0-9]-[0-2][0-9]:[0-5][0-9]:[0-5][0-9])
  appname.pattern.appidentifier=\[0-9\]\[0-9\]\[0-9\]+
  appname.pattern.suffix.0=\(PROD|RC1|RC2|RC3|RC4|RC5\)
  appname.pattern.suffix.1=\(CURRENT|\[0-1\]\[0-9\]-\[0-3\]\[0-9\]-20\[0-9\]\[0-9\]-\[0-2\]\[0-9\]:\[0-5\]\[0-9\]:\[0-5\]\[0-9\]\)
  appname.separator=-
  
  # Pattern that "live" apps will match. Non-live apps are skipped.
  # Do not set this property if all apps should be considered live
  appname.pattern.live=^.*-CURRENT$
  
  # If you want AddUser to add users to locked applications, then you need
  # the following property set to true:
  circumvent.locks=true


Execution:

1. Update the removeusers.properties using the information above.
2. Create the input file using the information above.
3. Run the batch/shell script. The first two arguments must be:
	-config 
	<the path to the config properties file>

Password Encryption

Configuration files can contain passwords, configured via properties of the form:
	<prefix>.password=<value>
Where <prefix> indicates what the password is for.

Passwords are entered in plain text, and then (usually) automatically encrypted in-place by the 
utility the next time it reads the configuration file. When creating a configuration file, 
set the value of each password to the password in plain text. The first time the utility reads 
the file, if the property <prefix>.password.isencrypted is not present, it will encrypt the 
password and insert the property <prefix>.password.isencrypted=true. To change the password, 
remove the <prefix>.password.isencrypted property, and set <prefix>.password to the password 
in plain text. To tell the utility to leave the password as plain text, insert 
<prefix>.password.isencrypted=false.