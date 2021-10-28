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

package org.apache.doris.stack.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.doris.manager.common.domain.RResult;
import org.apache.doris.stack.entity.ProcessInstanceEntity;
import org.apache.doris.stack.entity.TaskInstanceEntity;
import org.apache.doris.stack.model.request.AgentCommon;
import org.apache.doris.stack.model.request.AgentInstallReq;
import org.apache.doris.stack.model.request.AgentRegister;
import org.apache.doris.stack.service.ServerProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "Server API")
@RestController
@RequestMapping("/api/server")
@Slf4j
public class ServerController {

    @Autowired
    private ServerProcess serverProcess;

    /**
     * query user history installation progress
     */
    @ApiOperation(value = "query user history installation progress")
    @RequestMapping(value = "/historyProgress", method = RequestMethod.POST)
    public RResult historyProgress(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProcessInstanceEntity process = serverProcess.historyProgress(request, response);
        return RResult.success(process);
    }

    /**
     * Installation progress of the current process
     */
    @ApiOperation(value = "query user history installation progress")
    @RequestMapping(value = "/progress/{processId}", method = RequestMethod.GET)
    public RResult processProgress(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable(value = "processId") int processId) throws Exception {
        List<TaskInstanceEntity> tasks = serverProcess.processProgress(request, response, processId);
        return RResult.success(tasks);
    }

    /**
     * After the installation is complete, call the interface
     */
    @ApiOperation(value = "After the installation is complete, call the interface")
    @RequestMapping(value = "/installComplete/{processId}", method = RequestMethod.POST)
    public RResult installComplete(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable(value = "processId") int processId) throws Exception {
        serverProcess.installComplete(request, response, processId);
        return RResult.success();
    }

    /**
     * install and start agent and return processId
     */
    @ApiOperation(value = "install agent and return processId")
    @RequestMapping(value = "/installAgent", method = RequestMethod.POST)
    public RResult installAgent(HttpServletRequest request, HttpServletResponse response,
                                @RequestBody AgentInstallReq agentInstallReq) throws Exception {
        int processId = serverProcess.installAgent(request, response, agentInstallReq);
        return RResult.success(processId);
    }

    /**
     * agent info list
     */
    @ApiOperation(value = "agent info list")
    @RequestMapping(value = "/agentList", method = RequestMethod.POST)
    public RResult agentList() {
        return RResult.success(serverProcess.agentList());
    }

    /**
     * agent role info
     */
    @ApiOperation(value = "agent role info")
    @RequestMapping(value = "/agentRole", method = RequestMethod.GET)
    public RResult agentRole(@RequestParam String host) {
        return RResult.success(serverProcess.agentRole(host));
    }

    /**
     * heatbeat report api
     */
    @ApiOperation(value = "agent heartbeat report")
    @RequestMapping(value = "/heartbeat", method = RequestMethod.POST)
    public RResult heartbeat(@RequestBody AgentCommon agent) {
        log.info("{} heartbeat.", agent.getHost());
        serverProcess.heartbeat(agent.getHost(), agent.getPort());
        return RResult.success();
    }

    /**
     * register agent
     */
    @ApiOperation(value = "agent register")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public RResult register(@RequestBody AgentRegister agent) {
        log.info("{} register.", agent.getHost());
        boolean register = serverProcess.register(agent);
        return RResult.success(register);
    }
}
