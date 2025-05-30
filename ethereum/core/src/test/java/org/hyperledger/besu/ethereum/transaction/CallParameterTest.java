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
package org.hyperledger.besu.ethereum.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.tuweni.bytes.Bytes;
import org.junit.jupiter.api.Test;

public class CallParameterTest {

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

  @Test
  public void acceptsAndCapMaxValueForGas() throws JsonProcessingException {
    final String json =
        """
        {
          "gas": "0xffffffffffffffff"
        }
        """;

    final CallParameter callParameter = objectMapper.readValue(json, CallParameter.class);

    assertThat(callParameter.getGas()).hasValue(Long.MAX_VALUE);
  }

  @Test
  public void dataAsPayLoad() throws JsonProcessingException {
    final String json =
        """
        {
          "data": "0x1234"
        }
        """;

    final CallParameter callParameter = objectMapper.readValue(json, CallParameter.class);

    assertThat(callParameter.getPayload()).contains(Bytes.fromHexString("0x1234"));
  }

  @Test
  public void inputAsPayLoad() throws JsonProcessingException {
    final String json =
        """
            {
              "input": "0x1234"
            }
            """;

    final CallParameter callParameter = objectMapper.readValue(json, CallParameter.class);

    assertThat(callParameter.getPayload()).contains(Bytes.fromHexString("0x1234"));
  }

  @Test
  public void inputAndDataWithSameValueAsPayLoad() throws JsonProcessingException {
    final String json =
        """
            {
              "input": "0x1234",
              "data": "0x1234"
            }
            """;

    final CallParameter callParameter = objectMapper.readValue(json, CallParameter.class);

    assertThat(callParameter.getPayload()).contains(Bytes.fromHexString("0x1234"));
  }

  @Test
  public void inputAndDataWithDifferentValueAsPayLoadCauseException() {
    final String json =
        """
            {
              "input": "0x1234",
              "data": "0x1235"
            }
            """;

    assertThatExceptionOfType(JsonMappingException.class)
        .isThrownBy(() -> objectMapper.readValue(json, CallParameter.class))
        .withMessageContaining("problem: Only one of 'input' or 'data' should be provided");
  }

  @Test
  public void extraParametersAreIgnored() throws JsonProcessingException {
    // 0x96 = 150
    final String json =
        """
            {
              "gas": "0x96",
              "gasLimit": "0xfa",
              "extraField": "extra"
            }
            """;

    final CallParameter callParameter = objectMapper.readValue(json, CallParameter.class);

    assertThat(callParameter.getGas()).hasValue(150);
  }
}
