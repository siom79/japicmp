def it = jApiClasses.iterator()
while (it.hasNext()) {
	def jApiClass = it.next()
	def fqn = jApiClass.getFullyQualifiedName()
	if (fqn.startsWith("japicmp.test.annotation")) {
		it.remove()
	}
	def methodIt = jApiClass.getMethods().iterator()
	while (methodIt.hasNext()) {
		def method = methodIt.next()
		if (method.getName().startsWith("get") || method.getName().startsWith("set")) {
			methodIt.remove()
		}
	}
}
return jApiClasses
