<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- merge.xsl
	substitute per-object XML data into per-batch METS XML template
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
	
	<!-- add per-object comment -->
	<xsl:template match="/" xmlns:METS="http://www.loc.gov/METS/" xmlns:xlink="http://www.w3.org/TR/xlink" >
		<xsl:copy>
			<xsl:if test="$substitutions/input/comment">
				<xsl:comment>
					<xsl:value-of select="$substitutions/input/comment"/>
				</xsl:comment>
			</xsl:if>		
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	
	<!-- substitute per-object @OBJID and @LABEL -->
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
	
	<!-- substitute xform param date for @CREATEDATE and @LASTMODDATE -->
	<!-- /METS:mets/METS:metsHdr -->
	<xsl:template match="METS:metsHdr" xmlns:METS="http://www.loc.gov/METS/" >
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

	<!-- substitute xform param date for @CREATED -->
	<xsl:template match="METS:techMD|METS:rightsMD|METS:sourceMD|METS:digiprovMD|METS:descMD|METS:serviceBinding" xmlns:METS="http://www.loc.gov/METS/">
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

	<!-- substitute metadata -->
	<!-- /METS:mets/METS:dmdSecFedora/*/METS:mdWrap/METS:xmlData|/METS:mets/METS:amdSec/*/METS:mdWrap/METS:xmlData -->
	<xsl:template match="METS:mdWrap/METS:xmlData" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="metadataID" select="../../../@ID" /><!-- e.g., DIGIPROV1, from amdSec element -->			
			<xsl:choose>
				<xsl:when test="$substitutions/input/metadata/metadata[@ID=$metadataID]">
					<xsl:apply-templates select="$substitutions/input/metadata/metadata[@ID=$metadataID]/node()" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="node()"/>
				</xsl:otherwise>
			</xsl:choose>		
			<xsl:choose>
				<xsl:when test="$substitutions/input/datastreams/datastream[@ID=$metadataID]/xmlContent">
					<xsl:apply-templates select="$substitutions/input/datastreams/datastream[@ID=$metadataID]/xmlContent/node()" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="node()"/>
				</xsl:otherwise>
			</xsl:choose>				
    		</xsl:copy>
	</xsl:template>

	<!-- substitute metadata labels -->
	<!-- /METS:mets/METS:dmdSecFedora/*/METS:mdWrap|/METS:mets/METS:amdSec/*/METS:mdWrap -->
	<xsl:template match="METS:mdWrap" 
		xmlns:METS="http://www.loc.gov/METS/">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="metadataID" select="../../@ID" /><!-- e.g., DESC1, from METS:dmdSecFedora element -->
			<xsl:if test="$substitutions/input/metadata/metadata[@ID=$metadataID]/@LABEL" >
				<xsl:attribute name="LABEL">
					<xsl:value-of select="$substitutions/input/metadata/metadata[@ID=$metadataID]/@LABEL" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$substitutions/input/datastreams/datastream[@ID=$metadataID]/@LABEL" >
				<xsl:attribute name="LABEL">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@ID=$metadataID]/@LABEL" />
				</xsl:attribute>
			</xsl:if>				
			<xsl:if test="$substitutions/input/metadata/metadata[@ID=$metadataID]/@MIMETYPE" >
				<xsl:attribute name="LABEL">
					<xsl:value-of select="$substitutions/input/metadata/metadata[@ID=$metadataID]/@MIMETYPE" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$substitutions/input/datastreams/datastream[@ID=$metadataID]/@MIMETYPE" >
				<xsl:attribute name="LABEL">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@ID=$metadataID]/@MIMETYPE" />
				</xsl:attribute>
			</xsl:if>				
			<xsl:apply-templates select="node()"/>			
    		</xsl:copy>
	</xsl:template>	

	<!-- substitute MIMETYPE for non-metadata datastreams -->
	<!-- /METS:mets/METS:dmdSecFedora/*/METS:mdWrap|/METS:mets/METS:amdSec/*/METS:mdWrap -->
	<xsl:template match="METS:file" 
		xmlns:METS="http://www.loc.gov/METS/">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="datastreamID" select="../@ID" /><!-- e.g., DESC1, from METS:dmdSecFedora element -->			
			<xsl:if test="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@MIMETYPE" >
				<xsl:attribute name="LABEL">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@MIMETYPE" />
				</xsl:attribute>
			</xsl:if>		
			<xsl:if test="$date">
				<xsl:attribute name="CREATED">
					<xsl:value-of select="$date"/>
				</xsl:attribute>
			</xsl:if>					
			<xsl:apply-templates select="node()"/>			
    		</xsl:copy>
	</xsl:template>	

	<!-- substitute per-datastream @xlink:title and @xlink:href -->
	<!-- /METS:mets/METS:fileSec/METS:fileGrp/METS:fileGrp/METS:file/METS:FLocat -->
 	<xsl:template match="METS:FLocat" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:variable name="datastream" select="../../@ID" />
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="prefix" select="concat('$substitutions/input/datastreams/datastream[@id=&quot;',
				$datastream,
				'&quot;]/')" />
			<xsl:variable name="title_ptr" select="concat($prefix,'@title')" />
			<xsl:variable name="href_ptr" select="concat($prefix,'@href')" />
			<xsl:if test="$substitutions/input/datastreams/datastream[@ID=$datastream]/@xlink:title" >
				<xsl:attribute name="xlink:title">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@ID=$datastream]/@xlink:title" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$substitutions/input/datastreams/datastream[@ID=$datastream]/@xlink:href" >
				<xsl:attribute name="xlink:href">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@ID=$datastream]/@xlink:href" />
				</xsl:attribute>
			</xsl:if>
			<!-- processing terminals here, so no need to xsl:apply-templates select="node()" -->
    		</xsl:copy>
	</xsl:template>
	
	<!-- substitute per-disseminator labels; prefer datastream-specific over disseminator-general -->
	<!-- THIS FEATURE IS DEPRECATED AS OF fEDORA 2.1
	<xsl:template match="/METS:mets/METS:structMap/METS:div/METS:div" xmlns:METS="http://www.loc.gov/METS/">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="disseminatorID" select="../../@ID" />			
			<xsl:variable name="datastreamID" select="METS:fptr/@FILEID" />
			<xsl:choose>
				<xsl:when test="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/disseminator[@ID=$disseminatorID]/@LABEL">
					<xsl:apply-templates select="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/disseminator[@ID=$disseminatorID]/@LABEL" />
				</xsl:when>
				<xsl:when test="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@LABEL">
					<xsl:apply-templates select="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@LABEL" />
				</xsl:when>
			</xsl:choose>		
			<xsl:apply-templates select="node()"/>			
    		</xsl:copy>
	</xsl:template>	
	-->
	
</xsl:transform>

