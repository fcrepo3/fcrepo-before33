<?xml version="1.0"?>
<!--
  Copyright (C) DSTC Pty Ltd (ACN 052 372 577) 2002
 
  The software contained on this media is the property of the
  DSTC Pty Ltd.  Use of this software is strictly in accordance
  with the license agreement in the accompanying LICENSE file.
  If your distribution of this software does not contain a
  LICENSE file then you have no rights to use this software
  in any manner and should contact DSTC at the address below
  to determine an appropriate licensing arrangement.
 
		DSTC Pty Ltd
		Level 7, General Purpose South 
		The University of Queensland 
		QLD 4072 Australia 
		Tel: +61 7 3365 4310
		Fax: +61 7 3365 4311
		Email: titanium_enquiries@dstc.edu.au
 
  This software is being provided "AS IS" without warranty of
  any kind.  In no event shall DSTC Pty Ltd be liable for
  damage of any kind arising out of or in connection with
  the use or performance of this software.
 
-->
<!-- 
  File:
     xs3p.xsl
  Description:
     Stylesheet that generates XHTML documentation, given an XML 
     Schema document
  Assumptions:
     -Resulting documentation will only be displayed properly with 
      the latest browsers that support XHTML and CSS. Older 
      browsers are not supported.
     -Assumed that XSD document conforms to the XSD recommendation.
      No validity checking is done.
  Constraints:
     -Local schema components cannot contain two dashes in 
      'documentation' elements within their 'annotation' element.
      This is because the contents of those 'documentation' 
      elements are displayed in a separate window using Javascript. 
      This Javascript code is enclosed in comments, which do not
      allow two dashes inside themselves.
  Notes:
     -Javascript code is placed within comments, even though in 
      strict XHTML, JavaScript code should be placed within CDATA 
      sections. This is because current browsers generate a syntax 
      error if the page contains CDATA sections. Placing Javascript 
      code within comments means that the code cannot contain two 
      dashes.
      (See 'PrintJSCode' named template.)
  Stylesheet Sections:
     -Global Parameters
          Specify parameters that can be set externally to customise 
          stylesheet
     -Constants
          Constants used by the stylesheet
     -Main Document
          Templates to generate the overall document and the top-level 
          sections within it
     -Hierarchy table
          Templates for displaying type hierarchy for simple and 
          complex types, and substitution group hierarchy for elements
     -Properties table
          Templates for displaying the properties of top-level schema 
          components
     -XML Instance Representation table
          Templates for displaying how an XML instance conforming to 
          the schema component would look like
     -Schema Component Representation table
          Templates for displaying the XML representation of the schema
          component
     -XML Pretty Printer
          Templates for displaying arbitrary XML instances
     -Handling Schema Component References
          Templates for generating internal and external links
     -General Utility Templates
          General templates used by other templates in this stylesheet
  To Do List:
     -It is not clever when printing out element and attribute 
      wildcards in the XML Instance Representation tables. It prints 
      out all wildcards, rather than working out the actual wildcard 
      is from multiple wildcard instances.
     -Same as above for simple type constraints, e.g. it doesn't 
      summarise multiple pattern constraints.
-->
<xsl:stylesheet
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns="http://www.w3.org/1999/xhtml"
 xmlns:xsd="http://www.w3.org/2001/XMLSchema"
 version="1.0" 
 exclude-result-prefixes="xsd">

  <xsl:output
    method = "xml" 
    encoding="iso-8859-1"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    indent="yes" />


  <!-- **** Global Parameters **** -->

  <!-- Title of XHTML document. -->
  <xsl:param name="title"></xsl:param>

  <!-- If 'true', sorts the top-level schema components by type, 
       then name.
       Otherwise, displays the components by the order that they
       appear in the schema. -->
  <xsl:param name="sortByComponent">true</xsl:param>

  <!-- If 'true', XHTML document uses JavaScript for added
       functionality, such as pop-up windows and information-
       hiding.
       Otherwise, XHTML document does not use JavaScript. -->
  <xsl:param name="useJavaScript">true</xsl:param>

  <!-- If 'true', prints all super-types in the
       type hierarchy box.
       Otherwise, prints the parent type only in the
       type hierarchy box. -->
  <xsl:param name="printAllSuperTypes">true</xsl:param>

  <!-- If 'true', prints all sub-types in the
       type hierarchy box.
       Otherwise, prints the direct sub-types only in the
       type hierarchy box. -->
  <xsl:param name="printAllSubTypes">true</xsl:param>

  <!-- If 'true', prints out the Glossary section. -->
  <xsl:param name="printGlossary">true</xsl:param>

  <!-- If 'true', prints out the Legend section. -->
  <xsl:param name="printLegend">true</xsl:param>

  <!-- If 'true', prints prefix matching namespace of schema
       components in XML Instance Representation tables. -->
  <xsl:param name="printNSPrefixes">true</xsl:param>


  <!-- **** Constants **** -->

  <!-- XML Schema Namespace -->
  <xsl:variable name="XSD_NS">http://www.w3.org/2001/XMLSchema</xsl:variable>

  <!-- XML Namespace -->
  <xsl:variable name="XML_NS">http://www.w3.org/XML/1998/namespace</xsl:variable>

  <!-- Number of 'em' to indent from child element's start
         tag to parent element's start tag -->
  <xsl:variable name="ELEM_INDENT">1.5</xsl:variable>

  <!-- Number of 'em' to indent from attribute's tag to
         containing element's start tag -->
  <xsl:variable name="ATTR_INDENT">0.5</xsl:variable>

  <!-- Title to use if none provided -->
  <xsl:variable name="DEFAULT_TITLE">XML Schema Documentation</xsl:variable>

  <!-- Prefixes for anchor names -->

  <xsl:variable name="ATTR_PREFIX">attribute_</xsl:variable>
  <xsl:variable name="ATTR_GRP_PREFIX">attributeGroup_</xsl:variable>
  <xsl:variable name="CTYPE_PREFIX">complexType_</xsl:variable>
  <xsl:variable name="ELEM_PREFIX">element_</xsl:variable>
  <xsl:variable name="GRP_PREFIX">group_</xsl:variable>
  <xsl:variable name="NOTA_PREFIX">notation_</xsl:variable>
  <xsl:variable name="STYPE_PREFIX">simpleType_</xsl:variable>
  <xsl:variable name="TERM_PREFIX">term_</xsl:variable>
  <xsl:variable name="KEY_PREFIX">key_</xsl:variable>
  <xsl:variable name="SAMPLE_PREFIX">sample_</xsl:variable>
  <xsl:variable name="NS_PREFIX">ns_</xsl:variable>


  <!-- **** Main Document **** -->

  <!--
     Main template that starts the process
     -->
  <xsl:template match="/xsd:schema">
     <!-- Get title of document -->
     <xsl:variable name="actualTitle">
        <xsl:choose>
          <xsl:when test="$title != ''">
             <xsl:value-of select="$title"/>
          </xsl:when>
          <xsl:otherwise> 
             <xsl:value-of select="$DEFAULT_TITLE"/>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:variable>

     <html>
        <head>
          <title><xsl:value-of select="$actualTitle"/></title>
          <meta http-equiv="Content-Type" content="text/xml; charset=iso-8859-1"/>
          <style type="text/css"><xsl:call-template name="DocumentCSSStyles"/></style>
          <xsl:if test="normalize-space(translate($useJavaScript,'TRUE','true'))='true'">
             <xsl:call-template name="PrintJSCode">
                <xsl:with-param name="code">
                  <xsl:call-template name="DocumentJSCode"/>
                </xsl:with-param>
             </xsl:call-template>
          </xsl:if>
        </head>
        <body>
          <xsl:if test="normalize-space(translate($useJavaScript,'TRUE','true'))='true'">
             <xsl:attribute name="onload">init();</xsl:attribute>
          </xsl:if>

          <!-- Title -->
          <h1><a name="top"><xsl:value-of select="$actualTitle"/></a></h1>

          <!-- Buttons for displaying printer-friendly version, and
                 expanding and collapsing all boxes -->
          <xsl:call-template name="GlobalControlButtons"/>

          <!-- Section: Table of Contents -->
          <h2>Table of Contents</h2>
          <xsl:apply-templates select="." mode="toc"/>
          <xsl:call-template name="SectionFooter"/>

          <!-- Section: Schema Document Properties -->
          <h2><a name="SchemaProperties">Schema Document Properties</a></h2>
             <!-- Sub-section: Properties table -->
          <xsl:apply-templates select="." mode="properties"/>
             <!-- Sub-section: Namespace Legend -->
          <h3>Declared Namespaces</h3>
          <xsl:apply-templates select="." mode="namespaces"/>
             <!-- Sub-section: Schema Component Representation table -->
          <xsl:call-template name="SchemaComponentTable">
             <xsl:with-param name="component" select="."/>
          </xsl:call-template>
          <xsl:call-template name="SectionFooter"/>

          <!-- Section: Redefined Schema Components -->
          <xsl:if test="xsd:redefine">
             <h2><a name="Redefinitions">Redefined Schema Components</a></h2>
             <xsl:apply-templates select="xsd:simpleType | xsd:complexType | xsd:attributeGroup | xsd:group" mode="topSection"/>
          </xsl:if>

          <!-- Sections: Top-level Schema Components -->
          <xsl:choose>
             <!-- Sort schema components -->
             <xsl:when test="normalize-space(translate($sortByComponent,'TRUE','true'))='true'">
                <!-- Declarations -->
                <xsl:if test="xsd:attribute or xsd:element">
                  <h2><a name="SchemaDeclarations">Global Declarations</a></h2>
                  <xsl:apply-templates select="xsd:attribute | xsd:element" mode="topSection">
                     <xsl:sort select="local-name(.)" order="ascending"/>
                     <xsl:sort select="@name" order="ascending"/>
                  </xsl:apply-templates>
                </xsl:if>

                <!-- Definitions -->
                <xsl:if test="xsd:attributeGroup or xsd:complexType or xsd:group or xsd:notation or xsd:simpleType">
                  <h2><a name="SchemaDefinitions">Global Definitions</a></h2>
                  <xsl:apply-templates select="xsd:attributeGroup | xsd:complexType | xsd:group | xsd:notation | xsd:simpleType" mode="topSection">
                     <xsl:sort select="local-name(.)" order="ascending"/>
                     <xsl:sort select="@name" order="ascending"/>
                  </xsl:apply-templates>
                </xsl:if>
             </xsl:when>
             <!-- Display schema components as they occur -->
             <xsl:otherwise> 
                <h2><a name="SchemaComponents">Global Schema Components</a></h2>
                <xsl:apply-templates select="xsd:attribute | xsd:attributeGroup | xsd:complexType | xsd:element | xsd:group | xsd:notation | xsd:simpleType" mode="topSection"/>
             </xsl:otherwise>
          </xsl:choose>

          <!-- Section: Legend -->
          <xsl:if test="normalize-space(translate($printLegend,'TRUE','true'))='true'">
             <div id="legend">
                <h2><a name="Legend">Legend</a></h2>
                <xsl:call-template name="Legend"/>
                <xsl:call-template name="SectionFooter"/>
             </div>
          </xsl:if>

          <!-- Section: Glossary -->
          <xsl:if test="normalize-space(translate($printGlossary,'TRUE','true'))='true'">
             <div id="glossary">
                <h2><a name="Glossary">Glossary</a></h2>
                <xsl:call-template name="Glossary"/>
                <xsl:call-template name="SectionFooter"/>
             </div>
          </xsl:if>

          <!-- Document Footer -->
          <p style="font-size: 8pt;">
             <xsl:text>Generated by </xsl:text>
             <a href="http://titanium.dstc.edu.au/xml/xs3p">xs3p</a>
             <xsl:text>.</xsl:text>
             <xsl:if test="normalize-space(translate($useJavaScript,'TRUE','true'))='true'">
                <xsl:text> Last modified: </xsl:text>
                <xsl:call-template name="PrintJSCode">
                  <xsl:with-param name="code">document.write(document.lastModified);</xsl:with-param>
                </xsl:call-template>
             </xsl:if>
          </p>
        </body>
     </html>
  </xsl:template>

  <xsl:template match="xsd:schema" mode="namespaces">
     <table class="namespaces">
        <tr>
           <th>Prefix</th>
           <th>Namespace</th>
        </tr>
        <xsl:if test="namespace::*[local-name(.)='']">
           <xsl:variable name="ns" select="namespace::*[local-name(.)='']"/>
           <tr>
              <td>
                 <a name="{$NS_PREFIX}">Default namespace</a>
              </td>
              <td>
                 <xsl:choose>
                    <xsl:when test="/xsd:schema/@targetNamespace and $ns=normalize-space(/xsd:schema/@targetNamespace)">
                       <span class="targetNS">
                          <xsl:value-of select="$ns"/>
                       </span>
                    </xsl:when>
                    <xsl:otherwise> 
                       <xsl:value-of select="$ns"/>
                    </xsl:otherwise>
                 </xsl:choose>
              </td>
           </tr>
        </xsl:if>
        <xsl:for-each select="namespace::*[local-name(.)!='']">
           <xsl:variable name="prefix" select="local-name(.)"/>
           <xsl:variable name="ns" select="."/>
           <tr>
              <td>
                 <a name="{concat($NS_PREFIX, $prefix)}">
                    <xsl:value-of select="$prefix"/>
                 </a>
              </td>
              <td>
                 <xsl:choose>
                    <xsl:when test="/xsd:schema/@targetNamespace and $ns=normalize-space(/xsd:schema/@targetNamespace)">
                       <span class="targetNS">
                          <xsl:value-of select="$ns"/>
                       </span>
                    </xsl:when>
                    <xsl:otherwise> 
                       <xsl:value-of select="$ns"/>
                    </xsl:otherwise>
                 </xsl:choose>
              </td>
           </tr>
        </xsl:for-each>
     </table>
  </xsl:template>

  <!--
     Prints out the Table of Contents.
     -->
  <xsl:template match="xsd:schema" mode="toc">
     <ul>
        <!-- Section: Schema Document Properties -->
        <li><a href="#SchemaProperties">Schema Document Properties</a></li>
        <!-- Section: Redefined Schema Components -->
        <xsl:if test="xsd:redefine">
          <li><a href="#Redefinitions">Redefined Schema Components</a></li>
        </xsl:if>
        <!-- Sections: Top-level Schema Components -->
        <xsl:choose>
          <!-- Sort schema components -->
          <xsl:when test="normalize-space(translate($sortByComponent,'TRUE','true'))='true'">
             <!-- Declarations -->
             <xsl:if test="xsd:attribute or xsd:element">
                <li><a href="#SchemaDeclarations">Global Declarations</a>
                <ul>
                  <xsl:apply-templates select="xsd:attribute | xsd:element" mode="toc">
                     <xsl:sort select="local-name(.)" order="ascending"/>
                     <xsl:sort select="@name" order="ascending"/>
                  </xsl:apply-templates>
                </ul>
                </li>
             </xsl:if>

             <!-- Definitions -->
             <xsl:if test="xsd:attributeGroup or xsd:complexType or xsd:group or xsd:notation or xsd:simpleType">
                <li><a href="#SchemaDefinitions">Global Definitions</a>
                <ul>
                  <xsl:apply-templates select="xsd:attributeGroup | xsd:complexType | xsd:group | xsd:notation | xsd:simpleType" mode="toc">
                     <xsl:sort select="local-name(.)" order="ascending"/>
                     <xsl:sort select="@name" order="ascending"/>
                  </xsl:apply-templates>
                </ul>
                </li>
             </xsl:if>
          </xsl:when>
          <!-- Display schema components in order as they appear in schema -->
          <xsl:otherwise> 
             <li><a href="#SchemaComponents">Global Schema Components</a>
             <ul>
                <xsl:apply-templates select="xsd:attribute | xsd:attributeGroup | xsd:complexType | xsd:element | xsd:group | xsd:notation | xsd:simpleType" mode="toc"/>
             </ul>
             </li>
          </xsl:otherwise>
        </xsl:choose>
     </ul>
     <!-- Section: Legend -->
     <xsl:if test="normalize-space(translate($printLegend,'TRUE','true'))='true'">
        <ul id="legendTOC" style="margin-top: 0em">
          <li><a href="#Legend">Legend</a></li>
        </ul>
     </xsl:if>
     <!-- Section: Glossary -->
     <xsl:if test="normalize-space(translate($printGlossary,'TRUE','true'))='true'">
        <ul id="glossaryTOC" style="margin-top: 0em">
          <li><a href="#Glossary">Glossary</a></li>
        </ul>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out a link to a top-level schema component section in the
     Table of Contents.
     -->
  <xsl:template match="xsd:*[@name]" mode="toc">
     <xsl:variable name="componentID">
        <xsl:call-template name="GetComponentID">
          <xsl:with-param name="component" select="."/>
        </xsl:call-template>
     </xsl:variable>

     <li>
        <a href="#{$componentID}">
          <xsl:call-template name="GetComponentDescription">
             <xsl:with-param name="component" select="."/>
          </xsl:call-template>
          <xsl:text>: </xsl:text>
          <strong><xsl:value-of select="@name"/></strong>
        </a>
     </li>
  </xsl:template>

  <!--
     Prints out the top-level section for a schema component.
     -->
  <xsl:template match="xsd:*[@name]" mode="topSection">
     <!-- Header -->
     <xsl:call-template name="ComponentSectionHeader">
        <xsl:with-param name="component" select="."/>
     </xsl:call-template>

     <!-- Hierarchy table (for types and elements) -->
     <xsl:apply-templates select="." mode="hierarchy"/>

     <!-- Properties table -->
     <xsl:apply-templates select="." mode="properties"/>

     <!-- XML Instance Representation table -->
     <xsl:call-template name="SampleInstanceTable">
        <xsl:with-param name="component" select="."/>
     </xsl:call-template>

     <!-- Schema Component Representation table -->
     <xsl:call-template name="SchemaComponentTable">
        <xsl:with-param name="component" select="."/>
     </xsl:call-template>

     <!-- Footer -->
     <xsl:call-template name="SectionFooter"/>
  </xsl:template>

  <!--
     Prints out the buttons that can expand and collapse all boxes in
     this page.
     -->
  <xsl:template name="GlobalControlButtons">
     <xsl:if test="normalize-space(translate($useJavaScript,'TRUE','true'))='true'">
        <!-- Printer-friendly Version -->
        <div id="printerControls" style="display:none;">
          <input type="checkbox" onclick="displayMode(this.checked)"/>
          <xsl:text>Printer-friendly Version</xsl:text>
        </div>

        <!-- Expand/Collapse All buttons -->
        <div id="globalControls" style="display:none">
          <p>
             <strong>XML Instance Representation: </strong><br/>
             <span style="margin-left: 1em; white-space: nowrap">
                <xsl:text>[ </xsl:text>
                <a href="javascript:void(0)" onclick="expandAll(xiBoxes)">Expand All</a>
                <xsl:text> | </xsl:text>
                <a href="javascript:void(0)" onclick="collapseAll(xiBoxes)">Collapse All</a>
                <xsl:text> ]</xsl:text>
             </span>
          </p>

          <p>
             <strong>Schema Component Representation: </strong><br/>
             <span style="margin-left: 1em; white-space: nowrap">
                <xsl:text>[ </xsl:text>
                <a href="javascript:void(0)" onclick="expandAll(scBoxes)">Expand All</a>
                <xsl:text> | </xsl:text>
                <a href="javascript:void(0)" onclick="collapseAll(scBoxes)">Collapse All</a>
                <xsl:text> ]</xsl:text>
             </span>
          </p>
        </div>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out the section header of a top-level schema component.
     Param(s):
            component (Node) required
              Top-level schema component
     -->
  <xsl:template name="ComponentSectionHeader">
     <xsl:param name="component"/>

     <xsl:variable name="componentID">
        <xsl:call-template name="GetComponentID">
          <xsl:with-param name="component" select="$component"/>
        </xsl:call-template>
     </xsl:variable>
     <xsl:variable name="componentDescription">
        <xsl:call-template name="GetComponentDescription">
          <xsl:with-param name="component" select="$component"/>
        </xsl:call-template>
     </xsl:variable>
     <xsl:variable name="componentTermRef">
        <xsl:call-template name="GetComponentTermRef">
          <xsl:with-param name="component" select="$component"/>
        </xsl:call-template>
     </xsl:variable>

     <h3>
        <!-- Description -->
        <xsl:choose>
          <xsl:when test="$componentTermRef != ''">
             <xsl:call-template name="PrintGlossaryTermRef">
                <xsl:with-param name="code" select="$componentTermRef"/>
                <xsl:with-param name="term" select="$componentDescription"/>
             </xsl:call-template>
          </xsl:when>
          <xsl:otherwise> 
             <xsl:value-of select="$componentDescription"/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text>: </xsl:text>
        <!-- Name -->
        <a name="{$componentID}" class="name">
          <xsl:value-of select="$component/@name"/>
        </a>
     </h3>
  </xsl:template>

  <!-- 
     Prints out footer for top-level sections. 
     -->
  <xsl:template name="SectionFooter">
     <!-- Link to top of page-->
     <div style="text-align:right"><a href="#top">top</a></div>
     <hr/>
  </xsl:template>

  <!-- 
     Java Script code required by the entire HTML document.
     -->
  <xsl:template name="DocumentJSCode">
     <!-- Get all IDs of XML Instance Representation boxes
            and place them in an array. -->
     <xsl:text>/* IDs of XML Instance Representation boxes */
</xsl:text>
     <xsl:text>var xiBoxes = new Array(</xsl:text>
     <xsl:for-each select="/xsd:schema/xsd:*[@name]">
        <xsl:if test="position()!=1">
          <xsl:text>, </xsl:text>
        </xsl:if>
        <xsl:text>'</xsl:text>
        <xsl:call-template name="GetComponentID">
          <xsl:with-param name="component" select="."/>
        </xsl:call-template>
        <xsl:text>_xibox'</xsl:text>
     </xsl:for-each>
     <xsl:text>);
</xsl:text>

     <!-- Get all IDs of Schema Component Representation boxes
            and place them in an array. -->
     <xsl:text>/* IDs of Schema Component Representation boxes */
</xsl:text>
     <xsl:text>var scBoxes = new Array('schema_scbox'</xsl:text>
     <xsl:for-each select="/xsd:schema/xsd:*[@name]">
        <xsl:text>, '</xsl:text>
        <xsl:call-template name="GetComponentID">
          <xsl:with-param name="component" select="."/>
        </xsl:call-template>
        <xsl:text>_scbox'</xsl:text>
     </xsl:for-each>
     <xsl:text>);
</xsl:text>

     <!-- Functions -->
     <xsl:text>
/**
 * Can get the ID of the button controlling
 * a collapseable box by concatenating
 * this string onto the ID of the box itself.
 */
var B_SFIX = "_button";

/**
 * Counter of documentation windows
 * Used to give each window a unique name
 */
var windowCount = 0;

/**
 * Initialises the state of the HTML page.
 */
function init() {
  var obj = getElementObject("globalControls");
  if (obj != null) {
     obj.style.display="block";
  }
  obj = getElementObject("printerControls");
  if (obj != null) {
     obj.style.display="inline";
  }
  expandAll(xiBoxes);
  collapseAll(scBoxes);
  viewControlButtons(xiBoxes);
  viewControlButtons(scBoxes);
}

/**
 * Returns an element in the current HTML document.
 * 
 * @param elementID Identifier of HTML element
 * @return              HTML element object
 */
function getElementObject(elementID) {
    var elemObj = null;
    if (document.getElementById) {
        elemObj = document.getElementById(elementID);
    }
    return elemObj;
}            

/**
 * Closes a collapseable box.
 * 
 * @param boxObj      Collapseable box
 * @param buttonObj Button controlling box
 */
function closeBox(boxObj, buttonObj) {
  if (boxObj == null || buttonObj == null) {
     // Box or button not found
  } else {
     // Change 'display' CSS property of box
     boxObj.style.display="none";

     // Change text of button
     if (boxObj.style.display=="none") {
        buttonObj.value=" + ";
     }
  }
}

/**
 * Opens a collapseable box.
 * 
 * @param boxObj      Collapseable box
 * @param buttonObj Button controlling box
 */
function openBox(boxObj, buttonObj) {
  if (boxObj == null || buttonObj == null) {
     // Box or button not found
  } else {
     // Change 'display' CSS property of box
     boxObj.style.display="block";

     // Change text of button
     if (boxObj.style.display=="block") {
        buttonObj.value=" - ";
     }
  }
}

/**
 * Switches the state of a collapseable box, e.g.
 * if it's opened, it'll be closed, and vice versa.
 * 
 * @param boxID Identifier of box
 */
function switchState(boxID) {
  var boxObj = getElementObject(boxID);
  var buttonObj = getElementObject(boxID+B_SFIX);
  if (boxObj == null || buttonObj == null) {
     // Box or button not found
  } else if (boxObj.style.display=="none") {
     // Box is closed, so open it
     openBox(boxObj, buttonObj);
  } else if (boxObj.style.display=="block") {
     // Box is opened, so close it
     closeBox(boxObj, buttonObj);
  }
}

/**
 * Closes all boxes in a given list.
 * 
 * @param boxList Array of box IDs
 */
function collapseAll(boxList) {
  var idx;
  for (idx = 0; idx &lt; boxList.length; idx++) {
     var boxObj = getElementObject(boxList[idx]);
     var buttonObj = getElementObject(boxList[idx]+B_SFIX);
     closeBox(boxObj, buttonObj);
  }
}

/**
 * Open all boxes in a given list.
 * 
 * @param boxList Array of box IDs
 */
function expandAll(boxList) {
  var idx;
  for (idx = 0; idx    &lt; boxList.length; idx++) {
     var boxObj = getElementObject(boxList[idx]);
     var buttonObj = getElementObject(boxList[idx]+B_SFIX);
     openBox(boxObj, buttonObj);
  }
}

/**
 * Makes all the control buttons of boxes appear.
 * 
 * @param boxList Array of box IDs
 */
function viewControlButtons(boxList) {
    var idx;
    for (idx = 0; idx &lt; boxList.length; idx++) {
        buttonObj = getElementObject(boxList[idx]+B_SFIX);
        if (buttonObj != null) {
            buttonObj.style.display = "inline";
        }
    }
}

/**
 * Makes all the control buttons of boxes disappear.
 * 
 * @param boxList Array of box IDs
 */
function hideControlButtons(boxList) {
    var idx;
    for (idx = 0; idx &lt; boxList.length; idx++) {
        buttonObj = getElementObject(boxList[idx]+B_SFIX);
        if (buttonObj != null) {
            buttonObj.style.display = "none";
        }
    }
}

/**
 * Sets the page for either printing mode
 * or viewing mode. In printing mode, the page
 * is made to be more readable when printing it out.
 * In viewing mode, the page is more browsable.
 *
 * @param isPrinterVersion If true, display in
 *                                 printing mode; otherwise, 
 *                                 in viewing mode
 */
function displayMode(isPrinterVersion) {
    var obj;
    if (isPrinterVersion) {
        // Hide global control buttons
        obj = getElementObject("globalControls");
        if (obj != null) {
            obj.style.display = "none";
        }
        // Hide Legend
        obj = getElementObject("legend");
        if (obj != null) {
            obj.style.display = "none";
        }
        obj = getElementObject("legendTOC");
        if (obj != null) {
            obj.style.display = "none";
        }
        // Hide Glossary
        obj = getElementObject("glossary");
        if (obj != null) {
            obj.style.display = "none";
        }
        obj = getElementObject("glossaryTOC");
        if (obj != null) {
            obj.style.display = "none";
        }

        // Expand all XML Instance Representation tables
        expandAll(xiBoxes);
        // Expand all Schema Component Representation tables
        expandAll(scBoxes);

        // Hide Control buttons
        hideControlButtons(xiBoxes);
        hideControlButtons(scBoxes);
    } else {
        // View global control buttons
        obj = getElementObject("globalControls");
        if (obj != null) {
            obj.style.display = "block";
        }
        // View Legend
        obj = getElementObject("legend");
        if (obj != null) {
            obj.style.display = "block";
        }
        obj = getElementObject("legendTOC");
        if (obj != null) {
            obj.style.display = "block";
        }
        // View Glossary
        obj = getElementObject("glossary");
        if (obj != null) {
            obj.style.display = "block";
        }
        obj = getElementObject("glossaryTOC");
        if (obj != null) {
            obj.style.display = "block";
        }

        // Expand all XML Instance Representation tables
        expandAll(xiBoxes);
        // Collapse all Schema Component Representation tables
        collapseAll(scBoxes);

        // View Control buttons
        viewControlButtons(xiBoxes);
        viewControlButtons(scBoxes);
    }
}

/**
 * Opens up a window displaying the documentation
 * of a schema component in the XML Instance
 * Representation table.
 * 
 * @param compDesc      Description of schema component 
 * @param compName      Name of schema component 
 * @param docTextArray Array containing the paragraphs 
 *                           of the new document
 */
