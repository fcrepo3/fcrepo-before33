<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- xml2xsl.xsl
	this stylesheet is a "meta" or general stylesheet:
	it transforms its source/input into another, "object" or specific, stylesheet.
	the source/input to the meta stylesheet is a METS document.
	the source/input to the object stylesheet contains a subset of METS nodes, in a simplified schema.
	application of the object stylesheet substitutes the METS nodes present in its source/input
	into the original meta source/input, replacing values from the original.

	this requires xsl:if or xsl:choose elements to be inserted into the object stylesheet,
	instead of executed in the meta stylesheet.  hence the use of xsl:element, xsl:attribute, 
	and xsl:value-of elements below
-->

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
xmlns:xsi="http://www.w3.org/2000/10/XMLSchema-instance"
xmlns:METS="http://www.loc.gov/METS/"
xmlns:fedoraAudit="http://fedora.comm.nsdlib.org/audit"
xmlns:uvadesc="http://dl.lib.virginia.edu/bin/dtd/descmeta/"
xmlns:uvadigiprov="http://dl.lib.virginia.edu/bin/admin/digiprov/"
xmlns:uvasource="http://dl.lib.virginia.edu/bin/admin/source/"
xmlns:uvarights="http://dl.lib.virginia.edu/bin/admin/rights/"
xmlns:uvatech="http://dl.lib.virginia.edu/bin/admin/tech/"
xmlns:xlink="http://www.w3.org/TR/xlink"
>
	<xsl:param name="date" select="NO-DATE-PARAM"/>
	<xsl:param name="subfilepath" select="NO-SUBFILEPATH-PARAM"/>
	<xsl:variable name="substitutions" select="document($subfilepath)"/>

	<xsl:output method="xml" indent="yes" />

	<xsl:template match="@*">
		<xsl:copy/>
	</xsl:template>

	<xsl:template name="generic-node" match="node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

	<!-- xsl:template match="comment()" >
		< xsl:element name="xsl:comment">
			< xsl:value-of select="."/>
		< /xsl:element>
	< /xsl:template -->
	
	<xsl:template match="/" xmlns:METS="http://www.loc.gov/METS/" xmlns:xlink="http://www.w3.org/TR/xlink" >
		<xsl:copy>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	
	<!-- target substitution /METS:mets/@LABEL -->
	<xsl:template match="/METS:mets">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:if test="$substitutions/input/@LABEL">
				<xsl:attribute name="LABEL">
					<xsl:value-of select="$substitutions/input/@LABEL"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$substitutions/input/@OBJID">
				<xsl:attribute name="OBJID">
					<xsl:value-of select="$substitutions/input/@OBJID"/>
				</xsl:attribute>
			</xsl:if>			
			<xsl:apply-templates select="node()"/>
    		</xsl:copy>
	</xsl:template>
	
	<!-- target substitutions @CREATEDATE and @LASTMODDATE in /METS:metsHdr -->
	<xsl:template match="/METS:mets/METS:metsHdr" xmlns:METS="http://www.loc.gov/METS/" >
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:if test="$date">
				<xsl:attribute name="CREATEDATE">
					<xsl:value-of select="$date"/>
				</xsl:attribute>
				<xsl:attribute name="LASTMODDATE">
					<xsl:value-of select="$date"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="node()"/>
    		</xsl:copy>
	</xsl:template>
	

	<!-- target substitutions @CREATED in METS:file METS:behaviorSec -->
	<xsl:template match="METS:file|METS:behaviorSec" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:if test="$date">
				<xsl:attribute name="CREATED">
					<xsl:value-of select="$date"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="node()"/>
    		</xsl:copy>
	</xsl:template>
	
	<!-- target substitutions @CREATED in METS:dmdSec, METS:techMD, METS:rightsMD, METS:sourceMD, METS:digiprovMD in /METS:mets -->
	<xsl:template match="/METS:mets/METS:dmdSec|/METS:mets/METS:techMD|/METS:mets/METS:rightsMD|/METS:mets/METS:sourceMD|/METS:mets/METS:digiprovMD" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:if test="$date">
				<xsl:attribute name="CREATED">
					<xsl:value-of select="$date"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="node()"/>
    		</xsl:copy>
	</xsl:template>
	



	
	<!-- target substitutions name and note in /METS:mets/METS:metsHdr/METS:agent -->
	<!-- support additional comments -->
	<xsl:template match="/METS:mets/METS:metsHdr/METS:agent[@TYPE='INDIVIDUAL']/METS:name" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:if test="$substitutions/input/agents/INDIVIDUAL/METS:name/comment">
			<xsl:element name="xsl:comment">
				<xsl:value-of select="$substitutions/input/agents/INDIVIDUAL/METS:name/comment" />
			</xsl:element>
		</xsl:if>
		<xsl:copy>
			<xsl:apply-templates select="@*"/>			
			<xsl:choose>
				<xsl:when test="$substitutions/input/agents/INDIVIDUAL/METS:name/text()">
					<xsl:value-of select="$substitutions/input/agents/INDIVIDUAL/METS:name/text()"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="text()" />
				</xsl:otherwise>
			</xsl:choose>
			<!-- processing terminals here, so no need to xsl:apply-templates select="node()" -->
    		</xsl:copy>
	</xsl:template>
	<!-- >>>>>>>>>>> needs testing and replication for ORGANIZATION|OTHER x note <<<<<<<<<<< -->


	<!-- target substitutions @title and @href in /METS:mets/METS:fileSec/METS:fileGrp/METS:fileGrp/METS:file/METS:FLocat -->
 	<xsl:template match="/METS:mets/METS:fileSec/METS:fileGrp/METS:fileGrp/METS:file/METS:FLocat" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:variable name="datastream" select="../../@ID" />
		<xsl:copy>
			<xsl:apply-templates select="@*"/>

			<!-- xsl:variable name="datastream" select="../../@ID" / -->
			<xsl:variable name="prefix" select="concat('$substitutions/input/datastreams/datastream[@id=&quot;',
				$datastream,
				'&quot;]/')" />
			<xsl:variable name="title_ptr" select="concat($prefix,'@title')" />
			<xsl:variable name="href_ptr" select="concat($prefix,'@href')" />
			<xsl:if test="$substitutions/input/datastreams/datastream[@id=$datastream]/@title" >
				<xsl:attribute name="xlink:title">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@id=$datastream]/@title" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$substitutions/input/datastreams/datastream[@id=$datastream]/@href" >
				<xsl:attribute name="xlink:href">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@id=$datastream]/@href" />
				</xsl:attribute>
			</xsl:if>
			<!-- processing terminals here, so no need to xsl:apply-templates select="node()" -->
    		</xsl:copy>
	</xsl:template>
	


	

	
	
	
	 
	<!-- support only additional comments on /METS:mets/METS:metsHdr/METS:agent -->
	<xsl:template match="/METS:mets/METS:metsHdr/METS:agent[@TYPE='INDIVIDUAL']" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:if test="$substitutions/input/agents/INDIVIDUAL/comment">
			<!-- xsl:comment -->
				<!-- xsl:value-of select="$substitutions/input/agents/INDIVIDUAL/comment" / -->
			<!-- /xsl:comment -->
		</xsl:if>
		<xsl:call-template name="generic-node" />
	</xsl:template>	
	
	<!-- support only additional comments on /METS:mets/METS:metsHdr/METS:agent -->
	<xsl:template match="/METS:mets/METS:metsHdr/METS:agent[@TYPE='ORGANIZATION']" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:if test="$substitutions/input/agents/ORGANIZATION/comment">
			<!-- xsl:comment -->
				<!-- xsl:value-of select="$substitutions/input/agents/INDIVIDUAL/comment" / -->
			<!-- /xsl:comment -->
		</xsl:if>
		<xsl:call-template name="generic-node" />
	</xsl:template>	

	<!-- support only additional comments on /METS:mets/METS:metsHdr/METS:agent -->
	<xsl:template match="/METS:mets/METS:metsHdr/METS:agent[@TYPE='OTHER']" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:if test="$substitutions/input/agents/OTHER/comment">
			<!-- xsl:comment -->
				<!-- xsl:value-of select="$substitutions/input/agents/INDIVIDUAL/comment" / -->
			<!-- /xsl:comment -->
		</xsl:if>
		<xsl:call-template name="generic-node" />
	</xsl:template>		
	
	
	<!-- target substitutions @CREATED in METS:dmdSec, METS:techMD, METS:rightsMD, METS:sourceMD, METS:digiprovMD in /METS:mets/METS:amdSec -->
	<xsl:template match="/METS:mets/METS:amdSec/METS:dmdSec|/METS:mets/METS:amdSec/METS:techMD|/METS:mets/METS:amdSec/METS:rightsMD|/METS:mets/METS:amdSec/METS:sourceMD|/METS:mets/METS:amdSec/METS:digiprovMD" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:if test="$date">
				<xsl:attribute name="CREATED">
					<xsl:value-of select="$date"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="node()"/>
    		</xsl:copy>
	</xsl:template>	

		
	 <!-- target substitution (per-datastream metadata) -->
	<xsl:template match="/METS:mets/METS:amdSec/*/METS:mdWrap/METS:xmlData" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="metadataID" select="../../../@ID" /><!-- e.g., DIGIPROV1, from amdSec element -->
			<xsl:if test="$substitutions/input/metadata/metadata[@id=$metadataID]/@LABEL" >
				<xsl:attribute name="LABEL">
					<xsl:value-of select="$substitutions/input/metadata/metadata[@id=$metadataID]/@LABEL" />
				</xsl:attribute>
			</xsl:if>			
			<xsl:choose>
				<xsl:when test="$substitutions/input/metadata/metadata[@id=$metadataID]">
					<xsl:apply-templates select="$substitutions/input/metadata/metadata[@id=$metadataID]/*" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="node()"/>
				</xsl:otherwise>
			</xsl:choose>		
    		</xsl:copy>
	</xsl:template>

	 <!-- >>>>>>>>>>>>>> target substitution (per-datastream DESC metadata) <<<<<<<<<<<< -->

	
</xsl:transform>

