<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY titleText              "Fedora Backend Security Configuration">
  <!ENTITY saveButtonText         "Save Changes">
  <!ENTITY defaultText            "Default">
  <!ENTITY fedoraInternalCallText "Internal">
  <!ENTITY roleBeforeText         "">
  <!ENTITY roleAfterText          "">
  <!ENTITY desc                   "ns:serviceSecurityDescription">
]>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns="info:fedora/fedora-system:def/beSecurity#">
  <xsl:template match="/">
    <html>
      <head>
        <title>&titleText;</title>
        <script>
<![CDATA[

var activeLayer = null;

function setActiveLayer(layerName) {
  if (activeLayer != null) {
    activeLayer.style.visibility = "hidden";
  }
  activeLayer = document.getElementById(layerName);
  activeLayer.style.visibility = "visible";
}

function init() {
  setActiveLayer('default');
  document.getElementById("activeLayerChooser").selectedIndex = 0;
}

]]>
        </script>
        <style type="text/css">
<![CDATA[

#header {
    clear: both;
    margin-bottom: 10px;
    min-height: 70px;
    width: 100%;
}

#title {
    text-align: center; 
}

#body {
  position: absolute;
  left: 10;
  top: 90;
}

#activeLayerChooser {
  width: 215px;
}

.innerTop {
  background: #ddddff;
  border-left: solid 1px #999999;
  border-top: solid 1px #999999;
  border-right: solid 1px #999999;
  padding: 5px;
  margin: 0px;
  width: 200;
}

* html .innerTop {
	width: 200px; 
	w\idth: 210px; 
}

.innerBottom {
  position: absolute;
  top: 269;
  left: 10;
  background: #ddddff;
  border-bottom: solid 1px #999999;
  border-left: solid 1px #999999;
  border-right: solid 1px #999999;
  padding: 5px;
  margin: 0px;
  width: 600;
}

* html .innerBottom {
	width: 600px; 
	w\idth: 610px; 
}

#left {
  z-index: 3;
  border-left: solid 3px #000000;
  border-top: solid 3px #000000;
  border-bottom: solid 3px #000000;
  position: absolute;
  left: 0;
  top: 0;
  width: 225;
  background: #bbbbee;
  padding: 5px;
  margin: 0px;
}

* html #left {  /* This is the Tan hack */
	width: 225px; 
	w\idth: 238px; 
}

.right {
  z-index: 2;
  border-right: solid 3px #000000;
  border-top: solid 3px #000000;
  border-bottom: solid 3px #000000;
  border-left: solid 3px #000000;
  position: absolute;
  left: 235;
  top: 0;
  width: 620;
  background: #bbbbee;
  padding: 5px;
  margin: 0px;
  visibility: hidden;
}

* html .right {
	width: 620px; 
	w\idth: 636px; 
}

p {
  font-family: sans-serif;
  font-size: 16px;
  padding: 0px;
  margin: 0px;
}

select {
  font-family: sans-serif;
  font-size: 12px;
  padding: 0px;
  margin: 0px;
}

h2 {
  font-family: sans-serif;
  font-size: 20px;
  padding-top: 1px;
  padding-bottom: 1px;
  margin-top: 2px;
  margin-bottom: 2px;
  background: #000000;
  text-color: white;
  color: white;
}

h3 {
  font-family: sans-serif;
  font-size: 18px;
  padding-top: 1px;
  margin-top: 2px;
  padding-bottom: 1px;
  margin-bottom: 2px;
  border-bottom: dashed 1px #000000;
}

]]>
        </style>
      </head>
      <body onLoad="javascript:init()">
<div id="header">
<table border="0" width="650">
  <tr>
    <td>
      <img src="/images/newlogo2.jpg" width="141" height="134"/>
    </td>
    <td valign="top">
    <center>
    <span style="font-weight: bold; color: #000000; margin-top: 4px; margin-bottom: 4px; font-size: 24px; line-height: 110%; padding-top: 8px; padding-bottom: 4px;">Fedora Backend Security Configuration</span><br/>
    YES, THE TEXT AND LAYOUT STILL NEED A FEW TWEAKS!!
    </center>
    </td>
  </tr>
