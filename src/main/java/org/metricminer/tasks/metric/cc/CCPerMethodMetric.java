package org.metricminer.tasks.metric.cc;

import java.io.InputStream;

import org.metricminer.antlr4.java.AntLRVisitor;
import org.metricminer.tasks.metric.common.Metric;


public class CCPerMethodMetric implements Metric {

    private CCListener visitor;

    public void calculate(InputStream is) {
        
		try {
			visitor = new CCListener();
	        new AntLRVisitor().visit(visitor, is);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean matches(String name) {
        return name.endsWith(".java");
    }


}
