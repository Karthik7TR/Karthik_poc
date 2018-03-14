<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKCurrentAwareness.xsl" forceDefaultProduct="true"/>
	<xsl:include href="UKGeneralBlocks.xsl" />

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="bodyContent" select="//data/body or //data/abstract or //data/in_force or //data/progress 
		or //data/outcome or //data/legislation or //data/cases or //data/companies or //data/grey-materials or //data/urls"/>
	<xsl:variable name="contentType" select ="'&ukWestlawContent;'"/>
	<xsl:variable name="pdfIcon" select="'&ukIconPdf;'"/>
	<xsl:variable name="showLoading" select="true()"/>

	<xsl:template name="BuildDocumentTypeAttribute">
		<xsl:attribute name ="data-documenttype">
			<xsl:value-of select="'&currentAwarenessAbstractType;'"/>
		</xsl:attribute>
	</xsl:template>

	<!-- HEADER-->
	<xsl:template name="BuildCurrentAwarenessHeaderContent">
		<xsl:param name="titleSelector"/>
		<h1 class="&title;">
			<xsl:apply-templates select="$titleSelector" />
		</h1>
	</xsl:template>

	<xsl:template name="BuildDocumentHeaderContent">
		<xsl:call-template name="BuildCurrentAwarenessHeaderContent">
			<xsl:with-param name="titleSelector" select="//data/title"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="BuildLoggedOutDocumentHeaderContent">
		<xsl:call-template name="BuildCurrentAwarenessHeaderContent">
			<xsl:with-param name="titleSelector" select="n-docbody/title"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="data/title | n-docbody/title" priority="5">
		<xsl:apply-templates/>
	</xsl:template>
	<!-- HEADER END -->

	<!-- ToC -->
	<xsl:template name="BuildDocumentTocContent">
		<xsl:call-template name="WriteTocListOpen">
			<xsl:with-param name="includeClass" select="'&coKhTocOlList; &ukResearchToCList;'"/>
		</xsl:call-template>

		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemAnchor" select="'#'"/>
			<xsl:with-param name="IsAnchor" select="false()"/>
			<xsl:with-param name="TocItemCaption" select="'&abstractText;'"/>
		</xsl:call-template>

		<xsl:call-template name="WriteTocListClose"/>
	</xsl:template>
	<!-- ToC END -->

	<!-- BODY -->
	<xsl:template name="BuildDocumentBody">
		<div class="&ukMainDocumentContent; &docDivision;">
			<xsl:choose>
				<xsl:when test="$bodyContent">
					<xsl:apply-templates select="n-docbody/document/data/body"/>
					<xsl:apply-templates select="n-docbody/document/data/abstract"/>
					<xsl:apply-templates select="n-docbody/document/data/in_force"/>
					<xsl:apply-templates select="n-docbody/document/data/progress"/>
					<xsl:apply-templates select="n-docbody/document/data/outcome"/>
					<xsl:apply-templates select="n-docbody/document/data/citation[@hansard='true']"/>
					<xsl:apply-templates select="n-docbody/document/data/legislation/leg_enabling"/>
					<xsl:apply-templates select="n-docbody/document/data/legislation/leg_amended"/>
					<xsl:apply-templates select="n-docbody/document/data/legislation/leg_repealed"/>
					<xsl:apply-templates select="n-docbody/document/data/cases"/>
					<xsl:apply-templates select="n-docbody/document/data/legislation/leg_referred"/>
					<xsl:apply-templates select="n-docbody/document/data/companies"/>
					<xsl:apply-templates select="n-docbody/document/data/grey-materials"/>
					<xsl:apply-templates select="n-docbody/document/data/urls"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="BuildBodyField">
						<xsl:with-param name="fieldContent" select="'&ukNoAbstractIsAvailableText;'"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="abstract">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&abstractText;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="in_force">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukInForce2;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:call-template name="BuildDate"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="progress">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukProgress2;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="outcome">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukOutcome;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="cases">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukCasesReferred;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="citation[@hansard='true']">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukHansard;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="legislation/leg_enabling | legislation/leg_amended | legislation/leg_repealed | legislation/leg_referred"/>
	
	<xsl:template match="legislation/leg_enabling[1]" priority="5">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukEnablingLegislation;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="//legislation/leg_enabling">
					<div>
						<xsl:apply-templates select="leg_citation"/>
					</div>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="legislation/leg_amended[1]" priority="5">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukAmendedLegislation;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="//legislation/leg_amended">
					<div>
						<xsl:apply-templates select="leg_citation"/>
					</div>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="legislation/leg_repealed[1]" priority="5">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukRepealedLegislation;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="//legislation/leg_repealed">
					<div>
						<xsl:apply-templates select="leg_citation"/>
					</div>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="legislation/leg_referred[1]" priority="5">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukLegislationReferred;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="//legislation/leg_referred">
					<div>
						<xsl:apply-templates select="leg_citation"/>
					</div>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companies">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukCompanies;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="grey-materials">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukFullText;'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="grey-materials/link[@ttype = 'smg/img/greymaterials/binary/pdf']">
		<div>
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlobV1', concat('imageGuid=', @tuuid), concat('imageFileName=', ./cite.query/text()), concat('extension=', 'pdf'), concat('targetType=', '&inlineParagraph;'), concat('originationContext=', '&docDisplayOriginationContext;'), '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs))"/>
				</xsl:attribute>
				<xsl:attribute name="title">
					<xsl:value-of select="'&viewPdfAltText;'"/>
				</xsl:attribute>
				<xsl:attribute name="type">
					<xsl:value-of select="'&pdfMimeType;'"/>
				</xsl:attribute>
				<xsl:apply-templates select="./cite.query"/>
				<xsl:text>&nbsp;&nbsp;</xsl:text>
				<img>
					<xsl:attribute name="src">
						<xsl:value-of select="$Images"/>
						<xsl:value-of select="$pdfIcon"/>
					</xsl:attribute>
					<xsl:attribute name="class">
						<xsl:value-of select="'&pdfIconClass;'"/>
					</xsl:attribute>
					<xsl:attribute name="alt">
						<xsl:value-of select="'&viewPdfAltText;'"/>
					</xsl:attribute>
				</img>
			</xsl:element>
		</div>
	</xsl:template>
  
	<xsl:template match="cite.query" priority="5">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="urls">
		<xsl:call-template name="BuildBodyField">
			<xsl:with-param name="fieldCaption" select="'&ukRelatedLinks;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="urls/url">
		<div class="&wordBreakClass;">
				<xsl:call-template name="LinkOpensInNewTab">
					<xsl:with-param name="href" select="."/>
					<xsl:with-param name="title" select="'&externalLinkHoverOverText;'"/>
				</xsl:call-template>
		</div>
	</xsl:template>

	<!-- list -->
	<xsl:template match="list">
		<ul class="&docUnorderedList;">
			<xsl:apply-templates/>
		</ul>
	</xsl:template>

	<xsl:template match="listitem">
		<li>
			<xsl:apply-templates/>
		</li>		
	</xsl:template>
	<!-- BODY END -->

	<!-- METADATA -->
	<xsl:template name="BuildMetaInfoColumnContent">
		<div class="&metaBlockBorderBottom;">
			<xsl:call-template name="MetaDocType"/>
			<xsl:call-template name="MetaContributors"/>
		</div>
		<xsl:call-template name="MetaCitation"/>
			<xsl:call-template name="MetaCourt"/>
			<xsl:call-template name="MetaJudgmentDate"/>
			<xsl:call-template name="MetaJudge"/>
			<xsl:call-template name="MetaRoyalAssent"/>
			<xsl:call-template name="MetaPublicationDate"/>
			<xsl:call-template name="MetaSeries"/>
			<xsl:call-template name="MetaAdded"/>
			<xsl:call-template name="MetaPublisher"/>
			<xsl:call-template name="MetaISBN"/>
			<xsl:call-template name="MetaPrice"/>
		<xsl:call-template name="MetaSubject"/>
		<xsl:call-template name="MetaOtherRelatedSubjects"/>
		<xsl:call-template name="MetaKeywords"/>
	</xsl:template>

	<xsl:template name="MetaDocType">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/entry_type"/>
				<xsl:apply-templates select="n-docbody/document/data/sec_entry_type"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="MetaContributors">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/contributors"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="MetaCitation">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&citationText;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/citation[not(@hansard)]"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="MetaCourt">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukCourtText;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/court"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>
		
	<xsl:template name="MetaJudge">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukJudges;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:for-each select="n-docbody/document/data/judge">
					<xsl:apply-templates/>
					<xsl:if test="position()!=last()">
						<xsl:text>; </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaJudgmentDate">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukJudgmentDate2;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:call-template name="BuildDate">
					<xsl:with-param name="current" select="n-docbody/document/data/judgment_date"/>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaPublicationDate">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukPublicationDate2;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:call-template name="BuildDate">
					<xsl:with-param name="current" select="n-docbody/document/data/publication_date"/>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaSeries">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukSeries;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/series"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="MetaAdded">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukAdded2;'"/>
			<xsl:with-param name="fieldContent">
				<!-- Remove time zone from content in format "May 14, 2013 08:32 (BST)". Leave just "May 14, 2013 08:32" -->
				<xsl:variable name="node" select="n-docbody/document/metadata.block/md.identifiers/md.cites/md.primarycite"/>
				<xsl:value-of select="normalize-space(substring-before($node, '('))"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="MetaPublisher">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukPublisherNoColon;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/publisher"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="MetaISBN">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukISBN;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/isbn"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="MetaPrice">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukPriceNoColon;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/price"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaSubject">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaMainSubject;'"/>
			<xsl:with-param name="fieldCaption" select="'&euSubject;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/subjects[1]"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaOtherRelatedSubjects">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaSubjects;'"/>
			<xsl:with-param name="fieldCaption" select="'&casesOtherRelatedSubjectsText;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/subjects[position() > 1]" />
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaKeywords">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaKeywords;'"/>
			<xsl:with-param name="fieldCaption" select="'&euKeywords;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/keywords"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="MetaRoyalAssent">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaField;'"/>
			<xsl:with-param name="fieldCaption" select="'&ukRoyalAssent2;'"/>
			<xsl:with-param name="fieldContent">
				<xsl:apply-templates select="n-docbody/document/data/royal_assent"/>
			</xsl:with-param>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="author | entry_type | sec_entry_type">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="citation[not(@hansard)] | contributors | court | judge | isbn | keywords | md.cites/md.primarycite | price | publisher | royal_assent | series">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="subjects" priority="5">
		<xsl:apply-templates select="subject_heading" mode="display"/>
		<xsl:apply-templates select="main_subject" mode="display"/>
		<xsl:if test="position()!=last()">
			<xsl:text>; </xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="main_subject"  mode="display">
		<xsl:choose>
			<xsl:when test="not(preceding-sibling::subject_heading)">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:when test="preceding-sibling::subject_heading != .">
				<xsl:text> - </xsl:text>
				<xsl:apply-templates/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="subjects[1] | subjects[2]"/>
	<!-- METADATA END -->

	<xsl:template name="BuildDate">
		<xsl:param name="current" select="."/>
		<xsl:choose>
			<xsl:when test="$current/@day">
				<xsl:value-of select="DateTimeExtension:BuildDate($current/@year, $current/@month, $current/@day, 'd MMMM yyyy')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$current"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="BuildEndOfDocument">
		<xsl:call-template name="EmptyEndOfDocument">
			<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>