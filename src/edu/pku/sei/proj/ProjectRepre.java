package edu.pku.sei.proj;

import java.util.HashMap;
import java.util.Map;

public class ProjectRepre {
	private String name;
	private int bug;
	private String srcRoot;
	private String testRoot;
		
	private Map<String, PackageRepre> allPackageMap = new HashMap<>();
	private Map<String, ClassRepre> allClassMap = new HashMap<>();
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getBug() {
		return bug;
	}
	public void setBug(int bug) {
		this.bug = bug;
	}
	public String getSrcRoot() {
		return srcRoot;
	}
	public void setSrcRoot(String srcRoot) {
		this.srcRoot = srcRoot;
	}
	public String getTestRoot() {
		return testRoot;
	}
	public void setTestRoot(String testRoot) {
		this.testRoot = testRoot;
	}
	
	public PackageRepre getPackage(String pkgName){
		if(allPackageMap.containsKey(pkgName)){
			return allPackageMap.get(pkgName);
		}else{
			PackageRepre pkgRepre = new PackageRepre(pkgName);
			allPackageMap.put(pkgName, pkgRepre);
			return pkgRepre;
		}
	}

	public ClassRepre getClassRepre(String pkgName, String clsName){
		PackageRepre pkg = getPackage(pkgName);
		return pkg.getClassRepre(clsName);
	}
	
	public ClassRepre getClassRepre(PackageRepre pkg, String clsName){
		return pkg.getClassRepre(clsName);
	}
}
