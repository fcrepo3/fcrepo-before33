<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:aaa="http://www.w3.org/1999/XSL/Transform" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sch="http://www.ascc.net/xml/schematron" xmlns:zvon="http://zvon.org/schematron" xmlns:saxon="http://icl.com/saxon" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" version="1.0" extension-element-prefixes="saxon" saxon:saxon="" zvon:zvon="http://zvon.org/schematron" wsdl:wsdl="http://schemas.xmlsoap.org/wsdl/" soap:soap="http://schemas.xmlsoap.org/wsdl/soap/">
   <xsl:output method="html" indent="yes"/>
   <xsl:key name="binding" match="wsdl:binding" use="@name"/>
   <xsl:key name="portType" match="wsdl:portType" use="@name"/>
   <xsl:key name="message" match="wsdl:message" use="@name"/>
   <xsl:template match="/">
      <html>
         <head>
            <title>Schematron validation</title>
         </head>
         <body>
            <h2>A Schematron Schema for SOAP based WSDL</h2>
            <table border="1">
               <tr>
                  <th>Pattern: Definitions Checks</th>
               </tr>
               <tr>
                  <td>
                     <xsl:apply-templates select="/" mode="d0e12"/>
                  </td>
               </tr>
            </table>
            <br/>
            <table border="1">
               <tr>
                  <th>Pattern: Service Checks</th>
               </tr>
               <tr>
                  <td>
                     <xsl:apply-templates select="/" mode="d0e30"/>
                  </td>
               </tr>
            </table>
            <br/>
            <table border="1">
               <tr>
                  <th>Pattern: Binding Checks</th>
               </tr>
               <tr>
                  <td>
                     <xsl:apply-templates select="/" mode="d0e76"/>
                  </td>
               </tr>
            </table>
            <br/>
            <table border="1">
               <tr>
                  <th>Pattern: portType checks</th>
               </tr>
               <tr>
                  <td>
                     <xsl:apply-templates select="/" mode="d0e179"/>
                  </td>
               </tr>
            </table>
            <br/>
            <table border="1">
               <tr>
                  <th>Pattern: message checks</th>
               </tr>
               <tr>
                  <td>
                     <xsl:apply-templates select="/" mode="d0e214"/>
                  </td>
               </tr>
            </table>
            <br/>
         </body>
      </html>
      <xsl:message>
         <xsl:apply-templates select="/" mode="startDiagnostics"/>
      </xsl:message>
   </xsl:template>
   <xsl:template match="/" mode="d0e12">
      <xsl:if test="not(.=wsdl:definitions)">
         <div>Root element should be http://schemas.xmlsoap.org/wsdl/:definitions</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e12"/>
   </xsl:template>
   <xsl:template match="wsdl:definitions" mode="d0e12">
      <xsl:if test="not(@name)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> should have a name attribute</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e12"/>
   </xsl:template>
   <xsl:template match="wsdl:service" mode="d0e30">
      <xsl:if test="not(@name)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> A service should have a name attribute</div>
      </xsl:if>
      <xsl:if test="not(wsdl:port)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> A service should have at least one port element</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e30"/>
   </xsl:template>
   <xsl:template match="wsdl:service/wsdl:port" mode="d0e30">
      <xsl:if test="not(@name)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> A port should have a name attriubte</div>
      </xsl:if>
      <xsl:if test="not(@binding)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> A port should have a binding attriubte</div>
      </xsl:if>
      <xsl:if test="not(soap:address)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> A port should define a soap:address element</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e30"/>
   </xsl:template>
   <xsl:template match="wsdl:service/wsdl:port/@binding" mode="d0e30">
      <xsl:if test="not(key('binding',substring-after(.,':')) or key('binding',.))">
         <div>
 				
            <xsl:apply-templates select="." mode="fullPath"/> should reference a binding that exists</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e30"/>
   </xsl:template>
   <xsl:template match="wsdl:service/wsdl:port/soap:address" mode="d0e30">
      <xsl:if test="not(@location)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> a soap:address should have a location attribute</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e30"/>
   </xsl:template>
   <xsl:template match="wsdl:binding" mode="d0e76">
      <xsl:if test="not(@name)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> A binding should have a name attribute</div>
      </xsl:if>
      <xsl:if test="not(@type)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> A binding should have a type attribute</div>
      </xsl:if>
      <xsl:if test="not(soap:binding)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> A binding should have a soap binding element</div>
      </xsl:if>
      <xsl:if test="not(wsdl:operation)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> A binding should have 1 or more operations</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e76"/>
   </xsl:template>
   <xsl:template match="wsdl:binding/@type" mode="d0e76">
      <xsl:if test="not(key('portType',substring-after(.,':')) or key('portType',.))">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> should point to a portType that exists.</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e76"/>
   </xsl:template>
   <xsl:template match="soap:binding" mode="d0e76">
      <xsl:if test="not(normalize-space(@style)='rpc' or normalize-space(@style)='document')">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> a soap:binding style should be rpc or document</div>
      </xsl:if>
      <xsl:if test="not(@transport)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> a soap:binding should have a transport attribute</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e76"/>
   </xsl:template>
   <xsl:template match="wsdl:binding/wsdl:operation" mode="d0e76">
      <xsl:if test="not(@name)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> An operation should have a name attriubte</div>
      </xsl:if>
      <xsl:if test="not(wsdl:input)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> An operation should have an input element</div>
      </xsl:if>
      <xsl:if test="not(soap:operation)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> An operation should have a soap:operation element</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e76"/>
   </xsl:template>
   <xsl:template match="wsdl:binding/wsdl:operation/@name" mode="d0e76">
      <xsl:if test="not(key('portType',substring-after(../../@type,':'))/wsdl:operation/@name=. or key('portType',../../@type)/wsdl:operation/@name=.)">
         <div>
				
            <xsl:apply-templates select="." mode="fullPath"/> should point to an operation that exists in the portType '<xsl:value-of select="../../@type"/>'
			</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e76"/>
   </xsl:template>
   <xsl:template match="soap:operation" mode="d0e76">
      <xsl:if test="not(@soapAction)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> A soap:operation should have a soapAction attriubte</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e76"/>
   </xsl:template>
   <xsl:template match="wsdl:binding/wsdl:operation/wsdl:input" mode="d0e76">
      <xsl:if test="not(soap:body)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> An input should have a soap:body child</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e76"/>
   </xsl:template>
   <xsl:template match="wsdl:binding/wsdl:operation/wsdl:output" mode="d0e76">
      <xsl:if test="not(soap:body)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> An output should have a soap:body child</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e76"/>
   </xsl:template>
   <xsl:template match="soap:body" mode="d0e76">
      <xsl:if test="not(normalize-space(@use)='literal' or normalize-space(@use)='encoded')">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> the use attribute for soap:body should be 'literal' or 'encoded'</div>
      </xsl:if>
      <xsl:if test="not(normalize-space(@use)='literal' or (normalize-space(@use)='encoded' and @encodingStyle))">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> soap:body elements with an use='encoded' should have an encodingStyle attriubte</div>
      </xsl:if>
      <xsl:if test="not(@namespace)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> the soap:body element should have a namespace attribute</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e76"/>
   </xsl:template>
   <xsl:template match="wsdl:portType" mode="d0e179">
      <xsl:if test="not(@name)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> should have a name attribute</div>
      </xsl:if>
      <xsl:if test="not(wsdl:operation)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> should have at least 1 operation</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e179"/>
   </xsl:template>
   <xsl:template match="wsdl:portType/wsdl:operation" mode="d0e179">
      <xsl:if test="not(@name)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> should have a name attribute</div>
      </xsl:if>
      <xsl:if test="not(wsdl:input or wsdl:output)">
         <div> should have an input and/or an output element</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e179"/>
   </xsl:template>
   <xsl:template match="wsdl:portType/wsdl:operation/wsdl:input | wsdl:portType/wsdl:operation/wsdl:output" mode="d0e179">
      <xsl:if test="not(@message)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> should have a message attriubte</div>
      </xsl:if>
      <xsl:if test="not(key('message',substring-after(@message,':')) or key('message',@message))">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> should point to a message that exists.</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e179"/>
   </xsl:template>
   <xsl:template match="wsdl:message" mode="d0e214">
      <xsl:if test="not(@name)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> should have a name attribute</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e214"/>
   </xsl:template>
   <xsl:template match="wsdl:message/wsdl:part" mode="d0e214">
      <xsl:if test="not(@name)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> should have a name attribute</div>
      </xsl:if>
      <xsl:if test="not(@type or @element)">
         <div>
            <xsl:apply-templates select="." mode="fullPath"/> should have a 'type' or 'element' attriubte</div>
      </xsl:if>
      <xsl:apply-templates select="*|@*" mode="d0e214"/>
   </xsl:template>
   <xsl:template match="text()" mode="d0e12"/>
   <xsl:template match="*|@*" mode="d0e12">
      <xsl:apply-templates select="node()|@*" mode="d0e12"/>
   </xsl:template>
   <xsl:template match="text()" mode="dia_d0e12"/>
   <xsl:template match="*|@*" mode="dia_d0e12">
      <xsl:apply-templates select="node()|@*" mode="dia_d0e12"/>
   </xsl:template>
   <xsl:template match="text()" mode="d0e30"/>
   <xsl:template match="*|@*" mode="d0e30">
      <xsl:apply-templates select="node()|@*" mode="d0e30"/>
   </xsl:template>
   <xsl:template match="text()" mode="dia_d0e30"/>
   <xsl:template match="*|@*" mode="dia_d0e30">
      <xsl:apply-templates select="node()|@*" mode="dia_d0e30"/>
   </xsl:template>
   <xsl:template match="text()" mode="d0e76"/>
   <xsl:template match="*|@*" mode="d0e76">
      <xsl:apply-templates select="node()|@*" mode="d0e76"/>
   </xsl:template>
   <xsl:template match="text()" mode="dia_d0e76"/>
   <xsl:template match="*|@*" mode="dia_d0e76">
      <xsl:apply-templates select="node()|@*" mode="dia_d0e76"/>
   </xsl:template>
   <xsl:template match="text()" mode="d0e179"/>
   <xsl:template match="*|@*" mode="d0e179">
      <xsl:apply-templates select="node()|@*" mode="d0e179"/>
   </xsl:template>
   <xsl:template match="text()" mode="dia_d0e179"/>
   <xsl:template match="*|@*" mode="dia_d0e179">
      <xsl:apply-templates select="node()|@*" mode="dia_d0e179"/>
   </xsl:template>
   <xsl:template match="text()" mode="d0e214"/>
   <xsl:template match="*|@*" mode="d0e214">
      <xsl:apply-templates select="node()|@*" mode="d0e214"/>
   </xsl:template>
   <xsl:template match="text()" mode="dia_d0e214"/>
   <xsl:template match="*|@*" mode="dia_d0e214">
      <xsl:apply-templates select="node()|@*" mode="dia_d0e214"/>
   </xsl:template>
   <xsl:template match="/" mode="startDiagnostics">
      <xsl:apply-templates select="/" mode="dia_d0e12"/>
      <xsl:apply-templates select="/" mode="dia_d0e30"/>
      <xsl:apply-templates select="/" mode="dia_d0e76"/>
      <xsl:apply-templates select="/" mode="dia_d0e179"/>
      <xsl:apply-templates select="/" mode="dia_d0e214"/>
   </xsl:template>
   <xsl:template match="*|@*" mode="fullPath">
      <xsl:apply-templates select="parent::*" mode="fullPath"/>
      <xsl:text>/</xsl:text>
      <xsl:if test="count(. | ../@*) = count(../@*)">@</xsl:if>
      <xsl:value-of select="name()"/>
      <xsl:if test="preceding-sibling::*[name()=name(current())] or following-sibling::*[name()=name(current())]">
         <xsl:text>[</xsl:text>
         <xsl:value-of select="1+count(preceding-sibling::*[name()=name(current())])"/>
         <xsl:text>]</xsl:text>
      </xsl:if>
   </xsl:template>
</xsl:stylesheet>