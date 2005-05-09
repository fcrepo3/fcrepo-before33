<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Fedora Backend Security Configuration</title>
        <script language="javascript"><![CDATA[

function doSubmit() {
    // for each role...
    var names;
    for (var i = 0; i < theForm.elements.length; i++) {
      if (theForm.elements[i].name.indexOf(".role") != -1) {
        // set the ipList value if ipListValue is not custom
        var role = theForm.elements[i].value;
        var ipListValueRadio = theForm.elements[role + ".ipListValue"];
        for (var j = 0; j < ipListValueRadio.length; j++) {
          if (theForm.elements[role + ".ipListValue"][j].checked) {
            var ipListValue = theForm.elements[role + ".ipListValue"][j].value;
            if (ipListValue != "custom") {
              theForm.elements[role + ".ipList"].value = ipListValue;
//              alert(role + " ipListValue is " + ipListValue);
            }
          }
        }
      }
    }
    return true;
}
]]>
        </script>
      </head>
      <body onLoad="javascript:document.theForm.reset()">
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
          <p/>
          <form name="theForm" method="POST" onSubmit="return doSubmit()">
          <xsl:element name="input">
            <xsl:attribute name="type">hidden</xsl:attribute>
            <xsl:attribute name="name">lastModified</xsl:attribute>
            <xsl:attribute name="value">
                <xsl:value-of select="backendSecurityConfig/@lastModified"/>
            </xsl:attribute>
          </xsl:element>
          <table border="1" cellpadding="0" cellspacing="0">
          <tr><td>
          <table border="0" cellpadding="4" cellspacing="0">
            <xsl:for-each select="//service">
            <tr bgcolor="#aaaaaa">
              <td>
                <strong><xsl:value-of select="@role"/></strong>
              </td>
              <td><nobr>Basic Authentication</nobr></td>
              <td><nobr>Secure Sockets Layer</nobr></td>
              <td><nobr>Allowed IP Addresses</nobr></td>
            </tr>
              <tr>
                <td valign="top">
                    <em><xsl:value-of select="@label"/></em>
                    <xsl:element name="input">
                      <xsl:attribute name="type">hidden</xsl:attribute>
                      <xsl:attribute name="name"><xsl:value-of select="@role"/>.role</xsl:attribute>
                      <xsl:attribute name="value"><xsl:value-of select="@role"/></xsl:attribute>
                    </xsl:element>
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
                    <xsl:attribute name="value">false</xsl:attribute>
                    <xsl:if test="@basicAuth = 'false'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>Optional<br/>
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
                    <xsl:attribute name="value">false</xsl:attribute>
                    <xsl:if test="@ssl = 'false'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>Optional<br/>
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
                    <xsl:attribute name="value">default</xsl:attribute>
                    <xsl:if test="@ssl = 'default'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>Use Default
                </td>
                <td valign="top">
                  <nobr>
                   <xsl:element name="input">
                      <xsl:attribute name="type">radio</xsl:attribute>
                      <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipListValue</xsl:attribute>
                      <xsl:attribute name="value">.*</xsl:attribute>
                      <xsl:if test="@ipList = '.*'">
                        <xsl:attribute name="checked">checked</xsl:attribute>
                      </xsl:if>
                    </xsl:element> Unrestricted
                  </nobr>
                  <nobr>
                    <xsl:element name="input">
                      <xsl:attribute name="type">radio</xsl:attribute>
                      <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipListValue</xsl:attribute>
                      <xsl:attribute name="value">custom</xsl:attribute>
                      <xsl:if test="@ipList != '.*' and @ipList != 'default'">
                        <xsl:attribute name="checked">checked</xsl:attribute>
                      </xsl:if>
                    </xsl:element> Restricted
                    <xsl:element name="input">
                        <xsl:attribute name="type">text</xsl:attribute>
                        <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipList</xsl:attribute>
                        <xsl:attribute name="value">
                          <xsl:if test="@ipList != '.*' and @ipList != 'default'">
                            <xsl:value-of select="@ipList"/>
                          </xsl:if>
                        </xsl:attribute>
                    </xsl:element>
                  </nobr><br/>
                  <nobr>
                   <xsl:element name="input">
                      <xsl:attribute name="type">radio</xsl:attribute>
                      <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipListValue</xsl:attribute>
                      <xsl:attribute name="value">default</xsl:attribute>
                      <xsl:if test="@ipList = 'default'">
                        <xsl:attribute name="checked">checked</xsl:attribute>
                      </xsl:if>
                    </xsl:element> Use Default
                  </nobr>
                </td>
              </tr>
              <tr>
                <td colspan="4">
                &#160;
                </td>
              </tr>
            </xsl:for-each>
          </table>
          </td></tr></table>
          <input type="submit" value="Save Changes..."/>
          </form>
        </center>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>