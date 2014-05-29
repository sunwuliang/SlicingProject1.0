The slicing project is used to slice a .ecore class model and a .xmi object configuration based on a single OCL invariant or multiple OCL invariants. 

Below is a descrition of a list of folders/files in the project:  
1. ecores folder includes a list of .ecore class models;
2. ocls folder includes a list of .ocl files;
3. xmis folder includes a list of .xmi object configuraitons;
4. slices folder includes a list of slicing results including sliced class models, object configurations, and invariants;
5. src folder includes the Java source files for the slicing project. 
6. Proposal.pdf is a research report about the technical details of the slicing project. 

The src.org.csu.slicing package has three subpackages: instance, main, and util.
The entryo pint of the project is in the main package, specifiically the Coslicer class.
In the main package, the Slicer class is used to slice a .ecore class model, the Coslicer class is used to sliced a .xmi object configurations, and the Evaluator class is used to check if an (sliced) object configuration is consistent with a (sliced) class model with one or many invariants. 
In the instance package, InsGenerator is used to generate random instances of the UML2 class model.  
In the util package, EMFHelper is used to load/save .ecore, .ocl and .xmi files, InvPrePostAnalzyer uses RefModelElmtsVisitor to identify the class model elements that are referenced by one or many invariants, RefModelElmtsVisitor implemetns the AbstractVisitor class from the Eclipse OCL project,  SizeCalculator is used to measure the size of class models (in terms of number of classes, attributes, etc. ) and object configurations (in terms of objects, links, and slots), and UML2Invs uses a HashMap to store the OCL invariants defined in the UML2 class model.
