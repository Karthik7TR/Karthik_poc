<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKGeneralBlocks.xsl"/>

	<xsl:template name ="BuildDocumentTocContent">
		<xsl:call-template name="BuildAuthorSection"/>
		<xsl:call-template name="BuildTocTree"/>
		<xsl:call-template name="BuildRelatedTopicsPlaceholder"/>
	</xsl:template>

	<xsl:template name="BuildAuthorSection">
		<xsl:if test="n-metadata/resultList/authors">
			<xsl:element name="div">
				<xsl:attribute name="class">&authorsSection;  &tocSectionClass;</xsl:attribute>
				<xsl:element name="h2">
					<xsl:attribute name="class">&authorsSectionHeader;</xsl:attribute>
					&authorSectionName;
				</xsl:element>

				<xsl:apply-templates select="n-metadata/resultList/authors/node()"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BuildTocTree">
		<xsl:if test="not($DeliveryMode)">
			<xsl:element name="div">
				<xsl:attribute name="class">&tocSectionClass;</xsl:attribute>
				<xsl:attribute name="id">&overviewTocId;</xsl:attribute>
				<xsl:element name="h2">&tableOfContentsText;</xsl:element>

				<xsl:element name="ol">
					<xsl:attribute name="class">&coKhTocOlList; &tocSectionContentClass;</xsl:attribute>
					<xsl:call-template name="OverviewTocItems"/>

					<xsl:call-template name="LegislationTocItems"/>

					<xsl:element name="li">
						<xsl:call-template name="BuildTocItem">
							<xsl:with-param name="title" select="'&keyCasesTitle;'"/>
							<xsl:with-param name="anchor" select="'&casesSectionId;'"/>
						</xsl:call-template>
					</xsl:element>

					<xsl:call-template name="ReadingTocItems"/>

				</xsl:element>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template name="LegislationTocItems">
		<xsl:element name="li">
			<xsl:call-template name="BuildTocItem">
				<xsl:with-param name="title" select="'&legislationTitle;'"/>
				<xsl:with-param name="anchor" select="'&legislationSectionId;'"/>
			</xsl:call-template>

			<xsl:element name="ol">
				<xsl:attribute name="class">&coKhTocSubList;</xsl:attribute>

				<xsl:for-each select="//key-acts | //key-subordinate-legislation | //key-quasi-legislation | //key-european-union-legislation">
					<xsl:apply-templates select="." mode="ToC" />
				</xsl:for-each>

			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="ReadingTocItems">
		<xsl:element name="li">
			<xsl:call-template name="BuildTocItem">
				<xsl:with-param name="title" select="'&readingTitle;'"/>
				<xsl:with-param name="anchor" select="'&readingSectionId;'"/>
			</xsl:call-template>
			<xsl:element name="ol">
				<xsl:attribute name="class">&coKhTocSubList;</xsl:attribute>

				<xsl:for-each select="//key-texts | //further-reading">
					<xsl:apply-templates select="." mode="ToC" />
				</xsl:for-each>

			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="key-acts" mode="ToC">
		<xsl:call-template name="KeyTocListItem">
			<xsl:with-param name="title" select="'&keyActsHeading;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="key-subordinate-legislation" mode="ToC">
		<xsl:call-template name="KeyTocListItem">
			<xsl:with-param name="title" select="'&keySubordinateLegislationHeading;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="key-quasi-legislation" mode="ToC">
		<xsl:call-template name="KeyTocListItem">
			<xsl:with-param name="title" select="'&keyQuasiLegislationHeading;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="key-european-union-legislation" mode="ToC">
		<xsl:call-template name="KeyTocListItem">
			<xsl:with-param name="title" select="'&keyEuropeanUnionLegislationHeading;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="key-quasi-legislation" mode="ToC">
		<xsl:call-template name="KeyTocListItem">
			<xsl:with-param name="title" select="'&keyQuasiLegislationHeading;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="key-european-union-legislation" mode="ToC">
		<xsl:call-template name="KeyTocListItem">
			<xsl:with-param name="title" select="'&keyEuropeanUnionLegislationHeading;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="key-texts" mode="ToC">
		<xsl:call-template name="KeyTocListItem">
			<xsl:with-param name="title" select="'&keyTextsHeading;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="further-reading" mode="ToC">
		<xsl:call-template name="KeyTocListItem">
			<xsl:with-param name="title" select="'&furtherReadingHeading;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="KeyTocListItem">
		<xsl:param name="title"/>
		<xsl:element name="li">
			<xsl:call-template name="BuildTocItem">
				<xsl:with-param name="title" select="$title"/>
				<xsl:with-param name="anchor" select="name(.)"/>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>

	<xsl:template name="OverviewTocItems">
		<xsl:for-each select="//overview">
			<xsl:element name="li">
				<xsl:variable name="anchor" select="'&articleOverviewSectionId;'" />

				<xsl:call-template name="BuildTocItem">
					<xsl:with-param name="title" select="'&overviewOfTopic;'"/>
					<xsl:with-param name="anchor" select="$anchor" />
				</xsl:call-template>

				<xsl:call-template name="BuildTocSubLists">
					<xsl:with-param name="topAnchor" select="$anchor" />
				</xsl:call-template>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="BuildTocSubLists">
		<xsl:param name="topAnchor"/>
		<xsl:param name="index" select="0"/>

		<xsl:if test="./section/title | ./paragraph/title">
			<xsl:element name="ol">
				<xsl:attribute name="class">&coKhTocSubList;</xsl:attribute>
				<xsl:for-each select="./section | ./paragraph">
					<xsl:if test="title">
						<xsl:variable name="curIndex" select="number($index) + count(preceding-sibling::*//title)" />
						<xsl:variable name="anchor" select="concat($topAnchor, $curIndex)"/>
						<xsl:element name="li">
							<xsl:call-template name="BuildTocItem">
								<xsl:with-param name="title" select="title/text()"/>
								<xsl:with-param name="anchor" select="$anchor"/>
							</xsl:call-template>
							<xsl:call-template name="BuildTocSubLists">
								<xsl:with-param name="topAnchor" select="$topAnchor"/>
								<xsl:with-param name="index" select="number($curIndex)+1"/>
							</xsl:call-template>
						</xsl:element>
					</xsl:if>
				</xsl:for-each>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BuildTocItem">
		<xsl:param name='title'/>
		<xsl:param name='anchor'/>

		<xsl:element name="div">
			<xsl:attribute name="class">&coKhTocItem;</xsl:attribute>
			<xsl:element name="a">
				<xsl:attribute name='href'>
					<xsl:value-of select="concat('#', '&internalLinkIdPrefix;', $anchor)" />
				</xsl:attribute>
				<xsl:value-of select="$title" />
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="//resultList/authors/author">
		<xsl:variable name="authorProfile" select="./@profile"/>
		<xsl:variable name="authorName" select="./display-name"/>
		<xsl:variable name="organisationName" select="./organisation-name"/>
		<xsl:element name="div">
			<xsl:attribute name="class">&authorInfo; &tocSectionContentClass;</xsl:attribute>

			<xsl:call-template name="BuildImage">
				<xsl:with-param name="guid" select="./profile-image"/>
				<xsl:with-param name="styles" select="'&authorImage;'"/>
				<xsl:with-param name="alt" select="$authorName"/>
			</xsl:call-template>

			<xsl:element name="p">
				<xsl:choose>
					<xsl:when test="$DeliveryMode">
						<xsl:value-of select="$authorName"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:element name="a">
							<xsl:attribute name="class">&authorNameLink;</xsl:attribute>
							<xsl:attribute name="href">&jsVoidText;</xsl:attribute>
							<xsl:attribute name="data-author-profile">
								<xsl:value-of select="$authorProfile"/>
							</xsl:attribute>
							<xsl:value-of select="$authorName"/>
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>

			<xsl:element name="p">
				<xsl:if test="not($DeliveryMode)">
					<xsl:attribute name="class">&authorOrganisation;</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="$organisationName"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="BuildRelatedTopicsPlaceholder">
		<xsl:element name="div">
			<xsl:attribute name="class">&tocSectionClass;</xsl:attribute>
			<xsl:attribute name="id">&relatedTopicsId;</xsl:attribute>
		</xsl:element>
	</xsl:template>

	<xsl:template name="AddTocHeadingText">
		<xsl:text>&navigationHeadingText;</xsl:text>
	</xsl:template>

</xsl:stylesheet>