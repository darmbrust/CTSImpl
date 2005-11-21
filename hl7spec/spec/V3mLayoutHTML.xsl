<?xml version="1.0"?>
<!-- edited with XMLSPY v5 rel. 4 U (http://www.xmlspy.com) by XMLSPY 5 Enterprise Ed. (HL7 Publications TC) -->
<!-- File: v3pub-online-Woody-1020 
This is Paul's 9/10/01 file with a LIMITED number of changes as detailed below.
-->
<!-- PROBLEMS 30/01/01Template match="xtermref" href not appearing within text output -->
<!-- CHANGES DPW 24/10/01: Template match=xspecref : Document and href paths altered relative to new build structure -->
<!DOCTYPE xsl:stylesheet [
	<!ENTITY copy "&#169;">
	<!ENTITY nbsp "&#160;">
	<!ENTITY reg "&#174;">
	<!ENTITY sect '&#xa7;'>
	<!ENTITY % v3ent_files SYSTEM "V3m.ent">
	%v3ent_files;
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/TR/WD-xlink" xmlns:saxon="http://icl.com/saxon" extension-element-prefixes="saxon" exclude-result-prefixes="xlink" version="1.0">
	<xsl:import href="v3mRefUtilityHTML.xsl"/>
	<xsl:output method="html" indent="yes" encoding="ISO-8859-1" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>
	<xsl:variable name="BaseFileName" select="substring(/spec/@sectionCode,1,6)"/>
	<xsl:param name="datestamp"/>
	<xsl:param name="timestamp"/>
	<xsl:template match="spec" mode="css"/>
	<xsl:strip-space elements="*"/>
	<xsl:template match="/" name="root">
		<html>
			<head>
				<!--
	call-template moved inside <head>
  -->
				<xsl:call-template name="makeJavascriptHeader"/>
				<title>
					<xsl:value-of select="//header/title"/>
					<!-- GWB Change: In Paul's, this was 
						<xsl:value-of select="//header/title"/> but that failed to pick up the relevant sections in 
						documents like the RIM and Vocabulary that do NOT have "spec" as the root, which are 
						rooted in 'RIM' and 'VOC', respectively -->
				</title>
				<!-- HRS - added ability to put a shortcut into heading -->
				<xsl:for-each select="//shortcut[1]">
					<link rel="SHORTCUT ICON" href="graphics/{@source}"/>
				</xsl:for-each>
				<!-- HRS end -->
				<link rel="stylesheet" type="text/css" href="V3m.css"/>
				<style type="text/css">
					<xsl:apply-templates select="." mode="css"/>
				</style>
			</head>
			<body>
				<table class="header">
					<!--
	make sure the spec template isn't called twice by
	using the for-each trick to make it the context node
 -->
					<xsl:choose>
						<xsl:when test="spec">
							<xsl:for-each select="spec">
								<xsl:call-template name="spec"/>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="spec"/>
						</xsl:otherwise>
					</xsl:choose>
					<!-- GWB Change: In Paul's, this had only the "for-each" loop (the 3rd through 5th
					lines below <xsl:choose> above.  As with the earlier concern, however, this fails to pick up
					 the root in documents like the RIM and Vocabulary that do NOT have "spec" as the root, but
					 are rooted in 'RIM' and 'VOC', respectively.  I believe the use of choose provides for both 
					 the assurance of a single path through 'spec', as Paul seeks, and the RIM and Vocab.  -->
				</table>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="spec" name="spec">
		<xsl:apply-templates/>
		<xsl:if test="//footnote">
			<tr>
				<!-- pvb: nowrap="" ==> nowrap="nowrap" -->
				<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
				<td class="number_col" nowrap="nowrap">&nbsp; </td>
				<td class="body_col">
					<h2>Endnotes</h2>
					<ol>
						<xsl:for-each select="//footnote">
							<xsl:variable name="anchor">
								<xsl:number level="any" count="footnote" format="1"/>
							</xsl:variable>
							<li>
								<a name="fn{$anchor}"/>
							[<a href="#fn-src{$anchor}">source</a>]
							<xsl:apply-templates select="." mode="footnote-text"/>
							</li>
						</xsl:for-each>
					</ol>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	<!--
	I wish I could get this:
		if /RIM then stuff out of here
  -->
	<xsl:template match="header">
		<!--<div class="head">-->
		<tr>
			<td width="100%" colspan="3">
				<h1>
					<xsl:value-of select="title"/>
				</h1>
			</td>
		</tr>
		<tr>
			<td width="100%" colspan="3">
				<xsl:if test="/RIM">
					<p>
					Version: <xsl:value-of select="version"/> (<xsl:value-of select="date"/>)
					<br/>
					ModelID: <xsl:value-of select="../@modelID"/>
					</p>
				</xsl:if>
			</td>
		</tr>
		<tr>
			<td width="100%" colspan="3">
				<xsl:apply-templates select="authlist"/>
			</td>
		</tr>
		<tr>
			<td width="100%" colspan="3">
				<xsl:apply-templates select="ballot"/>
			</td>
		</tr>
		<tr>
			<td width="100%" colspan="3">
				<xsl:call-template name="copyright"/>
			</td>
		</tr>
		<!--
			<xsl:apply-templates select="abstract"/>
-->
		<tr>
			<td width="100%" colspan="3">
				<xsl:apply-templates select="status"/>
			</td>
		</tr>
		<tr>
			<td width="100%" colspan="3">
				<xsl:if test="/RIM">
					<xsl:apply-templates select="legalese"/>
					<a name="{../@id}"/>
				</xsl:if>
				<hr title="Separator for header"/>
				<!--</td></tr></table>-->
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="ballot">
		<!--<p>
			<xsl:value-of select="@type"/> Ballot # <xsl:value-of select="@number"/>
		</p>-->
	</xsl:template>
	<xsl:template match="authlist">
		<!--close main table, as this is the introduction-->
		<table border="0" width="100%">
			<xsl:for-each select="author">
				<tr>
					<xsl:for-each select="role">
						<td width="225" valign="top" align="left" nowrap="nowrap">
							<xsl:value-of select="."/>
						</td>
					</xsl:for-each>
					<td width="95%" valign="top" align="left" nowrap="nowrap">
						<xsl:for-each select="name">
							<xsl:value-of select="."/>
							<br/>
						</xsl:for-each>
						<xsl:for-each select="email">
							<xsl:element name="a">
								<xsl:attribute name="href">mailto:<xsl:value-of select="@href"/></xsl:attribute>
								<xsl:value-of select="@href"/>
							</xsl:element>
							<br/>
						</xsl:for-each>
						<xsl:for-each select="affiliation">
							<xsl:value-of select="."/>
						</xsl:for-each>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	<xsl:template match="status">
		<h2>
			<a name="status">Status of this document</a>
		</h2>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="body">
		<tr>
			<td width="100%" colspan="3">
				<h2>
					<a name="contents">Table of Contents</a>
				</h2>
				<!--If this is a foundation document, it will call the toc in Layout.  If it is a domain document, it will call the 4 in Domain.-->
				<xsl:call-template name="toc"/>
				<hr/>
			</td>
		</tr>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="front">
		<tr>
			<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
			<td class="number_col" nowrap="nowrap">&nbsp; </td>
			<td class="body_col">
				<xsl:apply-templates/>
				<hr title="Separator from body"/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="back">
		<tr>
			<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
			<td class="number_col" nowrap="nowrap">&nbsp; </td>
			<td class="body_col">
				<hr title="Separator from footer"/>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="div1|div2|div3|div4">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="div1/text()|div2/text()|div3/text()|div4/text()">
		<tr>
			<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
			<td class="number_col" nowrap="nowrap">&nbsp; </td>
			<td class="body_col">
				<xsl:apply-templates select="text()"/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="div1/graphic|div2/graphic|div3/graphic|div4/graphic">
		<tr>
			<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
			<td class="number_col" nowrap="nowrap">&nbsp; </td>
			<td class="body_col">
				<xsl:call-template name="inlineGraphic"/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="div1/description|div2/description|div3/description|div4/description">
		<tr>
			<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
			<td class="number_col" nowrap="nowrap">&nbsp; </td>
			<td class="body_col">
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="div1/head|inform-div1/head">
		<xsl:call-template name="head">
			<xsl:with-param name="HeaderLevel" select="'h2'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="div2/head">
		<xsl:call-template name="head">
			<xsl:with-param name="HeaderLevel" select="'h3'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="div3/head">
		<xsl:call-template name="head">
			<xsl:with-param name="HeaderLevel" select="'h4'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="div4/head">
		<xsl:call-template name="head">
			<xsl:with-param name="HeaderLevel" select="'h5'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="head">
		<xsl:param name="HeaderLevel"/>
		<xsl:if test="not(div1[1])">
			<xsl:call-template name="insert_space"/>
		</xsl:if>
		<tr>
			<td class="graphic_col" nowrap="nowrap">
				<xsl:call-template name="showBallotStatus">
					<xsl:with-param name="checkNode" select="../."/>
				</xsl:call-template>
			</td>
			<td class="number_col" nowrap="nowrap">
				<xsl:element name="{$HeaderLevel}">
					<xsl:for-each select="..">
						<xsl:call-template name="insertID"/>
						<xsl:apply-templates select="." mode="number"/>
					</xsl:for-each>
				</xsl:element>
			</td>
			<td class="body_col">
				<xsl:element name="{$HeaderLevel}">
					<xsl:apply-templates/>
					<xsl:call-template name="inform"/>
				</xsl:element>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="divn-head">
		<xsl:param name="HeaderLevel"/>
		<xsl:param name="HeaderTag"/>
		<xsl:if test="not(div1[1])">
			<xsl:call-template name="insert_space"/>
		</xsl:if>
		<tr>
			<td class="graphic_col" nowrap="nowrap">
				<xsl:call-template name="showBallotStatus"/>
			</td>
			<td class="number_col" nowrap="nowrap">
				<!--xsl:if test="ancestor::back">
							<xsl:value-of select="'A.'"/>
						</xsl:if-->
				<xsl:element name="{$HeaderLevel}">
					<xsl:call-template name="insertID"/>
					<xsl:apply-templates select="." mode="number"/>
				</xsl:element>
			</td>
			<td class="body_col">
				<xsl:element name="{$HeaderLevel}">
					<xsl:value-of select="@name"/>
					<xsl:text/>
					<xsl:value-of select="($HeaderTag)"/>
					<xsl:call-template name="inform"/>
				</xsl:element>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="custom-head">
		<xsl:param name="HeaderLevel"/>
		<xsl:param name="HeaderTag"/>
		<xsl:if test="not(div1[1])">
			<xsl:call-template name="insert_space"/>
		</xsl:if>
		<tr>
			<td class="graphic_col" nowrap="nowrap">
				<xsl:call-template name="showBallotStatus"/>
			</td>
			<td class="number_col" nowrap="nowrap">
				<xsl:element name="{$HeaderLevel}">
					<xsl:call-template name="insertID"/>
					<xsl:apply-templates select="." mode="number"/>
				</xsl:element>
			</td>
			<td class="body_col">
				<xsl:element name="{$HeaderLevel}">
					<xsl:value-of select="$HeaderTag"/>
					<xsl:call-template name="inform"/>
				</xsl:element>
			</td>
		</tr>
	</xsl:template>
	<!-- pvb: added prio to avoid ambiguity with HST's rule on line 383 -->
	<xsl:template match="div1/p|div2/p|div3/p|div4/p" priority="100">
		<tr>
			<td class="graphic_col" nowrap="nowrap">&nbsp;</td>
			<td class="number_col" nowrap="nowrap">&nbsp;</td>
			<td class="body_col">
				<p>
					<xsl:apply-templates/>
				</p>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="p">
		<xsl:choose>
			<xsl:when test="ancestor::leafTerm or ancestor::specDomain or ancestor::abstDomain or ancestor::vocSet">
				<!-- provide small text in Voc tables | | -->
				<p class="vocDesc">
					<xsl:apply-templates/>
				</p>
			</xsl:when>
			<xsl:otherwise>
				<p>
					<xsl:apply-templates/>
				</p>
			</xsl:otherwise>
		</xsl:choose>
		<!--</p><br/><br/>-->
	</xsl:template>
	<!-- HST: try to cope: DTD allows nesting of blocks within p -->
	<xsl:template match="p[p|list|slist|glist|orglist|blist|note|issue]">
		<xsl:call-template name="blocks">
			<xsl:with-param name="nodes" select="*|text()"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="blocks">
		<xsl:param name="nodes"/>
		<xsl:if test="$nodes[position()=1]">
			<xsl:choose>
				<!-- blocks go through unchanged -->
				<xsl:when test="$nodes[position()=1 and
						(self::p or self::list or
						self::slist or self::glist or self::orglist
						or self::blist or self::note or self::issue)]">
					<xsl:apply-templates select="$nodes[position()=1]"/>
					<xsl:call-template name="blocks">
						<xsl:with-param name="nodes" select="$nodes[position()>1]"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<!-- text or mixins get joined together and wrapped in <p>..</p> -->
					<p>
						<xsl:apply-templates select="$nodes[position()=1]"/>
						<xsl:call-template name="mixins">
							<xsl:with-param name="nodes" select="$nodes[position()>1]"/>
						</xsl:call-template>
					</p>
					<!--
	need position of first block, if any, but don't see
	how to get it, so use recursion again.
  -->
					<xsl:call-template name="findblock">
						<xsl:with-param name="nodes" select="$nodes[position()>1]"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<xsl:template name="mixins">
		<xsl:param name="nodes"/>
		<xsl:if test="$nodes[position()=1]">
			<xsl:choose>
				<xsl:when test="$nodes[position()=1 and
						(self::p or self::list or
						self::slist or self::glist or self::orglist or
						self::blist or self::note or self::issue)]"/>
				<xsl:otherwise>
					<xsl:apply-templates select="$nodes[position()=1]"/>
					<xsl:call-template name="mixins">
						<xsl:with-param name="nodes" select="$nodes[position()>1]"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<xsl:template name="findblock">
		<xsl:param name="nodes"/>
		<xsl:if test="$nodes[position()=1]">
			<xsl:choose>
				<xsl:when test="$nodes[position()=1 and
						(self::p or self::list or
						self::slist or self::glist or self::orglist or
						self::blist or self::note or self::issue)]">
					<xsl:call-template name="blocks">
						<xsl:with-param name="nodes" select="$nodes"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="findblock">
						<xsl:with-param name="nodes" select="$nodes[position()>1]"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<xsl:template match="eg" name="egbody">
		<xsl:choose>
			<xsl:when test="parent::div1|parent::div2|parent::div3">
				<tr>
					<td class="graphic_col" nowrap="nowrap">&nbsp;</td>
					<td class="number_col" nowrap="nowrap">&nbsp;</td>
					<td class="body_col">
						<xsl:call-template name="makePreTag"/>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="makePreTag"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="makePreTag">
		<pre>
			<xsl:if test="@role='error'">
				<xsl:attribute name="style">color: red</xsl:attribute>
			</xsl:if>
			<xsl:if test="@role='example'">
				<xsl:attribute name="class">example</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="@text">
					<xsl:value-of select="document(@text)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</pre>
	</xsl:template>
	<xsl:template match="ednote">
		<tr>
			<td class="graphic_col" nowrap="nowrap">&nbsp;</td>
			<td class="number_col" nowrap="nowrap">&nbsp;</td>
			<td class="body_col">
				<blockquote>
					<p>
						<b>Ed. Note: </b>
						<xsl:apply-templates/>
					</p>
				</blockquote>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="edtext">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="issue">
		<xsl:call-template name="insertID"/>
		<tr>
			<td class="graphic_col" nowrap="nowrap">&nbsp;</td>
			<td class="number_col" nowrap="nowrap">&nbsp;</td>
			<td class="body_col">
				<blockquote>
					<p>
						<b>Issue (<xsl:value-of select="@id"/>): </b>
						<xsl:apply-templates/>
					</p>
				</blockquote>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="note">
		<tr>
			<td class="graphic_col" nowrap="nowrap">&nbsp;</td>
			<td class="number_col" nowrap="nowrap">&nbsp;</td>
			<td class="body_col">
				<blockquote>
					<b>NOTE: </b>
					<xsl:apply-templates/>
				</blockquote>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="issue/p|note/p">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="loc">
		<a href="{@href}">
			<xsl:choose>
				<xsl:when test="* | text()">
					<xsl:apply-templates/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@href"/>
				</xsl:otherwise>
			</xsl:choose>
		</a>
	</xsl:template>
	<xsl:template match="bibref">
		<a href="#{@ref}">
			<!--
				12/28/2001 MAC 
				input used to be xxx [xxx] in XML source files so output would be duplicated. 
				XML content should simply bibref the id(@ref)/@key nodes to get the bib text for 
				output and output it as a link.
				<xsl:text/>[<xsl:value-of select="id(@ref)/@key"/>]<xsl:text/>-->
			<xsl:value-of select="id(@ref)/@key"/>
		</a>
	</xsl:template>
	<xsl:template match="specref">
		<a href="#{@ref}">
			<xsl:value-of select="id(@ref)/head"/>
			(&sect;
			<xsl:for-each select="id(@ref)">
				<xsl:apply-templates select="." mode="number"/>
			</xsl:for-each>)</a>
	</xsl:template>
	<xsl:template match="termref">
		<a class="termref" href="#{@ref}">
			<xsl:apply-templates/>
		</a>
	</xsl:template>
	<!--	<xsl:template match="dtref">
		<xsl:variable name="filenameonly" select="&datatypesfile;"/>
		<xsl:variable name="thisref" select="@ref"/>
		<xsl:variable name="htmfile" select="concat($filenameonly, '.htm')"/>
		<xsl:variable name="xmlfile" select="concat($filenameonly, '.xml')"/>
		<a>
			<xsl:attribute name="class">xtermref</xsl:attribute>
			<xsl:attribute name="href"><xsl:value-of select="concat($htmfile,'#')"/><xsl:value-of select="(@ref)"/></xsl:attribute>
			<xsl:apply-templates select="document($xmlfile)//dt[@ref=$thisref]/node()"/>
			<xsl:apply-templates/>
		</a>
	</xsl:template> -->
	<!-- HRS - new type added to include idl and other code snippets -->
	<xsl:template match="codefragment">
		<xsl:variable name="section" select="@section"/>
		<pre>
			<xsl:copy-of select="document(concat('code/',@file))//*[local-name()=$section]/node()"/>
		</pre>
	</xsl:template>
	<!-- HRS end -->
	<xsl:template match="dtref">
		<xsl:variable name="genericOrCollection" select='substring-before(@ref,"_")'/>
		<xsl:choose>
			<xsl:when test='$genericOrCollection=""'>
				<a class="ilink" href="#{@ref}">
					<xsl:value-of select="@ref"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="type" select='substring-after(@ref,"_")'/>
				<a class="ilink" href="#{$genericOrCollection}">
					<xsl:value-of select="$genericOrCollection"/>
				</a>&lt;
				<xsl:choose>
					<xsl:when test='$type="T"'>
						T
					</xsl:when>
					<xsl:otherwise>
						<a class="ilink" href="#{$type}">
							<xsl:value-of select="$type"/>
						</a>
					</xsl:otherwise>
				</xsl:choose>&gt;
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match='dtref[@role="pageref"]'>
		<!-- should generate an <fo:page-number/> of the page
	     where the dt occurs -->
	</xsl:template>
	<xsl:template match="xtermref">
		<!--		if no dot exists
			build a string that contains the two-up, two-down syntax to point to the glossary.htm file
			prepend that string to the href value
			continue as is
		end if
		