</table>
</div>
        <div id="body">
        <form>
          <div id="left">
            <xsl:element name="select">
              <xsl:attribute name="id">activeLayerChooser</xsl:attribute>
              <xsl:attribute name="size">
                <!-- the number of non-slash-containing configs -->
                <xsl:choose>
                  <xsl:when test="count(/&desc;/&desc;[not(contains(@role,'/'))]) &lt; 10">
                    <xsl:value-of select="count(/&desc;/&desc;[not(contains(@role,'/'))])"/>
                  </xsl:when>
                  <xsl:otherwise>10</xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <xsl:attribute name="onChange">
                javascript:setActiveLayer(this.options[this.selectedIndex].value)
              </xsl:attribute>
              <option value="default" selected="selected">&defaultText;</option>
              <xsl:for-each select="/&desc;/&desc;[not(contains(@role, '/')) and contains(@role, ':')]">
                <xsl:element name="option">
                  <xsl:attribute name="value"><xsl:value-of select="@role"/></xsl:attribute>
                  &roleBeforeText;<xsl:value-of select="@role"/>&roleAfterText;
                </xsl:element>
              </xsl:for-each>
            </xsl:element>
            <p>Local IP: <input type="text" cols="12" value="127.0.0.1"/><br/>
            User: <input type="text" cols="12" value="backendUser"/><br/>
            Pass: <input type="password" cols="12"/>
            </p>
            <center>
              <input type="submit" value="&saveButtonText;"/>
            </center>
          </div>
          
          <div id="default" class="right">
          <h2>Default Settings</h2>
          <p>These settings will be used by default.</p>
          </div>
          
          <xsl:for-each select="/&desc;/&desc;[not(contains(@role, '/')) and contains(@role, ':')]">
            <xsl:element name="div">
              <xsl:attribute name="id"><xsl:value-of select="@role"/></xsl:attribute>
              <xsl:attribute name="class">right</xsl:attribute>
              <xsl:attribute name="style">height: 462;</xsl:attribute>
              
              <h2><xsl:value-of select="@role"/></h2>
        
              <table border="0" cellpadding="5" cellspacing="0" width="100%">
                <tr>
                  <td valign="top">
                    <h3>Calls to Service</h3>
<p>
<table border="0" cellpadding="0" cellspacing="3">

<tr>
<td valign="top" align="right"><p><nobr>SSL:</nobr></p></td>
<td valign="top">
<span class="regular">
                      <select>
                        <option>Yes</option>
                        <option>No</option>
                        <option>[inherit]</option>
                      </select>
</span>
</td>
</tr>

<tr>
<td valign="top" align="right"><p><nobr>Basic Auth:</nobr></p></td>
<td valign="top">
                      <select>
                        <option>Yes</option>
                        <option>No</option>
                        <option>[inherit]</option>
                      </select>
</td>
</tr>

<tr>
<td valign="top" align="right"><nobr>Username:</nobr></td>
<td valign="top">
<input type="text"/>
</td>
</tr>

<tr>
<td valign="top" align="right"><nobr>Password:</nobr></td>
<td valign="top">
<input type="text"/>
</td>
</tr>


</table>
</p>



                  </td>
                  <td valign="top">
                    <h3>Callbacks to Fedora</h3>
                    <p>
<table border="0" cellpadding="0" cellspacing="3">
<tr>
<td valign="top" align="right"><nobr>SSL:</nobr></td>
<td valign="top" colspan="3">
                      <select>
                        <option>Yes</option>
                        <option>No</option>
                        <option>[inherit]</option>
                      </select>
</td>
</tr>
<tr>
<td valign="top" align="right"><nobr>Basic Auth:</nobr></td>
<td valign="top" colspan="3">
                      <select>
                        <option>Yes</option>
                        <option>No</option>
                        <option>[inherit]</option>
                      </select>
