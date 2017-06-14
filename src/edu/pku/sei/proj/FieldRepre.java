package edu.pku.sei.proj;

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
		
}
