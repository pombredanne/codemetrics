package org.metricminer.tasks.metric.lines;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.Map;

import org.metricminer.tasks.metric.common.MethodsAndAttributesVisitor;
import org.metricminer.tasks.metric.common.Metric;


public class LinesOfCodeMetric implements Metric {

	private LinesOfCodeVisitor visitor;
	private String clazzName;

	public void calculate(InputStream is) {
		try {
			CompilationUnit cunit = JavaParser.parse(is);
			
			MethodsAndAttributesVisitor nameVisitor = new MethodsAndAttributesVisitor();
			nameVisitor.visit(cunit, null);
			
			clazzName = nameVisitor.getName();
			
			visitor = new LinesOfCodeVisitor();
			visitor.visit(cunit, null);
			
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Map<String, Integer> getMethodLines() {
		return visitor.methodLines();
	}

    @Override
    public boolean matches(String name) {
        return name.endsWith(".java");
    }

}
