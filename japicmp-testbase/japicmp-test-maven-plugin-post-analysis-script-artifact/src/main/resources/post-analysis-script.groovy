def it = jApiClasses.iterator()
while (it.hasNext()) {
	def jApiClass = it.next()
	def fqn = jApiClass.getFullyQualifiedName()
	if (fqn == "japicmp.test.AbstractModifier") {
		it.remove()
	}
}
return jApiClasses
