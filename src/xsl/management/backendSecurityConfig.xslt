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
          <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td valign="top" width="200">
                <center>
                <img src="/images/newlogo2.jpg" width="141" height="134"/>
                </center>
              </td>
              <td valign="top">
                <center>
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
          <table border="0" cellpadding="4" cellspacing="0" width="95%">
            <xsl:for-each select="//service">
            <tr>
              <td bgcolor="#9999ff">
                <strong><xsl:value-of select="@role"/></strong>
              </td>
              <td bgcolor="#cccccc"><nobr><strong><font color="#000000">Basic Authentication</font></strong></nobr></td>
              <td bgcolor="#cccccc"><nobr><strong><font color="#000000">SSL</font></strong></nobr></td>
              <td bgcolor="#cccccc"><nobr><strong><font color="#000000">Allowed IPs</font></strong></nobr></td>
            </tr>
              <tr bgcolor="#eeeeee">
                <td bgcolor="#ddddff" valign="top">
                  <xsl:choose>
                  <xsl:when test="@label != ''">
                    <font size="-1">[
                        <xsl:element name="a">
                          <xsl:attribute name="href">
                            <xsl:text>../get/</xsl:text>
                            <xsl:value-of select="@role"/>
                            <xsl:text>/WSDL</xsl:text>
                          </xsl:attribute>
                          <xsl:attribute name="target">_WSDL</xsl:attribute>
                          WSDL
                        </xsl:element>
                        |
                        <xsl:element name="a">
                          <xsl:attribute name="href">
                            <xsl:text>../get/</xsl:text>
                            <xsl:value-of select="@role"/>
                            <xsl:text>/DSINPUTSPEC</xsl:text>
                          </xsl:attribute>
                          <xsl:attribute name="target">_DSINPUTSPEC</xsl:attribute>
                          DS Input Spec
                        </xsl:element>
                        |
                        <xsl:element name="a">
                          <xsl:attribute name="href">
                            <xsl:text>../get/</xsl:text>
                            <xsl:value-of select="@role"/>
                            <xsl:text>/METHODMAP</xsl:text>
                          </xsl:attribute>
                          <xsl:attribute name="target">_METHODMAP</xsl:attribute>
                          Method Map
                        </xsl:element>
                      ]</font><br/>
                    <em><xsl:value-of select="@label"/></em>
                  </xsl:when>
                  <xsl:otherwise>
                    <em>This object does not exist in the repository.</em> 
                  </xsl:otherwise>
                  </xsl:choose>
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
                    <xsl:attribute name="id"><xsl:value-of select="@role"/>.basicAuth.optional</xsl:attribute>
                    <xsl:attribute name="value">false</xsl:attribute>
                    <xsl:if test="@basicAuth = 'false'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>
                  <xsl:element name="label">
                    <xsl:attribute name="for"><xsl:value-of select="@role"/>.basicAuth.optional</xsl:attribute>
                    Optional
                  </xsl:element><br/>
                  <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name"><xsl:value-of select="@role"/>.basicAuth</xsl:attribute>
                    <xsl:attribute name="id"><xsl:value-of select="@role"/>.basicAuth.required</xsl:attribute>
                    <xsl:attribute name="value">true</xsl:attribute>
                    <xsl:if test="@basicAuth = 'true'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>
                  <xsl:element name="label">
                    <xsl:attribute name="for"><xsl:value-of select="@role"/>.basicAuth.required</xsl:attribute>
                    Required
                  </xsl:element><br/>
                  <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name"><xsl:value-of select="@role"/>.basicAuth</xsl:attribute>
                    <xsl:attribute name="id"><xsl:value-of select="@role"/>.basicAuth.default</xsl:attribute>
                    <xsl:attribute name="value">default</xsl:attribute>
                    <xsl:if test="@basicAuth = 'default'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>
                  <xsl:element name="label">
                    <xsl:attribute name="for"><xsl:value-of select="@role"/>.basicAuth.default</xsl:attribute>
                    Default
                  </xsl:element>
                </td>
                <td valign="top">
                 <nobr>
                  <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name"><xsl:value-of select="@role"/>.ssl</xsl:attribute>
                    <xsl:attribute name="id"><xsl:value-of select="@role"/>.ssl.optional</xsl:attribute>
                    <xsl:attribute name="value">false</xsl:attribute>
                    <xsl:if test="@ssl = 'false'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>
                  <xsl:element name="label">
                    <xsl:attribute name="for"><xsl:value-of select="@role"/>.ssl.optional</xsl:attribute>
                    Optional
                  </xsl:element>
                 </nobr>
                 <br/>
                 <nobr>
                  <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name"><xsl:value-of select="@role"/>.ssl</xsl:attribute>
                    <xsl:attribute name="id"><xsl:value-of select="@role"/>.ssl.required</xsl:attribute>
                    <xsl:attribute name="value">true</xsl:attribute>
                    <xsl:if test="@ssl = 'true'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>
                  <xsl:element name="label">
                    <xsl:attribute name="for"><xsl:value-of select="@role"/>.ssl.required</xsl:attribute>
                    Required
                  </xsl:element>
                 </nobr>
                 <br/>
                 <nobr>
                  <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name"><xsl:value-of select="@role"/>.ssl</xsl:attribute>
                    <xsl:attribute name="id"><xsl:value-of select="@role"/>.ssl.default</xsl:attribute>
                    <xsl:attribute name="value">default</xsl:attribute>
                    <xsl:if test="@ssl = 'default'">
                      <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                  </xsl:element>
                  <xsl:element name="label">
                    <xsl:attribute name="for"><xsl:value-of select="@role"/>.ssl.default</xsl:attribute>
                    Use Default
                  </xsl:element>
                 </nobr>
                </td>
                <td valign="top">
                <!--
                  <nobr>
                   <xsl:element name="input">
                      <xsl:attribute name="type">radio</xsl:attribute>
                      <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipListValue</xsl:attribute>
                      <xsl:attribute name="id"><xsl:value-of select="@role"/>.ipListValue.noRestrictions</xsl:attribute>
                      <xsl:attribute name="value">.*</xsl:attribute>
                      <xsl:if test="@ipList = '.*'">
                        <xsl:attribute name="checked">checked</xsl:attribute>
                      </xsl:if>
                    </xsl:element>
                    <xsl:element name="label">
                      <xsl:attribute name="for"><xsl:value-of select="@role"/>.ipListValue.noRestrictions</xsl:attribute>
                      All
                    </xsl:element>
                  </nobr>
                  -->

