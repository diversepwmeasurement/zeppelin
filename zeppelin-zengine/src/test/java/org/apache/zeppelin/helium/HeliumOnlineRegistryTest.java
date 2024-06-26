/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zeppelin.helium;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.zeppelin.conf.ZeppelinConfiguration;


class HeliumOnlineRegistryTest {
  // ip 192.168.65.17 belongs to private network
  // request will be ended with connection time out error
  private static final String IP = "192.168.65.17";
  private static final String TIMEOUT = "2000";

  private File tmpDir;

  @BeforeEach
  public void setUp() throws Exception {
    tmpDir = Files.createTempDirectory("ZeppelinLTest").toFile();
  }

  @AfterEach
  public void tearDown() throws IOException {
    FileUtils.deleteDirectory(tmpDir);
  }

  @Test
  void zeppelinNotebookS3TimeoutPropertyTest() throws IOException {
    ZeppelinConfiguration zConf = ZeppelinConfiguration.load();
    zConf.setProperty(ZeppelinConfiguration.ConfVars.ZEPPELIN_NOTEBOOK_S3_TIMEOUT.getVarName(), TIMEOUT);
    zConf.setProperty(ZeppelinConfiguration.ConfVars.ZEPPELIN_NOTEBOOK_S3_ENDPOINT.getVarName(), IP);
    HeliumOnlineRegistry heliumOnlineRegistry = new HeliumOnlineRegistry(
            "https://" + IP,
            "https://" + IP,
            tmpDir, zConf
    );

    long start = System.currentTimeMillis();
    heliumOnlineRegistry.getAll();
    long processTime = System.currentTimeMillis() - start;

    long basicTimeout = Long.valueOf(
            ZeppelinConfiguration.ConfVars.ZEPPELIN_NOTEBOOK_S3_TIMEOUT.getStringValue()
    );
    assertTrue(
            basicTimeout > processTime,
            String.format(
                    "Wrong timeout during connection: expected %s, actual is about %d",
                    TIMEOUT,
                    processTime
            )
    );
  }
}
