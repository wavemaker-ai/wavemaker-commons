/*******************************************************************************
 * Copyright (C) 2022-2023 WaveMaker, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wavemaker.commons;

import java.io.File;
import org.junit.jupiter.api.Assertions;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wavemaker.commons.util.WMFileUtils;
import com.wavemaker.commons.util.WMIOUtils;


/**
 * @author Simon Toens
 */
public abstract class WMTestCase {

    private static final Level DEFAULT_LEVEL = Level.INFO;

    private static final Logger logger = Logger.global;

    static {
        logger.setLevel(DEFAULT_LEVEL);
    }

    protected WMTestCase() {
    }

    protected void info(Object o) {
        logger.info(String.valueOf(o));
    }

    protected void warn(Object o) {
        logger.warning(String.valueOf(o));
    }

    protected File createTempDir() {
        try {
            return WMIOUtils.createTempDirectory();
        } catch (IOException ex) {
            throw new WMRuntimeException(ex);
        }
    }

    public static void assertEquals(File expected, File actual) throws IOException {
        assertEquals(null, expected, actual);
    }

    public static void assertEquals(String message, File expected, File actual) throws IOException {

        String msg = "mismatch between expected file \"" + expected + "\" and actual \"" + actual + "\"";
        if (message != null) {
            msg = message + ": " + msg;
        }

        if (expected.equals(actual)) {
            // pass
        } else if (expected.isFile() && actual.isFile()) {
            String expectedStr = WMFileUtils.readFileToString(expected);
            String actualStr = WMFileUtils.readFileToString(actual);

            Assertions.assertEquals(expectedStr, actualStr, msg);
        } else if (expected.isDirectory() && actual.isDirectory()) {
            Assertions.assertEquals(expected.getAbsolutePath(), actual.getAbsolutePath(), msg);
        } else {
            Assertions.fail(msg);
        }
    }
}
