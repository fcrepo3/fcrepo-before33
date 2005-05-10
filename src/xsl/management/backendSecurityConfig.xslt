<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
            } else {
              var spaceDelimitedList = "";
              var zlist = theForm.elements[role + ".ipList.select"];
              for (var k = 0; k < zlist.options.length; k++) {
                if (k > 0) spaceDelimitedList = spaceDelimitedList + " ";
                spaceDelimitedList = spaceDelimitedList + zlist.options[k].value;
              }
              theForm.elements[role + ".ipList"].value = spaceDelimitedList;
            }
          }
        }
      }
    }
    return true;
}

function doSelect(role) {
  theForm.elements[role + ".ipList.deleteButton"].disabled = false;
}

function doAdd(role) {
  var ip = prompt("Enter a new IP address pattern.\nThis will be used to match allowed hosts.", "");
  if (ip == null || ip.length == 0) return;
  var select = theForm.elements[role + ".ipList.select"];
  select.options[select.options.length] = new Option(ip, ip);
  if (select.options.length > 1) {
    select.size = select.options.length;
  }
}

function doDelete(role) {
  var select = theForm.elements[role + ".ipList.select"];
  var oldSelectedIndex = select.selectedIndex;
  select.options[oldSelectedIndex] = null;
  if (select.options.length > 0) {
    if (select.options.length == oldSelectedIndex) {
      select.selectedIndex = oldSelectedIndex - 1;
    } else {
      select.selectedIndex = oldSelectedIndex;
    }
  } else {
    theForm.elements[role + ".ipList.deleteButton"].disabled = true;
  }
  if (select.options.length > 1) {
    select.size = select.options.length;
  }
}

function customSelected(role) {
  var select = theForm.elements[role + ".ipList.select"];
  select.disabled = false;
  theForm.elements[role + ".ipList.addButton"].disabled = false;
}

