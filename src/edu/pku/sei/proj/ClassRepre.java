package edu.pku.sei.proj;

import java.io.File;
import java.util.List;

public class ClassRepre {
	private File srcFile;
	
	private PackageRepre pkg;
	private String name;
	
	private ClassRepre fatherCls;
	private List<ClassRepre> superInterfaces;
	
	private boolean isInterface;

	private int flag;
	
	private List<FiledRepre> fields;
	private List<MethodRepre> methods;
	
	
	private List<ClassRepre> innerClazzes;
	
	private ClassRepre(File srcFile, String clsName) {
		super();
		this.srcFile = srcFile;
		this.name = clsName;
	}
	
	public File getSrcFile() {
		return srcFile;
	}
	public void setSrcFile(File srcFile) {
		this.srcFile = srcFile;
	}
	public PackageRepre getPkg() {
		return pkg;
	}
	public void setPkg(PackageRepre pkg) {
		this.pkg = pkg;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ClassRepre getFatherCls() {
		return fatherCls;
	}
	public void setFatherCls(ClassRepre fatherCls) {
		this.fatherCls = fatherCls;
	}
	public List<ClassRepre> getSuperInterfaces() {
		return superInterfaces;
	}
	public void setSuperInterfaces(List<ClassRepre> superInterfaces) {
		this.superInterfaces = superInterfaces;
	}
	public boolean isInterface() {
		return isInterface;
	}
	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public List<FiledRepre> getFields() {
		return fields;
	}
	public void setFields(List<FiledRepre> fields) {
		this.fields = fields;
	}
	public List<MethodRepre> getMethods() {
		return methods;
	}
	public void setMethods(List<MethodRepre> methods) {
		this.methods = methods;
	}
	public List<ClassRepre> getInnerClazzes() {
		return innerClazzes;
	}

	public void setInnerClazzes(List<ClassRepre> innerClazzes) {
		this.innerClazzes = innerClazzes;
	}	
	
}
