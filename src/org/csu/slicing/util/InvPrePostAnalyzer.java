package org.csu.slicing.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;

public class InvPrePostAnalyzer {
	
	private EPackage ePkg;
	private String oclFilePath;
	// Used for Coslicer
	// <InvName, Set<ReferencedElements>>
	private Map<String, Set<Object>> refModelElmtsPerInv;
	// Used for ContractAwareSlicer
	// <Oper, Set<ReferencedElements>, <Inv, Set<ReferencedElements> 
	private Map<EObject, Set<Object>> refModelElmtsPerCons;
	// <OperName + "Pre", Constraint>, <OperName + "Post", Constraint>, <InvName, Constraint>
	private Map<String, Constraint> constraintMap;
	
	public InvPrePostAnalyzer(EPackage eModel, String oclFilePath) {
		this.ePkg = eModel;
		this.oclFilePath = oclFilePath;
	}
	
	public Map<String, Set<Object>> getRefModelElmtsPerInv() {
		if (refModelElmtsPerInv == null)
			analyzeRefModelElmtsPerInv();
		
		return refModelElmtsPerInv;
	}	
	
	public Map<EObject, Set<Object>> getRefModelElmtsPerCons() {
		if (refModelElmtsPerCons == null)
			analyzeRefModelElmtsPerInv();
		
		return refModelElmtsPerCons;
	}
	
	public Map<String, Constraint> getConstraintMap() {
		if (constraintMap == null)
			analyzeRefModelElmtsPerInv();
		
		return constraintMap;
	}

	private void analyzeRefModelElmtsPerInv() {
		EPackage.Registry registry = new EPackageRegistryImpl();
		
		if (ePkg == null)
			return;
		
		refModelElmtsPerInv = new HashMap<String, Set<Object>>();
		refModelElmtsPerCons = new HashMap<EObject, Set<Object>>();
		
		registry.put(ePkg.getNsURI(), ePkg);
		
		EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(registry);
		OCL ocl = OCL.newInstance(environmentFactory);
		
		constraintMap = new HashMap<String, Constraint>();
		
		RefModelElmtVisitor visitor = RefModelElmtVisitor.getInstance();
		
		// parse the contents as an OCL document
		try {
			InputStream in = new FileInputStream(oclFilePath);	
			/* 
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String temp;
			while ((temp = br.readLine()) != null) {
				System.out.println(temp);
			}*/
			
			OCLInput input = new OCLInput(in);
		    
		    List<Constraint> constraints = ocl.parse(input);
		    
			long startTime = System.currentTimeMillis();
			
		    
		    for (Constraint next : constraints) {
		    	
		        OCLExpression<EClassifier> body = next.getSpecification().getBodyExpression();
		        body.accept(visitor);
		        
		        String stringKey = next.getName();
		        EObject objKey = next;
		        
		        String consType = next.getStereotype();
		        //System.out.println(consType);
		        if (consType.equals("precondition") || consType.equals("postcondition") || consType.equals("body")) {
		        	//System.out.println(next.getConstrainedElements());
			        EOperation eOper = (EOperation)next.getConstrainedElements().get(0);
			        
			        stringKey = eOper.getName();
			        objKey = eOper;
			        
			        if (consType.equals("precondition")) {
			        	constraintMap.put(stringKey + "Pre", next);
			        } else {
			        	if (consType.equals("postcondition"))
			        		constraintMap.put(stringKey + "Post", next);
			        	else
			        		constraintMap.put(stringKey + "Body", next);
			        }
			        
		    	} else {
		    		//System.out.printf("%s: %s%n", next.getName(), body);
		    		constraintMap.put(stringKey, next);
		    	}
		        if (stringKey == null) {
		        	throw new Exception("Invariant must have a name!");
		        }
		        
		        Set<Object> tempElmts = new HashSet<Object>(visitor.getRefModelElmts());
		        
		        if (consType.equals("invariant")) {
		        	this.refModelElmtsPerInv.put(stringKey, tempElmts);
		        }
		        if (this.refModelElmtsPerCons.containsKey(objKey)) {
		        	this.refModelElmtsPerCons.get(objKey).addAll(tempElmts);
		        } else {
		        	this.refModelElmtsPerCons.put(objKey, tempElmts);
		        }
		        visitor.getRefModelElmts().clear();
		    }
		    
		    long estimatedTime = System.currentTimeMillis() - startTime;
			
		    System.out.println("Parsing OCL file estimated time is " + estimatedTime);
			
		    in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}		

	}
	
	public static void main(String[] args) {
		// Two types of Ecore model input path: 
		// 1. "input/UML2MM.ecore"
		// 2."D:\\EclipseWorkspaceForSlicing\\ClassModelSlicing\\input\\UML2MM.ecore"
		// Two types of Ecore output path:
		// 1. "UML2MM.ecore" 
		// 2. "D:\\EclipseWorkspaceForSlicing\\ClassModelSlicing\\input\\UML2MM.ecore"
		
		
		String basePath = "D:\\EclipseWorkspaceForSlicing\\ClassModelSlicing\\";
		String ecoreFileName = "CoachBus.ecore";
		String ecorePath = basePath + "ecores\\";
		String ecoreFilePath = ecorePath + ecoreFileName;
		String oclFilePath = basePath + "ocls\\" + ecoreFileName.replace(".ecore", ".ocl");
		
		EPackage ePkg = EMFHelper.loadModel(ecoreFilePath);
		InvPrePostAnalyzer iParser = new InvPrePostAnalyzer(ePkg, oclFilePath);
		Map<String, Set<Object>> refModelElmtsPerInv = iParser.getRefModelElmtsPerInv();
		for (String invName : refModelElmtsPerInv.keySet()) {
			System.out.println(invName);
			
			Collection<Object> refModelElmts = refModelElmtsPerInv.get(invName);
			for (Object refModelElmt : refModelElmts) {
				System.out.println(refModelElmt);
			}
			
			System.out.println("");
		}
		
		//EPackage test = (EPackage)EMFHelper.loadModel(ecoreFilePath);
		//System.out.println(test.getName());
	}

}
