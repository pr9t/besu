/*
 * Copyright 2020 ConsenSys AG.
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
package org.hyperledger.besu.consensus.qbft.core.messagewrappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.hyperledger.besu.consensus.common.bft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.common.bft.payload.SignedData;
import org.hyperledger.besu.consensus.qbft.core.QbftBlockTestFixture;
import org.hyperledger.besu.consensus.qbft.core.messagedata.QbftV1;
import org.hyperledger.besu.consensus.qbft.core.payload.PreparePayload;
import org.hyperledger.besu.consensus.qbft.core.payload.PreparedRoundMetadata;
import org.hyperledger.besu.consensus.qbft.core.payload.RoundChangePayload;
import org.hyperledger.besu.consensus.qbft.core.types.QbftBlock;
import org.hyperledger.besu.consensus.qbft.core.types.QbftBlockCodec;
import org.hyperledger.besu.cryptoservices.NodeKey;
import org.hyperledger.besu.cryptoservices.NodeKeyUtils;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.ethereum.core.Util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoundChangeTest {
  @Mock private QbftBlockCodec blockEncoder;

  private static final QbftBlock BLOCK = new QbftBlockTestFixture().build();

  @Test
  public void canRoundTripARoundChangeMessage() {
    when(blockEncoder.readFrom(any())).thenReturn(BLOCK);

    final NodeKey nodeKey = NodeKeyUtils.generate();
    final Address addr = Util.publicKeyToAddress(nodeKey.getPublicKey());

    final RoundChangePayload payload =
        new RoundChangePayload(
            new ConsensusRoundIdentifier(1, 1),
            Optional.of(new PreparedRoundMetadata(BLOCK.getHash(), 0)));

    final SignedData<RoundChangePayload> signedRoundChangePayload =
        SignedData.create(payload, nodeKey.sign(payload.hashForSignature()));

    final PreparePayload preparePayload =
        new PreparePayload(new ConsensusRoundIdentifier(1, 0), BLOCK.getHash());
    final SignedData<PreparePayload> signedPreparePayload =
        SignedData.create(preparePayload, nodeKey.sign(preparePayload.hashForSignature()));

    final RoundChange roundChange =
        new RoundChange(
            signedRoundChangePayload,
            Optional.of(BLOCK),
            blockEncoder,
            List.of(signedPreparePayload));

    final RoundChange decodedRoundChange = RoundChange.decode(roundChange.encode(), blockEncoder);

    assertThat(decodedRoundChange.getMessageType()).isEqualTo(QbftV1.ROUND_CHANGE);
    assertThat(decodedRoundChange.getAuthor()).isEqualTo(addr);
    assertThat(decodedRoundChange.getSignedPayload())
        .isEqualToComparingFieldByField(signedRoundChangePayload);
    assertThat(decodedRoundChange.getProposedBlock()).isNotEmpty();
    assertThat(decodedRoundChange.getProposedBlock().get()).isEqualToComparingFieldByField(BLOCK);
    assertThat(decodedRoundChange.getPrepares()).hasSize(1);
    assertThat(decodedRoundChange.getPrepares().getFirst())
        .isEqualToComparingFieldByField(signedPreparePayload);
  }

  @Test
  public void canRoundTripEmptyPreparedRoundAndPreparedList() {
    final NodeKey nodeKey = NodeKeyUtils.generate();
    final Address addr = Util.publicKeyToAddress(nodeKey.getPublicKey());

    final RoundChangePayload payload =
        new RoundChangePayload(new ConsensusRoundIdentifier(1, 1), Optional.empty());

    final SignedData<RoundChangePayload> signedRoundChangePayload =
        SignedData.create(payload, nodeKey.sign(payload.hashForSignature()));

    final RoundChange roundChange =
        new RoundChange(
            signedRoundChangePayload, Optional.empty(), blockEncoder, Collections.emptyList());

    final RoundChange decodedRoundChange = RoundChange.decode(roundChange.encode(), blockEncoder);

    assertThat(decodedRoundChange.getMessageType()).isEqualTo(QbftV1.ROUND_CHANGE);
    assertThat(decodedRoundChange.getAuthor()).isEqualTo(addr);
    assertThat(decodedRoundChange.getSignedPayload())
        .isEqualToComparingFieldByField(signedRoundChangePayload);
    assertThat(decodedRoundChange.getProposedBlock()).isEmpty();
    assertThat(decodedRoundChange.getPrepares()).isEmpty();
  }
}
