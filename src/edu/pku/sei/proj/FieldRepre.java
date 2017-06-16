package edu.pku.sei.proj;

import java.lang.reflect.Modifier;

public class FieldRepre extends MemoryCell{
	
	private int flag;

	public FieldRepre(ClassRepre cls, String name, String type, int flag) {
		super(cls, name, type);
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "FiledRepre [name=" + name + ", type=" + type + "]";
	}
	
	
	public boolean isInherable(){
		return Modifier.isPublic(this.flag) || Modifier.isProtected(this.flag);
	}
		
}
