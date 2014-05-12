package org.csu.slicing.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.ocl.ecore.PrimitiveType;
import org.eclipse.ocl.ecore.SetType;
import org.eclipse.ocl.expressions.AssociationClassCallExp;
import org.eclipse.ocl.expressions.BooleanLiteralExp;
import org.eclipse.ocl.expressions.CollectionItem;
import org.eclipse.ocl.expressions.CollectionLiteralExp;
import org.eclipse.ocl.expressions.CollectionRange;
import org.eclipse.ocl.expressions.EnumLiteralExp;
import org.eclipse.ocl.expressions.IfExp;
import org.eclipse.ocl.expressions.IntegerLiteralExp;
import org.eclipse.ocl.expressions.InvalidLiteralExp;
import org.eclipse.ocl.expressions.IterateExp;
import org.eclipse.ocl.expressions.IteratorExp;
import org.eclipse.ocl.expressions.LetExp;
import org.eclipse.ocl.expressions.MessageExp;
import org.eclipse.ocl.expressions.NavigationCallExp;
import org.eclipse.ocl.expressions.NullLiteralExp;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.expressions.RealLiteralExp;
import org.eclipse.ocl.expressions.StateExp;
import org.eclipse.ocl.expressions.StringLiteralExp;
import org.eclipse.ocl.expressions.TupleLiteralExp;
import org.eclipse.ocl.expressions.TupleLiteralPart;
import org.eclipse.ocl.expressions.TypeExp;
import org.eclipse.ocl.expressions.UnlimitedNaturalLiteralExp;
import org.eclipse.ocl.expressions.UnspecifiedValueExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.utilities.AbstractVisitor;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.ocl.utilities.Visitable;

/*
 * Referenced Model Element Analyzer: a visitor that identifies the class model
 * elements that are referenced by the given OCL statement.
 */

public class RefModelElmtVisitor extends AbstractVisitor{
	
	private static RefModelElmtVisitor instance;
	
	Set<Object> RefModelElmts; 
	
	private RefModelElmtVisitor () {
		RefModelElmts = new HashSet<Object>();
	}
	
	public static RefModelElmtVisitor getInstance() {
		if (instance == null)
			return new RefModelElmtVisitor();
		return instance;
	}
	public Set<Object> getRefModelElmts() {
		return this.RefModelElmts;
	}

	@Override
	public Object visitAssociationClassCallExp(AssociationClassCallExp callExp) {
		// TODO Auto-generated method stub
		return super.visitAssociationClassCallExp(callExp);
	}

	@Override
	public Object visitBooleanLiteralExp(BooleanLiteralExp literalExp) {
		// TODO Auto-generated method stub
		return super.visitBooleanLiteralExp(literalExp);
	}

	@Override
	public Object visitCollectionItem(CollectionItem item) {
		// TODO Auto-generated method stub
		return super.visitCollectionItem(item);
	}

	@Override
	public Object visitCollectionLiteralExp(CollectionLiteralExp literalExp) {
		// TODO Auto-generated method stub
		return super.visitCollectionLiteralExp(literalExp);
	}

	@Override
	public Object visitCollectionRange(CollectionRange range) {
		// TODO Auto-generated method stub
		return super.visitCollectionRange(range);
	}

	@Override
	public Object visitConstraint(Object constraint) {
		// TODO Auto-generated method stub
		return super.visitConstraint(constraint);
	}

	@Override
	public Object visitEnumLiteralExp(EnumLiteralExp literalExp) {
		// TODO Auto-generated method stub
		return super.visitEnumLiteralExp(literalExp);
	}

	@Override
	public Object visitExpressionInOCL(ExpressionInOCL expression) {
		// TODO Auto-generated method stub
		
		return super.visitExpressionInOCL(expression);
	}

	@Override
	public Object visitIfExp(IfExp ifExp) {
		// TODO Auto-generated method stub
		return super.visitIfExp(ifExp);
	}

	@Override
	public Object visitIntegerLiteralExp(IntegerLiteralExp literalExp) {
		// TODO Auto-generated method stub
		return super.visitIntegerLiteralExp(literalExp);
	}

	@Override
	public Object visitInvalidLiteralExp(InvalidLiteralExp literalExp) {
		// TODO Auto-generated method stub
		return super.visitInvalidLiteralExp(literalExp);
	}

	@Override
	public Object visitIterateExp(IterateExp callExp) {
		// TODO Auto-generated method stub
		
		return super.visitIterateExp(callExp);
	}

	@Override
	public Object visitIteratorExp(IteratorExp callExp) {
		// TODO Auto-generated method stub
		
		return super.visitIteratorExp(callExp);
	}

	@Override
	public Object visitLetExp(LetExp letExp) {
		// TODO Auto-generated method stub
		return super.visitLetExp(letExp);
	}

	@Override
	public Object visitMessageExp(MessageExp messageExp) {
		//System.out.println("Processing OperationCallExp : " + messageExp.getName() + " toString: " +  messageExp.toString());
		return super.visitMessageExp(messageExp);
	}

	@Override
	public Object visitNullLiteralExp(NullLiteralExp literalExp) {
		// TODO Auto-generated method stub
		return super.visitNullLiteralExp(literalExp);
	}

