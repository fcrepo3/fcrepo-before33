<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<html><body>
<p><xsl:apply-templates select="/poem/stanza"/></p>
</body></html>
</xsl:template>

<xsl:template match="stanza">
<p><table><xsl:apply-templates/></table></p>
</xsl:template> 

<xsl:template match="line">
<tr>
<td width="350"><xsl:value-of select="."/></td>
<td width="50">
   <xsl:variable name="line-nr">
      <xsl:number level="any" from="poem"/>
   </xsl:variable>
   <xsl:if test="$line-nr mod 3 = 0">
      <xsl:value-of select="$line-nr"/>
   </xsl:if>
</td>
</tr>
</xsl:template>
</xsl:stylesheet>

