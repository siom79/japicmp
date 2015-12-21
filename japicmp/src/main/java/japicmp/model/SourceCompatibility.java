package japicmp.model;


public class SourceCompatibility {


	/**
	 * check if source is compatible, for now it is just a basic check , it is :
	 * Binary Compatible
	 * no removal allowed
	 * on interface no removal of methods ( this is may be included in binary compability)
	 * on interface no additionnal methods if no default implementation done
	 * -No unit test for this as the project is java 7. but it works :)
     */
	public static boolean sourceCompatible(JApiClass jApiClass)
	{
		JApiClassType classType = jApiClass.getClassType();
		if (jApiClass.isBinaryCompatible() == false)
			return false;

		if (jApiClass.getChangeStatus() == JApiChangeStatus.REMOVED)
		{return false;}

		if ( (jApiClass.getChangeStatus() == JApiChangeStatus.UNCHANGED) || (jApiClass.getChangeStatus() == JApiChangeStatus.NEW) )
		{return true;}

		if (  (classType.getNewTypeOptional().get() ==  classType.getOldTypeOptional().get() )
			&& (classType.getNewTypeOptional().get() == JApiClassType.ClassType.INTERFACE )  ) {
			for (JApiMethod currentMethod : jApiClass.getMethods())
			{
				if (currentMethod.getChangeStatus() == JApiChangeStatus.REMOVED)
				{
					return  false;
				}
				else
				if (currentMethod.getChangeStatus() == JApiChangeStatus.NEW)
				{
					if (currentMethod.getNewMethod().get().isEmpty() == true)
					{
						return false;
					}
				}
			}
		}
		return true;
	}
}