<xtermref href="../../foundationdocuments/helpfiles/glossary.htm#fred"/>
<xtermref href="fred"/>-->
		<xsl:variable name="glossaryLink">
			<xsl:call-template name="foundationMap">
				<xsl:with-param name="foundationDocument" select="'glossary.htm'"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="href">
			<xsl:choose>
				<xsl:when test="not(contains(@href,'.'))">
					<xsl:value-of select="concat($glossaryLink,'#',@href)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@href"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="filenameonly">
			<xsl:call-template name="extractfilename">
				<xsl:with-param name="filestring" select="$href"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="htmfile" select="concat($filenameonly, '.htm')"/>
		<a>
			<xsl:attribute name="class">xtermref</xsl:attribute>
			<xsl:attribute name="href"><xsl:value-of select="$href"/></xsl:attribute>
			<xsl:variable name="refID">
				<xsl:value-of select="substring-after($href,'#')"/>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test=". != ''">
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="dir" select="substring-before($href, $filenameonly)"/>
					<xsl:variable name="dirplus" select="concat($dir, $filenameonly)"/>
					<xsl:variable name="xmlfile" select="concat($dirplus,'.xml')"/>
					<xsl:apply-templates select="document($xmlfile)//*[@id=$refID]/label/node()"/>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</a>
	</xsl:template>
	<xsl:template name="extractfilename">
		<xsl:param name="filestring"/>
		<xsl:choose>
			<xsl:when test="contains($filestring, '/')">
				<xsl:variable name="remainder" select="substring-after($filestring, '/')"/>
				<xsl:call-template name="extractfilename">
					<xsl:with-param name="filestring" select="$remainder"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="substring-before($filestring, '.')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!--xsl:template match="xspecref">
		<tr>
			<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
			<td class="number_col" nowrap="nowrap">&nbsp; </td>
			<td class="body_col">
				<b>
					<a>
						<xsl:attribute name="class">elink</xsl:attribute>
						<xsl:attribute name="href"><xsl:value-of select="@spec"/><xsl:text>#</xsl:text><xsl:value-of select="@ref"/></xsl:attribute>
						<xsl:apply-templates/>
					</a>
				</b>
			</td>
		</tr>
	</xsl:template-->
	<xsl:template match="xspecref">
		<xsl:variable name="href">
			<xsl:call-template name="foundationMap">
				<xsl:with-param name="foundationDocument" select="@spec"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$href = 'error'">
				<b>&invalidSpecification1; <xsl:value-of select="@spec"/> &invalidSpecification2;</b>
			</xsl:when>
			<xsl:otherwise>
				<a>
					<xsl:attribute name="href"><xsl:value-of select="$href"/>#<xsl:value-of select="@ref"/></xsl:attribute>
					<xsl:value-of select="@alt"/>
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="ilxspecref">
		<b>
			<a>
				<xsl:attribute name="class">elink</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select="@spec"/><xsl:text>.htm#</xsl:text><xsl:value-of select="@ref"/></xsl:attribute>
				<xsl:apply-templates/>
			</a>
		</b>
	</xsl:template>
	<xsl:template name="foundationMap">
		<!--This takes the name of a file in question and supplies back the full path and name of the file-->
		<xsl:param name="foundationDocument"/>
		<xsl:variable name="modifiedFoundationDocument">
			<xsl:choose>
				<xsl:when test="contains($foundationDocument,'.')">
					<xsl:value-of select="translate((substring-before($foundationDocument, '.')), 'ABCDEFGHIJKLMNNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="translate($foundationDocument, 'ABCDEFGHIJKLMNNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$modifiedFoundationDocument = 'v3guide'">
				<xsl:value-of select="concat('&dir_helpfiles;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'backbone'">
				<xsl:value-of select="concat('&dir_helpfiles;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'glossary'">
				<xsl:value-of select="concat('&dir_helpfiles;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'datatypes'">
				<xsl:value-of select="concat('&dir_helpfiles;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'datatypes-its-xml'">
				<xsl:value-of select="concat('&dir_itsxml;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'messaging-its-xml'">
				<xsl:value-of select="concat('&dir_itsxml;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'requirements-its-xml'">
				<xsl:value-of select="concat('&dir_itsxmlfreq;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'datatypes-its-uml'">
				<xsl:value-of select="concat('&dir_itsuml;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'transport-intro'">
				<xsl:value-of select="concat('&dir_transport;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'transport-ebxml'">
				<xsl:value-of select="concat('&dir_transport;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'transport-mllp'">
				<xsl:value-of select="concat('&dir_transport;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'transport-wsprofiles'">
				<xsl:value-of select="concat('&dir_transport;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'conformance'">
				<xsl:value-of select="concat('&dir_helpfiles;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'itsxml'">
				<xsl:value-of select="concat('&dir_itsxml;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'rim'">
				<xsl:value-of select="concat('&dir_referencefiles;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:when test="$modifiedFoundationDocument = 'vocabulary'">
				<xsl:value-of select="concat('&dir_referencefiles;',$modifiedFoundationDocument,'.htm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'error'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="titleref">
		<a href="#{@href}">
			<xsl:apply-templates/>
		</a>
	</xsl:template>
	<xsl:template match="termdef">
		<a name="{@id}"/>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="anchor">
		<a name="{@id}"/>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="term">
		<b>
			<xsl:apply-templates/>
		</b>
	</xsl:template>
	<xsl:template match="code">
		<code>
			<xsl:apply-templates/>
		</code>
	</xsl:template>
	<!-- added GS 3/7/2002-->
	<xsl:template match="ul">
		<ul>
			<xsl:apply-templates select="@*|node()"/>
		</ul>
	</xsl:template>
	<xsl:template match="emph">
		<em>
			<xsl:apply-templates/>
		</em>
	</xsl:template>
	<xsl:template match="emph[@role='underline']">
		<u>
			<xsl:apply-templates/>
		</u>
	</xsl:template>
	<xsl:template match="emph[@role='strong']">
		<strong>
			<xsl:apply-templates/>
		</strong>
	</xsl:template>
	<xsl:template match="emph[@role='sub']">
		<sub>
			<xsl:apply-templates/>
		</sub>
	</xsl:template>
	<xsl:template match="emph[@role='sup']">
		<sup>
			<xsl:apply-templates/>
		</sup>
	</xsl:template>
	<xsl:template match="blist">
		<tr>
			<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
			<td class="number_col" nowrap="nowrap">&nbsp; </td>
			<td class="body_col">
				<dl>
					<xsl:apply-templates/>
				</dl>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="blist/bibl">
		<dt>
			<b>
				<a name="{@id}">
					<xsl:value-of select="@key"/>
				</a>
			</b>
		</dt>
		<dd>
			<xsl:apply-templates/>
		</dd>
	</xsl:template>
	<xsl:template match="list[@role='ordered']">
		<xsl:choose>
			<!--			<xsl:when test="../descriptive|../../descriptive"> Replaced this 20030226 because did not allow lists in VOC descritpions and precluded a numbered list under a numbered list. gwb -->
			<xsl:when test="ancestor::descriptive or ancestor::list">
				<ol>
					<xsl:apply-templates/>
				</ol>
			</xsl:when>
			<xsl:when test="/VOC and ancestor::vocSet">
				<ol>
					<xsl:apply-templates/>
				</ol>
			</xsl:when>
			<xsl:otherwise>
				<tr>
					<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
					<td class="number_col" nowrap="nowrap">&nbsp; </td>
					<td class="body_col">
						<ol>
							<xsl:apply-templates/>
						</ol>
					</td>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="list[@role='unordered']">
		<xsl:choose>
			<!--			<xsl:when test="ancestor::descriptive|ancestor::list"> Replaced this 20030226 because did not allow lists in VOC descriptions. gwb -->
			<xsl:when test="ancestor::descriptive or ancestor::list or ancestor::def">
				<ul>
					<xsl:apply-templates/>
				</ul>
			</xsl:when>
			<xsl:when test="/VOC and ancestor::vocSet">
				<ul>
					<xsl:apply-templates/>
				</ul>
			</xsl:when>
			<xsl:otherwise>
				<tr>
					<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
					<td class="number_col" nowrap="nowrap">&nbsp; </td>
					<td class="body_col">
						<ul style="padding-left:20px;">
							<xsl:apply-templates/>
						</ul>
					</td>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="glist">
		<tr>
			<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
			<td class="number_col" nowrap="nowrap">&nbsp; </td>
			<td class="body_col">
				<!-- originally did this (MC): <xsl:apply-templates/> -->
				<xsl:for-each select="gitem">
					<xsl:sort select="@id"/>
					<xsl:apply-templates select="."/>
				</xsl:for-each>
				<a href="#contents"> (Return to glossary index)</a>
				<!-- GWB 2003-12-07 - added sort above -->
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="gitem">
		<a name="{@id}"/>
		<xsl:apply-templates select="label"/>
		<br/>
		<!-- GWB 2003-12-07 added the loop on Defs below, and added the defined by reference -->
		<xsl:for-each select="def">
			<xsl:sort select="@sortkey"/>
			<xsl:apply-templates/>
			<xsl:if test="@domainid">
				<xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;(Defined in </xsl:text>
				<xsl:value-of select="@domainid"/>
				<xsl:text>)</xsl:text>
			</xsl:if>
		</xsl:for-each>
		<!-- Originally did this (MC): <xsl:apply-templates select="def/*"/>-->
		<br/>
		<br/>
	</xsl:template>
	<xsl:template match="item">
		<li>
			<xsl:apply-templates/>
		</li>
	</xsl:template>
	<!-- HRS want to be able to hilite code labels differently -->
	<xsl:template match="codeelement">
		<codeelement>
			<xsl:apply-templates/>
		</codeelement>
	</xsl:template>
	<!-- HRS end -->
	<!-- HRS - added the ability to identify an exception, source in earlier text and reference it later -->
	<xsl:template match="exception">
		<li>
			<codeelement id="{concat(ancestor::div1/@id,@name)}">
				<xsl:value-of select="@name"/>
			</codeelement>
			<xsl:choose>
				<xsl:when test="@section">
					<xsl:variable name="sect" select="@section"/>
					<xsl:variable name="name" select="@name"/>
					<xsl:value-of select="//div1[@id=$sect]//exception[@name=$name]"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</li>
	</xsl:template>
	<xsl:template match="exceptionRef">
		<xsl:variable name="exc" select="."/>
		<codeelement>
			<a href="{concat('#',ancestor::div1/@id,$exc)}">
				<xsl:value-of select="."/>
			</a>
		</codeelement>
	</xsl:template>
	<!-- HRS end -->
	<xsl:template match="label">
		<b>
			<xsl:apply-templates/>
		</b>
	</xsl:template>
	<xsl:template match="def">
		<br/>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="spec/header/title"/>
	<xsl:template match="version"/>
	<xsl:template name="copyright">
		<xsl:if test="$datestamp and $timestamp">
			<xsl:variable name="militaryhour" select="substring-before($timestamp, ':')"/>
			<xsl:variable name="hour">
				<xsl:choose>
					<xsl:when test="number($militaryhour) > 12">
						<xsl:value-of select="number($militaryhour) - 12"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="number($militaryhour)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="minutes" select="substring-before(substring-after($timestamp,concat($militaryhour,':')),':')"/>
			<xsl:variable name="ampm">
				<xsl:choose>
					<xsl:when test="number($militaryhour)>11">PM</xsl:when>
					<xsl:otherwise>AM</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<p class="copyright">Last Published:&nbsp;<xsl:value-of select="$datestamp"/>&nbsp;<xsl:value-of select="$hour"/>:<xsl:value-of select="$minutes"/>&nbsp;<xsl:value-of select="$ampm"/>
			</p>
		</xsl:if>
		<p class="copyright">HL7&reg; Version 3 Standard, &copy; 2004 Health Level Seven&reg;, Inc.  All Rights Reserved.</p>
		<p class="copyright">HL7 and Health Level Seven are registered trademarks of Health Level Seven, Inc.  Reg. U.S. Pat &amp; TM Off</p>
	</xsl:template>
	<xsl:template match="nt">
		<a href="#{@def}">
			<xsl:apply-templates/>
		</a>
	</xsl:template>
	<xsl:template match="xnt">
		<a href="{@href}">
			<xsl:apply-templates/>
		</a>
	</xsl:template>
	<xsl:template match="quote">
		<tr>
			<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
			<td class="number_col" nowrap="nowrap">&nbsp; </td>
			<td class="body_col">
				<blockquote>
					<xsl:apply-templates/>
				</blockquote>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="p[quote]">
		<blockquote>
			<xsl:apply-templates/>
		</blockquote>
	</xsl:template>
	<xsl:template mode="number" match="back//*|footnote">
		<xsl:number level="multiple" count="inform-div1|div1|div2|div3|div4|footnote" format="A.1 "/>
	</xsl:template>
	<xsl:template mode="number" match="*">
		<xsl:number level="multiple" count="inform-div1|div1|div2|div3|div4" format="1.1 "/>
	</xsl:template>
	<!--
	what is this?
  -->
	<xsl:template name="DataType">
		<xsl:param name="Text"/>
		<xsl:param name="Path" select="''"/>
		<xsl:choose>
			<xsl:when test="contains($Text,'-')">
				<a href="{$Path}DataType1.htm#{substring-before($Text,'-')}">
					<xsl:value-of select="substring-before($Text,'-')"/>
				</a>
				<xsl:text>&lt;</xsl:text>
				<a href="{$Path}DataType1.htm#{substring-before(substring-after($Text,'-'),'-')}">
					<xsl:value-of select="substring-before(substring-after($Text,'-'),'-')"/>
				</a>
				<xsl:text>&gt;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<a href="{$Path}DataType1#{$Text}">
					<xsl:value-of select="$Text"/>
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="thead|tfoot|tbody|colgroup|tr|th|td">
		<xsl:copy>
			<xsl:apply-templates select="* | @* | text()"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="col"/>
	<xsl:template match="table/@width"/>
	<xsl:template match="table">
		<tr>
			<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
			<td class="number_col" nowrap="nowrap">&nbsp; </td>
			<td class="body_col">
				<xsl:copy>
					<xsl:attribute name="width">90%</xsl:attribute>
					<xsl:attribute name="cellspacing">0</xsl:attribute>
					<xsl:attribute name="cellpadding">5</xsl:attribute>
					<xsl:choose>
						<xsl:when test="@border">
							<xsl:attribute name="border"><xsl:value-of select="@border"/></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="border">1</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="@bordercolor">
							<xsl:attribute name="bordercolor"><xsl:value-of select="@bordercolor"/></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="bordercolor">blue</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<!--						<xsl:attribute name="border">1</xsl:attribute>
					<xsl:attribute name="bordercolor">blue</xsl:attribute>  
					GWB 2003-02-25 The changes above allow the user/designer to select border style and border color, while still providing for
						a deafult in case none is specified-->
					<!--  	<xsl:copy-of select="* | @*"/>  THIS is the culprit that is breaking the rendering of stuff inside tables!   GWB-2003-02-25 tried
												replacing it with something like with <xsl:apply-templates select="* | @* | text()"/> 
												That allowed the internal markup to render, but the table was thoroughly hosed.
												This copy-of (previous) is simply copying the content including <xspecref .... direct to the HTML
												which fortunately (or unfortunately) ignores it. -->
					<!-- GWB 2003-02-25 After the rant above, the problem was fixed using "mode."  When a user table is encountered, the template above is
						entered to cause the addition of the outer two master table columns.  Thereafter, everything happens within a single cell of the 
						master table.  This is signified by the mode "inner" meaning inner table.  Note that MOST templates that are not used inside a user table.
						Those templates that are must have a second definition, with the mode="inner", if they are to work.  These templates are gathered in the
						section below this.
						
						Within the original table, the remainder of the rendering is accomplished by selecting all templates with mode="inner" as follows: -->
					<xsl:apply-templates mode="inner"/>
				</xsl:copy>
			</td>
		</tr>
	</xsl:template>
	<!-- Following are templates that must be executed WITHIN the context of a user deffined table, thus with mode="inner".  They are grouped according to whether their
	content change relative to the non-mode version is major, minor, or pass-through. -->
	<!-- "table" template has a major change.  It is essentially what had been there before.  Note, this retains the forced assignment of border and border color.
	If ever a user wants to put a table within a table, they may need to control this.  If so, use the logic applied in the "table" without mode template above. -->
	<xsl:template match="table" mode="inner">
		<xsl:copy>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">5</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>
			<xsl:attribute name="bordercolor">blue</xsl:attribute>
			<xsl:apply-templates select="* | @*"/>
		</xsl:copy>
	</xsl:template>
	<!-- Minor changes are templates that are fundamentally the same as the original, except that the mode gets passed through as inner templates are invoked. 
	This applies to "p" and match="thead|tfoot|tbody|colgroup|tr|th|td" .  These follow.  Note that it is NOT clear that
	 match="thead|tfoot|tbody|colgroup|tr|th|td" is EVER made, except in mode="inner", but I was unwilling to make that assumption, so it was safer
	 to leave it. -->
	<xsl:template match="item" mode="inner">
		<li>
			<xsl:apply-templates mode="inner"/>
		</li>
	</xsl:template>
	<xsl:template match="list[@role='ordered']" mode="inner">
		<ol>
			<xsl:apply-templates mode="inner"/>
		</ol>
	</xsl:template>
	<xsl:template match="list[@role='unordered']" mode="inner">
		<ul>
			<xsl:apply-templates mode="inner"/>
		</ul>
	</xsl:template>
	<xsl:template match="p" mode="inner">
		<p>
			<xsl:apply-templates mode="inner"/>
		</p>
	</xsl:template>
	
	<xsl:template match="thead|tfoot|tbody|colgroup|tr|th|td" mode="inner">
		<xsl:copy>
			<xsl:if test="@colspan">
				<xsl:attribute name="colspan"><xsl:value-of select="@colspan"/></xsl:attribute>
			</xsl:if>
			<xsl:if test="@align">
				<xsl:attribute name="align"><xsl:value-of select="@align"/></xsl:attribute>
			</xsl:if>
			<xsl:apply-templates mode="inner"/>
		</xsl:copy>
	</xsl:template>
	<!-- pass-through's are templates in which the mode="inner" template simply invokes its none modal version. 
		These are table caption, 	plus several 'refs' dtref, xdtref, xspecref, ilxspecref 
	Note that "termref" "xtermref" and the other in-lines like "emph" should probably be added here, but they need 
	more careful thought and testing than can be applied just before a ballot goes out.
	-->
	<xsl:template match="emph" mode="inner">
		<xsl:apply-templates select="."/>
	</xsl:template>
	<xsl:template match="dtref|xdtref|xspecref|ilxspecref|caption" mode="inner">
		<xsl:apply-templates select="."/>
	</xsl:template>
	<!--
	somehow, we need to make sure that namespace decls
	don't come thru...but these don't work
  	<xsl:template match='@xmlns:xlink'/>
  -->
	<!--xsl:template match="@*">
		<xsl:copy/>
	</xsl:template
	GWB: following change & uncommenting of this template is per pvb 12/6/2003 -->
	<xsl:template match="@*">
		<xsl:attribute name="{local-name()}"><xsl:value-of select="."/></xsl:attribute>
	</xsl:template>
	<xsl:template match="caption">
		<!-- HRS Put the anchor on a single space at the top of the table
		      and copy any caption attributes across so that the user has more control
		      over where the caption goes
		    -->
		<xsl:if test="../@id">
				<a name="{../@id}">&nbsp;</a>
		</xsl:if>
		<xsl:element name="caption">
			<xsl:for-each select="@*">
				<xsl:attribute name="{local-name()}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>
			Table
			<xsl:number level="any" count="table[caption]" format="1: "/>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="p/graphic">
		<xsl:call-template name="inlineGraphic"/>
	</xsl:template>
	<xsl:template match="descriptive/graphic">
		<xsl:call-template name="inlineGraphic"/>
	</xsl:template>
	<xsl:template name="inlineGraphic">
		<xsl:variable name="fileName">
			<xsl:call-template name="getGraphicFileWithExtension">
				<xsl:with-param name="fileReference" select="translate(@source,'G','g')"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="starts-with($fileName,'L-') or starts-with($fileName, 'graphics/L-')">
				<xsl:variable name="prepend">
					<xsl:choose>
						<xsl:when test="@alt and @alt != ''">
							<xsl:value-of select="concat(@alt,' (link to graphic opens in a new window)')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="'Link to graphic (opens in a new window)'"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<a>
					<xsl:attribute name="href"><xsl:value-of select="$fileName"/></xsl:attribute>
					<xsl:attribute name="target">_blank</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
					<!--					<xsl:value-of select="@alt"> </xsl:value-of>-->
					<xsl:value-of select="$prepend"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<br/>
				<img class="graphic" border="0">
					<xsl:attribute name="src"><xsl:value-of select="$fileName"/></xsl:attribute>
					<xsl:choose>
						<xsl:when test="@alt and @alt != '' and not (contains($fileName, 'imageunavailable'))">
							<xsl:attribute name="alt"><xsl:value-of select="@alt"/></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="alt"><xsl:value-of select="@source"/></xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</img>
				<br/>
				<xsl:if test="@alt and @alt != ''">
					<xsl:value-of select="@alt"/>
				</xsl:if>
				<br/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="graphic">
		<xsl:call-template name="inlineGraphic"/>
	</xsl:template>
	<xsl:template match="graphic" mode="inner">
		<xsl:call-template name="inlineGraphic"/>
	</xsl:template>
	<!-- THIS IS FOR GENERIC EXHIBITS -->
	<xsl:variable name="labelMapRTF">
		<exhibit role="requirement" label="Requirement"/>
		<exhibit role="example" label="Example"/>
		<exhibit role="template" label="Type Template"/>
		<exhibit role="schema" label="Schema Fragment"/>
		<exhibit role="dtdl" label="Definition"/>
		<exhibit role="exhibit" label="Exhibit"/>
		<!--HRS added two new types -->
		<exhibit role="codefragment" label="Listing"/>
		<exhibit role="figure" label="Figure"/>
		<!-- HRS end -->
		<exhibit label="Exhibit"/>
	</xsl:variable>
	<xsl:template match="exhibit">
		<tr>
			<td class="graphic_col" nowrap="nowrap"> &nbsp; </td>
			<td class="number_col" nowrap="nowrap">&nbsp; </td>
			<td class="body_col">
				<!-- HRS - center figures in the documet -->
				<xsl:choose>
					<xsl:when test="@role='figure'">
						<div align="CENTER">
							<table>
								<xsl:apply-templates select="./caption[1]"/>
								<tr>
									<td class="{@role}">
										<xsl:apply-templates select="*[not(name()='caption')]|text()"/>
									</td>
								</tr>
							</table>
						</div>
					</xsl:when>
					<xsl:otherwise>
				<!-- HRS end -->
						<table width="90%">
							<!-- pvb: made @id conditional on its presence in the source...
							because some exhibits don't have ids and that is resulting
							in invalid HTML
						  -->
							<xsl:if test="@id">
								<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
							</xsl:if>
							<xsl:apply-templates select="./caption[1]"/>
							<tr>
								<td class="{@role}">
									<xsl:apply-templates select="*[not(name()='caption')]|text()"/>
								</td>
							</tr>
						</table>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="exhibit/caption">
		<xsl:param name="role" select="../@role"/>
		<!--
pvb: no need for node-sets with saxon 7.6+  GWB: Undid this change to allow Instant Saxon use -->
		<xsl:param name="mappedLabel" select="saxon:node-set($labelMapRTF)/exhibit[@role=$role]/@label"/>
		<!-- GWB: following is linje pvb inserted to replace line above 
		<xsl:param name="mappedLabel" select="$labelMapRTF/exhibit[@role=$role]/@label"/> -->
		<xsl:param name="label">
			<xsl:choose>
				<xsl:when test="../@label">
					<xsl:value-of select="../@label"/>
				</xsl:when>
				<xsl:when test="$mappedLabel">
					<xsl:value-of select="$mappedLabel"/>
				</xsl:when>
				<xsl:otherwise>
					<!--
pvb: no need for node-sets with saxon 7.6+    GWB: Undid this change to allow Instant Saxon use -->
					<xsl:value-of select="saxon:node-set($labelMapRTF)/exhibit[not(@role)]/@label"/>
					<!-- GWB: following is line pvb inserted to replace line above
					<xsl:value-of select="$labelMapRTF/exhibit[not(@role)]/@label"/> -->
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:value-of select="$label"/>
			<xsl:number level="any" count="exhibit[@role=$role]" format=" 1: "/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	<!-- END EXHIBIT -->
	<!-- added GS 7/25/2002 -->
	<xsl:template match="pre">
		<pre>
			<xsl:apply-templates/>
		</pre>
	</xsl:template>
	<xsl:template match="br">
		<xsl:copy>
			<xsl:apply-templates select="* | @*"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="footnote">
		<xsl:variable name="anchor">
			<xsl:number level="any" count="footnote" format="1"/>
		</xsl:variable>
		<a name="fn-src{$anchor}"/>
		<a href="#fn{$anchor}">
			<sup style="font-size: smaller">
				<xsl:value-of select="$anchor"/>
			</sup>
		</a>
	</xsl:template>
	<xsl:template match="footnote" mode="footnote-text">
		<xsl:apply-templates select="p[1]"/>
		<xsl:apply-templates select="p[position()>1]"/>
	</xsl:template>
	<xsl:template match="footnote/p[1]">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="xdtref">
		<a href="&datatypesfile;.htm#{@ref}">
			<xsl:choose>
				<xsl:when test="text()">
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@ref"/>
				</xsl:otherwise>
			</xsl:choose>
		</a>
	</xsl:template>
	<xsl:template match="tabref">
		<xsl:variable name="ref" select="@ref"/>
		<a href="#{@ref}">Table
			<xsl:for-each select="//table[@id=$ref]">
				<xsl:number level="any" count="table[caption]"/>
			</xsl:for-each>
		</a>
	</xsl:template>
	<xsl:template match="xtabref">
		<xsl:variable name="ref" select="@ref"/>
		<xsl:variable name="spec" select='concat("../input/XML/",@spec,".xml")'/>
		<xsl:variable name="specNodes" select="document($spec)/*"/>
		<a class="elink" target="xspecref" href="../{@spec}/{@spec}.htm#{@ref}">Table
			<xsl:for-each select="$specNodes//table[@id=$ref]">
				<xsl:number level="any" count="table[caption]"/>
			</xsl:for-each>
		</a>
	</xsl:template>
	<xsl:template match="propname">
		<b>
			<i>
				<xsl:choose>
					<xsl:when test="text()">
						<xsl:value-of select="."/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="id" select="ancestor::prop/@id"/>
						<xsl:value-of select="&v3dt;//prop[@id=$id]/head"/>
					</xsl:otherwise>
				</xsl:choose>
			</i>
		</b>
	</xsl:template>
	<xsl:template match="descriptive" name="descriptive">
		<tr>
			<td class="graphic_col" nowrap="nowrap">
				<xsl:call-template name="showBallotStatus"/>
			</td>
			<td class="number_col" nowrap="nowrap">&nbsp;</td>
			<td class="body_col">
				<xsl:choose>
					<xsl:when test="@name='Scope'">
						&nbsp;
					</xsl:when>
					<xsl:otherwise>
						<br/>
						<b class="heading">
							<xsl:value-of select="@name"/>
						</b>
						<p/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
		<tr>
			<td class="graphic_col" nowrap="nowrap">&nbsp;</td>
			<td class="number_col" nowrap="nowrap">&nbsp;</td>
			<td class="body_col">
				<p>
					<xsl:apply-templates/>
				</p>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="makeref">
		<xsl:apply-templates select="." mode="number"/>
		<xsl:choose>
			<!-- If element has an ID attribute, use this for the link reference -->
			<xsl:when test="@id">
				<xsl:variable name="href">
					<xsl:choose>
						<xsl:when test="contains(@id, 'id') and contains(@id, 'ic')">
							<xsl:value-of select="concat($BaseFileName,'_DO_XX.htm#',@id)"/>
						</xsl:when>
						<xsl:when test="substring(@id,1,4)=substring($BaseFileName,3,4)">
							<xsl:value-of select="concat($BaseFileName,substring(@id,5,3),'.htm#',@id)"/>
						</xsl:when>
						<!--the introduction in in the same file as the table of contents, so need a
						special case for that -->
						<xsl:when test="'intro'=substring(@id,1,5)">
							<xsl:text/>#<xsl:value-of select="@id"/>
						</xsl:when>
						<!-- all the other divisions have no text, so do not need a link to the top of the document
						and the document name cannot be go except by a lookup on the first bit of the id - so if
						it is required then this choose statement can be used to do the lookup -->
						<xsl:otherwise/>
					</xsl:choose>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="not($href='')">
						<a href="{$href}">
							<xsl:call-template name="makerefLabel"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="makerefLabel"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<!-- The element has no ID attribute, so use the text content as the reference -->
					<xsl:when test="head">
						<a href="#section-{translate(head,' ','-')}">
							<xsl:value-of select="head"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<a href="#section-{translate(@name,' ','-')}">
							<xsl:value-of select="@name"/>
						</a>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="makerefLabel">
		<xsl:choose>
			<!-- If a head element, use its value; otherwise use the name attribute of the element -->
			<xsl:when test="head">
				<tr>
					<td class="graphic_col" nowrap="nowrap">&nbsp;<!--An image will go here later-->
					</td>
					<td class="number_col" nowrap="nowrap">&nbsp;</td>
					<td class="body_col">
						<xsl:value-of select="head"/>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@name"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="toc">
		<xsl:param name="numberPrefix"/>
		<xsl:for-each select="div1">
			<xsl:value-of select="$numberPrefix"/>
			<xsl:apply-templates select="." mode="number"/>&nbsp;
			      <a href="#{@id}">
				<xsl:value-of select="head"/>
			</a>
			<br/>
			<xsl:for-each select="div2">
				 &nbsp;&nbsp;&nbsp;<xsl:value-of select="$numberPrefix"/>
				<xsl:apply-templates select="." mode="number"/>&nbsp;
				<a>
					<xsl:attribute name="href"><xsl:value-of select="concat('#',@id)"/></xsl:attribute>
					<xsl:value-of select="head"/>
				</a>
				<br/>
				<xsl:for-each select="div3">
				 &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<xsl:value-of select="$numberPrefix"/>
					<xsl:apply-templates select="." mode="number"/>&nbsp;
					<a>
						<xsl:attribute name="href"><xsl:value-of select="concat('#',@id)"/></xsl:attribute>
						<xsl:value-of select="head/text()"/>
					</a>
					<br/>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:if test="not(name(.) = 'back') and (../back/child::* or back/child::*)">
			<h3>Appendices</h3>
			<xsl:for-each select="../back|back">
				<xsl:call-template name="toc">
					<!--xsl:with-param name="numberPrefix" select="'A.'"/-->
				</xsl:call-template>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
	<xsl:template name="insert_space">
		<!--This tempalte inserts a blank space between objects in the main table.  For an example see any of the domain xsl documents.  It is used librally thoughout them.-->
		<tr>
			<td colspan="3">&nbsp;</td>
		</tr>
	</xsl:template>
	<xsl:template name="file_exists" xmlns:file="java.io.File">
		<!--This template is used to test for the existance of a file using the java file.exists() method.  The use of this method makes this stylesheet dependent on a java xsl processor, though ways around this might be possible.  For an example, see v3mDomainHTML_HD.xsl- -->
		<xsl:param name="filename"/>
		<xsl:variable name="directory" select="file:new(file:new($filename))"/>
		<xsl:if test="not(file:exists($directory))">false</xsl:if>
		<xsl:if test="file:exists($directory)">true</xsl:if>
	</xsl:template>
	<xsl:template name="showBallotStatus">
		<!--This template is used to show the colored dots that go along with an object's status.  For an example, see v3mDomainHTML.xsl-->
		<xsl:param name="checkNode" select="."/>
		<xsl:param name="setStatus" select="$checkNode/@ballotStatus"/>
		<xsl:choose>
			<!--first test to see if the current context has ballotStatus-->
			<xsl:when test="$setStatus!=''">
				<xsl:variable name="statusgraphic">
					<xsl:value-of select="concat($setStatus, '.gif')"/>
				</xsl:variable>
				<xsl:variable name="altstatus">
					<xsl:choose>
						<xsl:when test="$checkNode/@ballotNumber">
							<xsl:value-of select="concat($setStatus,$checkNode/@ballotNumber)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$setStatus"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:element name="img">
					<xsl:attribute name="src"><xsl:value-of select="concat('graphics/',$statusgraphic)"/></xsl:attribute>
					<xsl:attribute name="alt"><xsl:value-of select="$altstatus"/></xsl:attribute>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>&nbsp;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="insertID">
		<!--This template makes a target with the value of an ojbect's ID - if an id exists.  If it doesn't exist, it creates another target using the 'head' value.  For an example, see v3mDomainHTML.xsl or v3mLayoutHTML.xsl-->
		<xsl:choose>
			<xsl:when test="@id">
				<a name="{@id}"/>
			</xsl:when>
			<xsl:otherwise>
				<a name="section-{translate(head,' ','-')}"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="getGraphicFileWithExtension">
		<!--This template takes a name of a file without an extension, tests a variety of extensions and large graphics, and returns the first one that exists.  It returns nothing if none exists.  See v3mDomainHTML or v3mLayoutHTML for examples.-->
		<xsl:param name="fileReference"/>
		<xsl:variable name="stripGraphics">
			<xsl:choose>
				<xsl:when test="contains($fileReference, 'graphics/')">
					<xsl:value-of select="substring-after($fileReference,'graphics/')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$fileReference"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="fileName">
			<xsl:choose>
				<xsl:when test="contains($stripGraphics,'.')">
					<!--Because there is a period, we will assume that have actually given us the file name-->
					<xsl:variable name="testFileExists">
						<xsl:call-template name="testLargeGraphicExists">
							<xsl:with-param name="filename" select="$stripGraphics"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="$testFileExists != ''">
							<xsl:value-of select="$testFileExists"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="'graphics/imageunavailable.gif'"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<!--we will now try to test the individual extensions.  This is the part that needs to be edited to add new extensions-->
					<xsl:variable name="gifExists">
						<xsl:call-template name="testLargeGraphicExists">
							<xsl:with-param name="filename" select="concat($stripGraphics, '.gif')"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="jpgExists">
						<xsl:call-template name="testLargeGraphicExists">
							<xsl:with-param name="filename" select="concat($stripGraphics, '.jpg')"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="$gifExists != ''">
							<xsl:value-of select="$gifExists"/>
						</xsl:when>
						<xsl:when test="$jpgExists != ''">
							<xsl:value-of select="$jpgExists"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="'graphics/imageunavailable.gif'"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!--Now return the filename that has been selected.  This should be an empty string if no file exists like this-->
		<xsl:value-of select="$fileName"/>
	</xsl:template>
	<xsl:template name="testLargeGraphicExists">
		<!--This template takes a file name and tests to see if either it or the large version exists.  It returns the name of the file that exits or nothing if neither exists.  See v3mDomainHTML or v3mLayoutHTML for examples-->
		<xsl:param name="filename"/>
		<xsl:variable name="fileExists">
			<xsl:call-template name="file_exists">
				<xsl:with-param name="filename" select="concat('&dir_graphics;',$filename)"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="largeFileExists">
			<xsl:call-template name="file_exists">
				<xsl:with-param name="filename" select="concat('&dir_graphics;','L-',$filename)"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$fileExists = 'true'">
				<xsl:value-of select="concat('graphics/', $filename)"/>
			</xsl:when>
			<xsl:when test="$largeFileExists = 'true'">
				<xsl:value-of select="concat('graphics/L-', $filename)"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="inform">
		<!--This template inserts the label (non-Normative) for those objects that aren't within an inform-div1 tag.  For an example, see v3mDomainHTML.xsl-->
		<xsl:if test="parent::inform-div1">
			<xsl:text> (Non-Normative)</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
