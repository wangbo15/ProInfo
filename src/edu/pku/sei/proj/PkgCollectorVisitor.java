package edu.pku.sei.proj;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class PkgCollectorVisitor extends ASTVisitor{
	
	private String currentPkg = "";
	
	public String getCurrentPkg() {
		return currentPkg;
	}
	
	@Override
	public boolean visit(CompilationUnit node) {
		if(node.getPackage() != null) {
			currentPkg = node.getPackage().getName().toString().trim();

		}
		return false;
	}
	
}
