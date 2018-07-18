<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html"/>

	<xsl:template match="/japicmp">
		<html>
			<head>
				<title>
					<xsl:value-of select="@title"/>
				</title>
				<style type="text/css"></style>
			</head>
			<body>
				<span class="title">
					<xsl:value-of select="@title"/>
				</span>
				<br/>
				<div class="meta-information">
					<table>
						<tr>
							<td>Old:</td>
							<td>
								<xsl:value-of select="@oldJar"/>
							</td>
						</tr>
						<tr>
							<td>New:</td>
							<td>
								<xsl:value-of select="@newJar"/>
							</td>
						</tr>
						<tr>
							<td>Created:</td>
							<td>
								<xsl:value-of select="@creationTimestamp"/>
							</td>
						</tr>
						<tr>
							<td>Access modifier filter:</td>
							<td>
								<xsl:value-of select="@accessModifier"/>
							</td>
						</tr>
						<tr>
							<td>Only modifications:</td>
							<td>
								<xsl:value-of select="@onlyModifications"/>
							</td>
						</tr>
						<tr>
							<td>Only binary incompatible modifications:</td>
							<td>
								<xsl:value-of select="@onlyBinaryIncompatibleModifications"/>
							</td>
						</tr>
						<tr>
							<td>Ignore missing classes:</td>
							<td>
								<xsl:value-of select="@ignoreMissingClasses"/>
							</td>
						</tr>
						<tr>
							<td>Includes:</td>
							<td>
								<xsl:value-of select="@packagesInclude"/>
							</td>
						</tr>
						<tr>
							<td>Excludes:</td>
							<td>
								<xsl:value-of select="@packagesExclude"/>
							</td>
						</tr>
						<tr>
							<td id="semver-label">Semantic Versioning:</td>
							<td id="semver-version">
								<xsl:value-of select="@semanticVersioning"/>
							</td>
						</tr>
					</table>
					<xsl:if test="@ignoreMissingClasses = 'true'">
						<div class="warnings">
							<span>
								WARNING: You are using the option '--ignore-missing-classes', i.e. superclasses and
								interfaces that could not be found on the classpath are ignored. Hence changes
								caused by these superclasses and interfaces are not reflected in the output.
							</span>
						</div>
					</xsl:if>
				</div>
				<ul>
					<xsl:if test="count(classes/class) > 0">
						<li>
							<a href="#toc">Classes</a>
						</li>
					</xsl:if>
				</ul>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="classes">
		<div class="toc" id="toc">
			<span class="label">Classes:</span>
			<table>
				<thead>
					<tr>
						<td>Status</td>
						<td>Fully Qualified Name</td>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="class" mode="toc">
						<xsl:sort select="@fullyQualifiedName"/>
					</xsl:apply-templates>
				</tbody>
			</table>
		</div>
		<div class="explanations">
			<span>Binary incompatible changes are marked with (!) while source incompatible changes are marked with (*).</span>
		</div>
		<div>
			<xsl:apply-templates select="class" mode="detail">
				<xsl:sort select="@fullyQualifiedName"/>
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<xsl:template match="class" mode="toc">
		<tr>
			<td>
				<xsl:call-template name="outputChangeStatus"/>
			</td>
			<td>
				<a>
					<xsl:attribute name="href">#<xsl:value-of select="@fullyQualifiedName"/></xsl:attribute>
					<xsl:value-of select="@fullyQualifiedName"/>
				</a>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="class" mode="detail">
		<div>
			<div class="class">
				<xsl:attribute name="id"><xsl:value-of select="@fullyQualifiedName"/></xsl:attribute>
				<div class="class_header">
					<span class="label">
						<a>
							<xsl:attribute name="name"><xsl:value-of select="@fullyQualifiedName"/></xsl:attribute>
						</a>
						<xsl:call-template name="outputChangeStatus"/>
						<xsl:call-template name="javaObjectSerializationCompatible"/>
						<xsl:call-template name="modifiers"/>
						<xsl:call-template name="classType"/>&#160;<xsl:value-of select="@fullyQualifiedName"/>
					</span>
					<a href="#toc" class="toc_link">top</a>
				</div>
				<xsl:if test="count(compatibilityChanges/compatibilityChange) > 0">
					<div class="class_compatibilityChanges">
						<span class="label_class_member">Compatibility Changes:</span>
						<table>
							<thead>
								<tr>
									<td>Change</td>
								</tr>
							</thead>
							<tbody>
								<xsl:for-each select="compatibilityChanges/compatibilityChange">
									<tr>
										<td><xsl:value-of select="text()"/></td>
									</tr>
								</xsl:for-each>
							</tbody>
						</table>
					</div>
				</xsl:if>
				<xsl:if test="classFileFormatVersion/@changeStatus = 'MODIFIED'">
					<div class="class_fileFormatVersion">
						<span class="label_class_member">class File Format Version:</span>
						<table>
							<thead>
								<tr>
									<td>Status</td>
									<td>Old Version</td>
									<td>New Version</td>
								</tr>
							</thead>
							<tbody>
								<xsl:apply-templates select="classFileFormatVersion"/>
							</tbody>
						</table>
					</div>
				</xsl:if>
				<div class="class_superclass">
					<xsl:if test="  count(superclass) > 0
                                    and (superclass/@superclassNew != 'n.a.' or superclass/@superclassOld != 'n.a.')
                                    and (       (/japicmp/@onlyModifications = 'false' and /japicmp/@onlyBinaryIncompatibleModifications = 'false')
                                            or ((/japicmp/@onlyModifications = 'true' or /japicmp/@onlyBinaryIncompatibleModifications = 'true') and superclass/@changeStatus = 'NEW' and superclass/@superclassNew != 'java.lang.Object')
                                            or ((/japicmp/@onlyModifications = 'true' or /japicmp/@onlyBinaryIncompatibleModifications = 'true') and superclass/@changeStatus = 'REMOVED' and superclass/@superclassOld != 'java.lang.Object')
                                            or ((/japicmp/@onlyModifications = 'true' or /japicmp/@onlyBinaryIncompatibleModifications = 'true') and superclass/@changeStatus = 'MODIFIED')
                                            )">
						<span class="label_class_member">Superclass:</span>
						<table>
							<thead>
								<tr>
									<td>Status</td>
									<td>Superclass</td>
									<td>Compatibility Changes:</td>
								</tr>
							</thead>
							<tbody>
								<xsl:apply-templates select="superclass"/>
							</tbody>
						</table>
					</xsl:if>
				</div>
				<div class="class_interfaces">
					<xsl:if test="count(interfaces/interface) > 0">
						<span class="label_class_member">Interfaces:</span>
						<table>
							<thead>
								<tr>
									<td>Status</td>
									<td>Interface</td>
									<td>Compatibility Changes:</td>
								</tr>
							</thead>
							<tbody>
								<xsl:apply-templates select="interfaces/interface">
									<xsl:sort select="@fullyQualifiedName"/>
								</xsl:apply-templates>
							</tbody>
						</table>
					</xsl:if>
				</div>
				<xsl:if test="serialVersionUid/@serializableOld = 'true' or serialVersionUid/@serializableNew = 'true'">
					<div class="class_serialVersionUid">
						<table>
							<thead>
								<tr>
									<td></td>
									<td>Serializable</td>
									<td>default serialVersionUID</td>
									<td>serialVersionUID in class</td>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td class="matrix_layout">Old</td>
									<td>
										<xsl:if test="serialVersionUid/@serializableOld != serialVersionUid/@serializableNew">
											<xsl:attribute name="class">modified</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="serialVersionUid/@serializableOld"/>
									</td>
									<td>
										<xsl:if test="serialVersionUid/@serialVersionUidDefaultOld != serialVersionUid/@serialVersionUidDefaultNew">
											<xsl:attribute name="class">modified</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="serialVersionUid/@serialVersionUidDefaultOld"/>
									</td>
									<td>
										<xsl:if test="serialVersionUid/@serialVersionUidInClassOld != serialVersionUid/@serialVersionUidInClassNew">
											<xsl:attribute name="class">modified</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="serialVersionUid/@serialVersionUidInClassOld"/>
									</td>
								</tr>
								<tr>
									<td class="matrix_layout">New</td>
									<td>
										<xsl:if test="serialVersionUid/@serializableOld != serialVersionUid/@serializableNew">
											<xsl:attribute name="class">modified</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="serialVersionUid/@serializableNew"/>
									</td>
									<td>
										<xsl:if test="serialVersionUid/@serialVersionUidDefaultOld != serialVersionUid/@serialVersionUidDefaultNew">
											<xsl:attribute name="class">modified</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="serialVersionUid/@serialVersionUidDefaultNew"/>
									</td>
									<td>
										<xsl:if test="serialVersionUid/@serialVersionUidInClassOld != serialVersionUid/@serialVersionUidInClassNew">
											<xsl:attribute name="class">modified</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="serialVersionUid/@serialVersionUidInClassNew"/>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</xsl:if>
				<div class="class_fields">
					<xsl:if test="count(fields/field) > 0">
						<span class="label_class_member">Fields:</span>
						<table>
							<thead>
								<tr>
									<td>Status</td>
									<td>Modifier</td>
									<td>Type</td>
									<td>Field</td>
									<td>Compatibility Changes:</td>
								</tr>
							</thead>
							<tbody>
								<xsl:apply-templates select="fields/field">
									<xsl:sort select="@name"/>
								</xsl:apply-templates>
							</tbody>
						</table>
					</xsl:if>
				</div>
				<div class="class_constructors">
					<xsl:if test="count(constructors/constructor) > 0">
						<span class="label_class_member">Constructors:</span>
						<table>
							<thead>
								<tr>
									<td>Status</td>
									<td>Modifier</td>
									<td>Constructor</td>
									<td>Exceptions</td>
									<td>Compatibility Changes:</td>
									<td>Line Number</td>
								</tr>
							</thead>
							<tbody>
								<xsl:apply-templates select="constructors/constructor">
									<xsl:sort select="@name"/>
								</xsl:apply-templates>
							</tbody>
						</table>
					</xsl:if>
				</div>
				<div class="class_methods">
					<xsl:if test="count(methods/method) > 0">
						<span class="label_class_member">Methods:</span>
						<table>
							<thead>
								<tr>
									<td>Status</td>
									<td>Modifier</td>
									<td>Type</td>
									<td>Method</td>
									<td>Exceptions</td>
									<td>Compatibility Changes:</td>
									<td>Line Number</td>
								</tr>
							</thead>
							<tbody>
								<xsl:apply-templates select="methods/method">
									<xsl:sort select="@name"/>
								</xsl:apply-templates>
							</tbody>
						</table>
					</xsl:if>
				</div>
				<xsl:call-template name="annotations"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="annotations">
		<xsl:if test="count(annotations/annotation) > 0">
			<div class="class_annotations">
				<span class="label_class_member">Annotations:</span>
				<table>
					<thead>
						<tr>
							<td>Status:</td>
							<td>Fully Qualified Name:</td>
							<td>Elements:</td>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="annotations/annotation">
							<xsl:sort select="@fullyQualifiedName"/>
						</xsl:apply-templates>
					</tbody>
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="classFileFormatVersion">
		<tr>
			<td><xsl:call-template name="outputChangeStatus"/></td>
			<xsl:if test="@majorVersionOld != '-1' and @minorVersionOld != '-1'">
				<td><xsl:value-of select="@majorVersionOld"/>.<xsl:value-of select="@minorVersionOld"/></td>
			</xsl:if>
			<xsl:if test="@majorVersionOld = '-1' or @minorVersionOld = '-1'">
				<td>n.a.</td>
			</xsl:if>
			<xsl:if test="@majorVersionNew != '-1' and @minorVersionNew != '-1'">
				<td><xsl:value-of select="@majorVersionNew"/>.<xsl:value-of select="@minorVersionNew"/></td>
			</xsl:if>
			<xsl:if test="@majorVersionNew = '-1' or @minorVersionNew = '-1'">
				<td>n.a.</td>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template match="superclass">
		<tr>
			<td>
				<xsl:call-template name="outputChangeStatus"/>
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="@changeStatus = 'NEW'">
						<xsl:value-of select="@superclassNew"/>
					</xsl:when>
					<xsl:when test="@changeStatus = 'REMOVED'">
						<xsl:value-of select="@superclassOld"/>
					</xsl:when>
					<xsl:when test="@changeStatus = 'MODIFIED'">
						<xsl:value-of select="@superclassNew"/>(&lt;-&#160;<xsl:value-of select="@superclassOld"/>)
					</xsl:when>
					<xsl:when test="@changeStatus = 'UNCHANGED'">
						<xsl:value-of select="@superclassNew"/>
					</xsl:when>
				</xsl:choose>
			</td>
			<td>
				<xsl:call-template name="compatibilityChanges"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="interface">
		<tr>
			<td>
				<xsl:call-template name="outputChangeStatus"/>
			</td>
			<td>
				<xsl:value-of select="@fullyQualifiedName"/>
			</td>
			<td>
				<xsl:call-template name="compatibilityChanges"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="field">
		<tr>
			<td>
				<xsl:call-template name="outputChangeStatus"/>
			</td>
			<td>
				<xsl:call-template name="modifiers"/>
			</td>
			<td>
				<xsl:call-template name="type"/>
			</td>
			<td>
				<xsl:call-template name="compatibilityChanges"/>
			</td>
			<td>
				<xsl:value-of select="@name"/><xsl:call-template name="annotations"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="constructor">
		<tr>
			<td>
				<xsl:call-template name="outputChangeStatus"/>
			</td>
			<td>
				<xsl:call-template name="modifiers"/>
			</td>
			<td>
				<xsl:value-of select="@name"/>(<xsl:apply-templates select="parameters"/>)<xsl:call-template name="annotations"/>
			</td>
			<td>
				<xsl:call-template name="exceptions"/>
			</td>
			<td>
				<xsl:call-template name="compatibilityChanges"/>
			</td>
			<td>
				<table>
					<thead>
						<tr>
							<td>Old file</td>
							<td>New file</td>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<xsl:value-of select="@oldLineNumber"/>
							</td>
							<td>
								<xsl:value-of select="@newLineNumber"/>
							</td>
						</tr>
					</tbody>
				</table>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="method">
		<tr>
			<td>
				<xsl:call-template name="outputChangeStatus"/>
			</td>
			<td>
				<xsl:call-template name="modifiers"/>
			</td>
			<td>
				<xsl:apply-templates select="returnType"/>
			</td>
			<td>
				<xsl:value-of select="@name"/>(<xsl:apply-templates select="parameters"/>)<xsl:call-template name="annotations"/>
			</td>
			<td>
				<xsl:call-template name="exceptions"/>
			</td>
			<td>
				<xsl:call-template name="compatibilityChanges"/>
			</td>
			<td>
				<table>
					<thead>
						<tr>
							<td>Old file</td>
							<td>New file</td>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<xsl:value-of select="@oldLineNumber"/>
							</td>
							<td>
								<xsl:value-of select="@newLineNumber"/>
							</td>
						</tr>
					</tbody>
				</table>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="exceptions">
		<xsl:if test="count(exceptions/exception) > 0">
			<table>
				<thead>
					<tr>
						<td>Status:</td>
						<td>Name:</td>
					</tr>
				</thead>
				<tbody>
					<xsl:for-each select="exceptions/exception">
						<tr>
							<td>
								<xsl:call-template name="outputChangeStatus"/>
							</td>
							<td>
								<xsl:value-of select="@name"/>
							</td>
						</tr>
					</xsl:for-each>
				</tbody>
			</table>
		</xsl:if>
		<xsl:if test="count(exceptions/exception) = 0">n.a.</xsl:if>
	</xsl:template>

	<xsl:template match="annotation">
		<tr>
			<td>
				<xsl:call-template name="outputChangeStatus"/>
			</td>
			<td>
				<xsl:value-of select="@fullyQualifiedName"/>
			</td>
			<td>
				<xsl:if test="count(elements/element) > 0">
					<table>
						<thead>
							<tr>
								<td>Status:</td>
								<td>Name:</td>
								<td>Old element values:</td>
								<td>New element values:</td>
							</tr>
						</thead>
						<tbody>
							<xsl:apply-templates select="elements"/>
						</tbody>
					</table>
				</xsl:if>
				<xsl:if test="count(elements/element) = 0">n.a.</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="elements">
		<xsl:for-each select="element">
			<tr>
				<td>
					<xsl:call-template name="outputChangeStatus"/>
				</td>
				<td>
					<xsl:value-of select="@name"/>
				</td>
				<td>
					<xsl:for-each select="oldElementValues/oldElementValue">
						<xsl:if test="position() > 1">,<wbr/></xsl:if>
						<xsl:choose>
							<xsl:when test="@type = 'Annotation'">
								@<xsl:value-of select="@fullyQualifiedName"/>(<xsl:apply-templates select="values"/>)
							</xsl:when>
							<xsl:when test="@type = 'Array'">
								{<xsl:apply-templates select="values"/>}
							</xsl:when>
							<xsl:when test="@type = 'Enum'">
								<xsl:value-of select="@fullyQualifiedName"/>.<xsl:value-of select="@value"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@value"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</td>
				<td>
					<xsl:for-each select="newElementValues/newElementValue">
						<xsl:if test="position() > 1">,<wbr/></xsl:if>
						<xsl:choose>
							<xsl:when test="@type = 'Annotation'">
								@<xsl:value-of select="@fullyQualifiedName"/>(<xsl:apply-templates select="values"/>)
							</xsl:when>
							<xsl:when test="@type = 'Array'">
								{<xsl:apply-templates select="values"/>}
							</xsl:when>
							<xsl:when test="@type = 'Enum'">
								<xsl:value-of select="@fullyQualifiedName"/>.<xsl:value-of select="@value"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@value"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="values">
		<xsl:for-each select="value">
			<xsl:if test="position() > 1">,<wbr/></xsl:if>
			<xsl:choose>
				<xsl:when test="@type = 'Annotation'">
					@<xsl:value-of select="@fullyQualifiedName"/>(<xsl:apply-templates select="values"/>)
				</xsl:when>
				<xsl:when test="@type = 'Array'">
					{<xsl:apply-templates select="values"/>}
				</xsl:when>
				<xsl:when test="@type = 'Enum'">
					<xsl:value-of select="@fullyQualifiedName"/>.<xsl:value-of select="@value"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@value"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="parameters">
		<xsl:if test="count(parameter) > 0">
			<xsl:for-each select="parameter">
				<xsl:if test="position() > 1">,<wbr/></xsl:if>
				<xsl:value-of select="@type"/>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<xsl:template name="outputChangeStatus">
		<span>
			<xsl:choose>
				<xsl:when test="@changeStatus = 'MODIFIED'">
					<xsl:attribute name="class">modified</xsl:attribute>
				</xsl:when>
				<xsl:when test="@changeStatus = 'UNCHANGED'">
					<xsl:attribute name="class">unchanged</xsl:attribute>
				</xsl:when>
				<xsl:when test="@changeStatus = 'NEW'">
					<xsl:attribute name="class">new</xsl:attribute>
				</xsl:when>
				<xsl:when test="@changeStatus = 'REMOVED'">
					<xsl:attribute name="class">removed</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:value-of select="@changeStatus"/>
			<xsl:if test="@binaryCompatible = 'false'">&#160;(!)</xsl:if>
			<xsl:if test="@binaryCompatible = 'true' and @sourceCompatible = 'false'">&#160;(*)</xsl:if>
		</span>
	</xsl:template>

	<xsl:template name="modifiers">
		<xsl:for-each select="modifiers/modifier">
			<span>
				<xsl:choose>
					<xsl:when test="@changeStatus = 'MODIFIED'">
						<xsl:attribute name="class">modified modifier</xsl:attribute>
					</xsl:when>
					<xsl:when test="@changeStatus = 'UNCHANGED'">
						<xsl:attribute name="class">unchanged modifier</xsl:attribute>
					</xsl:when>
					<xsl:when test="@changeStatus = 'NEW'">
						<xsl:attribute name="class">new modifier</xsl:attribute>
					</xsl:when>
					<xsl:when test="@changeStatus = 'REMOVED'">
						<xsl:attribute name="class">removed modifier</xsl:attribute>
					</xsl:when>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="@changeStatus = 'MODIFIED'">
						<xsl:call-template name="modifier">
							<xsl:with-param name="modifier" select="@newValue"/>
							<xsl:with-param name="changeStatus" select="@changeStatus"/>
						</xsl:call-template>
						&#160;(&lt;-&#160;
						<xsl:call-template name="modifier">
							<xsl:with-param name="modifier" select="@oldValue"/>
							<xsl:with-param name="changeStatus" select="@changeStatus"/>
						</xsl:call-template>
						)&#160;
					</xsl:when>
					<xsl:when test="@changeStatus = 'UNCHANGED'">
						<xsl:call-template name="modifier">
							<xsl:with-param name="modifier" select="@newValue"/>
							<xsl:with-param name="changeStatus" select="@changeStatus"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="@changeStatus = 'NEW'">
						<xsl:call-template name="modifier">
							<xsl:with-param name="modifier" select="@newValue"/>
							<xsl:with-param name="changeStatus" select="@changeStatus"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="@changeStatus = 'REMOVED'">
						<xsl:call-template name="modifier">
							<xsl:with-param name="modifier" select="@oldValue"/>
							<xsl:with-param name="changeStatus" select="@changeStatus"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</span>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="modifier">
		<xsl:param name="modifier"/>
		<xsl:param name="changeStatus"/>
		<xsl:choose>
			<xsl:when test="$modifier = 'NON_FINAL'">
				<xsl:if test="$changeStatus = 'MODIFIED'">not_final</xsl:if>
			</xsl:when>
			<xsl:when test="$modifier = 'NON_STATIC'">
				<xsl:if test="$changeStatus = 'MODIFIED'">not_static</xsl:if>
			</xsl:when>
			<xsl:when test="$modifier = 'NON_ABSTRACT'">
				<xsl:if test="$changeStatus = 'MODIFIED'">not_abstract</xsl:if>
			</xsl:when>
			<xsl:when test="$modifier = 'NON_BRIDGE'">
				<xsl:if test="$changeStatus = 'MODIFIED'">not_bridge</xsl:if>
			</xsl:when>
			<xsl:when test="$modifier = 'NON_SYNTHETIC'">
				<xsl:if test="$changeStatus = 'MODIFIED'">not_synthetic</xsl:if>
			</xsl:when>
			<xsl:when test="$modifier = 'PACKAGE_PROTECTED'">
				<xsl:if test="$changeStatus = 'MODIFIED'">package_protected</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="translate($modifier, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
				<xsl:if test="$changeStatus != 'MODIFIED'">&#160;</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="compatibilityChanges">
		<xsl:if test="count(compatibilityChanges/compatibilityChange) > 0">
			<table>
				<thead>
					<tr>
						<td>Change</td>
					</tr>
				</thead>
				<tbody>
					<xsl:for-each select="compatibilityChanges/compatibilityChange">
						<tr>
							<td><xsl:value-of select="text()"/></td>
						</tr>
					</xsl:for-each>
				</tbody>
			</table>
		</xsl:if>
		<xsl:if test="count(compatibilityChanges/compatibilityChange) = 0">n.a.</xsl:if>
	</xsl:template>

	<xsl:template name="type">
		<span>
			<xsl:choose>
				<xsl:when test="@changeStatus = 'MODIFIED'">
					<xsl:attribute name="class">modified modifier</xsl:attribute>
				</xsl:when>
				<xsl:when test="@changeStatus = 'UNCHANGED'">
					<xsl:attribute name="class">unchanged modifier</xsl:attribute>
				</xsl:when>
				<xsl:when test="@changeStatus = 'NEW'">
					<xsl:attribute name="class">new modifier</xsl:attribute>
				</xsl:when>
				<xsl:when test="@changeStatus = 'REMOVED'">
					<xsl:attribute name="class">removed modifier</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="type/@changeStatus = 'MODIFIED'">
					<xsl:value-of select="type/@newValue"/>&#160;(&lt;-&#160;<xsl:value-of select="type/@oldValue"/>)
				</xsl:when>
				<xsl:when test="type/@changeStatus = 'UNCHANGED'">
					<xsl:value-of select="type/@newValue"/>
				</xsl:when>
				<xsl:when test="type/@changeStatus = 'NEW'">
					<xsl:value-of select="type/@newValue"/>
				</xsl:when>
				<xsl:when test="type/@changeStatus = 'REMOVED'">
					<xsl:value-of select="type/@oldValue"/>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="@binaryCompatible = 'false'">
				(!)
			</xsl:if>
			<xsl:if test="@binaryCompatible = 'true' and @sourceCompatible = 'false'">
				(!)
			</xsl:if>
		</span>
	</xsl:template>

	<xsl:template match="returnType">
		<span>
			<xsl:choose>
				<xsl:when test="@changeStatus = 'MODIFIED'">
					<xsl:attribute name="class">modified method_return_type</xsl:attribute>
				</xsl:when>
				<xsl:when test="@changeStatus = 'UNCHANGED'">
					<xsl:attribute name="class">unchanged method_return_type</xsl:attribute>
				</xsl:when>
				<xsl:when test="@changeStatus = 'NEW'">
					<xsl:attribute name="class">new method_return_type</xsl:attribute>
				</xsl:when>
				<xsl:when test="@changeStatus = 'REMOVED'">
					<xsl:attribute name="class">removed method_return_type</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="@changeStatus = 'MODIFIED'">
					<xsl:value-of select="@newValue"/>&#160;(&lt;-&#160;<xsl:value-of select="@oldValue"/>)
				</xsl:when>
				<xsl:when test="@changeStatus = 'UNCHANGED'">
					<xsl:value-of select="@newValue"/>
				</xsl:when>
				<xsl:when test="@changeStatus = 'NEW'">
					<xsl:value-of select="@newValue"/>
				</xsl:when>
				<xsl:when test="@changeStatus = 'REMOVED'">
					<xsl:value-of select="@oldValue"/>
				</xsl:when>
			</xsl:choose>
		</span>
	</xsl:template>

	<xsl:template name="classType">
		<span>
			<xsl:choose>
				<xsl:when test="classType/@changeStatus = 'MODIFIED'">
					<xsl:attribute name="class">modified</xsl:attribute>
				</xsl:when>
				<xsl:when test="classType/@changeStatus = 'UNCHANGED'">
					<xsl:attribute name="class">unchanged</xsl:attribute>
				</xsl:when>
				<xsl:when test="classType/@changeStatus = 'NEW'">
					<xsl:attribute name="class">new</xsl:attribute>
				</xsl:when>
				<xsl:when test="classType/@changeStatus = 'REMOVED'">
					<xsl:attribute name="class">removed</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="classType/@changeStatus = 'MODIFIED'">
					<xsl:value-of select="translate(classType/@newType, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>&#160;(&lt;-&#160;<xsl:value-of
					select="translate(classType/@oldType, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>)
				</xsl:when>
				<xsl:when test="classType/@changeStatus = 'UNCHANGED'">
					<xsl:value-of select="translate(classType/@newType, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
				</xsl:when>
				<xsl:when test="classType/@changeStatus = 'NEW'">
					<xsl:value-of select="translate(classType/@newType, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
				</xsl:when>
				<xsl:when test="classType/@changeStatus = 'REMOVED'">
					<xsl:value-of select="translate(classType/@oldType, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
				</xsl:when>
			</xsl:choose>
		</span>
	</xsl:template>

	<xsl:template name="javaObjectSerializationCompatible">
		<span>
			<xsl:choose>
				<xsl:when test="@javaObjectSerializationCompatible = 'NOT_SERIALIZABLE'"><xsl:attribute name="class"></xsl:attribute>&#160;</xsl:when>
				<xsl:when test="@javaObjectSerializationCompatible = 'SERIALIZABLE_COMPATIBLE'"><xsl:attribute name="class">new</xsl:attribute>&#160;(Serializable compatible)&#160;</xsl:when>
				<xsl:otherwise><xsl:attribute name="class">removed</xsl:attribute>&#160;(Serializable incompatible(!): <xsl:value-of select="@javaObjectSerializationCompatibleAsString"/>)&#160;</xsl:otherwise>
			</xsl:choose>
		</span>
	</xsl:template>
</xsl:stylesheet>
