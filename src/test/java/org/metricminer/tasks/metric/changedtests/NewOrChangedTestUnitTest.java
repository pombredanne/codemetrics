package org.metricminer.tasks.metric.changedtests;

import static br.com.aniche.msr.tests.ParserTestUtils.classDeclaration;
import static br.com.aniche.msr.tests.ParserTestUtils.toInputStream;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class NewOrChangedTestUnitTest {

	private NewOrChangedTestUnit metric;

	@Before
	public void setUp() {
		metric = new NewOrChangedTestUnit();
	}
	
	@Test
	public void shouldDetectANewTest() {
		
		String diff = 
				"+ @Test\n" + 
				"+ public void someTest() {\n" +
				"+ bla();\n" +
				"+ }";
		
		
		metric.calculate(toInputStream(classDeclaration(diff.replace("+", ""))), diff);
		
		Set<String> tests = metric.newTests();
		assertTrue(tests.contains("someTest"));
	}

	@Test
	public void shouldDetectIfNotNewTest() {
		
		String diff = 
				"@Test\n" + 
				"public void someTest() {\n" +
				"bla();\n" +
				"+ ble();\n" +
				"}";
		
		
		metric.calculate(toInputStream(classDeclaration(diff.replace("+", ""))), diff);
		
		Set<String> tests = metric.newTests();
		assertFalse(tests.contains("someTest"));
	}
	
	@Test
	public void shouldDetectIfTestWasChanged() {
		
		String diff = 
				"+ bla();\n";
		
		String fullTestClass = "class Test {\n" +
				"@Test\n" +
				"public void someTest() {\n" +
				" bla();\n" +
				"}\n" +
				"}\n";

		metric.calculate(toInputStream(fullTestClass), diff);
		
		Set<String> newTests = metric.newTests();
		assertFalse(newTests.contains("someTest"));

		Set<String> modifiedTests = metric.modifiedTests();
		assertTrue(modifiedTests.contains("someTest"));
	}
	
	@Test
	public void shouldWorkProperlyWithMethodsWithoutAnnotation() {
		String diff = "+ // new\n";
		
		String fullTestClass = "class Test {\n" +
				"@Test\n" +
				"public void someTest() {\n" +
				"   bla();\n" +
				"}\n" +
				"public void someMethod() {\n" +
				"}\n" +
				"}\n";
		
		metric.calculate(toInputStream(fullTestClass), diff);
	}
}