function viewDocumentation(compDesc, compName, docTextArray) {
  var width = 400;
  var height = 200;
  var locX = 100;
  var locY = 200;

  /* Generate content */
  var actualText = "&lt;html>";
  actualText += "&lt;head>&lt;title>";
  actualText += compDesc;
  if (compName != '') {
     actualText += ": " + compName;
  }
  actualText += "&lt;/title>&lt;/head>";
  actualText += "&lt;body bgcolor=\"#FFFFEE\">";
  // Title
  actualText += "&lt;p style=\"font-family: Arial, san-serif; font-size: 12pt; font-weight: bold; letter-spacing:1px;\">";
  actualText += compDesc;
  if (compName != '') {
     actualText += ": &lt;span style=\"color:#006699\">" + compName + "&lt;/span>";
  }
  actualText += "&lt;/p>";
  // Documentation
  var idx;
  for (idx = 0; idx &lt; docTextArray.length; idx++) {
     actualText += "&lt;p style=\"font-family: Arial, san-serif; font-size: 10pt;\">" + docTextArray[idx] + "&lt;/p>";
  }
  // Link to close window
  actualText += "&lt;a href=\"javascript:void(0)\" onclick=\"window.close();\" style=\"font-family: Arial, san-serif; font-size: 8pt;\">Close&lt;/a>";
  actualText += "&lt;/body>&lt;/html>";

  /* Display window */
  windowCount++;
  var docWindow = window.open("", "documentation"+windowCount, "toolbar=no,location=no,status=no,menubar=no,scrollbars=auto,resizable,alwaysRaised,dependent,titlebar=no,width="+width+",height="+height+",screenX="+locX+",left="+locX+",screenY="+locY+",top="+locY);
  docWindow.document.write(actualText);
}
</xsl:text>
  </xsl:template>

  <!--
     CSS properties for the entire HTML document.
     -->
  <xsl:template name="DocumentCSSStyles">
<xsl:text>
     /* General */
     body, p, li, span, th, td, div {
        font-family: Arial, san-serif;
        font-size: 10pt;
     }
     body {
        color:black;
        background-color:white;
     }
     ul {
        margin-left: 1.5em;
        margin-bottom: 0em;
     }
     hr {
        color: black;
     }
     table {
        margin-top: 10px;
        margin-bottom: 10px;
        margin-left: 2px;
        margin-right: 2px;
     }
     table td, table th {
        padding-top: 3px;
        padding-bottom: 3px;
        padding-left: 10px;
        padding-right: 10px;
        vertical-align: top;
     }
     table th {
        font-weight:bold;
        text-align:left;
     }
     table.hierarchy, table.sample, table.schemaComponent {
        background-color: white;
     }
     table.properties, table.hierarchy, table.sample, table.schemaComponent {
        width: 90%;
     }


     /* For box containing whether to display in printing mode 
         or viewing mode */
     #printerControls {
        white-space: nowrap;
        font-weight: bold;
        color: #996633; /* orange-brown */
        position: absolute;
        left: 60%;
        top: 5em;
     }


     /* For box containing collapse and expand all boxes */
     #globalControls {
        border: 2px solid #aaaaaa;
        padding: 1em;
        position: absolute;
        left: 60%;
        top: 7em;
     }


     /* Control buttons to make 'div' boxes appear and disappear */
     .boxControl {
        width: 1.4em;
        height: 1.4em;
        text-align: center;
        vertical-align: middle;
        font-size: 11pt;
     }


     /* Legend table */
     #legend table {
        margin-top: 2em;
     }
     .legendhint {
        color: #888888; /* light gray */
        width: 90%;
        margin-left: 1em;
     }

     /* Namespaces table */
     table.namespaces th, table.namespaces td {
        background-color: #eeeeee;
     }

     /* Hierarchy table */
     table.hierarchy {
        border: 2px solid #aaaaaa; /* gray */
     }
     table.hierarchy th {
        font-weight:normal;
        font-style:italic;
        width: 20%;
     }
     table.hierarchy th, table.hierarchy td {
        padding: 5px;
     }


     /* Properties table */
     table.properties th {
        width: 30%;
        background-color:#FF7777; /* pink */
     }
     table.properties th, table.properties th a {
        color: black;
     }
     table.properties td {
        background-color:#eeeeee; /* gray */
     }


     /* XML Instance Representation table */
     table.sample td div.box {
        border: 2px dashed black;
        padding: 5px;
     }
        /* Normal elements and attributes */
     table.sample td, table.sample td a {
        color: black;
     }
        /* Group Headers */
     table.sample td .group, table.sample td .group a {
        color: #999999; /* light gray */
     }
        /* Type Information */
     table.sample td .type, table.sample td .type a {
        color: #999999; /* light gray */
     }
        /* Occurrence Information */
     table.sample td .occurs, table.sample td .occurs a {
        color: #999999; /* light gray */
     }
        /* Fixed values */
     table.sample td .fixed {
        color:#006633; /* green */
        font-weight: bold;
     }
        /* Simple type constraints */
     table.sample td .constraint, table.sample td .constraint a {
        color: #999999; /* light gray */
     }
        /* Elements and attributes inherited from base type */
     table.sample td .inherited, table.sample td .inherited a {
        color: #666666; /* dark gray */
     }
        /* Elements and attributes added to or changed from base type */
     table.sample td .newFields {
        font-weight:bold;
     }
        /* Other type of information */
     table.sample td .other, table.sample td .other a {
        color:#336699; /* blue */
        font-style: italic;
     }
        /* Link to open up window displaying documentation */
     table.sample td a.documentation {
        text-decoration:none;
        padding-left: 3px;
        padding-right: 3px;
        padding-top: 0px;
        padding-bottom: 0px;
        font-weight: bold;
        font-size: 11pt;
        background-color: #FFFFDD;
        color: #006699;
     }
        /* Invert colors of documentation link when mouse hovers 
            over it */
     table.sample td a.documentation:hover {
        color: #FFFFDD;
        background-color: #006699;
     }


     /* Schema Component Representation table */
     table.schemaComponent td div.box {
        padding: 5px;
        border: 2px black solid;
     }
        /* Syntax characters */
     table.schemaComponent td {
        color:#0000ff; /* blue */
     }
        /* Element and attribute tags */
     table.schemaComponent td .scTag {
        color:#AA3333; /* maroon */
     }
        /* Element and attribute content */
     table.schemaComponent td .scContent, table.schemaComponent td .scContent a {
        color:black;
        font-weight:bold;
     }


     /* Title */
     h1 {
        font-family: Arial, san-serif;
        font-size: 18pt;
        letter-spacing: 2px;
        border-bottom: 1px #cccccc solid;
        padding-top: 5px;
        padding-bottom: 5px;
        width: 75%;
     }


     /* Section Headers */
     h2 {
        font-family: Arial, san-serif;
        font-size: 14pt;
        letter-spacing: 1px;
     }
     h3, h3 a, .name, .header, .headername {
        font-family: Arial, san-serif;
        font-size: 12pt;
        font-weight: bold;
        color: black;
     }
     .name, .headername {
        color:#FF9933; /* orange */
     }


     /* Target Namespace */
     .targetNS {
        color: red;
        font-weight:bold;
     }


     /* Glossary Terms */
     .glossaryTerm {
        color:#003366; /* blue */
        font-weight:bold;
     }


     /* XML code */
     code, table.schemaComponent td, table.schemaComponent td span, table.schemaComponent td div, table.sample td, table.sample td span, table.sample td div {
        font-family: Courier New, san-serif;
        font-size: 10pt;
     }
