<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" version="1.0" extension-element-prefixes="saxon" saxon:saxon="" zvon:zvon="http://zvon.org/schematron" wsdl:wsdl="http://schemas.xmlsoap.org/wsdl/" soap:soap="http://schemas.xmlsoap.org/wsdl/soap/">
   <aaa:output method="html" indent="yes"/>
   <aaa:key name="binding" match="wsdl:binding" use="@name"/>
   <aaa:key name="portType" match="wsdl:portType" use="@name"/>
   <aaa:key name="message" match="wsdl:message" use="@name"/>
   <aaa:template match="/">
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
                     <aaa:apply-templates select="/" mode="d0e12"/>
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
                     <aaa:apply-templates select="/" mode="d0e30"/>
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
                     <aaa:apply-templates select="/" mode="d0e76"/>
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
                     <aaa:apply-templates select="/" mode="d0e179"/>
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
                     <aaa:apply-templates select="/" mode="d0e214"/>
                  </td>
               </tr>
            </table>
            <br/>
         </body>
      </html>
      <aaa:message>
         <aaa:apply-templates select="/" mode="startDiagnostics"/>
      </aaa:message>
   </aaa:template>
   <aaa:template match="/" mode="d0e12">
      <aaa:if test="not(.=wsdl:definitions)">
         <div>Root element should be http://schemas.xmlsoap.org/wsdl/:definitions</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e12"/>
   </aaa:template>
   <aaa:template match="wsdl:definitions" mode="d0e12">
      <aaa:if test="not(@name)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> should have a name attribute</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e12"/>
   </aaa:template>
   <aaa:template match="wsdl:service" mode="d0e30">
      <aaa:if test="not(@name)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> A service should have a name attribute</div>
      </aaa:if>
      <aaa:if test="not(wsdl:port)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> A service should have at least one port element</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e30"/>
   </aaa:template>
   <aaa:template match="wsdl:service/wsdl:port" mode="d0e30">
      <aaa:if test="not(@name)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> A port should have a name attriubte</div>
      </aaa:if>
      <aaa:if test="not(@binding)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> A port should have a binding attriubte</div>
      </aaa:if>
      <aaa:if test="not(soap:address)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> A port should define a soap:address element</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e30"/>
   </aaa:template>
   <aaa:template match="wsdl:service/wsdl:port/@binding" mode="d0e30">
      <aaa:if test="not(key('binding',substring-after(.,':')) or key('binding',.))">
         <div>
 				
            <aaa:apply-templates select="." mode="fullPath"/> should reference a binding that exists</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e30"/>
   </aaa:template>
   <aaa:template match="wsdl:service/wsdl:port/soap:address" mode="d0e30">
      <aaa:if test="not(@location)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> a soap:address should have a location attribute</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e30"/>
   </aaa:template>
   <aaa:template match="wsdl:binding" mode="d0e76">
      <aaa:if test="not(@name)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> A binding should have a name attribute</div>
      </aaa:if>
      <aaa:if test="not(@type)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> A binding should have a type attribute</div>
      </aaa:if>
      <aaa:if test="not(soap:binding)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> A binding should have a soap binding element</div>
      </aaa:if>
      <aaa:if test="not(wsdl:operation)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> A binding should have 1 or more operations</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e76"/>
   </aaa:template>
   <aaa:template match="wsdl:binding/@type" mode="d0e76">
      <aaa:if test="not(key('portType',substring-after(.,':')) or key('portType',.))">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> should point to a portType that exists.</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e76"/>
   </aaa:template>
   <aaa:template match="soap:binding" mode="d0e76">
      <aaa:if test="not(normalize-space(@style)='rpc' or normalize-space(@style)='document')">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> a soap:binding style should be rpc or document</div>
      </aaa:if>
      <aaa:if test="not(@transport)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> a soap:binding should have a transport attribute</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e76"/>
   </aaa:template>
   <aaa:template match="wsdl:binding/wsdl:operation" mode="d0e76">
      <aaa:if test="not(@name)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> An operation should have a name attriubte</div>
      </aaa:if>
      <aaa:if test="not(wsdl:input)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> An operation should have an input element</div>
      </aaa:if>
      <aaa:if test="not(soap:operation)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> An operation should have a soap:operation element</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e76"/>
   </aaa:template>
   <aaa:template match="wsdl:binding/wsdl:operation/@name" mode="d0e76">
      <aaa:if test="not(key('portType',substring-after(../../@type,':'))/wsdl:operation/@name=. or key('portType',../../@type)/wsdl:operation/@name=.)">
         <div>
				
            <aaa:apply-templates select="." mode="fullPath"/> should point to an operation that exists in the portType '<aaa:value-of select="../../@type"/>'
			</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e76"/>
   </aaa:template>
   <aaa:template match="soap:operation" mode="d0e76">
      <aaa:if test="not(@soapAction)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> A soap:operation should have a soapAction attriubte</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e76"/>
   </aaa:template>
   <aaa:template match="wsdl:binding/wsdl:operation/wsdl:input" mode="d0e76">
      <aaa:if test="not(soap:body)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> An input should have a soap:body child</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e76"/>
   </aaa:template>
   <aaa:template match="wsdl:binding/wsdl:operation/wsdl:output" mode="d0e76">
      <aaa:if test="not(soap:body)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> An output should have a soap:body child</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e76"/>
   </aaa:template>
   <aaa:template match="soap:body" mode="d0e76">
      <aaa:if test="not(normalize-space(@use)='literal' or normalize-space(@use)='encoded')">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> the use attribute for soap:body should be 'literal' or 'encoded'</div>
      </aaa:if>
      <aaa:if test="not(normalize-space(@use)='literal' or (normalize-space(@use)='encoded' and @encodingStyle))">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> soap:body elements with an use='encoded' should have an encodingStyle attriubte</div>
      </aaa:if>
      <aaa:if test="not(@namespace)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> the soap:body element should have a namespace attribute</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e76"/>
   </aaa:template>
   <aaa:template match="wsdl:portType" mode="d0e179">
      <aaa:if test="not(@name)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> should have a name attribute</div>
      </aaa:if>
      <aaa:if test="not(wsdl:operation)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> should have at least 1 operation</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e179"/>
   </aaa:template>
   <aaa:template match="wsdl:portType/wsdl:operation" mode="d0e179">
      <aaa:if test="not(@name)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> should have a name attribute</div>
      </aaa:if>
      <aaa:if test="not(wsdl:input or wsdl:output)">
         <div> should have an input and/or an output element</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e179"/>
   </aaa:template>
   <aaa:template match="wsdl:portType/wsdl:operation/wsdl:input | wsdl:portType/wsdl:operation/wsdl:output" mode="d0e179">
      <aaa:if test="not(@message)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> should have a message attriubte</div>
      </aaa:if>
      <aaa:if test="not(key('message',substring-after(@message,':')) or key('message',@message))">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> should point to a message that exists.</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e179"/>
   </aaa:template>
   <aaa:template match="wsdl:message" mode="d0e214">
      <aaa:if test="not(@name)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> should have a name attribute</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e214"/>
   </aaa:template>
   <aaa:template match="wsdl:message/wsdl:part" mode="d0e214">
      <aaa:if test="not(@name)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> should have a name attribute</div>
      </aaa:if>
      <aaa:if test="not(@type or @element)">
         <div>
            <aaa:apply-templates select="." mode="fullPath"/> should have a 'type' or 'element' attriubte</div>
      </aaa:if>
      <aaa:apply-templates select="*|@*" mode="d0e214"/>
   </aaa:template>
   <aaa:template match="text()" mode="d0e12"/>
   <aaa:template match="*|@*" mode="d0e12">
      <aaa:apply-templates select="node()|@*" mode="d0e12"/>
   </aaa:template>
   <aaa:template match="text()" mode="dia_d0e12"/>
   <aaa:template match="*|@*" mode="dia_d0e12">
      <aaa:apply-templates select="node()|@*" mode="dia_d0e12"/>
   </aaa:template>
   <aaa:template match="text()" mode="d0e30"/>
   <aaa:template match="*|@*" mode="d0e30">
      <aaa:apply-templates select="node()|@*" mode="d0e30"/>
   </aaa:template>
   <aaa:template match="text()" mode="dia_d0e30"/>
   <aaa:template match="*|@*" mode="dia_d0e30">
      <aaa:apply-templates select="node()|@*" mode="dia_d0e30"/>
   </aaa:template>
   <aaa:template match="text()" mode="d0e76"/>
   <aaa:template match="*|@*" mode="d0e76">
      <aaa:apply-templates select="node()|@*" mode="d0e76"/>
   </aaa:template>
   <aaa:template match="text()" mode="dia_d0e76"/>
   <aaa:template match="*|@*" mode="dia_d0e76">
      <aaa:apply-templates select="node()|@*" mode="dia_d0e76"/>
   </aaa:template>
   <aaa:template match="text()" mode="d0e179"/>
   <aaa:template match="*|@*" mode="d0e179">
      <aaa:apply-templates select="node()|@*" mode="d0e179"/>
   </aaa:template>
   <aaa:template match="text()" mode="dia_d0e179"/>
   <aaa:template match="*|@*" mode="dia_d0e179">
      <aaa:apply-templates select="node()|@*" mode="dia_d0e179"/>
   </aaa:template>
   <aaa:template match="text()" mode="d0e214"/>
   <aaa:template match="*|@*" mode="d0e214">
      <aaa:apply-templates select="node()|@*" mode="d0e214"/>
   </aaa:template>
   <aaa:template match="text()" mode="dia_d0e214"/>
   <aaa:template match="*|@*" mode="dia_d0e214">
      <aaa:apply-templates select="node()|@*" mode="dia_d0e214"/>
   </aaa:template>
   <aaa:template match="/" mode="startDiagnostics">
      <aaa:apply-templates select="/" mode="dia_d0e12"/>
      <aaa:apply-templates select="/" mode="dia_d0e30"/>
      <aaa:apply-templates select="/" mode="dia_d0e76"/>
      <aaa:apply-templates select="/" mode="dia_d0e179"/>
      <aaa:apply-templates select="/" mode="dia_d0e214"/>
   </aaa:template>
   <aaa:template match="*|@*" mode="fullPath">
      <aaa:apply-templates select="parent::*" mode="fullPath"/>
      <aaa:text>/</aaa:text>
      <aaa:if test="count(. | ../@*) = count(../@*)">@</aaa:if>
      <aaa:value-of select="name()"/>
      <aaa:if test="preceding-sibling::*[name()=name(current())] or following-sibling::*[name()=name(current())]">
         <aaa:text>[</aaa:text>
         <aaa:value-of select="1+count(preceding-sibling::*[name()=name(current())])"/>
         <aaa:text>]</aaa:text>
      </aaa:if>
   </aaa:template>
</aaa:stylesheet>