/*
 * Created by Angel Leon (@gubatron)
 * Copyright (c) 2007-2020, FrostWire(R). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frostwire.tests;

import com.frostwire.telluride.TellurideLauncher;
import com.frostwire.telluride.TellurideListener;
import com.frostwire.util.OSUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.fail;

public class TellurideTests {
    static String executableSuffix = ".exe";

    static {
        if (OSUtils.isAnyMac()) {
            executableSuffix = "_macos";
        } else if (OSUtils.isLinux()) {
            executableSuffix = "_linux";
        }

        File data = new File("/Users/gubatron/FrostWire/Torrent Data", "Alone_Together_-_Mona_Wonderlick_Free_Copyright-safe_Music-1kaQP9XL6L4.mkv");
        if (data.exists()) {
            data.delete();
        }
    }

    static boolean progressWasReported = false;

    @Test
    public void testDownload() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TellurideListener tellurideListener = new TellurideListener() {
            @Override
            public void onProgress(float completionPercentage, float fileSize, String fileSizeUnits, float downloadSpeed, String downloadSpeedUnits, String ETA) {
                System.out.println("[TellurideTests][testDownload] onProgress(completionPercentage=" +
                        completionPercentage + ", fileSize=" +
                        fileSize + ", fileSizeUnits=" +
                        fileSizeUnits + ", downloadSpeed=" +
                        downloadSpeed + ", downloadSpeedUnits=" +
                        downloadSpeedUnits + ", ETA=" + ETA + ")");
                progressWasReported = true;
            }

            @Override
            public void onError(String errorMessage) {
                fail("[TellurideTests][testDownload] onError(errorMessage=" + errorMessage + ")");
            }

            @Override
            public void onFinished(int exitCode) {
                latch.countDown();
                if (exitCode != 0) {
                    fail("[TellurideTests][testDownload] onFinished(exitCode=" + exitCode + ")");
                }
                if (!progressWasReported) {
                    fail("[TellurideTests][testDownload] Failed, did not receive onProgress call. onFinished(exitCode=" + exitCode + ")");
                }
            }

            @Override
            public void onDestination(String outputFilename) {
                System.out.println("[TellurideTests][testDownload] onDestination(outputFilename=" + outputFilename + ")");
            }

            @Override
            public boolean aborted() {
                return false;
            }

            @Override
            public void onMeta(String json) {
                System.out.println("[TellurideTests][testDownload] GOT JSON!");
                System.out.println(json);
            }

            @Override
            public int hashCode() {
                return 1;
            }
        };

        TellurideLauncher.launch(new File("/Users/gubatron/workspace.frostwire/frostwire/telluride/telluride" + executableSuffix),
                "https://www.youtube.com/watch?v=1kaQP9XL6L4", // Alone Together - Mona Wonderlick · [Free Copyright-safe Music]
                new File("/Users/gubatron/FrostWire/Torrent Data"),
                false,
                false,
                tellurideListener);

        System.out.println("[TellurideTests][testDownload] waiting...");
        latch.await();
        System.out.println("[TellurideTests][testDownload] finished.");
    }

    @Test
    public void testMetaOnly() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TellurideListener tellurideListener = new TellurideListener() {
            @Override
            public void onProgress(float completionPercentage, float fileSize, String fileSizeUnits, float downloadSpeed, String downloadSpeedUnits, String ETA) {
            }

            @Override
            public void onError(String errorMessage) {
                fail("[TellurideTests][testMetaOnly] onError(errorMessage=" + errorMessage + ")");
            }

            @Override
            public void onFinished(int exitCode) {
                latch.countDown();
                if (exitCode != 0) {
                    fail("[TellurideTests][testMetaOnly] onFinished(exitCode=" + exitCode + ")");
                }
            }

            @Override
            public void onDestination(String outputFilename) {
            }

            @Override
            public boolean aborted() {
                return false;
            }

            @Override
            public void onMeta(String json) {
                System.out.println("[TellurideTests][testMetaOnly] GOT JSON!");
                System.out.println(json);
            }

            @Override
            public int hashCode() {
                return 1;
            }
        };

        TellurideLauncher.launch(new File("/Users/gubatron/workspace.frostwire/frostwire/telluride/telluride" + executableSuffix),
                "https://www.youtube.com/watch?v=1kaQP9XL6L4", // Alone Together - Mona Wonderlick · [Free Copyright-safe Music]
                new File("/Users/gubatron/FrostWire/Torrent Data"),
                false,
                true,
                tellurideListener);

        System.out.println("[TellurideTests][testMetaOnly] waiting...");
        latch.await();
        System.out.println("[TellurideTests][testMetaOnly] finished.");
    }
}
