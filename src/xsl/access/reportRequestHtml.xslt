<?xml version="1.0" ?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
	<xsl:param name="GENERAL-TITLE"/>
	<xsl:param name="SPECIFIC-TITLE"/>

	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of select="$GENERAL-TITLE"/> - <xsl:value-of select="$SPECIFIC-TITLE"/>
				</title>
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
									<h2><xsl:value-of select="$GENERAL-TITLE"/></h2>
									<h3><xsl:value-of select="$SPECIFIC-TITLE"/></h3>
								</center>
							</td>
						</tr>
					</table>
					<form method="post" action="/fedora/report">
						<center>
							<table border="0" cellpadding="6" cellspacing="0">
								<tr>
									<td valign="top">
										Report on
										<font>
											<select name="report" size="1" multiple="false">
												<option value="all objects" selected="selected">all objects</option>
												<option value="active objects">active objects</option>
												<option value="inactive objects">inactive objects</option>
												<option value="all bdefs">all bdefs</option>
												<option value="active bdefs">active bdefs</option>
												<option value="inactive bdefs">inactive bdefs</option>																								
												<option value="all bmechs">all bmechs</option>
												<option value="active bmechs">active bmechs</option>
												<option value="inactive bmechs">inactive bmechs</option>																								
											</select>
										</font>
										<font>
											<select name="dateRange" size="1" multiple="false">
												<option value="none" selected="selected">(regardless of when created or last modified)</option>
												<option value="mltd">last modified within past 24 hours</option>
												<option value="mgtd">last modified more than 24 hours ago</option>
												<option value="mltw">last modified within past 7 days</option>
												<option value="mgtw">last modified more than 7 days ago</option>
												<option value="mltm">last modified within past 30 days</option>
												<option value="mgtm">last modified more than 30 days ago</option>
												<option value="mlty">last modified within past 1 year</option>
												<option value="mgty">last modified more than 1 year ago</option>
												<option value="cltd">created within past 24 hours</option>
												<option value="cgtd">created more than 24 hours ago</option>
												<option value="cltw">created within past 7 days</option>												
												<option value="cgtw">created more than 7 days ago</option>
												<option value="cltm">created within past 30 days</option>												
												<option value="cgtm">created more than 30 days ago</option>
												<option value="clty">created within past 1 year</option>												
												<option value="cgty">created more than 1 year ago</option>
											</select>
										</font>
									</td>
								</tr>							
								<tr>
									<td align="center" valign="center">
										<font>optionally limit to pid prefix
											<input name="prefix" type="text" size="10" maxlength="256"/>
										</font>
										<font>
											include
											<select name="maxResults" multiple="false" size="1">
												<option value="10" selected="selected">first 10</option>
												<option value="20">first 20</option>
												<option value="100">first 100</option>
												<option value="1000">first 1000</option>
												<option value="*">all</option>												
											</select>
											instances
										</font>
										<input type="submit" value="Get Report"/>
									</td>
								</tr>
								<tr>
									<td align="center" valign="center">
										<font>
											<select name="xslt" multiple="false" size="1">
												<option value="HTML_XSLT" selected="selected">view as html</option>
												<option value="XML_XSLT">xml</option>
											</select>
										</font>
									</td>
								</tr>
							</table>
						</center>
					</form>
				</center>				
			</body>
		</html>
	</xsl:template>
	
</xsl:stylesheet>	





				