</xsl:text>
  </xsl:template>

  <!--
     Print outs a legend describing the meaning of colors, bold items,
     etc. in the top-level sections.
     -->
  <xsl:template name="Legend">
     <!-- Header -->
     <table style="padding:0px">
     <tr>
        <td>
          <span class="header">Complex Type: </span>
        </td>
        <td>
          <span class="headername">AusAddress</span>
        </td>
     </tr>
     <tr>
        <td>
          <span class="legendhint" style="margin-left: 0em; white-space: nowrap">Schema Component Type</span>
        </td>
        <td>
          <span class="legendhint" style="margin-left: 0em; white-space: nowrap">Schema Component Name</span>
        </td>
     </tr>
     </table>

     <!-- Hierarchy Table -->
     <table class="hierarchy">
     <tr>
        <xsl:choose>
          <xsl:when test="normalize-space(translate($printAllSuperTypes, 'TRUE', 'true'))='true'">
             <th>Super-types:</th>
             <td>
                <span class="type" style="color: #0000FF; text-decoration:underline;">Address</span>
                <xsl:text> &lt; </xsl:text>
                <span class="current">AusAddress</span>
                <xsl:text> (by extension)</xsl:text>
             </td>
          </xsl:when>
          <xsl:otherwise> 
             <th>Parent type:</th>
             <td>
                <span class="type" style="color: #0000FF; text-decoration:underline;">Address</span>
                <xsl:text> (derivation method: extension)</xsl:text>
             </td>
          </xsl:otherwise>
        </xsl:choose>
     </tr>
     <tr>
        <xsl:choose>
          <xsl:when test="normalize-space(translate($printAllSubTypes, 'TRUE', 'true'))='true'">
             <th>Sub-types:</th>
             <td>
                <ul><li>
                  <span class="type" style="color: #0000FF; text-decoration:underline;">QLDAddress</span><xsl:text> (by restriction)</xsl:text>
                </li></ul>
             </td>
          </xsl:when>
          <xsl:otherwise> 
             <th>Direct sub-types:</th>
             <td>
                <ul><li>
                  <span class="type" style="color: #0000FF; text-decoration:underline;">QLDAddress</span><xsl:text> (by restriction)</xsl:text>
                </li></ul>
             </td>
          </xsl:otherwise>
        </xsl:choose>
     </tr>
     </table>
     <div class="legendhint">If this schema component is a type definition, its type hierarchy is shown in a gray-bordered box.</div>

     <!-- Properties Table -->
     <table class="properties">
        <tr><th>Name</th><td>AusAddress</td></tr>
        <tr>
          <th>
             <a title="Look up 'Abstract' in glossary" href="#term_Abstract">Abstract</a>
          </th>
          <td>no</td>
        </tr>
     </table>
     <div class="legendhint">The table above displays the properties of this schema component.</div>

     <!-- XML Instance Representation Table -->
     <table class="sample">
     <tr><th>XML Instance Representation</th></tr>
     <tr><td>
        <div class="box">
          <span style="margin-left: 0em">&lt;...</span>
          <span class="newFields">
             <span> country="<span class="fixed">Australia</span>"</span>
          </span>
          <xsl:text>&gt; </xsl:text><br/>
          <span style="margin-left: 1.5em" class="inherited">&lt;unitNo&gt; <span class="type">string</span> &lt;/unitNo&gt; <span class="occurs">[0..1]</span></span><br/>
          <span style="margin-left: 1.5em" class="inherited">&lt;houseNo&gt; <span class="type">string</span> &lt;/houseNo&gt; <span class="occurs">[1]</span></span><br/>
          <span style="margin-left: 1.5em" class="inherited">&lt;street&gt; <span class="type">string</span> &lt;/street&gt; <span class="occurs">[1]</span></span><br/>
          <span class="group" style="margin-left: 1.5em">Start <a title="Look up 'Choice' in glossary" href="#term_Choice">Choice</a> <span class="occurs">[1]</span></span><br/>
          <span style="margin-left: 3em" class="inherited">&lt;city&gt; <span class="type">string</span> &lt;/city&gt; <span class="occurs">[1]</span></span><br/>
          <span style="margin-left: 3em" class="inherited">&lt;town&gt; <span class="type">string</span> &lt;/town&gt; <span class="occurs">[1]</span></span><br/>
          <span class="group" style="margin-left: 1.5em">End Choice</span><br/>
          <span class="newFields">
             <span style="margin-left: 1.5em">&lt;state&gt; <span class="type" style="text-decoration:underline;">AusStates</span> &lt;/state&gt; <span class="occurs">[1]</span></span><br/>
             <span style="margin-left: 1.5em">&lt;postcode&gt; <span class="constraint">string &lt;&lt;<em>pattern</em> = [1-9][0-9]{3}>></span> &lt;/postcode&gt; <span class="occurs">[1]</span>
             <xsl:if test="normalize-space(translate($useJavaScript,'TRUE','true'))='true'">
                <a href="javascript:void(0)" title="View Documentation" class="documentation" onclick="docArray = new Array('Post code must be a four-digit number.'); viewDocumentation('Element', 'postcode', docArray);">?</a>
             </xsl:if>
             </span><br/>
          </span>
          <span style="margin-left: 0em">&lt;/...&gt;</span><br/>
        </div>
     </td></tr>
     </table>

     <div class="legendhint">
        <p>The XML Instance Representation table above shows the schema component's content as an XML instance.</p>
        <ul>
          <li>The minimum and maximum occurrence of elements and attributes are provided in square brackets, e.g. [0..1].</li>
          <li>Model group information are shown in gray, e.g. Start Choice ... End Choice.</li>
          <li>For type derivations, the elements and attributes that have been added to or changed from the base type's content are shown in <span style="font-weight: bold">bold</span>.</li>
          <li>If an element/attribute has a fixed value, the fixed value is shown in green, e.g. country="Australia".</li>
          <li>Otherwise, the type of the element/attribute is displayed.
          <ul>
             <li>If the element/attribute's type is in the schema, a link is provided to it.</li>
             <li>For local simple type definitions, the constraints are displayed in angle brackets, e.g. &lt;&lt;<em>pattern</em> = [1-9][0-9]{3}>>.</li>
          </ul>
          </li>
          <xsl:if test="normalize-space(translate($useJavaScript,'TRUE','true'))='true'">
             <li>If a local element/attribute has documentation, it will be displayed in a window that pops up when the question mark inside the attribute or next to the element is clicked, e.g. &lt;postcode>.</li>
          </xsl:if>
        </ul>
     </div>

     <!-- Schema Component Representation Table -->
     <table class="schemaComponent">
     <tr><th>Schema Component Representation</th></tr>
     <tr><td>
        <div class="box">
          <span style="margin-left: 0em">&lt;<span class="scTag">complexType</span> <span class="scTag">name</span>="<span class="scContent">AusAddress</span>"&gt;</span><br/>
          <span style="margin-left: 1.5em">&lt;<span class="scTag">complexContent</span>&gt;</span><br/>
          <span style="margin-left: 3em">&lt;<span class="scTag">extension</span> <span class="scTag">base</span>="<span class="scContent"><span class="type" style="text-decoration:underline;">Address</span></span>"&gt;</span><br/>
          <span style="margin-left: 4.5em">&lt;<span class="scTag">sequence</span>&gt;</span><br/>
          <span style="margin-left: 6em">&lt;<span class="scTag">element</span> <span class="scTag">name</span>="<span class="scContent">state</span>" <span class="scTag">type</span>="<span class="scContent"><span class="type" style="text-decoration:underline;">AusStates</span></span>"/&gt;</span><br/>
          <span style="margin-left: 6em">&lt;<span class="scTag">element</span> <span class="scTag">name</span>="<span class="scContent">postcode</span>"&gt;</span><br/>
          <span style="margin-left: 7.5em">&lt;<span class="scTag">simpleType</span>&gt;</span><br/>
          <span style="margin-left: 9em">&lt;<span class="scTag">restriction</span> <span class="scTag">base</span>="<span class="scContent"><span class="type">string</span></span>"&gt;</span><br/>
          <span style="margin-left: 10.5em">&lt;<span class="scTag">pattern</span> <span class="scTag">value</span>="<span class="scContent">[1-9][0-9]{3}</span>"/&gt;</span><br/>
          <span style="margin-left: 9em">&lt;/<span class="scTag">restriction</span>&gt;</span><br/>
          <span style="margin-left: 7.5em">&lt;/<span class="scTag">simpleType</span>&gt;</span><br/>
          <span style="margin-left: 6em">&lt;/<span class="scTag">element</span>&gt;</span><br/>
          <span style="margin-left: 4.5em">&lt;/<span class="scTag">sequence</span>&gt;</span><br/>
          <span style="margin-left: 4.5em">&lt;<span class="scTag">attribute</span> <span class="scTag">name</span>="<span class="scContent">country</span>" <span class="scTag">type</span>="<span class="scContent"><span class="type">string</span></span>" <span class="scTag">fixed</span>="<span class="scContent">Australia</span>"/&gt;</span><br/>
          <span style="margin-left: 3em">&lt;/<span class="scTag">extension</span>&gt;</span><br/>
          <span style="margin-left: 1.5em">&lt;/<span class="scTag">complexContent</span>&gt;</span><br/>
          <span style="margin-left: 0em">&lt;/<span class="scTag">complexType</span>&gt;</span><br/>
        </div>
     </td></tr>
     </table>

     <div class="legendhint">The Schema Component Representation table above displays the underlying XML representation of the schema component. (Annotations are not shown.)</div>
  </xsl:template>

  <!--
     Print outs all terms for the glossary section.
     -->
  <xsl:template name="Glossary">
     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">Abstract</xsl:with-param>
        <xsl:with-param name="term">Abstract</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>(Applies to complex type definitions and element declarations).</xsl:text>
          <xsl:text> An abstract element or complex type cannot used to validate an element instance.</xsl:text>
          <xsl:text> If there is a reference to an abstract element, only element declarations that can substitute the abstract element can be used to validate the instance.</xsl:text>
          <xsl:text> For references to abstract type definitions, only derived types can be used.</xsl:text>
        </xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">All</xsl:with-param>
        <xsl:with-param name="term">All Model Group</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>Child elements can be provided </xsl:text>
          <em><xsl:text>in any order</xsl:text></em>
          <xsl:text> in instances.</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="link">http://www.w3.org/TR/xmlschema-1/#element-all</xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">Choice</xsl:with-param>
        <xsl:with-param name="term">Choice Model Group</xsl:with-param>
        <xsl:with-param name="description">
          <em><xsl:text>Only one</xsl:text></em>
          <xsl:text> from the list of child elements and model groups can be provided in instances.</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="link">http://www.w3.org/TR/xmlschema-1/#element-choice</xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">CollapseWS</xsl:with-param>
        <xsl:with-param name="term">Collapse Whitespace Policy</xsl:with-param>
        <xsl:with-param name="description">Replace tab, line feed, and carriage return characters with space character (Unicode character 32). Then, collapse contiguous sequences of space characters into single space character, and remove leading and trailing space characters.</xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">ElemBlock</xsl:with-param>
        <xsl:with-param name="term">Disallowed Substitutions</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>(Applies to element declarations).</xsl:text>
          <xsl:text> If </xsl:text>
          <em>substitution</em>
          <xsl:text> is specified, then </xsl:text>
          <xsl:call-template name="PrintGlossaryTermRef">
             <xsl:with-param name="code">SubGroup</xsl:with-param>
             <xsl:with-param name="term">substitution group</xsl:with-param>
          </xsl:call-template>
          <xsl:text> members cannot be used in place of the given element declaration to validate element instances.</xsl:text>

          <xsl:text> If </xsl:text><em>derivation methods</em>
          <xsl:text>, e.g. extension, restriction, are specified, then the given element declaration will not validate element instances that have types derived from the element declaration's type using the specified derivation methods.</xsl:text>
          <xsl:text> Normally, element instances can override their declaration's type by specifying an </xsl:text>
          <code>xsi:type</code>
          <xsl:text> attribute.</xsl:text>
        </xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">Key</xsl:with-param>
        <xsl:with-param name="term">Key Constraint</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>Like </xsl:text>
          <xsl:call-template name="PrintGlossaryTermRef">
             <xsl:with-param name="code">Unique</xsl:with-param>
             <xsl:with-param name="term">Uniqueness Constraint</xsl:with-param>
          </xsl:call-template>
          <xsl:text>, but additionally requires that the specified value(s) must be provided.</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="link">http://www.w3.org/TR/xmlschema-1/#cIdentity-constraint_Definitions</xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">KeyRef</xsl:with-param>
        <xsl:with-param name="term">Key Reference Constraint</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>Ensures that the specified value(s) must match value(s) from a </xsl:text>
          <xsl:call-template name="PrintGlossaryTermRef">
             <xsl:with-param name="code">Key</xsl:with-param>
             <xsl:with-param name="term">Key Constraint</xsl:with-param>
          </xsl:call-template>
          <xsl:text> or </xsl:text>
          <xsl:call-template name="PrintGlossaryTermRef">
             <xsl:with-param name="code">Unique</xsl:with-param>
             <xsl:with-param name="term">Uniqueness Constraint</xsl:with-param>
          </xsl:call-template>
          <xsl:text>.</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="link">http://www.w3.org/TR/xmlschema-1/#cIdentity-constraint_Definitions</xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">ModelGroup</xsl:with-param>
        <xsl:with-param name="term">Model Group</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>Groups together element content, specifying the order in which the element content can occur and the number of times the group of element content may be repeated.</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="link">http://www.w3.org/TR/xmlschema-1/#Model_Groups</xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">Nillable</xsl:with-param>
        <xsl:with-param name="term">Nillable</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>(Applies to element declarations). </xsl:text>
          <xsl:text>If an element declaration is nillable, instances can use the </xsl:text>
          <code>xsi:nil</code>
          <xsl:text> attribute.</xsl:text>
          <xsl:text> The </xsl:text>
          <code>xsi:nil</code>
          <xsl:text> attribute is the boolean attribute, </xsl:text>
          <em>nil</em>
          <xsl:text>, from the </xsl:text>
          <em>http://www.w3.org/2001/XMLSchema-instance</em>
          <xsl:text> namespace.</xsl:text>
          <xsl:text> If an element instance has an </xsl:text>
          <code>xsi:nil</code>
          <xsl:text> attribute set to true, it can be left empty, even though its element declaration may have required content.</xsl:text>
        </xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">Notation</xsl:with-param>
        <xsl:with-param name="term">Notation</xsl:with-param>
        <xsl:with-param name="description">A notation is used to identify the format of a piece of data. Values of elements and attributes that are of type, NOTATION, must come from the names of declared notations.</xsl:with-param>
        <xsl:with-param name="link">http://www.w3.org/TR/xmlschema-1/#cNotation_Declarations</xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">PreserveWS</xsl:with-param>
        <xsl:with-param name="term">Preserve Whitespace Policy</xsl:with-param>
        <xsl:with-param name="description">Preserve whitespaces exactly as they appear in instances.</xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">TypeFinal</xsl:with-param>
        <xsl:with-param name="term">Prohibited Derivations</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>(Applies to type definitions). </xsl:text>
          <xsl:text>Derivation methods that cannot be used to create sub-types from a given type definition.</xsl:text>
        </xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">TypeBlock</xsl:with-param>
        <xsl:with-param name="term">Prohibited Substitutions</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>(Applies to complex type definitions). </xsl:text>
          <xsl:text>Prevents sub-types that have been derived using the specified derivation methods from validating element instances in place of the given type definition.</xsl:text>
        </xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">ReplaceWS</xsl:with-param>
        <xsl:with-param name="term">Replace Whitespace Policy</xsl:with-param>
        <xsl:with-param name="description">Replace tab, line feed, and carriage return characters with space character (Unicode character 32).</xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">Sequence</xsl:with-param>
        <xsl:with-param name="term">Sequence Model Group</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>Child elements and model groups must be provided </xsl:text>
          <em><xsl:text>in the specified order</xsl:text></em>
          <xsl:text> in instances.</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="link">http://www.w3.org/TR/xmlschema-1/#element-sequence</xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">SubGroup</xsl:with-param>
        <xsl:with-param name="term">Substitution Group</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>Elements that are </xsl:text>
          <em><xsl:text>members</xsl:text></em>
          <xsl:text> of a substitution group can be used wherever the </xsl:text>
          <em><xsl:text>head</xsl:text></em>
          <xsl:text> element of the substitution group is referenced.</xsl:text>
        </xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">ElemFinal</xsl:with-param>
        <xsl:with-param name="term">Substitution Group Exclusions</xsl:with-param>
        <xsl:with-param name="description">
          <xsl:text>(Applies to element declarations). </xsl:text>
          <xsl:text>Prohibits element declarations from nominating themselves as being able to substitute a given element declaration, if they have types that are derived from the original element's type using the specified derivation methods.</xsl:text>
        </xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">TargetNS</xsl:with-param>
        <xsl:with-param name="term">Target Namespace</xsl:with-param>
        <xsl:with-param name="description">The target namespace identifies the namespace that components in this schema belongs to. If no target namespace is provided, then the schema components do not belong to any namespace.</xsl:with-param>
     </xsl:call-template>

     <xsl:call-template name="PrintGlossaryTerm">
        <xsl:with-param name="code">Unique</xsl:with-param>
        <xsl:with-param name="term">Uniqueness Constraint</xsl:with-param>
        <xsl:with-param name="description">Ensures uniqueness of an element/attribute value, or a combination of values, within a specified scope.</xsl:with-param>
        <xsl:with-param name="link">http://www.w3.org/TR/xmlschema-1/#cIdentity-constraint_Definitions</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Prints out a term in the glossary section.
     Param(s):
            code (String) required
              Unique ID of glossary term
            term (String) required
              Glossary term
            description (Result Tree Fragment) required
              Meaning of term; may contain HTML tags and links
            link (String) optional
              URI containing more info about term
     -->
  <xsl:template name="PrintGlossaryTerm">
     <xsl:param name="code"/>
     <xsl:param name="term"/>
     <xsl:param name="description"/>
     <xsl:param name="link"/>

     <p>
        <span class="glossaryTerm">
          <a name="{concat($TERM_PREFIX, $code)}">
             <xsl:value-of select="$term"/>
          </a>
          <xsl:text> </xsl:text>
        </span>

        <xsl:copy-of select="$description"/>

        <xsl:if test="$link != ''">
          <xsl:text> See: </xsl:text>
          <xsl:call-template name="PrintURI">
             <xsl:with-param name="uri" select="$link"/>
          </xsl:call-template>
          <xsl:text>.</xsl:text>
        </xsl:if>
     </p>
  </xsl:template>


  <!-- **** Hierarchy table **** -->

  <!-- 
     Prints out substitution group hierarchy for
     element declarations.
     -->
  <xsl:template match="xsd:element" mode="hierarchy">
     <!--
        Find out members of substitution group that
        this element heads.
        -->
     <xsl:variable name="members">
        <ul>
          <xsl:call-template name="PrintSGroupMembers">
             <xsl:with-param name="element" select="."/>
          </xsl:call-template>
        </ul>
     </xsl:variable>
     <xsl:variable name="hasMembers">
        <xsl:if test="normalize-space($members)!=''">
          <xsl:text>true</xsl:text>
        </xsl:if>
     </xsl:variable>

     <!-- Print hierarchy table -->
     <xsl:if test="@substitutionGroup or normalize-space($hasMembers)='true'">
        <table class="hierarchy"><tr><td>
        <ul>
        <!-- Print substitution group that this element belongs to -->
        <xsl:if test="@substitutionGroup">
          <li>
             <em>This element can be used wherever the following element is referenced: </em>
             <ul>
                <li>
                  <xsl:call-template name="PrintElementRef">
                     <xsl:with-param name="ref" select="@substitutionGroup"/>
                  </xsl:call-template>
                </li>
             </ul>
          </li>
        </xsl:if>
        <!-- Print substitution group that this element heads -->
        <xsl:if test="normalize-space($hasMembers)='true'">
          <li>
             <em>The following elements can be used wherever this element is referenced: </em>
             <xsl:copy-of select="$members"/>
          </li>
        </xsl:if>
        </ul>
        </td></tr></table>
     </xsl:if>
  </xsl:template>

  <!-- 
     Prints out Hierarchy table for complex type definitions.
     -->
  <xsl:template match="xsd:complexType" mode="hierarchy">
     <table class="hierarchy">
        <!-- Print super types -->
        <tr>
          <th>
             <xsl:choose>
                <xsl:when test="normalize-space(translate($printAllSuperTypes, 'TRUE', 'true'))='true'">
                  <xsl:text>Super-types:</xsl:text>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:text>Parent type:</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </th>
          <td>
             <xsl:choose>
                <xsl:when test="xsd:simpleContent or xsd:complexContent">
                  <xsl:call-template name="PrintSupertypes">
                     <xsl:with-param name="type" select="."/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>None</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </td>
        </tr>

        <!-- Print sub types -->
        <tr>
          <th>
             <xsl:choose>
                <xsl:when test="normalize-space(translate($printAllSubTypes, 'TRUE', 'true'))='true'">
                  <xsl:text>Sub-types:</xsl:text>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:text>Direct sub-types:</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </th>
          <td>
             <xsl:call-template name="PrintComplexSubtypes">
                <xsl:with-param name="type" select="."/>
             </xsl:call-template>
          </td>
        </tr>
     </table>
  </xsl:template>

  <!-- 
     Prints out Hierarchy table for simple type definitions.
     -->
  <xsl:template match="xsd:simpleType" mode="hierarchy">
     <table class="hierarchy">
        <!-- Print super types -->
        <tr>
          <th>
             <xsl:choose>
                <xsl:when test="normalize-space(translate($printAllSuperTypes, 'TRUE', 'true'))='true'">
                  <xsl:text>Super-types:</xsl:text>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:text>Parent type:</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </th>
          <td>
             <xsl:choose>
                <xsl:when test="xsd:restriction">
                  <xsl:call-template name="PrintSupertypes">
                     <xsl:with-param name="type" select="."/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>None</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </td>
        </tr>

        <!-- Print sub types -->
        <tr>
          <th>
             <xsl:choose>
                <xsl:when test="normalize-space(translate($printAllSubTypes, 'TRUE', 'true'))='true'">
                  <xsl:text>Sub-types:</xsl:text>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:text>Direct sub-types:</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </th>
          <td>
             <xsl:call-template name="PrintSimpleSubtypes">
                <xsl:with-param name="type" select="."/>
             </xsl:call-template>
          </td>
        </tr>
     </table>
  </xsl:template>

  <!--
     Unmatched template for 'hierarchy' mode
     -->
  <xsl:template match="*" mode="hierarchy"/>

  <!-- 
     Prints out members, if any, of a substitution group that a given 
     element declaration heads.
     Assumes it will be called within XHTML <ul> tags.
     Param(s):
            element (Node) required
              Top-level element declaration
     -->
  <xsl:template name="PrintSGroupMembers">
     <xsl:param name="element"/>

     <!-- Get 'block' attribute. -->
     <xsl:variable name="block">
        <xsl:call-template name="PrintBlockSet">
          <xsl:with-param name="EBV">
             <xsl:choose>
                <xsl:when test="$element/@block">
                  <xsl:value-of select="$element/@block"/>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:value-of select="/xsd:schema/@blockDefault"/>
                </xsl:otherwise>
             </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
     </xsl:variable>

     <xsl:variable name="elemName" select="normalize-space($element/@name)"/>
     <xsl:for-each select="/xsd:schema/xsd:element[normalize-space(@substitutionGroup)=$elemName or normalize-space(substring-after(@substitutionGroup, ':'))=$elemName]">
        <li>
          <xsl:call-template name="PrintElementRef">
             <xsl:with-param name="name" select="@name"/>
          </xsl:call-template>
        </li>

        <!-- Recursively find members of a substitution group that 
              current element in list might head, since substitution 
              groups are transitive (unless 'substitution' is blocked). 
          -->
        <xsl:if test="not(contains($block, 'substitution'))">
          <xsl:call-template name="PrintSGroupMembers">
             <xsl:with-param name="element" select="."/>
          </xsl:call-template>
        </xsl:if>
     </xsl:for-each>
  </xsl:template>
  
  <!--
     Prints out the super types of a given type definition.
     Param(s):
            type (Node) required
                Type definition
            isCallingType (boolean) optional
                If true, 'type' is the type definition that starts this 
                call. Otherwise, this is a recursive call from 
                'PrintSupertypes'.
     -->
  <xsl:template name="PrintSupertypes">
     <xsl:param name="type"/>
     <xsl:param name="isCallingType">true</xsl:param>

     <!-- Get base type reference -->
     <xsl:variable name="baseTypeRef">
        <xsl:choose>
          <!-- Complex type definition -->
          <xsl:when test="local-name($type)='complexType'">
             <xsl:choose>
                <xsl:when test="$type/xsd:simpleContent/xsd:extension">
                  <xsl:value-of select="$type/xsd:simpleContent/xsd:extension/@base"/>
                 </xsl:when>
                <xsl:when test="$type/xsd:simpleContent/xsd:restriction">
                  <xsl:value-of select="$type/xsd:simpleContent/xsd:restriction/@base"/>
                 </xsl:when>
                <xsl:when test="$type/xsd:complexContent/xsd:extension">
                  <xsl:value-of select="$type/xsd:complexContent/xsd:extension/@base"/>
                 </xsl:when>
                <xsl:when test="$type/xsd:complexContent/xsd:restriction">
                  <xsl:value-of select="$type/xsd:complexContent/xsd:restriction/@base"/>
                </xsl:when>
             </xsl:choose>
          </xsl:when>
          <!-- Simple type definition -->
          <xsl:when test="local-name($type)='simpleType'">
             <xsl:choose>
                <xsl:when test="$type/xsd:restriction/@base">
                  <xsl:value-of select="$type/xsd:restriction/@base"/>
                </xsl:when>
                <xsl:when test="$type/xsd:restriction/xsd:simpleType">
                  <xsl:text>Local type definition</xsl:text>
                </xsl:when>
             </xsl:choose>
          </xsl:when>
        </xsl:choose>
     </xsl:variable>

     <!-- Get derivation method -->
     <xsl:variable name="derive">
        <!-- Complex type definition -->
        <xsl:choose>
          <xsl:when test="local-name($type)='complexType'">
             <xsl:choose>
                <xsl:when test="$type/xsd:simpleContent/xsd:extension or $type/xsd:complexContent/xsd:extension">
                  <xsl:text>extension</xsl:text>
                </xsl:when>
                <xsl:when test="$type/xsd:simpleContent/xsd:restriction or $type/xsd:complexContent/xsd:restriction">
                  <xsl:text>restriction</xsl:text>
                </xsl:when>
             </xsl:choose>
          </xsl:when>
          <!-- Simple type definition -->
          <xsl:when test="local-name($type)='simpleType'">
             <xsl:text>restriction</xsl:text>
          </xsl:when>
        </xsl:choose>
     </xsl:variable>

     <xsl:choose>
        <!-- Print out entire hierarchy -->
        <xsl:when test="normalize-space(translate($printAllSuperTypes, 'TRUE', 'true'))='true'">
          <xsl:choose>
             <xsl:when test="normalize-space($baseTypeRef)='Local type definition'">
                <xsl:value-of select="$baseTypeRef"/>

                <!-- Symbol to indicate type derivation-->
                <xsl:text> &lt; </xsl:text>
             </xsl:when>
             <xsl:when test="normalize-space($baseTypeRef)!=''">
                <!-- Get base type name from reference -->
                <xsl:variable name="baseTypeName">
                  <xsl:call-template name="GetRefName">
                     <xsl:with-param name="ref" select="$baseTypeRef"/>
                  </xsl:call-template>
                </xsl:variable>

                <!-- Get base type definition from schema -->
                <xsl:variable name="baseType" select="/xsd:schema/xsd:complexType[@name=$baseTypeName] | /xsd:schema/xsd:simpleType[@name=$baseTypeName] | /xsd:schema/xsd:redefine/xsd:complexType[@name=$baseTypeName] | /xsd:schema/xsd:redefine/xsd:simpleType[@name=$baseTypeName]"/>
                <xsl:choose>
                  <!-- Base type was found in this schema -->
                  <xsl:when test="$baseType">
                     <!-- Make recursive call to print out base type and itself -->
                     <xsl:call-template name="PrintSupertypes">
                        <xsl:with-param name="type" select="$baseType"/>
                        <xsl:with-param name="isCallingType">false</xsl:with-param>
                     </xsl:call-template>
                  </xsl:when>
                  <!-- Base type was not found in this schema -->
                  <xsl:otherwise> 
                     <xsl:call-template name="PrintTypeRef">
                        <xsl:with-param name="ref" select="$baseTypeRef"/>
                     </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>

                <!-- Symbol to indicate type derivation -->
                <xsl:text> &lt; </xsl:text>
             </xsl:when>
             <xsl:otherwise>
                <!-- IGNORE: Base type may not be exist probably because 
                      current type does not be derived from another type.
                  -->
             </xsl:otherwise>
          </xsl:choose>

          <!-- Print out current type's name -->
          <xsl:choose>
             <xsl:when test="$isCallingType='true'">
                <strong><xsl:value-of select="$type/@name"/></strong>
             </xsl:when>
             <xsl:otherwise> 
                <xsl:call-template name="PrintTypeRef">
                     <xsl:with-param name="name" select="$type/@name"/>
                </xsl:call-template>
             </xsl:otherwise>
          </xsl:choose>

          <!-- Print out derivation method -->
          <xsl:if test="$derive != ''">
             <xsl:text> (by </xsl:text>
             <xsl:value-of select="$derive"/>
             <xsl:text>)</xsl:text>
          </xsl:if>
        </xsl:when>

        <!-- Print out parent type only -->
        <xsl:otherwise>
          <!-- Print out base type reference -->
          <xsl:choose>
             <xsl:when test="normalize-space($baseTypeRef)='Local type definition'">
                <xsl:value-of select="$baseTypeRef"/>
             </xsl:when>
             <xsl:when test="$baseTypeRef!=''">
                <xsl:call-template name="PrintTypeRef">
                  <xsl:with-param name="ref" select="$baseTypeRef"/>
                </xsl:call-template>
             </xsl:when>
             <xsl:otherwise>
                <!-- IGNORE: Base type may not be exist probably because 
                      current type does not be derived from another type.
                  -->
             </xsl:otherwise>
          </xsl:choose>

          <!-- Print out derivation method -->
          <xsl:if test="$derive != ''">
             <xsl:text> (derivation method: </xsl:text>
             <xsl:value-of select="$derive"/>
             <xsl:text>)</xsl:text>
          </xsl:if>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out the sub types of a given complex type definition.
     Param(s):
            type (Node) required
                Complex type definition
            isCallingType (boolean) optional
                If true, 'type' is the type definition that starts this 
                call. Otherwise, this is a recursive call from 
                'PrintComplexSubtypes'
     -->
  <xsl:template name="PrintComplexSubtypes">
     <xsl:param name="type"/>
     <xsl:param name="isCallingType">true</xsl:param>

     <xsl:variable name="typeName" select="normalize-space($type/@name)"/>

     <!-- Find sub types -->
     <xsl:variable name="subTypes">
        <ul>
        <xsl:for-each select="/xsd:schema/xsd:complexType/xsd:complexContent/xsd:restriction[normalize-space(@base)=$typeName or normalize-space(substring-after(@base, ':'))=$typeName] | /xsd:schema/xsd:complexType/xsd:complexContent/xsd:extension[normalize-space(@base)=$typeName or normalize-space(substring-after(@base, ':'))=$typeName]">
          <li>
             <xsl:variable name="subType" select="../.."/>

             <!-- Write out type name -->
             <xsl:call-template name="PrintTypeRef">
                <xsl:with-param name="name" select="$subType/@name"/>
             </xsl:call-template>

             <!-- Write derivation method -->
             <xsl:text> (by </xsl:text>
             <xsl:value-of select="local-name(.)"/>
             <xsl:text>)</xsl:text>

             <!-- Make recursive call to write sub-types of this sub-type -->
             <xsl:if test="normalize-space(translate($printAllSubTypes, 'TRUE', 'true'))='true'">
                <xsl:call-template name="PrintComplexSubtypes">
                  <xsl:with-param name="type" select="$subType"/>
                  <xsl:with-param name="isCallingType">false</xsl:with-param>
                </xsl:call-template>
             </xsl:if>
          </li>
        </xsl:for-each>
        </ul>
     </xsl:variable>

     <!-- Print out sub types -->
     <xsl:choose>
        <xsl:when test="normalize-space($subTypes)!=''">
          <xsl:copy-of select="$subTypes"/>
        </xsl:when>
        <xsl:when test="$isCallingType='true'">
          <xsl:text>None</xsl:text>
        </xsl:when>
     </xsl:choose>
  </xsl:template>

  <!--
        Prints out the sub types of a given simple type definition.
        Param(s):
            type (Node) required
                Simple type definition
            isCallingType (boolean) optional
                If true, 'type' is the type definition that starts this 
                call. Otherwise, this is a recursive call from 
                'PrintSimpleSubtypes'
     -->
  <xsl:template name="PrintSimpleSubtypes">
     <xsl:param name="type"/>
     <xsl:param name="isCallingType">true</xsl:param>

     <xsl:variable name="typeName" select="normalize-space($type/@name)"/>

     <!-- Find sub-types that are simple type definitions -->
     <xsl:variable name="simpleSubTypes">
        <ul>
        <xsl:for-each select="/xsd:schema/xsd:simpleType/xsd:restriction[normalize-space(@base)=$typeName or normalize-space(substring-after(@base, ':'))=$typeName]">
          <li>
             <xsl:variable name="subType" select=".."/>

             <!-- Write out type name -->
             <xsl:call-template name="PrintTypeRef">
                <xsl:with-param name="name" select="$subType/@name"/>
             </xsl:call-template>

             <!-- Write derivation method -->
             <xsl:text> (by restriction)</xsl:text>

             <!-- Make recursive call to write sub-types of this sub-type -->
             <xsl:if test="normalize-space(translate($printAllSubTypes, 'TRUE', 'true'))='true'">
                <xsl:call-template name="PrintSimpleSubtypes">
                  <xsl:with-param name="type" select="$subType"/>
                  <xsl:with-param name="isCallingType">false</xsl:with-param>
                </xsl:call-template>
             </xsl:if>
          </li>
        </xsl:for-each>
        </ul>
     </xsl:variable>

     <!-- Find sub-types that are complex type definitions -->
     <xsl:variable name="complexSubTypes">
        <ul>
        <xsl:for-each select="/xsd:schema/xsd:complexType/xsd:simpleContent/xsd:restriction[normalize-space(@base)=$typeName or normalize-space(substring-after(@base, ':'))=$typeName] | /xsd:schema/xsd:complexType/xsd:simpleContent/xsd:extension[normalize-space(@base)=$typeName or normalize-space(substring-after(@base, ':'))=$typeName]">
          <li>
             <xsl:variable name="subType" select="../.."/>

             <!-- Write out type name -->
             <xsl:call-template name="PrintTypeRef">
                <xsl:with-param name="name" select="$subType/@name"/>
             </xsl:call-template>

             <!-- Write derivation method -->
             <xsl:text> (by </xsl:text>
             <xsl:value-of select="local-name(.)"/>
             <xsl:text>)</xsl:text>

             <!-- Make recursive call to write sub-types of this sub-type -->
             <xsl:if test="normalize-space(translate($printAllSubTypes, 'TRUE', 'true'))='true'">
                <xsl:call-template name="PrintComplexSubtypes">
                  <xsl:with-param name="type" select="$subType"/>
                  <xsl:with-param name="isCallingType">false</xsl:with-param>
                </xsl:call-template>
             </xsl:if>
          </li>
        </xsl:for-each>
        </ul>
     </xsl:variable>

     <xsl:variable name="hasSimpleSubTypes">
        <xsl:if test="normalize-space($simpleSubTypes)!=''">
          <xsl:text>true</xsl:text>
        </xsl:if>
     </xsl:variable>
     <xsl:variable name="hasComplexSubTypes">
        <xsl:if test="normalize-space($complexSubTypes)!=''">
          <xsl:text>true</xsl:text>
        </xsl:if>
     </xsl:variable>

     <!-- Print out sub types -->
     <xsl:choose>
        <xsl:when test="$hasSimpleSubTypes='true' or $hasComplexSubTypes='true'">
          <xsl:if test="$hasSimpleSubTypes='true'">
             <xsl:copy-of select="$simpleSubTypes"/>
          </xsl:if>
          <xsl:if test="$hasComplexSubTypes='true'">
             <xsl:copy-of select="$complexSubTypes"/>
          </xsl:if>
        </xsl:when>
        <xsl:when test="$isCallingType='true'">
          <xsl:text>None</xsl:text>
        </xsl:when>
     </xsl:choose>
  </xsl:template>


  <!-- **** Properties table **** -->

  <!--
     Prints out the contents of an 'appinfo' or a 'documentation'
     element in the Properties table.
     -->
  <xsl:template match="xsd:appinfo | xsd:documentation" mode="properties">
     <!-- Print out children using XML pretty printer templates -->
     <xsl:apply-templates select="* | text()" mode="xpp"/>

     <!-- Print out source attribute -->
     <xsl:if test="@source">
        <xsl:if test="* | text()"><br/></xsl:if>
        <xsl:text> More information at: </xsl:text>
        <xsl:call-template name="PrintURI">
          <xsl:with-param name="uri" select="@source"/>
        </xsl:call-template>
        <xsl:text>.</xsl:text>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out the Properties table for a top-level
     attribute declaration.
     -->
  <xsl:template match="xsd:attribute" mode="properties">
     <table class="properties">
        <!-- Name -->
        <tr><th>Name</th><td><xsl:value-of select="@name"/></td></tr>
        <!-- Type -->
        <tr>
          <th>Type</th>
          <td>
             <xsl:choose>
                <xsl:when test="xsd:simpleType">
                  <xsl:text>Locally-defined simple type</xsl:text>
                </xsl:when>
                <xsl:when test="@type">
                  <xsl:call-template name="PrintTypeRef">
                     <xsl:with-param name="ref" select="@type"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>anySimpleType</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </td>
        </tr>
        <!-- Default Value -->
        <xsl:if test="@default">
          <tr><th>Default Value</th><td><xsl:value-of select="@default"/></td></tr>
        </xsl:if>
        <!-- Fixed Value -->
        <xsl:if test="@fixed">
          <tr><th>Fixed Value</th><td><xsl:value-of select="@fixed"/></td></tr>
        </xsl:if>
        <!-- Annotation -->
        <xsl:call-template name="PrintAnnotatation">
          <xsl:with-param name="component" select="."/>
        </xsl:call-template>
     </table>
  </xsl:template>

  <!--
     Prints out Properties table for a top-level
     attribute group or model group definition.
     -->
  <xsl:template match="xsd:attributeGroup | xsd:group" mode="properties">
     <table class="properties">
        <!-- Name -->
        <tr><th>Name</th><td><xsl:value-of select="@name"/></td></tr>
        <!-- Annotation -->
        <xsl:call-template name="PrintAnnotatation">
          <xsl:with-param name="component" select="."/>
        </xsl:call-template>
     </table>
  </xsl:template>

  <!--
     Prints out the Properties table for a top-level
     complex type definition.
     -->
  <xsl:template match="xsd:complexType" mode="properties">
     <table class="properties">
        <!-- Name -->
        <tr><th>Name</th><td><xsl:value-of select="@name"/></td></tr>
        <!-- Abstract -->
        <tr>
          <th>
             <xsl:call-template name="PrintGlossaryTermRef">
                <xsl:with-param name="code">Abstract</xsl:with-param>
                <xsl:with-param name="term">Abstract</xsl:with-param>
             </xsl:call-template>
          </th>
          <td>
             <xsl:call-template name="PrintBoolean">
                <xsl:with-param name="boolean" select="@abstract"/>
             </xsl:call-template>
          </td>
        </tr>
        <!-- Final -->
        <xsl:variable name="final">
          <xsl:call-template name="PrintDerivationSet">
             <xsl:with-param name="EBV">
                <xsl:choose>
                  <xsl:when test="@final">
                     <xsl:value-of select="@final"/>
                  </xsl:when>
                  <xsl:otherwise> 
                     <xsl:value-of select="/xsd:schema/@finalDefault"/>
                  </xsl:otherwise>
                </xsl:choose>
             </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <xsl:if test="normalize-space($final)!=''">
          <tr>
             <th>
                <xsl:call-template name="PrintGlossaryTermRef">
                  <xsl:with-param name="code">TypeFinal</xsl:with-param>
                  <xsl:with-param name="term">Prohibited Derivations</xsl:with-param>
                </xsl:call-template>
             </th>
             <td><xsl:value-of select="$final"/></td>
          </tr>
        </xsl:if>
        <!-- Block -->
        <xsl:variable name="block">
          <xsl:call-template name="PrintDerivationSet">
             <xsl:with-param name="EBV">
                <xsl:choose>
                  <xsl:when test="@block">
                     <xsl:value-of select="@block"/>
                  </xsl:when>
                  <xsl:otherwise> 
                     <xsl:value-of select="/xsd:schema/@blockDefault"/>
                  </xsl:otherwise>
                </xsl:choose>
             </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <xsl:if test="normalize-space($block)!=''">
          <tr>
             <th>
                <xsl:call-template name="PrintGlossaryTermRef">
                <xsl:with-param name="code">TypeBlock</xsl:with-param>
                <xsl:with-param name="term">Prohibited Substitutions</xsl:with-param>
                </xsl:call-template>
             </th>
             <td><xsl:value-of select="$block"/></td>
          </tr>
        </xsl:if>
        <!-- Annotation -->
        <xsl:call-template name="PrintAnnotatation">
          <xsl:with-param name="component" select="."/>
        </xsl:call-template>
     </table>
  </xsl:template>

  <!--
     Prints out the Properties table for a top-level
     element declaration.
     -->
  <xsl:template match="xsd:element" mode="properties">
     <table class="properties">
        <!-- Name -->
        <tr><th>Name</th><td><xsl:value-of select="@name"/></td></tr>
        <!-- Type -->
        <tr>
          <th>Type</th>
          <td>
             <xsl:choose>
                <xsl:when test="xsd:simpleType">
                  <xsl:text>Locally-defined simple type</xsl:text>
                </xsl:when>
                <xsl:when test="xsd:complexType">
                  <xsl:text>Locally-defined complex type</xsl:text>
                </xsl:when>
                <xsl:when test="@type">
                  <xsl:call-template name="PrintTypeRef">
                     <xsl:with-param name="ref" select="@type"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>anyType</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </td>
        </tr>
        <!-- Nillable -->
        <tr>
          <th>
             <xsl:call-template name="PrintGlossaryTermRef">
                <xsl:with-param name="code">Nillable</xsl:with-param>
                <xsl:with-param name="term">Nillable</xsl:with-param>
             </xsl:call-template>
          </th>
          <td>
             <xsl:call-template name="PrintBoolean">
                <xsl:with-param name="boolean" select="@nillable"/>
             </xsl:call-template>
          </td>
        </tr>
        <!-- Abstract -->
        <tr>
          <th>
             <xsl:call-template name="PrintGlossaryTermRef">
                <xsl:with-param name="code">Abstract</xsl:with-param>
                <xsl:with-param name="term">Abstract</xsl:with-param>
             </xsl:call-template>
          </th>
          <td>
             <xsl:call-template name="PrintBoolean">
                <xsl:with-param name="boolean" select="@abstract"/>
             </xsl:call-template>
          </td>
        </tr>
        <!-- Default Value -->
        <xsl:if test="@default">
          <tr><th>Default Value</th><td><xsl:value-of select="@default"/></td></tr>
        </xsl:if>
        <!-- Fixed Value -->
        <xsl:if test="@fixed">
          <tr><th>Fixed Value</th><td><xsl:value-of select="@fixed"/></td></tr>
        </xsl:if>
        <!-- Final -->
        <xsl:variable name="final">
          <xsl:call-template name="PrintDerivationSet">
             <xsl:with-param name="EBV">
                <xsl:choose>
                  <xsl:when test="@final">
                     <xsl:value-of select="@final"/>
                  </xsl:when>
                  <xsl:otherwise> 
                     <xsl:value-of select="/xsd:schema/@finalDefault"/>
                  </xsl:otherwise>
                </xsl:choose>
             </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <xsl:if test="normalize-space($final)!=''">
          <tr>
             <th>
                <xsl:call-template name="PrintGlossaryTermRef">
                  <xsl:with-param name="code">ElemFinal</xsl:with-param>
                  <xsl:with-param name="term">Substitution Group Exclusions</xsl:with-param>
                </xsl:call-template>
             </th>
             <td><xsl:value-of select="$final"/></td>
          </tr>
        </xsl:if>
        <!-- Block -->
        <xsl:variable name="block">
          <xsl:call-template name="PrintBlockSet">
             <xsl:with-param name="EBV">
                <xsl:choose>
                  <xsl:when test="@block">
                     <xsl:value-of select="@block"/>
                  </xsl:when>
                  <xsl:otherwise> 
                     <xsl:value-of select="/xsd:schema/@blockDefault"/>
                  </xsl:otherwise>
                </xsl:choose>
             </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <xsl:if test="normalize-space($block)!=''">
          <tr>
             <th>
                <xsl:call-template name="PrintGlossaryTermRef">
                  <xsl:with-param name="code">ElemBlock</xsl:with-param>
                  <xsl:with-param name="term">Disallowed Substitutions</xsl:with-param>
                </xsl:call-template>
             </th>
             <td><xsl:value-of select="$block"/></td>
          </tr>
        </xsl:if>
        <!-- Annotation -->
        <xsl:call-template name="PrintAnnotatation">
          <xsl:with-param name="component" select="."/>
        </xsl:call-template>
     </table>
  </xsl:template>

  <!--
     Prints out the Properties table for a top-level
     notation declaration.
     -->
  <xsl:template match="xsd:notation" mode="properties">
     <table class="properties">
        <!-- Name -->
        <tr><th>Name</th><td><xsl:value-of select="@name"/></td></tr>
        <!-- Public Identifier -->
        <tr>
          <th>Public Identifier</th>
          <td><xsl:value-of select="@public"/></td>
        </tr>
        <!-- System Identifier -->
        <xsl:if test="@system">
          <tr>
             <th>System Identifier</th>
             <td><xsl:value-of select="@system"/></td>
          </tr>
        </xsl:if>
        <!-- Annotation -->
        <xsl:call-template name="PrintAnnotatation">
          <xsl:with-param name="component" select="."/>
        </xsl:call-template>
     </table>
  </xsl:template>

  <!--
     Prints out the Properties table for the root
     schema element.
     -->
  <xsl:template match="xsd:schema" mode="properties">
     <table class="properties">
        <!-- Target Namespace -->
        <tr>
          <th>
             <xsl:call-template name="PrintGlossaryTermRef">
                <xsl:with-param name="code">TargetNS</xsl:with-param>
                <xsl:with-param name="term">Target Namespace</xsl:with-param>
             </xsl:call-template>
          </th>
          <td>
             <xsl:choose>
                <xsl:when test="@targetNamespace">
                  <span class="targetNS"><xsl:value-of select="@targetNamespace"/></span>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:text>None</xsl:text>
                </xsl:otherwise>              
             </xsl:choose>
          </td>
        </tr>
        <!-- Version -->
        <xsl:if test="@version">
          <tr><th>Version</th><td><xsl:value-of select="@version"/></td></tr>
        </xsl:if>
        <!-- Language -->
        <xsl:if test="@xml:lang">
          <tr><th>Language</th><td><xsl:value-of select="@xml:lang"/></td></tr>
        </xsl:if>
        <!-- Element/Attribute Form Defaults -->
        <tr>
          <th>Element and Attribute Namespaces</th>
          <td>
             <ul>
                <li>Global element and attribute declarations belong to this schema's target namespace.</li>
                <li>
                  <xsl:choose>
                     <xsl:when test="normalize-space(@elementFormDefault)='qualified'">
                        <xsl:text>By default, local element declarations belong to this schema's target namespace.</xsl:text>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:text>By default, local element declarations have no namespace.</xsl:text>
                     </xsl:otherwise>
                  </xsl:choose>
                </li>
                <li>
                  <xsl:choose>
                     <xsl:when test="normalize-space(@attributeFormDefault)='qualified'">
                        <xsl:text>By default, local attribute declarations belong to this schema's target namespace.</xsl:text>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:text>By default, local attribute declarations have no namespace.</xsl:text>
                     </xsl:otherwise>
                  </xsl:choose>
                </li>
             </ul>
          </td>
        </tr>
        <!-- Schema Composition, e.g. include, import, redefine -->
        <xsl:if test="xsd:include or xsd:import or xsd:redefine">
          <tr>
          <th>Schema Composition</th>
          <td>
             <ul>
                <!-- Import -->
                <xsl:if test="xsd:import">
                  <li>This schema imports schema(s) from the following namespace(s):
                     <ul>
                     <xsl:for-each select="xsd:import">
                        <li>
                          <em><xsl:value-of select="@namespace"/></em>
                          <xsl:if test="@schemaLocation">
                             <xsl:text> (document may be found at </xsl:text>
                             <xsl:call-template name="PrintURI">
                                <xsl:with-param name="uri" select="@schemaLocation"/>
                             </xsl:call-template>
                             <xsl:text>)</xsl:text>
                          </xsl:if>
                        </li>
                     </xsl:for-each>
                     </ul>
                  </li>
                </xsl:if>
                <!-- Include -->
                <xsl:if test="xsd:include">
                  <li>This schema includes components from the following schema document(s):
                  <ul>
                  <xsl:for-each select="xsd:include">
                     <li>
                        <xsl:call-template name="PrintURI">
                          <xsl:with-param name="uri" select="@schemaLocation"/>
                        </xsl:call-template>
                     </li>
                  </xsl:for-each>
                  </ul>
                  </li>
                </xsl:if>
                <!-- Redefine -->
                <xsl:if test="xsd:redefine">
                  <li>This schema includes components from the following schema document(s), where some of the components have been redefined:
                  <ul>
                     <xsl:for-each select="xsd:redefine">
                        <li>
                          <xsl:call-template name="PrintURI">
                             <xsl:with-param name="uri" select="@schemaLocation"/>
                          </xsl:call-template>
                        </li>
                     </xsl:for-each>
                  </ul>
                  <xsl:text>See </xsl:text><a href="#Redefinitions">Redefined Schema Components</a><xsl:text> section.</xsl:text>
                  </li>
                </xsl:if>
             </ul>
          </td>
          </tr>
        </xsl:if>
        <!-- Annotation -->
        <xsl:call-template name="PrintAnnotatation">
          <xsl:with-param name="component" select="."/>
        </xsl:call-template>
     </table>
  </xsl:template>

  <!--
     Prints out the Properties table for a top-level
     simple type definition.
     -->
  <xsl:template match="xsd:simpleType" mode="properties">
     <table class="properties">
        <!-- Name -->
        <tr><th>Name</th><td><xsl:value-of select="@name"/></td></tr>
        <!-- Constraints -->
        <tr>
          <th>Content</th>
          <td>
             <xsl:call-template name="PrintSimpleConstraints">
                <xsl:with-param name="simpleContent" select="."/>
             </xsl:call-template>
          </td>
        </tr>
        <!-- Final -->
        <xsl:variable name="final">
          <xsl:call-template name="PrintSimpleDerivationSet">
             <xsl:with-param name="EBV">
                <xsl:choose>
                  <xsl:when test="@final">
                     <xsl:value-of select="@final"/>
                  </xsl:when>
                  <xsl:otherwise> 
                     <xsl:value-of select="/xsd:schema/@finalDefault"/>
                  </xsl:otherwise>
                </xsl:choose>
             </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <xsl:if test="normalize-space($final)!=''">
          <tr>
             <th>
                <xsl:call-template name="PrintGlossaryTermRef">
                  <xsl:with-param name="code">TypeFinal</xsl:with-param>
                  <xsl:with-param name="term">Prohibited Derivations</xsl:with-param>
                </xsl:call-template>
             </th>
             <td><xsl:value-of select="$final"/></td>
          </tr>
        </xsl:if>
        <!-- Annotation -->
        <xsl:call-template name="PrintAnnotatation">
          <xsl:with-param name="component" select="."/>
        </xsl:call-template>
     </table>
  </xsl:template>

  <!--
     Unmatched template for 'properties' mode
     -->
  <xsl:template match="*" mode="properties"/>

  <!--
     Prints out the rows to display 'annotation'
     elements of an component in the Properties table.
     This template assumes it will be called within an
     HTML 'table' element.
        Param(s):
            component (Node) required
                Schema component
     -->
  <xsl:template name="PrintAnnotatation">
     <xsl:param name="component"/>

     <xsl:if test="$component/xsd:annotation/xsd:documentation">
        <tr>
          <th>Documentation</th>
          <td>
             <xsl:for-each select="$component/xsd:annotation/xsd:documentation">
                <xsl:if test="position()!=1"><br/><br/></xsl:if>
                <xsl:apply-templates select="." mode="properties"/>
             </xsl:for-each>
          </td>
        </tr>
     </xsl:if>
     <xsl:if test="$component/xsd:annotation/xsd:appinfo">
        <tr>
          <th>Application Data</th>
          <td>
             <xsl:for-each select="$component/xsd:annotation/xsd:appinfo">
                <xsl:if test="position()!=1"><br/><br/></xsl:if>
                <xsl:apply-templates select="." mode="properties"/>
             </xsl:for-each>
          </td>
        </tr>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out the constraints of simple content
     to be displayed within a Properties table.
     Param(s):
            simpleContent (Node) required
                Node containing the simple content
  -->
  <xsl:template name="PrintSimpleConstraints">
     <xsl:param name="simpleContent"/>

     <xsl:choose>
        <!-- Derivation by restriction -->
        <xsl:when test="$simpleContent/xsd:restriction">
          <xsl:call-template name="PrintSimpleRestriction">
             <xsl:with-param name="restriction" select="$simpleContent/xsd:restriction"/>
          </xsl:call-template>
        </xsl:when>
        <!-- Derivation by list -->
        <xsl:when test="$simpleContent/xsd:list">
          <ul>
          <li><xsl:text>List of: </xsl:text>
          <xsl:choose>
             <xsl:when test="$simpleContent/xsd:list/@itemType">
                <xsl:call-template name="PrintTypeRef">
                  <xsl:with-param name="ref" select="$simpleContent/xsd:list/@itemType"/>
                </xsl:call-template>
             </xsl:when>
             <xsl:otherwise>
                <ul>
                  <li>Locally defined type: 
                  <xsl:call-template name="PrintSimpleConstraints">
                     <xsl:with-param name="simpleContent" select="$simpleContent/xsd:list/xsd:simpleType"/>
                  </xsl:call-template>
                  </li>
                </ul>
             </xsl:otherwise>
          </xsl:choose>
          </li>
          </ul>
        </xsl:when>
        <!-- Derivation by union -->
        <xsl:when test="$simpleContent/xsd:union">
          <ul>
             <li><xsl:text>Union of following types: </xsl:text>

             <ul>
             <xsl:if test="$simpleContent/xsd:union/@memberTypes">
                <xsl:call-template name="PrintWhitespaceList">
                  <xsl:with-param name="value" select="$simpleContent/xsd:union/@memberTypes"/>
                  <xsl:with-param name="compType">type</xsl:with-param>
                  <xsl:with-param name="isInList">true</xsl:with-param>
                </xsl:call-template>
             </xsl:if>
             <xsl:for-each select="$simpleContent/xsd:union/xsd:simpleType">
                <li>Locally defined type: 
                <xsl:call-template name="PrintSimpleConstraints">
                  <xsl:with-param name="simpleContent" select="."/>
                </xsl:call-template>
                </li>
             </xsl:for-each>
             </ul>
             </li>
          </ul>
        </xsl:when>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out the constraints of simple content
     derived by restriction, which is to be displayed 
     in a Properties table.
     Param(s):
            restriction (Node) required
                Node containing with the restriction
  -->
  <xsl:template name="PrintSimpleRestriction">
     <xsl:param name="restriction"/>

     <!-- Print out base type info -->
     <xsl:choose>
        <!-- Locally-defined base type -->
        <xsl:when test="$restriction/xsd:simpleType">
          <xsl:call-template name="PrintSimpleConstraints">
             <xsl:with-param name="simpleContent" select="$restriction/xsd:simpleType"/>
          </xsl:call-template>
        </xsl:when>

        <!-- Base type reference -->
        <xsl:when test="$restriction">
          <xsl:variable name="baseTypeRef" select="$restriction/@base"/>
          <xsl:variable name="baseTypeName">
             <xsl:call-template name="GetRefName">
                <xsl:with-param name="ref" select="$baseTypeRef"/>
             </xsl:call-template>
          </xsl:variable>
          <xsl:variable name="baseTypeNS">
             <xsl:call-template name="GetRefNS">
                <xsl:with-param name="ref" select="$baseTypeRef"/>
             </xsl:call-template>
          </xsl:variable>

          <xsl:choose>
             <!-- Base type is built-in XSD type -->
             <xsl:when test="$baseTypeNS=$XSD_NS">
                <ul><li>
                <xsl:text>Built-in XSD Type: </xsl:text>
                <xsl:choose>
                  <xsl:when test="normalize-space(/xsd:schema/@targetNamespace)=$XSD_NS">
                     <xsl:call-template name="PrintTypeRef">
                        <xsl:with-param name="ref" select="$baseTypeRef"/>
                     </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise> 
                     <xsl:value-of select="$baseTypeName"/>
                  </xsl:otherwise>
                </xsl:choose>
                </li></ul>
             </xsl:when>

             <xsl:otherwise> 
                <xsl:variable name="baseType" select="/xsd:schema/xsd:simpleType[@name=$baseTypeName]"/>
                <xsl:choose>
                  <xsl:when test="$baseType">
                     <!-- Print out constraints of base type in schema. -->
                     <xsl:call-template name="PrintSimpleConstraints">
                        <xsl:with-param name="simpleContent" select="$baseType"/>
                     </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                     <ul><li>
                     <strong>
                        <xsl:text>'</xsl:text>
                        <xsl:value-of select="$baseTypeName"/>
                        <xsl:text>' super type was not found in this schema. </xsl:text>
                        <xsl:text>Its facets could not be printed out.</xsl:text>
                     </strong>
                     </li></ul>
                  </xsl:otherwise>
                </xsl:choose>
             </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
     </xsl:choose>

     <!-- Enumeration List -->
     <xsl:variable name="enumeration">
        <xsl:call-template name="PrintEnumFacets">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Regular Expression Pattern -->
     <xsl:variable name="pattern">
        <xsl:call-template name="PrintPatternFacet">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Range -->
     <xsl:variable name="range">
        <xsl:call-template name="PrintRangeFacets">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Total Number of Digits -->
     <xsl:variable name="totalDigits">
        <xsl:call-template name="PrintTotalDigitsFacet">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Number of Fraction Digits -->
     <xsl:variable name="fractionDigits">
        <xsl:call-template name="PrintFractionDigitsFacet">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Length -->
     <xsl:variable name="length">
        <xsl:call-template name="PrintLengthFacets">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Whitespace Policy -->
     <xsl:variable name="whitespace">
        <xsl:call-template name="PrintWhitespaceFacet">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Print out facets -->
     <xsl:if test="$enumeration!='' or $pattern!='' or $range!='' or $totalDigits!='' or $fractionDigits!='' or $length!='' or $whitespace!=''">
        <ul>
        <xsl:if test="$enumeration!=''">
          <li><xsl:copy-of select="$enumeration"/></li>
        </xsl:if>
        <xsl:if test="$pattern!=''">
          <li><xsl:copy-of select="$pattern"/></li>
        </xsl:if>
        <xsl:if test="$range!=''">
          <li><xsl:copy-of select="$range"/></li>
        </xsl:if>
        <xsl:if test="$totalDigits!=''">
          <li><xsl:copy-of select="$totalDigits"/></li>
        </xsl:if>
        <xsl:if test="$fractionDigits!=''">
          <li><xsl:copy-of select="$fractionDigits"/></li>
        </xsl:if>
        <xsl:if test="$length!=''">
          <li><xsl:copy-of select="$length"/></li>
        </xsl:if>
        <xsl:if test="$whitespace!=''">
          <li><xsl:copy-of select="$whitespace"/></li>
        </xsl:if>
        </ul>
     </xsl:if>
  </xsl:template>


  <!-- **** XML Instance Representation table **** -->

  <!-- 
     Prints out the XML Instance Representation table for a top-level 
     schema component. 
     Param(s):
            component (Node) required
              Top-level schema component
     -->
  <xsl:template name="SampleInstanceTable">
     <xsl:param name="component"/>

     <!-- Not applicable for simple type definitions and notation declarations -->
     <xsl:if test="local-name($component)!='simpleType' and local-name($component)!='notation'">
        <xsl:variable name="componentID">
          <xsl:call-template name="GetComponentID">
             <xsl:with-param name="component" select="$component"/>
          </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="CollapseableBox">
          <xsl:with-param name="anchor" select="concat($SAMPLE_PREFIX, $componentID)"/>
          <xsl:with-param name="boxID" select="concat($componentID, '_xibox')"/>
          <xsl:with-param name="tableClass">sample</xsl:with-param>
          <xsl:with-param name="boxTitle">XML Instance Representation</xsl:with-param>
          <xsl:with-param name="boxContents">
             <xsl:apply-templates select="$component" mode="sample"/>
          </xsl:with-param>
          <xsl:with-param name="isOpened">true</xsl:with-param>
        </xsl:call-template>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out a sample XML instance representation of an 'all' 
     model group.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            isInherited (boolean) optional
                If true, display elements using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display elements using 'newFields' CSS class.
     -->
  <xsl:template match="xsd:all" mode="sample">
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     
     <xsl:if test="normalize-space(@maxOccurs)!='0'">
        <!-- Header -->
        <span class="group" style="margin-left: {$margin}em">
          <xsl:text>Start </xsl:text>
          <xsl:call-template name="PrintGlossaryTermRef">
             <xsl:with-param name="code">All</xsl:with-param>
             <xsl:with-param name="term">All</xsl:with-param>
          </xsl:call-template>

          <!-- Min/max occurs information-->
          <xsl:text> </xsl:text>
          <xsl:call-template name="PrintOccurs">
             <xsl:with-param name="component" select="."/>
          </xsl:call-template>

          <!-- Documentation -->
          <xsl:call-template name="PrintSampleDocumentation">
             <xsl:with-param name="component" select="."/>
          </xsl:call-template>
        </span><br/>

        <!-- Content -->
        <xsl:apply-templates select="xsd:*" mode="sample">
          <xsl:with-param name="margin" select="number($margin)+number($ELEM_INDENT)"/>
          <xsl:with-param name="isInherited" select="$isInherited"/>
          <xsl:with-param name="isNewField" select="$isNewField"/>
        </xsl:apply-templates>

        <!-- Footer -->
        <span class="group" style="margin-left: {$margin}em">
          <xsl:text>End All</xsl:text>
        </span><br/>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out a sample XML instance representation of an element 
     content wild card.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:any | xsd:anyAttribute" mode="sample">
     <xsl:param name="margin">0</xsl:param>

     <div class="other" style="margin-left: {$margin}em">
        <xsl:call-template name="PrintWildcard">
          <xsl:with-param name="wildcard" select="."/>
        </xsl:call-template>
     </div>
  </xsl:template>

  <!--
     Prints out a sample XML instance representation 
     of an attribute declaration.
     Param(s):
            subTypeAttrs (String) optional
                List of attributes in sub-types of the type that contains
                this attribute
            isInherited (boolean) optional
                If true, display attribute using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display attribute using 'newFields' CSS class.
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            addBR (boolean) optional
                If true, add <br/> before attribute.
     -->
  <xsl:template match="xsd:attribute[@name]" mode="sample">
     <xsl:param name="subTypeAttrs"/>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="addBR">false</xsl:param>

     <!-- Get attribute namespace -->
     <xsl:variable name="attrNS">
        <xsl:call-template name="GetAttributeNS">
          <xsl:with-param name="attribute" select="."/>
        </xsl:call-template>
     </xsl:variable>

     <xsl:choose>
        <xsl:when test="contains($subTypeAttrs, concat('*', normalize-space($attrNS), '+', normalize-space(@name), '+'))">
          <!-- IGNORE: Sub type has attribute with same name; 
                 Sub-type's attribute declaration will override this 
                 one. -->
        </xsl:when>
        <xsl:when test="@use and normalize-space(@use)='prohibited'">
          <!-- IGNORE: Attribute is prohibited. -->
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$addBR!='false'"><br/></xsl:if>

          <span style="margin-left: {$margin}em">
             <xsl:choose>
                <xsl:when test="$isNewField!='false'">
                  <xsl:attribute name="class">newFields</xsl:attribute>
                </xsl:when>
                <xsl:when test="$isInherited!='false'">
                  <xsl:attribute name="class">inherited</xsl:attribute>
                </xsl:when>
             </xsl:choose>

             <xsl:text> </xsl:text>
             <xsl:call-template name="PrintNSPrefix">
                <xsl:with-param name="prefix">
                   <xsl:call-template name="GetAttributePrefix">
                      <xsl:with-param name="attribute" select="."/>
                   </xsl:call-template>
                </xsl:with-param>
             </xsl:call-template>
             <xsl:value-of select="@name"/>
             <xsl:text>="</xsl:text>

             <xsl:choose>
                <!-- Fixed value is provided -->
                <xsl:when test="@fixed">
                  <span class="fixed">
                     <xsl:value-of select="@fixed"/>
                  </span>
                </xsl:when>
                <!-- Type reference is provided -->
                <xsl:when test="@type">
                  <xsl:call-template name="PrintTypeRef">
                     <xsl:with-param name="ref" select="@type"/>
                  </xsl:call-template>
                </xsl:when>
                <!-- Local type definition is provided -->
                <xsl:when test="xsd:simpleType">
                  <xsl:apply-templates select="xsd:simpleType" mode="sample"/>
                </xsl:when>
                <xsl:otherwise>
                  <span class="type">anySimpleType</span>
                </xsl:otherwise>
             </xsl:choose>

             <xsl:if test="local-name(..)!='schema'">
                <!-- Occurrence info-->
                <xsl:text> </xsl:text>
                <xsl:call-template name="PrintOccurs">
                  <xsl:with-param name="component" select="."/>
                </xsl:call-template>
                <!-- Documentation -->
                <xsl:call-template name="PrintSampleDocumentation">
                  <xsl:with-param name="component" select="."/>
                </xsl:call-template>
             </xsl:if>

             <xsl:text>"</xsl:text>
          </span>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out a sample XML instance representation 
     of an attribute reference.
     Param(s):
            subTypeAttrs (String) optional
                List of attribute in sub-types of the type that contains 
                this attribute
            isInherited (boolean) optional
                If true, display attributes using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display attributes using 'newFields' CSS class.
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            addBR (boolean) optional
                If true, add <br/> before attribute.
     -->
  <xsl:template match="xsd:attribute[@ref]" mode="sample">
     <xsl:param name="subTypeAttrs"/>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="addBR">false</xsl:param>

     <!-- Get attribute name -->
     <xsl:variable name="attrName">
        <xsl:call-template name="GetRefName">
          <xsl:with-param name="ref" select="@ref"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Get attribute namespace -->
     <xsl:variable name="attrNS">
        <xsl:call-template name="GetAttributeNS">
          <xsl:with-param name="attribute" select="."/>
        </xsl:call-template>
     </xsl:variable>

     <xsl:choose>
        <xsl:when test="contains($subTypeAttrs, concat('*', normalize-space($attrNS), '+', normalize-space($attrName), '+'))">
          <!-- IGNORE: Sub type has attribute with same name; 
                 Sub-type's attribute declaration will override this 
                 one. -->
        </xsl:when>
        <xsl:when test="@use and normalize-space(@use)='prohibited'">
          <!-- IGNORE: Attribute is prohibited. -->
        </xsl:when>
        <xsl:otherwise> 
          <xsl:if test="$addBR!='false'"><br/></xsl:if>

          <span style="margin-left: {$margin}em">
             <xsl:choose>
                <xsl:when test="$isNewField!='false'">
                  <xsl:attribute name="class">newFields</xsl:attribute>
                </xsl:when>
                <xsl:when test="$isInherited!='false'">
                  <xsl:attribute name="class">inherited</xsl:attribute>
                </xsl:when>
             </xsl:choose>

             <xsl:text> </xsl:text>
             <xsl:call-template name="PrintAttributeRef">
                <xsl:with-param name="ref" select="@ref"/>
                <xsl:with-param name="isSample">true</xsl:with-param>
             </xsl:call-template>
             <xsl:text>="</xsl:text>

             <!-- Fixed value is provided -->
             <xsl:if test="@fixed">
                <span class="fixed"><xsl:value-of select="@fixed"/></span>
                <xsl:text> </xsl:text>
             </xsl:if>

             <!-- Print occurs info-->
             <xsl:call-template name="PrintOccurs">
                <xsl:with-param name="component" select="."/>
             </xsl:call-template>

             <!-- Documentation -->
             <xsl:call-template name="PrintSampleDocumentation">
                <xsl:with-param name="component" select="."/>
             </xsl:call-template>

             <xsl:text>"</xsl:text>
          </span>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out a sample XML instance representation of an attribute 
     group definition.
     -->
  <xsl:template match="xsd:attributeGroup[@name]" mode="sample">
     <xsl:for-each select="xsd:attribute | xsd:attributeGroup | xsd:anyAttribute">
        <xsl:variable name="addBR">
          <xsl:choose>
             <xsl:when test="position()!=1">
                <xsl:text>true</xsl:text>
             </xsl:when>
             <xsl:otherwise>
                <xsl:text>false</xsl:text>
             </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:apply-templates select="." mode="sample">
          <xsl:with-param name="addBR" select="$addBR"/>
        </xsl:apply-templates>
     </xsl:for-each>
  </xsl:template>

  <!--
     Prints out a sample XML instance representation of an attribute 
     group reference.
     Param(s):
            subTypeAttrs (String) optional
                List of attributes in sub-types of the type that contains
                this attribute group
            isInherited (boolean) optional
                If true, display attributes using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display attributes using 'newFields' CSS class.
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:attributeGroup[@ref]" mode="sample">
     <xsl:param name="subTypeAttrs"/>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     <xsl:param name="margin">0</xsl:param>
     
     <!-- Get attribute group name -->
     <xsl:variable name="attrGrpName">
        <xsl:call-template name="GetRefName">
          <xsl:with-param name="ref" select="@ref"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Find attribute group in schema -->
     <xsl:variable name="attrGrp" select="/xsd:schema/xsd:attributeGroup[@name=$attrGrpName]"/>

     <xsl:choose>
        <!-- Found -->
        <xsl:when test="$attrGrp">
          <xsl:apply-templates select="$attrGrp/xsd:attribute | $attrGrp/xsd:attributeGroup" mode="sample">
             <xsl:with-param name="subTypeAttrs" select="$subTypeAttrs"/>
             <xsl:with-param name="isInherited" select="$isInherited"/>
             <xsl:with-param name="isNewField" select="$isNewField"/>
             <xsl:with-param name="margin" select="$margin"/>
             <xsl:with-param name="addBR">true</xsl:with-param>
          </xsl:apply-templates>
        </xsl:when>
        <!-- Not Found -->
        <xsl:otherwise>
          <br/>
          <span style="margin-left: {$margin}em" class="other">
             <xsl:text>Attribute Group (not shown): </xsl:text>
             <xsl:call-template name="PrintAttributeGroupRef">
                <xsl:with-param name="ref" select="@ref"/>
             </xsl:call-template>
          </span>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out a sample XML instance representation of a 'choice' 
     model group.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            isInherited (boolean) optional
                If true, display elements using'inherited' CSS class.
            isNewField (boolean) optional
                If true, display elements using 'newFields' CSS class.
            parentGroups (String) optional
                List of parent model group definitions that contain this 
                model group. Used to prevent infinite loops when 
                displaying model group definitions.
     -->
  <xsl:template match="xsd:choice" mode="sample">
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     <xsl:param name="parentGroups"/>
     
     <xsl:if test="normalize-space(@maxOccurs)!='0'">
        <!-- Header -->
        <span class="group" style="margin-left: {$margin}em">
          <xsl:text>Start </xsl:text>
          <xsl:call-template name="PrintGlossaryTermRef">
             <xsl:with-param name="code">Choice</xsl:with-param>
             <xsl:with-param name="term">Choice</xsl:with-param>
          </xsl:call-template>

          <!-- Min/max occurrence -->
          <xsl:text> </xsl:text>
          <xsl:call-template name="PrintOccurs">
             <xsl:with-param name="component" select="."/>
          </xsl:call-template>

          <!-- Documentation -->
          <xsl:call-template name="PrintSampleDocumentation">
             <xsl:with-param name="component" select="."/>
          </xsl:call-template>
        </span><br/>

        <!-- Content -->
        <xsl:apply-templates select="xsd:*" mode="sample">
          <xsl:with-param name="margin" select="number($margin)+number($ELEM_INDENT)"/>
          <xsl:with-param name="isInherited" select="$isInherited"/>
          <xsl:with-param name="isNewField" select="$isNewField"/>
          <xsl:with-param name="parentGroups" select="$parentGroups"/>
        </xsl:apply-templates>

        <!-- Footer -->
        <span class="group" style="margin-left: {$margin}em">
          <xsl:text>End Choice</xsl:text>
        </span><br/>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out a sample XML instance from a complex type definition.
     -->
  <xsl:template match="xsd:complexType" mode="sample">
     <xsl:call-template name="PrintSampleComplexElement">
        <xsl:with-param name="type" select="."/>
     </xsl:call-template>
  </xsl:template>

  <!--
     Prints out a sample XML instance from an element declaration.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            isInherited (boolean) optional
                If true, display elements using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display elements using 'newFields' CSS class.
     -->
  <xsl:template match="xsd:element[@name]" mode="sample">
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     
     <xsl:choose>
        <!-- Prohibited element declaration -->
        <xsl:when test="normalize-space(@maxOccurs)='0'">
          <!-- IGNORE if max occurs is zero -->
        </xsl:when>
        <!-- Global element declaration -->
        <xsl:when test="local-name(..)='schema'">
          <xsl:choose>
             <!-- With type reference -->
             <xsl:when test="@type">
                <xsl:variable name="elemTypeName">
                  <xsl:call-template name="GetRefName">
                     <xsl:with-param name="ref" select="@type" />
                  </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="ctype" select="/xsd:schema/xsd:complexType[@name=$elemTypeName]"/>

                <xsl:choose>
                  <!-- Complex type found in schema -->
                  <xsl:when test="$ctype">
                     <xsl:call-template name="PrintSampleComplexElement">
                        <xsl:with-param name="element" select="."/>
                        <xsl:with-param name="type" select="$ctype"/>
                     </xsl:call-template>
                  </xsl:when>
                  <!-- Complex type not found in schema -->
                  <xsl:otherwise>
                     <xsl:call-template name="PrintSampleSimpleElement">
                        <xsl:with-param name="element" select="." />
                     </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
             </xsl:when>
             <!-- With local complex type definition -->
             <xsl:when test="xsd:complexType">
                <xsl:call-template name="PrintSampleComplexElement">
                  <xsl:with-param name="element" select="."/>
                  <xsl:with-param name="type" select="xsd:complexType"/>
                </xsl:call-template>
             </xsl:when>
             <xsl:otherwise>
                <xsl:call-template name="PrintSampleSimpleElement">
                  <xsl:with-param name="element" select="." />
                </xsl:call-template>
             </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <!-- Local element declaration -->
        <xsl:otherwise>
          <xsl:choose>
             <!-- With local complex type definition -->
             <xsl:when test="xsd:complexType">
                <xsl:call-template name="PrintSampleComplexElement">
                  <xsl:with-param name="element" select="."/>
                  <xsl:with-param name="type" select="xsd:complexType"/>
                  <xsl:with-param name="margin" select="$margin" />
                  <xsl:with-param name="isInherited" select="$isInherited" />
                  <xsl:with-param name="isNewField" select="$isNewField" />
                </xsl:call-template>
             </xsl:when>
             <xsl:otherwise>
                <xsl:call-template name="PrintSampleSimpleElement">
                  <xsl:with-param name="element" select="." />
                  <xsl:with-param name="margin" select="$margin" />
                  <xsl:with-param name="isInherited" select="$isInherited" />
                  <xsl:with-param name="isNewField" select="$isNewField" />
                </xsl:call-template>
             </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out a sample XML instance from an element
     reference.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            isInherited (boolean) optional
                If true, display elements using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display elements using 'newFields' CSS class.
     -->
  <xsl:template match="xsd:element[@ref]" mode="sample">
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>

     <xsl:if test="normalize-space(@maxOccurs)!='0'">
        <xsl:call-template name="PrintSampleSimpleElement">
          <xsl:with-param name="element" select="."/>
          <xsl:with-param name="margin" select="$margin"/>
          <xsl:with-param name="isInherited" select="$isInherited"/>
          <xsl:with-param name="isNewField" select="$isNewField"/>
        </xsl:call-template>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out a sample XML instance from a group definition.
     -->
  <xsl:template match="xsd:group[@name]" mode="sample">
     <xsl:apply-templates select="xsd:*" mode="sample">
        <xsl:with-param name="parentGroups" select="concat('*', normalize-space(@name), '+')"/>
     </xsl:apply-templates>
  </xsl:template>

  <!--
     Prints out a sample XML instance from a group reference.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            isInherited (boolean) optional
                If true, display elements using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display elements using 'newFields' CSS class.
            parentGroups (String) optional
                List of parent model group definitions that contain this 
                model group. Used to prevent infinite loops when 
                displaying model group definitions.
     -->
  <xsl:template match="xsd:group[@ref]" mode="sample">
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     <xsl:param name="parentGroups"/>

     <!-- Get group definition name -->
     <xsl:variable name="grpName">
        <xsl:call-template name="GetRefName">
          <xsl:with-param name="ref" select="@ref"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Find group definition in schema -->
     <xsl:variable name="grpDef" select="/xsd:schema/xsd:group[@name=$grpName]"/>

     <!-- Get occurrence info -->
     <xsl:variable name="occursInfo">
        <xsl:call-template name="PrintOccurs">
          <xsl:with-param name="component" select="."/>
        </xsl:call-template>
     </xsl:variable>

     <xsl:choose>
        <!-- Group definition not found or recursive group definition --> 
        <xsl:when test="not($grpDef) or contains($parentGroups, concat('*', normalize-space($grpName), '+'))">
          <!-- Don't show contents -->
          <div class="other" style="margin-left: {$margin}em">
             <xsl:text>Group (not shown): </xsl:text>
             <xsl:call-template name="PrintGroupRef">
                <xsl:with-param name="ref" select="@ref"/>
                <xsl:with-param name="isSample">true</xsl:with-param>
             </xsl:call-template>

             <!-- Occurrence info -->
             <xsl:text> </xsl:text>
             <xsl:copy-of select="$occursInfo"/>
             <!-- Documentation -->
             <xsl:call-template name="PrintSampleDocumentation">
                <xsl:with-param name="component" select="." />
             </xsl:call-template>
          </div>
        </xsl:when>
        <!-- Min/max occurs is one. -->
        <xsl:when test="normalize-space($occursInfo)='[1]'">
          <!-- Don't print out header and footer. -->
          <xsl:apply-templates select="$grpDef/xsd:*" mode="sample">
             <xsl:with-param name="margin" select="$margin"/>
             <xsl:with-param name="isInherited" select="$isInherited"/>
             <xsl:with-param name="isNewField" select="$isNewField"/>
             <xsl:with-param name="parentGroups" select="concat($parentGroups, concat('*', normalize-space($grpName), '+'))"/>
          </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
          <!-- Header -->
          <span class="group" style="margin-left: {$margin}em">
             <xsl:text>Start Group: </xsl:text>
             <xsl:call-template name="PrintGroupRef">
                <xsl:with-param name="name" select="$grpName"/>
                <xsl:with-param name="isSample">true</xsl:with-param>
             </xsl:call-template>

             <!-- Occurrence info -->
             <xsl:text> </xsl:text>
             <xsl:copy-of select="$occursInfo"/>
             <!-- Documentation -->
             <xsl:call-template name="PrintSampleDocumentation">
                <xsl:with-param name="component" select="." />
             </xsl:call-template>
          </span><br/>

          <!-- Content -->
          <xsl:apply-templates select="$grpDef/xsd:*" mode="sample">
             <xsl:with-param name="margin" select="number($margin)+number($ELEM_INDENT)"/>
             <xsl:with-param name="isInherited" select="$isInherited"/>
             <xsl:with-param name="isNewField" select="$isNewField"/>
             <xsl:with-param name="parentGroups" select="concat($parentGroups, concat('*', normalize-space($grpName), '+'))"/>
          </xsl:apply-templates>

          <!-- Footer -->
          <span class="group" style="margin-left: {$margin}em">
             <xsl:text>End Group: </xsl:text>
             <xsl:value-of select="$grpName"/>
          </span><br/>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out a sample XML instance from a 'sequence' model group.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            isInherited (boolean) optional
                If true, display elements using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display elements using 'newFields' CSS class.
            parentGroups (String) optional
                List of parent model group definitions that contain 
                this model group. Used to prevent infinite loops when 
                displaying model group definitions.
     -->
  <xsl:template match="xsd:sequence" mode="sample">
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     <xsl:param name="parentGroups"/>
     
     <xsl:if test="normalize-space(@maxOccurs)!='0'">
        <!-- Get occurrence info -->
        <xsl:variable name="occursInfo">
          <xsl:call-template name="PrintOccurs">
             <xsl:with-param name="component" select="."/>
          </xsl:call-template>
        </xsl:variable>
     
        <xsl:choose>
          <!-- Min/max occurs is one. -->
          <xsl:when test="normalize-space($occursInfo)='[1]'">
             <!-- Don't print out header and footer. -->
             <xsl:apply-templates select="xsd:*" mode="sample">
                <xsl:with-param name="margin" select="$margin"/>
                <xsl:with-param name="isInherited" select="$isInherited"/>
                <xsl:with-param name="isNewField" select="$isNewField"/>
                <xsl:with-param name="parentGroups" select="$parentGroups"/>
             </xsl:apply-templates>
          </xsl:when>
          <xsl:otherwise>
             <!-- Header -->
             <span class="group" style="margin-left: {$margin}em">
                <xsl:text>Start </xsl:text>
                <xsl:call-template name="PrintGlossaryTermRef">
                  <xsl:with-param name="code">Sequence</xsl:with-param>
                  <xsl:with-param name="term">Sequence</xsl:with-param>
                </xsl:call-template>

                <xsl:text> </xsl:text>
                <xsl:copy-of select="$occursInfo"/>
                <!-- Documentation -->
                <xsl:call-template name="PrintSampleDocumentation">
                  <xsl:with-param name="component" select="."/>
                </xsl:call-template>
             </span><br/>

             <!-- Content -->
             <xsl:apply-templates select="xsd:*" mode="sample">
                <xsl:with-param name="margin" select="number($margin)+number($ELEM_INDENT)"/>
                <xsl:with-param name="isInherited" select="$isInherited"/>
                <xsl:with-param name="isNewField" select="$isNewField"/>
                <xsl:with-param name="parentGroups" select="$parentGroups"/>
             </xsl:apply-templates>

             <!-- Footer -->
             <span class="group" style="margin-left: {$margin}em">
                <xsl:text>End Sequence</xsl:text>
             </span><br/>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out the constraints of a complex type with simple content 
     to be displayed within a sample XML instance.
     -->
  <xsl:template match="xsd:simpleContent[xsd:restriction]" mode="sample">
     <span class="constraint">
        <xsl:call-template name="PrintSampleSimpleRestriction">
          <xsl:with-param name="restriction" select="./xsd:restriction"/>
        </xsl:call-template>
     </span>
  </xsl:template>

  <!--
     Prints out the constraints of a simple type definition to be 
     displayed within a sample XML instance.
     -->
  <xsl:template match="xsd:simpleType" mode="sample">
     <span class="constraint">
        <xsl:call-template name="PrintSampleSimpleConstraints">
          <xsl:with-param name="simpleContent" select="."/>
        </xsl:call-template>
     </span>
  </xsl:template>

  <!--
     Prints out the identity constraints of an element to be displayed 
     within a sample XML instance.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:unique | xsd:key | xsd:keyref" mode="sample">
     <xsl:param name="margin">0</xsl:param>

     <div class="other" style="margin-left: {$margin}em">
        <xsl:text>&lt;!-- </xsl:text><br/>

        <xsl:choose>
          <xsl:when test="local-name(.)='unique'">
             <xsl:call-template name="PrintGlossaryTermRef">
                <xsl:with-param name="code">Unique</xsl:with-param>
                <xsl:with-param name="term">Uniqueness</xsl:with-param>
             </xsl:call-template>
          </xsl:when>
          <xsl:when test="local-name(.)='key'">
             <xsl:call-template name="PrintGlossaryTermRef">
                <xsl:with-param name="code">Key</xsl:with-param>
                <xsl:with-param name="term">Key</xsl:with-param>
             </xsl:call-template>
          </xsl:when>
          <xsl:otherwise> 
             <xsl:call-template name="PrintGlossaryTermRef">
                <xsl:with-param name="code">KeyRef</xsl:with-param>
                <xsl:with-param name="term">Key Reference</xsl:with-param>
             </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text> Constraint - </xsl:text>
        <strong>
          <xsl:choose>
             <xsl:when test="local-name(.)='keyref'">
                <xsl:value-of select="@name"/>
             </xsl:when>
             <xsl:otherwise>
                <xsl:variable name="componentID">
                  <xsl:call-template name="GetComponentID">
                     <xsl:with-param name="component" select="."/>
                  </xsl:call-template>
                </xsl:variable>
                <a name="{concat($SAMPLE_PREFIX, $componentID)}">
                  <xsl:value-of select="@name"/>
                </a>
             </xsl:otherwise>
          </xsl:choose>
        </strong>
        <br/>
        
        <xsl:text>Selector - </xsl:text>
        <strong><xsl:value-of select="xsd:selector/@xpath"/></strong>
        <br/>
        
        <xsl:text>Field(s) - </xsl:text>
        <xsl:for-each select="xsd:field">
          <xsl:if test="position()!=1">
             <xsl:text>, </xsl:text>
          </xsl:if>
          <strong><xsl:value-of select="@xpath"/></strong>
        </xsl:for-each>
        <br/>

        <xsl:if test="local-name(.)='keyref'">
          <xsl:text>Refers to - </xsl:text>
          <xsl:call-template name="PrintKeyRef">
             <xsl:with-param name="ref" select="@refer"/>
             <xsl:with-param name="isSample">true</xsl:with-param>
          </xsl:call-template>
          <br/>
        </xsl:if>

        <xsl:text>--></xsl:text>
     </div>
  </xsl:template>

  <!--
     Unmatched template for 'sample' mode
     -->
  <xsl:template match="*" mode="sample"/>

  <!--
     Prints out a link which will open up a window, displaying a 
     schema component's documentation.
     Param(s):
            component (Node) required
                Schema component
  -->
  <xsl:template name="PrintSampleDocumentation">
     <xsl:param name="component"/>

     <xsl:if test="normalize-space(translate($useJavaScript,'TRUE','true'))='true' and $component and $component/xsd:annotation/xsd:documentation">
        <xsl:variable name="documentation">
          <xsl:for-each select="$component/xsd:annotation/xsd:documentation">
             <!-- Check for two dashes, which will break the JavaScript 
                    code -->
             <xsl:if test="contains(., '--')">
                <xsl:message terminate="yes">
                  <xsl:text>A local schema component contains two dashes</xsl:text>
                  <xsl:text> in 'documentation' elements within its </xsl:text>
                  <xsl:text>'annotation' element.</xsl:text>
                </xsl:message>
             </xsl:if>

             <xsl:if test="position()!=1">
                <xsl:text>,</xsl:text>
             </xsl:if>
             <xsl:text>'</xsl:text>
             <xsl:call-template name="EscapeQuotes">
                <xsl:with-param name="value" select="normalize-space(.)"/>
             </xsl:call-template>
             <xsl:text>'</xsl:text>
          </xsl:for-each>
        </xsl:variable>

        <xsl:text> </xsl:text>
        <a href="javascript:void(0)" title="View Documentation" class="documentation">
          <!-- onclick attribute -->
          <xsl:attribute name="onclick">
             <xsl:text>docArray = new Array(</xsl:text>
             <xsl:value-of select="$documentation"/>
             <xsl:text>); viewDocumentation('</xsl:text>
             <xsl:call-template name="GetComponentDescription">
                <xsl:with-param name="component" select="$component"/>
             </xsl:call-template>
             <xsl:text>', '</xsl:text>
             <xsl:choose>
                <xsl:when test="$component/@name">
                  <xsl:value-of select="$component/@name"/>
                </xsl:when>
                <xsl:when test="$component/@ref">
                  <xsl:call-template name="GetRefName">
                     <xsl:with-param name="ref" select="$component/@ref"/>
                  </xsl:call-template>
                </xsl:when>
             </xsl:choose>
             <xsl:text>', docArray);</xsl:text>
          </xsl:attribute>
          <xsl:text>?</xsl:text>
        </a>
     </xsl:if>
  </xsl:template>

  <!--
     Translates occurrences of single and double quotes
     in a piece of text with single and double quote
     escape characters.
     Param(s):
            value (String) required
                Text to translate
  -->
  <xsl:template name="EscapeQuotes">
     <xsl:param name="value"/>

     <xsl:variable name="noSingleQuotes">
        <xsl:call-template name="TranslateStr">
          <xsl:with-param name="value" select="$value"/>
          <xsl:with-param name="strToReplace">'</xsl:with-param>
          <xsl:with-param name="replacementStr">\'</xsl:with-param>
        </xsl:call-template>
     </xsl:variable>
     <xsl:variable name="noDoubleQuotes">
        <xsl:call-template name="TranslateStr">
          <xsl:with-param name="value" select="$noSingleQuotes"/>
          <xsl:with-param name="strToReplace">"</xsl:with-param>
          <xsl:with-param name="replacementStr">\"</xsl:with-param>
        </xsl:call-template>
     </xsl:variable>
     <xsl:value-of select="$noDoubleQuotes"/>
  </xsl:template>

  <!--
     Prints out a sample element instance in one line.
     Param(s):
            element (Node) required
                Element declaration or reference
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            isInherited (boolean) optional
                If true, display element using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display element using 'newFields' CSS class.
  -->
  <xsl:template name="PrintSampleSimpleElement">
     <xsl:param name="element"/>
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>

     <!-- Element Tag -->
     <xsl:variable name="elemTag">
       <!-- Local Name -->
       <xsl:choose>
          <!-- Element reference -->
          <xsl:when test="$element/@ref">
             <!-- Note: Prefix will be automatically written out
                  in call to 'PrintElementRef'. -->
             <xsl:call-template name="PrintElementRef">
                <xsl:with-param name="ref" select="@ref" /> 
                <xsl:with-param name="isSample">true</xsl:with-param>
             </xsl:call-template>
          </xsl:when>
          <!-- Element declaration -->
          <xsl:otherwise> 
             <!-- Prefix -->
             <xsl:call-template name="PrintNSPrefix">
                <xsl:with-param name="prefix">
                   <xsl:call-template name="GetElementPrefix">
                      <xsl:with-param name="element" select="$element"/>
                   </xsl:call-template>
                </xsl:with-param>
             </xsl:call-template>
             <xsl:value-of select="$element/@name"/>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:variable>

     <div style="margin-left: {$margin}em">
        <xsl:choose>
          <xsl:when test="$isNewField!='false'">
             <xsl:attribute name="class">newFields</xsl:attribute>
          </xsl:when>
          <xsl:when test="$isInherited!='false'">
             <xsl:attribute name="class">inherited</xsl:attribute>
          </xsl:when>
        </xsl:choose>

        <!-- Start Tag -->
        <xsl:text>&lt;</xsl:text>
        <xsl:copy-of select="$elemTag"/>
        <xsl:text>></xsl:text>

        <!-- Contents -->
        <xsl:text> </xsl:text>
        <xsl:choose>
          <!-- Fixed value is provided -->
          <xsl:when test="$element/@fixed">
             <span class="fixed"><xsl:value-of select="$element/@fixed"/></span>
          </xsl:when>
          <!-- Type reference is provided -->
          <xsl:when test="$element/@name and $element/@type">
             <!-- Find out if simple type in schema -->
             <xsl:variable name="elemTypeName">
                <xsl:call-template name="GetRefName">
                  <xsl:with-param name="ref" select="$element/@type"/>
                </xsl:call-template>
             </xsl:variable>
             <xsl:variable name="stype" select="/xsd:schema/xsd:simpleType[@name=$elemTypeName]"/>

             <xsl:choose>
                <xsl:when test="$stype">
                  <xsl:apply-templates select="$stype" mode="sample"/>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:call-template name="PrintTypeRef">
                     <xsl:with-param name="ref" select="$element/@type"/>
                     <xsl:with-param name="isSample">true</xsl:with-param>
                  </xsl:call-template>
                </xsl:otherwise>
             </xsl:choose>
          </xsl:when>
          <!-- Local simple type definition is provided -->
          <xsl:when test="$element/@name and $element/xsd:simpleType">
             <xsl:apply-templates select="$element/xsd:simpleType" mode="sample"/>
          </xsl:when>
          <xsl:otherwise> 
             <xsl:text>...</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text> </xsl:text>

        <!-- Identity Constraints -->
        <xsl:if test="$element/xsd:unique or $element/xsd:key or $element/xsd:keyref">
          <xsl:apply-templates select="$element/xsd:unique | $element/xsd:key | $element/xsd:keyref" mode="sample">
             <xsl:with-param name="margin" select="$ELEM_INDENT"/>
          </xsl:apply-templates>
        </xsl:if>

        <!-- End Tag -->
        <xsl:text>&lt;/</xsl:text>
        <xsl:copy-of select="$elemTag"/>
        <xsl:text>></xsl:text>

        <xsl:if test="local-name($element/..)!='schema'">
          <!-- Min/max occurs information -->
          <xsl:text> </xsl:text>
          <xsl:call-template name="PrintOccurs">
             <xsl:with-param name="component" select="$element"/>
          </xsl:call-template>

          <!-- Documentation -->
          <xsl:call-template name="PrintSampleDocumentation">
             <xsl:with-param name="component" select="$element"/>
          </xsl:call-template>
        </xsl:if>
     </div>
  </xsl:template>

  <!--
     Prints out a sample element instance that has complex content.
     Param(s):
            type (Node) required
                Complex type definition
            element (Node) optional
                Element declaration
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            isInherited (boolean) optional
                If true, display element using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display element using 'newFields' CSS class.
     -->
  <xsl:template name="PrintSampleComplexElement">
     <xsl:param name="type"/>
     <xsl:param name="element"/>
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>

     <xsl:variable name="tag">
        <xsl:choose>
          <xsl:when test="$element">
             <!-- Prefix -->
             <xsl:call-template name="PrintNSPrefix">
                <xsl:with-param name="prefix">
                   <xsl:call-template name="GetElementPrefix">
                      <xsl:with-param name="element" select="$element"/>
                   </xsl:call-template>
                </xsl:with-param>
             </xsl:call-template>
             <xsl:value-of select="$element/@name"/>
          </xsl:when>
          <xsl:otherwise> 
             <xsl:text>...</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:variable>

     <xsl:variable name="fromTopCType">
        <xsl:choose>
          <xsl:when test="not($element) and local-name($type/..)='schema'">
             <xsl:text>true</xsl:text>
          </xsl:when>
          <xsl:otherwise> 
             <xsl:text>false</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:variable>

     <!-- Start Tag -->
     <div style="margin-left: {$margin}em">
        <xsl:choose>
          <xsl:when test="$isNewField!='false'">
             <xsl:attribute name="class">newFields</xsl:attribute>
          </xsl:when>
          <xsl:when test="$isInherited!='false'">
             <xsl:attribute name="class">inherited</xsl:attribute>
          </xsl:when>
        </xsl:choose>

        <xsl:text>&lt;</xsl:text>
        <xsl:copy-of select="$tag"/>

        <!-- Print attributes -->
        <xsl:call-template name="PrintSampleTypeAttrs">
          <xsl:with-param name="type" select="$type"/>
          <xsl:with-param name="isInherited" select="$isInherited"/>
          <xsl:with-param name="isNewField" select="$isNewField"/>
          <xsl:with-param name="margin" select="$ATTR_INDENT"/>
          <xsl:with-param name="fromTopCType" select="$fromTopCType"/>
        </xsl:call-template>

        <!-- Get content -->
        <xsl:variable name="content">
          <xsl:call-template name="PrintSampleTypeContent">
             <xsl:with-param name="type" select="$type"/>
             <xsl:with-param name="margin" select="$ELEM_INDENT"/>
             <xsl:with-param name="isInherited" select="$isInherited"/>
             <xsl:with-param name="isNewField" select="$isNewField"/>
             <xsl:with-param name="fromTopCType" select="$fromTopCType"/>
          </xsl:call-template>
        </xsl:variable>

        <!-- Find out if content type is mixed -->
        <xsl:variable name="mixed">
          <xsl:choose>
             <xsl:when test="normalize-space(translate($type/xsd:complexContent/@mixed, 'TRUE', 'true'))='true' or normalize-space($type/xsd:complexContent/@mixed)='1'">
                <xsl:text>true</xsl:text>
             </xsl:when>
             <xsl:when test="normalize-space(translate($type/@mixed, 'TRUE', 'true'))='true' or normalize-space($type/@mixed)='1'">
                <xsl:text>true</xsl:text>
             </xsl:when>
             <xsl:otherwise>
                <xsl:text>false</xsl:text>
             </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <!-- Find out if there are identity constraints -->
        <xsl:variable name="hasIdConstraints">
          <xsl:if test="$element and ($element/xsd:unique or $element/xsd:key or $element/xsd:keyref)">
             <xsl:text>true</xsl:text>
          </xsl:if>
        </xsl:variable>

        <!-- Print content -->
        <xsl:choose>
          <!-- Empty content -->
          <xsl:when test="$hasIdConstraints!='true' and normalize-space($content)=''">
             <!-- Close start tag -->
             <xsl:text>/> </xsl:text>

             <xsl:if test="$element and local-name($element/..)!='schema'">
                <!-- Occurrence info -->
                <xsl:text> </xsl:text>
                <xsl:call-template name="PrintOccurs">
                     <xsl:with-param name="component" select="$element"/>
                </xsl:call-template>

                <!-- Documentation -->
                <xsl:call-template name="PrintSampleDocumentation">
                     <xsl:with-param name="component" select="$element"/>
                </xsl:call-template>
             </xsl:if>
          </xsl:when>
          <xsl:otherwise> 
             <!-- Close start tag -->
             <xsl:text>> </xsl:text>

             <xsl:if test="$element and local-name($element/..)!='schema'">
                <!-- Occurrence info -->
                <xsl:text> </xsl:text>
                <xsl:call-template name="PrintOccurs">
                     <xsl:with-param name="component" select="$element"/>
                </xsl:call-template>

                <!-- Documentation -->
                <xsl:text> </xsl:text>
                <xsl:call-template name="PrintSampleDocumentation">
                     <xsl:with-param name="component" select="$element"/>
                </xsl:call-template>
             </xsl:if>

             <!-- Identity Constraints -->
             <xsl:if test="$element">
                <xsl:apply-templates select="$element/xsd:unique | $element/xsd:key | $element/xsd:keyref" mode="sample">
                  <xsl:with-param name="margin" select="$ELEM_INDENT"/>
                </xsl:apply-templates>
             </xsl:if>
             
             <!-- Print out message if has mixed content -->
             <xsl:if test="$mixed='true'">
                <div class="other" style="margin-left: {$ELEM_INDENT}em">
                     <xsl:text>&lt;-- Mixed content --></xsl:text>
                </div>
             </xsl:if>

             <!-- Element Content -->
             <xsl:copy-of select="$content"/>

             <!-- End Tag -->
             <xsl:text>&lt;/</xsl:text>
             <xsl:copy-of select="$tag"/>
             <xsl:text>></xsl:text>
          </xsl:otherwise>
        </xsl:choose>
     </div>
  </xsl:template>

  <!--
     Prints out attributes of a complex type definition, including 
     those inherited from a base type.
     Param(s):
            type (Node) required
                Complex type definition
            subTypeAttrs (String) optional
                List of attributes in sub-types of this current type 
                definition
            isInherited (boolean) optional
                If true, display attributes using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display attributes using 'newFields' CSS class.
            fromTopCType (boolean) optional
                Set to true if this is being displayed in the XML 
                Instance Representation table of a top-level complex 
                type definition, in which case, 'inherited' attributes 
                and elements are distinguished.
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
  -->
  <xsl:template name="PrintSampleTypeAttrs">
     <xsl:param name="type"/>
     <xsl:param name="subTypeAttrs"/>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     <xsl:param name="fromTopCType">false</xsl:param>
     <xsl:param name="margin">0</xsl:param>

     <xsl:choose>
        <!-- Derivation -->
        <xsl:when test="$type/xsd:complexContent or $type/xsd:simpleContent">
          <xsl:choose>
             <xsl:when test="$type/xsd:complexContent/xsd:restriction">
                <xsl:call-template name="PrintSampleDerivedTypeAttrs">
                  <xsl:with-param name="derivationElem" select="$type/xsd:complexContent/xsd:restriction"/>
                  <xsl:with-param name="subTypeAttrs" select="$subTypeAttrs"/>
                  <xsl:with-param name="isInherited" select="$isInherited"/>
                  <xsl:with-param name="isNewField" select="$isNewField"/>
                  <xsl:with-param name="fromTopCType" select="$fromTopCType"/>
                  <xsl:with-param name="margin" select="$margin"/>
                </xsl:call-template>
             </xsl:when>
             <xsl:when test="$type/xsd:simpleContent/xsd:restriction">
                <xsl:call-template name="PrintSampleDerivedTypeAttrs">
                  <xsl:with-param name="derivationElem" select="$type/xsd:simpleContent/xsd:restriction"/>
                  <xsl:with-param name="subTypeAttrs" select="$subTypeAttrs"/>
                  <xsl:with-param name="isInherited" select="$isInherited"/>
                  <xsl:with-param name="isNewField" select="$isNewField"/>
                  <xsl:with-param name="fromTopCType" select="$fromTopCType"/>
                  <xsl:with-param name="margin" select="$margin"/>
                </xsl:call-template>
             </xsl:when>
             <xsl:when test="$type/xsd:complexContent/xsd:extension">
                <xsl:call-template name="PrintSampleDerivedTypeAttrs">
                  <xsl:with-param name="derivationElem" select="$type/xsd:complexContent/xsd:extension"/>
                  <xsl:with-param name="subTypeAttrs" select="$subTypeAttrs"/>
                  <xsl:with-param name="isInherited" select="$isInherited"/>
                  <xsl:with-param name="isNewField" select="$isNewField"/>
                  <xsl:with-param name="fromTopCType" select="$fromTopCType"/>
                  <xsl:with-param name="margin" select="$margin"/>
                </xsl:call-template>
             </xsl:when>
             <xsl:when test="$type/xsd:simpleContent/xsd:extension">
                <xsl:call-template name="PrintSampleDerivedTypeAttrs">
                  <xsl:with-param name="derivationElem" select="$type/xsd:simpleContent/xsd:extension"/>
                  <xsl:with-param name="subTypeAttrs" select="$subTypeAttrs"/>
                  <xsl:with-param name="isInherited" select="$isInherited"/>
                  <xsl:with-param name="isNewField" select="$isNewField"/>
                  <xsl:with-param name="fromTopCType" select="$fromTopCType"/>
                  <xsl:with-param name="margin" select="$margin"/>
                </xsl:call-template>
             </xsl:when>
          </xsl:choose>
        </xsl:when>

        <!-- No derivation -->
        <xsl:when test="local-name($type)='complexType'">
          <xsl:call-template name="PrintSampleAttrList">
             <xsl:with-param name="list" select="$type"/>
             <xsl:with-param name="subTypeAttrs" select="$subTypeAttrs"/>
             <xsl:with-param name="isInherited" select="$isInherited"/>
             <xsl:with-param name="isNewField" select="$isNewField"/>
             <xsl:with-param name="margin" select="$margin"/>
          </xsl:call-template>
        </xsl:when>

        <!-- Ignore base types that are simple types -->
     </xsl:choose>
  </xsl:template>

  <!--
     Helper function 'PrintSampleTypeAttrs' template to
     handle case of derived types.
     Param(s):
            derivationElem (Node) required
                'restriction' or 'extension' element
            subTypeAttrs (String) optional
                List of attributes in sub-types of 
                this current type definition
            isInherited (boolean) optional
                If true, display attributes using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display attributes using 'newFields' CSS class.
            fromTopCType (boolean) optional
                Set to true if this is being displayed 
                in the XML Instance Representation table 
                of a top-level complex type definition, in
                which case, 'inherited' attributes and
                elements are distinguished.
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
  -->
  <xsl:template name="PrintSampleDerivedTypeAttrs">
     <xsl:param name="derivationElem"/>
     <xsl:param name="subTypeAttrs"/>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     <xsl:param name="fromTopCType">false</xsl:param>
     <xsl:param name="margin">0</xsl:param>

     <!-- Get attributes from this type to add to 
            'subTypeAttrs' list for recursive call on base type -->
     <xsl:variable name="thisAttrs">
        <xsl:call-template name="GetAttrList">
          <xsl:with-param name="list" select="$derivationElem"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Print out attributes in base type -->
     <xsl:variable name="baseTypeName">
        <xsl:call-template name="GetRefName">
          <xsl:with-param name="ref" select="$derivationElem/@base"/>
        </xsl:call-template>
     </xsl:variable>
     <xsl:call-template name="PrintSampleTypeAttrs">
        <xsl:with-param name="type" select="/xsd:schema/xsd:complexType[@name=$baseTypeName]"/>
        <xsl:with-param name="subTypeAttrs" select="concat($subTypeAttrs, $thisAttrs)"/>
        <xsl:with-param name="isInherited">
          <xsl:choose>
             <xsl:when test="$fromTopCType!='false'">
                <xsl:text>true</xsl:text>
             </xsl:when>
             <xsl:otherwise> 
                <xsl:text>false</xsl:text>
             </xsl:otherwise>
          </xsl:choose>
        </xsl:with-param>
        <xsl:with-param name="isNewField" select="$isNewField"/>
        <xsl:with-param name="fromTopCType" select="$fromTopCType"/>
        <xsl:with-param name="margin" select="$margin"/>
     </xsl:call-template>

     <!-- Print out attributes in this type -->
     <xsl:variable name="derivationMethod" select="local-name($derivationElem)"/>
     <xsl:if test="normalize-space($thisAttrs)!=''">
        <xsl:call-template name="PrintSampleAttrList">
          <xsl:with-param name="list" select="$derivationElem"/>
          <xsl:with-param name="subTypeAttrs" select="$subTypeAttrs"/>
          <xsl:with-param name="isInherited" select="$isInherited"/>
          <xsl:with-param name="isNewField">
             <xsl:choose>
                <xsl:when test="$fromTopCType!='false' and $isInherited='false'">
                  <xsl:text>true</xsl:text>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:text>false</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="margin" select="$margin"/>
        </xsl:call-template>
     </xsl:if>
  </xsl:template>

  <!--
     Returns the names and namespaces of attributes
     in a list of attributes and attribute groups.
     Param(s):
            list (Node) required
                Node containing list of attributes and attribute groups
  -->
  <xsl:template name="GetAttrList">
     <xsl:param name="list"/>
  
     <xsl:if test="$list">
        <xsl:for-each select="$list/xsd:attribute | $list/xsd:attributeGroup | $list/xsd:anyAttribute">
          <xsl:choose>
             <!-- Attribute declaration -->
             <xsl:when test="local-name(.)='attribute' and @name">
                <!-- Get attribute name -->
                <xsl:variable name="attrName" select="@name"/>
                <!-- Get attribute namespace -->
                <xsl:variable name="attrNS">
                  <xsl:call-template name="GetAttributeNS">
                     <xsl:with-param name="attribute" select="."/>
                  </xsl:call-template>
                </xsl:variable>

                <xsl:value-of select="concat('*', normalize-space($attrNS), '+', normalize-space($attrName), '+')"/>
             </xsl:when>
             <!-- Attribute reference -->
             <xsl:when test="local-name(.)='attribute' and @ref">
                <!-- Get attribute name -->
                <xsl:variable name="attrName">
                  <xsl:call-template name="GetRefName">
                     <xsl:with-param name="ref" select="@ref"/>
                  </xsl:call-template>
                </xsl:variable>
                <!-- Get attribute namespace -->
                <xsl:variable name="attrNS">
                  <xsl:call-template name="GetAttributeNS">
                     <xsl:with-param name="attribute" select="."/>
                  </xsl:call-template>
                </xsl:variable>

                <xsl:value-of select="concat('*', normalize-space($attrNS), '+', normalize-space($attrName), '+')"/>
             </xsl:when>
             <!-- Attribute Group reference -->
             <xsl:when test="local-name(.)='attributeGroup' and @ref">
                <xsl:variable name="attrGrpName">
                  <xsl:call-template name="GetRefName">
                     <xsl:with-param name="ref" select="@ref"/>
                  </xsl:call-template>
                </xsl:variable>
                <xsl:call-template name="GetAttrList">
                  <xsl:with-param name="list" select="/xsd:schema/xsd:attributeGroup[@name=$attrGrpName]"/>
                </xsl:call-template>
             </xsl:when>
             <!-- Attribute wildcard -->
             <xsl:when test="local-name(.)='anyAttribute'">
             </xsl:when>
          </xsl:choose>
        </xsl:for-each>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out sample XML instances from a list of attributes and 
     attribute groups.
     Param(s):
            list (Node) required
                Node containing list of attributes and attribute groups
            subTypeAttrs (String) optional
                List of attributes in sub-types of 
                the type definition containing this list
            isInherited (boolean) optional
                If true, display attributes using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display attributes using 'newFields' CSS class.
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
  -->
  <xsl:template name="PrintSampleAttrList">
     <xsl:param name="list"/>
     <xsl:param name="subTypeAttrs"/>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     <xsl:param name="margin">0</xsl:param>

     <xsl:apply-templates select="$list/xsd:attribute | $list/xsd:attributeGroup | $list/xsd:anyAttribute" mode="sample">
        <xsl:with-param name="subTypeAttrs" select="$subTypeAttrs"/>
        <xsl:with-param name="isInherited" select="$isInherited"/>
        <xsl:with-param name="isNewField" select="$isNewField"/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="addBR">true</xsl:with-param>
     </xsl:apply-templates>
  </xsl:template>

  <!--
     Prints out the element content of a complex type definition,
     including those inherited from a base type.
     Param(s):
            type (Node) required
                Complex type definition
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            isInherited (boolean) optional
                If true, display elements using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display elements using 'newFields' CSS class.
            fromTopCType (boolean) optional
                Set to true if this is being displayed in the XML 
                Instance Representation table of a top-level complex 
                type definition, in which case, 'inherited' attributes 
                and elements are distinguished.
            addBR (boolean) optional
                If true, can add <br/> before element content.
                Applicable only if displaying complex content.
  -->
  <xsl:template name="PrintSampleTypeContent">
     <xsl:param name="type"/>
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
     <xsl:param name="fromTopCType">false</xsl:param>
     <xsl:param name="addBR">true</xsl:param>

     <xsl:if test="$addBR='true'"><br/></xsl:if>

     <xsl:choose>
        <!-- Derivation by restriction on complex content -->
        <xsl:when test="$type/xsd:complexContent/xsd:restriction">
          <xsl:variable name="restriction" select="$type/xsd:complexContent/xsd:restriction"/>

          <!-- Test if base type is in schema to print out warning comment-->
          <xsl:variable name="baseTypeName">
             <xsl:call-template name="GetRefName">
                <xsl:with-param name="ref" select="$restriction/@base"/>
             </xsl:call-template>
          </xsl:variable>
          <xsl:if test="$baseTypeName!='anyType'">
             <xsl:variable name="baseType" select="/xsd:schema/xsd:complexType[@name=$baseTypeName]"/>
             <xsl:choose>
                <xsl:when test="$baseType">
                  <!-- IGNORE element content of base type if by restriction,
                         since current content will override restricted
                         base type's content. -->
                </xsl:when>
                <xsl:otherwise>
                  <div class="other" style="margin-left: {$margin}em;">
                     <xsl:text>&lt;!-- '</xsl:text>
                     <xsl:value-of select="$baseTypeName"/>
                     <xsl:text>' super type was not found in this schema. Some attributes may be missing. --></xsl:text>
                  </div>
                </xsl:otherwise>
              </xsl:choose>
          </xsl:if>

          <!-- Print out content from this type -->
          <xsl:if test="$restriction/xsd:*[local-name(.)!='annotation']">
             <xsl:call-template name="PrintSampleParticleList">
                <xsl:with-param name="list" select="$restriction"/>
                <xsl:with-param name="margin" select="$margin"/>
                <xsl:with-param name="isInherited" select="$isInherited"/>
                <xsl:with-param name="isNewField">
                  <xsl:choose>
                     <xsl:when test="$fromTopCType!='false' and $isInherited='false'">
                        <xsl:text>true</xsl:text>
                     </xsl:when>
                     <xsl:otherwise> 
                        <xsl:text>false</xsl:text>
                     </xsl:otherwise>
                  </xsl:choose>
                </xsl:with-param>
             </xsl:call-template>
          </xsl:if>
        </xsl:when>

        <!-- Derivation by extension on complex content -->
        <xsl:when test="$type/xsd:complexContent/xsd:extension">
          <xsl:variable name="extension" select="$type/xsd:complexContent/xsd:extension"/>

          <!-- Print out content from base type -->
          <xsl:variable name="baseTypeName">
             <xsl:call-template name="GetRefName">
                <xsl:with-param name="ref" select="$extension/@base"/>
             </xsl:call-template>
          </xsl:variable>
          <xsl:if test="$baseTypeName!='anyType'">
             <xsl:variable name="baseType" select="/xsd:schema/xsd:complexType[@name=$baseTypeName]"/>
             <xsl:choose>
                <xsl:when test="$baseType">
                  <xsl:call-template name="PrintSampleTypeContent">
                     <xsl:with-param name="type" select="$baseType"/>
                     <xsl:with-param name="margin" select="$margin"/>
                     <xsl:with-param name="isInherited">
                        <xsl:choose>
                          <xsl:when test="$fromTopCType!='false'">
                          <xsl:text>true</xsl:text>
                          </xsl:when>
                          <xsl:otherwise> 
                          <xsl:text>false</xsl:text>
                          </xsl:otherwise>
                        </xsl:choose>
                     </xsl:with-param>
                     <xsl:with-param name="isNewField" select="$isNewField"/>
                     <xsl:with-param name="fromTopCType" select="$fromTopCType"/>
                     <xsl:with-param name="addBR">false</xsl:with-param>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <div class="other" style="margin-left: {$margin}em;">
                     <xsl:text>&lt;!-- '</xsl:text>
                     <xsl:value-of select="$baseTypeName"/>
                     <xsl:text>' super type was not found in this schema. Some elements and attributes may be missing. --></xsl:text>
                  </div>
                </xsl:otherwise>
             </xsl:choose>
          </xsl:if>

          <!-- Print out content from this type -->
          <xsl:if test="$extension/xsd:*[local-name(.)!='annotation']">
             <xsl:call-template name="PrintSampleParticleList">
                <xsl:with-param name="list" select="$extension"/>
                <xsl:with-param name="margin" select="$margin"/>
                <xsl:with-param name="isInherited" select="$isInherited"/>
                <xsl:with-param name="isNewField">
                  <xsl:choose>
                     <xsl:when test="$fromTopCType!='false' and $isInherited='false'">
                        <xsl:text>true</xsl:text>
                     </xsl:when>
                     <xsl:otherwise> 
                        <xsl:text>false</xsl:text>
                     </xsl:otherwise>
                  </xsl:choose>
                </xsl:with-param>
             </xsl:call-template>
          </xsl:if>
        </xsl:when>

        <!-- Derivation by restriction on simple content -->
        <xsl:when test="$type/xsd:simpleContent/xsd:restriction">
          <!-- Print out simple type constraints-->
          <span style="margin-left: {$margin}em">
             <xsl:text> </xsl:text>
             <xsl:apply-templates select="$type/xsd:simpleContent" mode="sample"/>
             <xsl:text> </xsl:text>
          </span>
          <br/>
        </xsl:when>

        <!-- Derivation by extension on simple content -->
        <xsl:when test="$type/xsd:simpleContent/xsd:extension">
          <!-- Print out base type name -->
          <span style="margin-left: {$margin}em">
             <xsl:text> </xsl:text>
             <xsl:call-template name="PrintTypeRef">
                <xsl:with-param name="ref" select="$type/xsd:simpleContent/xsd:extension/@base"/>
                <xsl:with-param name="isSample">true</xsl:with-param>
             </xsl:call-template>
             <xsl:text> </xsl:text>
          </span>
          <br/>
        </xsl:when>

        <!-- No derivation: complex type definition -->
        <xsl:when test="local-name($type)='complexType'">
          <!-- Print out content from this type -->
          <xsl:call-template name="PrintSampleParticleList">
             <xsl:with-param name="list" select="$type"/>
             <xsl:with-param name="margin" select="$margin"/>
             <xsl:with-param name="isInherited" select="$isInherited"/>
             <xsl:with-param name="isNewField" select="$isNewField"/>
          </xsl:call-template>
        </xsl:when>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out sample XML instances from a list of 
     element particle.
     Param(s):
            list (Node) required
                Node containing list of element particles
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            isInherited (boolean) optional
                If true, display elements using 'inherited' CSS class.
            isNewField (boolean) optional
                If true, display elements using 'newFields' CSS class.
  -->
  <xsl:template name="PrintSampleParticleList">
     <xsl:param name="list"/>
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="isInherited">false</xsl:param>
     <xsl:param name="isNewField">false</xsl:param>
  
     <xsl:if test="$list">
        <xsl:apply-templates select="$list/xsd:group | $list/xsd:sequence | $list/xsd:choice | $list/xsd:all | $list/xsd:element" mode="sample">
          <xsl:with-param name="margin" select="$margin"/>
          <xsl:with-param name="isInherited" select="$isInherited"/>
          <xsl:with-param name="isNewField" select="$isNewField"/>
        </xsl:apply-templates>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out the constraints of simple content
     to be displayed within a sample XML instance.
     Param(s):
            simpleContent (Node) required
                Node containing with the simple content
  -->
  <xsl:template name="PrintSampleSimpleConstraints">
     <xsl:param name="simpleContent"/>

     <xsl:choose>
        <!-- Derivation by restriction -->
        <xsl:when test="$simpleContent/xsd:restriction">
          <xsl:call-template name="PrintSampleSimpleRestriction">
             <xsl:with-param name="restriction" select="$simpleContent/xsd:restriction"/>
          </xsl:call-template>
        </xsl:when>

        <!-- Derivation by list -->
        <xsl:when test="$simpleContent/xsd:list">
          <xsl:choose>
             <xsl:when test="$simpleContent/xsd:list/@itemType">
                <xsl:text>list of: </xsl:text>
                <xsl:call-template name="PrintTypeRef">
                  <xsl:with-param name="ref" select="$simpleContent/xsd:list/@itemType"/>
                </xsl:call-template>
             </xsl:when>
             <xsl:otherwise>
                <xsl:text>list of: [</xsl:text>
                <xsl:call-template name="PrintSampleSimpleConstraints">
                  <xsl:with-param name="simpleContent" select="$simpleContent/xsd:list/xsd:simpleType"/>
                </xsl:call-template>
                <xsl:text>]</xsl:text>
             </xsl:otherwise>
          </xsl:choose>
        </xsl:when>

        <!-- Derivation by union -->
        <xsl:when test="$simpleContent/xsd:union">
             <xsl:text>union of: [</xsl:text>

             <xsl:variable name="hasMemberTypes">
                <xsl:if test="normalize-space($simpleContent/xsd:union/@memberTypes)!=''">
                  <xsl:text>true</xsl:text>
                </xsl:if>
             </xsl:variable>
             <xsl:if test="$hasMemberTypes='true'">
                <xsl:call-template name="PrintWhitespaceList">
                  <xsl:with-param name="value" select="$simpleContent/xsd:union/@memberTypes"/>
                  <xsl:with-param name="compType">type</xsl:with-param>
                  <xsl:with-param name="separator">, </xsl:with-param>
                </xsl:call-template>
             </xsl:if>
             <xsl:for-each select="$simpleContent/xsd:union/xsd:simpleType">
                <xsl:if test="position()!=1 or $hasMemberTypes='true'">
                  <xsl:text>, </xsl:text>
                </xsl:if>
                <xsl:text>[</xsl:text>
                <xsl:call-template name="PrintSampleSimpleConstraints">
                  <xsl:with-param name="simpleContent" select="."/>
                </xsl:call-template>
                <xsl:text>]</xsl:text>
             </xsl:for-each>

             <xsl:text>]</xsl:text>
        </xsl:when>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out the constraints of simple content
     derived by restriction, which is to be displayed 
     within a sample XML instance.
     Param(s):
            restriction (Node) required
                Node containing with the restriction
  -->
  <xsl:template name="PrintSampleSimpleRestriction">
     <xsl:param name="restriction"/>

     <!-- Print out base type info -->
     <xsl:choose>
        <!-- Locally defined base type -->
        <xsl:when test="$restriction/xsd:simpleType">
          <xsl:call-template name="PrintSampleSimpleConstraints">
             <xsl:with-param name="simpleContent" select="$restriction/xsd:simpleType"/>
          </xsl:call-template>
        </xsl:when>

        <!-- Base type reference -->
        <xsl:when test="$restriction">
          <xsl:variable name="baseTypeRef" select="$restriction/@base"/>
          <xsl:variable name="baseTypeName">
             <xsl:call-template name="GetRefName">
                <xsl:with-param name="ref" select="$baseTypeRef"/>
             </xsl:call-template>
          </xsl:variable>
          <xsl:variable name="baseTypeNS">
             <xsl:call-template name="GetRefNS">
                <xsl:with-param name="ref" select="$baseTypeRef"/>
             </xsl:call-template>
          </xsl:variable>
          <xsl:choose>
             <!-- Base type is built-in XSD type -->
             <xsl:when test="$baseTypeNS=$XSD_NS">
                <xsl:choose>
                  <xsl:when test="normalize-space(/xsd:schema/@targetNamespace)=$XSD_NS">
                     <xsl:call-template name="PrintTypeRef">
                        <xsl:with-param name="ref" select="$baseTypeRef"/>
                     </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise> 
                     <xsl:value-of select="$baseTypeName"/>
                  </xsl:otherwise>
                </xsl:choose>
             </xsl:when>

             <xsl:otherwise>
                <!-- Write out reference to base type -->
                <xsl:call-template name="PrintTypeRef">
                  <xsl:with-param name="ref" select="$baseTypeRef"/>
                </xsl:call-template>

<!-- Uncomment to recursive print out simple content restrictions,
      rather than just printing out reference to base type
                <xsl:variable name="simpleBaseType" select="/xsd:schema/xsd:simpleType[@name=$baseTypeName]"/>
                <xsl:choose>
                  <xsl:when test="$simpleBaseType">
                     <xsl:call-template name="PrintSampleSimpleConstraints">
                        <xsl:with-param name="simpleContent" select="$simpleBaseType"/>
                     </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:variable name="complexBaseType" select="/xsd:schema/xsd:complexType[xsd:simpleContent/xsd:restriction and @name=$baseTypeName]"/>
                     <xsl:choose>
                        <xsl:when test="$complexBaseType">
                          <xsl:call-template name="PrintSampleSimpleRestriction">
                             <xsl:with-param name="restriction" select="$complexBaseType/xsd:simpleContent/xsd:restriction"/>
                          </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise> 
                          <xsl:call-template name="PrintTypeRef">
                             <xsl:with-param name="ref" select="$baseTypeName"/>
                          </xsl:call-template>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:otherwise>
                </xsl:choose>
-->
             </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
     </xsl:choose>

     <!-- Enumeration List -->
     <xsl:variable name="enumeration">
        <xsl:call-template name="PrintEnumFacets">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Regular Expression Pattern -->
     <xsl:variable name="pattern">
        <xsl:call-template name="PrintPatternFacet">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Range -->
     <xsl:variable name="range">
        <xsl:call-template name="PrintRangeFacets">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Total Number of Digits -->
     <xsl:variable name="totalDigits">
        <xsl:call-template name="PrintTotalDigitsFacet">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Number of Fraction Digits -->
     <xsl:variable name="fractionDigits">
        <xsl:call-template name="PrintFractionDigitsFacet">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Length -->
     <xsl:variable name="length">
        <xsl:call-template name="PrintLengthFacets">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Whitespace Policy -->
     <xsl:variable name="whitespace">
        <xsl:call-template name="PrintWhitespaceFacet">
          <xsl:with-param name="simpleRestrict" select="$restriction"/>
        </xsl:call-template>
     </xsl:variable>

     <!-- Print out facets -->
     <xsl:if test="$enumeration !=''">
         <xsl:text> &lt;&lt;</xsl:text>
         <xsl:copy-of select="$enumeration"/>
         <xsl:text>>></xsl:text>
     </xsl:if>
     <xsl:if test="$pattern !=''">
         <xsl:text> &lt;&lt;</xsl:text>
         <xsl:copy-of select="$pattern"/>
         <xsl:text>>></xsl:text>
     </xsl:if>
     <xsl:if test="$range !=''">
         <xsl:text> &lt;&lt;</xsl:text>
         <xsl:copy-of select="$range"/>
         <xsl:text>>></xsl:text>
     </xsl:if>
     <xsl:if test="$totalDigits !=''">
         <xsl:text> &lt;&lt;</xsl:text>
         <xsl:copy-of select="$totalDigits"/>
         <xsl:text>>></xsl:text>
     </xsl:if>
     <xsl:if test="$fractionDigits !=''">
         <xsl:text> &lt;&lt;</xsl:text>
         <xsl:copy-of select="$fractionDigits"/>
         <xsl:text>>></xsl:text>
     </xsl:if>
     <xsl:if test="$length !=''">
         <xsl:text> &lt;&lt;</xsl:text>
         <xsl:copy-of select="$length"/>
         <xsl:text>>></xsl:text>
     </xsl:if>
     <xsl:if test="$whitespace !=''">
         <xsl:text> &lt;&lt;</xsl:text>
         <xsl:copy-of select="$whitespace"/>
         <xsl:text>>></xsl:text>
     </xsl:if>
  </xsl:template>


  <!-- **** Schema Component Representation table **** -->

  <!-- 
     Prints out the Schema Component Representation table 
     for a top-level schema component. 
     Param(s):
            component (Node) required
              Top-level schema component
     -->
  <xsl:template name="SchemaComponentTable">
     <xsl:param name="component"/>

     <xsl:variable name="componentID">
        <xsl:call-template name="GetComponentID">
          <xsl:with-param name="component" select="$component"/>
        </xsl:call-template>
     </xsl:variable>

     <xsl:call-template name="CollapseableBox">
        <xsl:with-param name="boxID" select="concat($componentID, '_scbox')"/>
        <xsl:with-param name="tableClass">schemaComponent</xsl:with-param>
        <xsl:with-param name="boxTitle">Schema Component Representation</xsl:with-param>
        <xsl:with-param name="boxContents">
          <xsl:apply-templates select="$component" mode="schemaComponent"/>
        </xsl:with-param>
        <xsl:with-param name="isOpened">false</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Prints out schema component representation of 
     declarations.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:attribute[@name] | xsd:element[@name]" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: name -->
          <xsl:call-template name="DisplayAttr">
             <xsl:with-param name="attrName">name</xsl:with-param>
             <xsl:with-param name="attrValue">
                <xsl:value-of select="@name"/>
             </xsl:with-param>
          </xsl:call-template>

          <!-- Attribute: type -->
          <xsl:if test="@type">
             <xsl:call-template name="DisplayAttr">
                <xsl:with-param name="attrName">type</xsl:with-param>
                <xsl:with-param name="attrValue">
                  <xsl:call-template name="PrintTypeRef">
                     <xsl:with-param name="ref" select="@type"/>
                  </xsl:call-template>
                </xsl:with-param>
             </xsl:call-template>
          </xsl:if>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*name+*type+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="excludeFilter">*annotation+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Prints out schema component representation of 
     definitions and key/uniqueness constraints.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:attributeGroup[@name] | xsd:complexType[@name] | xsd:simpleType[@name] | xsd:group[@name] | xsd:key | xsd:unique" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: name -->
          <xsl:call-template name="DisplayAttr">
             <xsl:with-param name="attrName">name</xsl:with-param>
             <xsl:with-param name="attrValue">
                <xsl:value-of select="@name"/>
             </xsl:with-param>
          </xsl:call-template>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*name+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="excludeFilter">*annotation+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Prints out schema component representation of attribute
     references.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:attribute[@ref]" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: ref -->
          <xsl:call-template name="DisplayAttr">
             <xsl:with-param name="attrName">ref</xsl:with-param>
             <xsl:with-param name="attrValue">
                <xsl:call-template name="PrintAttributeRef">
                  <xsl:with-param name="ref" select="@ref"/>
                </xsl:call-template>
             </xsl:with-param>
          </xsl:call-template>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*ref+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="excludeFilter">*annotation+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Prints out schema component representation of attribute group
     references.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:attributeGroup[@ref]" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: ref -->
          <xsl:call-template name="DisplayAttr">
             <xsl:with-param name="attrName">ref</xsl:with-param>
             <xsl:with-param name="attrValue">
                <xsl:call-template name="PrintAttributeGroupRef">
                  <xsl:with-param name="ref" select="@ref"/>
                </xsl:call-template>
             </xsl:with-param>
          </xsl:call-template>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*ref+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="excludeFilter">*annotation+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Prints out schema component representation of element 
     references.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:element[@ref]" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: ref -->
          <xsl:call-template name="DisplayAttr">
             <xsl:with-param name="attrName">ref</xsl:with-param>
             <xsl:with-param name="attrValue">
                <xsl:call-template name="PrintElementRef">
                  <xsl:with-param name="ref" select="@ref"/>
                </xsl:call-template>
             </xsl:with-param>
          </xsl:call-template>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*ref+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="excludeFilter">*annotation+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Prints out schema component representation of model group
     references.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:group[@ref]" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: ref -->
          <xsl:call-template name="DisplayAttr">
             <xsl:with-param name="attrName">ref</xsl:with-param>
             <xsl:with-param name="attrValue">
                <xsl:call-template name="PrintGroupRef">
                  <xsl:with-param name="ref" select="@ref"/>
                </xsl:call-template>
             </xsl:with-param>
          </xsl:call-template>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*ref+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="excludeFilter">*annotation+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Prints out schema component representation of 
     'appinfo' and 'documentation' elements.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:appinfo | xsd:documentation" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: source -->
          <xsl:if test="@source">
             <xsl:call-template name="DisplayAttr">
                <xsl:with-param name="attrName">source</xsl:with-param>
                <xsl:with-param name="attrValue">
                  <xsl:call-template name="PrintURI">
                     <xsl:with-param name="uri" select="@source"/>
                  </xsl:call-template>
                </xsl:with-param>
             </xsl:call-template>
          </xsl:if>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*source+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="hasAnyContent">true</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Prints out schema component representation of 
     key reference constraints.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:keyref" mode="schemaComponent">    
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: name -->
          <xsl:call-template name="DisplayAttr">
             <xsl:with-param name="attrName">name</xsl:with-param>
             <xsl:with-param name="attrValue">
                <xsl:value-of select="@name"/>
             </xsl:with-param>
          </xsl:call-template>

          <!-- Attribute: refers -->
          <xsl:call-template name="DisplayAttr">
             <xsl:with-param name="attrName">refer</xsl:with-param>
             <xsl:with-param name="attrValue">
                <xsl:call-template name="PrintKeyRef">
                  <xsl:with-param name="ref">
                     <xsl:value-of select="@refer"/>
                  </xsl:with-param>
                </xsl:call-template>
             </xsl:with-param>
          </xsl:call-template>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*name+*refer+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="excludeFilter">*annotation+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>
  
  <!--
     Prints out schema component representation of 
     derivations by extension and restrictions.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:extension | xsd:restriction" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: base -->
          <xsl:if test="@base">
             <xsl:call-template name="DisplayAttr">
                <xsl:with-param name="attrName">base</xsl:with-param>
                <xsl:with-param name="attrValue">
                  <xsl:call-template name="PrintTypeRef">
                     <xsl:with-param name="ref" select="@base"/>
                  </xsl:call-template>
                </xsl:with-param>
             </xsl:call-template>
          </xsl:if>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*base+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="excludeFilter">*annotation+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>
  
  <!--
     Prints out schema component representation of 
     derivations by list.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:list" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: itemType-->
          <xsl:if test="@itemType">
             <xsl:call-template name="DisplayAttr">
                <xsl:with-param name="attrName">itemType</xsl:with-param>
                <xsl:with-param name="attrValue">
                  <xsl:call-template name="PrintTypeRef">
                     <xsl:with-param name="ref" select="@itemType"/>
                  </xsl:call-template>
                </xsl:with-param>
             </xsl:call-template>
          </xsl:if>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*itemType+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="excludeFilter">*annotation+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>
  
  <!--
     Prints out schema component representation of 
     derivations by union.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:union" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: memberTypes-->
          <xsl:if test="@memberTypes">
             <xsl:call-template name="DisplayAttr">
                <xsl:with-param name="attrName">memberTypes</xsl:with-param>
                <xsl:with-param name="attrValue">
                  <xsl:call-template name="PrintWhitespaceList">
                     <xsl:with-param name="value" select="@memberTypes"/>
                     <xsl:with-param name="compType">type</xsl:with-param>
                  </xsl:call-template>
                </xsl:with-param>
             </xsl:call-template>
          </xsl:if>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*memberTypes+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="excludeFilter">*annotation+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Prints out schema component representation of 
     the root schema element.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="xsd:schema" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <!-- Attribute: source -->
          <xsl:if test="@xml:lang">
             <xsl:call-template name="DisplayAttr">
                <xsl:with-param name="attrName">xml:lang</xsl:with-param>
                <xsl:with-param name="attrValue">
                  <xsl:value-of select="@xml:lang"/>
                </xsl:with-param>
             </xsl:call-template>
          </xsl:if>

          <!-- Other attributes -->
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
             <xsl:with-param name="attrsNotToDisplay">*lang+</xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="includeFilter">*include+*import+*redefine+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Default way to print out schema component representation.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="*" mode="schemaComponent">
     <xsl:param name="margin">0</xsl:param>

     <xsl:call-template name="DisplaySchemaComponent">
        <xsl:with-param name="component" select="."/>
        <xsl:with-param name="margin" select="$margin"/>
        <xsl:with-param name="attributes">
          <xsl:call-template name="DisplayOtherAttributes">
             <xsl:with-param name="component" select="."/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="excludeFilter">*annotation+</xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <!--
     Displays a schema element in the correct format
     for the Schema Component Representation table, e.g.
     tags are one color, and content are another.
     Param(s):
            component (Node) required
                Schema element to be displayed
            attributes (Result Tree Fragment) optional
                Pre-formatted attributes of schema element
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
            hasAnyContent (boolean) optional
                Set to true if schema element can accept 
                child elements from namespaces other than
                the schema namespace, e.g. 'documentation'
                and 'appinfo'
            includeFilter (String) optional
                List of element names, sandwiched between the
                characters, '*' and '+'. If specified, only the
                child elements of the component with tags in 
                the list will be displayed.
            excludeFilter (String) optional
                List of element names, sandwiched between the
                characters, '*' and '+'. If specified, display
                all child elements of the component, except
                those with tags in the list.
     -->
  <xsl:template name="DisplaySchemaComponent">
     <xsl:param name="component"/>
     <xsl:param name="attributes"/>
     <xsl:param name="margin">0</xsl:param>
     <xsl:param name="hasAnyContent">false</xsl:param>
     <xsl:param name="includeFilter"/>
     <xsl:param name="excludeFilter"/>

     <xsl:variable name="tag">
        <xsl:call-template name="PrintNSPrefix">
           <xsl:with-param name="prefix">
              <xsl:call-template name="GetXSDPrefix"/>
           </xsl:with-param>
           <xsl:with-param name="nolink">true</xsl:with-param>
        </xsl:call-template>
        <xsl:value-of select="local-name($component)"/>
     </xsl:variable>

     <div style="margin-left: {$margin}em">
        <!-- Start Tag -->
        <xsl:text>&lt;</xsl:text>
        <span class="scTag">
           <xsl:copy-of select="$tag"/>
        </span>

        <!-- Attributes -->
        <xsl:copy-of select="$attributes"/>

        <!-- Content -->
        <xsl:variable name="content">
          <xsl:choose>
              <!-- Include filter is on -->
              <xsl:when test="$includeFilter!=''">
                 <xsl:apply-templates select="$component/xsd:*[contains($includeFilter, concat('*', local-name(.), '+'))]" mode="schemaComponent">
                    <xsl:with-param name="margin" select="$ELEM_INDENT"/>
                 </xsl:apply-templates>
                 <div class="scContent" style="margin-left: {$ELEM_INDENT}em">...</div>
              </xsl:when>
              <!-- Exclude filter is on -->
              <xsl:when test="$excludeFilter!=''">
                 <xsl:apply-templates select="$component/xsd:*[not(contains($excludeFilter, concat('*', local-name(.), '+')))]" mode="schemaComponent">
                    <xsl:with-param name="margin" select="$ELEM_INDENT"/>
                 </xsl:apply-templates>
              </xsl:when>
              <!-- Permits any content -->
              <xsl:when test="$hasAnyContent='true'">
                 <div class="scContent" style="margin-left: {$ELEM_INDENT}em">
                    <xsl:apply-templates select="$component/* | $component/text()" mode="xpp"/>
                 </div>
              </xsl:when>
              <!-- Contains schema elements -->
              <xsl:otherwise>
                 <xsl:apply-templates select="$component/xsd:*" mode="schemaComponent">
                    <xsl:with-param name="margin" select="$ELEM_INDENT"/>
                 </xsl:apply-templates>
              </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        
        <xsl:choose>
          <!-- Has content -->
          <xsl:when test="normalize-space($content)!=''">
             <!-- End of start tag -->
             <xsl:text>></xsl:text>

             <xsl:copy-of select="$content"/>

             <!-- End Tag -->
             <xsl:text>&lt;/</xsl:text>
             <span class="scTag">
                <xsl:copy-of select="$tag"/>
             </span>
             <xsl:text>></xsl:text>
          </xsl:when>
          <!-- Empty content -->
          <xsl:otherwise> 
             <!-- End of start tag -->
             <xsl:text>/></xsl:text>
          </xsl:otherwise>
        </xsl:choose>
     </div>
  </xsl:template>

  <!--
     Displays a schema attribute in the correct format
     for the Schema Component Representation table, e.g.
     tags are one color, and content are another.
     Param(s):
            attrName (String) required
                Name of attribute
            attrValue (Result Tree Fragment) required
                Value of attribute, which may be links
     -->
  <xsl:template name="DisplayAttr">
     <xsl:param name="attrName"/>
     <xsl:param name="attrValue"/>

     <xsl:text> </xsl:text>
     <span class="scTag"><xsl:value-of select="$attrName"/></span>
     <xsl:text>="</xsl:text>
     <xsl:if test="normalize-space()!='$attrValue'">
        <span class="scContent">
          <xsl:copy-of select="$attrValue"/>
        </span>
     </xsl:if>
     <xsl:text>"</xsl:text>
  </xsl:template>

  <!--
     Displays attributes from a schema element, unless
     otherwise specified, in the correct format
     for the Schema Component Representation table, e.g.
     tags are one color, and content are another.
     Param(s):
            component (Node) required
                Schema element whose attributes are to be displayed
            attrsNotToDisplay (String) required
                List of attributes not to be displayed
                Each attribute name should prepended with '*'
                and appended with '+'
     -->
  <xsl:template name="DisplayOtherAttributes">
     <xsl:param name="component"/>
     <xsl:param name="attrsNotToDisplay"/>

     <xsl:for-each select="$component/attribute::*">
        <xsl:variable name="attrName" select="local-name(.)"/>
        <xsl:if test="not(contains($attrsNotToDisplay, concat('*', $attrName, '+')))">
          <xsl:call-template name="DisplayAttr">
             <xsl:with-param name="attrName" select="normalize-space($attrName)"/>
             <xsl:with-param name="attrValue" select="normalize-space(.)"/>
          </xsl:call-template>
        </xsl:if>
     </xsl:for-each>
  </xsl:template>


  <!-- **** XML Pretty Printer **** -->

  <!--
     Displays an arbitrary XML element.
     Param(s):
            margin (nonNegativeInteger) optional
                Number of 'em' to indent from left
     -->
  <xsl:template match="*" mode="xpp">
     <xsl:param name="margin">0</xsl:param>

     <div style="margin-left: {$margin}em">
        <code>
          <!-- Start Tag -->
          <xsl:text>&lt;</xsl:text>
          <xsl:value-of select="local-name(.)"/>

          <!-- Attributes -->
          <xsl:for-each select="@*">    
             <xsl:text> </xsl:text><xsl:value-of select="name(.)"/>
             <xsl:text>="</xsl:text><xsl:value-of select="."/>
             <xsl:text>"</xsl:text>
          </xsl:for-each>

          <xsl:choose>
             <xsl:when test="* | text()">
                <!-- Close Start Tag -->
                <xsl:text>> </xsl:text>

                <!-- Content -->    
                <xsl:apply-templates select="* | text()" mode="xpp">
                  <xsl:with-param name="margin" select="$ELEM_INDENT"/>
                </xsl:apply-templates>

                <!-- End Tag -->
                <xsl:text>&lt;/</xsl:text>
                <xsl:value-of select="local-name(.)"/>
                <xsl:text>></xsl:text>
             </xsl:when>
             <xsl:otherwise> 
                <!-- Close Start Tag -->
                <xsl:text>/></xsl:text>
             </xsl:otherwise>
          </xsl:choose>
        </code>
     </div>
  </xsl:template>

  <!--
     Displays an arbitrary XML text node.
     -->
  <xsl:template match="text()" mode="xpp">
     <xsl:value-of select="."/>
  </xsl:template>


  <!-- **** Handling Schema Component References **** -->

  <!--
     Generates a link to an attribute.
     Param(s):
            name (String) optional
                Name of attribute
            ref (String) optional
                Reference to attribute
            (One of 'name' and 'ref' must be provided.)
            isSample (String) optional
                If true, link should be for XML Instance
                Representation table of attribute declaration.
                Otherwise, link will be for top of
                attribute declaration section.
     -->
  <xsl:template name="PrintAttributeRef">
     <xsl:param name="name"/>
     <xsl:param name="ref"/>
     <xsl:param name="isSample">false</xsl:param>
     
     <xsl:choose>
        <xsl:when test="$name!=''">
          <xsl:call-template name="PrintName">
             <xsl:with-param name="name" select="$name"/>
             <xsl:with-param name="compType">attribute</xsl:with-param>
             <xsl:with-param name="isSample" select="$isSample"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="$ref!=''">
          <xsl:call-template name="PrintRef">
             <xsl:with-param name="ref" select="$ref"/>
             <xsl:with-param name="compType">attribute</xsl:with-param>
             <xsl:with-param name="isSample" select="$isSample"/>
          </xsl:call-template>
        </xsl:when>
     </xsl:choose>
  </xsl:template>
  
  <!--
     Generates a link to an attribute group.
     Param(s):
            name (String) optional
                Name of attribute group
            ref (String) optional
                Reference to attribute group
            (One of 'name' and 'ref' must be provided.)
            isSample (String) optional
                If true, link should be for XML Instance
                Representation table of attribute group definition.
                Otherwise, link will be for top of
                attribute group definition section.
     -->
  <xsl:template name="PrintAttributeGroupRef">
     <xsl:param name="name"/>
     <xsl:param name="ref"/>
     <xsl:param name="isSample">false</xsl:param>
     
     <xsl:choose>
        <xsl:when test="$name!=''">
          <xsl:call-template name="PrintName">
             <xsl:with-param name="name" select="$name"/>
             <xsl:with-param name="compType">attribute group</xsl:with-param>
             <xsl:with-param name="isSample" select="$isSample"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="$ref!=''">
          <xsl:call-template name="PrintRef">
             <xsl:with-param name="ref" select="$ref"/>
             <xsl:with-param name="compType">attribute group</xsl:with-param>
             <xsl:with-param name="isSample" select="$isSample"/>
          </xsl:call-template>
        </xsl:when>
     </xsl:choose>
  </xsl:template>
  
  <!--
     Generates a link to an element.
     Param(s):
            name (String) optional
                Name of element
            ref (String) optional
                Reference to element
            (One of 'name' and 'ref' must be provided.)
            isSample (String) optional
                If true, link should be for XML Instance
                Representation table of element declaration.
                Otherwise, link will be for top of
                element declaration section.
     -->
  <xsl:template name="PrintElementRef">
     <xsl:param name="name"/>
     <xsl:param name="ref"/>
     <xsl:param name="isSample">false</xsl:param>
     
     <xsl:choose>
        <xsl:when test="$name!=''">
          <xsl:call-template name="PrintName">
             <xsl:with-param name="name" select="$name"/>
             <xsl:with-param name="compType">element</xsl:with-param>
             <xsl:with-param name="isSample" select="$isSample"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="$ref!=''">
          <xsl:call-template name="PrintRef">
             <xsl:with-param name="ref" select="$ref"/>
             <xsl:with-param name="compType">element</xsl:with-param>
             <xsl:with-param name="isSample" select="$isSample"/>
          </xsl:call-template>
        </xsl:when>
     </xsl:choose>
  </xsl:template>
  
  <!--
     Generates a link to a group.
     Param(s):
            name (String) optional
                Name of group
            ref (String) optional
                Reference to group
            (One of 'name' and 'ref' must be provided.)
            isSample (String) optional
                If true, link should be for XML Instance
                Representation table of group definition.
                Otherwise, link will be for top of
                group definition section.
     -->
  <xsl:template name="PrintGroupRef">
     <xsl:param name="name"/>
     <xsl:param name="ref"/>
     <xsl:param name="isSample">false</xsl:param>
     
     <xsl:choose>
        <xsl:when test="$name!=''">
          <xsl:call-template name="PrintName">
             <xsl:with-param name="name" select="$name"/>
             <xsl:with-param name="compType">group</xsl:with-param>
             <xsl:with-param name="isSample" select="$isSample"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="$ref!=''">
          <xsl:call-template name="PrintRef">
             <xsl:with-param name="ref" select="$ref"/>
             <xsl:with-param name="compType">group</xsl:with-param>
             <xsl:with-param name="isSample" select="$isSample"/>
          </xsl:call-template>
        </xsl:when>
     </xsl:choose>
  </xsl:template>
  
  <!--
     Generates a link to a key/uniqueness constraint.
     Param(s):
            name (String) optional
                Name of key/uniqueness constraint
            ref (String) optional
                Reference to key/uniqueness constraint
            (One of 'name' and 'ref' must be provided.)
            isSample (String) optional
                If true, link should be for a key/uniqueness
                constraint in a XML Instance Representation table.
     -->
  <xsl:template name="PrintKeyRef">
     <xsl:param name="name"/>
     <xsl:param name="ref"/>
     <xsl:param name="isSample">true</xsl:param>
     
     <xsl:choose>
        <xsl:when test="$name!=''">
          <xsl:call-template name="PrintName">
             <xsl:with-param name="name" select="$name"/>
             <xsl:with-param name="compType">uniqueness/key constraint</xsl:with-param>
             <xsl:with-param name="isSample" select="$isSample"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="$ref!=''">
          <xsl:call-template name="PrintRef">
             <xsl:with-param name="ref" select="$ref"/>
             <xsl:with-param name="compType">uniqueness/key constraint</xsl:with-param>
             <xsl:with-param name="isSample" select="$isSample"/>
          </xsl:call-template>
        </xsl:when>
     </xsl:choose>
  </xsl:template>
  
  <!--
     Generates a link to a type.
     Param(s):
            name (String) optional
                Name of type
            ref (String) optional
                Reference to type
            (One of 'name' and 'ref' must be provided.)
            isSample (String) optional
                If true, link should be for XML Instance
                Representation table of type definition.
                Otherwise, link will be for top of
                type definition section.
     -->
  <xsl:template name="PrintTypeRef">
     <xsl:param name="name"/>
     <xsl:param name="ref"/>
     <xsl:param name="isSample">false</xsl:param>
     
     <xsl:choose>
        <xsl:when test="$name!=''">
          <span class="type">
             <xsl:call-template name="PrintName">
                <xsl:with-param name="name" select="$name"/>
                <xsl:with-param name="compType">type</xsl:with-param>
                <xsl:with-param name="isSample" select="$isSample"/>
             </xsl:call-template>
          </span>
        </xsl:when>
        <xsl:when test="$ref!=''">
          <span class="type">
             <xsl:call-template name="PrintRef">
                <xsl:with-param name="ref" select="$ref"/>
                <xsl:with-param name="compType">type</xsl:with-param>
                <xsl:with-param name="isSample" select="$isSample"/>
             </xsl:call-template>
          </span>
        </xsl:when>
     </xsl:choose>
  </xsl:template>
  
  <!--
     Prints out the name of a schema component, providing a link,
     if appropriate.
     Assumes the schema component is from the current schema's target namespace.
     Param(s):
            name (String) required
                Name of schema component
            compType (String) required
                Type of schema component
            isSample (String) optional
                If true, link should be for XML Instance
                Representation table of type definition.
                Otherwise, link will be for top of
                type definition section.
     -->
  <xsl:template name="PrintName">
     <xsl:param name="name"/>
     <xsl:param name="compType"/>
     <xsl:param name="isSample">false</xsl:param>

     <!-- Get correct terminology for statements -->     
     <xsl:variable name="noun">
        <xsl:choose>
          <xsl:when test="$compType='element' or $compType='attribute'">
             <xsl:text>declaration</xsl:text>
          </xsl:when>
          <xsl:otherwise>
             <xsl:text>definition</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:variable>
     <xsl:variable name="adverb">
        <xsl:choose>
          <xsl:when test="$compType='element' or $compType='attribute'">
             <xsl:text>declared</xsl:text>
          </xsl:when>
          <xsl:otherwise>
             <xsl:text>defined</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:variable>
     
     <!-- Get prefix and check whether component can be found in the schema -->
     <xsl:variable name="compPrefix">
        <xsl:choose>
          <xsl:when test="$compType='attribute' and /xsd:schema/xsd:attribute[@name=$name]">
             <xsl:value-of select="$ATTR_PREFIX"/>
          </xsl:when>
          <xsl:when test="$compType='attribute group' and (/xsd:schema/xsd:attributeGroup[@name=$name] or /xsd:schema/xsd:redefine/xsd:attributeGroup[@name=$name])">
             <xsl:value-of select="$ATTR_GRP_PREFIX"/>
          </xsl:when>
          <xsl:when test="$compType='element' and /xsd:schema/xsd:element[@name=$name]">
             <xsl:value-of select="$ELEM_PREFIX"/>
          </xsl:when>
          <xsl:when test="$compType='group' and (/xsd:schema/xsd:group[@name=$name] or /xsd:schema/xsd:redefine/xsd:group[@name=$name])">
             <xsl:value-of select="$GRP_PREFIX"/>
          </xsl:when>
          <xsl:when test="$compType='type' and (/xsd:schema/xsd:complexType[@name=$name] or /xsd:schema/xsd:redefine/xsd:complexType[@name=$name])">
             <xsl:value-of select="$CTYPE_PREFIX"/>
          </xsl:when>
          <xsl:when test="$compType='type' and (/xsd:schema/xsd:simpleType[@name=$name] or /xsd:schema/xsd:redefine/xsd:simpleType[@name=$name])">
             <xsl:value-of select="$STYPE_PREFIX"/>
          </xsl:when>
          <xsl:when test="$compType='uniqueness/key constraint' and /xsd:schema//xsd:element/xsd:key[@name=$name] or /xsd:schema//xsd:element/xsd:unique[@name=$name]">
             <xsl:value-of select="$KEY_PREFIX"/>
          </xsl:when>
        </xsl:choose>
     </xsl:variable>
     
     <xsl:choose>
        <xsl:when test="normalize-space($compPrefix)!=''">
          <!-- Component found in schema -->

          <!-- Generate href link -->
          <xsl:variable name="link">
             <xsl:choose>
                <xsl:when test="$isSample!='false' and $compPrefix!=$STYPE_PREFIX">
                  <!-- Jump to sample instance table -->
                  <!-- Doesn't apply to identity constraints and simple types. 
                         There are no sample instance table in their sections. -->
                  <xsl:value-of select="concat($SAMPLE_PREFIX,$compPrefix,normalize-space($name))"/>
                </xsl:when>
                <xsl:otherwise>
                  <!-- Jump to beginning of schema component's section -->
                  <xsl:value-of select="concat($compPrefix,normalize-space($name))"/>
                </xsl:otherwise>
             </xsl:choose>
          </xsl:variable>

          <!-- Generate link title -->     
          <xsl:variable name="title">
             <xsl:text>Jump to '</xsl:text><xsl:value-of select="$name"/>
             <xsl:text>' </xsl:text><xsl:value-of select="$compType"/>
             <xsl:choose>
                <xsl:when test="$isSample!='false' and $compPrefix!=$KEY_PREFIX and $compPrefix!=$STYPE_PREFIX">
                  <xsl:text>'s XML instance representation</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text> </xsl:text><xsl:value-of select="$noun"/>
                </xsl:otherwise>
             </xsl:choose>
             <xsl:text>.</xsl:text>
          </xsl:variable>

          <a title="{$title}" href="#{$link}">
             <xsl:value-of select="$name"/>
          </a>
        </xsl:when>
        <xsl:otherwise> 
          <!-- Component not found in schema -->

          <!-- Generate link title -->
          <xsl:variable name="title">
             <xsl:text>'</xsl:text><xsl:value-of select="$name"/>
             <xsl:text>' </xsl:text><xsl:value-of select="$compType"/>
             <xsl:text> </xsl:text><xsl:value-of select="$noun"/>
             <xsl:text> could not be found in this schema.</xsl:text>
          </xsl:variable>

          <!-- Generate error message -->
          <xsl:variable name="errmsg">
             <xsl:text>\'</xsl:text><xsl:value-of select="$name"/>
             <xsl:text>\' </xsl:text><xsl:value-of select="$compType"/>
             <xsl:text> is from the current schema namespace, but its </xsl:text>
             <xsl:value-of select="$noun"/>
             <xsl:text> could not be located. It may be </xsl:text>
             <xsl:value-of select="$adverb"/>
             <xsl:text>  in an \'included\' schema.</xsl:text>
          </xsl:variable>

          <!-- Link will open alert box with error message. -->
          <a title="{$title}" href="javascript:void(0)" onclick="alert('{$errmsg}')">
             <xsl:value-of select="$name"/>
          </a>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>
  
  <!--
     Prints out a reference to a schema component.
     This template will try to work out the namespace of the schema
     component.
     Param(s):
            ref (String) required
                Reference to schema component
            compType (String) required
                Type of schema component
            isSample (String) optional
                If true, link should be for XML Instance
                Representation table of type definition.
                Otherwise, link will be for top of
                type definition section.
     -->
  <xsl:template name="PrintRef">
     <xsl:param name="ref"/>
     <xsl:param name="compType"/>
     <xsl:param name="isSample">false</xsl:param>
     
     <!-- Get correct terminology for statements -->     
     <xsl:variable name="noun">
        <xsl:choose>
          <xsl:when test="$compType='element' or $compType='attribute'">
             <xsl:text>declaration</xsl:text>
          </xsl:when>
          <xsl:otherwise>
             <xsl:text>definition</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:variable>
     <xsl:variable name="adverb">
        <xsl:choose>
          <xsl:when test="$compType='element' or $compType='attribute'">
             <xsl:text>declared</xsl:text>
          </xsl:when>
          <xsl:otherwise>
             <xsl:text>defined</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:variable>

     <!-- Get local name -->
     <xsl:variable name="refName">
        <xsl:call-template name="GetRefName">
          <xsl:with-param name="ref" select="$ref"/>
        </xsl:call-template>
     </xsl:variable>
     <!-- Get prefix -->
     <xsl:variable name="refPrefix">
        <xsl:call-template name="GetRefPrefix">
          <xsl:with-param name="ref" select="$ref"/>
        </xsl:call-template>
     </xsl:variable>
     <!-- Get prefix of this schema's target namespace -->
     <xsl:variable name="tnPrefix">
        <xsl:call-template name="GetThisPrefix"/>
     </xsl:variable>

     <!-- Print prefix -->
     <xsl:call-template name="PrintNSPrefix">
        <xsl:with-param name="prefix" select="$refPrefix"/>
     </xsl:call-template>
     <!-- Print local name -->
     <xsl:choose>
        <!-- Reference to schema component from
             current schema's namespace -->
        <xsl:when test="$refPrefix=$tnPrefix">
           <!-- Provide a link to schema component's section -->
           <xsl:call-template name="PrintName">
              <xsl:with-param name="name" select="$refName"/>
              <xsl:with-param name="compType" select="$compType"/>
              <xsl:with-param name="isSample" select="$isSample"/>
           </xsl:call-template>
        </xsl:when>
        <!-- Reference to schema component from
             another namespace -->
        <xsl:otherwise>
           <xsl:value-of select="$refName"/>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out a reference to a namespace in the schema.
     Param(s):
            prefix (String) required
              Namespace prefix referenced
     -->
  <xsl:template name="PrintNamespaceRef">
     <xsl:param name="prefix"/>

     <xsl:if test="$prefix!=''">
        <xsl:choose>
           <xsl:when test="/xsd:schema/namespace::*[local-name(.)=normalize-space($prefix)]">
              <a href="#{concat($NS_PREFIX, $prefix)}" title="Find out namespace of '{$prefix}' prefix">
                 <xsl:value-of select="$prefix"/>
              </a>
           </xsl:when>
           <xsl:otherwise>
              <a href="javascript:void(0)" onclick="alert('Namespace was not declared!')" title="Unknown namespace">
                 <xsl:value-of select="$prefix"/>
              </a>
           </xsl:otherwise>
        </xsl:choose>
     </xsl:if>
  </xsl:template>

  <!--
     Prints out a reference to a term in the glossary section.
     Param(s):
            code (String) required
              Unique ID of glossary term
            term (String) optional
              Glossary term
     -->
  <xsl:template name="PrintGlossaryTermRef">
     <xsl:param name="code"/>
     <xsl:param name="term"/>

     <xsl:choose>
        <xsl:when test="$code !='' and normalize-space(translate($printGlossary,'TRUE','true'))='true'">
          <a title="Look up '{$term}' in glossary" href="#{concat($TERM_PREFIX, $code)}">
             <xsl:choose>
                <xsl:when test="$term!=''">
                  <xsl:value-of select="$term"/>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:text>[term]</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$term"/>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>


  <!-- **** General Utility Templates **** -->

  <!-- 
     Creates a box that can open and class. 
     Param(s):
            anchor (String) optional
              Anchor name to this box
            boxID (String) required
              Unique ID of the 'div' box
            tableClass (String) optional
              CSS class of table containing 'div' box
            boxTitle (String) required
              Title (which will always be shown)
            boxContents (String) required
              Contents of 'div' box
            isOpened (String) required
              Set to true if initially opened, and
              false if initially closed
     -->
  <xsl:template name="CollapseableBox">
     <xsl:param name="anchor"/>
     <xsl:param name="boxID"/>
     <xsl:param name="tableClass"/>
     <xsl:param name="boxTitle"/>
     <xsl:param name="boxContents"/>
     <xsl:param name="isOpened">false</xsl:param>

     <xsl:variable name="buttonID" select="concat($boxID, '_button')"/>

     <table>
        <xsl:if test="$tableClass!=''">
          <xsl:attribute name="class">
             <xsl:value-of select="$tableClass"/>
          </xsl:attribute>
        </xsl:if>

        <tr>
        <th>
          <xsl:if test="normalize-space(translate($useJavaScript,'TRUE','true'))='true'">
             <!-- Button to control the opening and closing of the 'div' box -->
             <input type="button" id="{$buttonID}" class="boxControl" onclick="switchState('{$boxID}'); return false;" style="display: none"/>
                <!--
                  Button's 'display' property is set to 'none', 
                  so that button will only be displayed if
                  box can be successfully opened and closed.
                -->
          </xsl:if>

          <!-- Title -->
          <xsl:text> </xsl:text>
          <xsl:choose>
             <xsl:when test="$anchor != ''">
                <a name="{$anchor}"><xsl:value-of select="$boxTitle"/></a>
             </xsl:when>
             <xsl:otherwise> 
                <xsl:value-of select="$boxTitle"/>
             </xsl:otherwise>
          </xsl:choose>
        </th>
        </tr>
        <tr>
        <td>
          <!-- Box containing the schema component representation -->
          <div id="{$boxID}" class="box">
             <xsl:copy-of select="$boxContents"/>
          </div>
        </td>
        </tr>
     </table>
  </xsl:template>

  <!--
     Returns the namespace of an attribute
     declaration or reference.
     Param(s):
            attribute (Node) required
                Attribute declaration or reference
  -->
  <xsl:template name="GetAttributeNS">
     <xsl:param name="attribute"/>

     <xsl:choose>
        <!-- Qualified local attribute declaration -->
        <xsl:when test="$attribute[@name] and (normalize-space(translate($attribute/@form, 'QUALIFED', 'qualifed'))='qualified' or normalize-space(translate(/xsd:schema/@attributeFormDefault, 'QUALIFED', 'qualifed'))='qualified')">
          <xsl:value-of select="/xsd:schema/@targetNamespace"/>
        </xsl:when>
        <!-- Reference to global attribute declaration -->
        <xsl:when test="$attribute[@ref]">
          <xsl:call-template name="GetRefNS">
             <xsl:with-param name="ref" select="$attribute/@ref"/>
          </xsl:call-template>
        </xsl:when>
        <!-- Otherwise, attribute has no namespace -->
     </xsl:choose>
  </xsl:template>

  <!--
     Returns the description that can be used in 
     headers for a schema component.
     Param(s):
            component (Node) required
                Schema component
  -->
  <xsl:template name="GetComponentDescription">
     <xsl:param name="component"/>
     
     <xsl:choose>
        <xsl:when test="local-name($component)='all'">
          <xsl:text>All Model Group</xsl:text>
        </xsl:when>
        <xsl:when test="local-name($component)='attribute'">
          <xsl:text>Attribute</xsl:text>
        </xsl:when>
        <xsl:when test="local-name($component)='attributeGroup'">
          <xsl:text>Attribute Group</xsl:text>
        </xsl:when>
        <xsl:when test="local-name($component)='choice'">
          <xsl:text>Choice Model Group</xsl:text>
        </xsl:when>
        <xsl:when test="local-name($component)='complexType'">
          <xsl:text>Complex Type</xsl:text>
        </xsl:when>
        <xsl:when test="local-name($component)='element'">
          <xsl:text>Element</xsl:text>
        </xsl:when>
        <xsl:when test="local-name($component)='group'">
          <xsl:text>Model Group</xsl:text>
        </xsl:when>
        <xsl:when test="local-name($component)='notation'">
          <xsl:text>Notation</xsl:text>
        </xsl:when>
        <xsl:when test="local-name($component)='sequence'">
          <xsl:text>Sequence Model Group</xsl:text>
        </xsl:when>
        <xsl:when test="local-name($component)='simpleType'">
          <xsl:text>Simple Type</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message terminate="yes">
             <xsl:text>Unknown schema component, </xsl:text>
             <xsl:value-of select="local-name($component)"/>
             <xsl:text>.</xsl:text>
          </xsl:message>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Returns the unique identifier for a top-level schema
     component. Returns the string "schema" if the 'component' 
     is the root schema element.
     Param(s):
            component (Node) required
                Schema component
  -->
  <xsl:template name="GetComponentID">
     <xsl:param name="component"/>

     <xsl:choose>
        <xsl:when test="local-name($component)='schema'">
          <xsl:text>schema</xsl:text>
        </xsl:when>
        <xsl:otherwise> 
          <xsl:variable name="componentPrefix">
             <xsl:call-template name="GetComponentPrefix">
                     <xsl:with-param name="component" select="$component"/>
             </xsl:call-template>
          </xsl:variable>
          <xsl:value-of select="concat($componentPrefix, $component/@name)"/>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Returns the prefix to add in front of a schema component
     name when generating anchor names.
     Param(s):
            component (Node) required
                Schema component
  -->
  <xsl:template name="GetComponentPrefix">
     <xsl:param name="component"/>
     
     <xsl:choose>
        <xsl:when test="local-name($component)='attribute'">
          <xsl:value-of select="$ATTR_PREFIX"/>
        </xsl:when>
        <xsl:when test="local-name($component)='attributeGroup'">
          <xsl:value-of select="$ATTR_GRP_PREFIX"/>
        </xsl:when>
        <xsl:when test="local-name($component)='complexType'">
          <xsl:value-of select="$CTYPE_PREFIX"/>
        </xsl:when>
        <xsl:when test="local-name($component)='element'">
          <xsl:value-of select="$ELEM_PREFIX"/>
        </xsl:when>
        <xsl:when test="local-name($component)='group'">
          <xsl:value-of select="$GRP_PREFIX"/>
        </xsl:when>
        <xsl:when test="local-name($component)='notation'">
          <xsl:value-of select="$NOTA_PREFIX"/>
        </xsl:when>
        <xsl:when test="local-name($component)='simpleType'">
          <xsl:value-of select="$STYPE_PREFIX"/>
        </xsl:when>
        <xsl:when test="local-name($component)='key' or local-name($component)='unique'">
          <xsl:value-of select="$KEY_PREFIX"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message terminate="yes">
             <xsl:text>Unknown top-level schema component, </xsl:text>
             <xsl:value-of select="local-name($component)"/>
             <xsl:text>.</xsl:text>
          </xsl:message>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Returns a glossary term reference for the 
     schema component type, if applicable.
     Param(s):
            component (Node) required
                Schema component
  -->
  <xsl:template name="GetComponentTermRef">
     <xsl:param name="component"/>
     
     <xsl:choose>
        <xsl:when test="local-name($component)='notation'">
          <xsl:text>Notation</xsl:text>
        </xsl:when>
     </xsl:choose>
  </xsl:template>

  <!--
     Returns the namespace prefix of an attribute.
     Param(s):
            attribute (Node) required
                Attribute declaration or reference
  -->
  <xsl:template name="GetAttributePrefix">
    <xsl:param name="attribute"/>

    <xsl:choose>
       <!-- Element reference -->
       <xsl:when test="$attribute/@ref">
          <xsl:call-template name="GetRefPrefix">
          <xsl:with-param name="ref" select="$attribute/@ref"/>
          </xsl:call-template>
       </xsl:when>
       <!-- Global element declaration -->
       <xsl:when test="local-name($attribute/..)='schema'">
          <xsl:call-template name="GetThisPrefix"/>
       </xsl:when>
       <!-- Local element declaration -->
       <xsl:otherwise>
          <xsl:if test="($attribute/@form and normalize-space($attribute/@form)='qualified') or (/xsd:schema/@attributeFormDefault and normalize-space(/xsd:schema/@attributeFormDefault)='qualified')">
             <xsl:call-template name="GetThisPrefix"/>
          </xsl:if>
       </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Returns the namespace prefix of an element.
     Param(s):
            element (Node) required
                Element declaration or reference
  -->
  <xsl:template name="GetElementPrefix">
    <xsl:param name="element"/>

    <xsl:choose>
       <!-- Element reference -->
       <xsl:when test="$element/@ref">
          <xsl:call-template name="GetRefPrefix">
          <xsl:with-param name="ref" select="$element/@ref"/>
          </xsl:call-template>
       </xsl:when>
       <!-- Global element declaration -->
       <xsl:when test="local-name($element/..)='schema'">
          <xsl:call-template name="GetThisPrefix"/>
       </xsl:when>
       <!-- Local element declaration -->
       <xsl:otherwise>
          <xsl:if test="($element/@form and normalize-space($element/@form)='qualified') or (/xsd:schema/@elementFormDefault and normalize-space(/xsd:schema/@elementFormDefault)='qualified')">
             <xsl:call-template name="GetThisPrefix"/>
          </xsl:if>
       </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Returns the local name of a reference.
     Param(s):
            ref (String) required
                Reference
  -->
  <xsl:template name="GetRefName">
     <xsl:param name="ref"/>

     <xsl:choose>
        <xsl:when test="contains($ref, ':')">
          <!-- Ref has namespace prefix -->
          <xsl:value-of select="substring-after($ref, ':')"/>
        </xsl:when>
        <xsl:otherwise> 
          <!-- Ref has no namespace prefix -->
          <xsl:value-of select="$ref"/>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Returns the namespace prefix of a reference.
     Param(s):
            ref (String) required
                Reference
  -->
  <xsl:template name="GetRefPrefix">
     <xsl:param name="ref"/>

     <!-- Get namespace prefix -->     
     <xsl:value-of select="substring-before($ref, ':')"/>
  </xsl:template>

  <!--
     Returns the namespace of a reference.
     Param(s):
            ref (String) required
                Reference
  -->
  <xsl:template name="GetRefNS">
     <xsl:param name="ref"/>

     <!-- Get namespace prefix -->     
     <xsl:variable name="refPrefix" select="substring-before($ref, ':')"/>
     <!-- Get namespace -->
     <xsl:value-of select="/xsd:schema/namespace::*[local-name(.)=normalize-space($refPrefix)]"/>
  </xsl:template>

  <!--
     Returns the declared prefix of this schema's target namespace.
  -->
  <xsl:template name="GetThisPrefix">
     <xsl:if test="/xsd:schema/@targetNamespace">
        <xsl:value-of select="local-name(/xsd:schema/namespace::*[normalize-space(.)=normalize-space(/xsd:schema/@targetNamespace)])"/>
     </xsl:if>
  </xsl:template>

  <!--
     Returns the declared prefix of XML Schema namespace.
  -->
  <xsl:template name="GetXSDPrefix">
     <xsl:value-of select="local-name(/xsd:schema/namespace::*[normalize-space(.)=$XSD_NS])"/>
  </xsl:template>

  <!--
     Prints out a boolean value.
     Param(s):
            boolean (String) required
                Boolean value
  -->
  <xsl:template name="PrintBoolean">
     <xsl:param name="boolean"/>

     <xsl:choose>
        <xsl:when test="normalize-space(translate($boolean,'TRUE', 'true'))='true' or normalize-space($boolean)='1'">
          <xsl:text>yes</xsl:text>
        </xsl:when>
        <xsl:otherwise> 
          <xsl:text>no</xsl:text>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out a link to the namespace of a prefix,
     and tacks on a colon in the end.
     Param(s):
            prefix (String) required
                Namespace prefix
            nolink (boolean) optional
                If true, doesn't provide a link to the
                namespace in the namespaces table.
  -->
  <xsl:template name="PrintNSPrefix">
     <xsl:param name="prefix"/>
     <xsl:param name="nolink">false</xsl:param>

     <xsl:if test="$prefix!='' and normalize-space(translate($printNSPrefixes,'TRUE','true'))='true'">
        <xsl:choose>
           <xsl:when test="$nolink='false'">
              <xsl:call-template name="PrintNamespaceRef">
                 <xsl:with-param name="prefix" select="$prefix"/>
              </xsl:call-template>
           </xsl:when>
           <xsl:otherwise> 
              <xsl:value-of select="$prefix"/>
           </xsl:otherwise>
        </xsl:choose>
        <xsl:text>:</xsl:text>
     </xsl:if>
  </xsl:template>
  
  <!--
     Prints out the min/max occurrences of a schema component.
     Param(s):
            component (Node) required
                Schema component
  -->
  <xsl:template name="PrintOccurs">
     <xsl:param name="component"/>

     <!-- Get min occurs -->
     <xsl:variable name="min">
        <xsl:choose>
          <xsl:when test="local-name($component)='attribute'">
             <xsl:choose>
                <xsl:when test="normalize-space($component/@use)='required'">
                  <xsl:text>1</xsl:text>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:text>0</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </xsl:when>
          <xsl:otherwise> 
             <xsl:choose>
                <xsl:when test="$component/@minOccurs">
                  <xsl:value-of select="$component/@minOccurs"/>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:text>1</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:variable>

     <!-- Get max occurs -->
     <xsl:variable name="max">
        <xsl:choose>
          <xsl:when test="local-name($component)='attribute'">
             <xsl:choose>
                <xsl:when test="normalize-space($component/@use)='prohibited'">
                  <xsl:text>0</xsl:text>
                </xsl:when>
                <xsl:otherwise> 
                  <xsl:text>1</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </xsl:when>
          <xsl:otherwise> 
             <xsl:choose>
                <xsl:when test="normalize-space($component/@maxOccurs)='unbounded'">
                  <xsl:text>*</xsl:text>
                </xsl:when>
                <xsl:when test="$component/@maxOccurs">
                  <xsl:value-of select="$component/@maxOccurs"/>
                </xsl:when>
                <!--
                Not found in XML Schema recommendation
                <xsl:when test="$component/@minOccurs and number($component/@minOccurs) &gt; 1">
                  <xsl:value-of select="$component/@minOccurs"/>
                </xsl:when>
                 -->
                <xsl:otherwise> 
                  <xsl:text>1</xsl:text>
                </xsl:otherwise>
             </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:variable>

     <span class="occurs">
        <xsl:choose>
          <xsl:when test="number($min)=1 and number($max)=1">
                <xsl:text>[1]</xsl:text>
          </xsl:when>
          <xsl:otherwise> 
                <xsl:text>[</xsl:text><xsl:value-of select="$min"/>
                <xsl:text>..</xsl:text><xsl:value-of select="$max"/>
                <xsl:text>]</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
     </span>
  </xsl:template>

  <!--
     Translates occurrence of '#all' in 'block' value
     of element declarations.
     Param(s):
            EBV (String) required
                Value
  -->
  <xsl:template name="PrintBlockSet">
     <xsl:param name="EBV"/>

     <xsl:choose>
        <xsl:when test="normalize-space($EBV)='#all'">
          <xsl:text>restriction, extension, substitution</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$EBV"/>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Translates occurrence of '#all' in 'final' value
     of element declarations, and 'block' and 'final' values 
     in complex type definitions.
     Param(s):
            EBV (String) required
                Value
  -->
  <xsl:template name="PrintDerivationSet">
     <xsl:param name="EBV"/>

     <xsl:choose>
        <xsl:when test="normalize-space($EBV)='#all'">
          <xsl:text>restriction, extension</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$EBV"/>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Translates occurrence of '#all' in 'final' value
     of simple type definitions.
     Param(s):
            EBV (String) required
                Value
  -->
  <xsl:template name="PrintSimpleDerivationSet">
     <xsl:param name="EBV"/>

     <xsl:choose>
        <xsl:when test="normalize-space($EBV)='#all'">
          <xsl:text>restriction, list, union</xsl:text>
        </xsl:when>
        <xsl:when test="normalize-space($EBV)!=''">
          <xsl:value-of select="$EBV"/>
        </xsl:when>
     </xsl:choose>
  </xsl:template>
  
  <!--
     Print out a URI. If it starts with 'http', a link is provided.
     Param(s):
            uri (String) required
              URI. to be printed
     -->
  <xsl:template name="PrintURI">
     <xsl:param name="uri"/>

     <xsl:choose>
        <xsl:when test="starts-with($uri, 'http')">
          <a title="{$uri}" href="{$uri}"><xsl:value-of select="$uri"/></a>
        </xsl:when>
        <xsl:otherwise> 
          <xsl:value-of select="$uri"/>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Tokenises a whitespace-separated list of values, and
     displays them appropriately.
     Param(s):
            value (String) required
              Whitespace-separated list
            compType (String) optional
              Specify schema component type if values in list are
              schema components, so appropriate links can be provided.
            isInList (boolean) optional
              If true, place each value within 'li' tags.
              Assumes that this template is called within an HTML
              list element.
            separator (String) optional
              Character(s) to use to separate resulting values in list.
              Only used if 'isInList' is false.
              If none is provided, uses a space character, ' '.
            isFirst (boolean) optional
              If false, it's a recursive call from 'PrintWhitespaceList'.
     -->
  <xsl:template name="PrintWhitespaceList">
     <xsl:param name="value"/>
     <xsl:param name="compType"/>
     <xsl:param name="isInList">false</xsl:param>
     <xsl:param name="separator"/>
     <xsl:param name="isFirst">true</xsl:param>

     <xsl:variable name="token" select="normalize-space(substring-before($value, ' '))"/>
     <xsl:choose>
        <xsl:when test="$token!=''">
          <!-- Whitespace found in value -->
          
          <!-- Print out token -->
          <xsl:call-template name="PrintWhitespaceListToken">
             <xsl:with-param name="token" select="$token"/>
             <xsl:with-param name="compType" select="$compType"/>
             <xsl:with-param name="isInList" select="$isInList"/>
             <xsl:with-param name="separator" select="$separator"/>
             <xsl:with-param name="isFirst" select="$isFirst"/>
          </xsl:call-template>

          <!-- Continue parsing -->
          <xsl:variable name="rest" select="normalize-space(substring-after($value, $token))"/>
          <xsl:if test="$rest!=''">
             <xsl:call-template name="PrintWhitespaceList">
                <xsl:with-param name="value" select="$rest"/>
                <xsl:with-param name="compType" select="$compType"/>
                <xsl:with-param name="isInList" select="$isInList"/>
                <xsl:with-param name="separator" select="$separator"/>
                <xsl:with-param name="isFirst">false</xsl:with-param>
             </xsl:call-template>
          </xsl:if>
        </xsl:when>
        <xsl:otherwise> 
          <!-- No more whitespaces  -->

          <!-- Print out one last token -->
          <xsl:if test="normalize-space($value)!=''">
             <xsl:call-template name="PrintWhitespaceListToken">
                <xsl:with-param name="token" select="$value"/>
                <xsl:with-param name="compType" select="$compType"/>
                <xsl:with-param name="isInList" select="$isInList"/>
                <xsl:with-param name="separator" select="$separator"/>
                <xsl:with-param name="isFirst" select="$isFirst"/>
             </xsl:call-template>
          </xsl:if>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Helper template for 'PrintWhitespaceList' template,
     which prints out one token in the list.
     Param(s):
            token (String) required
              Token to be printed
            compType (String) optional
              Schema component type of token, if applicable.
            isInList (boolean) optional
              If true, place token within 'li' tags.
            separator (String) optional
              Character(s) use to separate one token from another.
              Only used if 'isInList' is false.
              If none is provided, uses a space character, ' '.
            isFirst (boolean) optional
              If true, token is the first value in the list.
     -->
  <xsl:template name="PrintWhitespaceListToken">
     <xsl:param name="token"/>
     <xsl:param name="compType"/>
     <xsl:param name="isInList">false</xsl:param>
     <xsl:param name="separator"/>
     <xsl:param name="isFirst">true</xsl:param>

     <xsl:variable name="displayValue">
        <xsl:choose>
          <xsl:when test="$compType!=''">
             <xsl:call-template name="PrintRef">
                <xsl:with-param name="ref" select="normalize-space($token)"/>
                <xsl:with-param name="compType" select="$compType"/>
             </xsl:call-template>
          </xsl:when>
          <xsl:otherwise> 
             <xsl:value-of select="normalize-space($token)"/>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:variable>

     <xsl:choose>
        <xsl:when test="$isInList!='false'">
          <li><xsl:copy-of select="$displayValue"/></li>
        </xsl:when>
        <xsl:when test="$isFirst!='true'">
          <xsl:choose>
            <xsl:when test="$separator!=''">
                  <xsl:value-of select="$separator"/>
            </xsl:when>
            <xsl:otherwise> 
                  <xsl:text> </xsl:text>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:copy-of select="$displayValue"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:copy-of select="$displayValue"/>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <!--
     Print out a wildcard.
     Param(s):
            wildcard (Node) required
              Wildcard to be printed
     -->
  <xsl:template name="PrintWildcard">
     <xsl:param name="wildcard"/>

     <xsl:text>Allow any </xsl:text>
     <xsl:choose>
        <xsl:when test="local-name($wildcard)='anyAttribute'">
          <xsl:text>attributes</xsl:text>
        </xsl:when>
        <xsl:otherwise> 
          <xsl:text>elements</xsl:text>
        </xsl:otherwise>
     </xsl:choose>
     <xsl:text> from </xsl:text>

     <xsl:choose>
        <!-- ##any -->
        <xsl:when test="not($wildcard/@namespace) or normalize-space($wildcard/@namespace)='##any'">
          <xsl:text>any namespace</xsl:text>
        </xsl:when>
        <!-- ##other -->
        <xsl:when test="normalize-space($wildcard/@namespace)='##other'">
          <xsl:text>a namespace other than this schema's namespace</xsl:text>
        </xsl:when>
        <!-- ##targetNamespace, ##local, specific namespaces -->
        <xsl:otherwise>
          <!-- ##targetNamespace -->
          <xsl:variable name="hasTargetNS">
             <xsl:if test="contains($wildcard/@namespace, '##targetNamespace')">
                <xsl:text>true</xsl:text>
             </xsl:if>
          </xsl:variable>

          <!-- ##local -->
          <xsl:variable name="hasLocalNS">
             <xsl:if test="contains($wildcard/@namespace, '##local')">
                <xsl:text>true</xsl:text>
             </xsl:if>
          </xsl:variable>

          <!-- Specific namespaces -->
             <!-- Remove '##targetNamespace' string if any-->
          <xsl:variable name="temp">
             <xsl:choose>
                <xsl:when test="$hasTargetNS='true'">
                  <xsl:value-of select="concat(substring-before($wildcard/@namespace, '##targetNamespace'), substring-after($wildcard/@namespace, '##targetNamespace'))"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$wildcard/@namespace"/>
                </xsl:otherwise>
             </xsl:choose>
          </xsl:variable>
             <!-- Remove '##local' string if any -->
          <xsl:variable name="specificNS">
             <xsl:choose>
                <xsl:when test="$hasLocalNS='true'">
                  <xsl:value-of select="concat(substring-before($temp, '##local'), substring-after($temp, '##local'))"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$temp"/>
                </xsl:otherwise>
             </xsl:choose>
          </xsl:variable>
          <xsl:variable name="hasSpecificNS">
             <xsl:if test="normalize-space($specificNS)!=''">
                <xsl:text>true</xsl:text>
             </xsl:if>
          </xsl:variable>

          <xsl:if test="$hasLocalNS='true'">
             <xsl:text>no namespace</xsl:text>
          </xsl:if>

          <xsl:if test="$hasTargetNS='true'">
             <xsl:choose>
                <xsl:when test="$hasLocalNS='true' and $hasSpecificNS!='true'">
                  <xsl:text> and </xsl:text>
                </xsl:when>
                <xsl:when test="$hasLocalNS='true'">
                  <xsl:text>, </xsl:text>
                </xsl:when>
             </xsl:choose>
             <xsl:text>this schema's namespace</xsl:text>
          </xsl:if>

          <xsl:if test="$hasSpecificNS='true'">
             <xsl:choose>
                <xsl:when test="$hasTargetNS='true' and $hasLocalNS='true'">
                  <xsl:text>, and </xsl:text>
                </xsl:when>
                <xsl:when test="$hasTargetNS='true' or $hasLocalNS='true'">
                  <xsl:text> and </xsl:text>
                </xsl:when>
             </xsl:choose>
             <xsl:text>the following namespace(s): </xsl:text>
             <xsl:call-template name="PrintWhitespaceList">
                <xsl:with-param name="value" select="normalize-space($specificNS)"/>
                <xsl:with-param name="separator">, </xsl:with-param>
             </xsl:call-template>
          </xsl:if>
        </xsl:otherwise>
     </xsl:choose>

     <!-- Process contents -->
     <xsl:call-template name="PrintWildcardPC">
        <xsl:with-param name="wildcard" select="$wildcard"/>
     </xsl:call-template>

     <xsl:text>.</xsl:text>

     <!-- Print min/max occurs -->
     <xsl:if test="local-name($wildcard)='any'">
        <xsl:text> </xsl:text>
        <xsl:call-template name="PrintOccurs">
          <xsl:with-param name="component" select="$wildcard"/>
        </xsl:call-template>
     </xsl:if>
  </xsl:template>

  <!--
     Print out the value of process contents of a wildcard.
     Param(s):
            wildcard (Node) required
              Wildcard
     -->
  <xsl:template name="PrintWildcardPC">
     <xsl:param name="wildcard"/>

     <xsl:text> (</xsl:text>
     <xsl:choose>
        <xsl:when test="$wildcard/@processContents">
          <xsl:value-of select="normalize-space($wildcard/@processContents)"/>
        </xsl:when>
        <xsl:otherwise> 
          <xsl:text>strict</xsl:text>
        </xsl:otherwise>
     </xsl:choose>
     <xsl:text> validation)</xsl:text>
  </xsl:template>

  <!--
     Print out the pattern property for derivations by
     restriction on simple content.
     Param(s):
            simpleRestrict (Node) required
              'restriction' element
     -->
  <xsl:template name="PrintPatternFacet">
     <xsl:param name="simpleRestrict"/>

     <xsl:if test="$simpleRestrict/xsd:pattern">
        <em><xsl:text>pattern</xsl:text></em>
        <xsl:text> = </xsl:text>
        <xsl:value-of select="$simpleRestrict/xsd:pattern/@value"/>
     </xsl:if>
  </xsl:template>

  <!--
     Print out the total digits property for derivations by
     restriction on simple content.
     Param(s):
            simpleRestrict (Node) required
              'restriction' element
     -->
  <xsl:template name="PrintTotalDigitsFacet">
     <xsl:param name="simpleRestrict"/>

     <xsl:if test="$simpleRestrict/xsd:totalDigits">
        <em><xsl:text>total no. of digits</xsl:text></em>
        <xsl:text> = </xsl:text>
        <xsl:value-of select="$simpleRestrict/xsd:totalDigits/@value"/>
     </xsl:if>
  </xsl:template>

  <!--
     Print out the fraction digits property for derivations by
     restriction on simple content.
     Param(s):
            simpleRestrict (Node) required
              'restriction' element
     -->
  <xsl:template name="PrintFractionDigitsFacet">
     <xsl:param name="simpleRestrict"/>

     <xsl:if test="$simpleRestrict/xsd:fractionDigits">
        <em><xsl:text>no. of fraction digits</xsl:text></em>
        <xsl:text> = </xsl:text>
        <xsl:value-of select="$simpleRestrict/xsd:fractionDigits/@value"/>
     </xsl:if>
  </xsl:template>

  <!--
     Print out the enumeration list for derivations by
     restriction on simple content.
     Param(s):
            simpleRestrict (Node) required
              'restriction' element
     -->
  <xsl:template name="PrintEnumFacets">
     <xsl:param name="simpleRestrict"/>

     <xsl:if test="$simpleRestrict/xsd:enumeration">
        <em><xsl:text>value</xsl:text></em>
        <xsl:text> = </xsl:text>
        <xsl:text>{</xsl:text>

        <xsl:for-each select="$simpleRestrict/xsd:enumeration">
          <xsl:if test="position()!=1">
                <xsl:text>|</xsl:text>
          </xsl:if>
          <xsl:text>'</xsl:text>
          <xsl:value-of select="@value"/>
          <xsl:text>'</xsl:text>
        </xsl:for-each>

        <xsl:text>}</xsl:text>
     </xsl:if>
  </xsl:template>

  <!--
     Print out the length property for derivations by
     restriction on simple content.
     Param(s):
            simpleRestrict (Node) required
              'restriction' element
     -->
  <xsl:template name="PrintLengthFacets">
     <xsl:param name="simpleRestrict"/>

     <xsl:choose>
        <xsl:when test="$simpleRestrict/xsd:length">
          <em><xsl:text>length</xsl:text></em>
          <xsl:text> = </xsl:text>
          <xsl:value-of select="$simpleRestrict/xsd:length/@value"/>
        </xsl:when>
        <xsl:when test="$simpleRestrict/xsd:minLength">
          <em><xsl:text>length</xsl:text></em>
          <xsl:text> >= </xsl:text>
          <xsl:value-of select="$simpleRestrict/xsd:minLength/@value"/>
        </xsl:when>
        <xsl:when test="$simpleRestrict/xsd:maxLength">
          <em><xsl:text>length</xsl:text></em>
          <xsl:text> &lt;= </xsl:text>
          <xsl:value-of select="$simpleRestrict/xsd:maxLength/@value"/>
        </xsl:when>
     </xsl:choose>
  </xsl:template>

  <!--
     Print out the whitespace property for derivations by
     restriction on simple content.
     Param(s):
            simpleRestrict (Node) required
              'restriction' element
     -->
  <xsl:template name="PrintWhitespaceFacet">
     <xsl:param name="simpleRestrict"/>

     <xsl:variable name="ws" select="normalize-space(translate($simpleRestrict/xsd:whiteSpace/@value, 'ACELOPRSV', 'aceloprsv'))"/>
     <xsl:choose>
        <xsl:when test="$ws='preserve'">
          <em><xsl:text>whitespace</xsl:text></em>
          <xsl:text> = </xsl:text>
          <xsl:call-template name="PrintGlossaryTermRef">
             <xsl:with-param name="code">PreserveWS</xsl:with-param>
             <xsl:with-param name="term">preserve</xsl:with-param>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="$ws='replace'">
          <em><xsl:text>whitespace</xsl:text></em>
          <xsl:text> = </xsl:text>
          <xsl:call-template name="PrintGlossaryTermRef">
             <xsl:with-param name="code">ReplaceWS</xsl:with-param>
             <xsl:with-param name="term">replace</xsl:with-param>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="$ws='collapse'">
          <em><xsl:text>whitespace</xsl:text></em>
          <xsl:text> = </xsl:text>
          <xsl:call-template name="PrintGlossaryTermRef">
             <xsl:with-param name="code">CollapseWS</xsl:with-param>
             <xsl:with-param name="term">collapse</xsl:with-param>
          </xsl:call-template>
        </xsl:when>
     </xsl:choose>
  </xsl:template>

  <!--
     Print out the value ranges for derivations by
     restriction on simple content.
     Param(s):
            simpleRestrict (Node) required
              'restriction' element
     -->
  <xsl:template name="PrintRangeFacets">
     <xsl:param name="simpleRestrict"/>

     <xsl:choose>
        <xsl:when test="($simpleRestrict/xsd:minInclusive or $simpleRestrict/xsd:minExclusive) and ($simpleRestrict/xsd:maxInclusive or $simpleRestrict/xsd:maxExclusive)">
          <xsl:choose>
             <xsl:when test="$simpleRestrict/xsd:minInclusive">
                <xsl:value-of select="$simpleRestrict/xsd:minInclusive/@value"/>
                <xsl:text> &lt;= </xsl:text>
             </xsl:when>
             <xsl:otherwise> 
                <xsl:value-of select="$simpleRestrict/xsd:minExclusive/@value"/>
                <xsl:text> &lt; </xsl:text>
             </xsl:otherwise>
          </xsl:choose>
          <em><xsl:text>value</xsl:text></em>
          <xsl:choose>
             <xsl:when test="$simpleRestrict/xsd:maxInclusive">
                <xsl:text> &lt;= </xsl:text>
                <xsl:value-of select="$simpleRestrict/xsd:maxInclusive/@value"/>
             </xsl:when>
             <xsl:otherwise> 
                <xsl:text> &lt; </xsl:text>
                <xsl:value-of select="$simpleRestrict/xsd:maxExclusive/@value"/>
             </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:when test="$simpleRestrict/xsd:minInclusive">
          <em><xsl:text>value</xsl:text></em>
          <xsl:text> >= </xsl:text>
          <xsl:value-of select="$simpleRestrict/xsd:minInclusive/@value"/>
        </xsl:when>
        <xsl:when test="$simpleRestrict/xsd:minExclusive">
          <em><xsl:text>value</xsl:text></em>
          <xsl:text> > </xsl:text>
          <xsl:value-of select="$simpleRestrict/xsd:minExclusive/@value"/>
        </xsl:when>
        <xsl:when test="$simpleRestrict/xsd:maxInclusive">
          <em><xsl:text>value</xsl:text></em>
          <xsl:text> &lt;= </xsl:text>
          <xsl:value-of select="$simpleRestrict/xsd:maxInclusive/@value"/>
        </xsl:when>
        <xsl:when test="$simpleRestrict/xsd:maxExclusive">
          <em><xsl:text>value</xsl:text></em>
          <xsl:text> &lt; </xsl:text>
          <xsl:value-of select="$simpleRestrict/xsd:maxExclusive/@value"/>
        </xsl:when>
     </xsl:choose>
  </xsl:template>

  <!--
     Prints out JavaScript code.
     NOTE: Javascript code is placed within comments to make it
     work with current browsers. In strict XHTML, JavaScript code 
     should be placed within CDATA sections. However, most
     browsers generate a syntax error if the page contains 
     CDATA sections. Placing Javascript code within comments
     means that the code cannot contain two dashes.
     Param(s):
            code (Result Tree Fragment) required
                Javascript code
  -->
  <xsl:template name="PrintJSCode">
     <xsl:param name="code"/>

     <script type="text/javascript">
        <!-- If browsers start supporting CDATA sections, 
              uncomment the following piece of code. -->
        <!-- <xsl:text disable-output-escaping="yes">
&lt;![CDATA[
</xsl:text> -->

        <!-- If browsers start supporting CDATA sections, 
              remove the following piece of code. -->
        <xsl:text disable-output-escaping="yes">
&lt;!--
</xsl:text>

        <xsl:value-of select="$code" disable-output-escaping="yes"/>

        <!-- If browsers start supporting CDATA sections, 
              remove the following piece of code. -->
        <xsl:text disable-output-escaping="yes">
// --&gt;
</xsl:text>

        <!-- If browsers start supporting CDATA sections, 
              uncomment the following piece of code. -->
        <!-- <xsl:text disable-output-escaping="yes">
]]&gt;
</xsl:text> -->
     </script>
  </xsl:template>

  <!--
     Translates occurrences of a string
     in a piece of text with another string.
     Param(s):
            value (String) required
                Text to translate
            strToReplace (String) required
                String to be replaced
            replacementStr (String) required
                Replacement text
  -->
  <xsl:template name="TranslateStr">
     <xsl:param name="value"/>
     <xsl:param name="strToReplace"/>
     <xsl:param name="replacementStr"/>

     <xsl:if test="$value != ''">
        <xsl:variable name="beforeText" select="substring-before($value, $strToReplace)"/>
        <xsl:choose>
          <xsl:when test="$beforeText != ''">
             <xsl:value-of select="$beforeText"/>
             <xsl:value-of select="$replacementStr"/>
             <xsl:call-template name="TranslateStr">
                <xsl:with-param name="value" select="substring-after($value, $strToReplace)"/>
                <xsl:with-param name="strToReplace" select="$strToReplace"/>
                <xsl:with-param name="replacementStr" select="$replacementStr"/>
             </xsl:call-template>
          </xsl:when>
          <xsl:otherwise> 
             <xsl:value-of select="$value"/>
          </xsl:otherwise>
        </xsl:choose>
     </xsl:if>
  </xsl:template>

</xsl:stylesheet>
