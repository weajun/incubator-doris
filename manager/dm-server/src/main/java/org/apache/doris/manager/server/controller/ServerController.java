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
package org.apache.doris.manager.server.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.doris.manager.common.domain.RResult;
import org.apache.doris.manager.server.model.req.AgentCommon;
import org.apache.doris.manager.server.model.req.AgentRegister;
import org.apache.doris.manager.server.model.req.SshInfo;
import org.apache.doris.manager.server.service.ServerProcess;
import org.apache.doris.manager.server.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.doris.manager.server.constants.Constants.KEY_DORIS_AGENT_INSTALL_DIR;

@RestController
@RequestMapping("/server")
public class ServerController {

    private static final Logger log = LoggerFactory.getLogger(ServerController.class);
    private static final String AGENT_INSTALL_DIR = PropertiesUtil.getPropValue(KEY_DORIS_AGENT_INSTALL_DIR);

    @Autowired
    private ServerProcess serverProcess;

    /**
     * install and start agent
     */
    @RequestMapping(value = "/installAgent", method = RequestMethod.POST)
    public RResult installAgent(@RequestBody SshInfo sshInfo) {
        if(StringUtils.isBlank(sshInfo.getInstallDir())){
            sshInfo.setInstallDir(AGENT_INSTALL_DIR);
        }
        serverProcess.initAgent(sshInfo);
        serverProcess.startAgent(sshInfo);
        return RResult.success();
    }

    /**
     * agent info list
     */
    @RequestMapping(value = "/agentList", method = RequestMethod.POST)
    public RResult agentList() {
        return RResult.success(serverProcess.agentList());
    }

    /**
     * agent role info
     */
    @RequestMapping(value = "/agentRole", method = RequestMethod.GET)
    public RResult agentRole(@RequestParam String host) {
        return RResult.success(serverProcess.agentRole(host));
    }

    /**
     * heatbeat agent
     */
    @RequestMapping(value = "/heartbeat", method = RequestMethod.POST)
    public RResult heartbeat(@RequestBody AgentCommon agent) {
        log.info("{} heartbeat.", agent.getHost());
        serverProcess.heartbeat(agent.getHost(), agent.getPort());
        return RResult.success();
    }

    /**
     * register agent
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public RResult register(@RequestBody AgentRegister agent) {
        log.info("{} register.", agent.getHost());
        boolean register = serverProcess.register(agent);
        return RResult.success(register);
    }

}
