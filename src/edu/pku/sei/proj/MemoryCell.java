package edu.pku.sei.proj;

public abstract class MemoryCell {
	protected ClassRepre cls;
	protected String name;
	protected String type;
	
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
