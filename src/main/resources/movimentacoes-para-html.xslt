<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:alura="http://alura.com.br">

    <xsl:template match="/movimentacoes">
		<html xmlns:alura="http://alura.com.br">
			<Body>
				<xsl:for-each select="movimentacao">
					<movimentacao>
               			<valor><xsl:value-of select="valor"/></valor>
               			<data><xsl:value-of select="data"/></data>
               			<tipo><xsl:value-of select="tipo"/></tipo>
               		</movimentacao>
                 </xsl:for-each>
            </Body>
        </html>
    </xsl:template>
</xsl:stylesheet>