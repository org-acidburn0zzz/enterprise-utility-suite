package com.blackducksoftware.tools.teamsync.membership;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.teamsync.membership.AddUserInputFileWriter;
import com.blackducksoftware.tools.teamsync.membership.DirectoryWriter;

public class AddUserInputFileWriterTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
        Map<String, Set<String>> directory = new HashMap<>();

        Set<String> appIdentifiers = new HashSet<>();
        appIdentifiers.add("11111");
        appIdentifiers.add("22222");
        directory.put("u000000", appIdentifiers);

        appIdentifiers = new HashSet<>();
        appIdentifiers.add("33333");
        appIdentifiers.add("44444");
        appIdentifiers.add("55555");
        directory.put("u000001", appIdentifiers);

        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);

        DirectoryWriter writer = new AddUserInputFileWriter(ps);
        writer.write(directory);

        String s = os.toString();
        System.out.println("'" + s + "'");

        assertEquals("u000000;22222;11111" + IOUtils.LINE_SEPARATOR + "u000001;55555;44444;33333" + IOUtils.LINE_SEPARATOR, s);
    }

}
