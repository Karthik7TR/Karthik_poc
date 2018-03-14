<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKCasesLoc.xsl" forceDefaultProduct="true"/>
	<xsl:include href="UKCasesLocToc.xsl"/>
	<xsl:include href="UKCasesStatuses.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name ="contentType" select ="'&ukWestlawContent;'"/>
	<xsl:variable name ="showLoading" select="true()"/>

	<xsl:variable name="hasCaseJudgment">
		<xsl:choose>
			<xsl:when test="//metadata.block//md.fulltext[@href.format='&officialTranscriptText;']">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="abstract//caseref">
		<xsl:choose>
			<xsl:when test="link">
				<i>
					<xsl:apply-templates select="link" />
				</i>
			</xsl:when>
			<xsl:otherwise>
				<i>
					<xsl:apply-templates />
				</i>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="emphasis">
		<xsl:choose>
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

	<!--<xsl:template name="BuildDocumentToc">
		<xsl:call-template name="BuildDocumentTocInternal">
			<xsl:with-param name="debug" select="true()"/>
		</xsl:call-template>
	</xsl:template>-->
	<xsl:template name="BuildDocumentTypeAttribute">
		<xsl:attribute name ="data-documenttype">
			<xsl:value-of select="'&caseAnalysisType;'" />
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="BuildCaseLocHeaderContent">
		<xsl:param name="partiesSelector"/>
		<xsl:param name="otherNameSelector"/>
		<xsl:param name="shipNameSelector"/>
		<xsl:param name="subnomSelector"/>
		<h1 class="&title;">
			<xsl:apply-templates select="$partiesSelector"/>
			<xsl:if test="$otherNameSelector or $shipNameSelector">
				<span class="&ukWestlawExtraTitle;">
					<xsl:apply-templates select="$otherNameSelector"/>
					<xsl:apply-templates select="$shipNameSelector"/>
				</span>
			</xsl:if>
		</h1>
		<div class="&coProductName;">
			<xsl:apply-templates select="$subnomSelector"/>
		</div>
	</xsl:template>

	<xsl:template name="BuildDocumentHeaderContent">
		<xsl:call-template name="BuildCaseLocHeaderContent">
			<xsl:with-param name="partiesSelector" select="//casegroup/parties"/>
			<xsl:with-param name="otherNameSelector" select="//casegroup/othername"/>
			<xsl:with-param name="shipNameSelector" select="//casegroup/shipname"/>
			<xsl:with-param name="subnomSelector" select="//casegroup/subnom"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="BuildLoggedOutDocumentHeaderContent">
		<xsl:call-template name="BuildCaseLocHeaderContent">
			<xsl:with-param name="partiesSelector" select="n-docbody/parties"/>
			<xsl:with-param name="otherNameSelector" select="n-docbody/othername"/>
			<xsl:with-param name="shipNameSelector" select="n-docbody/shipname"/>
			<xsl:with-param name="subnomSelector" select="n-docbody/subnom"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="parties">
		<xsl:apply-templates select="partya"/>
		<xsl:apply-templates select="partyb"/>
	</xsl:template>

	<xsl:template match="partyb">
		<xsl:if test="string-length(.) &gt; 0">
			<xsl:text>&ukVersus;</xsl:text>
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="othername|shipname">
		<xsl:if test="preceding-sibling::othername|preceding-sibling::shipname">
			<br/>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="subnom">
		<xsl:choose>
			<xsl:when test="position() > 1">
				<xsl:text>, </xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="string-length(.) &gt; 0">
					<xsl:text>&ukAlsoKnownAs;</xsl:text>
					<xsl:text disable-output-escaping="yes"><![CDATA[ ]]></xsl:text>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template name="BuildDocumentBody">
		<div class="&ukMainDocumentContent; &docDivision;">
			<a>
				<xsl:attribute name="id">
					<xsl:value-of select ="concat('&internalLinkIdPrefix;', '&ukMainDocumentContent;')"/>
				</xsl:attribute>
			</a>
			<xsl:choose>
				<xsl:when test="string-length(//content/abstract) &gt; 0">
					<xsl:apply-templates select="//content/abstract"/>
				</xsl:when>
				<xsl:otherwise>
					<div class="&noCaseDigestExist;">
						<xsl:text>&noCaseDigestExistText;</xsl:text>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</div>
		<xsl:call-template name="AttachedFileForDocument"/>
		<a id="&ukReferencesOffset;"></a>
	</xsl:template>

	<xsl:template name="BuildMetaInfoColumnContent">
		<xsl:variable name="statusCode" select="n-metadata/metadata.block/md.history/md.keycite/md.flag.color.code" />
		<xsl:call-template name="DocumentStatusDetailed">
			<xsl:with-param name="statusCode" select="$statusCode"/>
		</xsl:call-template>
		<xsl:call-template name="graphicalHistoryLink"/>
		<xsl:apply-templates select="n-docbody/document/casegroup//court.name"/>
		<xsl:apply-templates select="n-docbody/document/metadata.block/md.dates/md.publisheddate"/>
		<xsl:call-template name="MetaWhereReported"/>
		<xsl:call-template name="MetaSubject"/>
		<xsl:call-template name="MetaOtherRelatedSubjects"/>
		<xsl:call-template name="MetaKeywords"/>
		<xsl:call-template name="MetaJudge"/>
		<xsl:call-template name="MetaCounsel"/>
		<xsl:call-template name="MetaSolicitor"/>
	</xsl:template>

	<xsl:template name="BuildEndOfDocument">
		<xsl:call-template name="EmptyEndOfDocument">
			<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="buildContentOfCaseBody">
		<xsl:param name="nameTitle"/>
		<xsl:param name="additionalValue"/>
		<xsl:variable name="contents">
			<xsl:choose>
				<xsl:when test="string-length($additionalValue) &gt; 0">
					<xsl:copy-of select="$additionalValue"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length(normalize-space($contents)) &gt; 0">
			<div class="&paraMainClass;">
				<h3>
					<xsl:value-of select="$nameTitle"/>
				</h3>
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="abstract">
		<h2 class="&docHeadText;">&ukCaseDigest;</h2>
		<xsl:apply-templates select="./summary"/>
		<xsl:if test="./*[(name() != 'summary') and (name() != 'held') and (name() != 'result') and (name() != 'bos') and (name() != 'eos')]">
			<xsl:call-template name="buildContentOfCaseBody">
				<xsl:with-param name="nameTitle">&caseDigestAbstract;</xsl:with-param>
				<xsl:with-param name="additionalValue">
					<xsl:apply-templates select="./*[(name() != 'summary') and (name() != 'held') and (name() != 'result')]"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates select="./result"/>
		<xsl:apply-templates select="./held"/>
	</xsl:template>

	<xsl:template match="summary">
		<xsl:call-template name="buildContentOfCaseBody">
			<xsl:with-param name="nameTitle">&caseDigestSummary;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="result">
		<xsl:call-template name="buildContentOfCaseBody">
			<xsl:with-param name="nameTitle">&ukHeld;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="summary/para | para | held">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template name="AttachedFileForDocument">
		<xsl:variable name="contents">
			<xsl:choose>
				<xsl:when test="$hasCaseJudgment = 'true'">
					<xsl:value-of select="'&viewOfficialTranscriptText;'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'&viewLawtelTranscriptText;'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="//document/metadata.block/md.references/md.print.rendition.id">
			<div class="&standardDocAttachment; &hideState;">
				<xsl:call-template name="createDocumentBlobLink">
					<xsl:with-param name="guid" select="//document/metadata.block/md.references/md.print.rendition.id"/>
					<xsl:with-param name="targetType" select="'&inlineParagraph;'"/>
					<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
					<xsl:with-param name="contents" select="$contents"/>
				</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>

	<!--metadata-->

	<xsl:template name="graphicalHistoryLink">
		<xsl:if test="not($DeliveryMode) and (//direct_history/svg/link) and (//direct_history/svg/relationship)">
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="contents">
					<xsl:element name="a">
						<xsl:attribute name="href">
							<xsl:text>#</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="id">
							<xsl:text>&coUkGraphicalHistoryLink;</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:text>&hideStateClass; &defaultButtonClass;</xsl:text>
						</xsl:attribute>
						<xsl:text>&ukGraphicalHistoryText;</xsl:text>
					</xsl:element>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="court.name">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaCourt;'" />
			<xsl:with-param name="fieldCaption" select="'&ukCourtText;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="md.publisheddate">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaDate;'" />
			<xsl:with-param name="fieldCaption" select="'&casesCaptionJudgmentDate;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:call-template name="formatYearMonthDayToDDMMMYYYY">
					<xsl:with-param name="date" select="."/>
					<xsl:with-param name="dayFormatWithZero" select="false()"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaWhereReported">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaCasesWhereReported;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukWhereReported;'"/>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="//where_reported">
					<xsl:apply-templates/>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="link[@tuuid and not(@DMS) and cite.query]">
		<xsl:choose>
			<xsl:when test="(cite.query/@w-ref-type = 'UE') or (cite.query/@w-ref-type = 'UO')">
				<xsl:call-template name="LinkTitle"/>
			</xsl:when>
			<xsl:otherwise>
				<a>
					<xsl:attribute name="href">
						<xsl:call-template name="GetDocumentUrl">
							<xsl:with-param name="documentGuid" select="@tuuid" />
						</xsl:call-template>
					</xsl:attribute>
					<xsl:if test="cite.query/@w-ref-type = 'UW'">
						<xsl:attribute name="target">
							<xsl:text>_blank</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="LinkTitle"/>
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="LinkTitle">
		<xsl:choose>
			<xsl:when test="./cite.query/text() = '&officialTranscriptText;'">
				<xsl:text>&judgmentCaptionJudgment;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
        <xsl:apply-templates select="./cite.query/node()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="citation | newspaper_cite">
		<xsl:if test="string-length(.) &gt; 0">
			<div>
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template match="subjectterm">
		<xsl:apply-templates/>
		<xsl:if test="position()!=last()">
			<xsl:text>; </xsl:text>
		</xsl:if>
	</xsl:template>
		
	<xsl:template match="subject.associated | subject.principal">
		<xsl:apply-templates select="./subjectterm"/>
		<xsl:if test="position()!=last()">
			<xsl:text>; </xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template name="MetaSubject">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaMainSubject;'"/>
			<xsl:with-param name="fieldCaption" select="'&euSubject;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/casegroup//subjects/subject.principal"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaOtherRelatedSubjects">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaSubjects;'"/>
			<xsl:with-param name="fieldCaption" select="'&casesOtherRelatedSubjectsText;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/casegroup//subjects/subject.associated" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaKeywords">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaKeywords;'"/>
			<xsl:with-param name="fieldCaption" select="'&euKeywords;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="n-docbody/document/casegroup/proceeding/content/taxonomy/keywords/keyword">
					<xsl:sort select="."/>
					<xsl:apply-templates/>
					<xsl:if test="position()!=last()">
						<xsl:text>; </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaJudge">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaCasesJudge;'"/>
			<xsl:with-param name="fieldCaption" select="'&euJudge;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="n-docbody/document/casegroup//judges/judge/judge.name">
					<xsl:apply-templates/>
					<xsl:if test="position()!=last()">
						<xsl:text>; </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaCounsel">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaCasesCounsel;'"/>
			<xsl:with-param name="fieldCaption" select="'&judgmentCaptionRepresentation;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="n-docbody/document/casegroup//appearances/appearance.counsel">
					<xsl:text>&ukFor;</xsl:text>
					<xsl:apply-templates select="counsel.Actingfor"/>
					<xsl:text>: </xsl:text>
					<xsl:apply-templates select="counsel.identity"/>
					<!--Check if text doesn't end with full stop-->
					<xsl:if test="'.' != substring(normalize-space(./counsel.identity), string-length(normalize-space(./counsel.identity)), 1)">
						<xsl:text>. </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaSolicitor">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaCasesSolicitor;'"/>
			<xsl:with-param name="fieldCaption" select="'&casesSolicitorText;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="n-docbody/document/casegroup//appearances/appearance.solicitors">
					<xsl:text>&ukFor;</xsl:text>
					<xsl:apply-templates select="solicitors.Actingfor"/>
					<xsl:text>: </xsl:text>
					<xsl:apply-templates select="solicitors.identity"/>
					<!--Check if text doesn't end with full stop-->
					<xsl:if test="'.' != substring(normalize-space(./solicitors.identity), string-length(normalize-space(./solicitors.identity)), 1)">
						<xsl:text>. </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>