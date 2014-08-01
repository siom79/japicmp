package japicmp.model;

public interface JApiHasModifier {

	JApiModifier<AccessModifier> getAccessModifier();

	String getAccessModifierOld();

	String getAccessModifierNew();

	JApiModifier<FinalModifier> getFinalModifier();

	String getFinalModifierOld();

	String getFinalModifierNew();

	JApiModifier<StaticModifier> getStaticModifier();

	String getStaticModifierOld();

	String getStaticModifierNew();
	
	JApiModifier<AbstractModifier> getAbstractModifier();

	String getAbstractModifierOld();

	String getAbstractModifierNew();
}
