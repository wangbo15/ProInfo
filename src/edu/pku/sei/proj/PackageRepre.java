package edu.pku.sei.proj;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PackageRepre implements Serializable  {

	private static final long serialVersionUID = -5884875718554506641L;
	
	private String pkgName;
	private Map<String , ClassRepre> clazzesMap = new HashMap<>();
	
	public PackageRepre(String pkgName){
		this.pkgName = pkgName;
	}
	
	public String getPkgName() {
		return pkgName;
	}
	
	public static boolean isJdkPackage(String pkg){
		return pkg.startsWith("java") || pkg.startsWith("org.xml") || pkg.contains("junit.") || pkg.startsWith("com.sun.");
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pkgName == null) ? 0 : pkgName.hashCode());
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
		PackageRepre other = (PackageRepre) obj;
		if (pkgName == null) {
			if (other.pkgName != null)
				return false;
		} else if (!pkgName.equals(other.pkgName))
			return false;
		return true;
	}
	
}
