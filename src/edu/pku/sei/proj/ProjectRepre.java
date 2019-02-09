package edu.pku.sei.proj;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
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
		if(testRoot != null) {
			this.testRoot = testRoot;
		}
	}
	
	public String getName() {
		assert name != null;
		return name;
	}
	
	public String getSrcRoot() {
		assert srcRoot != null;
		return srcRoot;
	}
	
	public String getTestRoot() {
		assert testRoot != null;
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
		if(allPackageMap.containsKey(pkgName) == false) {
			return null;
		}
		return allPackageMap.get(pkgName);
	}
	
	public ClassRepre getOrNewClassRepre(PackageRepre pkg, File srcFile, int startPosition, String clsName){
		return pkg.getOrNewClassRepre(srcFile, startPosition, clsName);
	}
	
	public ClassRepre getClassRepre(PackageRepre pkg, String clsName){
		return pkg.getClassRepre(clsName);
	}
	
	public void removeEmptyPkgs(){
		Iterator<Entry<String, PackageRepre>> it = allPackageMap.entrySet().iterator();  
		while(it.hasNext()) {
			Entry<String, PackageRepre> entry = it.next();
			if(entry.getValue().getClazzesMap().size() == 0){
				it.remove();
			}
		}
	}
}
