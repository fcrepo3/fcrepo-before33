<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Fedora Backend Security Configuration</title>
      </head>
      <body>
        <center>
          <table width="784" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td width="141" height="134" valign="top">
                <img src="/images/newlogo2.jpg" width="141" height="134"/>
              </td>
              <td width="643" valign="top">
                <center>
                  <h2>Fedora</h2>
                  <h3>Backend Security Configuration</h3>
                  Instructions go here.
                </center>
              </td>
            </tr>
          </table>
          <hr/>
          <form method="POST">
          <table border="1" cellpadding="4" cellspacing="0">
            <tr bgcolor="#aaaaaa">
              <td><nobr>Service</nobr></td>
              <td><nobr>Basic Authentication</nobr></td>
              <td><nobr>Secure Sockets Layer</nobr></td>
              <td><nobr>Allowed IP Addresses</nobr></td>
            </tr>
            <xsl:for-each select="//service">
              <tr>
                <td valign="top">
                    <strong><xsl:value-of select="@role"/></strong><br/>
                    <em><xsl:value-of select="@label"/></em>
                    <xsl:element name="input">
                      <xsl:attribute name="type">hidden</xsl:attribute>
                      <xsl:attribute name="name"><xsl:value-of select="@role"/>.label</xsl:attribute>
                      <xsl:attribute name="value"><xsl:value-of select="@label"/></xsl:attribute>
                    </xsl:element>
                </td>
                <td valign="top">
                  <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name"><xsl:value-of select="@role"/>.basicAuth</xsl:attribute>
                    <xsl:attribute name="value">true</xsl:attribute>
                    <xsl:if test="@basicAuth = 'true'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>Required<br/>
                  <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name"><xsl:value-of select="@role"/>.basicAuth</xsl:attribute>
                    <xsl:attribute name="value">false</xsl:attribute>
                    <xsl:if test="@basicAuth = 'false'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>Optional<br/>
                  <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name"><xsl:value-of select="@role"/>.basicAuth</xsl:attribute>
                    <xsl:attribute name="value">default</xsl:attribute>
                    <xsl:if test="@basicAuth = 'default'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>Use Default
                </td>
                <td valign="top">
                  <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name"><xsl:value-of select="@role"/>.ssl</xsl:attribute>
                    <xsl:attribute name="value">true</xsl:attribute>
                    <xsl:if test="@ssl = 'true'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>Required<br/>
                  <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name"><xsl:value-of select="@role"/>.ssl</xsl:attribute>
                    <xsl:attribute name="value">false</xsl:attribute>
                    <xsl:if test="@ssl = 'false'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>Optional<br/>
                  <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name"><xsl:value-of select="@role"/>.ssl</xsl:attribute>
                    <xsl:attribute name="value">false</xsl:attribute>
                    <xsl:if test="@ssl = 'default'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>Use Default
                </td>
                <td valign="top">
                    <xsl:element name="input">
                        <xsl:attribute name="type">text</xsl:attribute>
                        <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipList</xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@ipList"/>
                        </xsl:attribute>
                    </xsl:element>
                </td>
              </tr>
            </xsl:for-each>
          </table>
          <input type="submit"/>
          </form>
        </center>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>