<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- foxml-merge.xsl
	substitute per-object XML data into per-batch FOXML1.0 XML template
-->

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
xmlns:xsi="http://www.w3.org/2000/10/XMLSchema-instance" 
xmlns:fedoraAudit="http://fedora.comm.nsdlib.org/audit" 
xmlns:xlink="http://www.w3.org/TR/xlink" 
xmlns:foxml="info:fedora/fedora-system:def/foxml#"
>
	<xsl:param name="date">2005-03-20T00:00:00.000Z</xsl:param>
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
	<xsl:template match="/" xmlns:foxml="info:fedora/fedora-system:def/foxml#" >
		<xsl:copy>
			<xsl:if test="$substitutions/input/comment">
				<xsl:comment>
					<xsl:value-of select="$substitutions/input/comment"/>
				</xsl:comment>
			</xsl:if>		
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<!-- substitute per-object objectPID -->
	<xsl:template match="foxml:digitalObject">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:if test="$substitutions/input/@OBJID">
				<xsl:attribute name="PID">
					<xsl:value-of select="$substitutions/input/@OBJID"/>
				</xsl:attribute>
			</xsl:if>			
			<xsl:apply-templates select="node()"/>
    		</xsl:copy>
	</xsl:template>
	
	<!-- substitute per-object objectLabel -->
	<xsl:template match="foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#label']">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:if test="$substitutions/input/@LABEL">
				<xsl:attribute name="VALUE">
					<xsl:value-of select="$substitutions/input/@LABEL"/>
				</xsl:attribute>
			</xsl:if>			
			<xsl:apply-templates select="node()"/>
    		</xsl:copy>
	</xsl:template>
	
	<!-- substitute per-object xform param date for createdDate -->
	<xsl:template match="foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#createdDate']" >
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:if test="$date">
				<xsl:attribute name="VALUE">
					<xsl:value-of select="$date"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="node()"/>
    		</xsl:copy>
	</xsl:template>
	
	<!-- substitute per-object xform param date for lastModifiedDate -->
	<xsl:template match="foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/view#lastModifiedDate']" >
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:if test="$date">
				<xsl:attribute name="VALUE">
					<xsl:value-of select="$date"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="node()"/>
    		</xsl:copy>
	</xsl:template>	

	<!-- substitute xform param date for @CREATED -->
	<!--
	<xsl:template match="foxml:datastream/foxml:datastreamVersion">
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
	-->

	<!-- substitute xmlContent found in old-style metadata element tag -->
	<xsl:template match="foxml:datastream/foxml:datastreamVersion/foxml:xmlContent">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<!--<xsl:variable name="metadataID" select="../@ID" />			-->
			<xsl:variable name="datastreamID" select="../../@ID" />	
			<xsl:choose>
				<xsl:when test="$substitutions/input/metadata/metadata[@ID=$datastreamID]">
					<xsl:apply-templates select="$substitutions/input/metadata/metadata[@ID=$datastreamID]/node()" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="node()"/>
				</xsl:otherwise>
			</xsl:choose>	
			<xsl:choose>
				<xsl:when test="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/xmlContent">
					<xsl:apply-templates select="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/xmlContent/node()" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="node()"/>
				</xsl:otherwise>
			</xsl:choose>					
    		</xsl:copy>
	</xsl:template>

	<!-- substitute xmlContent found in new-style xmlContent element tag -->
	<!--
	<xsl:template match="foxml:datastream/foxml:datastreamVersion/foxml:xmlContent">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="datastreamID" select="../../@ID" />			
			<xsl:choose>
				<xsl:when test="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/xmlContent">
					<xsl:apply-templates select="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/xmlContent/node()" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="node()"/>
				</xsl:otherwise>
			</xsl:choose>		
    		</xsl:copy>
	</xsl:template>
-->

	<!-- substitute datastream Label, Reference, and createdDate -->
	<xsl:template match="foxml:datastream/foxml:datastreamVersion">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="datastreamID" select="../@ID" />
			<xsl:if test="$substitutions/input/metadata/metadata[@ID=$datastreamID]/@LABEL" >
				<xsl:attribute name="LABEL">
					<xsl:value-of select="$substitutions/input/metadata/metadata[@ID=$datastreamID]/@LABEL" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@LABEL" >
				<xsl:attribute name="LABEL">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@LABEL" />
				</xsl:attribute>
			</xsl:if>						
			<xsl:if test="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@xlink:title" >
				<xsl:attribute name="LABEL">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@xlink:title" />
				</xsl:attribute>
			</xsl:if>		
			<xsl:if test="$substitutions/input/metadata/metadata[@ID=$datastreamID]/@MIMETYPE" >
				<xsl:attribute name="MIMETYPE">
					<xsl:value-of select="$substitutions/input/metadata/metadata[@ID=$datastreamID]/@MIMETYPE" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@MIMETYPE" >
				<xsl:attribute name="MIMETYPE">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@MIMETYPE" />
				</xsl:attribute>
			</xsl:if>			
			<xsl:if test="$substitutions/input/metadata/metadata[@ID=$datastreamID]/@FORMAT_URI" >
				<xsl:attribute name="FORMAT_URI">
					<xsl:value-of select="$substitutions/input/metadata/metadata[@ID=$datastreamID]/@FORMAT_URI" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@FORMAT_URI" >
				<xsl:attribute name="FORMAT_URI">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@FORMAT_URI" />
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
	
	<xsl:template match="foxml:datastream/foxml:datastreamVersion/foxml:contentLocation">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="datastreamID" select="../../@ID" />
			<xsl:if test="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@xlink:href" >
				<xsl:attribute name="REF">
					<xsl:value-of select="$substitutions/input/datastreams/datastream[@ID=$datastreamID]/@xlink:href" />
				</xsl:attribute>
			</xsl:if>	
			<xsl:apply-templates select="node()"/>			
    		</xsl:copy>
	</xsl:template>	
	
	
	 <!-- substitute per-disseminator labels; prefer datastream-specific over disseminator-general -->
	 <!--
	<xsl:template match="/foxml:disseminator">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="disseminatorLABEL" select="foxml:disseminatorVersion/foxml:/foxml:serviceInputMap/@LABEL" />			
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

