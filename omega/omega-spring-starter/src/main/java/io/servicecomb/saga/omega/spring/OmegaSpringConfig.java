/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.saga.omega.spring;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.servicecomb.saga.omega.connector.thrift.ThriftMessageSender;
import io.servicecomb.saga.omega.format.NativeMessageFormat;
import io.servicecomb.saga.omega.transaction.MessageSender;

@Configuration
class OmegaSpringConfig {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Bean
  MessageSender messageSender(@Value("${alpha.cluster.address}") String[] addresses) {
    // TODO: 2017/12/26 connect to the one with lowest latency
    for (String address : addresses) {
      try {
        String[] pair = address.split(":");
        return ThriftMessageSender.create(pair[0], Integer.parseInt(pair[1]), new NativeMessageFormat());
      } catch (Exception e) {
        log.error("Unable to connect to alpha at {}", address, e);
      }
    }

    throw new IllegalArgumentException(
        "None of the alpha cluster is reachable: " + Arrays.toString(addresses));
  }
}
