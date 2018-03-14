<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="HtmlMetadata.xsl"/>
	<xsl:include href="DocLinks.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--Update WhatsMarket.xsl as well when updating this template.-->
	<xsl:template match="Document">
		<html>
			<xsl:call-template name="htmlMetadata" />
			<xsl:apply-templates select="n-docbody"/>
		</html>
	</xsl:template>

	<xsl:template match="prelim/author/web.address">
		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="@href"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>

	<xsl:template name="TitleAndAbstract">
		<h1>
			<xsl:value-of select="prelim/title"/>
		</h1>
	</xsl:template>

	<xsl:template match="abstract">
		<div id="&wlnEnterprise_resource_abstract;">
			<xsl:apply-templates />
			<xsl:apply-templates select="../prelim/author"/>
		</div>
	</xsl:template>

	<xsl:template match="division[@id]">
		<xsl:variable name="id" select="@id" />
		<a>
			<xsl:attribute name="name">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</a>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="body">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="drafting.note">
		<div class="&wlnEnterprise_draftingnote;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="author">
		<div class="&wlnEnterprise_resource_author;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="para">
		<p>
			<xsl:apply-templates />
		</p>
	</xsl:template>

	<xsl:template match="head">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="list.item">
		<li>
			<xsl:apply-templates />
		</li>
	</xsl:template>

	<!-- I DONT THINK WE WANT THIS... they dont link it
  <xsl:template match="web.address">
    <a>
      <xsl:attribute name="href">
        <xsl:value-of select="@href"/>
      </xsl:attribute>
      <xsl:apply-templates />
    </a>
  </xsl:template>
  -->

	<xsl:template match="web.address">
		<em>
			<xsl:apply-templates />
		</em>
	</xsl:template>

	<xsl:template match="list[@type='bulleted']">
		<ul>
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<xsl:template match="headtext">
		<h2>
			<xsl:apply-templates />
		</h2>
	</xsl:template>

	<xsl:template match="ital">
		<em>
			<xsl:apply-templates />
		</em>
	</xsl:template>

	<xsl:template match="bold">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>
  
  <!-- We dont want to show any of the Cite query links here -->
	<xsl:template match="//cite.query">
		<xsl:apply-templates />  
	</xsl:template>

	<!-- THIS IS THE TABLE STUFF IN HERE-->
	<xsl:template match="table">
		<div class="&wlnEnterprise_tableWrapper;">
			<table cellpadding="5" cellspacing="0" border="1" width="100%">
				<xsl:apply-templates />
			</table>
		</div>
	</xsl:template>

	<xsl:template match="tgroup">
		<colgroup>
			<xsl:apply-templates />
		</colgroup>
	</xsl:template>

	<xsl:template match="colspec">
		<xsl:variable name="colname" select="@colname" />
		<xsl:variable name="colwidth" select="@colwidth" />
		<colgroup>
			<xsl:attribute name="id">
				<xsl:value-of select="$colname"/>
			</xsl:attribute>
			<xsl:attribute name="width">
				<xsl:value-of select="$colwidth"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</colgroup>
	</xsl:template>

	<xsl:template match="row">
		<tr>
			<xsl:apply-templates />
		</tr>
	</xsl:template>

	<xsl:template match="entry">
		<td valign="top">
			<xsl:apply-templates />
		</td>
	</xsl:template>

	<!-- START RELATED TOPICS SECTION -->
	<xsl:template name="RelatedTopicsSection">
		<div id="&wlnEnterprise_RelatedTopics;">
			<xsl:if test="//md.topics">
				<ul>
					<xsl:for-each select="//md.topic[string-length(md.topic.name)>0]">
						<xsl:sort />
						<xsl:call-template name="BuildAlsoFoundLinkTopic" />
					</xsl:for-each>
				</ul>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="BuildAlsoFoundLinkTopic">
		<li>
			<xsl:call-template name="wrapWithTag">
				<xsl:with-param name="tag" select="string('span')" />
				<xsl:with-param name="contents">
					<xsl:apply-templates select="md.topic.name"/>
				</xsl:with-param> 
			</xsl:call-template>
		</li>
	</xsl:template>
	<!-- END RELATED TOPICS SECTION -->


	<!-- START RELATED RESOURCES SECTION -->
	<xsl:template name="RelatedResourcesSection">
		<xsl:text>&wlnEnterpriseTempRelatedResourcesPlaceHolder;</xsl:text>
	</xsl:template>
	<!-- END RELATED RESOURCES SECTION -->


	<!-- START MISC -->
	<xsl:template name="wrapWithTag">
		<xsl:param name="tag" select="div" />
		<xsl:param name="class"/>
		<xsl:param name="id"/>
		<xsl:param name="href"/>
		<xsl:param name="contents"/>
		<xsl:param name="style"/>
		<xsl:element name="{$tag}">
			<xsl:if test="$class">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($id) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($href) &gt; 0">
				<xsl:attribute name="href">
					<xsl:value-of select="$href"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($style) &gt; 0">
				<xsl:attribute name="style">
					<xsl:value-of select="$style"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	<!-- END MISC -->

</xsl:stylesheet>
