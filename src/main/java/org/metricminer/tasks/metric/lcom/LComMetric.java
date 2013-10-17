package org.metricminer.tasks.metric.lcom;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.metricminer.tasks.metric.common.Method;
import org.metricminer.tasks.metric.common.MethodsAndAttributesVisitor;
import org.metricminer.tasks.metric.common.Metric;


/**
 * Calculates LCOM
 * Formula: Formula in http://codenforcer.com/lcom.aspx
 * 
 * @author mauricioaniche
 *
 */
public class LComMetric implements Metric {

	private List<Method> methods;
	private List<String> attributes;
	private Map<String, Set<String>> methodsPerAttribute;
	private boolean isInterface;
	private String name;

	public void calculate(InputStream is) {
		try {
			CompilationUnit cunit = JavaParser.parse(is);
			
			MethodsAndAttributesVisitor firstVisitor = new MethodsAndAttributesVisitor();
			firstVisitor.visit(cunit, null);
			
			isInterface = firstVisitor.isInterface();
			name = firstVisitor.getName();
			
			attributes = firstVisitor.getAttributes();
			methods = firstVisitor.getMethods();
			
			MethodsPerAttributeVisitor secondVisitor = new MethodsPerAttributeVisitor(attributes);
			secondVisitor.visit(cunit, null);
			
			methodsPerAttribute = secondVisitor.getMethodsPerAttribute();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	public double lcomHS() {
		if(isInterface) return -1;
		return (1 / (m() - 1)) * (m() - (1/f())*sumMf());
	}

	public double lcom() {
		if(isInterface) return -1;
		double result = 1 - 1/(m()*f())*sumMf();		
		return Double.isNaN(result) ? 1 : result;
	}
	
	private double sumMf() {
		double total = 0;
		for (Entry<String, Set<String>> methods : methodsPerAttribute.entrySet()) {
			total += methods.getValue().size();
		}
		return total;
	}

	private double f() {
		return attributes.size();
	}

	private double m() {
		return methods.size();
	}

	public String getName() {
		return name;
	}

    @Override
    public boolean matches(String name) {
        return name.endsWith(".java");
    }

	
}
