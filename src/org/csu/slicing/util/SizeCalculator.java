package org.csu.slicing.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public class SizeCalculator {
	
	public static void analyzeInstanceByFile(String insFilePath) {
		
		int totalNo = 0;
		int objNo = 0;
		int linkNo = 0;
		int slotNo = 0;
		
		File insFile = new File(insFilePath);
		
		StringBuffer contents = new StringBuffer();
	    BufferedReader input = null;
	    
	    try {  
	    	input = new BufferedReader( new FileReader(insFile) );	      
	    	String line = null; 
	    	while (( line = input.readLine()) != null){
	    		contents.append(line);
	    	}
	    } catch (FileNotFoundException ex) {
	    	ex.printStackTrace();
	    } catch (IOException ex){ 
	    	ex.printStackTrace();
	    } finally {
	    	try {
	    		if (input!= null) {
	    			input.close();
	    		}
	    	}catch (IOException ex) {
	    		ex.printStackTrace();
	    	}
	    }
	    String fileContents = contents.toString();
	    String[] strList  = fileContents.split("<UML2:");
	  	objNo = strList.length - 1;
	    linkNo = fileContents.split("#").length - 1;
	    for (int i = 1; i < strList.length; i++) {
	  		slotNo += (strList[i].split("=").length - strList[i].split("=\"#/").length);
	  	}
	    
	    if (objNo == 0) {
	    	totalNo = 0;
	    	linkNo = 0;
	    	slotNo = 0;
	    }
	    totalNo = objNo + linkNo + slotNo;
		
		//System.out.println("Objet diagram " + "has " + objNo + " objects");
		//System.out.println("Objet diagram " + "has " + slotNo + " slots");
		//System.out.println("Objet diagram " + "has " + linkNo + " links");
		System.out.println("Objet diagram " + "has " + totalNo + " object elements");
	}
	
	public static void analyzeInstance(List<EObject> objs) {
		int totalNo = 0;
		int objNo = 0;
		int linkNo = 0;
		int slotNo = 0;
		
		for (EObject eObj : objs) {
			objNo = objNo + 1;
			//System.out.println(eObj);
			for (EAttribute eAttr : eObj.eClass().getEAllAttributes()) {
				if (eObj.eGet(eAttr) != null) {
					//System.out.println(eObj.eGet(eAttr));
					slotNo = slotNo + 1;
				}
			}
			
			for (EReference eRef : eObj.eClass().getEAllReferences()) {
				Object value = eObj.eGet(eRef);
				// 1 or EList
				if (value != null) {
					if (value instanceof EList) {
						if (((EList)value).size() != 0) {
							linkNo = linkNo + 1;
							//System.out.println(eObj.eGet(eRef));
						}
					} else {
						linkNo = linkNo + 1;
						//System.out.println(eObj.eGet(eRef));
					}
				}
				
			}
		}
		
		totalNo = objNo + linkNo + slotNo;
		
		System.out.println("Objet diagram " + "has " + objNo + " objects");
		System.out.println("Objet diagram " + "has " + slotNo + " slots");
		System.out.println("Objet diagram " + "has " + linkNo + " links");
		System.out.println("Objet diagram " + "has " + totalNo + " object elements");
	}
	public static void analyzeModel(EPackage ePkg) {
		
		int totalNo = 0;
		int clsNo = 0;
		int attrNo = 0;
		int refNo = 0;
		int opNo = 0;
		int parNo = 0;
		int dtNo = 0;
		int enumNo = 0;
		
		for (EClassifier eClsf : ePkg.getEClassifiers()) {
			if (eClsf instanceof EClass) {
				clsNo = clsNo + 1;
				
				EClass eCls = (EClass) eClsf;
				for (EStructuralFeature eStf : eCls.getEStructuralFeatures()) {
					if (eStf instanceof EAttribute) {
						attrNo = attrNo + 1;
					}
					if (eStf instanceof EReference) {
						refNo = refNo + 1;
					}
				}
				
				for (EOperation eOprt : eCls.getEOperations()) {
					opNo = opNo + 1;
					
					parNo = parNo + eOprt.getEParameters().size();
				}
				
			}
			
			// EEnum is a subclass of EDataType
			if (eClsf instanceof EEnum) {
				enumNo = enumNo + 1;
			} else {
				if (eClsf instanceof EDataType) {
					dtNo = dtNo + 1;
				}
			}
			
		}
				
		totalNo = clsNo + attrNo + refNo + opNo + parNo + dtNo + enumNo ;
		
		System.out.println("Class model " + ePkg.getName() + " has " + clsNo + " classes");
		System.out.println("Class model " + ePkg.getName() + " has " + attrNo + " attributes");
		System.out.println("Class model " + ePkg.getName() + " has " + refNo + " references");
		System.out.println("Class model " + ePkg.getName() + " has " + opNo + " operations");
		System.out.println("Class model " + ePkg.getName() + " has " + parNo + " parameters");
		System.out.println("Class model " + ePkg.getName() + " has " + dtNo + " datatypes");
		System.out.println("Class model " + ePkg.getName() + " has " + enumNo + " enumerations");
		System.out.println("Class model " + ePkg.getName() + " has " + totalNo + " class model elements");
	}
	public static void main(String[] args) {
		String basePath = "D:\\EclipseWorkspaceForSlicing\\ClassModelSlicing\\";
		
		String ecorePath = basePath + "ecores\\";
		String slicePath = basePath + "slices\\";
		String xmiPath = basePath + "xmis\\";
		
		String modelName = "UML2.ecore";
		String modelPath = ecorePath + modelName;
		//String slicedModelName = "SlicedUML2InsMARTE.ecore";
		
		//EPackage ePkg = EMFHelper.loadModel(modelPath);
		//SizeCalculator.analyzeModel(ePkg);
		
		/*if (ePkg.getESubpackages().isEmpty()) {
			SizeCalculator.analyzeModel(ePkg);
		} else {
			for (EPackage eSubPkg : ePkg.getESubpackages()) {
				System.out.println(eSubPkg.getName());
				SizeCalculator.analyzeModel(eSubPkg);
			}
		}*/
		//String ins2ecorePath = ecorePath + "MARTE.ecore";
		//EPackage ins2ecorePkg = EMFHelper.loadModel(ins2ecorePath);
		//SizeCalculator.analyzeModel(ins2ecorePkg);
		
		//String insName = "UML2InsMARTE.xmi";
		//String xmiPath = basePath + "xmis\\";
		//String insPath = xmiPath + insName;
		//List<EObject> objs = EMFHelper.loadInstance(insPath, ePkg);
		//SizeCalculator.analyzeInstance(objs);
		
		
		//String slicedModelPath = slicePath + slicedModelName;
		//EPackage slicedPkg = EMFHelper.loadModel(slicedModelPath);
		//SizeCalculator.analyzeModel(slicedPkg);
		
		EPackage ePkg = EMFHelper.loadModel(modelPath);
		SizeCalculator.analyzeModel(ePkg);
		
		String insName = "UML2InsRoyalAndLoyal.xmi";//"UML2InsLibrary.xmi";
		String insPath = xmiPath + insName;
		List<EObject> eObjs = EMFHelper.loadInstance(insPath, ePkg);
		//SizeCalculator.analyzeInstance(eObjs);
		
		SizeCalculator.analyzeInstanceByFile(insPath);
	}

}
