package japicmp.model;

public interface JApiHasModifier {

	JApiModifier<AccessModifier> getAccessModifier();

	JApiModifier<FinalModifier> getFinalModifier();

	JApiModifier<StaticModifier> getStaticModifier();
	
	JApiModifier<AbstractModifier> getAbstractModifier();
}
