package model.test;

import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

public class MessageCorrelationTest {

    @Rule
    public ProcessEngineRule engine = new ProcessEngineRule();

    public static final String MESSAGE_A = "MessageA";

    @Test(expected = MismatchingMessageCorrelationException.class)
    public void twoReceiversTest() {
        //given
        BpmnModelInstance modelInst = Bpmn.createExecutableProcess("MessageCorrelationTest")
                .startEvent()
                .parallelGateway("parallelGateway")
                .intermediateCatchEvent("Event1").message(MESSAGE_A)
                .endEvent("Path1Ended")
                .moveToLastGateway()
                .intermediateCatchEvent("Event2").message(MESSAGE_A)
                .endEvent().name("Path2Ended")
                .done();

        repositoryService().createDeployment()
                .addModelInstance("MessageCorrelationTest.bpmn", modelInst).deploy();
        //when
        ProcessInstance pi = runtimeService().startProcessInstanceByKey("MessageCorrelationTest");
        assertThat(pi).isWaitingAt("Event1", "Event2");
        runtimeService().correlateMessage(MESSAGE_A);
    }
}