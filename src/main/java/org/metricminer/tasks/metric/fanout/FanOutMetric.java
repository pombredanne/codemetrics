package org.metricminer.tasks.metric.fanout;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;

import org.metricminer.tasks.metric.common.ClassInfoVisitor;
import org.metricminer.tasks.metric.common.Metric;


public class FanOutMetric implements Metric {

	private FanOutVisitor visitor;
    private ClassInfoVisitor classInfo;


	public void calculate(InputStream is) {
		try {
			CompilationUnit cunit = JavaParser.parse(is);
			
			classInfo = new ClassInfoVisitor();
			classInfo.visit(cunit, null);
			
			visitor = new FanOutVisitor(classInfo.getName());
			visitor.visit(cunit, null);
			
			
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
	}

	public int fanOut() {
		return visitor.typesQty();
	}

    @Override
    public boolean matches(String name) {
        return name.endsWith(".java");
    }

    
}
