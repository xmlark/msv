<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="grammar">
		<html>
			<body>
				<h1>rules</h1>
				<p> total <xsl:value-of select="count(rules/rule)"/> rules </p>
				<xsl:for-each select="rules">
					<a name="{@nonTerminal}" />
					<table class="rules">
						<xsl:for-each select="rule">
							<tr><td>
								<xsl:apply-templates select="."/>
							</td><td style="padding-left: 2em">
								<a name="{@id}">
									<xsl:value-of select="@id"/>
								</a>
							</td></tr>
						</xsl:for-each>
					</table>
				</xsl:for-each>
				
				
				<h1>parser table</h1>
				<xsl:for-each select="*/parserTable">
					<h2>
						<xsl:if test="parent::*[name()='topLevel']">
							<xsl:text>(root)</xsl:text>
						</xsl:if>
						<xsl:value-of select="../@id"/>
					</h2>
					<dl>
						<xsl:for-each select="action">
							<dt>
								(
									<a href="{@stackTop}">
										<xsl:value-of select="@stackTop"/>
									</a>
								,
									<a href="{@token}">
										<xsl:value-of select="@token"/>
									</a>
								)
							</dt>
							<dd>
								<xsl:for-each select="rule">
									<xsl:variable name="rid" select="@ref"/>
									<xsl:apply-templates select="//rule[@id=$rid]"/><br/>
								</xsl:for-each>
							</dd>
						</xsl:for-each>
					</dl>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
	
	
	<xsl:template match="rule">
		<xsl:value-of select="left/@symbolRef"/>
		<xsl:text> -> </xsl:text>
		<xsl:for-each select="right/item">
			<a href="#{@symbolRef}">
				<xsl:value-of select="@symbolRef"/>
			</a>
			<xsl:text> </xsl:text>
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>