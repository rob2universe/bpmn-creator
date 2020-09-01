import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.CancelEventDefinition;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.io.File;

@Slf4j
public class TransactionSubprocess {

    public static final String CANCEL_END_EVENT = "CancelEndEvent";

    // @see https://docs.camunda.org/manual/latest/user-guide/model-api/bpmn-model-api/fluent-builder-api/
    public static void main(String[] args) {

        BpmnModelInstance modelInst;
        try {
//        File file = new File(ModelModifier.class.getClassLoader().getResource("testDiagram.bpmn").toURI());
            File file = new File("./src/main/resources/transaction.bpmn");
//            modelInst = Bpmn.readModelFromFile(file);
            modelInst = Bpmn.createProcess()
                    .id("MyTransactionProcess")
                    .executable()
                    .startEvent("ProcessStarted")
                    .transaction().id("myTXProcess").name("My TX Process")
                    .embeddedSubProcess()
                    .startEvent("TXRequired")
                    .userTask("AUserTask")
                    .endEvent(CANCEL_END_EVENT)
                    // Most event types are already supported by fluent API, such as:
                    // .compensateEventDefinition("ACompensationEvent")
                    .transactionDone()// or .subProcessDone()
                    .endEvent("ParentEnded").done();

            // Add event definition without fluent API
            ModelElementInstance element = modelInst.getModelElementById(CANCEL_END_EVENT);
            CancelEventDefinition eventDefinition = modelInst.newInstance(CancelEventDefinition.class);
            element.addChildElement(eventDefinition);

            log.info("Flow Elements - Name : Id : Type Name");
            modelInst.getModelElementsByType(SubProcess.class).forEach(e -> log.info("{} : {} : {}", e.getName(), e.getId(), e.getElementType().getTypeName()));

            Bpmn.writeModelToFile(file, modelInst);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
