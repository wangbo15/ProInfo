package edu.pku.sei.proj;

import java.io.Serializable;

public abstract class MemoryCell implements Serializable  {
	protected ClassRepre cls;
	protected String name;
	protected String type;
	
	public MemoryCell(ClassRepre cls, String name, String type) {
		super();
		this.cls = cls;
		this.name = name;
		this.type = type;
	}
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
