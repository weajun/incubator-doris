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
package org.apache.doris.manager.server.mapper;

import org.apache.doris.manager.server.entity.AgentEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * agent row mapper
 **/
public class AgentEntityMapper implements RowMapper<AgentEntity> {

    @Override
    public AgentEntity mapRow(ResultSet resultSet, int i) throws SQLException {
        Integer id = resultSet.getInt("id");
        String ht = resultSet.getString("host");
        Integer pt = resultSet.getInt("port");
        String installDir  = resultSet.getString("install_dir");
        String status = resultSet.getString("status");
        Date registerTime = resultSet.getTimestamp("register_time");
        Date lastReportedTime = resultSet.getTimestamp("last_reported_time");
        return new AgentEntity(id, ht, pt,installDir, status, registerTime, lastReportedTime);
    }
}
