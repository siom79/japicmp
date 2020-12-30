def it = jApiClasses.iterator()
while (it.hasNext()) {
	def jApiClass = it.next()
	def fqn = jApiClass.getFullyQualifiedName()
	if (fqn == "japicmp.test.annotation") {
		it.remove()
	}
	def methodIt = jApiClass.getMethods().iterator()
	while (methodIt.hasNext()) {
		def method = methodIt.next()
		if (method.getName() == "get" || method.getName() == "set") {
			methodIt.remove()
		}
	}
}
return jApiClasses
