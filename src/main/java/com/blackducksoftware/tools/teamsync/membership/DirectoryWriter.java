package com.blackducksoftware.tools.teamsync.membership;

import java.util.Map;
import java.util.Set;

public interface DirectoryWriter {
    void write(Map<String, Set<String>> directory);
}
