/**
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.LicenseSet;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class LicenseSetTest {

    @Test
    public void multipleLicenseSets() throws Exception {
        final LicenseSet licenseSet1 = new LicenseSet();
        licenseSet1.basedir = new File("src/test/resources/check/strict");
        licenseSet1.header = "src/test/resources/test-header1-diff.txt";

        final LicenseSet licenseSet2 = new LicenseSet();
        licenseSet2.basedir = new File("src/test/resources/check/issue76");
        licenseSet2.header = "src/test/resources/test-header1.txt";

        final LicenseSet licenseSetWithoutBaseDir = new LicenseSet();
        licenseSetWithoutBaseDir.header = "test-header1.txt";

        final LicenseSet[] licenseSets = {
                licenseSet1,
                licenseSet2,
                licenseSetWithoutBaseDir
        };

        final LicenseCheckMojo check = new LicenseCheckMojo();
        check.licenseSets = licenseSets;
        check.project = new MavenProjectStub();
        check.strictCheck = false;
        check.defaultBasedir = new File("src/test/resources/unknown");
        final MockedLog logger = new MockedLog();
        check.setLog(new DefaultLog(logger));
        check.execute();

        final String log = logger.getContent();
        final String fileFromFirstSet = new File("src/test/resources/check/strict/space.java").getCanonicalFile().getPath();
        final String fileFromSecondSet = new File("src/test/resources/check/issue76/after.xml").getCanonicalFile().getPath();
        final String fileFromDefaultBaseDirSet = new File("src/test/resources/unknown/header.txt").getCanonicalFile().getPath();

        assertTrue(log.contains("Header OK in: " + fileFromFirstSet));
        assertTrue(log.contains("Header OK in: " + fileFromSecondSet));
        assertTrue(log.contains("Header OK in: " + fileFromDefaultBaseDirSet));
    }

}
