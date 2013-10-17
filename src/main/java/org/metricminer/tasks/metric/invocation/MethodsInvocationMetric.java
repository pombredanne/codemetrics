package org.metricminer.tasks.metric.invocation;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.metricminer.tasks.metric.common.ClassInfoVisitor;
import org.metricminer.tasks.metric.common.Metric;


public class MethodsInvocationMetric implements Metric{

	private MethodInvocationVisitor visitor;
	private ClassInfoVisitor info;
	
	public void calculate(InputStream is) {
		try {
			CompilationUnit cunit = JavaParser.parse(is);
			
			info = new ClassInfoVisitor();
			info.visit(cunit, null);
			
			visitor = new MethodInvocationVisitor();
			visitor.visit(cunit, null);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
    public Set<MethodInvocation> getAllInvocations() {
    	return visitor.getInvocations();
    }
    
    public Map<String,Set<MethodInvocation>> getInvocations() {
    	
    	Map<String, Set<MethodInvocation>> results = new HashMap<String, Set<MethodInvocation>>();
    	
    	for(MethodInvocation mi : visitor.getInvocations()) {
    		if(!results.containsKey(mi.getOriginalMethod())) {
    			results.put(mi.getOriginalMethod(), new HashSet<MethodInvocation>());
    		}
    		results.get(mi.getOriginalMethod()).add(mi);
    	}
    	
    	return results;
    }

    @Override
    public boolean matches(String name) {
        return name.endsWith(".java");
    }

}
