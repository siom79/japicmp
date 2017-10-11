package japicmp.filter;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Filters {
	private static final Logger LOGGER = Logger.getLogger(Filters.class.getName());
	private final List<Filter> includes = new ArrayList<>();
	private final List<Filter> excludes = new ArrayList<>();

	public List<Filter> getIncludes() {
		return includes;
	}

	public List<Filter> getExcludes() {
		return excludes;
	}

	public boolean includeClass(CtClass ctClass) {
		String name = ctClass.getName();
		for (Filter filter : excludes) {
			if (filter instanceof ClassFilter) {
				ClassFilter classFilter = (ClassFilter) filter;
				if (classFilter.matches(ctClass)) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.log(Level.FINE, "Excluding class '" + name + "' because class filter '" + filter + "' matches.");
					}
					return false;
				}
			}
		}
		int includeCount = 0;
		for (Filter filter : includes) {
			includeCount++;
			if (filter instanceof BehaviorFilter) {
				BehaviorFilter behaviorFilter = (BehaviorFilter) filter;
				CtMethod[] methods = ctClass.getDeclaredMethods();
				for (CtMethod method : methods) {
					if (behaviorFilter.matches(method)) {
						return true;
					}
				}
				CtConstructor[] constructors = ctClass.getDeclaredConstructors();
				for (CtConstructor constructor : constructors) {
					if (behaviorFilter.matches(constructor)) {
						return true;
					}
				}
			} else if (filter instanceof FieldFilter) {
				FieldFilter fieldFilter = (FieldFilter) filter;
				CtField[] fields = ctClass.getDeclaredFields();
				for (CtField field : fields) {
					if (fieldFilter.matches(field)) {
						return true;
					}
				}
			} else {
				ClassFilter classFilter = (ClassFilter) filter;
				if (classFilter.matches(ctClass)) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.log(Level.FINE, "Including class '" + name + "' because class filter '" + filter + "' matches.");
					}

					return true;
				}
			}
		}
		if (includeCount > 0) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "Excluding class '" + name + "' because no include matched.");
			}
			return false;
		}
		return true;
	}

	public boolean includeBehavior(CtBehavior ctMethod) {
		for (Filter filter : excludes) {
			if (filter instanceof BehaviorFilter) {
				BehaviorFilter behaviorFilter = (BehaviorFilter) filter;
				if (behaviorFilter.matches(ctMethod)) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.log(Level.FINE, "Excluding method '" + ctMethod.getLongName() + "' because exclude method filter did match.");
					}
					return false;
				}
			}
		}
		int includesCount = 0;
		for (Filter filter : includes) {
			if (filter instanceof BehaviorFilter) {
				includesCount++;
				BehaviorFilter behaviorFilter = (BehaviorFilter) filter;
				if (behaviorFilter.matches(ctMethod)) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.log(Level.FINE, "Including method '" + ctMethod.getLongName() + "' because include method filter matched.");
					}
					return true;
				}
			}
		}
		if (includesCount > 0) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "Excluding method '" + ctMethod.getLongName() + "' because no include matched.");
			}
			return false;
		}
		return true;
	}

	public boolean includeField(CtField ctField) {
		for (Filter filter : excludes) {
			if (filter instanceof FieldFilter) {
				FieldFilter fieldFilter = (FieldFilter) filter;
				if (fieldFilter.matches(ctField)) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.log(Level.FINE, "Excluding field '" + ctField.getName() + "' because exclude field filter did match.");
					}
					return false;
				}
			}
		}
		int includesCount = 0;
		for (Filter filter : includes) {
			if (filter instanceof FieldFilter) {
				FieldFilter fieldFilter = (FieldFilter) filter;
				includesCount++;
				if (fieldFilter.matches(ctField)) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.log(Level.FINE, "Including field '" + ctField.getName() + "' because include field filter matched.");
					}
					return true;
				}
			}
		}
		if (includesCount > 0) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "Excluding field '" + ctField.getName() + "' because no include matched.");
			}
			return false;
		}
		return true;
	}
}
