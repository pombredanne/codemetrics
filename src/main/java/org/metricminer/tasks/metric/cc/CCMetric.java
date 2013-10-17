package org.metricminer.tasks.metric.cc;

import java.io.InputStream;

import org.metricminer.antlr4.java.AntLRVisitor;
import org.metricminer.tasks.metric.common.Metric;

public class CCMetric implements Metric {

    private CCListener visitor;

    public void calculate(InputStream is) {
		try {
			visitor = new CCListener();
	        new AntLRVisitor().visit(visitor, is);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public double avgCc() {
        double avgCc = visitor.getAvgCc();
        if (Double.isNaN(avgCc))
            avgCc = -1.0;
        return avgCc;
    }

    public int cc() {
        return visitor.getCc();
    }

    public int cc(String method) {
        return visitor.getCc(method);
    }

    @Override
    public boolean matches(String name) {
        return name.endsWith(".java");
    }

}
