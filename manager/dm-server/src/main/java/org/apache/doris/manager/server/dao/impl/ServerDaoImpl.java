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
package org.apache.doris.manager.server.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.doris.manager.server.constants.AgentStatus;
import org.apache.doris.manager.server.dao.ServerDao;
import org.apache.doris.manager.server.entity.AgentEntity;
import org.apache.doris.manager.server.entity.AgentRoleEntity;
import org.apache.doris.manager.server.mapper.AgentEntityMapper;
import org.apache.doris.manager.server.mapper.AgentRoleEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * server dao
 **/
@Repository
public class ServerDaoImpl implements ServerDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<AgentEntity> queryAgentNodes() {
        return queryAgentNodes(new ArrayList<>());
    }

    @Override
    public List<AgentEntity> queryAgentNodes(List<String> hosts) {
        String sql = "select id,host,port,status,register_time,last_reported_time" +
                " from t_agent ";
        if (hosts != null && !hosts.isEmpty()) {
            sql = sql + " where host in (%s)";
            String inSql = String.join(",", Collections.nCopies(hosts.size(), "?"));
            sql = String.format(sql, inSql);
        }
        List<AgentEntity> agentEntities = jdbcTemplate.query(
                sql, new AgentEntityMapper(), hosts.toArray());
        return agentEntities;
    }

    @Override
    public AgentEntity agentInfo(String host) {
        String sql = "select id,host,port,status,register_time,last_reported_time from t_agent where host = ? ";
        List<AgentEntity> agents = jdbcTemplate.query(sql, new AgentEntityMapper(), host);
        if (agents != null && !agents.isEmpty()) {
            return agents.get(0);
        }
        return null;
    }

    @Override
    public int refreshAgentStatus(String host, Integer port) {
        String sql = "update t_agent set last_reported_time = now(),status = ? where host = ? and port = ?";
        return jdbcTemplate.update(sql, AgentStatus.RUNNING.name(), host, port);
    }

    @Override
    public int registerAgent(String host, Integer port) {
        String sql = "insert into t_agent(host,port,status,register_time) values(?,?,?,now())";
        return jdbcTemplate.update(sql, host, port, AgentStatus.RUNNING.name());
    }

    @Override
    public int updateBatchAgentStatus(List<AgentEntity> agents) {
        String sql = "update t_agent set status = ? where id = ?";
        int[] i = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                AgentEntity agent = agents.get(i);
                ps.setString(1, agent.getStatus());
                ps.setInt(2, agent.getId());
            }

            public int getBatchSize() {
                return agents.size();
            }
        });
        return i.length;
    }

    @Override
    public int registerAgentRoles(List<AgentRoleEntity> agentRoles) {
        String sql = "insert into t_agent_role(host,role,install_dir) values(?,?,?)";
        int[] i = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                AgentRoleEntity node = agentRoles.get(i);
                ps.setString(1, node.getHost());
                ps.setString(2, node.getRole());
                ps.setString(3, node.getInstallDir());
            }

            public int getBatchSize() {
                return agentRoles.size();
            }
        });
        return i.length;
    }

    @Override
    public int registerAgentRole(AgentRoleEntity agentRole) {
        int cnt = jdbcTemplate.update(" insert into t_agent_role(host,role,install_dir) values(?,?,?)", ps -> {
            ps.setString(1, agentRole.getHost());
            ps.setString(2, agentRole.getRole());
            ps.setString(3, agentRole.getInstallDir());
        });
        return cnt;
    }

    @Override
    public List<AgentRoleEntity> agentRoles(String host, String role) {
        String sql = "select host,role,install_dir from t_agent_role where 1=1 ";
        List<Object> params = new ArrayList<>();
        if (StringUtils.isNotBlank(host)) {
            sql += "and host = ? ";
            params.add(host);
        }
        if (StringUtils.isNotBlank(role)) {
            sql += "and role = ? ";
            params.add(role);
        }
        List<AgentRoleEntity> agentRoles = jdbcTemplate.query(sql, new AgentRoleEntityMapper(), params.toArray());
        if (agentRoles == null) {
            return new ArrayList<>();
        }
        return agentRoles;
    }

    @Override
    public List<AgentRoleEntity> agentRoles(String host) {
        return agentRoles(host, null);
    }

    @Override
    public List<AgentRoleEntity> agentRoles() {
        return agentRoles(null, null);
    }
}
