package edu.pku.sei.proj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClassRepre {
	private File srcFile;
	
	private PackageRepre pkg;
	private String name;
	
	private ClassRepre fatherCls;
	private List<ClassRepre> superInterfaces;
	
	private boolean isInterface;

	private int flag;
	
	private List<FieldRepre> fields = new ArrayList<>();
	private List<MethodRepre> methods = new ArrayList<>();
	
	
	private List<ClassRepre> innerClazzes;
	
	public ClassRepre(File srcFile, PackageRepre pkg, String clsName) {
		super();
		this.pkg = pkg;
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

	public void insertFieldRepre(FieldRepre field){
		fields.add(field);
	}
	
	public List<FieldRepre> getFields() {
		return fields;
	}
	
	public void insertMethodRepre(MethodRepre method){
		methods.add(method);
	}

	public List<MethodRepre> getMethods() {
		return methods;
	}

	public List<ClassRepre> getInnerClazzes() {
		return innerClazzes;
	}

	public void setInnerClazzes(List<ClassRepre> innerClazzes) {
		this.innerClazzes = innerClazzes;
	}
	
	public List<MethodRepre> getMethodRepreByName(String name){
		List<MethodRepre> res = new ArrayList<>();
		for(MethodRepre mtd : this.methods){
			if(mtd.getName().equals(name)){
				res.add(mtd);
			}
		}
		return res;
	}

	public FieldRepre getFieldRepreByName(String name){
		for(FieldRepre fld : this.fields){
			if(fld.getName().equals(name)){
				return fld;
			}
		}
		return null;
	}
	
	
	@Override
	public String toString() {
		return "ClassRepre [pkg=" + pkg + ", name=" + name  + (fatherCls == null ? " " : ", fatherCls= " + fatherCls.getName()) + ", flag=" + flag + "]";
	}	
	
}
