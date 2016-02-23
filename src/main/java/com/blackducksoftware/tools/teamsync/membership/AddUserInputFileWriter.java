package com.blackducksoftware.tools.teamsync.membership;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class AddUserInputFileWriter implements DirectoryWriter {
    private final PrintStream printStream;

    public AddUserInputFileWriter(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void write(Map<String, Set<String>> directory) {
        SortedSet<String> sortedUserNames = new TreeSet<>(directory.keySet());
        for (String username : sortedUserNames) {
            StringBuilder entryString = new StringBuilder(username);

            for (String appId : directory.get(username)) {
                entryString.append(';');
                entryString.append(appId);
            }
            printStream.println(entryString.toString());
        }
    }

}
