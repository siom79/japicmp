package groovy

def it = jApiClasses.iterator()
while (it.hasNext()) {
    def jApiClass = it.next()
    def fqn = jApiClass.getFullyQualifiedName()
    if (fqn.startsWith("japicmp")) {
        it.remove()
    }
}
return
