package org.metricminer.tasks.metric.invocation;

import static br.com.aniche.msr.tests.ParserTestUtils.classDeclaration;
import static br.com.aniche.msr.tests.ParserTestUtils.toInputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class MethodsInvocationMetricTests {

	private MethodsInvocationMetric metric;

	@Before
	public void setUp() {
		this.metric = new MethodsInvocationMetric();
	}

	@Test
	public void shouldCountMethodsInvocationPerMethod() {
		metric.calculate(
				toInputStream(
						classDeclaration(
								"public void method() {"+
								"a();"+
								"b();"+
								"}"
								)));
	
		assertEquals(2, metric.getInvocations().get("method/0").size());
	}
	
	@Test
	public void shouldCountMethodsInvocationPerMethodRegardlessOfVisibility() {
		metric.calculate(
				toInputStream(
						classDeclaration(
								"public void method1() {"+
								"a();"+
								"b();"+
								"}"+
								"private void method2() {"+
								"a();"+
								"b();"+
								"}"+
								"protected void method3() {"+
								"a();"+
								"b();"+
								"}"+
								"void method4() {"+
								"a();"+
								"b();"+
								"}"
								)));
	
		assertEquals(2, metric.getInvocations().get("method1/0").size());
		assertEquals(2, metric.getInvocations().get("method2/0").size());
		assertEquals(2, metric.getInvocations().get("method3/0").size());
		assertEquals(2, metric.getInvocations().get("method4/0").size());
	}
	
	@Test
	public void shouldNotCountRepeatedInvocations() {
		metric.calculate(
				toInputStream(
						classDeclaration(
								"public void method() {"+
								"x++;"+
								"a();"+
								"a();"+
								"}"
								)));
	
		assertEquals(1, metric.getInvocations().get("method/0").size());
	}
	
	@Test
	public void shouldIgnoreMethodInvokedOutsideOfAMethod() {
		metric.calculate(
				toInputStream(
						classDeclaration(
								"private int x = someMethod();"+
								"public void method() {"+
								"x++;"+
								"a();"+
								"}"
								)));
		
		assertEquals(1, metric.getInvocations().get("method/0").size());
	}


	@Test
	public void shouldNotGetSequencedInvocations() {
		metric.calculate(
				toInputStream(
				classDeclaration(
				"@Test"+
				"public void shouldInvokeR() throws Exception {"+
				"	StatisticalTest test = new StatisticalTest(\"wilcoxon\", wilcoxon(), new User());"+
				"	QueryResult q1 = new QueryResult(path + \"/q1\", null);"+
				"	QueryResult q2 = new QueryResult(path + \"/q2\", null);"+
				"	"+
				"	StatisticalTestResult result = r.execute(test, q1, q2, null, \"test\");"+
				"	"+
				"	assertTrue(result.getOutput().contains(\"Wilcoxon signed rank test\"));"+
				"	assertTrue(result.getOutput().contains(\"p-value = 0.25\"));"+
				"}"
				)));

		Set<String> invokedMethods = new HashSet<String>();
		for(MethodInvocation mi : metric.getAllInvocations()) {
			invokedMethods.add(mi.getInvokedMethod());
		}
		assertTrue(invokedMethods.contains("getOutput/0"));
		assertFalse(invokedMethods.contains("contains/1"));
	}
	
	@Test
	public void shouldIdentifyTypeOfTheObjectThatAMethodHasBeenInvoked() {
		metric.calculate(
				toInputStream(
					"package a.b;\n"+
					"import a.b.c.AnotherClass;\n"+
					"class Bla {\n"+
					"public void method() {"+
					"AnotherClass a = new AnotherClass();"+
					"a.doSomething();"+
					"}"+
					"}"
				));
		
		MethodInvocation mi = metric.getAllInvocations().iterator().next();
		assertEquals("AnotherClass", mi.getInvokedClassName());
		assertEquals("a.b.c", mi.getInvokedPackage());
		assertEquals("doSomething/0", mi.getInvokedMethod());
		assertEquals("Bla", mi.getOriginalClass());
		assertEquals("method/0", mi.getOriginalMethod());
	}
	@Test
	public void shouldIdentifyTypeOfTheObjectThatAMethodHasBeenInvokedInline() {
		metric.calculate(
				toInputStream(
						"package a.b;\n"+
						"import a.b.c.AnotherClass;\n"+
						"class Bla {\n"+
						"public void method() {"+
						"new AnotherClass().doSomething();"+
						"}"+
						"}"
				));
		
		MethodInvocation mi = metric.getAllInvocations().iterator().next();
		assertEquals("AnotherClass", mi.getInvokedClassName());
		assertEquals("a.b.c", mi.getInvokedPackage());
		assertEquals("doSomething/0", mi.getInvokedMethod());
		assertEquals("Bla", mi.getOriginalClass());
		assertEquals("method/0", mi.getOriginalMethod());
	}
	
	@Test
	public void shouldIdentifyTypeOfTheObjectThatAMethodHasBeenDeclaredAsAttribute() {
		metric.calculate(
				toInputStream(
						"package a.b;\n"+
						"import a.b.c.AnotherClass;\n"+
						"class Bla {\n"+
						"private AnotherClass c;"+
						"public void method() {"+
						"c.doSomething();"+
						"}"+
						"}"
				));
		
		MethodInvocation mi = metric.getAllInvocations().iterator().next();
		assertEquals("AnotherClass", mi.getInvokedClassName());
		assertEquals("a.b.c", mi.getInvokedPackage());
		assertEquals("doSomething/0", mi.getInvokedMethod());
		assertEquals("Bla", mi.getOriginalClass());
		assertEquals("method/0", mi.getOriginalMethod());
	}
	
}
