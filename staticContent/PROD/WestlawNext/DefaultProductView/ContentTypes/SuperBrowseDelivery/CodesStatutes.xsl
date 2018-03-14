<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="LinkedToc.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Suppressions.xsl"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div>
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesStatutesClass;'"/>
			</xsl:call-template>

			<xsl:variable name="uuid" select="n-metadata/metadata.block/md.identifiers/md.uuid" />
			<input type="hidden" id="&documentGuid;" value="{$uuid}" alt="&documentGuid;" />
			
			<xsl:call-template name="StarPageMetadata"/>
			<xsl:apply-templates/>
			<xsl:if test="not($IsRuleBookMode)">
				<xsl:variable name="IsLastChild">
					<xsl:choose>
						<xsl:when test="parent::documents and not(following-sibling::Document)">
							<xsl:value-of select="true()" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="false()" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:if test="$IsLastChild = 'true'">
					<xsl:call-template name="EndOfDocument" />
				</xsl:if>
			</xsl:if>
			<xsl:if test="$IsRuleBookMode">
				<xsl:apply-templates select="/" mode="Custom"/>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="AddProductDocumentClasses">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:text>&documentFixedHeaderView;</xsl:text>
	</xsl:template>

	<!--Bug#700820,For Missing whitespace above some paragraphs-->
	<xsl:template match="para[preceding-sibling::subsection]/paratext" priority="1">
		<br/>
		<xsl:call-template name="renderParagraphTextDiv"/>
	</xsl:template>
	
	<!-- Suppress these two elements since they look weird. -->
	<xsl:template match="md.secondary.cites | popular.name.doc.title" />

	<!-- Message.Block/Message -->
	<xsl:template match="message.block">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="message.block/message">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="signature.block">
		<div>
			<xsl:for-each select="signature.line/signature">
				<xsl:apply-templates/>
				<br/>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template match="centdol"/>

	<!--Fix for the missing content in WLN(Collection : w_codesstailnvdp)-->
	<xsl:template match="ed.note.grade">
		<xsl:apply-templates/>
	</xsl:template>

	<!--Supress tgroup tags that have warning attribute. bug 339115)-->
	<xsl:template match="tgroup[@warning]" priority="2"/>

	<xsl:template name="parseYearMonthDayDateFormat">
		<xsl:param name="date" select="."/>
		<xsl:param name="displayDay" />
		<xsl:param name="displayTime" />
		<xsl:if test="string-length($date) &gt; 7 and number($date) != 'NaN'">
			<xsl:variable name ="year" select ="substring($date,1,4)"/>
			<xsl:variable name ="month" select ="substring($date,5,2)"/>
			<xsl:variable name ="day" select ="substring($date,7,2)"/>
			<xsl:choose>
				<xsl:when test ="$month = 01">January</xsl:when>
				<xsl:when test ="$month = 02">February</xsl:when>
				<xsl:when test ="$month = 03">March</xsl:when>
				<xsl:when test ="$month = 04">April</xsl:when>
				<xsl:when test ="$month = 05">May</xsl:when>
				<xsl:when test ="$month = 06">June</xsl:when>
				<xsl:when test ="$month = 07">July</xsl:when>
				<xsl:when test ="$month = 08">August</xsl:when>
				<xsl:when test ="$month = 09">September</xsl:when>
				<xsl:when test ="$month = 10">October</xsl:when>
				<xsl:when test ="$month = 11">November</xsl:when>
				<xsl:when test ="$month = 12">December</xsl:when>
			</xsl:choose>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:if test="$displayDay">
				<xsl:value-of select ="$day"/>
				<xsl:text>,<![CDATA[ ]]></xsl:text>
			</xsl:if>

			<xsl:value-of select ="$year"/>

			<xsl:if test="$displayTime">
				<xsl:if test="string-length($date) &gt; 13">
					<xsl:variable name ="hour" select ="substring($date,9,2)"/>
					<xsl:variable name ="minute" select ="substring($date,11,2)"/>
					<xsl:variable name ="second" select ="substring($date,13,2)"/>

					<xsl:text><![CDATA[ ]]></xsl:text>


					<xsl:choose>
						<xsl:when test="number($hour) = 00">12</xsl:when>
						<xsl:when test="number($hour) &gt; 12">
							<xsl:value-of select="number($hour) - 12"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="number($hour)"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select ="concat(':', $minute, ':', $second)"/>
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:choose>
						<xsl:when test="$hour &gt; 11">PM</xsl:when>
						<xsl:otherwise>AM</xsl:otherwise>
					</xsl:choose>
				</xsl:if>

			</xsl:if>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
