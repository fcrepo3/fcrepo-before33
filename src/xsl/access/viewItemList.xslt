<?xml version="1.0" encoding="UTF-8"?>
<?xmlspysamplexml C:\mellon\src\xsl\access\getItemList.xml?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes"/>
	<xsl:template match="objectItemList">
		<html>
			<head>
				<title>Object Items HTML Presentation</title>
			</head>
			<body>
				<center>
					<table width="784" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="141" height="134" valign="top">
								<img src="http://www.fedora.info/assets/newlogo2.jpg" width="141" height="134"/>
							</td>
							<td width="643" valign="top">
								<center>
									<h2>Fedora Digital Object</h2>
									<h3>Default Disseminator - Item List View</h3>
								</center>
							</td>
						</tr>
					</table>
					<hr/>
					<font size="+1" color="blue">Object Identifier (PID):       </font>
					<font size="+1">
						<xsl:value-of select="@PID"/>
					</font>
					<hr/>
					<p/>
					<table width="784" border="1" cellpadding="5" cellspacing="5" bgcolor="silver">
						<tr>
							<td>
								<b>
									<font size="+2">Item ID</font>
								</b>
							</td>
							<td>
								<b>
									<font size="+2">Item Description</font>
								</b>
							</td>
							<td>
								<b>
									<font size="+2">MIME Type</font>
								</b>
							</td>
						</tr>
						<xsl:for-each select="//item">
							<tr>
								<td>
									<xsl:value-of select="itemId"/>
								</td>
								<td>
									<xsl:variable name="item-url">
										<xsl:value-of select="itemURL"/>
									</xsl:variable>
									<a href="{$item-url}">
										<xsl:value-of select="itemLabel"/>
									</a>
								</td>
								<td>
									<xsl:value-of select="itemMIMEType"/>
								</td>
							</tr>
						</xsl:for-each>
					</table>
				</center>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
