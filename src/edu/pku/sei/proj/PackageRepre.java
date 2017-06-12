package edu.pku.sei.proj;

import java.util.HashMap;
import java.util.Map;

public class PackageRepre {
	private String pkgName;
	private Map<String , ClassRepre> clazzesMap = new HashMap<>();
	
	public PackageRepre(String pkgName){
		this.pkgName = pkgName;
	}
	
	public String getPkgName() {
		return pkgName;
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
}
