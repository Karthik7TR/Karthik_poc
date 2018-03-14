<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Document Data (artificially added by us) -->
	<xsl:template match="document-data" />

	<!-- Cites start -->
	<xsl:template match="md.pubid"/>
	<xsl:template match="md.cite.caveat"/>
	<xsl:template match="md.jurisdictions"/>
	<xsl:template match="md.subjects"/>
	<xsl:template match="md.media.block"/>
	<xsl:template match="md.exhibit.flag"/>
	<xsl:template match="pcite"/>
	<!-- Cites end -->

	<!-- Court start -->
	<xsl:template match="court.error"/>
	<!-- Court end -->

	<!-- Action start -->
	<xsl:template match="action.line.hidden"/>
	<!-- Action end -->

	<!-- Author start -->
	<xsl:template match="author.line.hidden"/>
	<!-- Author end -->

	<!-- Attorney start -->
	<xsl:template match="attorney.line.hidden"/>
	<!-- Attorney end -->

	<!-- Cornerpiece start -->
	<xsl:template match="cornerpiece"/>
	<!-- Cornerpiece end -->

	<!-- Doc Title start -->
	<xsl:template match="doc.title.hidden"/>
	<!-- Doc Title end -->

	<!-- Docket start -->
	<xsl:template match="docket.line.hidden"/>
	<xsl:template match="docket.line[@hidden='Y']" />
	<xsl:template match="docket.block[@hidden='Y']" />
	<!-- Docket end -->

	<!-- Date start -->
	<xsl:template match="date.hidden"/>
	<xsl:template match="date.line.hidden" />
	<xsl:template match="date.block[@hidden='Y']" />
	<xsl:template match="date.line[@hidden='Y']" />
	<xsl:template match="pub.date[@hidden='Y']" />
	<xsl:template match="year.hidden" />
	<xsl:template match="iso.date" />
	<!-- Date end -->

	<!-- Expert.Report start -->
	<xsl:template match="expertise.line/expertise.general"/>
	<xsl:template match="expertise.line/expertise.specific"/>
	<xsl:template match="case.type.line/case.type.general"/>
	<xsl:template match="case.type.line/case.type.specific"/>
	<!-- Expert.Report end -->

	<!-- Headnotes start -->
	<xsl:template match="headnote.error"/>
	<xsl:template match="topic.key.error"/>
	<xsl:template match="archive.headnote.block"/>
	<xsl:template match="expanded.classification.new"/>
	<xsl:template match="headnote.body.new"/>
	<!-- Headnotes end -->

	<!-- Opinion start -->
	<xsl:template match="opinion.error"/>
	<!-- Opinion end -->

	<!-- Title start -->
	<xsl:template match="party.line.hidden"/>
	<xsl:template match="title.block[@hidden='Y']" />
	<xsl:template match="primary.title[@hidden='Y']" />
	<xsl:template match="versus[@hidden='Y']" />
	<xsl:template match="and[@hidden='Y']" />
	<!-- Title end -->

	<!-- Reference start -->
	<xsl:template match="archive.brief.reference.block"/>
	<xsl:template match="westlaw.document.reference" />
	<xsl:template match="oral.argument.reference" />
	<!-- Reference end -->

	<!-- Withdrawn start -->
	<xsl:template match="withdrawn.block"/>
	<!-- Withdrawn end -->

	<!-- Vote start -->
	<xsl:template match="vote.line.hidden"/>
	<!-- Vote end -->

	<!-- Metadata start -->
	<xsl:template match="n-metadata/node()[not(self::metadata.block)]"/>
	<xsl:template match="metadata.block/node()[not(self::md.identifiers) and not(self::md.references)]"/>
	<xsl:template match="md.westlawids"/>
	<xsl:template match="md.uuid"/>
	<xsl:template match="md.legacy.id"/>
	<xsl:template match="md.bill.id"/>
	<xsl:template match="md.wip.version.num"/>
	<xsl:template match="md.legacy.heading.id"/>
	<xsl:template match="md.hyphenated.uuid"/>
	<xsl:template match="md.wl.profiler.identifiers"/>
	<xsl:template match="n-view"/>
	<xsl:template match="prism-clipdate"/>
	<xsl:template match="md.legacy.references"/>
	<xsl:template match="md.related.docs"/>
	<!-- Metadata end -->

	<!-- WestlawDescription start -->
	<xsl:template match="md.wl.memtab"/>
	<xsl:template match="md.wl.courtyear"/>
	<xsl:template match="md.document.statuses"/>
	<xsl:template match="md.starpages"/>
	<xsl:template match="md.related.link.code"/>
	<!-- WestlawDescription end -->

	<!-- Content Metadata start -->
	<xsl:template match="cmd.contentstitle"/>
	<xsl:template match="cmd.westlawdescrip" />
	<xsl:template match="cmd.publication" />
	<xsl:template match="cmd.dates" />
	<!-- Content Metadata start -->

	<!-- Content Layout start -->
	<xsl:template match="content.layout.block"/>
	<!-- Content Layout end -->

	<!-- Universal Errors start -->
	<xsl:template match="error.block"/>
	<xsl:template match="error.line"/>
	<xsl:template match="paragraph.error"/>
	<xsl:template match="content.error"/>
	<xsl:template match="link.error" />
	<!-- Universal Errors end -->

	<!-- Universal Hiddens start -->
	<xsl:template match="message.line.hidden"/>
	<xsl:template match="head[@hidden='Y']"/>
	<xsl:template match="head.hidden"/>
	<xsl:template match="short.head" />
	<xsl:template match="para[@hidden='Y']"/>
	<xsl:template match="paratext.hidden"/>
	<xsl:template match="note.para[@hidden='Y']"/>
	<!-- Universal Hiddens end -->

	<!-- Other Hiddens start... not sure where they belong! -->
	<xsl:template match="cite.line.hidden"/>
	<xsl:template match="short.synopsis.hidden"/>
	<!-- Others Hiddens end -->

	<!-- Empty Novus Elements start -->
	<xsl:template match="bop"/>
	<xsl:template match="eop"/>
	<xsl:template match="bos"/>
	<xsl:template match="eos"/>
	<xsl:template match="begin.quote"/>
	<xsl:template match="end.quote"/>
	<xsl:template match="lead"/>
	<xsl:template match="vsi"/>
	<!-- Empty Novus Elements end -->

	<!-- Other Elements... -->
	<xsl:template match="starpage"/>
	<xsl:template match="starpage.anchor/pdc.number"/>
	<xsl:template match="conditional[@action = 'suppress' and (not(@product.type) or @product.type = 'westlaw')]"/>
	<xsl:template match="wip.flag"/>
	<xsl:template match="alt.target.id"/>
	<!-- include.copyright - This is not used for NRS caselaw content, as it just has the Thomson Reuters copyright message.
						   It is used for the Officials and other 3rd party caselaw and contains the UUID of the Novus
						   document containing the copyright message that should be displayed. -->
	<xsl:template match="include.copyright"/>
	<xsl:template match="include.copyright.block/message.code"/>
	<xsl:template match="image.keywords"/>

	<!--Suppress message.code in include.currency.block and include.head.block-->
	<xsl:template match="include.currency.block/message.code"/>
	<xsl:template match="include.head.block/message.code"/>

	<!-- Supress include.sb.currency.block (include.currency.block is better) -->
	<xsl:template match="include.sb.currency.block"/>

	<!--Suppressed for AdminDecisions -->
	<xsl:template match="tndx.metadata.block"/>
	<xsl:template match="md.image.list.block"/>
	<xsl:template match="md.outline.block"/>
	<xsl:template match="layout.control.block"/>
	<xsl:template match="address.block/address.unknown"/>
	<xsl:template match="reference.source"/>
	<xsl:template match="original.source.image.id"/>
	<xsl:template match="geographic.block/geo.info"/>
	<xsl:template match="citation.line[@hidden='Y']"/>
	<xsl:template match="source[@hidden='Y']"/>
	<xsl:template match="addressee.title[@hidden='Y']"/>
	<xsl:template match="addressee.address[@hidden='Y']"/>
	<xsl:template match="footnote.end.text"/>

	<!-- ALRDigest related-->
	<xsl:template match="n-tocview" />

	<!-- DigestHeadnotes related -->
	<xsl:template match="md.identifiers/md.headnote.id | md.identifiers/md.sequence | md.identifiers/md.headnote.legacy.id " />
	<xsl:template match="digestheadnote/content.block/date.block" />
	<xsl:template match="headnote.publication.block" />

	<!-- Admin Codes related: Some of these will be displayed by the related info vertical so we will suppress them-->
	<xsl:template match="general.material"/>
	<xsl:template match="nod.link"/>
	<xsl:template match="legacy.centdol"/>
	
	<!-- ALR -->
	<xsl:template match="section.footnote.head" />
	
	<!-- Jury Verdict related -->
	<xsl:template match="trial.type.block" />
	<xsl:template match="range.block/range.code | range.block/party.range.codes" />
	<xsl:template match="case.topic/case.type | case.topic/case.subtype" />
	<xsl:template match="verdict.summary.block/award.amounts/plaintiff.award.amount" />

	<!-- Codes Session Laws -->
	<xsl:template match="md.tracking.no" />
	<xsl:template match="md.wl.update.query" />

	<!-- Suppress footnote references from title metadata -->
	<xsl:template match="/Document/document-data/title//footnote.reference | /Document/document-data/title//table.footnote.reference | /Document/document-data/title//endnote.reference" priority="2" />

	<!-- Suppress starpages from title metadata -->
	<xsl:template match="/Document/document-data/title//starpage.anchor" priority="2" />
	
	<!--Suppress court.line.hidden-->
	<xsl:template match="court.line.hidden"/>

	<!-- Supress the hidden line -->
	<xsl:template match="jurisdiction.line.hidden"/>

	<xsl:template match="md.cmsids"/>

	<xsl:template match="/Document/toc-data" />
	
	<!-- Construed Terms - Markman Orders -->
	<xsl:template match="construed.terms.block" />
	
	<!-- Federal Committee Schedules -->
	<xsl:template match="minority.statements | majority.statements" />
	
	<!-- Profiler -->
	<xsl:template match="city[@hidden='Y']"/>
	<xsl:template match="province[@hidden='Y']"/>
	
	<!-- DLE CD ROM - Remove the following tags from all content types, these are tags
		 added for print and CD ROM to be able to find and place images in the documents.
		 eReader will place images in the online version of the documents, but these tags
		 are still to be suppressed at that time as well.  -->
	<xsl:template match="tbl.image.id" />
	<xsl:template match="graphic.placeholder" />

	<!-- Rulebook specific nort elements -->
	<xsl:template match="section-heading" />
  <xsl:template match="heading-range" />
	<xsl:template match="citation-heading" />

</xsl:stylesheet>