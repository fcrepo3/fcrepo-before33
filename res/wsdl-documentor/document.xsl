<?xml version="1.0" ?>
<?xml-stylesheet href="simplifiedxslt.xslt" type="text/xsl"?>

<!-- Copyright (C) 2002 Cape Clear Software. All rights reserved.-->
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
	xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
	xmlns:http="http://schemas.xmlsoap.org/wsdl/http/"
	xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/">

<xsl:output method="html" indent="yes"/>

<xsl:template match="/">
<html>
	<head>
		<title>WSDL Documentation</title>
	</head>

	<body>
      <xsl:for-each select="//wsdl:operation">
        <xsl:sort case-order="upper-first" select="@name"/>

        <!-- method name -->
        <h3><xsl:value-of select="@name"/></h3><dir>

        <code>
        <!-- return type -->
        <xsl:if test="not(wsdl:output)">void</xsl:if>
        <xsl:variable name="outmsgname" select="wsdl:output/@message"/>
        <xsl:for-each select="//wsdl:message[@name=$outmsgname]">
          <xsl:for-each select="wsdl:part">
            <xsl:value-of select="@type"/>
            <xsl:if test="not (position()=last())"> 
              <xsl:text>, </xsl:text> 
            </xsl:if> 
          </xsl:for-each>
        </xsl:for-each>
        <xsl:text>&#160;</xsl:text>
	
        <!-- method name -->
        <xsl:value-of select="@name"/>(

        <!-- params -->
        <xsl:variable name="inmsgname" select="wsdl:input/@message"/>
        <xsl:for-each select="//wsdl:message[@name=$inmsgname]">
          <xsl:for-each select="wsdl:part">
            <xsl:value-of select="@type"/>&#160;<xsl:value-of select="@name"/>
            <xsl:if test="not (position()=last())"><xsl:text>, </xsl:text></xsl:if> 
          </xsl:for-each>
        </xsl:for-each>
        )
	
        <!-- exceptions -->
        <xsl:if test="wsdl:fault">
          throws
          <xsl:for-each select="wsdl:fault">
            <xsl:value-of select="@message" />
          </xsl:for-each>
        </xsl:if>

        </code>

        <!-- documentation -->
        <dir><p><xsl:value-of select="wsdl:documentation"/></p></dir>

        <!-- params -->
		<xsl:if test="wsdl:input">
  	      <p>
	      <b>Parameters:</b>
          <dir>
		  <xsl:variable name="inmsgname2" select="wsdl:input/@message"/>
		  <xsl:for-each select="//wsdl:message[@name=$inmsgname2]">
            <xsl:for-each select="wsdl:part">
              <code><xsl:value-of select="@name"/> - </code>
  	          <xsl:value-of select="wsdl:documentation"/><br></br>
            </xsl:for-each>
          </xsl:for-each>
          </dir>
          </p>
        </xsl:if>

        <!-- returns -->
		<xsl:if test="wsdl:output">
		  <p>
		  <b>Returns:</b>
		  <dir>
		  <xsl:variable name="outmsgname2" select="wsdl:output/@message"/>
		  <xsl:for-each select="//wsdl:message[@name=$outmsgname2]">
            <xsl:for-each select="wsdl:part">
  	          <xsl:value-of select="wsdl:documentation"/><br></br>
            </xsl:for-each>
          </xsl:for-each>
          </dir>
		  </p>
		</xsl:if>
        </dir>

        <hr size="1"></hr>

    </xsl:for-each>
  </body>
</html>
</xsl:template>

<!--

<xsl:template match="wsdl:part" >
	<xsl:call-template name="documentation"/>
	<xsl:call-template name="newline"/>
	<xsl:call-template name="keyword"/>
	<xsl:value-of select="@name" />
	<xsl:if test="@element">
		<xsl:call-template name="keyword">
			<xsl:with-param name="word" select="' element'"/>
		</xsl:call-template>
		<xsl:value-of select="@element"/></xsl:if>
	<xsl:if test="@type">
		<xsl:call-template name="keyword">
			<xsl:with-param name="word" select="' type'"/>
		</xsl:call-template>
		<xsl:value-of select="@type"/>
	</xsl:if>
	<xsl:apply-templates />
</xsl:template>

-->

</xsl:stylesheet>
