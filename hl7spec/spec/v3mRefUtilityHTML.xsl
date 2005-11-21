<?xml version="1.0"?>
<!-- edited with XMLSPY v5 rel. 4 U (http://www.xmlspy.com) by XMLSPY 5 Enterprise Ed. (HL7 Publications TC) -->
<!DOCTYPE xsl:stylesheet [
	<!ENTITY copy "&#169;">
	<!ENTITY nbsp "&#160;">
	<!ENTITY reg "&#174;">
	<!ENTITY sect '&#xa7;'>
	<!ENTITY % v3ent_files SYSTEM "V3m.ent">
	%v3ent_files;
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/TR/WD-xlink" xmlns:saxon="http://icl.com/saxon" extension-element-prefixes="saxon" exclude-result-prefixes="xlink" version="1.0">
	<!--note the saxon specific extension information.  These stylesheets require saxon in order to access saxon extensions, though I can't think of any being used offhand.  It might be a good exercise to remove these and see what happens.-->
	<xsl:output method="html" indent="yes" encoding="ISO-8859-1" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>
	<!--This stylesheet contains templates that are common to both Layout and Domain.  This Stylesheet should always be made available to all transforms.-->
	<xsl:template name="buildArtifactLink">
		<!--TODO: add domain level story boards and dmims-->
		<!--people will only link to IMCOCT.htm, no other CT sections
			given an artifact
			What ID to use?  (same as before)
			Then, which XML file do you live in?
			Are you a domain level item (DMIM, storydivn)
				If yes, then, are you internal?
					If yes, go get you
				If no, then does the XML file exist?
					If yes, open file does element exist?
						get artifact.
		-->
		<xsl:param name="baseClass">		
		</xsl:param>
		<xsl:param name="imageFile"/>
		<xsl:param name="imageHeight" select="'16'"/>
		<xsl:param name="imageWidth" select="'16'"/>
		<xsl:param name="imageAlt" select="'Image goes here'"/>
		<xsl:param name="appendage"/>
		<xsl:param name="optionalClass" select="''"/>
		<xsl:param name="artifactID">
			<xsl:choose>
				<xsl:when test="@refIdentifier != ''">
					<xsl:value-of select="@refIdentifier"/>
				</xsl:when>
				<xsl:when test="@identifier != ''">
					<xsl:value-of select="@identifier"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@ref"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<xsl:param name="parsedArtifactID">
			<!-- remove a "-" if one is present-->
			<xsl:choose>
				<xsl:when test="substring-before($artifactID,'-') != ''">
					<xsl:value-of select="substring-before($artifactID,'-')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$artifactID"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<xsl:param name="fullDomainID">
			<xsl:choose>
				<xsl:when test="@sectID != ''">
					<xsl:value-of select="@sectID"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="makeFullDomainID">
						<xsl:with-param name="artifactID" select="$parsedArtifactID"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<xsl:param name="artifactType">
			<xsl:choose>
				<xsl:when test="$fullDomainID = 'IMCOCT'">
					<xsl:value-of select="'CMETS'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="extractArtifactType">
						<xsl:with-param name="artifactID" select="$parsedArtifactID"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<xsl:param name="fullArtifactLinkID">
			<xsl:call-template name="makeFullArtifactLinkID">
				<xsl:with-param name="artifactID" select="$parsedArtifactID"/>
			</xsl:call-template>
		</xsl:param>
		<xsl:param name="subDomain">
			<xsl:choose>
				<xsl:when test="$fullDomainID = 'IMCOCT'">
					<xsl:value-of select="'DO'"/>
				</xsl:when>
				<!--This part has to change to reflect baseClasss changes-->
				<xsl:when test="$artifactType = 'DM'">
					<xsl:value-of select="'DO'"/>
				</xsl:when>
				<xsl:when test="@sectType != ''">
					<xsl:value-of select="@sectType"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="extractSubDomain">
						<xsl:with-param name="artifactID" select="$parsedArtifactID"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<xsl:param name="linkText">
			<!--This first part checks to make sure it is not a domain level object first-->
			<xsl:choose>
				<xsl:when test="$artifactType = 'DO'">
					<xsl:value-of select="substring($parsedArtifactID, 1, 4)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$parsedArtifactID"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<xsl:param name="filePath"/>
		<xsl:param name="newFilePath">
			<xsl:choose>
				<xsl:when test="$filePath = ''">
					<xsl:call-template name="getFilePath">
						<xsl:with-param name="fileName" select="$fullDomainID"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$filePath"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<xsl:variable name="xmlFile">
			<xsl:value-of select="concat($newFilePath,$fullDomainID,'.xml')"/>
		</xsl:variable>
		<xsl:variable name="xmlFileExists">
			<xsl:call-template name="file_exists">
				<xsl:with-param name="filename" select="$xmlFile"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="artifactBaseClass">
			<xsl:if test="$xmlFileExists = 'true'">
				<xsl:choose>
					<xsl:when test="translate($fullDomainID,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz') != translate($BaseFileName,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')">
						<xsl:value-of select="document($xmlFile)//spec/body/subdivn/*/*[@id=$fullArtifactLinkID]/@baseClass"/>
					</xsl:when>
					<xsl:otherwise>
						<!--xsl:message><xsl:value-of select="$fullArtifactLinkID"></xsl:value-of></xsl:message-->
						<xsl:value-of select="/spec/body/subdivn/*/*[@id=$fullArtifactLinkID]/@baseClass"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="filetoref">
			<xsl:choose>
				<xsl:when test="$artifactType = 'DO'">
					<!--This code could be changed to look inside the external XML files for domain level artifacts to see if they exist-->
					<xsl:value-of select="concat($newFilePath,$fullDomainID,'.htm#',$fullArtifactLinkID)"/>
				</xsl:when>
				<xsl:when test="$artifactType = 'ST' and $subDomain='DO'">
					<!--This code could be changed to look inside the external XML files for domain level artifacts to see if they exist-->
					<xsl:value-of select="concat($newFilePath,$fullDomainID,'.htm#',$fullArtifactLinkID)"/>
				</xsl:when>
				<xsl:when test="$artifactBaseClass = ''">
					<xsl:value-of select="concat($newFilePath,$fullDomainID,'.htm')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($newFilePath,$fullDomainID,'_',$artifactBaseClass,$appendage,'.htm#',$fullArtifactLinkID)"/>
					<!--xsl:value-of select="concat($newFilePath,$fullDomainID,$appendage,'.htm#',$fullArtifactLinkID)"/-->
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$xmlFileExists = 'false'">
				<xsl:element name="span">
					<xsl:attribute name="class"><xsl:value-of select="$optionalClass"/></xsl:attribute>
					<xsl:choose>
						<xsl:when test="$imageFile != ''">
							<xsl:element name="img">
								<xsl:attribute name="src"><xsl:value-of select="$imageFile"/></xsl:attribute>
								<xsl:attribute name="alt"><xsl:value-of select="$imageAlt"/></xsl:attribute>
								<xsl:attribute name="height"><xsl:value-of select="$imageHeight"/></xsl:attribute>
								<xsl:attribute name="width"><xsl:value-of select="$imageWidth"/></xsl:attribute>
								<xsl:attribute name="border"><xsl:value-of select="'0'"/></xsl:attribute>
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$linkText"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="a">
					<xsl:attribute name="href"><xsl:value-of select="$filetoref"/></xsl:attribute>
					<xsl:attribute name="class"><xsl:value-of select="$optionalClass"/></xsl:attribute>
					<xsl:choose>
						<xsl:when test="$imageFile != ''">
							<xsl:element name="img">
								<xsl:attribute name="src"><xsl:value-of select="$imageFile"/></xsl:attribute>
								<xsl:attribute name="alt"><xsl:value-of select="$imageAlt"/></xsl:attribute>
								<xsl:attribute name="height"><xsl:value-of select="$imageHeight"/></xsl:attribute>
								<xsl:attribute name="width"><xsl:value-of select="$imageWidth"/></xsl:attribute>
								<xsl:attribute name="border"><xsl:value-of select="'0'"/></xsl:attribute>
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$linkText"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="buildArtifactLink_old">
		<!--BuildArtifactLink is the most complex template to understand, but it is also the most helpful.  This template was made to take a reference to an artifact and turn it into a link for the Hl7 Ballot.  Examples of this template in use can be found in almost all of the templates.
		
			In the simplest case, you could process the following node: 
				
				<example1 ref="FIAB_RM010000-rmi"></example1>

			You would call it in the following way:
				
				<xsl:template match="example1">
					<xsl:call-template name="buildArtifactLink"/>
				</xsl:template>
				
			It would give you back the following:
				
				<a href="../../sectioncontent/AB/AMFIAB_SB_RM.htm#FIAB_RM010000-rmi">FIAB_RM010000</a>
			
The mechanics behind this require a little more explanation.  Below I will try to explain the code step by step as we move through the style sheet.

The next step is to set the parameters involved with processing this template.
The fist parameter, "artifactID" refers to the string to be processed by this template.

			You can always specify this string explicitly as such:
			
				<xsl:template match="example2">
					<xsl:call-template name="buildArtifactLink">
							<xsl:with-param name="artifactID" select="'FIAB_RM010000-rmi'"/>
					</xsl:call-template>
				</xsl:template>
			
			Or:
				
				<xsl:template match="example3">
					<xsl:call-template name="buildArtifactLink">
							<xsl:with-param name="artifactID" select="@theLocationOfMyArtifact"/>
					</xsl:call-template>
				</xsl:template>	
				
			Or however else you may want to reference the value of the artifactID
			
If you do not specify the artifactID, some assumptions are made about it's location.  This template assumes that it is first the "refIdentifier" attribute of the current node.  Next it assumes the "identifer" attribute contains the value, and finally it assumes the "ref" tag contains the info we need. 

If the information is contained anywhere other than the "refIdentifier","identifier", or "ref" tags, you will have to set the artifactID explicity (see Example2 or Example3 above).
		 	-->
		<xsl:param name="artifactID">
			<xsl:choose>
				<xsl:when test="@refIdentifier != ''">
					<xsl:value-of select="@refIdentifier"/>
				</xsl:when>
				<xsl:when test="@identifier != ''">
					<xsl:value-of select="@identifier"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@ref"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<!--
The next parameter we need is the parsedArtifactID.  This parameter selects everything before the hypen, if one is present in the artifactID.
For example, the artifactID of "FIAB_RM010000-rmi" would become "FIAB_RM010000".  
If there is no hyphen present, the artifactID is used as is. 
Note that this is a variable and cannot be set.  If you want to set anything, set the artifactID parameter.
		<xsl:variable name="parsedArtifactID">

pvb: var -> param
-->
		<xsl:param name="parsedArtifactID">
			<!-- remove a "-" if one is present-->
			<xsl:choose>
				<xsl:when test="substring-before($artifactID,'-') != ''">
					<xsl:value-of select="substring-before($artifactID,'-')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$artifactID"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<!--
		</xsl:variable>
-->
		<!--

This next parameter is called "fullDomainID".  It refers to the full domain of the link in question.

	If a "sectID" attribute exists in the current node it will be used.  Consider the the following node:

	<teref role="trigger" ref="FIAB_TE010005-te" sectID="AMFIAB" sectType="SB" refName="Patient Account Event Reactivate Fulfillment" refIdentifier="FIAB_TE010005"/>

	In this case, the "sectID" is present, so it is assumed to be "AMFIAB".
	
	If the "sectID" consider the following node:
	
		<approledivn identifier="roles-FIAB_DO000002" name="Application Roles" parent="FIAB_DO000002-ic" id="roles-FIAB_DO000002-ic">
	
	If the "parent" attribute was passed in as the "artifactID" paramter, this node would not have a "sectID" attribute.
	
	In this case, we have developed a special template called "makeFullDomainID" which will derive the fullDomainID from the artifactID.  This template resides in this stylesheet and you can reference it now if you need more information.
-->
		<xsl:param name="fullDomainID">
			<xsl:choose>
				<xsl:when test="@sectID != ''">
					<xsl:value-of select="@sectID"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="makeFullDomainID">
						<xsl:with-param name="artifactID" select="$parsedArtifactID"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<!-- 
The next parameter is called artifactType.  

The "atifactType" is the two letters that represent the file you would like to point to (eg "XX","IN","AR","SB").

	For instance in the following link:

	<a href="../../sectioncontent/AB/AMFIAB_SB_RM.htm#FIAB_RM010000-rmi">FIAB_RM010000</a>

	The "artifactType" is the "RM" that appears in the AMFIAB_SB_RM.htm part of the link.

This is derived from the 6th through 8th characters in the artifactID.  For instance, characters 6-8 of the artifactID "FIAB_RM010000-rmi" are "RM".

Please note that if the aforementioned "fullDomainID" is "IMCOCT", we want to use "CMETS" for the artifactType.  This is because IMCOCT is a special domain.
	
This parameter can always be set in which case you can override the special "IMCOCT" handling.

If there is no artifactType set explicitly in the call to this template, we try to derive it from the artifactID using the "extractArtifactType" template.  You can go to this template to read more about it.
-->
		<xsl:param name="artifactType">
			<xsl:choose>
				<xsl:when test="$fullDomainID = 'IMCOCT'">
					<xsl:value-of select="'CMETS'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="extractArtifactType">
						<xsl:with-param name="artifactID" select="$parsedArtifactID"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<!--
		
The "fullArtifactLinkID" is the artifact code used to create the html anchor (e.g.FICR_HD300300-hmd).  
This always ends with the artifact extension code.

For example, the in the following:

<a href="../../sectioncontent/AB/AMFIAB_SB_RM.htm#FIAB_RM010000-rmi">FIAB_RM010000</a>

The fullArtifactID is the part that comes after the link or:

FIAB_RM010000-rmi

I know we parsed this out earlier when we made the "parsedArtifactID".  The reason why we don't use it is because sometimes the information after the hyphen is not what we want.  I think this is a problem with hmd extensions, but I'm not sure...

You can see the "makeFullArtifactID" template for more info on this.
-->
		<xsl:param name="fullArtifactLinkID">
			<xsl:call-template name="makeFullArtifactLinkID">
				<xsl:with-param name="artifactID" select="$parsedArtifactID"/>
			</xsl:call-template>
		</xsl:param>
		<!--Subdomain is the information in the link like "SB" or "QU" or "MF".

For example, the in the following:

<a href="../../sectioncontent/AB/AMFIAB_SB_RM.htm#FIAB_RM010000-rmi">FIAB_RM010000</a>

The full artifactID is the "SB" that appears in:

AMFIAB_SB_RM.htm

If the fullDomain variable is IMCOCT, it always uses "DO" because there are no storyboards, queries, or master files.

If the artifactType parameter is "DM", this means it is a DMIM and likewise has no storyboards, queries, or master files.

If this node has a "sectType" attribute, it is used for as this parameter.

If none of the above are true, the param is determined with the "extractSubDomain" template which should have some more info.
-->
		<xsl:param name="subDomain">
			<xsl:choose>
				<xsl:when test="$fullDomainID = 'IMCOCT'">
					<xsl:value-of select="'DO'"/>
				</xsl:when>
				<xsl:when test="$artifactType = 'DM'">
					<xsl:value-of select="'DO'"/>
				</xsl:when>
				<xsl:when test="@sectType != ''">
					<xsl:value-of select="@sectType"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="extractSubDomain">
						<xsl:with-param name="artifactID" select="$parsedArtifactID"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<!--
The link text is the information that appears as text on the page.  Normally this is the parsedArtifactID, but you can set it to be anything you want.

Note that is the the artifactType is  "DO", that the first through fourth characters will be used as the link text.

For instance:
	FIAB_DO000002-ic
Will have an artifactType of DO.  This would create a link like this:
	
	<a href="../../sectioncontent/AB/AMFIAB.htm">FIAB</a>

-->
		<xsl:param name="linkText">
			<!--This first part checks to make sure it is not a domain level object first-->
			<xsl:choose>
				<xsl:when test="$artifactType = 'DO'">
					<xsl:value-of select="substring($parsedArtifactID, 1, 4)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$parsedArtifactID"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<!--
The "filePath" is the path the file and can be set as a parameter.

This is used as a convience to set the "newFilePath" variable.

For instance in:

<a href="../../sectioncontent/AB/AMFIAB.htm">FIAB</a>

The "filePath" is "../../sectioncontent/AB".

If the "file" path is not set, the template "getFilePath" is called.  You can read more about it in that template.
-->
		<xsl:param name="filePath"/>
		<!--
		<xsl:variable name="newFilePath">
-->
		<xsl:param name="newFilePath">
			<xsl:choose>
				<xsl:when test="$filePath = ''">
					<xsl:call-template name="getFilePath">
						<xsl:with-param name="fileName" select="$fullDomainID"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$filePath"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<!--
		</xsl:variable>
-->
		<!--
The aforemenationed were all required parameters for building links.

What follows are the optional parameters for use with images.

The "buildArtifactLink" template can also be used for images.  All image information must be set explicity by setting the appropriate paramaters.

	Consider the following example from v3mDomainHTML_HD.xsl:
				
					<xsl:call-template name="buildArtifactLink">
						<xsl:with-param name="imageFile" select="'graphics/rmimview.gif'"/>
						<xsl:with-param name="imageAlt" select="'View R-MIM'"/>
					</xsl:call-template>
	When used on:
					<example1 ref="FIAB_RM010000-rmi"></example1>
	Would give you:
					<a href="../../sectioncontent/AB/AMFIAB.htm"><img src="graphics/rmimview.gif" alt="View R-MIM" height="16" width="16" border="0"></a>
-->
		<xsl:param name="imageFile"/>
		<xsl:param name="imageHeight" select="'16'"/>
		<xsl:param name="imageWidth" select="'16'"/>
		<!--imageFile is for an image you would like to insert(optional)-->
		<xsl:param name="imageAlt" select="'Image goes here'"/>
		<!--imageAlt is the alt tag for this image (optional)-->
		<xsl:param name="appendage"/>
		<!--appendage is an optional addition that appears just before the .htm in the link (e.g. -table)-->
		<!--optionalClass if you want to associate a stylesheet with the link (as in the TOC tree code) -->
		<xsl:param name="optionalClass" select="''"/>
		<!--
The "filetoref" is the variable  that represents what will appear in the "href portion of the link.  It is derived from all the preceding code.
-->
		<xsl:variable name="filetoref">
			<!--This bit of code here was built to redirect items with an artifact Type of DO and redirect them to the toc of that page-->
			<xsl:choose>
				<xsl:when test="$artifactType = 'DO'">
					<xsl:value-of select="concat($newFilePath,$fullDomainID,'.htm')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($newFilePath,$fullDomainID,'_',$subDomain,'_',$artifactType,$appendage,'.htm#',$fullArtifactLinkID)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!--Here we actually create the link-->
		<xsl:element name="a">
			<xsl:attribute name="href"><xsl:value-of select="$filetoref"/></xsl:attribute>
			<xsl:attribute name="class"><xsl:value-of select="$optionalClass"/></xsl:attribute>
			<xsl:choose>
				<!--If imageFile info exists, process accordingly-->
				<xsl:when test="$imageFile != ''">
					<xsl:element name="img">
						<xsl:attribute name="src"><xsl:value-of select="$imageFile"/></xsl:attribute>
						<xsl:attribute name="alt"><xsl:value-of select="$imageAlt"/></xsl:attribute>
						<xsl:attribute name="height"><xsl:value-of select="$imageHeight"/></xsl:attribute>
						<xsl:attribute name="width"><xsl:value-of select="$imageWidth"/></xsl:attribute>
						<xsl:attribute name="border"><xsl:value-of select="'0'"/></xsl:attribute>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$linkText"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	<!--The extractArtifactType template is used to pull out the sixth through eigth letters in a reference.
	If these letters are "MT", this returns "HD" because we know it refers to an HD and that there is no such thing as an MT section for a domain.
		
	The arficactID is anything with a substring of 6,2 that represent the artifact type(what is returns is equivilant HTMLTarget in the buildArtifactLink atemplate DM,SB,RM,etc.)
-->
	<xsl:template name="extractArtifactType">
		<xsl:param name="artifactID" select="@ref"/>
		<xsl:choose>
			<xsl:when test="substring($artifactID,6,2)='MT'">
				<xsl:text>HD</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="substring($artifactID,6,2)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!--The template "getFilePath" gets the path to the file.  It assumes that the file resides in the directory defined in the "dir_sectioncontent" entity reference (from v3m.ent.  The second part of the filePath is the last two letters of the domain name. 

For instance in:

<a href="../../sectioncontent/AB/AMFIAB.htm">FIAB</a>

The "filePath" is "../../sectioncontent/AB".
-->
	<xsl:template name="getFilePath">
		<xsl:param name="fileName"/>
		<!--change dir_sectionconent to dir_support-->
		<xsl:value-of select="concat('&dir_sectioncontent;',(substring($fileName, (string-length($fileName) - 1))),'/')"/>
	</xsl:template>
	<!--The template "extractSubDomain" is used to determine which subdomain the reference belongs in.

This can be 

DO  - for Domains
SB - For state based
QU - For query based
MF - For master files.

Some special processing for QUQI and MFMI are required because they have the special letters in them.

If nothing is present, state based is assumed.
-->
	<xsl:template name="extractSubDomain">
		<!--The paramter artifactID refers to the complete artifact code (eg."AMFICR").-->
		<xsl:param name="artifactID">SB</xsl:param>
		<xsl:choose>
			<xsl:when test="substring($artifactID,1,2) = 'DO'">
				<xsl:value-of select="'DO'"/>
			</xsl:when>
			<!--xsl:when test="substring($artifactID,1,4) = 'QUQI'">
				<xsl:value-of select="'SB'"/>
			</xsl:when>
			<xsl:when test="substring($artifactID,1,2) = 'QU'">
				<xsl:value-of select="'QU'"/>
			</xsl:when>
			<xsl:when test="substring($artifactID,1,4) = 'MFMI'">
				<xsl:value-of select="'SB'"/>
			</xsl:when>
			<xsl:when test="substring($artifactID,1,2) = 'MF'">
				<xsl:value-of select="'MF'"/>
			</xsl:when-->
			<xsl:otherwise>
				<xsl:value-of select="'SB'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!--

"makeFullArtifactLinkID" is used to make the HTML target for a link.

It looks at the 6th through 8th characters in the artifactID and then explictly determines the link based on that information.
-->
	<xsl:template name="makeFullArtifactLinkID">
		<xsl:param name="artifactID"/>
		<xsl:param name="artifactType" select="substring($artifactID,6,2)"/>
		<xsl:choose>
			<!--first test to see if this is already has the extension on it.  If it does, simply return it back.-->
			<xsl:when test="substring-after($artifactID, '-') != ''">
				<xsl:value-of select="$artifactID"/>
			</xsl:when>
			<xsl:otherwise>
				<!--otherwise append the extension we want-->
				<xsl:choose>
					<xsl:when test="$artifactType='IN'">
						<xsl:value-of select="concat($artifactID, '-int')"/>
					</xsl:when>
					<xsl:when test="$artifactType='MT'">
						<xsl:value-of select="concat($artifactID, '-msg')"/>
					</xsl:when>
					<xsl:when test="$artifactType='HD'">
						<xsl:value-of select="concat($artifactID, '-hmd')"/>
					</xsl:when>
					<xsl:when test="$artifactType='ST'">
						<xsl:value-of select="concat($artifactID, '-str')"/>
					</xsl:when>
					<xsl:when test="$artifactType='RM'">
						<xsl:value-of select="concat($artifactID, '-rmi')"/>
					</xsl:when>
					<xsl:when test="$artifactType='DM'">
						<xsl:value-of select="concat($artifactID, '-rmi')"/>
					</xsl:when>
					<xsl:when test="$artifactType='AR'">
						<xsl:value-of select="concat($artifactID, '-ar')"/>
					</xsl:when>
					<xsl:when test="$artifactType='TE'">
						<xsl:value-of select="concat($artifactID, '-te')"/>
					</xsl:when>
					<xsl:when test="$artifactType='DO'">
						<xsl:value-of select="concat('intro-',$artifactID, '-ic')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$artifactID"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!--
Note:  THIS TEMPLATE MUST BE UPDATED EVERY TIME A NEW DOMAIN IS ADDED!!!!

"makeFullDomainID"  gives us the part of the link that refers to where the file is located.  For instance in :
	
	<example1 ref="FIAB_RM010000-rmi"></example1>

The "fullDomainID" is AMFIAB.  We only get "FIAB" from the artifactID, so in order to create the link:

	<a href="../../sectioncontent/AB/AMFIAB_SB_RM.htm#FIAB_RM010000-rmi">FIAB_RM010000</a>
	
We need to get a full mapping somewhere to the entire link.  Based off of the 3rd through 4th letters, we make an assumption and create this fullDomainID.

-->
	<xsl:template name="makeFullDomainID">
		<!--artifactID must contain the domain code at characters number 3 and 4.  This is the only requirement
		This MUST be updated every time a new domain is added!
		-->
		<xsl:param name="artifactID"/>
		<xsl:param name="returnFull" select="'Yes'"/>
		<xsl:param name="domainSubstring" select="substring($artifactID,3,2)"/>
		<xsl:choose>
			<xsl:when test="$domainSubstring='AB' or $domainSubstring = 'CR'">
				<xsl:value-of select="'AMFI'"/>
				<xsl:if test="$returnFull = 'Yes'">
					<xsl:value-of select="$domainSubstring"/>
				</xsl:if>
			</xsl:when>
			<xsl:when test="$domainSubstring='PA' or $domainSubstring= 'SC' or $domainSubstring= 'PM'">
				<xsl:value-of select="'AMPR'"/>
				<xsl:if test="$returnFull = 'Yes'">
					<xsl:value-of select="$domainSubstring"/>
				</xsl:if>
			</xsl:when>
			<xsl:when test="$domainSubstring='MR'">
				<xsl:value-of select="'HMRC'"/>
				<xsl:if test="$returnFull = 'Yes'">
					<xsl:value-of select="$domainSubstring"/>
				</xsl:if>
			</xsl:when>
			<xsl:when test="$domainSubstring='PC'">
				<xsl:value-of select="'HMRE'"/>
				<xsl:if test="$returnFull = 'Yes'">
					<xsl:value-of select="$domainSubstring"/>
				</xsl:if>
			</xsl:when>
			<xsl:when test="$domainSubstring='LB' or $domainSubstring= 'RX' or $domainSubstring= 'RR' or $domainSubstring= 'RT' or $domainSubstring= 'RI' or $domainSubstring= 'CG' or $domainSubstring= 'BB'">
				<xsl:value-of select="'HMPO'"/>
				<xsl:if test="$returnFull = 'Yes'">
					<xsl:value-of select="$domainSubstring"/>
				</xsl:if>
			</xsl:when>
			<xsl:when test="$domainSubstring='CI' or $domainSubstring= 'AI'">
				<xsl:value-of select="'IMMC'"/>
				<xsl:if test="$returnFull = 'Yes'">
					<xsl:value-of select="$domainSubstring"/>
				</xsl:if>
			</xsl:when>
			<xsl:when test="$domainSubstring='MI'">
				<xsl:value-of select="'IMMF'"/>
				<xsl:if test="$returnFull = 'Yes'">
					<xsl:value-of select="$domainSubstring"/>
				</xsl:if>
			</xsl:when>
			<xsl:when test="$domainSubstring='QI'">
				<xsl:value-of select="'IMQU'"/>
				<xsl:if test="$returnFull = 'Yes'">
					<xsl:value-of select="$domainSubstring"/>
				</xsl:if>
			</xsl:when>
			<xsl:when test="$domainSubstring='CT' or $domainSubstring= 'MT'">
				<xsl:value-of select="'IMCO'"/>
				<xsl:if test="$returnFull = 'Yes'">
					<xsl:value-of select="$domainSubstring"/>
				</xsl:if>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="makeJavascriptHeader">
		<!-- pvb: replaced language='Javascript' with type='text/javascript' -->
		<script src="tree.js" type="text/javascript"/>
		<script src="setTopFrameText.js" type="text/javascript"/>
		<script type="text/javascript">
			if (find('heading')) setHeadingTextFromFileName(document.location.href);
			</script>
	</xsl:template>
</xsl:stylesheet>
