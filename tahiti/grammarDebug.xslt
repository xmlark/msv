<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:variable name="allRules" select="/grammar/rules/rule"/>
	
	<xsl:template match="grammar">
		<html>
			<body>
				<h1>rules</h1>
				<p> total <xsl:value-of select="count(rules/rule)"/> rules </p>
				<table class="rules">
					<thead>
						<tr>
							<td style="padding-right:1em">rule no.</td>
							<td>rule body</td>
						</tr>
					</thead>
					<xsl:for-each select="$allRules">
						<tr><td>
							<a name="{@id}">
								<xsl:value-of select="@id"/>
							</a>
						</td><td>
							<a name="{../@nonTerminal}">
								<xsl:apply-templates select="."/>
							</a>
						</td></tr>
					</xsl:for-each>
				</table>
				
				<h1>root type</h1>
				<p>
					the common base type of possible root classes is
					<b><code>
						<xsl:value-of select="rootType/@name"/>
					</code></b>
				</p>
				
				<h1>parser table</h1>
				<p>
					This section shows LL parser tables for each start symbol.
					The red input tokens are ambiguous tokens. There are more than one
					valid expansion for that token.
				</p>
				
				<xsl:for-each select="*/parserTable">
					<h2>
						<xsl:if test="parent::*[name()='topLevel']">
							<xsl:text>(root)</xsl:text>
						</xsl:if>
						<xsl:value-of select="../@id"/>
					</h2>
					<table>
						<thead><tr><td>
							this rule is expanded
						</td><td>
							... when the token is one of these
						</td></tr></thead>
						
						<xsl:variable name="context" select="."/>
						
						<!-- for all distinct rules -->
						<xsl:for-each select="$allRules">
							<xsl:variable name="actions"
								select="$context/action[*/rule/@ref=current()/@id]"/>
							
							<xsl:if test="count($actions)!=0">
								<tr><td style="padding-right: 2em">
									<xsl:apply-templates select="."/><br/>
								</td><td>
									<xsl:for-each select="$actions/*[rule/@ref=current()/@id]">
										<xsl:if test="position()!=1">
											<xsl:text>, </xsl:text>
										</xsl:if>
										
										<span>
											<xsl:if test="count(rule)>1">
												<xsl:attribute name="style">color:red</xsl:attribute>
											</xsl:if>
											<xsl:choose>
												<xsl:when test="@id">
													<xsl:value-of select="@id"/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:text>otherwise</xsl:text>
												</xsl:otherwise>
											</xsl:choose>
										</span>
									</xsl:for-each>
								</td></tr>
							</xsl:if>
						</xsl:for-each>
					</table>
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