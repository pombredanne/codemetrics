package org.metricminer.tasks.metric.unitorsystemtest;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.Map;

import org.metricminer.tasks.metric.common.ClassInfoVisitor;
import org.metricminer.tasks.metric.common.Metric;

public class UnitOrSystemTestFinder implements Metric {

	private UnitOrSystemTestVisitor visitor;
	private Map<String, TestInfo> tests;
	private ClassInfoVisitor classInfo;

	@Override
	public void calculate(InputStream is) {
		try {
			CompilationUnit cunit = JavaParser.parse(is);
			
			classInfo = new ClassInfoVisitor();
			classInfo.visit(cunit, null);
			
			visitor = new UnitOrSystemTestVisitor(classInfo.getName());
			visitor.visit(cunit, null);
			
			tests = visitor.getTests();
			
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }		
	}

	@Override
	public boolean matches(String name) {
		return name.endsWith("Test.java") || name.endsWith("Tests.java");
	}

	public Map<String, TestInfo> getTests() {
		return tests;
	}

}
