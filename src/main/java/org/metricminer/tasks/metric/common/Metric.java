package org.metricminer.tasks.metric.common;

import java.io.InputStream;


public interface Metric {

    void calculate(InputStream is);

    boolean matches(String name);
    
}
