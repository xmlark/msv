<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:schmit="xalan://com.sun.msv.schmit.XalanExtension"
	xmlns:a="annotation"
	extension-element-prefixes="schmit">
	
	<xsl:template match="/">
		<!-- associate the current document with the schema -->
		<schmit:useSchema href="test.rng"/>
		<xsl:apply-templates />
	</xsl:template>
	
	<!--
		for each element, produce the contents of <a:description>
		as an attribute.
	-->
	<xsl:template match="*">
		<xsl:copy>
			<xsl:attribute name='description'>
				<xsl:value-of select="schmit:annotation(.)"/>
			</xsl:attribute>
			<xsl:copy-of select="schmit:annotation(.)/@a:important"/>
			<xsl:apply-templates select="*"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
