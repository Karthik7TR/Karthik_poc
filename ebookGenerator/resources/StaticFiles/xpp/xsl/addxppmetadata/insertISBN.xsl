<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"

	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

 	<xsl:param name="ISBNNumber" select="ISBNNumber" />
 	
 	<xsl:template name="insertISBNTemplate">
 		<xsl:param name="isbnNumber" />
 		<xsl:element name="ebook-isbn">
			<xsl:value-of select="$isbnNumber" />
		</xsl:element>
 	</xsl:template>
 	
 	<xsl:template name="isbnRemover" match="x:fm.copyright.page"/>

</xsl:stylesheet>