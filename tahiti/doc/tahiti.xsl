<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<!-- This stylesheet is a customization of the DocBook XSL Stylesheets -->
<!-- See http://sourceforge.net/projects/docbook/ -->
<xsl:import href="file:///c:/progra~1/applications/docbook/XSLT/docbook/html/docbook.xsl"/>

<!-- ============================================================ -->
<!-- Parameters -->

<xsl:param name="html.stylesheet">tahiti.css</xsl:param>


<xsl:template match="footnote" mode="process.footnote.mode">
  <div class="{name(.)}">
    [<xsl:apply-templates select="." mode="footnote.number"/>]
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="programlisting/emphasis">
  <font color="red">
  	<xsl:apply-templates/>
  </font>
</xsl:template>

</xsl:stylesheet>
