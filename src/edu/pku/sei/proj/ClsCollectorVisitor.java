package edu.pku.sei.proj;

import java.io.File;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ClsCollectorVisitor extends ASTVisitor {
		
	private File file;
	private ProjectRepre projectRepre;
	private PackageRepre pkgRepre;
	
	public ClsCollectorVisitor(File file, ProjectRepre projectRepre, PackageRepre pkgRepre) {
		super();
		this.file = file;
		this.projectRepre = projectRepre;
		this.pkgRepre = pkgRepre;
	}

	public static String typeDeclToClassName(TypeDeclaration node){
		String className = "";
		ASTNode father = null;
		ASTNode curNode = node;
		while((father  = curNode.getParent()) != null){
			TypeDeclaration curNodeTD = (TypeDeclaration) curNode;
			className = curNodeTD.getName().getIdentifier() + "$" + className;
			curNode = father;
		}
		
		return className.substring(0, className.length() - 1);
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		
		String className = typeDeclToClassName(node);
		
		String fileName = file.getName();
		String mainClsName = fileName.substring(0, fileName.length() - 5);
		
			
//		System.out.println(">>>>>>>>" + file.getName() + "  " + node.getName());
//		System.out.println("\t\t\t" +  className);

				
		ClassRepre currentCls = this.pkgRepre.getOrNewClassRepre(file, node.getStartPosition(), className);

		
		
//		currentCls.setMainCls(true);

			
		
		

		currentCls.setFlag(node.getModifiers());
		
		String fullName = pkgRepre.getPkgName() + "." + className;
		projectRepre.fullNameToClazzesMap.put(fullName, currentCls);
		
		return super.visit(node);
	}
	
	
}
