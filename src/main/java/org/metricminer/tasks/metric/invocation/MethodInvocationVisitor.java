package org.metricminer.tasks.metric.invocation;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.metricminer.tasks.metric.common.ClassInfoVisitor;
import org.metricminer.tasks.metric.common.FullMethodName;

public class MethodInvocationVisitor extends ClassInfoVisitor {

	private Stack<String> currentMethod;
	private String referencedType;
	private Map<String, String> methodScopedVariables;
	private Map<String, String> attributeScopedVariables;
	private Set<MethodInvocation> invocations;
	
	private Map<String, String> imports;
	
	private String assignTarget;

	public MethodInvocationVisitor() {
		this.attributeScopedVariables = new HashMap<String, String>();
		currentMethod = new Stack<String>();
		invocations = new HashSet<MethodInvocation>();
		imports = new HashMap<String, String>();
	}
	
	public void visit(ImportDeclaration n, Object arg) {
		
		String importLine = n.getName().toString();
		String[] names = importLine.split("\\.");
		
		String className = names[names.length-1];
		imports.put(className, importLine.replace("." + className, ""));
    }

	
	public void visit(FieldDeclaration expr, Object arg) {
		for(VariableDeclarator v : expr.getVariables()) {
			attributeScopedVariables.put(v.getId().getName(), expr.getType().toString());
		}
	}

	public void visit(MethodDeclaration expr, Object arg) {
			String methodName = FullMethodName.fullMethodName(expr.getName(), expr.getParameters());
			currentMethod.push(methodName);
			methodScopedVariables = new HashMap<String, String>();
			
			super.visit(expr, arg);
			currentMethod.pop();
	}
	
	public void visit(ObjectCreationExpr expr, Object arg) {
		String type = expr.getType().getName();
		
		if(assignTarget!=null && !assignTarget.isEmpty()) {
			changeType(assignTarget, type, methodScopedVariables);
			changeType(assignTarget, type, attributeScopedVariables);
			assignTarget = null;
		}
		
		super.visit(expr, arg);
	}
	
	public void visit(AssignExpr expr, Object arg) {
		assignTarget = expr.getTarget().toString();
		super.visit(expr, arg);
	}

	private void changeType(String target, String type, Map<String, String> methods) {
		if(methods==null) return;
		if(methods.containsKey(target)) {
			methods.remove(target);
			methods.put(target, type);
		}
		
	}

	public void visit(ReferenceType expr, Object arg) {
		referencedType = expr.toString();
	}

	
	public void visit(PrimitiveType expr, Object arg) {
		referencedType = expr.toString();
	}
	
	public void visit(VariableDeclarator expr, Object arg) {
		String lastReferencedType = referencedType;
		super.visit(expr, arg);
		if(isCollection(lastReferencedType)) lastReferencedType = referencedType;
		
		if(methodScopedVariables!=null) methodScopedVariables.put(expr.getId().toString(), lastReferencedType);
	}
	
	private boolean isCollection(String type) {
		return type.contains("List<") || type.contains("Set<") || type.contains("Map<") || type.contains("Queue<") || type.contains("Stack<"); 
	}

	public void visit(ForStmt n, Object arg) {
		if (n.getInit() != null) {
            for (Expression e : n.getInit()) {
                e.accept(this, arg);
            }
        }
	}

	
	public void visit(MethodCallExpr expr, Object arg) {
		if(!currentMethod.isEmpty()) {
			
			ScopeFinder scopeFinder = new ScopeFinder();

			String methodName = FullMethodName.fullMethodNameByArgs(expr.getName(), expr.getArgs());
			
			if(expr.getScope() == null || expr.getScope().toString().startsWith("this")) {
				addInvocationToMyself(methodName);
			}
			if(expr.getScope() !=null && !expr.getScope().toString().contains(".")) { 
				expr.getScope().accept(scopeFinder, null);
				
				String variable = scopeFinder.getScope();
				addInvocation(variable, methodName);
			} 
		}
		
		super.visit(expr, arg);
	}

	private void addInvocationToMyself(String methodName) {
		invocations.add(new MethodInvocation(this.getName(), currentMethod.peek(), getPackageName(), getName(), methodName));
	}

	private void addInvocation(String variable, String methodName) {
		
		String className;
		className = methodScopedVariables.get(variable);
		if(className == null) className = attributeScopedVariables.get(variable);
		if(className == null && firstIsCapitalLetter(variable)) className = variable;
		if(className == null) return;
		
		invocations.add(new MethodInvocation(this.getName(), currentMethod.peek(), packageFor(className), className, methodName));
		
	}

	private boolean firstIsCapitalLetter(String variable) {
		if(variable == null || variable.isEmpty()) return false;
			
		String firstLetter = variable.substring(0,1);
		String firstLetterInCapital = variable.substring(0,1).toUpperCase();
		
		return firstLetter.equals(firstLetterInCapital);
	}

	private String packageFor(String className) {
		String packageName = imports.get(className);
		if(packageName == null) packageName = getPackageName();
		
		return packageName;
	}

	public Set<MethodInvocation> getInvocations() {
		return invocations;
	}
}

class ScopeFinder extends VoidVisitorAdapter<Object> {
	private String scope;

	public void visit(NameExpr expr, Object arg) {
		scope = expr.getName();
	}
	
	public void visit(ObjectCreationExpr expr, Object arg) {
		scope = expr.getType().getName(); // consider it as static invocation
	}
	
	public String getScope() {
		return scope;
	}
}
