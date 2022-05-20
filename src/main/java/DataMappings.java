import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputParameter;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaOutputParameter;

import java.io.File;

@Slf4j
public class DataMappings {

  public static final String FILENAME = "./src/main/resources/datamappings.bpmn";

  // @see https://docs.camunda.org/manual/latest/user-guide/model-api/bpmn-model-api/fluent-builder-api/
  public static void main(String[] args) {

//        createModel();

    BpmnModelInstance modelInst;
    try {
      File file = new File(FILENAME);
      modelInst = Bpmn.readModelFromFile(file);

      log.info("Flow Elements - Name : Id : Type Name");
      modelInst.getModelElementsByType(FlowNode.class).forEach(e ->
      {
        log.info("{} : {} : {}", e.getName(), e.getId(), e.getElementType().getTypeName());
        log.info("Attribute completionQuantity: {}", e.getAttributeValue("completionQuantity"));
      });

      modelInst.getModelElementsByType(CamundaInputParameter.class).forEach(e ->
      {
        // may need to guard this more
        UserTask userTask = (UserTask) e.getParentElement().getParentElement().getParentElement();
        log.info("Parent {} has input {} with value {}", userTask.getId(), e.getCamundaName(), e.getTextContent());
      });

      modelInst.getModelElementsByType(CamundaOutputParameter.class).forEach(e ->
      {
        // may need to guard this more
        UserTask userTask = (UserTask) e.getParentElement().getParentElement().getParentElement();
        log.info("Parent {} has input {} with value {}", userTask.getId(), e.getCamundaName(), e.getTextContent());
      });

      Bpmn.writeModelToFile(file, modelInst);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void createModel() {
    BpmnModelInstance modelInst;
    try {
      File file = new File(FILENAME);
      modelInst = Bpmn.createProcess()
          .id("MyParentProcess")
          .executable()
          .startEvent("ProcessStarted")
          .userTask("UserTask1")
          .camundaInputParameter("myParam", "myValue")
          .camundaOutputParameter("myResult", "myResultValue")
          .endEvent("ParentEnded").done();


      log.info("Flow Elements - Name : Id : Type Name");
      modelInst.getModelElementsByType(FlowNode.class).forEach(e -> log.info("{} : {} : {}", e.getName(), e.getId(), e.getElementType().getTypeName()));

      Bpmn.writeModelToFile(file, modelInst);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
