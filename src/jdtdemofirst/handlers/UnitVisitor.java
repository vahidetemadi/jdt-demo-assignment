package jdtdemofirst.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class UnitVisitor extends ASTVisitor{
	
	List<MethodDeclaration> methods = new ArrayList<>();
	
	List<TypeDeclaration> types = new ArrayList<>();
	
	List<MethodInvocation> methodInvocations = new ArrayList<>();
	
	@Override
	public boolean visit(MethodDeclaration method) {
		methods.add(method);
		
		return super.visit(method);
	}
	
	@Override
	public boolean visit(TypeDeclaration typeDeclaration) {
		types.add(typeDeclaration);
		
		return super.visit(typeDeclaration);
	}
	
	@Override
	public boolean visit(MethodInvocation methodInvocation) {
		methodInvocations.add(methodInvocation);
		
		return super.visit(methodInvocation);
	}
	
	public void setMethod(List<MethodDeclaration> methods) {
		this.methods = methods;
	}
	
	public void setMethodInvocation(List<MethodInvocation> methodInvocations) {
		this.methodInvocations = methodInvocations;
	}
	
	public List<MethodDeclaration> getMethods() {
		return methods;
	}
	
	public List<TypeDeclaration> getTypeDeclarations() {
		return types;
	}
	
	public List<MethodInvocation> getMethodInvocations() {
		return methodInvocations;
	}

}
