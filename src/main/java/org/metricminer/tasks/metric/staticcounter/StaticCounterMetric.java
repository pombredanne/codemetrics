package org.metricminer.tasks.metric.staticcounter;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.Set;

import org.metricminer.tasks.metric.common.Metric;

public class StaticCounterMetric implements Metric {

	
	private Set<String> attributesName;
	private int attributesCounter;
	
	private Set<String> methodsName;
	private int methodsCounter;

	@Override
	public void calculate(InputStream is) {
		try {
			
			CompilationUnit cunit = JavaParser.parse(is);

			StaticCounterMetricVisitor nameVisitor = new StaticCounterMetricVisitor();
			nameVisitor.visit(cunit, null);
			
			attributesName = nameVisitor.attributesName;
			attributesCounter = nameVisitor.attributesCounter;
			
			methodsCounter = nameVisitor.methodsCounter;
			methodsName = nameVisitor.methodsName;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean matches(String name) {
		return name.endsWith(".java");
	}


	public int getNumberOfStaticAttributes() {
		return attributesCounter;
	}

	public Set<String> getStaticAttributesName() {
		return attributesName;
	}

	public int getNumberOfStaticMethods() {
		return methodsCounter;
	}

	public Set<String> getStaticMethodsName() {
		return methodsName;
	}

}