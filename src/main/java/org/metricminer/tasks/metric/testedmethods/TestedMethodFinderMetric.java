package org.metricminer.tasks.metric.testedmethods;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.metricminer.tasks.metric.common.ClassInfoVisitor;
import org.metricminer.tasks.metric.common.Metric;


public class TestedMethodFinderMetric implements Metric{

	private ClassInfoVisitor classInfo;
	private TestedMethodVisitor visitor;

	public void calculate(InputStream is) {
		try {
			CompilationUnit cunit = JavaParser.parse(is);
			
			classInfo = new ClassInfoVisitor();
			classInfo.visit(cunit, null);

			visitor = new TestedMethodVisitor(classInfo.getName());
			visitor.visit(cunit, null);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}		
	}

	public Map<String, Set<String>> getMethods() {
		return visitor.getInvokedMethods();
	}


    @Override
    public boolean matches(String name) {
        return name.endsWith("Test.java") || name.endsWith("Tests.java");
    }

}
