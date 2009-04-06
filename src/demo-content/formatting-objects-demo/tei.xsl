<!-- 
TEI XSLT stylesheet family version 1.3
RCS: $Date$, $Revision$, $Author$

XSL FO stylesheet to format TEI XML documents 

 Copyright 1999-2002 Sebastian Rahtz/Oxford University  <sebastian.rahtz@oucs.ox.ac.uk>

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and any associated documentation files (the
 ``Software''), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:
 
 The above copyright notice and this permission notice shall be included
 in all copies or substantial portions of the Software.
 

TODO:
 * multiple columns
 * much more parameterization
 * internationalization
-->

<!-- Still to handle from exercise.xml
marginal notes
-->

<xsl:stylesheet
  xmlns:fotex="http://www.tug.org/fotex"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">


<!-- parameterization -->

<xsl:import href="http://www.fedora.info/services/fop/tei-param.xsl"/>

<xsl:strip-space elements="cell"/>

<xsl:output indent="no" media-type="text/xml"/>

<!-- overrides -->


<xsl:variable name="top" select="/"/>
<xsl:variable name="tableSpecs">
  <xsl:choose>
  <xsl:when test="$readColSpecFile">
  <xsl:copy-of
      select="document($readColSpecFile,$top)/Info"/>
 </xsl:when>
 <xsl:otherwise> <Info></Info></xsl:otherwise>
</xsl:choose>
</xsl:variable>

<!-- example of different configuration
<xsl:variable name="activeLinebreaks">true</xsl:variable>
<xsl:variable name="activePagebreaks">true</xsl:variable>

<xsl:variable name="bodyMaster">12</xsl:variable>
<xsl:variable name="bodySize">
 <xsl:value-of select="$bodyMaster"/><xsl:text>pt</xsl:text>
</xsl:variable>
<xsl:variable name="smallSize">
 <xsl:value-of select="$bodyMaster * 0.9"/><xsl:text>pt</xsl:text>
</xsl:variable>
<xsl:variable name="exampleSize">
 <xsl:value-of select="$bodyMaster * 0.8"/><xsl:text>pt</xsl:text>
</xsl:variable>
<xsl:variable name="pageMarginBottom">325pt</xsl:variable>
<xsl:variable name="pageMarginRight">250pt</xsl:variable>
-->
<xsl:include href="http://www.fedora.info/services/fop/tei-lib.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-bib.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-drama.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-figure.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-front.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-lists.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-notes.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-para.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-poetry.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-special.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-struct.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-table.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-xref.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-markers.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-math.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/tei-makecolspec.xsl"/>
<xsl:include href="http://www.fedora.info/services/fop/teicommon.xsl"/>

<xsl:variable name="processor">
   <xsl:value-of select="system-property('xsl:vendor')"/>
</xsl:variable>

</xsl:stylesheet>
