<?xml version="1.0" encoding="UTF-8"?>
<?xmlspysamplexml C:\mellon\src\xsl\access\getItemList.xml?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Fedora Backend Security Configuration</title>
      </head>
      <body>
        <center>
<!--          <table width="784" border="0" cellpadding="0" cellspacing="0"> -->
          <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td width="141" height="134" valign="top">
                <img src="/images/newlogo2.jpg" width="141" height="134"/>
              </td>
<!--              <td width="643" valign="top"> -->
              <td valign="top">
                <center>
                  <h2>Fedora</h2>
                  <h3>Backend Security Configuration</h3>
                </center>
              </td>
            </tr>
          </table>
          <hr/>
          <xsl:for-each select="//service">
            <xsl:value-of select="@role"/><br/>
          </xsl:for-each>
        </center>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