</td>
</tr>
<tr>
<td valign="top" align="right"><nobr>Allowed IPs:</nobr></td>
<td valign="top">
  <select>
    <option>[inherit]</option>
    <option>Specify</option>
  </select>
</td>
<td valign="top">
<xsl:element name="select">
  <xsl:attribute name="size">2</xsl:attribute>
  <xsl:attribute name="style">width: 130px;</xsl:attribute>
  <option>127.0.0.1</option>
  <option>255.255.255.255</option>
</xsl:element>
</td>
<td valign="top">
<xsl:element name="input">
  <xsl:attribute name="type">button</xsl:attribute>
  <xsl:attribute name="name">all.ipList.addButton</xsl:attribute>
  <xsl:attribute name="value">+</xsl:attribute>
  <xsl:attribute name="onClick">javascript:doAdd('all')</xsl:attribute>
  <xsl:attribute name="style">width: 25px; height:20px;</xsl:attribute>
</xsl:element><br/>
<xsl:element name="input">
  <xsl:attribute name="type">button</xsl:attribute>
  <xsl:attribute name="name">all.ipList.deleteButton</xsl:attribute>
  <xsl:attribute name="value">-</xsl:attribute>
  <xsl:attribute name="onClick">javascript:doDelete('all')</xsl:attribute>
  <xsl:attribute name="style">width: 25px; height:20px;</xsl:attribute>
</xsl:element>
</td>
</tr>
</table>

                    </p>
                  </td>
                </tr>
                <tr>
                  <td colspan="2">
                    <h3 style="border: none;">Method Overrides</h3>
                    <div class="innerTop">
                      <select size="3" style="width:200;">
                        <xsl:variable name="startString"><xsl:value-of select="@role"/>/</xsl:variable>
                        <xsl:for-each select="/&desc;/&desc;[starts-with(@role, $startString)]">
                          <option><xsl:value-of select="substring-after(@role, '/')"/></option>
                        </xsl:for-each>
                      </select>
                    </div>
                    <div class="innerBottom">
                    
              <table border="0" cellpadding="5" cellspacing="0" width="100%">
                <tr>
                  <td valign="top">
                    <h3>Calls to Service</h3>
<p>
<table border="0" cellpadding="0" cellspacing="3">

<tr>
<td valign="top" align="right"><nobr>SSL:</nobr></td>
<td valign="top">
                      <select>
                        <option>Yes</option>
                        <option>No</option>
                        <option>[inherit]</option>
                      </select>
</td>
</tr>

<tr>
<td valign="top" align="right"><nobr>Basic Auth:</nobr></td>
<td valign="top">
                      <select>
                        <option>Yes</option>
                        <option>No</option>
                        <option>[inherit]</option>
                      </select>
</td>
</tr>

<tr>
<td valign="top" align="right"><nobr>Username:</nobr></td>
<td valign="top">
<input type="text"/>
</td>
</tr>

<tr>
<td valign="top" align="right"><nobr>Password:</nobr></td>
<td valign="top">
<input type="text"/>
</td>
</tr>


</table>
</p>



                  </td>
                  <td valign="top">
                    <h3>Callbacks to Fedora</h3>
                    <p>
<table border="0" cellpadding="0" cellspacing="3">
<tr>
<td valign="top" align="right"><nobr>SSL:</nobr></td>
<td valign="top" colspan="3">
                      <select>
                        <option>Yes</option>
                        <option>No</option>
                        <option>[inherit]</option>
                      </select>
</td>
</tr>
<tr>
<td valign="top" align="right"><nobr>Basic Auth:</nobr></td>
<td valign="top" colspan="3">
                      <select>
                        <option>Yes</option>
                        <option>No</option>
                        <option>[inherit]</option>
                      </select>
</td>
</tr>
<tr>
<td valign="top" align="right"><nobr>Allowed IPs:</nobr></td>
<td valign="top">
  <select>
    <option>[inherit]</option>
    <option>Specify</option>
  </select>
