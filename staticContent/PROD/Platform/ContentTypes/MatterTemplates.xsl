<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--<xsl:strip-space elements="*"/>-->

	<xsl:variable name="TileViewDelivery" select="$DeliveryFormat = 'WebKitPdf'" />

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:text>&staticTemplates; &disableHighlightFeaturesClass; &excludeFromAnnotationsClass;</xsl:text>
					<xsl:if test="$DeliveryMode and not($TileViewDelivery)">
						<xsl:text> &staticTemplatesText;</xsl:text>
					</xsl:if>
					<xsl:if test="$DeliveryMode and $TileViewDelivery">
						<xsl:text> &staticTemplatesTileViewDelivery;</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>

			<input type="hidden" id="&matterMapImageLinkId;" />

			<xsl:choose>
				<xsl:when test="$TileViewDelivery">
					<xsl:apply-templates select="//matter.plan.template/title" mode="tileViewDelivery" />
					<xsl:apply-templates select="//phase.block"/>
					<xsl:call-template name="wrapWithDiv">
						<xsl:with-param name="class" select="&endOfDocumentCopyrightClass;"/>
						<xsl:with-param name="contents" select="concat('&copy;', '&nbsp;', $currentYear, '&nbsp;', $endOfDocumentCopyrightText)"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>

			<xsl:if test="$PreviewMode">
				<!-- EndOfDocument is called in matter.plan.template/body for non-PreviewMode -->
				<xsl:call-template name="EndOfDocument" />
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="DocumentByWestlaw" priority="1" />

	<xsl:template match="matter.plan.template/title" mode="tileViewDelivery">
		<div id="&coDocContentHeaderPrelim;">
			<div id="&coDocHeaderContainer;">
				<h1 class="&titleClass; &noTocClass;">
					<xsl:apply-templates />
				</h1>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="matter.plan.template/title" name="matterTitle">
		<div id="&coDocContentHeaderPrelim;">
			<div id="&codocumentHeaderBorder;" class="&documentHeaderBorderClass;"></div>
			<div class="&coDocumentActions; &floatRight;"></div>
			<div id="&coDocHeaderContainer;">
				<h1 class="&titleClass; &noTocClass;">
					<xsl:apply-templates />
				</h1>
				<xsl:if test="/Document/n-metadata/metadata.block/md.contributors/md.author/text()">
					<div class="&coProductName;">
						<xsl:value-of select="concat('&byText;', '&nbsp;')" />
						<xsl:for-each select="/Document/n-metadata/metadata.block/md.contributors/md.author">
							<xsl:if test="preceding-sibling::md.author">
								<xsl:text>,&nbsp;</xsl:text>
							</xsl:if>
							<xsl:value-of select="." />
						</xsl:for-each>
					</div>
				</xsl:if>
				<!--<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.contributors" />-->
				<!-- Display extra content such as the status and jurisdiction. -->
				<xsl:call-template name="displayDocumentExtras" />
			</div>
		</div>
	</xsl:template>

	<!-- ****************************** -->
	<!-- * Start Preview Mode display * -->
	<!-- ****************************** -->

	<xsl:template match="n-docbody/title">
		<xsl:call-template name="matterTitle" />
	</xsl:template>

	<xsl:template match="n-docbody/abstract">
		<xsl:call-template name="matterAbstract" />
	</xsl:template>

	<!-- **************************** -->
	<!-- * End Preview Mode display * -->
	<!-- **************************** -->

	<!-- This prevents abstract processing in Bin.xsl -->
	<xsl:template match="matter.plan.template/abstract" />

	<xsl:template match="matter.plan.template/abstract" mode="ApplyStyles">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Needed to add styling in PreviewMode -->
	<xsl:template match="n-docbody/abstract" mode="ApplyStyles">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template name="matterAbstract">
		<xsl:choose>
			<xsl:when test="$DeliveryMode or $PreviewMode">
				<div class="&staticTemplatesAbstract; &borderTopClass;">
					<xsl:choose>
						<xsl:when test="$DeliveryMode">
							<xsl:apply-templates select="//n-docbody/matter.plan.template/abstract" mode="ApplyStyles" />
						</xsl:when>
						<xsl:when test="$PreviewMode">
							<xsl:apply-templates select="//n-docbody/abstract" mode="ApplyStyles" />
						</xsl:when>
					</xsl:choose>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="FullAbstract">
					<!-- Do not remove this <div>, below is a note from the Document Extension -->
					<!-- You need to wrap the incoming XML with a root element. You can just use an empty <div>. This is needed to insure a proper XPathNavigator is returned with single root node. -->
					<div>
						<xsl:apply-templates select="//n-docbody/matter.plan.template/abstract" mode="ApplyStyles" />
					</div>
				</xsl:variable>

				<xsl:variable name="TruncatedAbstract">
					<xsl:copy-of select="DocumentExtension:LimitTextByCharacterCount($FullAbstract, &LimitAbstractByCharacterCountValue;)"/>
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="string-length($FullAbstract) != string-length($TruncatedAbstract)">
						<!-- The abstract was truncated, display the partial abstract and add a 'Show More' link -->
						<div id="&staticTemplatesAbstract;" class="&staticTemplatesAbstract;">
							<xsl:copy-of select="$TruncatedAbstract"/>
							<a href="javascript:void(0);" id="&staticTemplatesAbstractShowMore;">Show More</a>
						</div>

						<!-- The entire abstract, needed for the lightbox when 'Show More' is clicked -->
						<div id="&staticTemplatesAbstractFullText;" class="&hideStateClass;">
							<xsl:copy-of select="$FullAbstract" />
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div id="&staticTemplatesAbstract;" class="&staticTemplatesAbstract;">
							<xsl:apply-templates select="//n-docbody/matter.plan.template/abstract" mode="ApplyStyles" />
						</div>
					</xsl:otherwise>
				</xsl:choose>

			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="list[@type='bulleted']">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:apply-templates select="list.item" mode="listDelivery" />
			</xsl:when>
			<xsl:otherwise>
				<ul class="&bulletListClass;">
					<xsl:apply-templates select="list.item" mode="listDisplay" />
				</ul>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="list.item" mode="listDelivery">
		<div class="&staticTemplatesAbstractList;">
			<span>&bull;</span>
			<xsl:text>&#160;</xsl:text>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="list.item" mode="listDisplay">
		<li>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="matter.plan.template">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="matter.plan.template/body">
		<div id="&coDocContentBody;">
			<xsl:call-template name="matterAbstract" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="phase.block">
		<div class="&taskBoard;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="phase">
		<xsl:variable name="phase-id">
			<xsl:value-of select="generate-id(.)"/>
		</xsl:variable>
		<div class="&taskBoardColumn;">
			<xsl:apply-templates>
				<xsl:with-param name="phase-id" select="$phase-id"/>
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<xsl:template match="phase/short.title">
		<xsl:param name="phase-id" />
		<div class="&taskBoardHeader;">
			<h2 id="&taskBoardShortTitle;-{$phase-id}" class="&taskBoardShortTitle;">
				<xsl:choose>
					<xsl:when test="$TileViewDelivery">
						<xsl:apply-templates />
					</xsl:when>
					<xsl:when test="$DeliveryMode and ../title/text()">
						<!-- For delivery we only show the long title (if there is one). -->
					</xsl:when>
					<xsl:when test="$DeliveryMode and not(../title/text())">
						<xsl:apply-templates />
					</xsl:when>
					<xsl:otherwise>
						<a href="javascript:void(0);">
							<xsl:apply-templates />
						</a>
					</xsl:otherwise>
				</xsl:choose>
			</h2>
		</div>
	</xsl:template>

	<xsl:template match="phase/title">
		<xsl:choose>
			<xsl:when test="$TileViewDelivery"></xsl:when>
			<xsl:when test="$DeliveryMode">
				<div class="&taskBoardTitle; &borderTopClass;">
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="&taskBoardTitle; &hideStateClass;">
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="task.block">
		<xsl:param name="phase-id" />
		<xsl:choose>
			<xsl:when test="$DeliveryMode and not($TileViewDelivery)">
				<div class="&taskBoardList;">
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<ol aria-labelledby="&taskBoardShortTitle;-{$phase-id}" class="&taskBoardList;">
					<xsl:apply-templates />
				</ol>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="task">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and not($TileViewDelivery)">
				<div class="&taskBoardListItem;">
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<li class="&taskBoardListItem;">
					<xsl:apply-templates />
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="task/short.title">
		<xsl:choose>
			<xsl:when test="$TileViewDelivery">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="$DeliveryMode and ../title/text()">
				<!-- For delivery we only show the long title (if there is one). -->
			</xsl:when>
			<xsl:when test="$DeliveryMode and not(../title/text())">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<a href="javascript:void(0);" class="&taskBoardTaskTitle;">
					<xsl:apply-templates />
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="task/title">
		<xsl:choose>
			<xsl:when test="$TileViewDelivery"></xsl:when>
			<xsl:when test="$DeliveryMode">
				<xsl:variable name="className">
					<xsl:choose>
						<xsl:when test="count(../preceding-sibling::task) > 0">
							<xsl:text>&taskBoardListItemTitle; &paraMainClass; &borderTopClass;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&taskBoardListItemTitle; &paraMainClass;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<div>
					<xsl:attribute name="class">
						<xsl:value-of select="$className"/>
					</xsl:attribute>
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="&taskBoardListItemTitle; &paraMainClass; &hideStateClass;">
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="resource.type.block">
		<xsl:choose>
			<xsl:when test="$TileViewDelivery"></xsl:when>
			<xsl:when test="$DeliveryMode">
				<table class="&taskBoardResources;">
					<tbody>
						<xsl:apply-templates select="resource.type[position() mod 2 = 1]" mode="DeliveryTableRow" />
					</tbody>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<div class="&taskBoardResources; &hideState;">
					<h2>
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&matterTemplatesResourcesHeadingKey;', '&matterTemplateResourcesHeading;')"/>
					</h2>
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="resource.type">
		<div class="&taskBoardResourcesGroup;">
			<xsl:apply-templates />
		</div>
		<xsl:if test="not(position() = last()) and not($DeliveryMode)">
			<div class="&dividerClass;"></div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="resource.type/head/headtext">
		<h3>
			<xsl:apply-templates />
		</h3>
	</xsl:template>

	<xsl:template match="resource">
		<div class="&taskBoardListItemResource;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template name="displayDocumentExtras">
		<span class="&coDocContentExtras;">
			<xsl:if test="/Document/n-metadata//plcmd.is.maintained = 'true'">
				<xsl:call-template name="displayMaintainedText" />

				<!-- Only display the bullet separator if there is both a status and a jurisdiction value. -->
				<xsl:variable name="jurisdiction" select="/Document/n-metadata/plc.metadata.block/plcmd.jurisdictions/plcmd.jurisdiction/plcmd.name" />
				<xsl:if test="not(normalize-space($jurisdiction))=''">
					<span class="&bulletClass;">
						<xsl:text> &bull; </xsl:text>
					</span>
				</xsl:if>
			</xsl:if>

			<xsl:call-template name="displayJurisdiction" />
		</span>
	</xsl:template>

	<xsl:template name="displayJurisdiction">
		<xsl:if test="/Document/n-metadata/plc.metadata.block/plcmd.jurisdictions/plcmd.jurisdiction/plcmd.name">
			<span class="&jurisdictionDisplayWrapper;">
				<xsl:apply-templates select="/Document/n-metadata/plc.metadata.block/plcmd.jurisdictions" />
			</span>
		</xsl:if>
	</xsl:template>

	<xsl:template name="displayMaintainedText">
		<span class="&maintainedStatus;">&MaintainedText;</span>
	</xsl:template>

	<xsl:template match="plcmd.jurisdiction">
		<xsl:if test="not(position() = '1')">
			<xsl:text>,&#160;</xsl:text>
		</xsl:if>
		<xsl:apply-templates select="plcmd.name" />
	</xsl:template>

	<xsl:template match="resource.type" mode="DeliveryTableRow">
		<tr>
			<td class="&taskBoardResourcesGroup;">
				<xsl:apply-templates />
			</td>
			<td class="&taskBoardResourcesGroup;">
				<xsl:apply-templates select="following-sibling::resource.type[1]/*" />
			</td>
		</tr>
	</xsl:template>

	<!-- This was copied from PLCCore/PLCCoreDocument/Xslt/Foundation/ContentBlocks/KnowHow.xsl -->
	<!-- Note if the suppression rules get to complex, move to an extension object -->
	<xsl:template match="web.address">
		<xsl:choose>
			<!-- Suppress .practicallaw.com -->
			<xsl:when test="contains(@href, '.practicallaw.com')">
				<xsl:apply-templates />
			</xsl:when>

			<!-- Do not show links that do not start with http:// (they are most likely relative to plc and not domain level) -->
			<xsl:when test="not(starts-with(@href, 'http://')) and not(starts-with(@href, 'https://'))">
				<xsl:apply-templates />
			</xsl:when>

			<!-- Block all external links on the iPad -->
			<xsl:when test="$IsIpad = 'true'">
				<xsl:apply-templates />
			</xsl:when>

			<!-- Default is rendering the link. -->
			<xsl:otherwise>
				<xsl:call-template name="renderWebAddressLinkOnClick" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="renderWebAddressLinkOnClick">
		<a target="_new" class="&pauseSessionOnClickClass;">
			<xsl:attribute name="href">
				<xsl:value-of select="@href"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>

	<xsl:template name="EndOfDocument" priority="1">
		<div id="&endOfDocumentId;">
			<xsl:if test="not($DeliveryMode)">
				<xsl:variable name="DocumentText">
					<xsl:call-template name="wrapWithSpan">
						<xsl:with-param name="contents" select="'&endOfDocumentText;'" />
					</xsl:call-template>
				</xsl:variable>

				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&coEndDocHeader;'" />
					<xsl:with-param name="contents" select="$DocumentText" />
				</xsl:call-template>

				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="&endOfDocumentCopyrightClass;"/>
					<xsl:with-param name="contents" select="concat('&copy;', '&nbsp;', $currentYear, '&nbsp;', $endOfDocumentCopyrightText)"/>
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:template>

</xsl:stylesheet>
