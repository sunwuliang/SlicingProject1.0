package org.csu.slicing.instance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.csu.slicing.util.EMFHelper;
import org.csu.slicing.util.InvPrePostAnalyzer;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

public class InsGenerator {
	
	public static Set<EObject> generateInstance(EPackage ePkg) {
		
		EFactory eFactory = ePkg.getEFactoryInstance();
		EcorePackage ecorePkg = EcorePackage.eINSTANCE;
		Random rand = new Random();
		
		
		Set<EObject> totalObjs = new HashSet<EObject>();
		
		for (int i = 0; i < 502; i++) {
			Map<EClass, Set<EObject>> clsInsMap = new HashMap<EClass, Set<EObject>>();
			Set<EObject> objs = new HashSet<EObject>();
			
			for (EClassifier eClsf : ePkg.getEClassifiers()) {
				if (eClsf instanceof EClass) {
					EClass eCls = (EClass) eClsf;
					if (!eCls.isAbstract()) {
						EObject eObj = eFactory.create(eCls);
						objs.add(eObj);
						
						if (clsInsMap.containsKey(eCls)) {
							clsInsMap.get(eCls).add(eObj);
						} else {
							Set<EObject> ins = new HashSet<EObject>();
							ins.add(eObj);
							clsInsMap.put(eCls, ins);
						}
						
						for (EAttribute eAttri : eCls.getEAttributes()) {
							EDataType dataType = eAttri.getEAttributeType();
							
							// GetEType is in the super class
							// GetEAttributeType is an operation in EAttribute
							// System.out.println(eAttri.getName());
							// System.out.println("getEAttributeType " + eAttri.getEAttributeType());
							// System.out.println("getEType " + eAttri.getEType());
							Object value = null;
							if (dataType == ecorePkg.getEInt()) {
								value = rand.nextInt();
							}
							if (dataType == ecorePkg.getEBoolean()) {
								value = rand.nextBoolean();
							}
							if (dataType == ecorePkg.getEDouble()) {
								value = rand.nextDouble();
							}
							if (dataType == ecorePkg.getEFloat()) {
								value = rand.nextFloat();
							}
							if (dataType == ecorePkg.getELong()) {
								value = rand.nextLong();
							}
							if (dataType == ecorePkg.getEString()) {
								value = UUID.randomUUID().toString();
							}
							
							if (eObj.eGet(eAttri) instanceof List) {
								((List) eObj.eGet(eAttri)).add(value);
							} else {
								eObj.eSet(eAttri, value);
							}
						
						}
						
					}
				}
			}
			
			for (EObject eObj : objs) {
				EClass eCls = eObj.eClass();
				for (EReference eRef : eCls.getEReferences()) {
					EClass eRefCls = eRef.getEReferenceType();
					// System.out.println(eCls.getName() + " " + eObj.eGet(eRef));
					// GetEType is in the super class
					// GetEReferenceType is an operation in EReference
					// System.out.println(eRef.getName());
					// System.out.println("getEType " + eRef.getEType());
					// System.out.println("getEReferenceType " + eRef.getEReferenceType());
					if (clsInsMap.containsKey(eRefCls)) {
						for (EObject eRefObj : clsInsMap.get(eRefCls)) {
							// Multiplicity is 0..*
							if (eObj.eGet(eRef) instanceof List) {
								List objList = (List) eObj.eGet(eRef);
								if (objList.isEmpty())
									objList.add(eRefObj);
							}
							// Multiplicity is 1
							else {
								//System.out.println(eCls.getName());
								//System.out.println(eRef.getName());
								if (eObj.eGet(eRef) == null) {
									eObj.eSet(eRef, eRefObj);
									break;
								}
							} 
						}
					}
				}	
			}
			
			totalObjs.addAll(objs);
		}
		return totalObjs;
	}
	
	public static void main(String[] args) {
		/**
		 * DataSet1 was made by 500 iterations
		 * DataSet2 was made by 1000 iterations
		 * DataSet3 was made by 3000 iterations
		 */
		
		String basePath = "D:\\EclipseWorkspaceForSlicing\\ClassModelSlicing\\";
		String ecoreFileName = "UML2.ecore";
		String ecorePath = basePath + "ecores\\";
		String ecoreFilePath = ecorePath + ecoreFileName;
		String xmiPath = basePath + "xmis\\";
		String xmiFilePath = xmiPath + ecoreFileName.replace(".ecore", "InsDataSet1 .xmi") ;
		
		
		EPackage ePkg = EMFHelper.loadModel(ecoreFilePath);
		
		Set<EObject> objs = InsGenerator.generateInstance(ePkg);
		EMFHelper.saveInstance(xmiFilePath, objs);
		
		//List<EObject> eObjs = EMFHelper.loadInstance(xmiFilePath, ePkg);
		//for (EObject eObj : eObjs) {
		//	System.out.println(eObj.eClass().getName());
		//}
	}

}
