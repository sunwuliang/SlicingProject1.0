package org.csu.slicing.util;

/**
 * http://www.ibm.com/developerworks/library/os-eclipse-dynamicemf/
 * http://www.vogella.com/tutorials/EclipseEMF/article.html EMF Generator
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.EcoreOCLStandardLibrary;
import org.eclipse.ocl.expressions.OCLExpression;

public class EMFHelper {
	
		
	public static EPackage loadModel(String path) {
		URI fileURI = URI.createFileURI(path);
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new EcoreResourceFactoryImpl());
		Resource resource = resourceSet.getResource(fileURI, true);
		EObject eObj = resource.getContents().get(0);
		
		if (eObj instanceof EPackage) {
			EPackage ePkg = (EPackage)eObj;
			// Local registration
			// resourceSet.getPackageRegistry().put(ePkg.getNsURI(), ePkg);
			// Globe registration
			// EPackage.Registry.INSTANCE.put(ePkg.getNsURI(), ePkg);
		
			return ePkg;
		}
		return null;
	}
	
	public static List<EPackage> loadModelWithMultiplePackages(String path) {
		URI fileURI = URI.createFileURI(path);
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new EcoreResourceFactoryImpl());
		Resource resource = resourceSet.getResource(fileURI, true);
		
		List<EPackage> ePkgs = new ArrayList<EPackage>();
		for (EObject eObj : resource.getContents()) {
			if (eObj instanceof EPackage) {
				EPackage ePkg = (EPackage)eObj;
				ePkgs.add(ePkg);
			}
		}
			
		return ePkgs;
	}
	
	public static List<EObject> loadInstance(String path, EPackage ePkg) {
		URI fileURI = URI.createFileURI(path);
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("*",
						new EcoreResourceFactoryImpl());
		/*
		 * There is no need to register the package probably because XMLResource.OPTION_SCHEMA_LOCATION
		 * works when saving an instance. 
		 */
		resourceSet.getPackageRegistry().put(ePkg.getNsURI(), ePkg);
		Resource resource = resourceSet.getResource(fileURI, true);
		
		//for (EObject eObj : resource.getContents())
		//	System.out.println(eObj);
		
		return resource.getContents();
	}
	
	public static void saveModel(String path, EPackage ePkg) {
		URI fileURI = URI.createFileURI(path);
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new EcoreResourceFactoryImpl());
		//resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", 
		//		new XMLResourceFactoryImpl());
		Resource resource = resourceSet.createResource(fileURI);
		//System.out.println(fileURI.path());
		resource.getContents().add(ePkg);
		try {
			resource.save(null);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveInstance(String path, Set<EObject> objs) {
		URI fileURI = URI.createFileURI(path);
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("*",
						new EcoreResourceFactoryImpl());
		Resource resource = resourceSet.createResource(fileURI);
		resource.getContents().addAll(objs);
		try {
			
			/**
			 * This adds an xsi:schemaLocation attribute to company.xml, associating the package's namespace 
			 * URI with the physical URI "xxx.ecore" (this relative URI is automatically computed, allowing 
			 * the two files to be relocated, together).
			 * It can be used to solve the PackageNotFoundException problem. 
			 */
			Map options = new HashMap();
			options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
			resource.save(options);
			
			//resource.save(null);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveOCLbyModification(String path, Set<Constraint> consSet, String ecoreFileName, EPackage ePkg) {
		
		try {
			File file = new File(path);
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write("import 'platform:/resource/ClassModelSlicing/" + 
					"slices/" + ecoreFileName + "'\n\n\n");
			out.write("package " + ePkg.getName() + "\n\n\n");
			for (Constraint constraint: consSet) {
				String cons = constraint.toString();
				cons = cons.replace(".<", " <");
				cons = cons.replace(".>", " >");
				cons = cons.replace(".=", " =");
				cons = cons.replace(".implies", " =");
				cons = cons.replace(".or", " =");
				cons = cons.replace(".and", " =");
				
				out.write(cons + "\n\n");
			}
			out.write("endpackage");
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveOCL(String path, Set<String> consSet, String ecoreFileName, EPackage ePkg) {
		
		try {
			File file = new File(path);
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write("import 'platform:/resource/ClassModelSlicing/" + 
					"slices/" + ecoreFileName + "'\n\n\n");
			out.write("package " + ePkg.getName() + "\n\n\n");
			for (String cons: consSet) {
				out.write(cons + "\n\n");	
			}
			out.write("endpackage");
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 	Cannot use loadOCLinXMI to parse the constraints because class model cannot be bound to the ocl 
	 */
	public static void saveOCLinXMI(String path, Collection<Constraint> collection) {
		URI fileURI = URI.createFileURI(path);
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("*",
						new EcoreResourceFactoryImpl());
		Resource resource = resourceSet.createResource(fileURI);
		resource.getContents().addAll(collection);
		try {
			
			/**
			 * This adds an xsi:schemaLocation attribute to company.xml, associating the package's namespace 
			 * URI with the physical URI "xxx.ecore" (this relative URI is automatically computed, allowing 
			 * the two files to be relocated, together).
			 * It can be used to solve the PackageNotFoundException problem. 
			 */
			Map options = new HashMap();
			options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
			resource.save(options);
			
			//resource.save(null);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static List<EObject> loadOCL(String path, EPackage ePkg) {
		
		URI fileURI = URI.createFileURI(path);
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("*",
						new EcoreResourceFactoryImpl());
		
		resourceSet.getPackageRegistry().put(ePkg.getNsURI(), ePkg);
		EcoreOCLStandardLibrary oclstd = new EcoreOCLStandardLibrary();
		resourceSet.getPackageRegistry().put("http://www.eclipse.org/ocl/1.1.0/oclstdlib.ecore", 
				oclstd.getOCLStdLibPackage());
		Resource resource = resourceSet.getResource(fileURI, true);
	
		return resource.getContents();
	}
	
	/**
	 * Cannot use loadOCLinXMI to parse the constraints because class model cannot be bound to the ocl 
	 */
	public static List<EObject> loadOCLinXMI(String path, EPackage ePkg) {
		
		URI fileURI = URI.createFileURI(path);
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("*",
						new EcoreResourceFactoryImpl());
		
		resourceSet.getPackageRegistry().put(ePkg.getNsURI(), ePkg);
		EcoreOCLStandardLibrary oclstd = new EcoreOCLStandardLibrary();
		resourceSet.getPackageRegistry().put("http://www.eclipse.org/ocl/1.1.0/oclstdlib.ecore", 
				oclstd.getOCLStdLibPackage());
		Resource resource = resourceSet.getResource(fileURI, true);
	
		return resource.getContents();
	}
	
	public static void main(String[] args) {
		// Two types of Ecore model input path: 
		// 1. "input/UML2MM.ecore"
		// 2."D:\\EclipseWorkspaceForSlicing\\ClassModelSlicing\\input\\UML2MM.ecore"
		// Two types of Ecore output path:
		// 1. "UML2MM.ecore" 
		// 2. "D:\\EclipseWorkspaceForSlicing\\ClassModelSlicing\\input\\UML2MM.ecore"
		
		String basePath = "D:\\EclipseWorkspaceForSlicing\\ClassModelSlicing\\";
		String ecoreFileName = "JDTAST.ecore";
		String ecorePath = basePath + "ecores\\";
		String ecoreFilePath = ecorePath + ecoreFileName;
		String xmiPath = basePath + "xmis\\";
		String xmiFileName = ecoreFileName.replace(".ecore", ".xmi");
		String xmiFilePath = xmiPath + xmiFileName;
		
		Set<EObject> eObjs = new HashSet<EObject>();
		for (EPackage ePkg : EMFHelper.loadModelWithMultiplePackages(ecoreFilePath)) {
			EFactory eFactory = ePkg.getEFactoryInstance();
			for (EClassifier eClsf : ePkg.getEClassifiers()) {
				if (eClsf instanceof EClass) {
					EClass eCls = (EClass) eClsf;
					if (! eCls.isAbstract()) {
						EObject eObj = eFactory.create(eCls);
						eObjs.add(eObj);
					}
				}
			}
		}
		
		EMFHelper.saveInstance(xmiFilePath, eObjs);
	
		/*
		EPackage ePkg = EMFHelper.loadModel(ecoreFilePath);
		List<EObject> eObjs = EMFHelper.loadInstance(xmiFilePath, ePkg);
		for (EObject eObj : eObjs) {
			System.out.println(eObj);
			EClass eCls = eObj.eClass();
			
			for (EStructuralFeature eStf : eCls.getEStructuralFeatures()) {
				System.out.println(eObj.eGet(eStf));
			}
			
			List<EClass> eClss = eCls.getEAllSuperTypes();
			
			for (EClass cls : eClss) {
				for (EStructuralFeature eStf : cls.getEStructuralFeatures()) {
					System.out.println(eObj.eGet(eStf));
				}
			}
			
			System.out.println("");
		}
		*/
		// EMFHelper.saveModel(ecoreFilePath, ePkg);
	}
}
