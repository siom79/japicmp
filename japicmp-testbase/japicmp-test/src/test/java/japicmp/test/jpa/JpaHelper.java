package japicmp.test.jpa;

import japicmp.output.extapi.jpa.model.JpaTable;

import java.util.List;

public class JpaHelper {

	public static JpaTable getEntityByName(List<JpaTable> entities, String name) {
		for (JpaTable jpaTable : entities) {
			if (jpaTable.getFullyQualifiedName().equals(name)) {
				return jpaTable;
			}
		}
		throw new IllegalStateException("Could not find entity with name '" + name + "'.");
	}
}