	@Override
	public Object visitOperationCallExp(OperationCallExp callExp) {
		//System.out.println("Processing OperationCallExp : " + callExp.getName() + " toString: " +  callExp.toString());
		
		EOperation eOper = (EOperation)callExp.getReferredOperation();
		// org.eclipse.emf.ecore.impl.EClassImpl@21e85538 (name: Set(T)_Class) (instanceClassName: null) (abstract: false, interface: false)
		// org.eclipse.emf.ecore.impl.EOperationImpl@1c32e0ec (name: asSet) (ordered: true, unique: true, lowerBound: 0, upperBound: 1)
		// Adhoc solution
		if (eOper.getEContainingClass().getName().indexOf("_Class") == -1) {
			this.RefModelElmts.add(eOper);
			//System.out.println(eOper);
		}
		
		return super.visitOperationCallExp(callExp);
	}

	@Override
	public Object visitPropertyCallExp(PropertyCallExp callExp) {
		//System.out.println("Processing PropertyCallExp : " + callExp.getName() + " toString: " +  callExp.toString());
		//System.out.println(callExp.getReferredProperty());
		
		Object obj = callExp.getReferredProperty();
		this.RefModelElmts.add(obj);
		
		if (obj instanceof EAttribute) {
			EAttribute eAttri = (EAttribute)obj;
			/*
			 * Bug: an invariant that is defined in the context of a subclass may
			 * reference an attribute of its super class
			 * Fix: eAttri.getEContainingClass();
			 */
			this.RefModelElmts.add(eAttri.getEContainingClass());
			
			// Add referenced enumeration
			if (eAttri.getEAttributeType() instanceof EEnum) {
				this.RefModelElmts.add(eAttri.getEAttributeType());				
			}
			
		} else {		
			if (obj instanceof EReference) {
				EReference eRef = (EReference)obj;
				/*
				 * Bug: an invariant that is defined in the context of a subclass may
				 * reference a reference of its super class
				 * Fix: eRef.getEContainingClass();
				 */
				
				this.RefModelElmts.add(eRef.getEContainingClass());
				/*
		         * Bug: the referenced model elements include references, 
		         * but fail to include the type (class) of the references.
		         * Fix: eRef.getEType();	
		         * 
		         */
				this.RefModelElmts.add(eRef.getEType());
			}
		}
		//this.RefModelElmts.add(callExp.getReferredProperty().)
		return super.visitPropertyCallExp(callExp);
	}

	@Override
	public Object visitRealLiteralExp(RealLiteralExp literalExp) {
		// TODO Auto-generated method stub
		return super.visitRealLiteralExp(literalExp);
	}

	@Override
	public Object visitStateExp(StateExp stateExp) {
		// TODO Auto-generated method stub
		return super.visitStateExp(stateExp);
	}

	@Override
	public Object visitStringLiteralExp(StringLiteralExp literalExp) {
		// TODO Auto-generated method stub
		return super.visitStringLiteralExp(literalExp);
	}

	@Override
	public Object visitTupleLiteralExp(TupleLiteralExp literalExp) {
		// TODO Auto-generated method stub
		return super.visitTupleLiteralExp(literalExp);
	}

	@Override
	public Object visitTupleLiteralPart(TupleLiteralPart part) {
		// TODO Auto-generated method stub
		return super.visitTupleLiteralPart(part);
	}

	@Override
	public Object visitTypeExp(TypeExp t) {
		//System.out.println("Processing TypeExp : " + t.getName() + " " + t.toString() + " " + t.getReferredType());
		
		this.RefModelElmts.add(t.getReferredType());
		return super.visitTypeExp(t);
	}

	@Override
	public Object visitUnlimitedNaturalLiteralExp(
			UnlimitedNaturalLiteralExp literalExp) {
		// TODO Auto-generated method stub
		return super.visitUnlimitedNaturalLiteralExp(literalExp);
	}

	@Override
	public Object visitUnspecifiedValueExp(UnspecifiedValueExp unspecExp) {
		// TODO Auto-generated method stub
		return super.visitUnspecifiedValueExp(unspecExp);
	}

	@Override
	public Object visitVariable(Variable variable) {
		//System.out.println("Processing Variable : " + variable.getName() + " vtype : " + variable.getType());
		
		 // Potential bugs: variable.getType() instanceof BagType, OrderedSetType, etc.
		 
		this.RefModelElmts.add(variable.getType());
		
		return super.visitVariable(variable);
	}

	@Override
	public Object visitVariableExp(VariableExp v) {
		//System.out.println("Processing VariableExp : " + v.getName() + " vtype : " + v.getType().toString());
				
		/**
		 * Bug: it may include the primitive types. E.g., updateUserID(id : String)
		 * Fix: exclude the primitive types
		 * Potential bugs: BagType, OrderedSetType, etc.
		 */
		if (v.getType() instanceof PrimitiveType) {
			//System.out.println(v.getType());	
			//if (v.getType() instanceof SetType) {
			//	SetType st = (SetType)v.getType();
				// Potential bug: what if st.getElementType instanceof PrimitiveType
			//	this.RefModelElmts.add(st.getElementType());
			//}
		} else {
			this.RefModelElmts.add(v.getType());
		}
		return super.visitVariableExp(v);
	}
}
