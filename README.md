# Enterprise Utility Suite
## Overview

The Enterprise Utility Suite is a set of Protex and Code Center utilities that can be useful in an enterprise deployment of Protex and Code Center.

## Where can I get the latest release?

You can download the latest source from GitHub: https://github.com/blackducksoftware/enterprise-utility-suite.

You can pull the binary from GitHub at: https://github.com/blackducksoftware/enterprise-utility-suite/releases

## Documentation

Please see wiki for more information: https://github.com/blackducksoftware/enterprise-utility-suite/wiki

## License

GNU General Public License v2.0 only.

## Background ##
These utilities are designed with the following environment in mind:

 - Protex is deployed for code scanning; Code Center is deployed for Managing security vulnerabilites and remediation.
 - A Code Center application has the same name as the corresponding Protex project.
 - The ccimporter utility (which is also open source, but not included in the enterprise utility suite) is used to synchronize Code Center with Protex.
 - Protex project names and Code Center application names conform to the following pattern: <AppIdentifier>-<description>-<suffix1>-<suffix2>, where:
 - AppIdentifier uniquely identifies an "application", but an application can have different variants or versions, distinguished by different suffix values.
 - The separator (shown here as hyphen) can be any character (or string).
 - Description and it's preceding separator are optional for each application (description can be included in some application names, and omitted in others).
 - The number of suffixes (0 or more) is configurable, but must the same for all applications. It is configured in advance and cannot be changed (at least not easily).
 - For each suffix configured there is a preceding separator. If zero suffixes are configured, there is no trailing separator on the application name (it will be either <AppIdentifier> or <AppIdentifier>-<description>, depending on whether or not it includes a description).

The utilities also support the concept of a Line Of Business (LOB), which is an organization that "owns" a subset of applications. An application's LOB can be stored in a (configurable) custom attribute. The AddUser utility has the ability to assign a given set of users to all of the applications owned by a given LOB.

## Utilities included in the suite ##
 - AddUsers: Creates Code Center user accounts and adds them to applications' teams.
 - RemoveUsers: Removes Code Center user accounts from applications' teams.
 - AppAdjuster: Adjusts values of custom attributes of a Code Center application.
 - Custom Attribute Populator (CaPop): Bulk uploads values from a spreadsheet into Code Center application custom attributes.
 - HighSev: Determines the severity level of an application's highest severity vulnerability, and writes that severity level into a given application custom attribute.
 - RemediationDataLoader: Bulk loads remediation metadata from a spread sheet.
 - Snapshot: Creates a backup of a (Protex) project / (Code Center) application pair.
 - TeamSync: Brings the team assignments for new applications up-to-date with the team assigned to other applications with the same AppIdentifier.
 - UserAccessExtractor: Generate, by looking at the current user access on qualifying applications, a report that shows which applications (by AppIdentifier) each user currently has access to. The report consisting of lines like: <username>;<AppIdentifier>;<AppIdentifier>...)
