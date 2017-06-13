package edu.pku.sei.proj;

import java.io.File;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ClsCollectorVisitor extends ASTVisitor {
		
	private File file;
	private ProjectRepre projectRepre;
	private PackageRepre pkgRepre;
	private ClassRepre mainCls;
	
	public ClsCollectorVisitor(File file, ProjectRepre projectRepre, PackageRepre pkgRepre, ClassRepre mainCls) {
		super();
		this.file = file;
		this.projectRepre = projectRepre;
		this.pkgRepre = pkgRepre;
		this.mainCls = mainCls;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		if(node.getParent() != node.getRoot()){
			return false;			
		}
		
		String className = node.getName().getIdentifier();
		ClassRepre currentCls = this.pkgRepre.getOrNewClassRepre(file, className);
		
		String fullName = pkgRepre.getPkgName() + "." + className;
		projectRepre.fullNameToClazzesMap.put(fullName, currentCls);
		
		return super.visit(node);
	}
	
	
}
