package org.apache.doris.stack.runner;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.apache.doris.manager.common.domain.CommandRequest;
import org.apache.doris.manager.common.domain.CommandResult;
import org.apache.doris.manager.common.domain.RResult;
import org.apache.doris.manager.common.domain.TaskResult;
import org.apache.doris.stack.agent.AgentCache;
import org.apache.doris.stack.agent.AgentRest;
import org.apache.doris.stack.entity.TaskInstanceEntity;
import org.apache.doris.stack.model.task.TaskDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

/**
 * taskExecutor
 **/
@Slf4j
@Component
public class TaskExecutor {

    /**
     * thread executor service
     */
    private final ListeningExecutorService taskExecService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(20));
    @Autowired
    private AgentRest agentRest;
    @Autowired
    private AgentCache agentCache;

    /**
     * execute common task : install agent/ join be
     */
    public void execTask(TaskInstanceEntity taskInstance, TaskDesc taskJson) {
        TaskContext taskContext = new TaskContext(taskInstance.getTaskType(), taskInstance, taskJson);
        ListenableFuture<Object> submit = taskExecService.submit(new TaskExecuteThread(taskContext));
        Futures.addCallback(submit, new TaskExecCallback(taskContext));
    }

    /**
     * execute agent task: install fe / start fe / deploy config
     */
    public RResult execAgentTask(TaskInstanceEntity taskInstance, CommandRequest request) {
        String host = taskInstance.getHost();
        int agentPort = agentCache.agentPort(taskInstance.getHost());
        RResult result = agentRest.commandExec(host, agentPort, request);
        return refreshAgentResult(host, agentPort, result);
    }

    /**
     * Re-fetch result after requesting the agent interface each time
     */
    private RResult refreshAgentResult(String host, Integer port, RResult result) {
        if (result == null || result.getData() == null) {
            return null;
        }
        CommandResult commandResult = JSON.parseObject(JSON.toJSONString(result.getData()), CommandResult.class);
        if (commandResult == null) {
            return null;
        }
        TaskResult taskResult = commandResult.getTaskResult();
        if (taskResult == null) {
            return null;
        }
        return agentRest.taskInfo(host, port, taskResult.getTaskId());
    }
}
