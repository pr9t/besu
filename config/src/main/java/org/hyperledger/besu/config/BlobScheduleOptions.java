/*
 * Copyright contributors to Hyperledger Besu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.config;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;

/** The Blob Schedule config options. */
public class BlobScheduleOptions {

  private final ObjectNode blobScheduleOptionsConfigRoot;

  private static final String CANCUN_KEY = "cancun";
  private static final String PRAGUE_KEY = "prague";
  private static final String OSAKA_KEY = "osaka";
  private static final String FUTURE_EIPS_KEY = "future_eips";

  /**
   * Instantiates a new Blob Schedule config options.
   *
   * @param blobScheduleConfigRoot the blob schedule config root
   */
  public BlobScheduleOptions(final ObjectNode blobScheduleConfigRoot) {
    this.blobScheduleOptionsConfigRoot = blobScheduleConfigRoot;
  }

  /**
   * Gets cancun blob schedule.
   *
   * @return the cancun blob schedule
   */
  public Optional<BlobSchedule> getCancun() {
    return JsonUtil.getObjectNode(blobScheduleOptionsConfigRoot, CANCUN_KEY).map(BlobSchedule::new);
  }

  /**
   * Gets prague blob schedule.
   *
   * @return the prague blob schedule
   */
  public Optional<BlobSchedule> getPrague() {
    return JsonUtil.getObjectNode(blobScheduleOptionsConfigRoot, PRAGUE_KEY).map(BlobSchedule::new);
  }

  /**
   * Gets osaka blob schedule.
   *
   * @return the osaka blob schedule
   */
  public Optional<BlobSchedule> getOsaka() {
    return JsonUtil.getObjectNode(blobScheduleOptionsConfigRoot, OSAKA_KEY).map(BlobSchedule::new);
  }

  /**
   * Gets future eips blob schedule.
   *
   * @return the future eips blob schedule
   */
  public Optional<BlobSchedule> getFutureEips() {
    return JsonUtil.getObjectNode(blobScheduleOptionsConfigRoot, FUTURE_EIPS_KEY)
        .map(BlobSchedule::new);
  }

  /**
   * As map.
   *
   * @return the map
   */
  public Map<String, Object> asMap() {
    final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    getCancun().ifPresent(bs -> builder.put(CANCUN_KEY, bs.asMap()));
    getPrague().ifPresent(bs -> builder.put(PRAGUE_KEY, bs.asMap()));
    getOsaka().ifPresent(bs -> builder.put(OSAKA_KEY, bs.asMap()));
    getFutureEips().ifPresent(bs -> builder.put(FUTURE_EIPS_KEY, bs.asMap()));
    return builder.build();
  }
}
