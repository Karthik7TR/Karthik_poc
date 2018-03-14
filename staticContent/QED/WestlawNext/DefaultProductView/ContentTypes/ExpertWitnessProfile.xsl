<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 3/25/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="ProfilerAddress.xsl"/>
	<xsl:include href="ProfilerAffiliation.xsl"/>
	<xsl:include href="AreaExpertise.xsl"/>
	<xsl:include href="Biography.xsl"/>
	<xsl:include href="CommunicationPreferences.xsl"/>
	<xsl:include href="ProfilerContact.xsl"/>
<xsl:include href="ProfilerExpertise.xsl"/>
	<xsl:include href="Figure.xsl"/>
	<xsl:include href="JobDescription.xsl"/>
	<xsl:include href="ProfilerLanguage.xsl"/>
	<xsl:include href="ProfilerOrganization.xsl"/>
	<xsl:include href="PersonalStatement.xsl"/>
	<xsl:include href="ProfilerPosition.xsl"/>
	<xsl:include href="ProfilerRepresentativeClients.xsl"/>
	<xsl:include href="Resume.xsl"/>
	<xsl:include href="ProfilerSharedUtilities.xsl"/>
	<xsl:include href="Speaker.xsl"/>
	<xsl:include href="TestimonyStatement.xsl"/>
	<xsl:include href="TimesTestified.xsl"/>
	<xsl:include href="WitnessWorkStatement.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:apply-templates select="n-docbody/profile"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
	<xsl:template match="profile">
		<xsl:call-template name="ExpertWitnessContactInformation" />
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:call-template name="ExpertWitnessContent" />
	</xsl:template>
				
	<!--
	**************************************************************************************
	* Expert Witness Content Blocks that override Profiler Content Blocks to add headers *
	**************************************************************************************
	-->	
	
	<!-- Area Of Expertise for Expert Witness -->
	<xsl:template match="expertise.block" priority="1">
		<xsl:if test="string-length(normalize-space(expertise)) &gt; 0">
		<div class="&headtextClass;">
				<strong>
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewAreasOfExpertiseKey;', '&ewAreasOfExpertise;')"/>
        </strong>
		</div>
			<xsl:apply-templates select="expertise"/>
		</xsl:if>
		<xsl:apply-templates select="specific.expertise"/>
		<xsl:apply-templates select="expertise.key.words"/>
		<xsl:apply-templates select="class"/>
	</xsl:template>

	<!-- Expertise key words for Expert Witness -->
	<xsl:template match="expertise.block/expertise.key.words">
		<xsl:if test="string-length(normalize-space(.)) &gt; 0">
		<div class="&headtextClass;">
				<strong>
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewExpertiseKeywordsKey;', '&ewExpertiseKeywords;')"/>
        </strong>
		</div>
			<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
		</xsl:if>
	</xsl:template>

	<!-- Specific Expertise for Expert Witness -->
	<xsl:template match="expertise.block/specific.expertise">
		<xsl:if test="string-length(normalize-space(.)) &gt; 0">
		<div class="&headtextClass;">
				<strong>
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewSpecificAreaOfExpertiseKey;', '&ewSpecificAreaOfExpertise;')"/>
        </strong>
		</div>
				<div class="&paraMainClass;">
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>

	<!--Expertise class Block for Expert Witness -->
	<xsl:template match="expertise.block/class">
		<xsl:if test="string-length(normalize-space(.)) &gt; 0">
			<div class="&headtextClass;">
				<strong>
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewRelevantCoursesKey;', '&ewRelevantCourses;')"/>
        </strong>
			</div>
			<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
		</xsl:if>
	</xsl:template>

	<!--Afiliation block for Expert Witness -->
	<xsl:template match="affiliation.block" priority="1">
		<xsl:if test="string-length(normalize-space(affiliate.name)) &gt; 0">
			<div class="&headtextClass;">
				<strong>
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewAffiliationKey;', '&ewAffiliation;')"/>
        </strong>
			</div>
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<!-- Organization block for Expert Witness -->
	<xsl:template match="organization.block" priority="1">
		<xsl:if test="string-length(normalize-space(.)) &gt; 0">
			<div class="&headtextClass;">
				<strong>
					<xsl:text>&ewOrganization;</xsl:text>
				</strong>
			</div>
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<!-- Position block for Expert Witness -->
	<xsl:template match="position.block" priority="1">
		<xsl:if test="position and string-length(normalize-space(position)) &gt; 0 and /Document/n-metadata/metadata.block/md.subjects/md.subject/md.doctype.name='1D+2'">
			<div class="&headtextClass;">
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewDegreeKey;', '&ewDegree;')"/>
			</div>
			<xsl:apply-templates select="position" />
		</xsl:if>
		<xsl:if test="position and string-length(normalize-space(position)) &gt; 0 and not(/Document/n-metadata/metadata.block/md.subjects/md.subject/md.doctype.name='1D+2')">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<!--Language Block for Expert Witness -->
	<xsl:template match="language.block" priority="1">
		<xsl:if test="string-length(normalize-space(.)) &gt; 0">
			<div class="&headtextClass;">
				<strong>
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewLanguagesKey;', '&ewLanguages;')"/>
        </strong>
			</div>
			<xsl:apply-templates select="para"/>
		</xsl:if>
	</xsl:template>

	<!--Representatice Client Block for Expert Witness -->
	<xsl:template match="representative.clients.block" priority="1">
		<xsl:if test="string-length(normalize-space(.)) &gt; 0">
			<div class="&headtextClass;">
				<strong>
          <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&ewPreviousClientWorkKey;', '&ewPreviousClientWork;')"/>
        </strong>
			</div>
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<!-- Expert Witness Paragraph -->	
	<xsl:template match="para" priority="1">
		<div class="&paraMainClass;">
			<xsl:apply-templates select="paratext"/>
		</div>
	</xsl:template>

	<xsl:template match="paratext" priority="1">
		<div class="&paratextMainClass;">
			<xsl:apply-templates/>
		</div>		
	</xsl:template>

	<!-- Data contains new lines,  New lines get dropped and not converted to spaces (ex. guid: IBA0F26405D4011E08B05FDF15589D8E8) -->
	<xsl:template match="paratext//text()" priority="1">
		<xsl:value-of select='.'/>
	</xsl:template>

	<!-- Never render these elements -->
	<xsl:template match="map|first.name|last.name|city|state|expertise.id|figure.caption|court.jurisdiction|born.block"/>

</xsl:stylesheet>