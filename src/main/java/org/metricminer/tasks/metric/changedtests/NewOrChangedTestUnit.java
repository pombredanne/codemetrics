package org.metricminer.tasks.metric.changedtests;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.metricminer.tasks.metric.common.InputStreamUtils;
import org.metricminer.tasks.metric.common.MetricThatNeedsDiff;

// TODO: Understand removed lines as a changed test
public class NewOrChangedTestUnit implements MetricThatNeedsDiff {

	private TestFinderVisitor visitor;
	private Set<String> newTests;
	private Set<String> modifiedTests;

	public NewOrChangedTestUnit() {
		this.newTests = new HashSet<String>();
		this.modifiedTests = new HashSet<String>();
	}
	
	@Override
	public void calculate(InputStream is, String diff) {
		
		String source = InputStreamUtils.toString(is);
		
		try {
			CompilationUnit cunit = JavaParser.parse(InputStreamUtils.toInputStream(source));
			
			visitor = new TestFinderVisitor();
			visitor.visit(cunit, null);
			
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
		
		checkForAddedTests(diff);
		checkForModifiedTests(diff, source);
		
	}

	private void checkForAddedTests(String diff) {
		for(String testName : visitor.getTests()) {
			if(wasAdded(diff, testName)) newTests.add(testName);
		}
	}

	private void checkForModifiedTests(String diff, String source) {
		for(String diffLine : diff.replace("\r", "").split("\n")) {
			if(newLine(diffLine)) {
				String lineWithoutThePlus = diffLine.substring(1, diffLine.length());
				int number = lineNumberInSource(lineWithoutThePlus, source);
				if(number > -1) {
					String test = testMethodWithLineIn(number);
					if(test!=null) modifiedTests.add(test);
				}
			}
		}
	}

	private String testMethodWithLineIn(int number) {
		for(Map.Entry<String, TestFinderVisitor.StartAndEnd> entry : visitor.getTestAttributes().entrySet()) {
			if(number >= entry.getValue().getStart() && number <= entry.getValue().getEnd()) {
				return entry.getKey();
			}
		}
		
		return null;
	}
	
	private int lineNumberInSource(String originalLine, String source) {
		int lineNumber = 1;
		for(String line : source.replace("\r", "").split("\n")) {
			if(line.contains(originalLine.trim())) {
				return lineNumber;
			}
			lineNumber++;
		}

		return -1;
	}

	private boolean wasAdded(String diff, String testName) {
		for(String lines : diff.replace("\r", "").split("\n")) {
			if(newLine(lines) && 
					lines.contains("public") && 
					lines.contains("void") && 
					lines.contains(testName)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean newLine(String lines) {
		return lines.startsWith("+");
	}
	@Override
	public boolean matches(String name) {
		return name.endsWith("Test.java") || name.endsWith("Tests.java");
	}

	public Set<String> newTests() {
		return newTests;
	}
	public Set<String> modifiedTests() {
		return modifiedTests;
	}

}
