import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.HitPolicy;
import org.camunda.bpm.model.dmn.impl.DmnModelConstants;
import org.camunda.bpm.model.dmn.instance.*;

import java.io.File;

@Slf4j
public class DMNDependency {

  public static void main(String[] args) throws Exception {

    DmnModelInstance modelInstance = initializeEmptyDmnModel();

    // Decision 1, DecisionTable 1, one input and one output
    Decision decision1 = modelInstance.newInstance(Decision.class);
    decision1.setId("Decision1");
    decision1.setName("Decision 1");
    modelInstance.getDefinitions().addChildElement(decision1);

    DecisionTable decisionTable1 = modelInstance.newInstance(DecisionTable.class);
    decisionTable1.setId("DecisionTable1");
    decisionTable1.setHitPolicy(HitPolicy.COLLECT);
    decision1.addChildElement(decisionTable1);

    Text text = modelInstance.newInstance(Text.class);
    InputExpression inputExpression = generateElement(modelInstance, InputExpression.class, "inputExp1");
    inputExpression.setText(text);
    inputExpression.setTypeRef("string");
    Input input1 = generateElement(modelInstance, Input.class, "Input1");
    input1.setLabel("Input 1");
    input1.setCamundaInputVariable("foo");
    input1.setInputExpression(inputExpression);

    Output output1 = generateElement(modelInstance, Output.class, "Output1");
    output1.setId("Output1");
    output1.setTypeRef("string");

    decisionTable1.addChildElement(input1);
    decisionTable1.addChildElement(output1);

    // Decision 2, DecisionTable 2, one input and one output
    Decision decision2 = modelInstance.newInstance(Decision.class);
    decision2.setId("Decision2");
    decision2.setName("Decision 2");
    modelInstance.getDefinitions().addChildElement(decision2);

    DecisionTable decisionTable2 = modelInstance.newInstance(DecisionTable.class);
    decisionTable2.setId("DecisionTable2");
    decisionTable2.setHitPolicy(HitPolicy.COLLECT);
    decision2.addChildElement(decisionTable2);

    Text text2 = modelInstance.newInstance(Text.class);
    InputExpression inputExpression2 = generateElement(modelInstance, InputExpression.class, "inputExp2");
    inputExpression2.setText(text2);
    inputExpression2.setTypeRef("string");
    Input input2 = generateElement(modelInstance, Input.class, "Input2");
    input2.setLabel("Input 1");
    input2.setCamundaInputVariable("foo");
    input2.setInputExpression(inputExpression2);

    Output output2 = generateElement(modelInstance, Output.class, "Output2");
    output2.setId("Output2");
    output2.setTypeRef("string");

    decisionTable2.addChildElement(input2);
    decisionTable2.addChildElement(output2);

    // Decision 2 is information requirement of Decision 1
    InformationRequirement infoRequirement = modelInstance.newInstance(InformationRequirement.class, "info1");
    infoRequirement.setRequiredDecision(decision2);
    decision1.getInformationRequirements().add(infoRequirement);


    Dmn.validateModel(modelInstance);
    Dmn.writeModelToFile(new File("src/main/resources/my.dmn"), modelInstance);

  }
  private static DmnModelInstance initializeEmptyDmnModel() {
    DmnModelInstance dmnModel = Dmn.createEmptyModel();
    Definitions definitions =
        generateNamedElement(dmnModel, "DRD", "definitions_xyz");
    definitions.setNamespace(DmnModelConstants.CAMUNDA_NS);
    definitions.setNamespace(DmnModelConstants.DMN13_ALTERNATIVE_NS);
    definitions.setNamespace(DmnModelConstants.DMN13_NS);
    dmnModel.setDefinitions(definitions);
    return dmnModel;
  }

  private static <E extends DmnElement> E generateElement(
      DmnModelInstance modelInstance, Class<E> elementClass, String id) {
    E element = modelInstance.newInstance(elementClass);
    element.setId(id);
    return element;
  }

  private static <E extends NamedElement> E generateNamedElement(
      DmnModelInstance modelInstance, String name, String id) {
    E element = generateElement(modelInstance, (Class<E>) Definitions.class, id);
    element.setName(name);
    return element;
  }
}
