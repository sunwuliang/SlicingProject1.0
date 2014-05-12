package org.csu.slicing.util;

import java.util.HashMap;
import java.util.Map;

public class UML2Invs {
	
	private static Map<String, String> consMap;
	
	public static Map<String, String> getConsMap() {
		
		if (consMap != null) 
			return consMap;
		
		consMap = new HashMap<String, String>();
		
		// From UMLclasses
		consMap.put("mustBeOwnedHasOwner", "context Element inv mustBeOwnedHasOwner: self.owner->notEmpty()");
		consMap.put("ownedElementHasVisibility", "context Package inv ownedElementHasVisibility: " +
				"self.member->forAll(e:NamedElement| e.visibility->notEmpty() implies e.visibility " + 
				"= VisibilityKind::public or e.visibility = VisibilityKind::private)");
		consMap.put("visibilityValid", "context ElementImport inv visibilityValid: " + 
				"self.visibility = VisibilityKind::public or self.visibility = VisibilityKind::private");
		consMap.put("importedVisibilityValid", "context ElementImport inv importedVisibilityValid: " + 
				"not self.importedElement.visibility.oclIsUndefined() implies self.importedElement.visibility " + 
				"= VisibilityKind::public");
		consMap.put("packageVisibilityValid", "context PackageImport inv packageVisibilityValid: self.visibility = " +
				"VisibilityKind::public or self.visibility = VisibilityKind::private");
		consMap.put("notConstrainingSelf", "context Constraint inv notConstrainingSelf: self.constrainedElement->excludes(self)");
		consMap.put("specializedAssociationsHasSameNumberOfEnds", "context Association inv specializedAssociationsHasSameNumberOfEnds: " + 
				"self.generalization.general->asSet()->forAll(p : Classifier | p.oclAsType(Association).memberEnd->size() = self.memberEnd->size()) ");
		consMap.put("onlyBinaryAssociationCanBeAggregations", "context Association inv onlyBinaryAssociationCanBeAggregations: " + 
				"self.memberEnd->exists(temp1:Property | temp1.aggregation <> AggregationKind::none) implies self.memberEnd->size() = 2");
		consMap.put("nAryAssociationsOwnTheirEnds", "context Association inv nAryAssociationsOwnTheirEnds: if self.memberEnd->size() > 2" +  
				"then self.ownedEnd->includesAll(self.memberEnd) else true endif");
		consMap.put("isAbstractDefined", "context Classifier inv isAbstractDefined: not self.isAbstract.oclIsUndefined()");
		consMap.put("inheritedMemberIsValid", "context Classifier inv inheritedMemberIsValid: self.inheritedMember->includesAll( " + 
				"self.generalization.general->asSet()->collect(p | p.member->select(m | if self.inheritedMember->includes(m) then " + 
				"m.visibility <> VisibilityKind::private else true endif ) ) )");
		consMap.put("sameGeneralClassifier", "context GeneralizationSet inv sameGeneralClassifier: self.generalization.general->asSet()->size() <= 1");
		consMap.put("languageAndBodiesAreValid", "context OpaqueExpression  inv languageAndBodiesAreValid: self.language->notEmpty() " +
				"implies (self.bodies->size() = self.language->size())");
		consMap.put("oe1", "context OpaqueExpression inv oe1: self.behavior->notEmpty() implies (self.behavior.ownedParameterSet->select( " + 
				"p : Parameter | p.direction <> ParameterDirectionKind::return)->isEmpty())");
		consMap.put("oe2", "context OpaqueExpression inv oe2: self.behavior->notEmpty() implies (self.behavior.ownedParameterSet->select( " + 
				"p : Parameter | p.direction = ParameterDirectionKind::return)->size() = 1)");
		consMap.put("derivedUnionIsDerived", "context Property inv derivedUnionIsDerived: self.isDerivedUnion implies self.isDerived");
		consMap.put("derivedUnionIsReadOnly", "context Property inv derivedUnionIsReadOnly: self.isDerivedUnion implies self.isReadOnly");
		consMap.put("isCompositeIsValid", "context Property inv isCompositeIsValid: self.isComposite = (self.aggregation = AggregationKind::composite)");
		consMap.put("subsetRequiresDifferentName", "context Property inv subsetRequiresDifferentName: self.subsettedProperty->notEmpty() " +  
				"implies self.subsettedProperty->forAll(sp: Property | sp.name <> self.name)");
		consMap.put("operationHasOnlyOneReturnParameter", "context Operation inv operationHasOnlyOneReturnParameter: " +
				"self.ownedParameter->select(par:Parameter | par.direction = ParameterDirectionKind::return)->size() <= 1");
		consMap.put("bodyConditionOnlyIfIsQuery", "context Operation inv bodyConditionOnlyIfIsQuery: self.bodyCondition->notEmpty() implies self.isQuery");
		consMap.put("isOrderedIsValid", "context Operation inv isOrderedIsValid: self.isOrdered = ( " + 
				"if self.ownedParameter->select(par | par.direction = ParameterDirectionKind::return)->asSet()->notEmpty() then " +  
				"self.ownedParameter->select(par | par.direction = ParameterDirectionKind::return)->asSet()->any(true).isOrdered else false endif)");
		consMap.put("isUniqueIsValid", "context Operation inv isUniqueIsValid: self.isUnique = ( " + 
				"if self.ownedParameter->select(par | par.direction = ParameterDirectionKind::return)->asSet()->notEmpty() then " +
				"self.ownedParameter->select(par | par.direction = ParameterDirectionKind::return)->asSet()->any(true).isUnique else true endif)");
		consMap.put("lowerIsValid", "context Operation inv lowerIsValid: self.lower = ( " + 
				"if self.ownedParameter->select(par | par.direction = ParameterDirectionKind::return)->asSet()->notEmpty() then " + 
				"self.ownedParameter->select(par | par.direction = ParameterDirectionKind::return)->asSet()->any(true).lower else null endif)");
		consMap.put("upperIsValid", "context Operation inv upperIsValid: self.upper = ( " + 
		 		"if self.ownedParameter->select(par | par.direction = ParameterDirectionKind::return)->asSet()->notEmpty() then " + 
				"self.ownedParameter->select(par | par.direction = ParameterDirectionKind::return)->asSet()->any(true).upper else null endif)");
		consMap.put("typeIsValid", "context Operation inv typeIsValid: self.type = ( " +
				"if self.ownedParameter->select(par | par.direction = ParameterDirectionKind::return)->asSet()->notEmpty() then " +
				"self.ownedParameter->select(par | par.direction = ParameterDirectionKind::return)->asSet()->any(true).type else null endif)");
		consMap.put("class1", "context Class inv class1: not self.isActive implies self.ownedReception->isEmpty()");
		consMap.put("bc1", "context BehavioredClassifier inv bc1: not self.classifierBehavior.oclIsUndefined() implies " + 
				"self.ownedBehavior->forAll(temp1 : Behavior | temp1.specification.oclIsUndefined())");
		consMap.put("interfaceFeaturesArePublic", "context Interface inv interfaceFeaturesArePublic: self.feature->forAll(f:Feature | " +
				" f.visibility = VisibilityKind::public)");
		consMap.put("definingFeaturIsFeatureOfClassifier", "context InstanceSpecification inv definingFeaturIsFeatureOfClassifier: " + 
				"self.slot->forAll(s:Slot | self.classifier->exists(c:Classifier | " + 
				" c.member->select(oclIsKindOf(Feature)).oclAsType(Feature)->asSet()->includes(s.definingFeature)))");
		consMap.put("oneStructuralFeatureDefinesAtMostOneSlotPerInstance", "context InstanceSpecification inv " + 
				"oneStructuralFeatureDefinesAtMostOneSlotPerInstance: self.classifier->forAll(c:Classifier| " + 
				"c.member->select(oclIsKindOf(Feature)).oclAsType(Feature)->asSet()->forAll(f:Feature| self.slot->select(s:Slot | " + 
				" s.definingFeature = f)->size() <= 1))");
		consMap.put("noNestedClassifiers", "context Component inv noNestedClassifiers: self.nestedClassifier->isEmpty()");

		// From UMLactions
		consMap.put("coa1", "context CreateObjectAction inv coa1: not self.classifier.isAbstract");
		consMap.put("coa2", "context CreateObjectAction inv coa2: not self.classifier.oclIsKindOf(AssociationClass)");
		consMap.put("coa3", "context CreateObjectAction inv coa3: self.result.type = self.classifier");
		consMap.put("doa1", "context DestroyObjectAction inv doa1: self.target.type->size() = 0 ");
		consMap.put("tia1", "context TestIdentityAction inv tia1: self.first.type->size() = 0 and self.second.type->size() = 0");
		consMap.put("rsa1", "context ReadSelfAction inv rsa1: self.context_->size() = 1");
		consMap.put("rsa2", "context ReadSelfAction inv rsa2: self.result.type = context_ ");
		consMap.put("sfa1", "context StructuralFeatureAction inv sfa1: not self.structuralFeature.isStatic");
		consMap.put("sfa2", "context StructuralFeatureAction inv sfa2: self.structuralFeature.featuringClassifier->size() = 1");
		consMap.put("wsfa1", "context WriteStructuralFeatureAction inv wsfa1: self.value.type->asSet() = self.structuralFeature.featuringClassifier");
		consMap.put("led1", "context LinkEndData inv led1: self.end.association->size() = 1");
		consMap.put("led2", "context LinkEndData inv led2: self.value.type = self.end.type");
		consMap.put("led3", "context LinkEndData inv led3: self.qualifier->collect(temp1: QualifierValue | temp1.qualifier) = self.end.qualifier->asBag()");

		return consMap;
	}
	
	public static void main(String[] args) {
		for (String consName : UML2Invs.getConsMap().keySet()) {
			System.out.println(consName);
		}
	}

}
