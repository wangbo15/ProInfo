package edu.pku.sei.proj;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassRepre implements Serializable {

	private static final long serialVersionUID = 160218840478062973L;

	private transient File srcFile;
	
	private transient int startPosition = -1;
	
	private PackageRepre pkg;
	private String name;
		
	private ClassRepre fatherCls;
	private List<ClassRepre> superInterfaces;
	
	private boolean isInterface;

	private int flag = -1;
	
	private List<FieldRepre> fields = new ArrayList<>();
		
	private List<MethodRepre> methods = new ArrayList<>();
	
	private ClassRepre containerCls;
	
	private boolean isLibaryClz = false;

	public ClassRepre(File srcFile, int startPosition, PackageRepre pkg, String clsName) {
		this.pkg = pkg;
		this.srcFile = srcFile;
		this.name = clsName;
		this.startPosition = startPosition;
	}
	
	public File getSrcFile() {
		return srcFile;
	}
	public void setSrcFile(File srcFile) {
		this.srcFile = srcFile;
	}
	
	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
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
	
	public ClassRepre getContainerCls() {
		return containerCls;
	}


	public void setContainerCls(ClassRepre containerCls) {
		this.containerCls = containerCls;
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
	
	public boolean isLibaryClz() {
		return isLibaryClz;
	}

	public void setLibaryClz(boolean isLibaryClz) {
		this.isLibaryClz = isLibaryClz;
	}
	
	@Override
	public String toString() {
		return pkg.getPkgName() + "." + name  + (fatherCls == null ? " " : " EXTENDS " + fatherCls.getName()) + ", flag=" + flag + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pkg == null) ? 0 : pkg.hashCode());
		result = prime * result + ((srcFile == null) ? 0 : srcFile.hashCode());
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
		ClassRepre other = (ClassRepre) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pkg == null) {
			if (other.pkg != null)
				return false;
		} else if (!pkg.equals(other.pkg))
			return false;
		if (srcFile == null) {
			if (other.srcFile != null)
				return false;
		} else if (!srcFile.equals(other.srcFile))
			return false;
		return true;
	}	
	
}
