Remediation Data Loader Utility
-------------------------------
This utility reads Component Use Vulnerability remediation dates from an Excel file, and updates
them in Code Center.

Remediation data file: 
	Excel (.xlsx)
	Single sheet (or all data in the first sheet)
	One header row (ignored by the utility)
	Columns:
		Application name (text)
		Component name (text)
		Component version (text)
		Vulnerability name (text)
		Target remediation date (date) [optional]
		Actual remediation date (date) [optional]
		Status Name [optional]
		Status Comment [optional]
		
	Important notes
	- There should be no blank/empty rows inserted before data rows (the utility will stop at the first empty/blank application name cell)
	- Make sure the cells under Component version are text, to avoid versions like 1.0 being converted to 1.
	- Insert a single quote (') before Application names that consist only of digits, 
		to avoid application names like 123456 being read out as 123456.0.
	- Status Name: Must be a defined vulnerability status name.
	- If any optional values are omitted, that value is left unchanged in Code Center.

	
The utility returns the following error codes:
	0: success
	-1: error

	
Execution
---------
To run the utility:
	Linux:   bin/RemDataLoader.sh  -config <config filename> <remediation data filename>
	Windows: bin\RemDataLoader.bat -config <config filename> <remediation data filename>
	
The configuration file must set the following properties:

cc.server.name=<codecenter URL>
cc.user.name=<codecenter username>
cc.password=<codecenter password>

cc.app.version=<application version to use for all applications>

Password Encryption

Configuration files can contain passwords, configured via properties of the form:
	<prefix>.password=<value>
Where <prefix> indicates what the password is for.

Passwords are entered in plain text, and then (usually) automatically encrypted in-place by the 
utility the next time it reads the configuration file. When creating a configuration file, 
set the value of each password to the password in plain text. The first time the utility reads 
the file, if the property <prefix>.password.isencrypted is not present, it will encrypt the 
password and insert the property <prefix>.password.isencrypted=true. To change the password, 
remove the <prefix>.password.isencrypted property, and set <prefix>.password to the password 
in plain text. To tell the utility to leave the password as plain text, insert 
<prefix>.password.isencrypted=false.