package org.metricminer.tasks.metric.methods;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.List;

import org.metricminer.tasks.metric.common.Method;
import org.metricminer.tasks.metric.common.MethodsAndAttributesVisitor;
import org.metricminer.tasks.metric.common.Metric;


public class MethodsCountMetric implements Metric {

	private MethodsAndAttributesVisitor visitor;

	public void calculate(InputStream is) {
		try {
			CompilationUnit cunit = JavaParser.parse(is);
			
			visitor = new MethodsAndAttributesVisitor();
			visitor.visit(cunit, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}


	public String getName() {
		return visitor.getName();
	}

	public List<Method> getMethods() {
		return visitor.getMethods();
	}

	public List<String> getAttributes() {
		return visitor.getAttributes();
	}

	public List<Method> getPublicMethods() {
		return visitor.getPublicMethods();
	}

	public List<Method> getPrivateMethods() {
		return visitor.getPrivateMethods();
	}

	public List<Method> getProtectedMethods() {
		return visitor.getProtectedMethods();
	}

	public List<Method> getDefaultMethods() {
		return visitor.getDefaultMethods();
	}

	public List<Method> getConstructorMethods() {
		return visitor.getConstructorMethods();
	}

	public List<String> getPublicAttributes() {
		return visitor.getPublicAttributes();
	}

	public List<String> getPrivateAttributes() {
		return visitor.getPrivateAttributes();
	}

	public List<String> getProtectedAttributes() {
		return visitor.getProtectedAttributes();
	}

	public List<String> getDefaultAttributes() {
		return visitor.getDefaultAttributes();
	}

    @Override
    public boolean matches(String name) {
        return name.endsWith(".java");
    }

}