function defaultSelected(role) {
  var select = theForm.elements[role + ".ipList.select"];
  select.selectedIndex = -1;
  select.disabled = true;
  theForm.elements[role + ".ipList.addButton"].disabled = true;
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
  NOTE: IP address patterns are regular expressions, as defined at <a href="http://www.w3.org/TR/xpath-functions/#regex-syntax">http://www.w3.org/TR/xpath-functions/#regex-syntax</a>
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
              <td bgcolor="#cccccc"><nobr><strong><font color="#000000">Host Patterns</font></strong></nobr></td>
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
                    <xsl:attribute name="for"><xsl:value-of select="@role"/>.ssl.default</xsl:attribute> Use
                    <acronym title="(Default is Optional)" style="border-bottom-style: solid; border-bottom-width: 1">Default</acronym>
                  </xsl:element>
                 </nobr>
                </td>
                <td valign="top">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td valign="top">
                    <xsl:element name="input">
                      <xsl:attribute name="type">radio</xsl:attribute>
                      <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipListValue</xsl:attribute>
                      <xsl:attribute name="value">custom</xsl:attribute>
                        <xsl:if test="@ipList != 'default'">
                          <xsl:attribute name="checked">checked</xsl:attribute>
                        </xsl:if>
                      <xsl:attribute name="onClick">javascript:customSelected('<xsl:value-of select="@role"/>')</xsl:attribute>
                    </xsl:element>
                    <xsl:element name="input">
                        <xsl:attribute name="type">hidden</xsl:attribute>
                        <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipList</xsl:attribute>
                    </xsl:element>
</td>
<td valign="top">
  <xsl:variable name="ips">
    <xsl:call-template name="tokenize">
      <xsl:with-param name="string" select="@ipList"/>
    </xsl:call-template>
  </xsl:variable>
<xsl:element name="select">
  <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipList.select</xsl:attribute>
  <xsl:choose>
    <xsl:when test="count($ips/token) &gt; 2">
      <xsl:attribute name="size">
        <xsl:value-of select="count($ips/token)"/>
      </xsl:attribute>
    </xsl:when>
    <xsl:otherwise>
      <xsl:attribute name="size">2</xsl:attribute>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:attribute name="onChange">javascript:doSelect('<xsl:value-of select="@role"/>')</xsl:attribute>
  <xsl:attribute name="style">width: 150px;</xsl:attribute>
                        <xsl:if test="@ipList = 'default'">
                          <xsl:attribute name="disabled">disabled</xsl:attribute>
                        </xsl:if>
  <xsl:for-each select="$ips/token"> 
      <xsl:if test=". != 'default'">
    <option>
      <xsl:value-of select="."/>
    </option>
      </xsl:if>
  </xsl:for-each>
</xsl:element>
</td>
<td valign="top">
<xsl:element name="input">
  <xsl:attribute name="type">button</xsl:attribute>
  <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipList.addButton</xsl:attribute>
  <xsl:attribute name="value">+</xsl:attribute>
  <xsl:attribute name="onClick">javascript:doAdd('<xsl:value-of select="@role"/>')</xsl:attribute>
  <xsl:attribute name="style">width: 25px; height:20px;</xsl:attribute>
  <xsl:if test="@ipList = 'default'">
    <xsl:attribute name="disabled">disabled</xsl:attribute>
  </xsl:if>
</xsl:element>
<xsl:element name="input">
  <xsl:attribute name="type">button</xsl:attribute>
  <xsl:attribute name="name"><xsl:value-of select="@role"/>.ipList.deleteButton</xsl:attribute>
  <xsl:attribute name="value">-</xsl:attribute>
  <xsl:attribute name="onClick">javascript:doDelete('<xsl:value-of select="@role"/>')</xsl:attribute>
  <xsl:attribute name="style">width: 25px; height:20px;</xsl:attribute>
  <xsl:attribute name="disabled">disabled</xsl:attribute>
</xsl:element>
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
                      <xsl:attribute name="onClick">javascript:defaultSelected('<xsl:value-of select="@role"/>')</xsl:attribute>
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


<xsl:template name="tokenize">
   <xsl:param name="string"
              select="''" />
   <xsl:param name="delimiters"
              select="' &#x9;
'" />
   <xsl:choose>
      <xsl:when test="not($string)" />
      <xsl:when test="not($delimiters)">
         <xsl:call-template name="_tokenize-characters">
            <xsl:with-param name="string"
                            select="$string" />
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="_tokenize-delimiters">
            <xsl:with-param name="string"
                            select="$string" />
            <xsl:with-param name="delimiters"
                            select="$delimiters" />
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>
<xsl:template name="_tokenize-characters">
   <xsl:param name="string" />
   <xsl:if test="$string">
      <token>
         <xsl:value-of select="substring($string, 1, 1)" />
      </token>
      <xsl:call-template name="_tokenize-characters">
         <xsl:with-param name="string"
                         select="substring($string, 2)" />
      </xsl:call-template>
   </xsl:if>
</xsl:template>
<xsl:template name="_tokenize-delimiters">
   <xsl:param name="string" />
   <xsl:param name="delimiters" />
   <xsl:variable name="delimiter"
                 select="substring($delimiters, 1, 1)" />
   <xsl:choose>
      <xsl:when test="not($delimiter)">
         <token>
            <xsl:value-of select="$string" />
         </token>
      </xsl:when>
      <xsl:when test="contains($string, $delimiter)">
         <xsl:if test="not(starts-with($string, $delimiter))">
            <xsl:call-template name="_tokenize-delimiters">
               <xsl:with-param name="string"
                               select="substring-before($string, $delimiter)" />
               <xsl:with-param name="delimiters"
                               select="substring($delimiters, 2)" />
            </xsl:call-template>
         </xsl:if>
         <xsl:call-template name="_tokenize-delimiters">
            <xsl:with-param name="string"
                            select="substring-after($string, $delimiter)" />
            <xsl:with-param name="delimiters"
                            select="$delimiters" />
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="_tokenize-delimiters">
            <xsl:with-param name="string"
                            select="$string" />
            <xsl:with-param name="delimiters"
                            select="substring($delimiters, 2)" />
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>