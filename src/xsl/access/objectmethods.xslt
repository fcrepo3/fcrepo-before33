<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
<xsl:output method="html" indent="yes"/> 
<xsl:template match="/">
	<html>
		<head>
			<title>Object Methods HTML Table View</title>
		</head>
		<body>
			<center>
			<table border="1" cellpadding="5">
				<xsl:apply-templates/>
			</table>
			</center>
		</body>
	</html>
</xsl:template>

<xsl:template match="object">
<tr>
	<td><b><font size='+2'>Object PID</font></b></td>
	<td><b><font size='+2'>Version</font></b></td>
	<td><b><font size='+2'>BDEF</font></b></td>
	<td><b><font size='+2'>Method Name</font></b></td>
	<td>&#x00A0;</td>
	<td><b><font size='+2'>Parm Name</font></b></td>
	<td colspan="100%"><b><font size='+2'>Allowed Parm Values<br>(Select A Value For Each Parm)</br></font></b></td>
</tr>
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="bdef">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="method">
	<form name="parmResolverForm" method="post" action="/fedora/getAccessParmResolver?">
	<tr>
		<td><font color="blue"><xsl:value-of select="../../@pid"/></font></td>
		<td>&#x00A0;<font color="green"><xsl:value-of select="../../@dateTime"/></font></td>
		<td><font color="green"><xsl:value-of select="../@pid"/></font></td>
		<td><font color="red"><xsl:value-of select="@name"/></font></td>
		<td>
			<input>
				<xsl:attribute name="type">hidden</xsl:attribute>
				<xsl:attribute name="name">PID</xsl:attribute>
				<xsl:attribute name="value"><xsl:value-of select="../../@pid"/></xsl:attribute>				
			</input>
			<input>
				<xsl:attribute name="type">hidden</xsl:attribute>
				<xsl:attribute name="name">bDefPID</xsl:attribute>
				<xsl:attribute name="value"><xsl:value-of select="../@pid"/></xsl:attribute>			
			</input>
			<input>
				<xsl:attribute name="type">hidden</xsl:attribute>
				<xsl:attribute name="name">methodName</xsl:attribute>
				<xsl:attribute name="value"><xsl:value-of select="@name"/></xsl:attribute>			
			</input>
			<input type="submit" name="Submit" value="RunDissemination"></input>
		</td>
		<xsl:choose>
			<xsl:when test="./parm/@parmName">
				<xsl:call-template name="parmTemplate"/>
			</xsl:when>
			<xsl:otherwise>
				<td colspan="100%">
					<font color="purple">No Parameters Defined</font>
				</td>	
			</xsl:otherwise>
		</xsl:choose>
	</tr>
	</form>
<xsl:apply-templates/>
</xsl:template>

<xsl:template   name="parmTemplate" >
<xsl:for-each select="parm">
	<xsl:choose>
		<xsl:when test="position()=1">
			<xsl:choose>
				<xsl:when test="parmDomainValues">
					<td><b><font color="purple">
						<xsl:value-of select="@parmName"/>
						</font></b>
					</td>
					<xsl:call-template name="valueTemplate"/>
				</xsl:when>
				<xsl:otherwise>
					<td><b><font color="purple">
						<xsl:value-of select="@parmName"/>
						</font></b>
					</td>
					<xsl:call-template name="noValuesTemplate"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<xsl:choose>
				<xsl:when test="parmDomainValues">
					<tr>
						<td colspan="5" rowspan="1"></td>
						<td>
							<b><font color="purple">
							<xsl:value-of select="@parmName"/>
							</font></b>
						</td>
						<xsl:call-template name="valueTemplate"/>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<tr>
						<td colspan="5" rowspan="1"></td>
						<td>
							<b><font color="purple">
							<xsl:value-of select="@parmName"/>
							</font></b>
						</td>
						<xsl:call-template name="noValuesTemplate"/>
					</tr>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>
	</xsl:choose>
</xsl:for-each>
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="value">
</xsl:template>

<xsl:template name="valueTemplate">
<xsl:for-each select="parmDomainValues/value">
	<td><xsl:value-of select="."/></td>
	<td>
		<input>
			<xsl:attribute name="type">radio</xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="../../@parmName"/></xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
		</input>
	</td>
</xsl:for-each>
</xsl:template>

<xsl:template name="noValuesTemplate">
	<td>
		<input>
			<xsl:attribute name="type">text</xsl:attribute>
			<xsl:attribute name="size">10</xsl:attribute>
			<xsl:attribute name="maxlength">32</xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="@parmName"/></xsl:attribute>
			<xsl:attribute name="value"></xsl:attribute>
		</input>
	</td>
</xsl:template>

</xsl:stylesheet>