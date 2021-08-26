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

package org.apache.doris.manager.agent.register;

import com.alibaba.fastjson.JSON;
import org.apache.doris.manager.agent.util.Request;
import org.apache.doris.manager.common.domain.RResult;

import java.util.HashMap;
import java.util.Map;

public class AgentRegister {

    public static boolean register() {

        String requestUrl = "http://" + AgentContext.getAgentServer() + "/server/register";
        Map<String, Object> map = new HashMap<>();
        map.put("host", AgentContext.getAgentIp());
        map.put("port", AgentContext.getAgentPort());
        map.put("installDir", AgentContext.getAgentInstallDir());

        RResult res = null;
        try {
            String result = Request.sendPostRequest(requestUrl, map);
            res = JSON.parseObject(result, RResult.class);
        } catch (Exception ex) {
            return false;
        }
        if (res != null && new Boolean(true).equals(res.getData())) {
            return true;
        }
        return false;
    }
}