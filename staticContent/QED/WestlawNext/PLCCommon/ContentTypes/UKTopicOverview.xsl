<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKTopicOverviewToc.xsl" />
	<xsl:include href="UKGeneralBlocks.xsl" />
	<xsl:include href="InternationalLogos.xsl"/>
	<xsl:include href="Copyright.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="isAuthorDocument" select="//metadata.block/md.infotype='author'"/>

	<xsl:template name="BuildDocumentToc">
		<xsl:if test="not($isAuthorDocument)">
			<xsl:call-template name="BuildDocumentTocInternal"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BuildDocumentBody">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>&ukMainDocumentContent;<![CDATA[ ]]>&docDivision;<![CDATA[ ]]>&simpleContentBlockClass;<![CDATA[ ]]>&topicOverviewDocClass;</xsl:text>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="$isAuthorDocument">
					<xsl:apply-templates select="//document/author-profile/author/biography"/>
					<xsl:apply-templates select="//authored-articles"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="LastUpdateSection" />
					<xsl:if test="$DeliveryMode">
						<xsl:element name="div">
							<xsl:attribute name="class">&paraMainClass;</xsl:attribute>
							<xsl:call-template name="BuildAuthorSection"/>
						</xsl:element>
					</xsl:if>
					<xsl:element name="div">
						<xsl:attribute name="id">&introductionSectionId;</xsl:attribute>
						<xsl:apply-templates select="//document/article/introduction"/>
					</xsl:element>
					<xsl:call-template name="OverviewSection"/>
					<xsl:call-template name="KeyDocumentsSection"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

	<xsl:template name="BuildDocumentHeaderContent">
		<xsl:if test="$isAuthorDocument">
			<xsl:call-template name="BuildImage">
				<xsl:with-param name="guid" select="//author-profile/author/author-image"/>
				<xsl:with-param name="styles" select="'&authorImage;'"/>
				<xsl:with-param name="alt" select="//author-profile/author/display-name"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:element name="h1">
			<xsl:attribute name="class">&titleClass;</xsl:attribute>
			<xsl:value-of select="//metadata.block/md.descriptions/md.title"/>
		</xsl:element>
		<xsl:if test="$isAuthorDocument">
			<xsl:element name="span">
				<xsl:value-of select="//resultList/organisation-name"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template name="LastUpdateSection">
		<xsl:element name="div">
			<xsl:attribute name="class">&coLastUpdateSection;</xsl:attribute>
			<xsl:element name="table">
				<xsl:element name="tr">
					<xsl:element name="td">&lastDateOfReview;</xsl:element>
					<xsl:element name="td">
						<xsl:call-template name="formatYearMonthDayToDDMMMYYYY">
							<xsl:with-param name="date">
								<xsl:value-of select="//n-metadata//md.dates/md.publisheddatetime"/>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:element>
				</xsl:element>
				<xsl:if test="boolean(//fulltext_metadata/last-update/node())">
					<xsl:element name="tr">
						<xsl:element name="td">&lastUpdate;</xsl:element>
						<xsl:element name="td">
							<xsl:apply-templates select="//fulltext_metadata/last-update/node()"/>
						</xsl:element>
					</xsl:element>
				</xsl:if>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="emphasis">
		<xsl:choose>
			<xsl:when test="@type='bold'">
				<b>
					<xsl:apply-templates />
				</b>
			</xsl:when>
			<xsl:when test="@type = 'strong'">
				<strong>
					<xsl:apply-templates />
				</strong>
			</xsl:when>
			<xsl:when test="@type = 'italic'">
				<i>
					<xsl:apply-templates />
				</i>
			</xsl:when>
			<xsl:when test="@type = 'weak'">
				<u>
					<xsl:apply-templates />
				</u>
			</xsl:when>
			<xsl:when test="@type = 'smallcaps'">
				<span class="&smallCapsFontVariant;">
					<xsl:apply-templates />
				</span>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="OverviewSection">
		<xsl:if test="boolean(//article/overview/node())">
			<xsl:element name="div">
				<xsl:attribute name="id">&articleOverviewSectionId;</xsl:attribute>
				<xsl:element name="h2">
					<xsl:if test="not($DeliveryMode)">
					<xsl:attribute name="class">&headtextClass;</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', '&articleOverviewSectionId;')"/>
					</xsl:attribute>
					<xsl:text>&overviewOfTopic;</xsl:text>
				</xsl:element>
				<xsl:choose>
					<xsl:when test="$DeliveryMode and //overview//paragraph[1]/@prefix-rules='number'">
						<xsl:call-template name="DeliveryOverviewSection"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="//article/overview"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<!--Image-->
	<xsl:template match="graphic">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="Images"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="div">
					<xsl:attribute name="class">&topicOverviewImageWrapperClass;</xsl:attribute>
					<xsl:call-template name="Images"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="Images">
		<xsl:variable name="blobHref">
			<xsl:call-template name="createBlobLink">
				<xsl:with-param name="guid" select="@id"/>
				<xsl:with-param name="mimeType" select="'&xPngMimeType;'"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:element name="a">
			<xsl:attribute name="type">&xPngMimeType;</xsl:attribute>
			<xsl:attribute name="title">&topicOverviewImageLinkTitle;</xsl:attribute>
			<xsl:attribute name="target">&linkTargetNewWindow;</xsl:attribute>
			<xsl:attribute name="href">
				<xsl:value-of select="$blobHref"/>
			</xsl:attribute>
			<xsl:element name="img">
				<xsl:attribute name="class">&topicOverviewImageClass;</xsl:attribute>
				<xsl:attribute name="alt">&relevantImageAltText;</xsl:attribute>
				<xsl:attribute name="src">
					<xsl:value-of select="$blobHref"/>
				</xsl:attribute>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<!--key sections-->
	<xsl:template name="KeyDocumentsSection">
		<xsl:element name="div">
			<xsl:attribute name="id">&keyDocumentsSectionId;</xsl:attribute>
			<xsl:element name="div">
				<xsl:attribute name="id">&legislationSectionId;</xsl:attribute>
				<xsl:element name="h2">
					<xsl:if test="not($DeliveryMode)">
					<xsl:attribute name="class">&headtextClass;</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', '&legislationSectionId;')"/>
					</xsl:attribute>
					<xsl:text>&legislationTitle;</xsl:text>
				</xsl:element>
				<xsl:call-template name="KeySection">
					<xsl:with-param name="section" select="//article/key-acts" />
					<xsl:with-param name="title" select="'&keyActsHeading;'" />
				</xsl:call-template>
				<xsl:call-template name="KeySection">
					<xsl:with-param name="section" select="//article/key-subordinate-legislation" />
					<xsl:with-param name="title" select="'&keySubordinateLegislationHeading;'" />
				</xsl:call-template>
				<xsl:call-template name="KeySection">
					<xsl:with-param name="section" select="//article/key-quasi-legislation" />
					<xsl:with-param name="title" select="'&keyQuasiLegislationHeading;'" />
				</xsl:call-template>
				<xsl:call-template name="KeySection">
					<xsl:with-param name="section" select="//article/key-european-union-legislation" />
					<xsl:with-param name="title" select="'&keyEuropeanUnionLegislationHeading;'" />
				</xsl:call-template>
			</xsl:element>

			<xsl:element name="div">
				<xsl:attribute name="id">&casesSectionId;</xsl:attribute>
				<xsl:element name="h2">
					<xsl:if test="not($DeliveryMode)">
					<xsl:attribute name="class">&headtextClass;</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', '&casesSectionId;')"/>
					</xsl:attribute>
					<xsl:text>&keyCasesTitle;</xsl:text>
				</xsl:element>
				<xsl:apply-templates select="//article/key-cases"/>
			</xsl:element>
			<xsl:element name="div">
				<xsl:attribute name="id">&readingSectionId;</xsl:attribute>
				<xsl:element name="h2">
					<xsl:if test="not($DeliveryMode)">
					<xsl:attribute name="class">&headtextClass;</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', '&readingSectionId;')"/>
					</xsl:attribute>
					<xsl:text>&readingTitle;</xsl:text>
				</xsl:element>
				<xsl:call-template name="KeySection">
					<xsl:with-param name="section" select="//article/key-texts" />
					<xsl:with-param name="title" select="'&keyTextsHeading;'" />
				</xsl:call-template>
				<xsl:call-template name="KeySection">
					<xsl:with-param name="section" select="//article/further-reading" />
					<xsl:with-param name="title" select="'&furtherReadingHeading;'" />
				</xsl:call-template>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="KeySection">
		<xsl:param name="section"></xsl:param>
		<xsl:param name="title"></xsl:param>
		<xsl:element name ="div">
			<xsl:attribute name="class">&paraMainClass;</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="concat('&internalLinkIdPrefix;', name($section))"/>
			</xsl:attribute>
			<xsl:element name="h3">
				<xsl:if test="not($DeliveryMode)">
				<xsl:attribute name="class">&headtextClass;</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="$title"/>
			</xsl:element>
			<xsl:apply-templates select="$section"/>
		</xsl:element>
	</xsl:template>
	<!--key sections END-->

	<xsl:template name="DeliveryOverviewSection">
		<!--inserting first Title and subtitle before lists. others will be included into preceding list items-->
		<xsl:if test="//overview/section[1]/title/text()">
			<xsl:apply-templates select="//overview/section[1]/title"/>
			<xsl:call-template name="SubtitleForDelivery">
				<xsl:with-param name="title" select="//overview/section[1]/paragraph[1]/title" />
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="//overview/paragraph[1]/title/text()">
			<xsl:call-template name="SubtitleForDelivery">
				<xsl:with-param name="title" select="//overview/paragraph[1]/title" />
			</xsl:call-template>
		</xsl:if>
		<xsl:element name="ol">
			<xsl:attribute name="class">
				<xsl:text>&paraMainClass;<![CDATA[ ]]>&ukListClass;<![CDATA[ ]]>&coListDecimalClass;<![CDATA[ ]]>&khList;</xsl:text>
			</xsl:attribute>
			<xsl:for-each select="//overview/section/paragraph/para | //overview/paragraph/para">
				<xsl:element name="li">
					<xsl:call-template name="paragraphItem"/>
					<xsl:if test="not(following-sibling::*)">
						<xsl:choose>
							<xsl:when test="not(../following-sibling::paragraph) and boolean(//article/overview/section/node())">
								<xsl:if test="../../following-sibling::section[1]/title/text()">
									<xsl:apply-templates select="../../following-sibling::section[1]/title"/>
								</xsl:if>
								<xsl:if test="../../following-sibling::section[1]/paragraph[1]/title/text()">
									<xsl:call-template name="SubtitleForDelivery">
										<xsl:with-param name="title" select="../../following-sibling::section[1]/paragraph[1]/title" />
									</xsl:call-template>
								</xsl:if>
							</xsl:when>
							<xsl:otherwise>
								<xsl:if test="../following-sibling::paragraph[1]/title/text()">
									<xsl:call-template name="SubtitleForDelivery">
										<xsl:with-param name="title" select="../following-sibling::paragraph[1]/title" />
									</xsl:call-template>
								</xsl:if>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template name ="SubtitleForDelivery">
		<xsl:param name="title"/>
		<xsl:element name="h4">
			<xsl:attribute name="class">&headtextClass;</xsl:attribute>
			<xsl:value-of select="$title"/>
		</xsl:element>
	</xsl:template>

	<!-- lists -->
	<xsl:template match="article//*[@prefix-rules='alpha']">
		<xsl:call-template name="listItemsDisplay">
			<xsl:with-param name="class" select="'&lowerAlphaListClass;'" />
			<xsl:with-param name="tag" select="'ul'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="article//*[@prefix-rules='bullet']">
		<xsl:call-template name="listItemsDisplay">
			<xsl:with-param name="class" select="'&bulletListClass;'" />
			<xsl:with-param name="tag" select="'ul'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="article//*[@prefix-rules='number']">
		<xsl:call-template name="listItemsDisplay">
			<xsl:with-param name="class" select="'&coListDecimalClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="listItemsDisplay">
		<xsl:param name="class"/>
		<!--adding for delivery. alpha lists should be an ul tag, other ol tag. in other case delivery won't work properly-->
		<xsl:param name="tag" select="'ol'"/>
		<xsl:element name="div">
			<xsl:attribute name="class">&overviewParagraphClass;</xsl:attribute>
			<xsl:if test="title/text()">
				<xsl:element name="h4">
					<xsl:attribute name="class">&headtextClass;</xsl:attribute>
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', '&articleOverviewSectionId;', count(preceding::paragraph/title | preceding::section/title | ../title))"/>
					</xsl:attribute>
					<xsl:value-of select="title"/>
				</xsl:element>
			</xsl:if>
			<xsl:element name="{$tag}">
				<xsl:attribute name="class">
					<xsl:text>&paraMainClass;<![CDATA[ ]]>&ukListClass;<![CDATA[ ]]></xsl:text>
					<xsl:value-of select="$class"/>
					<!--fix for PDF alignment lists-->
					<xsl:if test="$DeliveryMode">
						<xsl:text><![CDATA[ ]]>&khList;</xsl:text>
					</xsl:if>
				</xsl:attribute>
				<xsl:if test="$tag='ol'">
					<xsl:attribute name="start">
						<xsl:choose>
							<xsl:when test="boolean(//article/overview/section/node())">
								<xsl:value-of select="count(./preceding-sibling::paragraph/para | ../preceding-sibling::*/paragraph/para)+1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="count(./preceding-sibling::paragraph/para)+1"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:if>
				<xsl:for-each select="para">
					<xsl:element name="li">
						<xsl:call-template name="paragraphItem"/>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- lists END -->

	<xsl:template name="paragraphItem">
		<xsl:choose>
			<xsl:when test="count(./quote-block) &gt; 0">
				<xsl:for-each select="./quote-block">
					<xsl:if test="./preceding-sibling::text() | ./preceding-sibling::*[//text()]">
						<xsl:element name="div">
							<xsl:attribute name="class">&paraMainClass;</xsl:attribute>
							<xsl:apply-templates select="./preceding-sibling::node()"/>
						</xsl:element>
					</xsl:if>
					<xsl:apply-templates select="."/>
				</xsl:for-each>
				<xsl:if test="./quote-block[last()]/following-sibling::text() | ./quote-block[last()]/following-sibling::*[//text()]">
					<xsl:element name="div">
						<xsl:attribute name="class">&paraMainClass;</xsl:attribute>
						<xsl:apply-templates select="./quote-block[last()]/following-sibling::node()"/>
					</xsl:element>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="div">
					<xsl:attribute name="class">&paraMainClass;</xsl:attribute>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!--move title outside main list block.title implemented in listItemsDisplay template-->
	<xsl:template match="paragraph/title"/>

	<!-- quotes -->
	<xsl:template match="quote-block">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="quote-para"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="div">
					<xsl:attribute name="class">&ukQuotesClass;</xsl:attribute>
					<xsl:apply-templates select="quote-para"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="quote-para">
		<xsl:element name="div">
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<xsl:attribute name="class">&ukQuotesClass;</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">&paraMainClass;</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	<!-- quotes END -->

	<xsl:template match="email">
		<xsl:element name="a">
			<xsl:attribute name="href">
				<xsl:value-of select="@href"/>
			</xsl:attribute>
			<xsl:value-of select="normalize-space(.)"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="url">
		<xsl:element name="a">
			<xsl:attribute name="href">
				<xsl:value-of select="@href | @target"/>
			</xsl:attribute>
			<xsl:attribute name="target">&linkTargetNewWindow;</xsl:attribute>
			<xsl:attribute name="title">&topicOverviewExternalLinkTitle;</xsl:attribute>
			<xsl:value-of select="normalize-space(.)"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="section/title" >
		<xsl:element name="h3">
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<xsl:attribute name="class">&sectionHeadTextClass;</xsl:attribute>

				</xsl:when>
				<xsl:otherwise>
			<xsl:attribute name="class">&headtextClass;</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:attribute name="id">
				<xsl:value-of select="concat('&internalLinkIdPrefix;', '&articleOverviewSectionId;', count(preceding::paragraph/title | preceding::section/title))"/>
			</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="authored-articles">
		<xsl:if test="string-length(normalize-space(//document/author-profile/author/biography)) &gt; 0">
			<xsl:element name="hr"/>
		</xsl:if>
		<xsl:element name="h3">&authorTopicsSectionName;</xsl:element>
		<xsl:element name="ul">
			<xsl:for-each select="authored-article">
				<xsl:element name="li">
					<xsl:element name="a">
						<xsl:attribute name="href">
							<xsl:call-template name="GetDocumentUrl">
								<xsl:with-param name="documentGuid">
									<xsl:value-of select="@guid"/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template name="BuildLoggedOutDocumentHeader">
		<xsl:element name="div">
			<xsl:attribute name="id">&coDocContentHeaderPrelim;</xsl:attribute>
			<xsl:attribute name="class">&excludeFromAnnotationsClass; &disableHighlightFeaturesClass; &topicOverviewDocClass;</xsl:attribute>

			<xsl:element name="div">
				<xsl:attribute name="id">&coDocHeaderContainer;</xsl:attribute>

				<xsl:element name="div">
					<xsl:attribute name="class">&paraMainClass;</xsl:attribute>
					<xsl:call-template name="BuildLoggedOutDocumentHeaderContent" />
				</xsl:element>

			</xsl:element>

		</xsl:element>
	</xsl:template>

	<xsl:template name="BuildLoggedOutDocumentHeaderContent">
		<xsl:element name="h1">
			<xsl:attribute name="class">&titleClass;</xsl:attribute>
			<xsl:value-of select="//metadata.block/md.descriptions/md.title"/>
		</xsl:element>
		<xsl:call-template name="BuildLoggedOutDocumentAuthoredBySection"/>
	</xsl:template>

	<xsl:template name="BuildLoggedOutDocumentAuthoredBySection">
		<xsl:element name="div">
			<xsl:attribute name="class">&authorsPlainSection;</xsl:attribute>
			<xsl:element name="strong">&authorSectionName;<![CDATA[: ]]></xsl:element>
			<xsl:apply-templates select="n-metadata/resultList/authors/node()" mode="anonymous" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="//resultList/authors/author" mode="anonymous">
		<xsl:variable name="authorName" select="./display-name"/>
		<xsl:variable name="organisationName" select="./organisation-name"/>
		<xsl:element name="span">
			<xsl:attribute name="class">&authorPlainInfo;</xsl:attribute>

			<xsl:element name="span">
				<xsl:attribute name="class">&authorName;</xsl:attribute>
				<xsl:value-of select="$authorName"/>
			</xsl:element>

			<xsl:if test="$organisationName">
				<xsl:text><![CDATA[ | ]]></xsl:text>
				<xsl:element name="span">
					<xsl:attribute name="class">&authorOrganisation;</xsl:attribute>
					<xsl:value-of select="$organisationName"/>
				</xsl:element>
			</xsl:if>

			<xsl:if test="position() != last()">
				<xsl:text>&semiColon;</xsl:text>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<xsl:template name="BuildDocumentTypeAttribute">
		<xsl:attribute name="data-documenttype">&topicOverviewDocumentType;</xsl:attribute>
	</xsl:template>

	<xsl:template name="BuildMetaInfoColumn"/>
	<xsl:template name="ShowWestlawUkLogo"/>
</xsl:stylesheet>