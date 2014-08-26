<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	<xsl:template match="/japicmp">
		<html>
			<head></head>
			<body style="font-family:Verdana;">
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>
	<xsl:template match="classes">
		<div id="classes">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	<xsl:template match="class">
		<p>
			<xsl:value-of select="@fullyQualifiedName" />:<xsl:value-of select="@changeStatus" />
		</p>
	</xsl:template>
</xsl:stylesheet>