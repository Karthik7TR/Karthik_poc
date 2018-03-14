<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:include href="Universal.xsl" />
	<xsl:include href="FlatListTransform.xsl" />

	<!-- Document header definition -->
	<xsl:template name="DocumentHeader">
		<div class="&headnotesClass; &centerClass;">
			<div class="&documentHeadClass;">
				<xsl:apply-templates select="/Document/n-docbody/shared-metadata[1]/provNum" />
				<xsl:apply-templates select="/Document/n-docbody/shared-metadata[1]/legislation-title" />
				<xsl:apply-templates select="/Document/n-docbody/shared-metadata[1]/jurisdiction" />
				<xsl:apply-templates select="/Document/n-docbody/shared-metadata[1]/legislation-reference" />
			</div>
		</div>

		<!-- End of a header -->
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- Header items -->
	<xsl:template match="/Document/n-docbody/shared-metadata/legislation-title">
		<div class="&titleClass;">
			<xsl:apply-templates />
			<xsl:if test="/Document/n-docbody/shared-metadata/binary/download-binary[@type = 'pdf']">
				<xsl:call-template name="CreatePDFLink">
					<xsl:with-param name="guid" select="/Document/n-docbody/shared-metadata/binary/download-binary/@href-guid" />
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="para.group/heading">
		<div class="&uBoldClass; &alignHorizontalCenterClass; &paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="
		/Document/n-docbody/shared-metadata/jurisdiction
		| /Document/n-docbody/shared-metadata/legislation-reference
		| /Document/n-docbody/shared-metadata/provNum">
		<div class="&titleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Citation within the empowering-prov -->
	<xsl:template match="empowering-prov/citation">
		<div class="&paratextMainClass; &alignHorizontalRightClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Suppress a shared-metadata tag -->
	<xsl:template match="shared-metadata" />

	<!-- Suppress a metadata.block tag -->
	<xsl:template match="metadata.block" />

	<!-- Emphasis: bold -->
	<xsl:template match="emphasis[@style='bold'] | def-term">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<!-- Emphasis: italic -->
	<xsl:template match="emphasis[@style='italic']">
		<i>
			<xsl:apply-templates />
		</i>
	</xsl:template>

	<!-- Emphasis: bold and italic -->
	<xsl:template match="emphasis[@style='bold-italic']">
		<strong>
			<i>
				<xsl:apply-templates />
			</i>
		</strong>
	</xsl:template>

	<!-- List start definitions -->
	<xsl:template match="label-para[not(./preceding-sibling::*[1][self::label-para])]
								| numbered-para[not(./preceding-sibling::*[1][self::numbered-para])]
								| subprov[not(./preceding-sibling::*[1][self::subprov])]
								| def-para[not(./preceding-sibling::*[1][self::def-para])]">
		<div class="&paraMainClass;">
			<xsl:call-template name="TransformFlatListToHTMLList" />			
		</div>
	</xsl:template>

	<!-- List elements definitions -->
	<xsl:template match="label-para[./preceding-sibling::*[1][self::label-para]]
								| numbered-para[./preceding-sibling::*[1][self::numbered-para]]
								| subprov[./preceding-sibling::*[1][self::subprov]]
								| def-para[./preceding-sibling::*[1][self::def-para]]">
		<!-- Suppress -->
	</xsl:template>

	<xsl:template match="follow-text">
		<xsl:param name="isShown" select="false()" />
		<xsl:if test="$isShown">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<xsl:template name="ListItems">
		<li>
			<div class="&paratextMainClass;">
				<xsl:apply-templates />
			</div>
		</li>
	</xsl:template>

	<xsl:template match="label-para/para | numbered-para/para | subprov/para | def-para/para" priority="2">
		<xsl:apply-templates />
		<xsl:apply-templates select="../following-sibling::follow-text">
			<xsl:with-param name="isShown" select="true()" />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="label-para/label | numbered-para/label | subprov/label">
		<xsl:apply-templates />
		<xsl:value-of select="' '" />
	</xsl:template>

	<!-- Suppress id attribute -->
	<xsl:template match="para" priority="1">
		<xsl:call-template name="para">
			<xsl:with-param name="divId" select="''" />
		</xsl:call-template>
	</xsl:template>

	<!-- List root definition -->
	<xsl:template match="list">
		<ul>
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<!-- List item definition-->
	<xsl:template match="list/item">
		<li>
			<xsl:apply-templates select="*[not(self::label)]"/>
		</li>
	</xsl:template>

	<!-- History notes parent definition -->
	<xsl:template match="history">
		<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
			<xsl:value-of select="'&historyNotesText;'" />
		</h2>
		<div class="&footnoteSectionClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Wrap editorial notes to make them look like footnotes -->
	<xsl:template match="editorial-note">
		<div class="&footnoteSectionClass;">
			<h2 class="&footnoteSectionTitleClass;">
				<xsl:text>&newZealandEditorialNotesText;</xsl:text>
			</h2>
			<div class="&footnoteSectionClass;">
				<xsl:apply-templates />
			</div>
		</div>
	</xsl:template>

	<!-- Suppress the editorial note within a history block -->
	<xsl:template match="history/editorial-note">
		<xsl:param name="preserveEditorialNote" select="false()" />
		<xsl:if test="$preserveEditorialNote">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<!-- Wrap historic block within a history node in order to make it look like a footnote -->
	<xsl:template match="historic">
		<div class="&footnoteSectionClass;">
			<h2 class="&footnoteSectionTitleClass;">
				<xsl:text>&newZealandHisoricText;</xsl:text>
			</h2>
			<div class="&footnoteSectionClass;">
				<xsl:apply-templates />
			</div>
		</div>
	</xsl:template>

	<!-- Do not wrap editorial note paragraph with a paragraph -->
	<xsl:template match="history/editorial-note/para" priority="3">
		<xsl:value-of select="' '" />
		<xsl:apply-templates />
	</xsl:template>

	<!-- History note / editorial note without a history note definition -->
	<xsl:template match="history-note | history/editorial-note[not(./preceding-sibling::*[1][self::history-note])]">
		<div class="&paratextMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- History note + all following editorial notes definition -->
	<xsl:template match="history-note[./following-sibling::*[1][self::editorial-note]]">
		<xsl:variable name="thisNote" select="." />
		<div class="&paratextMainClass;">
			<xsl:apply-templates />
			<xsl:apply-templates select="
				./following-sibling::*[self::editorial-note]
					[
						./preceding-sibling::*[self::history-note][1] = $thisNote
					]">
				<xsl:with-param name="preserveEditorialNote" select="true()" />
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<!-- Assent and Forms signature: align to the right-->
	<xsl:template match="assent | end">
		<div class="&alignHorizontalRightClass; &italicClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Forms page headers -->
	<xsl:template match="tgroup[@align='center']">
		<xsl:call-template name="TGroupTemplate">
			<xsl:with-param name="checkNoColWidthExists" select="'true'"/>
			<xsl:with-param name="additionalClass" select="'&centeredClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Enable borders for all tables, except for those having frame='none' or frame='top' attribute -->
	<xsl:template match="table">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&tableGroupClass;</xsl:text>
				<xsl:if test="not(./@frame='none' or ./@frame='top')">
					<xsl:value-of select="' '" />
					<xsl:text>&tableGroupBorderClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="table[@frame='none']/tgroup/tbody/row/entry[@namest and @nameend and @colsep and @rowsep]">
		<td>
			<div class="&alignHorizontalCenterClass;">
				<xsl:apply-templates />
			</div>
		</td>
	</xsl:template>

	<!-- Prov.group header definition -->
	<xsl:template match="prov.group/heading">
		<div class="&paraMainClass; &alignHorizontalCenterClass; &italicClass; &uBoldClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Provisions table -->
	<xsl:template match="inline-toc[./inline-toc-document or ./inline-toc-node]">
		<div class="&footnoteSectionClass;">
			<h2 class="&footnoteSectionTitleClass;">
				<xsl:text>&newZealandContentText;</xsl:text>
			</h2>
			<div class="&footnoteSectionClass;">
				<xsl:if test="./inline-toc-document[@type='definition']">
					<div class="&paraMainClass; &uBoldClass;">
						<xsl:text>&editorialNoteText;</xsl:text>
					</div>
				</xsl:if>
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
			</div>
		</div>
	</xsl:template>

	<!-- Regular provision table content definition -->
	<xsl:template match="inline-toc-document">
		<div class="&paraMainClass; &indentLeft3Class;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Provision group -->
	<xsl:template match="inline-toc-document[@type='provision-group']">
		<div class="&paraMainClass; &indentLeft2Class; &uBoldClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Provisions table: provision -->
	<xsl:template match="inline-toc-document[@type='provision']">
		<div class="&paraMainClass; &indentLeft3Class;">
			<xsl:call-template name="IncludeLabelLink" />
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Part or a schedule -->
	<xsl:template match="inline-toc-document[@type='part' or @type='schedule']">
		<div class="&paraMainClass; &indentLeft2Class; &uBoldClass;">
			<xsl:call-template name="IncludeLabelLink" />
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Editorial notes -->
	<xsl:template match="inline-toc-document[@type='definition']">
		<div class="&paraMainClass; &indentLeft2Class;">
			<xsl:call-template name="IncludeLabelLink" />
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Label link -->
	<xsl:template name="IncludeLabelLink">
		<xsl:apply-templates select="./@label" />
		<xsl:value-of select="' '"/>
	</xsl:template>

	<!-- Suppress cover headers -->
	<xsl:template match="cover/title | cover/reference" />

	<!-- Amendment list -->
	<xsl:template match="amendment-list">
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Amendment list: header -->
	<xsl:template match="amendment-list/heading">
		<div class="&uBoldClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Amendment item -->
	<xsl:template match="amendment-list/amendment-item">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Preamble or pursuant or enactment nodes: add vertical indent -->
	<xsl:template match="preamble | pursuant | enactment">
		<div class="&indentTopClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Long title definition -->
	<xsl:template match="front/long-title">
		<div class="&uBoldClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Suppress links in history -->
	<xsl:template match="history/pitv" />

	<!-- Promulgation header definition -->
	<xsl:template match="promulgation/heading">
		<div class="&paraMainClass; &uBoldClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="prov | form">
		<div class="&sectionTitleClass; &uBoldClass;">
			<xsl:apply-templates select="label">
				<xsl:with-param name="isShown" select="true()" />
			</xsl:apply-templates>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:apply-templates select="heading" />
		</div>
		<xsl:apply-templates select="*[not(self::label or self::heading)]" />
	</xsl:template>

	<xsl:template match="schedule/heading | schedule/label">
		<div class="&titleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Labels (suppressed by default) -->
	<xsl:template match="label">
		<xsl:param name="isShown" select="false()" />
		<xsl:if test="$isShown">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<!-- The template for different types of paragraphs -->
	<xsl:template match="promulgation | admin-office | issue-authority | cf">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="issue-authority[following-sibling::*[1][self::gazette-date]]">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<!-- Legislation history root definition -->
	<xsl:template match="leg-history[history-item|admin-office]">
		<div class="&tableGroupClass;">
			<table>
				<xsl:apply-templates />
			</table>
		</div>
	</xsl:template>
	
	<!-- Legislation history heading definition -->
	<xsl:template match="leg-history/heading">
		<tr>
			<td colspan="2">
				<div class="&paraMainClass; &uBoldClass; &alignHorizontalCenterClass;">
					<xsl:apply-templates />
				</div>
			</td>
		</tr>
	</xsl:template>
	
	<!-- Legislation history item definition -->
	<xsl:template match="leg-history/history-item">
		<tr>
			<td>
				<xsl:apply-templates select="date" />
			</td>
			<td>
				<xsl:apply-templates select="description" />
			</td>
		</tr>
	</xsl:template>
	
	<!-- Admin-office within the legislation history root -->
	<xsl:template match="leg-history/admin-office">
		<tr>
			<td colspan="2">
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
			</td>
		</tr>
	</xsl:template>

	<!-- Entry point -->
	<xsl:template match="Document/n-docbody" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeNZLegislationClass;'"/>
			</xsl:call-template>

			<!-- Document header -->
			<xsl:call-template name="DocumentHeader" />

			<!-- Document content -->
			<xsl:apply-templates />

			<!-- Copyright block -->
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText" select="'&newZealandCopyrightText;'" />
				<xsl:with-param name="endOfDocumentCopyrightTextVerbatim" select="'true'" />
			</xsl:call-template>
		</div>
	</xsl:template>
</xsl:stylesheet>
