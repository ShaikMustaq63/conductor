/*
 * Copyright 2016 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.conductor.client.http;

import com.google.common.base.Preconditions;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class MetadataClient extends ClientBase {

    private static GenericType<List<WorkflowDef>> workflowDefList = new GenericType<List<WorkflowDef>>() {
    };

    private static GenericType<List<TaskDef>> taskDefList = new GenericType<List<TaskDef>>() {
    };

    /**
     * Creates a default metadata client
     */
    public MetadataClient() {
        super();
    }

    /**
     * @param clientConfig REST Client configuration
     */
    public MetadataClient(ClientConfig clientConfig) {
        super(clientConfig);
    }

    /**
     * @param clientConfig  REST Client configuration
     * @param clientHandler Jersey client handler. Useful when plugging in various http client interaction modules (e.g. ribbon)
     */
    public MetadataClient(ClientConfig clientConfig, ClientHandler clientHandler) {
        super(clientConfig, clientHandler);
    }

    /**
     * @param config  config REST Client configuration
     * @param handler handler Jersey client handler. Useful when plugging in various http client interaction modules (e.g. ribbon)
     * @param filters Chain of client side filters to be applied per request
     */
    public MetadataClient(ClientConfig config, ClientHandler handler, ClientFilter... filters) {
        super(config, handler);
        for (ClientFilter filter : filters) {
            super.client.addFilter(filter);
        }
    }


    // Workflow Metadata Operations

    /**
     * Register a workflow definition with the server
     *
     * @param workflowDef the workflow definition
     */
    public void registerWorkflowDef(WorkflowDef workflowDef) {
        Preconditions.checkNotNull(workflowDef, "Worfklow definition cannot be null");
        postForEntity("metadata/workflow", workflowDef);
    }

    /**
     * Updates a list of existing workflow definitions
     *
     * @param workflowDefs List of workflow definitions to be updated
     */
    public void updateWorkflowDefs(List<WorkflowDef> workflowDefs) {
        Preconditions.checkNotNull(workflowDefs, "Workflow defs list cannot be null");
        put("metadata/workflow", null, workflowDefs);
    }

    /**
     * Retrieve the workflow definition
     *
     * @param name    the name of the workflow
     * @param version the version of the workflow def
     * @return Workflow definition for the given workflow and version
     */
    public WorkflowDef getWorkflowDef(String name, Integer version) {
        Preconditions.checkArgument(StringUtils.isNotBlank(name), "name cannot be blank");
        return getForEntity("metadata/workflow/{name}", new Object[]{"version", version}, WorkflowDef.class, name);
    }

    /**
     * Retrieve all workflow definitions
     *
     * @return List of all workflow definitions registered with the server
     */
    public List<WorkflowDef> getAllWorkflowDefs() {
        return getForEntity("metadata/workflow", null, workflowDefList);
    }


    // Task Metadata Operations

    /**
     * Registers a list of task types with the conductor server
     *
     * @param taskDefs List of task types to be registered.
     */
    public void registerTaskDefs(List<TaskDef> taskDefs) {
        Preconditions.checkNotNull(taskDefs, "Task defs list cannot be null");
        postForEntity("metadata/taskdefs", taskDefs);
    }

    /**
     * Updates an existing task definition
     *
     * @param taskDef the task definition to be updated
     */
    public void updateTaskDef(TaskDef taskDef) {
        Preconditions.checkNotNull(taskDef, "Task definition cannot be null");
        put("metadata/taskdefs", null, taskDef);
    }

    /**
     * Retrieve all task definitions
     *
     * @return List of all the task definitions registered with the server
     */
    public List<TaskDef> getAllTaskDefs() {
        return getForEntity("metadata/taskdefs", null, taskDefList);
    }

    /**
     * Retrieve the task definition of a given task type
     *
     * @param taskType type of task for which to retrieve the definition
     * @return Task Definition for the given task type
     */
    public TaskDef getTaskDef(String taskType) {
        Preconditions.checkArgument(StringUtils.isNotBlank(taskType), "Task type cannot be blank");
        return getForEntity("metadata/taskdefs/{tasktype}", null, TaskDef.class, taskType);
    }

    /**
     * Removes a task type from the conductor server. Use with caution.
     *
     * @param taskType Task type to be unregistered.
     */
    public void unregisterTaskDef(String taskType) {
        Preconditions.checkArgument(StringUtils.isNotBlank(taskType), "Task type cannot be blank");
        delete("metadata/taskdefs/{tasktype}", taskType);
    }
}
