<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n In Progress As Of 3/25/2014 -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="contact.block">
		<xsl:if test="work.phone.number and string-length(work.phone.number) &gt; 0">
			<xsl:apply-templates select="work.phone.number"/>
		</xsl:if>
		<xsl:if test="office.phone.number and string-length(office.phone.number) &gt; 0">
			<xsl:apply-templates select="office.phone.number"/>
		</xsl:if>
		<xsl:if test="phone.number and string-length(phone.number) &gt; 0">
			<xsl:apply-templates select="phone.number"/>
		</xsl:if>
		<xsl:if test="home.phone.number and string-length(home.phone.number) &gt; 0">
			<xsl:apply-templates select="home.phone.number"/>
		</xsl:if>
		<xsl:if test="cell.phone.number and string-length(cell.phone.number) &gt; 0">
			<xsl:apply-templates select="cell.phone.number"/>
		</xsl:if>
		<xsl:if test="fax.number and string-length(fax.number) &gt; 0">
			<xsl:apply-templates select="fax.number"/>
		</xsl:if>
		<xsl:if test="pager.number and string-length(pager.number) &gt; 0">
			<xsl:apply-templates select="pager.number"/>
		</xsl:if>
		<xsl:if test="email and string-length(email) &gt; 0">
			<xsl:apply-templates select="email"/>
		</xsl:if>
		<xsl:if test="internet.url and string-length(internet.url) &gt; 0">
			<xsl:apply-templates select="internet.url"/>
		</xsl:if>
		<xsl:if test="blog.url and string-length(blog.url) &gt; 0">
			<xsl:apply-templates select="blog.url"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="work.phone.number | office.phone.number | phone.number | home.phone.number | cell.phone.number">
		<div>
			<strong>
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&profilerContactPhoneKey;', '&profilerContactPhone;')"/>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="fax.number">
		<div>
			<strong>
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&profilerContactFaxKey;', '&profilerContactFax;')"/>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="fax.number//text()">
			<xsl:call-template name="clean-phone-number">
				<xsl:with-param name="rawPhoneNumber" select="."/>
			</xsl:call-template>
	</xsl:template>

	<xsl:template match="pager.number">
		<div>
			<strong>
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&profilerContactPagerKey;', '&profilerContactPager;')"/>
			</strong>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="email">
		<xsl:variable name="mailtoAddress">
			<xsl:choose>
				<xsl:when test="cite.query/@w-normalized-cite">
					<xsl:value-of select="cite.query/@w-normalized-cite"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="normalize-space(.)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<div>
			<strong>
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&profilerContactEmailKey;', '&profilerContactEmail;')"/>
			</strong>
			<a class="&pauseSessionOnClickClass;">
				<xsl:attribute name="href">
					<xsl:text>mailto:</xsl:text>
					<xsl:value-of select="$mailtoAddress"/>
				</xsl:attribute>
				<xsl:value-of select="normalize-space(.)"/>
			</a>
		</div>
	</xsl:template>

	<xsl:template match="internet.url">
		<div>
			<strong>
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&profilerContactWebsiteKey;', '&profilerContactWebsite;')"/>
			</strong>
			<a class="&pauseSessionOnClickClass;" target="_blank">
				<xsl:attribute name="href">
					<xsl:if test="not(starts-with(cite.query/@w-normalized-cite, 'http://')) and not(starts-with(cite.query/@w-normalized-cite, 'https://'))">
						<xsl:text>http://</xsl:text>
					</xsl:if>
					<xsl:apply-templates select="cite.query/@w-normalized-cite"/>
				</xsl:attribute>
				<xsl:value-of select="normalize-space(.)"/>
			</a>
		</div>
	</xsl:template>

	<xsl:template match="blog.url">
		<div>
			<strong>
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&profilerContactBlogKey;', '&profilerContactBlog;')"/>
			</strong>
			<a class="&pauseSessionOnClickClass;" target="_blank">
				<xsl:attribute name="href">
					<xsl:apply-templates select="cite.query/@w-normalized-cite"/>
				</xsl:attribute>
				<xsl:call-template name="clean-url">
					<xsl:with-param name="rawUrl" select="."/>
				</xsl:call-template>
			</a>
		</div>
	</xsl:template>

</xsl:stylesheet>