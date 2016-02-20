#Examples#

##Comparing two versions of the guava library##

In the following you see some of the differences between the versions 18.0 and 19.0 of Google's guava library.
The differences between the two Java APIs are also printed on the command line for a quick overview.
Please note that binary incompatible changes are flagged with an exclamation mark (!) while source incompatible changes
are marked with a trailing star (*).

```
**** MODIFIED INTERFACE: PUBLIC ABSTRACT com.google.common.collect.RangeMap  (not serializable)
	+++* NEW METHOD: PUBLIC(+) ABSTRACT(+) java.util.Map asDescendingMapOfRanges()
**** MODIFIED INTERFACE: PUBLIC ABSTRACT com.google.common.collect.RangeSet  (not serializable)
	+++* NEW METHOD: PUBLIC(+) ABSTRACT(+) java.util.Set asDescendingSetOfRanges()
***! MODIFIED CLASS: com.google.common.collect.RegularImmutableBiMap  (type of field has changed)
	***  MODIFIED FIELD: PRIVATE FINAL java.util.Map$Entry[] (<- com.google.common.collect.ImmutableMapEntry[]) entries
	+++  NEW FIELD: STATIC(+) FINAL(+) com.google.common.collect.RegularImmutableBiMap EMPTY
	---! REMOVED CONSTRUCTOR: RegularImmutableBiMap(java.util.Map$Entry[])
	---! REMOVED CONSTRUCTOR: RegularImmutableBiMap(int, com.google.common.collect.ImmutableMapEntry$TerminalEntry[])
	---! REMOVED CONSTRUCTOR: RegularImmutableBiMap(com.google.common.collect.ImmutableMapEntry$TerminalEntry[])
	+++  NEW CONSTRUCTOR: PRIVATE(+) RegularImmutableBiMap(com.google.common.collect.ImmutableMapEntry[], com.google.common.collect.ImmutableMapEntry[], java.util.Map$Entry[], int, int)
	+++  NEW METHOD: PRIVATE(+) STATIC(+) void checkNoConflictInValueBucket(java.lang.Object, java.util.Map$Entry, com.google.common.collect.ImmutableMapEntry)
	---  REMOVED METHOD: PRIVATE(-) STATIC(-) com.google.common.collect.ImmutableMapEntry[] createEntryArray(int)
	+++  NEW METHOD: STATIC(+) com.google.common.collect.RegularImmutableBiMap fromEntries(java.util.Map$Entry[])
	+++  NEW METHOD: STATIC(+) com.google.common.collect.RegularImmutableBiMap fromEntryArray(int, java.util.Map$Entry[])
	+++  NEW METHOD: PUBLIC(+) int hashCode()
	+++  NEW METHOD: boolean isHashCodeFast()
...
```

Optionally japicmp can also create an HTML report.
An example for such a report can be found [here](http://htmlpreview.github.io/?https://github.com/siom79/japicmp/blob/master/doc/japicmp_guava.html):

<img src="https://raw.github.com/siom79/japicmp/master/doc/japicmp_guava.png" alt="HTML Report"></img>

You can also let japicmp create an XML report like the following one:

```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<japicmp xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" accessModifier="PRIVATE" creationTimestamp="2016-02-19T14:36:10.066+0100" ignoreMissingClasses="false" newJar="C:\MCP-IDE\repository\com\google\guava\guava\19.0\guava-19.0.jar" oldJar="C:\MCP-IDE\repository\com\google\guava\guava\18.0\guava-18.0.jar" onlyBinaryIncompatibleModifications="false" onlyModifications="true" packagesExclude="n.a." packagesInclude="all" semanticVersioning="1.0.0" title="JApiCmp-Report" xsi:noNamespaceSchemaLocation="japicmp.xsd">
    <classes>
        <class binaryCompatible="false" changeStatus="MODIFIED" fullyQualifiedName="com.google.common.base.CharMatcher" javaObjectSerializationCompatible="NOT_SERIALIZABLE" javaObjectSerializationCompatibleAsString="not serializable" sourceCompatible="false">
            <annotations/>
            <attributes>
                <attribute changeStatus="UNCHANGED" newValue="NON_SYNTHETIC" oldValue="NON_SYNTHETIC"/>
            </attributes>
            <classType changeStatus="UNCHANGED" newType="CLASS" oldType="CLASS"/>
            <compatibilityChanges/>
            <constructors>
                <constructor binaryCompatible="false" changeStatus="REMOVED" name="CharMatcher" newLineNumber="n.a." oldLineNumber="600" sourceCompatible="false">
                    <annotations/>
                    <attributes>
                        <attribute changeStatus="REMOVED" newValue="n.a." oldValue="NON_SYNTHETIC"/>
                    </attributes>
                    <compatibilityChanges>
                        <compatibilityChange>CONSTRUCTOR_REMOVED</compatibilityChange>
                    </compatibilityChanges>
                    <modifiers>
                        <modifier changeStatus="REMOVED" newValue="n.a." oldValue="NON_FINAL"/>
                        <modifier changeStatus="REMOVED" newValue="n.a." oldValue="NON_STATIC"/>
                        <modifier changeStatus="REMOVED" newValue="n.a." oldValue="PACKAGE_PROTECTED"/>
                        <modifier changeStatus="REMOVED" newValue="n.a." oldValue="NON_ABSTRACT"/>
                        <modifier changeStatus="REMOVED" newValue="n.a." oldValue="NON_BRIDGE"/>
                        <modifier changeStatus="REMOVED" newValue="n.a." oldValue="NON_SYNTHETIC"/>
                    </modifiers>
                    <parameters>
                        <parameter type="java.lang.String"/>
                    </parameters>
                </constructor>
            </constructors>
			...
```

##Tracking changes of an XML document marshalled with JAXB##

The following output shows the changes of a model class with some JAXB bindings:

	***  MODIFIED CLASS: PUBLIC japicmp.test.jaxb.SimpleDocument
		***  MODIFIED METHOD: PUBLIC java.lang.String getTitle()
			---  REMOVED ANNOTATION: javax.xml.bind.annotation.XmlAttribute
			+++  NEW ANNOTATION: javax.xml.bind.annotation.XmlElement
		***  MODIFIED METHOD: PUBLIC java.lang.String getAuthor()
			---  REMOVED ANNOTATION: javax.xml.bind.annotation.XmlAttribute
			+++  NEW ANNOTATION: javax.xml.bind.annotation.XmlElement
		***  MODIFIED ANNOTATION: javax.xml.bind.annotation.XmlRootElement
			***  MODIFIED ELEMENT: name=document (<- simpleDocument)

As can bee seen from the output above, the XML attributes title and author have changed to an XML element. The name of the XML root element has also changed from "simpleDocument" to "document".