</td>
<td valign="top">
<xsl:element name="select">
  <xsl:attribute name="size">2</xsl:attribute>
  <xsl:attribute name="style">width: 130px;</xsl:attribute>
  <option>127.0.0.1</option>
  <option>255.255.255.255</option>
</xsl:element>
</td>
<td valign="top">
<xsl:element name="input">
  <xsl:attribute name="type">button</xsl:attribute>
  <xsl:attribute name="name">all.ipList.addButton</xsl:attribute>
  <xsl:attribute name="value">+</xsl:attribute>
  <xsl:attribute name="onClick">javascript:doAdd('all')</xsl:attribute>
  <xsl:attribute name="style">width: 25px; height:20px;</xsl:attribute>
</xsl:element><br/>
<xsl:element name="input">
  <xsl:attribute name="type">button</xsl:attribute>
  <xsl:attribute name="name">all.ipList.deleteButton</xsl:attribute>
  <xsl:attribute name="value">-</xsl:attribute>
  <xsl:attribute name="onClick">javascript:doDelete('all')</xsl:attribute>
  <xsl:attribute name="style">width: 25px; height:20px;</xsl:attribute>
</xsl:element>
</td>
</tr>
</table>

                    </p>
                  </td>
                </tr>
                </table>



                    </div>
                  </td>
                </tr>
              </table>
              

<!--
              <xsl:value-of select="@callBasicAuth"/>

              <h3>callBasicAuth</h3>
              <xsl:value-of select="@callBasicAuth"/>

              <h3>callUsername</h3>
              <xsl:value-of select="@callUsername"/>

              <h3>callPassword</h3>
              <xsl:value-of select="@callPassword"/>

              </div>
              <h3>callbackSSL</h3>
              <xsl:value-of select="@callbackSSL"/>

              <h3>callbackBasicAuth</h3>
              <xsl:value-of select="@callbackBasicAuth"/>

              <h3>iplist</h3>
              <xsl:value-of select="@iplist"/>
-->              
            </xsl:element>
          </xsl:for-each>

        </form>
        </div>
      </body>
    </html>
  </xsl:template>

<!--
<serviceSecurityDescription xmlns="info:fedora/fedora-system:def/beSecurity#" 
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                            xsi:schemaLocation="info:fedora/fedora-system:def/beSecurity# http://www.fedora.info/definitions/1/0/api/beSecurity.xsd"
                            role="default" 
                            callSSL="false" 
                            callbackSSL="false" 
                            callBasicAuth="false" 
                            callbackBasicAuth="false">
  <serviceSecurityDescription role="fedoraInternalCall" 
                              callBasicAuth="false" 
                              callSSL="false" 
                              callbackBasicAuth="false" 
                              callbackSSL="false" 
                              callUsername="fedoraIntCallUser" 
                              callPassword="changeme" 
                              iplist="127.0.0.1"/>
  <serviceSecurityDescription role="demo:2" iplist="127.0.0.1"/>
  <serviceSecurityDescription role="demo:2/getShorty" iplist="127.0.0.1"/>	
  <serviceSecurityDescription role="demo:3" iplist="128.143.22.236"/>
  <serviceSecurityDescription role="demo:4" iplist="128.143.22.200"/>
  <serviceSecurityDescription role="demo:9" iplist="128.143.22.200"/>
  <serviceSecurityDescription role="demo:13" iplist="127.0.0.1"/>
  <serviceSecurityDescription role="demo:16" iplist="128.143.22.236"/>
  <serviceSecurityDescription role="demo:20" iplist="127.0.0.1"/>
  <serviceSecurityDescription role="demo:25" iplist="127.0.0.1"/>
  <serviceSecurityDescription role="demo:28" iplist="128.143.22.200"/>
  <serviceSecurityDescription role="demo:DualResImageCollection" iplist="127.0.0.1"/>
  <serviceSecurityDescription role="demo:DualResImageImpl" iplist="127.0.0.1"/>
</serviceSecurityDescription>
-->

</xsl:stylesheet>
