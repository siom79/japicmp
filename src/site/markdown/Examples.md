#Examples#

##Comparing two versions of the guava library##

In the following you see the beginning of the differences between the versions 16.0 and 17.0 of Google's guava library. The differences between the two Java APIs are also printed on the command line for a quick overview. Please note that binary incompatible changes are flagged with an exclamation mark.

	***! MODIFIED CLASS: PUBLIC FINAL com.google.common.base.Stopwatch
		***! MODIFIED CONSTRUCTOR: PACKAGE_PROTECTED (<- PUBLIC) Stopwatch()
			===  UNCHANGED ANNOTATION: java.lang.Deprecated
		***! MODIFIED CONSTRUCTOR: PACKAGE_PROTECTED (<- PUBLIC) Stopwatch(com.google.common.base.Ticker)
			===  UNCHANGED ANNOTATION: java.lang.Deprecated
	***! MODIFIED INTERFACE: PUBLIC ABSTRACT com.google.common.util.concurrent.Service
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.Service$State startAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.Service$State stopAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.ListenableFuture start()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.ListenableFuture stop()
			---  REMOVED ANNOTATION: java.lang.Deprecated
	***  MODIFIED CLASS: PUBLIC FINAL com.google.common.net.HttpHeaders
		+++  NEW FIELD: PUBLIC(+) STATIC(+) FINAL(+) java.lang.String FOLLOW_ONLY_WHEN_PRERENDER_SHOWN
	***! MODIFIED CLASS: PUBLIC ABSTRACT com.google.common.util.concurrent.AbstractScheduledService
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.ListenableFuture start()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.Service$State startAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.Service$State stopAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.ListenableFuture stop()
			---  REMOVED ANNOTATION: java.lang.Deprecated
	...

Optionally japicmp can also create an HTML report. An example for such a report can be found [here](http://htmlpreview.github.io/?https://github.com/siom79/japicmp/blob/master/doc/japicmp_guava.html):

<img src="https://raw.github.com/siom79/japicmp/master/doc/japicmp_guava.png" alt="HTML Report"></img>

You can also let japicmp create an XML report like the following one:

	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<japicmp newJar="/home/siom79/dev/guava-17.0.jar" oldJar="/home/siom79/dev/guava-16.0.jar">
		<classes>
			<class binaryCompatible="false" changeStatus="MODIFIED" fullyQualifiedName="com.google.common.base.Stopwatch" type="CLASS">
				<annotations/>
				<attributes>
					<attribute changeStatus="UNCHANGED" newValue="NON_SYNTHETIC" oldValue="NON_SYNTHETIC"/>
				</attributes>
				<constructors>
					<constructor binaryCompatible="false" changeStatus="MODIFIED" name="Stopwatch">
						<annotations>
							<annotation fullyQualifiedName="java.lang.Deprecated">
								<elements/>
							</annotation>
						</annotations>
						<attributes>
							<attribute changeStatus="UNCHANGED" newValue="NON_SYNTHETIC" oldValue="NON_SYNTHETIC"/>
						</attributes>
						<modifiers>
							<modifier changeStatus="UNCHANGED" newValue="NON_FINAL" oldValue="NON_FINAL"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_STATIC" oldValue="NON_STATIC"/>
							<modifier changeStatus="MODIFIED" newValue="PACKAGE_PROTECTED" oldValue="PUBLIC"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_ABSTRACT" oldValue="NON_ABSTRACT"/>
						</modifiers>
						<parameters/>
					</constructor>
					<constructor binaryCompatible="false" changeStatus="MODIFIED" name="Stopwatch">
						<annotations>
							<annotation fullyQualifiedName="java.lang.Deprecated">
								<elements/>
							</annotation>
						</annotations>
						<attributes>
							<attribute changeStatus="UNCHANGED" newValue="NON_SYNTHETIC" oldValue="NON_SYNTHETIC"/>
						</attributes>
						<modifiers>
							<modifier changeStatus="UNCHANGED" newValue="NON_FINAL" oldValue="NON_FINAL"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_STATIC" oldValue="NON_STATIC"/>
							<modifier changeStatus="MODIFIED" newValue="PACKAGE_PROTECTED" oldValue="PUBLIC"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_ABSTRACT" oldValue="NON_ABSTRACT"/>
						</modifiers>
						<parameters>
							<parameter type="com.google.common.base.Ticker"/>
						</parameters>
					</constructor>
				</constructors>
				<fields/>
				<interfaces/>
				<methods/>
				<modifiers>
					<modifier changeStatus="UNCHANGED" newValue="FINAL" oldValue="FINAL"/>
					<modifier changeStatus="UNCHANGED" newValue="NON_STATIC" oldValue="NON_STATIC"/>
					<modifier changeStatus="UNCHANGED" newValue="PUBLIC" oldValue="PUBLIC"/>
					<modifier changeStatus="UNCHANGED" newValue="NON_ABSTRACT" oldValue="NON_ABSTRACT"/>
				</modifiers>
				<superclass binaryCompatible="true" changeStatus="UNCHANGED" superclassNew="n.a." superclassOld="n.a."/>
			</class>
		...

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
