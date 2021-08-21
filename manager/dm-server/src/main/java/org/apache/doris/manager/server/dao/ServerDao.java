// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.doris.manager.server.dao;


import org.apache.doris.manager.server.entity.AgentEntity;
import org.apache.doris.manager.server.entity.AgentRoleEntity;
import org.apache.doris.manager.server.model.req.AgentRegister;

import java.util.List;

/**
 * server dao
 **/
public interface ServerDao {

    List<AgentEntity> queryAgentNodes();

    List<AgentEntity> queryAgentNodes(List<String> hosts);

    AgentEntity agentInfo(String host);

    int refreshAgentStatus(String host, Integer port);

    int registerAgent(AgentRegister agent);

    int updateBatchAgentStatus(List<AgentEntity> agents);

    int registerAgentRoles(List<AgentRoleEntity> agentRoles);

    int registerAgentRole(AgentRoleEntity agentRole);

    List<AgentRoleEntity> agentRoles(String host, String role);

    List<AgentRoleEntity> agentRoles(String host);

    List<AgentRoleEntity> agentRoles();

}
