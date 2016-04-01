User Access Extractor Utility
----------------
This utility will generate, by looking at the current user access on qualifying applications,
a report that shows which applications (by AppIdentifier) each user currently has access to. 
The report consisting of lines like: <username>;<AppIdentifier>;<AppIdentifier>...)

The applications examined to produce this list are determined by the values
of the appname.* properties.

Execution
---------
To run the utility:
	Linux:   bin/UserAccessExtractor.sh  -config <config filename> <outputFilename>
	Windows: bin\UserAccessExtractor.bat -config <config filename> <outputFilename>
	
The configuration file must set the following properties:

cc.server.name=<Code Center URL>
cc.user.name=<Code Center username>
cc.password=<password>

# Set the following property to have the utility fetch applications in chunks
# of a given size (# applications). When the #apps is large, Code Center can fail
# when you try to fetch them all at once
app.fetch.chunk.size=1000

# The following regex patterns specify the application name format
# <appidentifier>-<description>-<suffix0>-<suffix1>...
# Use these properties to control which apps get included.
# The following patterns include snapshot as well as -CURRENT applications.
appname.pattern.withdescriptionformat=\[0-9\]\[0-9\]\[0-9\]+-.*-(PROD|RC1|RC2|RC3|RC4|RC5)-(CURRENT|[0-1][0-9]-[0-3][0-9]-20[0-9][0-9]-[0-2][0-9]:[0-5][0-9]:[0-5][0-9])
appname.pattern.withoutdescriptionformat=\[0-9\]\[0-9\]\[0-9\]+-(PROD|RC1|RC2|RC3|RC4|RC5)-(CURRENT|[0-1][0-9]-[0-3][0-9]-20[0-9][0-9]-[0-2][0-9]:[0-5][0-9]:[0-5][0-9])
appname.pattern.followsdescription=-\(PROD|RC1|RC2|RC3|RC4|RC5\)-(CURRENT|[0-1][0-9]-[0-3][0-9]-20[0-9][0-9]-[0-2][0-9]:[0-5][0-9]:[0-5][0-9])
appname.pattern.appidentifier=\[0-9\]\[0-9\]\[0-9\]+
appname.pattern.suffix.0=\(PROD|RC1|RC2|RC3|RC4|RC5\)
appname.pattern.suffix.1=\(CURRENT|\[0-1\]\[0-9\]-\[0-3\]\[0-9\]-20\[0-9\]\[0-9\]-\[0-2\]\[0-9\]:\[0-5\]\[0-9\]:\[0-5\]\[0-9\]\)
appname.separator=-