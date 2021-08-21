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
package org.apache.doris.manager.server.service;


import org.apache.doris.manager.common.domain.RResult;
import org.apache.doris.manager.server.model.req.AgentReg;
import org.apache.doris.manager.server.model.req.DorisExecReq;
import org.apache.doris.manager.server.model.req.DorisInstallReq;
import org.apache.doris.manager.server.model.req.TaskInfoReq;
import org.apache.doris.manager.server.model.req.TaskLogReq;

import java.util.List;


/**
 * server agent
 **/
public interface ServerAgent {

    /**
     * install doris
     */
    List<Object> install(DorisInstallReq installReq);

    /**
     * request agent rest api
     */
    List<Object> execute(DorisExecReq dorisExec);

    /**
     * fetch task info
     */
    RResult taskInfo(TaskInfoReq taskInfo);

    /**
     * fetch log
     */
    RResult taskStdlog(TaskLogReq taskInfo);

    RResult taskErrlog(TaskLogReq taskInfo);

    void joinBe(List<String> hosts);

    boolean register(AgentReg agentReg);
}
