package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

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
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
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
		
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator;
		
		// Switch
		System.out.println("public class RunStatechart {\r\n" + 
				"	\r\n" + 
				"	public static void main(String[] args) throws IOException {\r\n" + 
				"		ExampleStatemachine s = new ExampleStatemachine();\r\n" + 
				"		s.setTimer(new TimerService());\r\n" + 
				"		RuntimeService.getInstance().registerStatemachine(s, 200);\r\n" + 
				"		s.init();\r\n" + 
				"		s.enter();\r\n" + 
				"		\r\n" + 
				"		String line;\r\n" + 
				"		Scanner console = new Scanner(System.in);\r\n" + 
				"		while ( !((line = console.nextLine()).equals(\"exit\")) ) {\r\n" + 
				"			switch (line) {");
		
		iterator = s.eAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if (content instanceof EventDefinition) {
				EventDefinition event = (EventDefinition) content;
				if (event.getDirection() == Direction.IN) {
					String name = event.getName().substring(0, 1).toUpperCase() + event.getName().substring(1);
					System.out.println("				case \""+ event.getName() +"\":\r\n" + 
							"					s.raise" + name +"();\r\n" + 
							"					break;");
				}
			}
		}
		
		System.out.println("			}\r\n" + 
				"			s.runCycle();\r\n" + 
				"			print(s);\r\n" + 
				"		}\r\n" + 
				"		console.close();\r\n" + 
				"		\r\n" + 
				"		System.exit(0);\r\n" + 
				"	}\r\n");
		
		// Print
		System.out.println("	public static void print(IExampleStatemachine s) {");
		
		iterator = s.eAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if (content instanceof VariableDefinition) {
				VariableDefinition variable = (VariableDefinition) content;
				String name = variable.getName().substring(0, 1).toUpperCase() + variable.getName().substring(1);
				System.out.println("		System.out.println(\"" + name.charAt(0) + " = \" + s.getSCInterface().get" + name + "());");
			}
		}
		
		System.out.println("	}\r\n" + 
				"}\r\n" + 
				"");
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
