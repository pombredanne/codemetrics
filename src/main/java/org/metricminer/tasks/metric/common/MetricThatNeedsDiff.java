package org.metricminer.tasks.metric.common;

import java.io.InputStream;


public interface MetricThatNeedsDiff {

    void calculate(InputStream is, String diff);

    boolean matches(String name);
    
}
