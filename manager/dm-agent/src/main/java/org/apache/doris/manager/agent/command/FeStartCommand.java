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

package org.apache.doris.manager.agent.command;

import org.apache.doris.manager.agent.common.AgentConstants;
import org.apache.doris.manager.agent.service.ServiceContext;
import org.apache.doris.manager.agent.task.ITaskHandlerFactory;
import org.apache.doris.manager.agent.task.QueuedTaskHandlerFactory;
import org.apache.doris.manager.agent.task.ScriptTask;
import org.apache.doris.manager.agent.task.ScriptTaskDesc;
import org.apache.doris.manager.agent.task.Task;
import org.apache.doris.manager.agent.task.TaskHandlerFactory;
import org.apache.doris.manager.common.domain.CommandType;
import org.apache.doris.manager.common.domain.FeStartCommandRequestBody;
import org.apache.doris.manager.common.domain.ServiceRole;

import java.util.Objects;

public class FeStartCommand extends FeCommand {
    private FeStartCommandRequestBody requestBody;

    public FeStartCommand(FeStartCommandRequestBody requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public Task setupTask() {
        ScriptTaskDesc taskDesc = new ScriptTaskDesc();

        String scriptCmd = AgentConstants.BASH_BIN;
        scriptCmd += ServiceContext.getServiceMap().get(ServiceRole.FE).getInstallDir() + "/bin/start_fe.sh ";
        if (Objects.nonNull(requestBody)) {
            if (Objects.nonNull(requestBody.getHelpHostPort()) && requestBody.getHelpHostPort().length() > 0) {
                scriptCmd += " --help " + requestBody.getHelpHostPort();
            }
        }
        scriptCmd += " --daemon";
        taskDesc.setScriptCmd(scriptCmd);
        return new ScriptTask(taskDesc);
    }

    @Override
    public ITaskHandlerFactory setupTaskHandlerFactory() {
        return TaskHandlerFactory.getTaskHandlerFactory(QueuedTaskHandlerFactory.class);
    }

    @Override
    public CommandType setupCommandType() {
        return CommandType.START_FE;
    }
}