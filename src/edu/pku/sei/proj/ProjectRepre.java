package edu.pku.sei.proj;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ProjectRepre implements Serializable {

	private static final long serialVersionUID = 9123749754249191830L;
	
	private String name;
	private String srcRoot;
	private String testRoot;
		
	public Map<String, PackageRepre> allPackageMap = new HashMap<>();
	
	public Map<String, ClassRepre> fullNameToClazzesMap = new HashMap<>();

	public ProjectRepre(String name, String srcRoot, String testRoot) {
		super();
		this.name = name;
		this.srcRoot = srcRoot;
		this.testRoot = testRoot;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	public PackageRepre getOrNewPackage(String pkgName){
		if(allPackageMap.containsKey(pkgName)){
			return allPackageMap.get(pkgName);
		}else{
			PackageRepre pkgRepre = new PackageRepre(pkgName);
			allPackageMap.put(pkgName, pkgRepre);
			return pkgRepre;
		}
	}

	public PackageRepre getPackage(String pkgName){
		
		assert allPackageMap.containsKey(pkgName): "ERROR PKG: " + pkgName;
		
		return allPackageMap.get(pkgName);
	}
	
	public ClassRepre getOrNewClassRepre(PackageRepre pkg, File srcFile, int startPosition, String clsName){
		return pkg.getOrNewClassRepre(srcFile, startPosition, clsName);
	}
	
	public ClassRepre getClassRepre(PackageRepre pkg, String clsName){
		return pkg.getClassRepre(clsName);
	}
	
	
	public void removeEmptyPkgs(){
		Map<String, PackageRepre> tmpMap = new HashMap<>();
		for(Entry<String, PackageRepre> entry : allPackageMap.entrySet()){
			if(entry.getValue().getClazzesMap().size() == 0){
				continue;
			}else{
				tmpMap.put(entry.getKey(), entry.getValue());
			}
		}
		allPackageMap.clear();
		allPackageMap = null;
		allPackageMap = tmpMap;
	}
	
}
