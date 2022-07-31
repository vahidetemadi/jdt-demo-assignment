package jdtdemofirst.handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

public class JDTDemoHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		//MessageDialog.openInformation(
		//		window.getShell(),
		//		"Jdt-demo-first",
		//		iWorkspace.getDescription().toString());
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		
		try {
			analyzeJavaProjects(projects);
		} catch (CoreException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void analyzeJavaProjects(IProject[] projects) throws CoreException, JavaModelException, IOException {
		 for (IProject project : projects) {
			 if (!project.isNatureEnabled("org.eclipse.jdt.core.javanature"))
				 continue;
			 
			 IJavaProject javaProject = JavaCore.create(project);
			 
			 List<TypeDeclaration> classes = new Stack<>();
			 
			 //holds all types exists in this project
			 Set<TypeDeclaration> types = new HashSet<>();
			 
			 UnitVisitor unitVisitor = new UnitVisitor();
	           
			 for (IPackageFragment packageFragment : javaProject.getPackageFragments()) {
				 if (packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE)
					 for (ICompilationUnit iCompilationUnit : packageFragment.getCompilationUnits()) {
						 CompilationUnit cUnit = this.parseViaAST(iCompilationUnit);
						 cUnit.accept(unitVisitor);
						 for (TypeDeclaration type : unitVisitor.getTypeDeclarations())
								 types.add(type);
						 //fillCompilationUnitClassStack(classes, cUnit, unitVisitor);
					 }
			 }
			 
			 File file = new File(System.getProperty("user.dir") + File.separator + "output" + File.separator + javaProject.getElementName().toString() + "_stats.txt");
			 file.getParentFile().mkdirs();
			 FileWriter fileWriter = new FileWriter(file, false);
			 
			 System.out.println("Java project name is: " + javaProject.getElementName());
			 fileWriter.write("Java project name is: " + javaProject.getElementName() + "\n");
			 
			 System.out.println("Here is the # of classes: " + types.size());
			 fileWriter.write("Here is the # of classes: " + types.size() + "\n");
			 
			 System.out.println("Here is the # of methods: " + 
					 types.stream().mapToInt(type -> type.getMethods().length).sum());
			 fileWriter.write("Here is the # of methods: " + 
					 types.stream().mapToInt(type -> type.getMethods().length).sum() + "\n");
			 
			 //iterate over the MethodDeclarations of the type, and prints invocations made by
			 types.forEach(type -> {
				 System.out.println("Class name: " + type.getName());
				 try {
					fileWriter.write("Class name: " + type.getName() + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 for (FieldDeclaration fieldDeclaration : type.getFields()) {

					 System.out.println("......Field definition --> "
						 		+ "name: " + 
								 fieldDeclaration.toString()
								 + "--- type: " +
								 fieldDeclaration.getType());
					 try {
						fileWriter.write("......Field definition--> "
							 		+ "name: " + 
									 fieldDeclaration.toString()
									 + "--- type: " +
									 fieldDeclaration.getType() + "\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
				 
				 for (MethodDeclaration method : type.getMethods()) {
					 unitVisitor.setMethod(new ArrayList<MethodDeclaration>());
					 unitVisitor.setMethodInvocation(new ArrayList<MethodInvocation>());
					 method.accept(unitVisitor);
					 System.out.println("...Method name: " + method.getName());
					 try {
						fileWriter.write("...Method name: " + method.getName() + "\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 
					 for (Object parameter : method.parameters()) {

						 System.out.println("......Param --> "
						 		+ "name: " + 
								 ((SingleVariableDeclaration) parameter).getName()
								 + "--- type: " +
								 ((SingleVariableDeclaration) parameter).getType());
						 try {
							fileWriter.write("......Param --> "
							 		+ "name: " + 
									 ((SingleVariableDeclaration) parameter).getName()
									 + "--- type: " +
									 ((SingleVariableDeclaration) parameter).getType() + "\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }
						 
					 for (MethodInvocation methodInvocation : unitVisitor.getMethodInvocations()) {
						 System.out.println("......Invokes: " + methodInvocation.getName());
						 try {
							fileWriter.write("......Invokes: " + methodInvocation.getName() + "\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }						 
				 }
			 });
			 
			 fileWriter.close();
		 }
	}
	
	/**
	 * Takes a unit and returns AST parsable CompilationUnit 
	 * @param unit ICompilation unit coming from project package
	 * @return parsable CompilationUnit 
	 */
	private CompilationUnit parseViaAST(ICompilationUnit unit) {
		ASTParser astParser = ASTParser.newParser(AST.JLS11);
		
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setSource(unit);
		astParser.setResolveBindings(true);

		return (CompilationUnit) astParser.createAST(null);
	}
	
	/**
	 * Iterates over the types of a unit and add them to the stack
	 * @param classStack
	 * @param cUnit
	 * @param unitVisitor
	 * @throws JavaModelException
	 */
	private void fillCompilationUnitClassStack(Stack<TypeDeclaration> classStack, CompilationUnit cUnit, UnitVisitor unitVisitor) throws JavaModelException {
		cUnit.accept(unitVisitor);
		System.out.println(unitVisitor.getTypeDeclarations());
		for (TypeDeclaration typeDeclaration : unitVisitor.getTypeDeclarations()) {
			if (!classStack.contains(typeDeclaration)) {
				//System.out.println(typeDeclaration.getName());
				classStack.add(typeDeclaration);
				lookUpForNestedClasses(classStack, typeDeclaration);
			}
		}
	}
	
	/**
	 * Recursively loops through nested classes and add them to the stack collection
	 * @param classStack
	 * @param typeDeclaration
	 */
	private void lookUpForNestedClasses(Stack<TypeDeclaration> classStack, TypeDeclaration typeDeclaration) {
		for (TypeDeclaration typeDeclaration2 : typeDeclaration.getTypes()) {
			//System.out.println(typeDeclaration2.getName());
			classStack.add(typeDeclaration);
			lookUpForNestedClasses(classStack, typeDeclaration2);
		}
		
		return;
	}
}
