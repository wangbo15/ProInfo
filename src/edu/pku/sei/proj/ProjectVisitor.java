package edu.pku.sei.proj;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


public class ProjectVisitor extends ASTVisitor {
	
	private File file;
	private ProjectRepre projectRepre;
	private PackageRepre pkgRepre;
	private ClassRepre mainCls;

	
	private Set<String> importedClazzes = new HashSet<>();

	
	public ProjectVisitor(File file, ProjectRepre projectRepre, PackageRepre pkgRepre, ClassRepre mainCls) {
		super();
		this.file = file;
		this.projectRepre = projectRepre;
		this.pkgRepre = pkgRepre;
		this.mainCls = mainCls;
	}


	@Override
	public boolean visit(CompilationUnit node) {
		PackageDeclaration pkgDecl = node.getPackage();
		String pkgStr = (pkgDecl == null) ? "" : pkgDecl.getName().getFullyQualifiedName();
		
		assert pkgStr.equals(pkgRepre.getPkgName());
		
		for (Iterator it = node.imports().iterator(); it.hasNext(); ){
			ImportDeclaration impDecl = (ImportDeclaration) it.next();
			Name nm = impDecl.getName();
			importedClazzes.add(nm.toString());
		}
		
		
		return super.visit(node);
	}


	@Override
	public boolean visit(TypeDeclaration node) {
		//skip inner clazzes
		if(node.getParent() != node.getRoot()){
			return false;			
		}
		String className = node.getName().getIdentifier();
		ClassRepre currentCls = this.pkgRepre.getOrNewClassRepre(file, className);
		int flag = node.getModifiers();
		
		if(className.equals(mainCls.getName())){
			assert mainCls == currentCls;
		}
		
		currentCls.setFlag(flag);
		currentCls.setInterface(node.isInterface());
		
		Type superTp = 	node.getSuperclassType();
		if(superTp != null){
			//TODO:: differnt type class
			String superTpStr = superTp.toString();
			String fullCls = null;
			for(String imp : importedClazzes){
				if(imp.endsWith(superTpStr)){
					fullCls = imp;
					break;
				}
			}
			if(fullCls != null){
				currentCls.setFatherCls(fullCls);
			}else{
				currentCls.setFatherCls(superTpStr);
			}
		}
		
		System.out.println("INSERT CLS: " + currentCls);
		
		for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext();){
			
			ASTNode body = (ASTNode) it.next();
			if(body instanceof FieldDeclaration || body instanceof MethodDeclaration){
				FieldOrMethodVisitor fldVisitor = new FieldOrMethodVisitor(currentCls);
				node.accept(fldVisitor);
			}
		}	
		
		return super.visit(node);
	}
	
	private class FieldOrMethodVisitor extends ASTVisitor{
		private ClassRepre clsRepre;
		
		public FieldOrMethodVisitor(ClassRepre clsRepre){
			this.clsRepre = clsRepre;
		}
		
		@Override
		public boolean visit(FieldDeclaration node) {
			
			int flag = node.getModifiers();
			String tpStr = node.getType().toString();
			
			for (Iterator it = node.fragments().iterator();it.hasNext();){
				VariableDeclarationFragment frag = (VariableDeclarationFragment) it.next();
				
				FiledRepre field = new FiledRepre(clsRepre, node.getType().toString(), frag.getName().toString(), flag);
				clsRepre.insertFieldRepre(field);
				System.out.println("INSERT FLD: " + field);

			}
			
			return false;
		}
	
		
		@Override
		public boolean visit(MethodDeclaration node) {
			int flag = node.getModifiers();
			boolean isConstructor = node.isConstructor();
			String retTp = null;
			if(! isConstructor){
				retTp = node.getReturnType2() == null ? "void" : node.getReturnType2().toString();
			}
			MethodRepre mtdRepre = new MethodRepre(clsRepre, flag, node.getName().getIdentifier(), retTp, isConstructor);
			
			clsRepre.insertMethodRepre(mtdRepre);
			
			System.out.println("INSERT MTD: " + mtdRepre);
			
			List params = node.parameters();
			if(params != null && params.size() > 0){
				for (Iterator it = node.parameters().iterator(); it.hasNext(); ){
					SingleVariableDeclaration frag = (SingleVariableDeclaration) it.next();
					int paraFlag = frag.getModifiers();
					LocalRepre param = new LocalRepre(clsRepre, frag.getName().getIdentifier(), frag.getType().toString(), mtdRepre, paraFlag);
					mtdRepre.insertParam(param);
				}
				
			}
			
			return false;
		}
	}

}
