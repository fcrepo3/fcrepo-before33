<?xml version="1.0" encoding="UTF-8"?>
<?xmlspysamplexml C:\mellon\src\xsl\access\getItemList.xml?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes"/>
	<xsl:template match="objectDatastreams">
		<html>
			<head>
				<title>Object Datastreams HTML Presentation</title>
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
									<h2>Fedora Digital Object</h2>
									<h3>List Datastreams</h3>
								</center>
							</td>
						</tr>
					</table>
					<hr/>
					<font size="+1" color="blue">Object Identifier (PID):       </font>
					<font size="+1">
						<xsl:value-of select="@pid"/>
					</font>
					<p/>
					<xsl:choose>
						<xsl:when test="@asOfDateTime">
							<font size="+1" color="blue">Version Date:   </font>
							<font size="+1"><xsl:value-of select="@asOfDateTime"/></font>
						</xsl:when>
						<xsl:otherwise>
							<font size="+1" color="blue">Version Date:   </font>
							<font size="+1">current</font>	
						</xsl:otherwise>
					</xsl:choose>		
					<hr/>			
					<table width="784" border="1" cellpadding="5" cellspacing="5" bgcolor="silver">
						<tr>
							<td>
								<b>
									<font size="+2">Datastream ID</font>
								</b>
							</td>
							<td>
								<b>
									<font size="+2">Datastream Label</font>
								</b>
							</td>
							<td>
								<b>
									<font size="+2">MIME Type</font>
								</b>
							</td>
						</tr>
						<xsl:for-each select="//datastream">
							<tr>
								<td>
									<xsl:value-of select="@dsid"/>
								</td>
								<td>
									<xsl:choose>
										<xsl:when test="../@asOfDateTime">
											<xsl:variable name="datastream-url">
												<xsl:text>/fedora/get/</xsl:text><xsl:value-of select="../@pid"/><xsl:text>/</xsl:text>
												<xsl:value-of select="@dsid"/><xsl:text>/</xsl:text><xsl:value-of select="../@asOfDateTime"/>
											</xsl:variable>
											<a href="{$datastream-url}">
												<xsl:value-of select="@label"/>
											</a>
									</xsl:when>
									<xsl:otherwise>
										<xsl:variable name="datastream-url">
											<xsl:text>/fedora/get/</xsl:text><xsl:value-of select="../@pid"/><xsl:text>/</xsl:text><xsl:value-of select="@dsid"/>
										</xsl:variable>
										<a href="{$datastream-url}">
											<xsl:value-of select="@label"/>
										</a>									
									</xsl:otherwise>
									</xsl:choose>									
								</td>
								<td>
									<xsl:value-of select="@mimeType"/>
								</td>
							</tr>
						</xsl:for-each>
					</table>
				</center>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
