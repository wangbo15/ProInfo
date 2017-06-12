package edu.pku.sei.proj;

import java.util.List;

public class MethodRepre {
	private ClassRepre cls;
	private String name;
	private String returnType;
	private List<LocalRepre> params;
	
	public ClassRepre getCls() {
		return cls;
	}
	public void setCls(ClassRepre cls) {
		this.cls = cls;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public List<LocalRepre> getParams() {
		return params;
	}
	public void setParams(List<LocalRepre> params) {
		this.params = params;
	}
	
	
}
