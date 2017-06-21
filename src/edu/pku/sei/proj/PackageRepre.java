package edu.pku.sei.proj;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PackageRepre implements Serializable  {
	private String pkgName;
	private Map<String , ClassRepre> clazzesMap = new HashMap<>();
	
	public PackageRepre(String pkgName){
		this.pkgName = pkgName;
	}
	
	public String getPkgName() {
		return pkgName;
	}
	
	public static boolean isJdkPackage(String pkg){
		return pkg.startsWith("java") || pkg.startsWith("org.xml") || pkg.contains("junit.");
	}
	
	public void insertClass(String className, ClassRepre classRepre){
		clazzesMap.put(className, classRepre);
	}
	
	public ClassRepre getClassRepre(String className){
		if(clazzesMap.containsKey(className)){
			return clazzesMap.get(className);
		}
		return null;
	}
	
	public ClassRepre getOrNewClassRepre(File srcFile, int startPosition, String className){
		if(clazzesMap.containsKey(className)){
			return clazzesMap.get(className);
		}else{
			ClassRepre cls = new ClassRepre(srcFile, startPosition, this, className);
			clazzesMap.put(className, cls);
			return cls;
		}
	}
	

	public Map<String, ClassRepre> getClazzesMap() {
		return clazzesMap;
	}

	@Override
	public String toString() {
		return "PackageRepre [pkgName=" + pkgName + "]";
	}
		
}
