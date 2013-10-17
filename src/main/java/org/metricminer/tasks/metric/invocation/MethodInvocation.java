package org.metricminer.tasks.metric.invocation;

public class MethodInvocation {

	
	private String originalMethod;
	private String invokedPackage;
	private String invokedClassName;
	private String invokedMethod;
	private String originalClass;

	public MethodInvocation(String originalClass, String originalMethod, String invokedPackage, 
			String invokedClassName,
			String invokedMethod) {
				this.originalClass = originalClass;
				this.originalMethod = originalMethod;
				this.invokedPackage = invokedPackage;
				this.invokedClassName = invokedClassName;
				this.invokedMethod = invokedMethod;
	}

	public String getOriginalMethod() {
		return originalMethod;
	}

	public String getInvokedClassName() {
		return invokedClassName;
	}

	public String getInvokedMethod() {
		return invokedMethod;
	}
	
	public String getInvokedPackage() {
		return invokedPackage;
	}

	public String getOriginalClass() {
		return originalClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((invokedClassName == null) ? 0 : invokedClassName
						.hashCode());
		result = prime
				* result
				+ ((invokedMethod == null) ? 0 : invokedMethod.hashCode());
		result = prime
				* result
				+ ((invokedPackage == null) ? 0 : invokedPackage
						.hashCode());
		result = prime * result
				+ ((originalClass == null) ? 0 : originalClass.hashCode());
		result = prime * result
				+ ((originalMethod == null) ? 0 : originalMethod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodInvocation other = (MethodInvocation) obj;
		if (invokedClassName == null) {
			if (other.invokedClassName != null)
				return false;
		} else if (!invokedClassName.equals(other.invokedClassName))
			return false;
		if (invokedMethod == null) {
			if (other.invokedMethod != null)
				return false;
		} else if (!invokedMethod.equals(other.invokedMethod))
			return false;
		if (invokedPackage == null) {
			if (other.invokedPackage != null)
				return false;
		} else if (!invokedPackage.equals(other.invokedPackage))
			return false;
		if (originalClass == null) {
			if (other.originalClass != null)
				return false;
		} else if (!originalClass.equals(other.originalClass))
			return false;
		if (originalMethod == null) {
			if (other.originalMethod != null)
				return false;
		} else if (!originalMethod.equals(other.originalMethod))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MethodInvocation [originalMethod=" + originalMethod
				+ ", invokedPackage=" + invokedPackage + ", invokedClassName="
				+ invokedClassName + ", invokedMethod=" + invokedMethod
				+ ", originalClass=" + originalClass + "]";
	}
	

	
	
	
}
