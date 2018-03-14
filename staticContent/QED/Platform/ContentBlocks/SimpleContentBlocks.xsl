<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Action.Block -->
	<xsl:template match="action.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<!-- Action.Line -->
	<xsl:template match="action.line">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="annotations">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="appendix">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" >
			<xsl:with-param name="id">
				<xsl:if test="@id or @ID">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@id | @ID)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>
	<xsl:template match="attorney.block/attorney.line | attorney.body/attorney.line | attorney.block/attorney.address | attorney.block/attorney.name | attorney.block/attorney.title | attorney.block/attorney.misc | attorney.block/memo.to.line | attorney.block/memo.to.info | attorney.block/memo.from.line | attorney.block/memo.from.info | attorney.block/memo.copies.line | attorney.block/memo.copies.info">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="author.line | author.block" name="author">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>
	<xsl:template match="author.line/author.role | author.line/author.description | author.block/author.name | author.block/author.title | author.block/author.byline | author.block/author.address">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>
	<xsl:template match="author.line/author">
		<xsl:call-template name="wrapWithSpan" />
		<xsl:if test="following-sibling::node()[1][self::author]">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="authority">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&subSectionClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="reference.block[.//authority.reference]">
		<xsl:choose>
			<xsl:when test=".//authority.reference[.//charfill]">
				<xsl:call-template name="TocTable">
					<xsl:with-param name="insertTocTableClass" select="false()"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="Toc"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="authority.reference">
		<xsl:choose>
			<xsl:when test="ancestor::reference.block//authority.reference[.//charfill]">
				<xsl:call-template name="TocTableEntry"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="TocEntry"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="body">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<xsl:template match="case.reference.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="chemical.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="chemical.list">
		<xsl:call-template name="wrapWithUl" />
	</xsl:template>

	<xsl:template match="chemical.list/chemical">
		<xsl:call-template name="wrapWithLi" />
	</xsl:template>

	<xsl:template match="codes.body">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="codes.para" priority="1">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<!-- Court.Block -->
	<xsl:template match="court.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<!-- Court.Line -->
	<xsl:template match="court.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="include.currency.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;',currency.id/@ID)"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="admin.code.reference">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="reference.text | sub.reference">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="description.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="id">
				<xsl:if test="@ID">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="description.block//label">
		<xsl:call-template name="wrapWithSpan" />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="active.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="dea.class.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="drug.device">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="exceptional.drug.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="exceptional.drug.block/exceptional.drug.def">
		<xsl:call-template name="wrapWithSpan" />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="gcc.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="gcc.block/gcc.text">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="generic.name.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="gfc.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="gfc.block/gfc.text">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="maint.drug.status.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="ndc.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="ndc.code.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="ndc.code.orig.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="ndc.code.std.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="orange.book.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="orange.book.code.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="orange.book.code.std">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="prod.specs.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="add.description.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="dosage.form.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="metric.size.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="ncpdp.std.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="ncp.label.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="pkg.count.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="pkg.desc.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="pkg.size.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="perscription.status.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="prod.comp.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="route.admin.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="storage">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="strength.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="single.source">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="tcc.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="tcc.drug.class.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="tcc.primary.agent.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="docket.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>
	<xsl:template match="docket.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="document.head.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<xsl:template match="editorial.note.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>
	<xsl:template match="editorial.note.block//unreported.case.note">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="expert.report.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>
	<xsl:template match="expert.report.block//expert.line | expert.report.block//expertise.line/expertise.complete | expert.report.block//case.type.line/case.type.complete | expert.report.block//case.location.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>
	<xsl:template match="expert.report.block/represented.party.line">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<xsl:template match="front.matter">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<xsl:template match="glossary">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="glossary.entry">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="glossary.division">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="glossary.division/label.designator">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="term">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<xsl:template match="definition/definition.para">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<xsl:template match="intro.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="nav.links">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<xsl:template match="opinion.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="panel.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>
	<xsl:template match="panel.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="prelim.block | prelim">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="prelim.head">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="prelim/summary">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="propagated.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="prop.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="reference.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="reference">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="remarks.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="remarks.block/remarks.body/text.line">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="research.references" name="researchReferences">
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id">
				<xsl:if test="@ID">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="reference.block | reference">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="research.references/reference.block/topic.key.ref">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="section">
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="id">
				<xsl:if test="@ID|@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="section.front">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="sidebar.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="sidebar.title">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="sidebar.body">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- Source -->
	<xsl:template match="source">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="stat">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="id" select="@ID" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="subsection">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="synopsis">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="synopsis.background">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="synopsis.holding">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
	</xsl:template>

	<xsl:template match="table.of.cases.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="table.of.cases.body">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="case.reference">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- Address -->
	<xsl:template match="address.block">
		<xsl:if test="count(node()[not(self::address.unknown)]) &gt; 0">
			<xsl:call-template name="wrapContentBlockWithGenericClass" />
		</xsl:if>
	</xsl:template>
	<xsl:template match="address.block/addressee.name | address.block/addressee.title | address.block/addressee.address | address.block/addressee.telephone">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Arbitrator -->
	<xsl:template match="arbitrator.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="arbitrator.block/arbitrator | arbitrator.block/arbitrator.name | arbitrator.block/arbitrator.title">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Approval.date -->
	<xsl:template match="approval.date">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- Caption -->
	<xsl:template match="caption.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	<xsl:template match="caption.block/subject">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<!-- Case Topic -->
	<xsl:template match="case.topic.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- Citation -->
	<xsl:template match="citation.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="citation.block/citation.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Publication -->
	<xsl:template match="publication.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="publication.block/publication.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>
	<xsl:template match="publication.block/publication.type.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>
	<xsl:template match="publication.type.line/publication.type">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::node()[1][self::publication.type]">
			<xsl:text>, </xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Company Name -->
	<xsl:template match="company.name">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- Complaint -->
	<xsl:template match="complaint.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- Copyright Line -->
	<xsl:template match="copyright.line">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- End Matter -->
	<xsl:template match="end.matter/end.matter.body">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- Expert Witness -->
	<xsl:template match="expert.witnesses.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="expert.witnesses.block/expert.witnesses.body | expert.witnesses.block/expert.witnesses.body/expert.witness">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Facts -->
	<xsl:template match="facts.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- Geographic -->
	<xsl:template match="geographic.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="geographic.block/geo.address | geographic.block/geo.city | geographic.block/geo.state | geographic.block/geo.zip | geographic.block/geo.telephone">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Injury -->
	<xsl:template match="injury.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- Injury Entities -->
	<xsl:template match="injury.entities.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- Journal -->
	<xsl:template match="journal.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="journal.block/journal.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- MainText -->
	<xsl:template match="main.text/main.text.body">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- Manufacturer Narrative -->
	<xsl:template match="manufacturer.narrative">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- Provider -->
	<xsl:template match="provider.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="provider.block/provider.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Range -->
	<xsl:template match="range.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="range.block/range.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Recall -->
	<xsl:template match="recall.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- Related Documents -->
	<xsl:template match="related.documents.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- School Info Service -->
	<xsl:template match="school.info.service.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="school.info.service.block/school.info.service.body">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- School Profile -->
	<xsl:template match="school.profile.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="school.profile.block/school.profile.body">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- School Statistical Summary -->
	<xsl:template match="school.statistical.summary.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="school.statistical.summary.block/school.stat.enrollment | school.statistical.summary.block/school.stat.grade.span | school.statistical.summary.block/school.stat.schools  | school.statistical.summary.block/school.stat.teachers">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!--Suggestions-->
	<xsl:template match="email.address">
		<a class="&pauseSessionOnClickClass;" target="_blank">
			<xsl:attribute name="href">
				mailto:<xsl:value-of select="@href"/>
				?subject=<xsl:value-of select="@subject"/>
			</xsl:attribute>
			<xsl:value-of select="."/>
		</a>
	</xsl:template>

	<!-- TrialDoc.Reference -->
	<xsl:template match="trialdoc.reference.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="trialdoc.reference.block/trialdoc.reference">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Verdict Summary -->
	<xsl:template match="verdict.summary.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- narrative -->
	<xsl:template match="narrative">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- concurring -->
	<xsl:template match="concurring.block | concurring.dissenting.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- declaration -->
	<xsl:template match="declaration.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- dissenting -->
	<xsl:template match="dissenting.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- editor.note.block -->
	<xsl:template match="editor.note.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- prelim -->
	<xsl:template match="prelim.synopsis">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- related.doc -->
	<xsl:template match="related.doc">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- registrar -->
	<xsl:template match="registrar.name">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- salutation -->
	<xsl:template match="salutation">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- separate -->
	<xsl:template match="separate.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- speaker -->
	<xsl:template match="speaker.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- syllabus -->
	<xsl:template match="syllabus">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<!-- title.address -->
	<xsl:template match="title.address">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- venue -->
	<xsl:template match="venue">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- opinion start -->
	<xsl:template match="opinion.block.body | opinion.body | opinion.cipdip">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="opinion.lead |lead.author.line.block | lead.author.line">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="opinion.concurrance | concurrance | concurrance.author.line.block | concurrance.author.line">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="opinion.dissent | dissent.author.line.block | dissent.author.line">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="opinion.undesignated">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="vote.block | vote.line">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<!-- opinion end-->

	<!-- bna.headnote start -->
	<xsl:template match="bna.headnote | bna.headnote//headnote.source | bna.headnote//bna.topic.line | bna.headnote//bna.key.line | bna.headnote//cite.line">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	<!-- bna.headnote end -->

	<!-- uspq.headnote start -->
	<xsl:template match="uspq.headnote | uspq.classification | uspq.key.line | uspq.topic.line | uspq.headnote.body">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	<!-- uspq.headnote end -->

	<!-- oral argument related simple content blocks start -->
	<xsl:template match="petitioner.argument.block | respondent.argument.block |	rebuttal.argument.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	<!-- oral argument related simple content blocks end -->

	<!-- finding.aid -->
	<xsl:template match="finding.aid">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- notes.block -->
	<xsl:template match="notes.block">
		<xsl:if test="not($EasyEditMode)">
			<xsl:call-template name="wrapContentBlockWithGenericClass"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="references">
		<xsl:if test="not($EasyEditMode)">
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<!-- pub.type -->
	<xsl:template match="pub.type">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- pub.topic -->
	<xsl:template match="pub.topic">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- issn -->
	<xsl:template match="issn">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- pub.name -->
	<xsl:template match="pub.name">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="provider">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="device.type">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- Publisher -->
	<xsl:template match="publisher.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="publisher.block/publisher.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- Source -->
	<xsl:template match="source.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>
	<xsl:template match="source.block/source.line | source.block/source.body/source">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<!-- doi -->
	<xsl:template match="doi">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- key.words.block -->
	<xsl:template match="key.words.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- abstract -->
	<xsl:template match="abstract">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="id">
				<xsl:if test="@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@id)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- appendix.block -->
	<xsl:template match="appendix.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="id">
				<xsl:if test="@id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@id)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- figure -->
	<xsl:template match="figure">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="id">
				<xsl:if test="@id or @ID">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@id | @ID)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="figure.body | figure.caption">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>


	<!-- question.answer.block -->
	<xsl:template match="question.answer.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- question -->
	<xsl:template match="question">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- answer -->
	<xsl:template match="answer">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- signature.block -->
	<xsl:template match="signature.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- signature.block containing leader node -->
	<xsl:template match="signature.block[.//leader]">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="contents">
				<div class="&leaderSignatureClass;">
					<xsl:apply-templates />
				</div>
				<div class="&clearClass;"></div>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- signature.block/location -->
	<xsl:template match="signature.block/location">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- signature.block/location -->
	<xsl:template match="signature.block/name">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- stanza -->
	<xsl:template match="stanza">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- stanza.line -->
	<xsl:template match="stanza.line">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- accession.no.block -->
	<xsl:template match="accession.no.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!-- chapter.name or subchapter.name -->
	<xsl:template match="chapter.name | subchapter.name">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- front.matter/pub.source | front.matter/division.name | front.matter/editor | front.matter/author -->
	<xsl:template match="front.matter/pub.source | front.matter/division.name | front.matter/editor | front.matter/author">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!-- table.title -->
	<xsl:template match="table.title">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="law.section">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="update.target">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="resolution">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="law.section">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="summary.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="summary.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="title.of.act">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="title.of.act.continued">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="grade.head">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="grade.head.cont">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="delegates">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="beit">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="west.id">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="topical.head">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="law.info">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="bill.info">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="intro.para">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="intro.para[leader]">
		<xsl:call-template name="leaderContent">
			<xsl:with-param name="parent" select="." />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mesh.text">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="background.block">
		<xsl:call-template name="wrapContentBlockWithGenericClassAndAnchoredId" />
	</xsl:template>

	<xsl:template match="history.physical.block">
		<xsl:call-template name="wrapContentBlockWithGenericClassAndAnchoredId" />
	</xsl:template>

	<xsl:template match="background.block | history.physical.block | diagnostic.testing.block | diagnosis.block | assessment.block | treatment.block | 
								       prognosis.block | disposition.block | related.info.block | supplemental.info.block | cautions.block | warning.block |
								       clinical.applications.block | dosing.info.block | overview.block | pharmacokinetics.block | class.block | tradenames.block |
								       adverse.reactions.block | clinical.studies.block | overdosage.block | supply.block | company.block | precautions.block |
								       history.block | manufacturer.block | references.block">
		<xsl:call-template name="wrapContentBlockWithGenericClassAndAnchoredId" />
	</xsl:template>

	<xsl:template match="til">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="rc.gen">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="sol">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="hcb">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="hcb1">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="hcb2">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="hcb3">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="hcb4">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="related.cite">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="whats.new">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="brand">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="juris">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="agency">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="division">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="ver.number">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="order.date.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="order.title | order.number | order.date | product.name">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="document.category">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="document.type">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="rules.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="timing.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="filing.req.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="general.req.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="documents.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="format.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="filing.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="hearings.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="forms.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="checklist.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="unit">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>

	<!-- court.authority -->
	<xsl:template match="court.authority">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="court.authority/jurisdiction | court.authority/regarding">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="address.line">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<!--ama image block-->
	<xsl:template match="ama.image.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="definition.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="related.term.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="language.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	<xsl:template match="language.block/language.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="country.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	<xsl:template match="country.block/country.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="journal.issue/primary.pubdate | journal.issue/volume | journal.issue/issue | journal.issue/pages | journal.issue/secondary.pubdate">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="abstract.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	<xsl:template match="abstract.block/abstract">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="affiliation.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	<xsl:template match="affiliation.block/affiliation.line">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="comments.corrections.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="phone.num.block">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>
	<xsl:template match="phone.num.block/label">
		<xsl:call-template name="wrapWithSpan" />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>
	<xsl:template match="phone.num.block/phone.num">
		<xsl:call-template name="wrapWithSpan" />
	</xsl:template>

	<xsl:template match="update.stack">
		<xsl:call-template name="wrapContentBlockWithGenericClassAndAnchoredId" />
	</xsl:template>

	<xsl:template match="article">
		<xsl:call-template name="wrapContentBlockWithGenericClassAndAnchoredId" />
	</xsl:template>

	<xsl:template match="blueline">
		<xsl:call-template name="wrapContentBlockWithGenericClassAndAnchoredId" />
	</xsl:template>

	<xsl:template match="stat.block">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" >
			<xsl:with-param name="id">
				<xsl:if test="@id or @ID">
					<xsl:value-of select="concat('&internalLinkIdPrefix;',@id | @ID)"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Copy With Reference - Paragraph Pinpointing -->
	<xsl:template match="pdc.number">
		<xsl:variable name="paraNumberText" select="./text()" />
		<xsl:if test="string-length($paraNumberText) &gt; 0">
			<input type="hidden" class="&paraNumberTextMetadataItemClass;" value="{$paraNumberText}" alt="&metadataAltText;"/>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

</xsl:stylesheet>
