package hu.bme.mit.yakindu.analysis.workhere;

import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.base.types.Direction;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		int nameless_state_number = 0;
		System.out.println("public static void print(IExampleStatemachine s) {");
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if (content instanceof VariableDefinition) {
				VariableDefinition variable = (VariableDefinition) content;
				String name = variable.getName().substring(0, 1).toUpperCase() + variable.getName().substring(1);
				System.out.println("\tSystem.out.println(\"" + name.charAt(0) + " = \" + s.getSCInterface().get" + name + "());");
			} /*else if (content instanceof EventDefinition) {
				EventDefinition event = (EventDefinition) content;
				if (event.getDirection() == Direction.IN) 
					;
			}*/
		}
		System.out.println("}");
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
