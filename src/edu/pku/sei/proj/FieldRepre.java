package edu.pku.sei.proj;

import java.lang.reflect.Modifier;

public class FieldRepre extends MemoryCell{
	
	private static final long serialVersionUID = 1888940838152654379L;
	private int flag;

	private String ininalValue;
	
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

	public void setIninalValue(String ininalValue) {
		this.ininalValue = ininalValue;
	}

	@Override
	public String toString() {
		return "FiledRepre [name=" + name + ", type=" + type + "]";
	}
	
	
	public boolean isInherable(){
		return Modifier.isPublic(this.flag) || Modifier.isProtected(this.flag);
	}
	
	public boolean isConstant(){
		return Modifier.isStatic(this.flag) && Modifier.isFinal(this.flag);
	}
	
	public String getInitStr(){
		boolean isPrimitive;
		switch (this.type) {
			case "int":
			case "char":
			case "short":
			case "long":
			case "float":
			case "double":
			case "boolean":
			case "byte":
			case "Integer":
			case "Long":
			case "Double":
			case "Float":
			case "Character":
			case "Short":
			case "Boolean":
			case "Byte":
				isPrimitive = true;
				break;
			default:
				isPrimitive = false;
			}
		if(isPrimitive){
			return this.ininalValue;
		}
		return null;
	}
}
