/*
 * Copyright (C) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.caliper.config;

import static java.util.logging.Level.WARNING;

import com.google.caliper.options.CaliperDirectory;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Loading the logging configuration at {@code ~/.caliper/logging.properties} if present.
 */
@Singleton
final class LoggingConfigLoader {
  private static final Logger logger = Logger.getLogger(LoggingConfigLoader.class.getName());

  private final File caliperDirectory;
  private final LogManager logManager;

  @Inject LoggingConfigLoader(@CaliperDirectory File caliperDirectory, LogManager logManager) {
    this.caliperDirectory = caliperDirectory;
    this.logManager = logManager;
  }

  @Inject void loadLoggingConfig() {
    File loggingPropertiesFile = new File(caliperDirectory, "logging.properties");
    if (loggingPropertiesFile.isFile()) {
      FileInputStream fis = null;
      try {
        fis = new FileInputStream(loggingPropertiesFile);
        logManager.readConfiguration(fis);
      } catch (SecurityException e) {
        logConfigurationException(e);
      } catch (IOException e) {
        logConfigurationException(e);
      } finally {
        Closeables.closeQuietly(fis);
      }
    }
    logger.info(String.format("Using logging configuration at %s", loggingPropertiesFile));
  }

  private static void logConfigurationException(Exception e) {
    logger.log(WARNING, "Could not apply the logging configuration", e);
  }
}