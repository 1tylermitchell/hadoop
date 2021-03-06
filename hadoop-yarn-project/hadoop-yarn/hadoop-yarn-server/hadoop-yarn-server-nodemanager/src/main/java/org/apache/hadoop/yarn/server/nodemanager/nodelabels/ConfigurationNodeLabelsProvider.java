/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.yarn.server.nodemanager.nodelabels;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

/**
 * Provides Node's Labels by constantly monitoring the configuration.
 */
public class ConfigurationNodeLabelsProvider extends AbstractNodeLabelsProvider {

  private static final Log LOG = LogFactory
      .getLog(ConfigurationNodeLabelsProvider.class);

  public ConfigurationNodeLabelsProvider() {
    super("Configuration Based NodeLabels Provider");
  }

  private void updateNodeLabelsFromConfig(Configuration conf)
      throws IOException {
    String confLabelString =
        conf.get(YarnConfiguration.NM_PROVIDER_CONFIGURED_NODE_LABELS, null);
    String[] nodeLabelsFromConfiguration =
        (confLabelString == null || confLabelString.isEmpty()) ? new String[] {}
            : StringUtils.getStrings(confLabelString);
    setNodeLabels(convertToNodeLabelSet(new HashSet<String>(
        Arrays.asList(nodeLabelsFromConfiguration))));
  }

  private class ConfigurationMonitorTimerTask extends TimerTask {
    @Override
    public void run() {
      try {
        updateNodeLabelsFromConfig(new YarnConfiguration());
      } catch (Exception e) {
        LOG.error("Failed to update node Labels from configuration.xml ", e);
      }
    }
  }

  @Override
  public TimerTask createTimerTask() {
    return new ConfigurationMonitorTimerTask();
  }
}
