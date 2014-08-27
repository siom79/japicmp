<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output method="html" />
	
	<xsl:template match="/japicmp">
		<html>
			<head>
				<title>japicmp</title>
				<style type="text/css">
					.new {
						color: green;
					}
					.removed {
						color: red;
					}
					.modified {
						color: orange;
					}
					.unchanged {
						color: black;
					}
					.classes_header {
						margin-bottom: 25px;
					}
					.methods_header {
						margin-left: 50px;
					}
					.title_classes {
						font-weight: bold;
					}
					.title_methods {
						font-weight: bold;
					}
				</style>
			</head>
			<body style="font-family:Verdana;">
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="classes">
		<div id="classes_new" class="classes_header">
			<span class="title_classes">New classes:</span>
			<xsl:apply-templates select="class[@changeStatus='NEW']" />
		</div>
		<div id="classes_removed" class="classes_header">
			<span class="title_classes">Removed classes:</span>
			<xsl:apply-templates select="class[@changeStatus='REMOVED']" />
		</div>
		<div id="classes_modified" class="classes_header">
			<span class="title_classes">Modified classes:</span>
			<xsl:apply-templates select="class[@changeStatus='MODIFIED']" />
		</div>
		<div id="classes_unchanged" class="classes_header">
			<span class="title_classes">Unchanged classes:</span>
			<xsl:apply-templates select="class[@changeStatus='UNCHANGED']" />
		</div>
	</xsl:template>
	
	<xsl:template match="class">
		<div>
			<xsl:attribute name="id">
     			<xsl:value-of select="@fullyQualifiedName" />
  			</xsl:attribute>
			<span>
				<xsl:choose>
					<xsl:when test="@changeStatus = 'MODIFIED'">
						<xsl:attribute name="class">
     						modified
  						</xsl:attribute>
					</xsl:when>
					<xsl:when test="@changeStatus = 'UNCHANGED'">
						<xsl:attribute name="class">
     						unchanged
  						</xsl:attribute>
					</xsl:when>
					<xsl:when test="@changeStatus = 'NEW'">
						<xsl:attribute name="class">
     						new
  						</xsl:attribute>
					</xsl:when>
					<xsl:when test="@changeStatus = 'REMOVED'">
						<xsl:attribute name="class">
     						removed
  						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
				<xsl:value-of select="@changeStatus" />
				&#160;
			</span>
			<span>
				<xsl:value-of select="@type" />
				&#160;
			</span>
			<span>
				<xsl:value-of select="@fullyQualifiedName" />
			</span>
		</div>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="methods">
		<div id="methods_new" class="methods_header">
			<xsl:apply-templates select="method[@changeStatus='NEW']" />
		</div>
		<div id="methods_removed" class="methods_header">
			<xsl:apply-templates select="method[@changeStatus='REMOVED']" />
		</div>
		<div id="methods_modified" class="methods_header">
			<xsl:apply-templates select="method[@changeStatus='MODIFIED']" />
		</div>
		<div id="methods_unchanged" class="methods_header">
			<xsl:apply-templates select="method[@changeStatus='UNCHANGED']" />
		</div>
	</xsl:template>
	
	<xsl:template match="method">
		<div>
			<span>
				<xsl:choose>
					<xsl:when test="@changeStatus = 'MODIFIED'">
						<xsl:attribute name="class">
     						modified
  						</xsl:attribute>
					</xsl:when>
					<xsl:when test="@changeStatus = 'UNCHANGED'">
						<xsl:attribute name="class">
     						unchanged
  						</xsl:attribute>
					</xsl:when>
					<xsl:when test="@changeStatus = 'NEW'">
						<xsl:attribute name="class">
     						new
  						</xsl:attribute>
					</xsl:when>
					<xsl:when test="@changeStatus = 'REMOVED'">
						<xsl:attribute name="class">
     						removed
  						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
				<xsl:value-of select="@changeStatus" />
				&#160;
			</span>
			<span>
				<xsl:value-of select="@returnType" />
				&#160;
			</span>
			<span>
				<xsl:value-of select="@name" />
			</span>
		</div>
		<xsl:apply-templates />
	</xsl:template>
</xsl:stylesheet>