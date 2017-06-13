package edu.pku.sei.proj;

public class LocalRepre extends MemoryCell{
	private MethodRepre mtd;
	private int flag;
	public LocalRepre(ClassRepre cls, String name, String type, MethodRepre mtd, int flag) {
		super(cls, name, type);
		this.mtd = mtd;
		this.flag = flag;
	}
	
	public MethodRepre getMtd() {
		return mtd;
	}

	public void setMtd(MethodRepre mtd) {
		this.mtd = mtd;
	}

	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	
}
