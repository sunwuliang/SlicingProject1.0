package org.csu.slicing.main;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csu.slicing.util.EMFHelper;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.Query;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;

/**
 * http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.ocl.doc%2Fhelp%2FEvaluatingConstraints.html&cp=49_5_1
 * @author sun
 *
 */
public class Evaluator {
	
	private EPackage ePkg;
	private List<EObject> eObjs;
	private String oclFilePath;
	private Set<String> selectedInvs;
	
	public Evaluator(EPackage ePkg, List<EObject> eObjs, String oclFilePath, 
			Set<String> invNames) {
		this.ePkg = ePkg;
		this.eObjs = eObjs;
		this.oclFilePath = oclFilePath;
		this.selectedInvs = invNames;
	}
	
	public Map<String, Map<EObject, Boolean>> check() {
		EPackage.Registry registry = new EPackageRegistryImpl();
		
		if (ePkg == null)
			return null;
		
		registry.put(ePkg.getNsURI(), ePkg);
		
		EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(registry);
		OCL ocl = OCL.newInstance(environmentFactory);
		
		Map<String, Constraint> constraintMap = new HashMap<String, Constraint>();
		
		// parse the contents as an OCL document
		try {
			InputStream in = new FileInputStream(oclFilePath);	
			OCLInput input = new OCLInput(in);
			List<Constraint> constraints = ocl.parse(input);
		    
		    for (Constraint next : constraints) {
		    	
		    	if (this.selectedInvs == null || this.selectedInvs.size() == 0 ||
		    			this.selectedInvs.contains(next.getName())) {
		    		constraintMap.put(next.getName(), next);
		    	}
		       
		        //OCLExpression<EClassifier> body = next.getSpecification().getBodyExpression();
		        //System.out.printf("%s: %s%n", next.getName(), body);
		        //for (EObject eObj : next.getConstrainedElements()) {
		        //	System.out.println(eObj);
		        //}
		    }   
		    in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}	
		
		
		long startTime = System.currentTimeMillis();
		
		Map<String, Map<EObject, Boolean>> result = new HashMap<String, Map<EObject, Boolean>>();
		
		// Class name <--> objects mapping
		// Cannot use class <--> objects mapping for .oclxmi file because reading ocl from .oclxmi file cannot binding the 
		// class model with the ocl constraint
		Map<EClass, Set<EObject>> clsObjs = new HashMap<EClass, Set<EObject>>(); 
		
		for (EClassifier eClsf : this.ePkg.getEClassifiers()) {
			if (eClsf instanceof EClass) {
				EClass eCls = (EClass) eClsf;
				clsObjs.put(eCls, new HashSet<EObject>());
			}
		}
		
		for (EObject eObj : eObjs) {
			if (clsObjs.containsKey(eObj.eClass())) {
				clsObjs.get(eObj.eClass()).add(eObj);
			} 
		}
		/*
		for (EObject eObj : eObjs) {
			if (clsObjs.containsKey(eObj.eClass())) {
				clsObjs.get(eObj.eClass()).add(eObj);
			} else {
				Set<EObject> instances = new HashSet<EObject>();
				instances.add(eObj);
				clsObjs.put(eObj.eClass(), instances);
			}
		}*/
		
		OCLHelper<EClassifier, ?, ?, Constraint> helper = ocl.createOCLHelper();
		for (Constraint constraint : constraintMap.values()) {
			Map<EObject, Boolean> resultPerInv = new HashMap<EObject, Boolean>();
			result.put(constraint.getName(), resultPerInv);
			//System.out.println("Checking Invariant " + constraint.getName() + " on ");
			
			for (EObject eObj : constraint.getConstrainedElements()) {
		        if (eObj instanceof EClass) {
		        	EClass eContextCls = (EClass)eObj;
		        	helper.setContext(eContextCls);
		        	
		        	Query constraintEval = ocl.createQuery(constraint);
		        	if (clsObjs.containsKey(eContextCls)) {
		        		evaluate(eContextCls, clsObjs, constraintEval, resultPerInv);
		        	}
		        	for (EClass eSubCls : clsObjs.keySet()) {
		        		if (eSubCls.getEAllSuperTypes().contains(eContextCls)) {
		        			evaluate(eSubCls, clsObjs, constraintEval, resultPerInv);
		        		}
		        	}
		        }
		    }
		}
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Evaluation estimated time is " + estimatedTime);
		
		return result;
	}
	
	private void evaluate(EClass eCls, Map<EClass, Set<EObject>> clsObjs, 
			Query constraintEval, Map<EObject, Boolean> resultPerInv) {
		for (EObject ins : clsObjs.get(eCls)) {
			boolean result = constraintEval.check(ins);
    		resultPerInv.put(ins, result);
    		//System.out.println("--------" + ins);
    		//System.out.println("--------Result is " + result);	
    	}
	}
	
	public static void main(String[] args) {
		String basePath = "D:\\EclipseWorkspaceForSlicing\\ClassModelSlicing\\";
		String ecoreFileName = "UML2.ecore";
		String ecorePath = basePath + "ecores\\";
		String ecoreFilePath = ecorePath + ecoreFileName;
		String xmiPath = basePath + "xmis\\";
		String xmiFileName = "UML2InsRoyalAndLoyal.xmi";//ecoreFileName.replace(".ecore", ".xmi");
		String xmiFilePath = xmiPath + xmiFileName;
		String oclFileName = ecoreFileName.replace(".ecore", ".ocl");
		String oclFilePath = basePath + "ocls\\" + oclFileName;
		
		String slicedModelPath = basePath + "slices\\" + "Sliced" + xmiFileName.replace(".xmi", ".ecore");
		String slicedOclFilePath = basePath + "slices\\" + "Sliced" + xmiFileName.replace(".xmi", ".ocl");
		String slicedInstancePath = basePath + "slices\\" + "Sliced" + xmiFileName;
		
		Set<String> invNames = new HashSet<String>();
		String temp = "onlyBinaryAssociationCanBeAggregations";
		invNames.add(temp);
		
		///*
		long startTime = System.currentTimeMillis();
		
		EPackage ePkg1 = EMFHelper.loadModel(ecoreFilePath);
		List<EObject> eObjs1 = EMFHelper.loadInstance(xmiFilePath, ePkg1);			
		Evaluator eval1 = new Evaluator(ePkg1, eObjs1, oclFilePath, invNames);
		Map<String, Map<EObject, Boolean>> result1 = eval1.check();
		System.out.println(result1.keySet());
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Evaluation estimated time including file loading is " + estimatedTime);
		//*/
		
		/*
		long startTime1 = System.currentTimeMillis();
		
		slicedModelPath = slicedModelPath.replace(".ecore", temp  + ".ecore");
		slicedOclFilePath = slicedOclFilePath.replace(".ocl", temp + ".ocl");
		slicedInstancePath = slicedInstancePath.replace(".xmi", temp + ".xmi");
		
		EPackage ePkg2 = EMFHelper.loadModel(slicedModelPath);
		List<EObject> eObjs2 = EMFHelper.loadInstance(slicedInstancePath, ePkg2);
		Evaluator eval2 = new Evaluator(ePkg2, eObjs2, slicedOclFilePath, null);
		Map<String, Map<EObject, Boolean>> result2 = eval2.check();
		System.out.println(result2.keySet());
				
		long estimatedTime1 = System.currentTimeMillis() - startTime1;
		System.out.println("Evaluation estimated time including file loading is " + estimatedTime1);
		*/
		
		//System.gc();
		
	}
}
