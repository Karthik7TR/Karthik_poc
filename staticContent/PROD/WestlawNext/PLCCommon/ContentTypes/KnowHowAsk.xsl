<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="KnowHow.xsl"/>
	<!-- Currently this stylesheet is not overriding anything from what Product stylesheet does currently. -->

	<xsl:template match="prelim">
		<small class="&askDisclaimerClass;">
			<xsl:call-template name="Disclaimer"/>
		</small>
		<hr class="&askDocRule;"/>
		<div id="&coDocContentHeaderPrelim;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
			<xsl:if test="not($DeliveryMode)">
				<div id="&codocumentHeaderBorder;" class="&documentHeaderBorderClass;"></div>
				<!-- Action Space -->
				<div class="&coDocumentActions; &coFloatRight;">
					<xsl:if test="$IAC-FAVORITE-DOCUMENT">
						<a href="&jsVoidText;" title="&addBookmarkText;">
							<span class="&addBookmarkIconClass; &addBookmarkIconAddBookmarkClass;">&addBookmarkText;</span>
						</a>
					</xsl:if>
				</div>
			</xsl:if>


			<xsl:if test="not($IsQuickDraftDelivery) and $IsWmCsvDelivery = 'false'">
				<!--Document Title-->
				<div id="&coDocHeaderContainer;">
					<xsl:if test="title" >
						<h1 class="&titleClass;">
							<xsl:value-of select="title"/>
						</h1>
					</xsl:if>
					<!-- Document Author -->
					<xsl:if test="author and author != ''">
						<div class="&coProductName; &askReplyFollowing;">
							<xsl:apply-templates select="author"/>
						</div>
					</xsl:if>
					<xsl:if test="not($PreviewMode)">
						<xsl:call-template name="AddCommentLink">
							<xsl:with-param name="classes" select="'&askReplyToThisPostInTitle;'"/>
						</xsl:call-template>
					</xsl:if>
					<div class="&coRelatedContentStickyLink; &coRelatedContentQuickLink;">
						<xsl:element name="a">
							<xsl:attribute name="href">
								<xsl:text>#kh_relatedContentOffset</xsl:text>
							</xsl:attribute>
							<xsl:text>&relatedContentStickylink;</xsl:text>
						</xsl:element>
					</div>
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="abstract">
		<xsl:if test="not($IsQuickDraftDelivery)">
			<hr class="&askDocRule;"/>
			<div class="&simpleContentBlockClass; &khAbstract; &askQuestion;">
				<div class="&qAskIcon;">Q:</div>
				<xsl:apply-templates />
				<xsl:if test="/Document/n-metadata/plc.metadata.block/plcmd.partof">
					<div class="&khRelatedDocument;">
						<xsl:text>&documentPartOf;</xsl:text>
						<xsl:variable name="hrefto">
							<xsl:call-template name="createPlcDocumentLink">
								<xsl:with-param name="plcref" select="/Document/n-metadata/plc.metadata.block/plcmd.partof/plcmd.resource.type/plcmd.plc.reference"></xsl:with-param>
							</xsl:call-template>
						</xsl:variable>
						<a href="{$hrefto}">
							<xsl:value-of select="/Document/n-metadata/plc.metadata.block/plcmd.partof/plcmd.title"/>
						</a>
					</div>
				</xsl:if>
			</div>
			<hr class="&askDocRule;"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="body/comment">
		<!-- Don't show any document stored comments...-->
		<xsl:text></xsl:text>
	</xsl:template>


	<xsl:template match="body/discussion[1]/comments/comment">
		<xsl:if test="not($PreviewMode)">
			<div>
				<xsl:choose>
					<xsl:when test="position() = 1">
						<xsl:attribute name="class">
							<xsl:text>&askResponse; &askCommentResponse;</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">
							<xsl:text>&askCommentResponse;</xsl:text>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:attribute name="data-askcomment">
					<xsl:text>{"commentId":</xsl:text>
					<xsl:value-of select="@commentId"/>
					<xsl:text>}</xsl:text>
				</xsl:attribute>


				<xsl:if test="position() = 1">
					<div class="&aAskIcon;">A:</div>
				</xsl:if>

				<xsl:choose>
					<xsl:when test="not(@displayName = '')">
						<xsl:choose>
							<xsl:when test="(@showAuthor = 'false') and not(@prismGuid = '')">
								<div class="&askCommentDisplayName;">
									<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askAnonymous;', '&askAnonymousDefault;')"/>
								</div>
							</xsl:when>
							<xsl:otherwise>
								<div class="&askCommentDisplayName;">
									<xsl:value-of select="@displayName" />
								</div>
								<div class="&askCommentDisplayService;">
									<xsl:value-of select="@displayService" />
								</div>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<div class="&askCommentDisplayName;">
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askAnonymous;', '&askAnonymousDefault;')"/>
						</div>
					</xsl:otherwise>
				</xsl:choose>

				<div class="&askCommentPostedDate;">
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askPostedOn;', '&askPostedOnDefault;')"/>
					<xsl:value-of select="DocumentExtension:ReformatDate(@createdDate,'dd MMM yyyy', 'en-GB')"/>
					<span class="&askDateTimeSeparator;">
						<xsl:choose>
							<xsl:when test="$DeliveryMode and $IsUKDocument">
								<xsl:text>&nbsp;&bull;&nbsp;</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>&bull;</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</span>
					<xsl:value-of select="DocumentExtension:ReformatDate(@createdDate,'HH:mm', 'en-GB')"/>
				</div>
				<div class="&askCommentBody;">
					<xsl:value-of select="DocumentExtension:WrapCommentIntoParagraphs(., boolean($DeliveryMode))" disable-output-escaping="yes"/>
				</div>
				<xsl:element name="a">
					<xsl:attribute name="name">
						<xsl:value-of select="@commentId"/>
					</xsl:attribute>
				</xsl:element>
				<div class="&askReportThisPost;">
					<xsl:element name="a">
						<xsl:attribute name="href">
							<xsl:text>mailto:</xsl:text>
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askAbuseEmailAddress;', '&askAbuseEmailAddressDefault;')"/>
							<xsl:text>?subject=</xsl:text>
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askAbuseEmailSubject;', '&askAbuseEmailSubjectDefault;')"/>
							<xsl:text >&amp;body=</xsl:text>
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askAbuseEmailBody;', '&askAbuseEmailBodyDefault;')"/>
							<xsl:variable name="documentGuid">
								<xsl:value-of select="//md.uuid"/>
							</xsl:variable>
							<xsl:value-of select="DocumentExtension:ConstructCurrentWebsitePageUri($documentGuid)"/>
							<xsl:text>#</xsl:text>
							<xsl:value-of select="@commentId"/>
							<xsl:text disable-output-escaping="yes">%0D%0A</xsl:text>
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askPostedOn;', '&askPostedOnDefault;')"/>
							<xsl:value-of select="@createdDate" />
							<xsl:text disable-output-escaping="yes">%0D%0A</xsl:text>
						</xsl:attribute>
						<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askReportThisPostLabel;', '&askReportThisPostLabelDefault;')"/>
					</xsl:element>
				</div>
				<xsl:if test="not($DeliveryMode)">
					<xsl:call-template name="AddCommentLink">
					</xsl:call-template>
					<xsl:if test="DocumentExtension:HasFacGranted('ASK-EDITOR')" >
						<div class="&askEditComment;">
							<xsl:element name="a">
								<xsl:attribute name="href">
									<xsl:text>#replyForm</xsl:text>
								</xsl:attribute>
								<xsl:attribute name="data-askcomment-action">
									<xsl:text>edit</xsl:text>
								</xsl:attribute>
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askEdit;', '&askEditDefault;')"/>
							</xsl:element>
						</div>

						<div class="&askDeleteComment;">
							<xsl:element name="a">
								<xsl:attribute name="href">
									<xsl:text>/AskCommentForm/DeleteComment?commentId=</xsl:text>
									<xsl:value-of select="@commentId"/>
								</xsl:attribute>
								<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askDelete;', '&askDeleteDefault;')"/>
							</xsl:element>
						</div>
					</xsl:if>
				</xsl:if>
			</div>
			<hr class="&askDocRule;"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="body/discussion[position() > 1]/comments/comment">
		<!-- ignore any repeated comments sections as a result of caching -->
	</xsl:template>

	<xsl:template name="AddCommentLink">
		<xsl:param name="classes"/>
		<xsl:if test="not($DeliveryMode)">
			<div class="&askReplyToThisPost; {$classes}">
				<xsl:element name="a">
					<xsl:attribute name="href">
						<xsl:text>#replyForm</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="data-askcomment-action">
						<xsl:text>add</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askAddReply;', '&askAddReplyDefault;')"/>
				</xsl:element>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="Disclaimer">
		<xsl:variable name="disclaimer">
			<xsl:value-of disable-output-escaping="yes" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askLegalDisclaimer;', '&askLegalDisclaimerDefault;')"/>
		</xsl:variable>
		<xsl:variable name="scopeRulesDocument">
			<xsl:value-of disable-output-escaping="yes" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', 'AskScopeRulesDocument', 'PlcRef')"/>
		</xsl:variable>

		<xsl:variable name="fullScopeRulesUri">
			<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PlcDocument', concat('plcref=', $scopeRulesDocument), concat('transitionType=', '&transitionTypeDocument;'), $specialVersionParamVariable, $specialRequestSourceParamVariable, 'contextData=(sc.Default)')"/>
		</xsl:variable>

		<xsl:call-template name="replace">
			<xsl:with-param name="string" select="$disclaimer" />
			<xsl:with-param name="pattern" select="'{0}'" />
			<xsl:with-param name="replacement" select="$fullScopeRulesUri" />
			<xsl:with-param name="disable-output-escaping" select="'yes'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="AfterDocument">
		<xsl:if test="not($PreviewMode)">
			<div class="&askAddReplyForm;">
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="AfterEndOfDocumentWidgets">
		<xsl:if test="count(//md.topics/md.topic) > 0">
			<div class="&askRelatedTopicsContainer;">
				<h3>
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askTopicHeader;', '&askTopicHeaderDefault;')"/>
				</h3>
				<ul>
					<xsl:for-each select="//md.topics/md.topic">
						<li>
							<xsl:element name="a">
								<xsl:attribute name="href">
									<xsl:value-of select="DocumentExtension:RetrieveWebsiteHostUrl()" />
									<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askTopicUrl;', '&askTopicUrlDefault;')"/>
									<xsl:value-of select="md.plc.reference"/>
								</xsl:attribute>
								<xsl:value-of select="md.topic.name"/>
							</xsl:element>
						</li>
					</xsl:for-each>
				</ul>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="knowhowMetaInfoColumn">
		<div id="&coDocContentMetaInfo;">
			<xsl:call-template name="resourceHistoryQuickLink"/>
			<xsl:call-template name="eventType"/>
			<xsl:call-template name="resourceType"/>
			<xsl:call-template name="knowhowStatus"/>
			<xsl:call-template name="knowhowJurisdictions"/>
			<xsl:call-template name="relatedContentQuickLink"/>
		</div>

	</xsl:template>

	<xsl:template name="knowhowStatus">
		<xsl:if test="/Document/n-docbody//prelim/currency/currency.date">
			<xsl:variable name="date">
				<xsl:value-of select="/Document/n-docbody//prelim/currency/currency.date" />
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$IsUKDocument and $DeliveryMode">
					<xsl:call-template name="askStatusUKDelivery">
						<xsl:with-param name="date" select="DocumentExtension:ReformatDate($date,'dd MMMM yyyy', 'en-GB')"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<div class="&askDocPublishDate;">
						<b>
							<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&askDatePublished;', '&askDatePublishedDefault;')"/>
						</b>
						<div>
							<xsl:value-of select="DocumentExtension:ReformatDate($date,'dd MMMM yyyy', 'en-GB')"/>
						</div>
					</div>
				</xsl:otherwise>
			</xsl:choose>

			<!-- If currency and no juris, don't add separator -->
			<xsl:if test="not(normalize-space(juris)='')">
				<xsl:call-template name="HeaderSeparator" />
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="askStatusUKDelivery">
		<xsl:param name="date"/>
		<xsl:variable name="currencyText">
			<xsl:choose>
				<xsl:when test="/Document/n-docbody//prelim/currency/currency.status">
					<xsl:value-of select="/Document/n-docbody//prelim/currency/currency.status" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="substring(/Document/n-docbody//prelim/currency, 1, string-length(/Document/n-docbody/practice.note/prelim/currency)-11)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="concat($currencyText, ' ', $date)"/>
	</xsl:template>

	<xsl:template name="EndOfDocument">
		<xsl:param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText"/>
		<xsl:if test="not($DeliveryMode)">
			<xsl:call-template name="AfterDocument"/>
			<xsl:call-template name="EndOfDocumentContent" />
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>