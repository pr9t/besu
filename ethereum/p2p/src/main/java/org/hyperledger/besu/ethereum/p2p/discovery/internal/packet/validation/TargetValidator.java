/*
 * Copyright contributors to Besu.
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
package org.hyperledger.besu.ethereum.p2p.discovery.internal.packet.validation;

import static com.google.common.base.Preconditions.checkArgument;

import org.hyperledger.besu.ethereum.p2p.discovery.internal.packet.findneighbors.FindNeighborsPacketData;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.tuweni.bytes.Bytes;

@Singleton
public class TargetValidator {

  public @Inject TargetValidator() {}

  public void validate(final Bytes target) {
    checkArgument(
        target != null && target.size() == FindNeighborsPacketData.TARGET_SIZE,
        "target must be a valid node id");
  }
}