<!--

<script language="javascript">

function doAdd() {
  var ip = prompt("Enter a new IP address regular expression", "");
  document.theForm.theList.options[document.theForm.theList.options.length] = new Option(ip, 
ip);
  if (document.theForm.theList.options.length > 1) {
    document.theForm.theList.size = document.theForm.theList.options.length;
  }
}

function doDelete() {
  var oldSelectedIndex = document.theForm.theList.selectedIndex;
  document.theForm.theList.options[oldSelectedIndex] = null;
  if (document.theForm.theList.options.length > 0) {
    if (document.theForm.theList.options.length == oldSelectedIndex) {
      document.theForm.theList.selectedIndex = oldSelectedIndex - 1;
    } else {
      document.theForm.theList.selectedIndex = oldSelectedIndex;
    }
  } else {
    document.theForm.deleteButton.disabled = true;
  }
  if (document.theForm.theList.options.length > 1) {
    document.theForm.theList.size = document.theForm.theList.options.length;
  }
}


function doSelect() {
  document.theForm.deleteButton.disabled = false;
}

</script>
</head>
<body onLoad="javascript:document.theForm.theList.selectedIndex=-1">

-->
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td valign="top">
                    <xsl:element name="input">
                      <xsl:attribute name="type">radio</xsl:attribute>
                      <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipListValue</xsl:attribute>
                      <xsl:attribute name="value">custom</xsl:attribute>
                      <xsl:if test="@ipList != '.*' and @ipList != 'default'">
                        <xsl:attribute name="checked">checked</xsl:attribute>
                      </xsl:if>
                    </xsl:element>
                    <xsl:element name="input">
                        <xsl:attribute name="type">hidden</xsl:attribute>
                        <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipList</xsl:attribute>
                        <xsl:attribute name="value">
                          <xsl:if test="@ipList != '.*' and @ipList != 'default'">
                            <xsl:value-of select="@ipList"/>
                          </xsl:if>
                        </xsl:attribute>
                    </xsl:element>
</td>
<td valign="top">
<select name="theList" size="2" onChange="javascript:doSelect()" style="width: 100px;">
<option>127.0.0.1</option>
</select>
</td>
<td valign="top">
<input name="addButton" type="button" value="+" onClick="javascript:doAdd()" style="width: 
25px; height: 20px;"/><br/>
<input name="deleteButton" type="button" value="-" disabled="disabled" 
onClick="javascript:doDelete()" style="width: 25px; height: 20px;"/>
</td>
</tr>
</table>
                  <nobr>
                   <xsl:element name="input">
                      <xsl:attribute name="type">radio</xsl:attribute>
                      <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipListValue</xsl:attribute>
                      <xsl:attribute name="id"><xsl:value-of select="@role"/>.ipListValue.default</xsl:attribute>
                      <xsl:attribute name="value">default</xsl:attribute>
                      <xsl:if test="@ipList = 'default'">
                        <xsl:attribute name="checked">checked</xsl:attribute>
                      </xsl:if>
                    </xsl:element> 
                    <xsl:element name="label">
                      <xsl:attribute name="for"><xsl:value-of select="@role"/>.ipListValue.default</xsl:attribute>
                      Use Default
                    </xsl:element>
                  </nobr>
                </td>
              </tr>
              <tr>
                <td colspan="4" align="right">
                &#160;
                </td>
              </tr>
            </xsl:for-each>
          </table>
                <input type="submit" value="Save Changes..."/><br/>
          </form>
        </center>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>