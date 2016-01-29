Summary:

Assigns users to Code Center applications' teams.

There are four modes:
- AppIdentifiers per user mode: Assigns users to applications (all with the same role).
- App and role per user mode: Assigns users to application with roles specified per user.
- Users per app mode: Assigns a list of users to a single application.
- Users per LOB mode: Assigns a list of users to all applications with a given LOB value.

AppIdentifiers per user mode
============================

  In this mode, the utility expects, as a program argument a file path to a 
  plain text file that contains on line per user involved. Blank lines and
  lines that start with '#' are ignored. Each line consists
  of:
  	- The user's username
  	- One or more AppIdentifiers
  There must be a semicolon (;) between each field. For example, the following
  line specifies that user a123456 should be added to applications with AppIdentifiers
  456 and 789:
  
  	a123456;456;789
  	
  In this mode, users are add to applications as specified, but never removed.
  
  Code Center user accounts will be created where they don't already exist. 
  Users added to applications will be given the role
  specified by the user.role property in the config file. New Code Center accounts
  will be created with the local Code Center password set to the value of the
  new.user.password property.
  
  Invoke this mode with a command line such as the following:

    bin\AddUsers.bat -config <properties file> -app-identifiers-per-user-file <path to app-identifiers-per-user text file>

  For example:
    bin\AddUsers.bat -config addusers.properties -app-identifiers-per-user-file AppIdentifiers_per_User.txt
    
  A configuration file for this mode will typically look something like this:
  
  cc.server.name=<Code Center URL>
  cc.user.name=<username>
  cc.password=<password>

  app.version=Unspecified
  user.role=<name of role for users added to apps>

  new.user.password=<password to be assigned to created user accounts>

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
  
  You do not need to set the value of the path property in the config file when using this mode (if
  you set it, it will be ignored).

Users per LOB mode
==================

  In LOB User List Adjustment mode, the utility expects, as program arguments, an LOB name,
  and a file path to a CSV or plain text file that contains the current list of users for
  that LOB. It will adjust the Team assignments (user list) for each application
  assigned to that LOB (according to the LOB custom application attribute) to match
  the new list by adding/removing users. Code Center user accounts will be created
  where they don't already exist. Users added to applications will be given the role
  specified by the user.role property in the config file. New Code Center accounts
  will be created with the local Code Center password set to the value of the
  new.user.password property.
  
  To use LOB User List Adjustment mode:
  
  1. Create a CSV or plain text file that contains the list of users for your LOB. You can
    create the file in Excel, and Save As "CSV (Comma delimited) (*.csv)". The file should
    only have data in column A.  Alternatively you can create the file with a
    text editor. Either way, put one username on each row/line, and do not include a header row.
    Leading and trailing whitespace are trimmed off.
    The first letter of the username, if it is uppercase, will be converted to lowercase, so the
    case of the first letter of each username in the input file is unimportant.
    Empty rows (before, between, or after rows containing usernames) are OK; they are ignored.
  
  2. Set the following properties in the config file:
    lob.adjust.mode=true
    lob.attr.name=<name of the custom application attribute where LOB is stored>
    new.user.password=<the local Code Center password for new users>
    report.dir=<the directory in which the utility should create the report, without trailing slash character>
    num.threads=<the number of parallel threads you want to use to process the application list>
  
  3. Invoke the utility as follows:
    bin\AddUsers.bat -config <properties file> -lob <LOB name> -lob-userlist-file <path to CSV file containing user list>

  For example:
    bin\AddUsers.bat -config addusers.properties -lob "My LOB" -lob-userlist-file LOB_Test_Users.csv
    
Users per app mode
==================

  If neither -app-identifiers-per-user-file nor -lob-userlist-file is specified on the command line and
  the "path" configuration property is not set, the utility runs in "users per app" mode.
  In this mode, the utility assigns   all of the users in the add user request list (the value of the
  add.user.request config property, a semicolon-separated list) to the application name specified by
  config properties app.name and app.version.
  
  Configuration file: The configuration file is a Java properties file named addusers.properties found
  on the classpath. It should define the following properties:
  	cc.server.name: the Code Center URL (starts with http: or https:)
  		Can be overridden from the command line using -server <servername>
  	cc.user.name: the Code Center username to use to perform the work (must have admin priviledges)
  		Can be overridden from the command line using -username <username>
  	cc.password: the password for the Code Center user (cc.user.name)
  		Can be overridden from the command line using -password <password>
  	app.version: the version string of the applications to be modified
  		Can be overridden from the command line using -app-version <app_version>
  	user.role: the default user role to use when assigning users to projects.
  		Can be overridden from the command line using -role <role>.
  		Can be overridden per-user in the user data file (highest precedence).
  	new.user.password: LOB User Adjust mode: password given to created users.
  	
  When running the utility in user creation mode, you must also specify:
    path: The file pathname of the user data file (described above).
    	Can be overridden from the command line using -f <pathname>
    
  When running the utility in user assignment mode, you must also specify:
    app.name: the name of the application to assign users to (the version is the value
    	of app.version).
    	Can be overridden from the command line using -app-name <appname>
    add.user.request: a semicolon-separated list of users to assign to the given application.
		Can be overridden from the command line using -user <userlist>
	
App and role per user mode
==========================

  Use of this mode is discouraged. It has two known flaws:
  	1. Some hard-coded length limits on user and application names.
  	2. It uses the applicationApi.searchApplications() method which can produce unexpected results.
  
  If neither -app-identifiers-per-user-file nor -lob-userlist-file is specified on the command line and
  and the "path" configuration property is set, the utility creates the users specified in the 
  user data file pointed to by path, and assigns them to projects as specified in the user data file.
  
  User data file:  Each line consists of either a comment ('#' in column one), or:
  	<username>;<application_name>;<user_role>
  		<username> is a comma-separated list of Code Center usernames; These users will be created if they do not exist.
  			These users will be assigned to the application specified by <application_name>
  			and the config property app.version.
  		<application_name> is a Code Center application name. This application must exist.
  			You can also specify the beginning of an application name, and it will use
  			each application that starts with that string.
  		<user_role> is a user role name, such as "Application Administrator", "Application Developer", etc.
  			If you specify a different role for a user already assigned to an application, the
  			new role is added.
  	the application version of the affected applications will be the value of the config
  	property app.version.
  	If <user_role> is omitted, the default role (the value of the config property user.role) will be used.


Execution (all modes):

[Required] Update the addusers.properties using the information above.
[User creation mode only] Create the user data file and make sure the path property points to it.

- Run the batch/shell script. The first two arguments must be:
	-config
	<the path to the config properties file>

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