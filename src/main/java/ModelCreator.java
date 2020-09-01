import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.UserTask;

import java.io.File;

@Slf4j
public class ModelCreator {

    // @see https://docs.camunda.org/manual/latest/user-guide/model-api/bpmn-model-api/fluent-builder-api/#generation-of-diagram-interchange
    public static void main(String[] args) {

        BpmnModelInstance modelInst;
        try {
        // File file = new File(ModelModifier.class.getClassLoader().getResource("process1.bpmn").toURI());
            File file = new File("./src/main/resources/process1.bpmn");
        //  modelInst = Bpmn.readModelFromFile(file);
            modelInst = Bpmn.createProcess()
                    .name("Twitter QA")
                    .executable()
                    .startEvent()
                    .userTask().id("ApproveTweet").name("Approve Tweet")
                    .exclusiveGateway().id("isApproved").name("Approved?")
                    .condition("approved", "#{approved}")
                    .serviceTask().id("sendTweet").name("Send tweet")
                    .endEvent().name("Tweet sent")
                    .moveToLastGateway()
                    // done();
                    // Gateway gateway = modelInst.getModelElementById("isApproved");
                    // gateway.builder()
                    .condition("Not approved", "#{!approved}")
                    .serviceTask().name("Send Rejection")
                    .endEvent().name("Tweet rejected").done();

            log.info("Flow Elements - Name : Id : Type Name");
            modelInst.getModelElementsByType(UserTask.class).forEach(e -> log.info("{} : {} : {}", e.getName(), e.getId(), e.getElementType().getTypeName()));

            Bpmn.writeModelToFile(file, modelInst);
//        file.createNewFile("/tmp/testDiagram2.bpmn")

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
