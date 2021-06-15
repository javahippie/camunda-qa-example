package com.camunda.training;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.assertj.core.api.Assertions.*;

public class ProcessJUnitTest {

  @Rule
  @ClassRule
  public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

  @Before
  public void setup() {
    init(rule.getProcessEngine());
  }
  
  @Test
  @Deployment(resources = "twitter.bpmn")
  public void testHappyPath() {
    // Create a HashMap to put in variables for the process instance
    Map<String, Object> variables = new HashMap<>();
    variables.put("content", "Heyyyyyyyyyyyyy");
    // Start process with Java API and variables
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("Process_TweetApproval", variables);

    List<Task> taskList = taskService()
            .createTaskQuery()
            .taskCandidateGroup("management")
            .processInstanceId(processInstance.getId())
            .list();



    assertThat(taskList).isNotNull();
    assertThat(taskList).hasSize(1);

    Task task = taskList.get(0);

    Map<String, Object> approvedMap = new HashMap<String, Object>();
    taskService().complete(task.getId(), Map.of("approved", true));

    // Make assertions on the process instance
    assertThat(processInstance).isEnded();
  }

}
