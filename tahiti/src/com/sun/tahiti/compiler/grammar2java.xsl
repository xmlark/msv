<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:output method="text" encoding="us-ascii" />
	
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="grammar">
	
	
	<!--            header            -->
	<!--==============================-->
		<xsl:if test="./package">
			<xsl:text>package </xsl:text>
			<xsl:value-of select="./package"/>
			<xsl:text>;</xsl:text>
			<xsl:call-template name="CRLF"/>
			<xsl:call-template name="CRLF"/>
		</xsl:if>
	
<xsl:text><![CDATA[
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.ValidationContext;
import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.msv.datatype.xsd.ngimpl.DataTypeLibraryImpl;
import com.sun.msv.grammar.*;
import com.sun.msv.util.StringPair;
import com.sun.tahiti.runtime.ll.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

/**
 * generated class.
 */
public class ]]></xsl:text>
		<xsl:value-of select="name"/>
		<xsl:text> {</xsl:text>
		<xsl:call-template name="CRLF"/>
		
		
	<!--      header                  -->
	<!--==============================-->
	<xsl:text><![CDATA[
	
	// empty StringPair which is used as a dummy name for datatypes
	private static final StringPair emptyName = new StringPair("","");
	
]]></xsl:text>
	
	
	
	
	<!--      symbol definitions      -->
	<!--==============================-->
	
		<xsl:text>// symbols</xsl:text>
		<xsl:call-template name="CRLF"/>
		
	<!-- element symbols -->
		<xsl:for-each select="elementSymbol">
			<xsl:text>	private static final LLElementExp </xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text>;</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>
		
	<!-- attribute symbols -->
		<xsl:for-each select="attributeSymbol">
			<xsl:text>	private static final LLAttributeExp </xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text>;</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>
		
	<!-- intermediate symbols -->
		<xsl:for-each select="intermediateSymbol">
			<xsl:text>	private static final IntermediateSymbol </xsl:text>
				<xsl:value-of select="@id"/>
			<xsl:text> = new IntermediateSymbol("</xsl:text>
				<xsl:value-of select="@id"/>
			<xsl:text>");</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>
		
	<!-- named symbols -->
		<xsl:for-each select="namedSymbol">
			<xsl:text>	public static final NamedSymbol </xsl:text>
				<xsl:value-of select="@id"/>
			<xsl:text> = new NamedSymbol("</xsl:text>
				<xsl:value-of select="@id"/>
			<xsl:text>");</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>
	
	<!-- datatype symbols -->
		<xsl:for-each select="dataSymbol">
			<xsl:text>	public static final DatabindableDatatype </xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text>;</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>
	
	<!-- primitive symbols -->
		<xsl:for-each select="primitiveSymbol">
			<xsl:text>	private static final NonTerminalSymbol </xsl:text>
				<xsl:value-of select="@id"/>
			<xsl:text> = new DefaultNonTerminalSymbol("</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text>");</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>
		
		
		
	<!-- ignore symbols -->
		<xsl:for-each select="ignoreSymbol">
			<xsl:text>	private static final NonTerminalSymbol </xsl:text>
				<xsl:value-of select="@id"/>
			<xsl:text> = new IgnoreSymbol("</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text>");</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		
		
		
	<!-- class symbols -->
		<xsl:for-each select="classSymbol">
			<xsl:text>	private static final NonTerminalSymbol </xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text> = new NonTerminalSymbol() {</xsl:text>
<xsl:text><![CDATA[
			public LLParser.Receiver createReceiver( final LLParser.Receiver parent ) {
				return new LLParser.FieldReceiver(){
					private ]]></xsl:text>
			<xsl:value-of select="@type"/>
			<xsl:text><![CDATA[ o;
					public void start() {
						o = new ]]></xsl:text>
			<xsl:value-of select="@type"/>
			<xsl:text><![CDATA[();
					}
					public void action(Object item, NamedSymbol name ) throws Exception {
						o.setField(name,item);
					}
					public void end() throws Exception {
						((LLParser.ObjectReceiver)parent).action( o );
					}
				};
			}
			public String toString() { return "C<]]></xsl:text>
			<xsl:value-of select="@type"/>
			<xsl:text>>"; }
		};</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>


	<!--  rule definitions -->
	<!--===================-->
		<xsl:text>	// all rules</xsl:text>
		<xsl:call-template name="CRLF"/>
		<xsl:text>	private static final Rule[] rules = new Rule[</xsl:text>
		<xsl:value-of select="count(rule)"/>
		<xsl:text>];</xsl:text>
		<xsl:call-template name="CRLF"/>
	
	
		
		
		
		
	<!-- the same problem for the 'final' modifier as above -->
		<xsl:text><![CDATA[
	public static /*final*/ BindableGrammar grammar;
	
	static {
		try {
			final ExpressionPool pool = new ExpressionPool();
			
]]></xsl:text>
	
	
	<!--   create datatype symbols    -->
	<!--==============================-->
		<xsl:text> // TODO: still leave a lot to be desired </xsl:text>
		<xsl:call-template name="CRLF"/>
		
		<xsl:for-each select="dataSymbol">
			<xsl:text>			</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text> = </xsl:text>
			<xsl:choose>
				<xsl:when test="library='' or
								library='http://www.w3.org/2001/XMLSchema' or
								library='http://www.w3.org/2001/XMLSchema-datatypes'">
					<xsl:text>com.sun.msv.datatype.xsd.DatatypeFactory</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:message terminate="yes">
						Unsupported datatype library
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>.getTypeByName("</xsl:text>
			<xsl:value-of select="name"/>
			<xsl:text>");</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>
	
	
	
	<!-- create empty element symbols -->
	<!--==============================-->
	
		<xsl:for-each select="elementSymbol">
			<xsl:text>	 		</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text> = new LLElementExp( </xsl:text>
			<xsl:apply-templates select="name"/>
			<xsl:text> ); </xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>
		
		
	<!-- construct grammar -->
	<!--===================-->
		<xsl:text>		// attributes and shared particles. In the same order as in the xml file </xsl:text>
		<xsl:call-template name="CRLF"/>
		
		<xsl:for-each select="particle|attributeSymbol">
			<xsl:choose>
				<xsl:when test="name()='particle'">
					<xsl:text>			final Expression </xsl:text>
					<xsl:value-of select="@id"/>
					<xsl:text> = </xsl:text>
					<xsl:apply-templates select="*" mode="exp"/>
					<xsl:text>;</xsl:text>
				</xsl:when>
				<xsl:when test="name()='attributeSymbol'">
					<xsl:text>			</xsl:text>
					<xsl:value-of select="@id"/>
					<xsl:text> = new LLAttributeExp(</xsl:text>
					<xsl:apply-templates select="name"/>
					<xsl:text> , </xsl:text>
					<xsl:apply-templates select="content/*" mode="exp"/>
					<xsl:text>);</xsl:text>
				</xsl:when>
			</xsl:choose>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>
		

	<!--      create bare rules       -->
	<!--==============================-->
		<!-- this can be done only after we create attribute symbols -->
		<xsl:for-each select="rule">
			<!-- rule itself -->
			<xsl:text>			rules[</xsl:text>
			<xsl:value-of select="@no"/>
			<xsl:text>] = new Rule( new Object[]{ </xsl:text>
			<xsl:for-each select="right/item">
				<xsl:if test="position()!=1">
					<xsl:text>,</xsl:text>
				</xsl:if>
				<xsl:value-of select="@symbolRef"/>
			</xsl:for-each>
			<xsl:text> }</xsl:text>
			
			<xsl:if test="@interleave='true'">
				<!-- generete filter -->
				<xsl:text>, new Filter[]{</xsl:text>
				<xsl:for-each select="right/item/filter">
					<xsl:if test="position()!=1">
						<xsl:text>,</xsl:text>
					</xsl:if>
					
					<xsl:choose>
						<xsl:when test="count(item)=0">
							<xsl:text>Filter.emptyFilter</xsl:text>
						</xsl:when>
						
						<xsl:when test="count(item)=1">
							<xsl:text>new Filter.SimpleFilter(</xsl:text>
							<xsl:value-of select="item/@symbolRef"/>
							<xsl:text>)</xsl:text>
						</xsl:when>
						
						<xsl:otherwise>
							<xsl:text>new Filter.SetFilter(new Object[]{</xsl:text>
							<xsl:for-each select="item">
								<xsl:if test="position()!=1">
									<xsl:text>,</xsl:text>
								</xsl:if>
								<xsl:value-of select="@symbolRef"/>
							</xsl:for-each>
							<xsl:text>})</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
				<xsl:text>}</xsl:text>
			</xsl:if>
			
			<xsl:text>);</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>
		<xsl:call-template name="CRLF"/>
		
		
	

	<!-- elements -->
		<xsl:text>		// element content models </xsl:text>
		<xsl:call-template name="CRLF"/>
		
		<xsl:for-each select="elementSymbol">
			<!-- create content model -->
			<xsl:text>			</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text>.contentModel = </xsl:text>
			<xsl:apply-templates select="content/*" mode="exp"/>
			<xsl:text>;</xsl:text>
			<xsl:call-template name="CRLF"/>
			
			<!-- then assign a parser table -->
			<xsl:text>			</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text>.parserTable = new Table_</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text>();</xsl:text>
			<xsl:call-template name="CRLF"/>
			
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>

	<!-- attributes -->
		<xsl:text>		// set parsing table for attributes </xsl:text>
		<xsl:call-template name="CRLF"/>
		<xsl:for-each select="attributeSymbol">
			<!-- create content model -->
			<xsl:text>			</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text>.parserTable = new Table_</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text>();</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>
		<xsl:call-template name="CRLF"/>
		
	<!-- top level -->
		<xsl:text>		// top level</xsl:text>
		<xsl:call-template name="CRLF"/>
		<xsl:text>			final Expression topLevel = </xsl:text>
		<xsl:apply-templates select="topLevel/content/*" mode="exp"/>
		<xsl:text>;</xsl:text>
		<xsl:call-template name="CRLF"/>
	
	<!-- root parser table -->
		<xsl:choose>
			<xsl:when test="topLevel/@id">
				<xsl:text>			final LLParserTable rootTable = new Table_</xsl:text>
				<xsl:value-of select="topLevel/@id"/>
				<xsl:text>();</xsl:text>
				<xsl:call-template name="CRLF"/>
				<xsl:text>			final Object rootSymbol = </xsl:text>
				<xsl:value-of select="topLevel/@id"/>
				<xsl:text>;</xsl:text>
				<xsl:call-template name="CRLF"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>			final LLParserTable rootTable = null;</xsl:text>
				<xsl:call-template name="CRLF"/>
				<xsl:text>			final Object rootSymbol = null;</xsl:text>
				<xsl:call-template name="CRLF"/>
			</xsl:otherwise>
		</xsl:choose>
	
	<!-- footer -->
		<xsl:text><![CDATA[
		// set the grammar
			grammar = new BindableGrammar() {
				public ExpressionPool getPool() { return pool; }
				public Expression getTopLevel() { return topLevel; }
				public LLParserTable getRootTable() { return rootTable; }
				public Object getRootSymbol() { return rootSymbol; }
			};
			
		} catch( Exception e ) {
			e.printStackTrace();
			throw new Error();
		}
	}
]]></xsl:text>
	
	
	<!--    parser table   -->
	<!--===================-->
		<xsl:for-each select="*/parserTable">
			<xsl:text>	private static final class Table_</xsl:text>
			<xsl:value-of select="parent::*/@id"/>
			<xsl:text> implements LLParserTable {</xsl:text>
			<xsl:call-template name="CRLF"/>
			
			<xsl:for-each select="action/token">
				<xsl:if test="count(rule)>1">
					<xsl:text>		private static final Rule[] a</xsl:text>
					<xsl:value-of select="@no"/>
					<!--
						if this action has more than one rule,
						an explicit array creation is necessary
					-->
					<xsl:text> = new Rule[]{</xsl:text>
					<xsl:for-each select="rule">
						<xsl:if test="position()!=1">
							<xsl:text>,</xsl:text>
						</xsl:if>
						<xsl:text>rules[</xsl:text>
						<xsl:value-of select="@no"/>
						<xsl:text>]</xsl:text>
					</xsl:for-each>
					<xsl:text>}</xsl:text>
					<xsl:text>;</xsl:text>
					<xsl:call-template name="CRLF"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:call-template name="CRLF"/>
			
			
			<!-- the get method -->
			<xsl:text>		public Rule[] get( Object top, Object input ) {</xsl:text>
			<xsl:call-template name="CRLF"/>
			
			<xsl:for-each select="action">
				<xsl:choose>
					<xsl:when test="token">
						<xsl:text>			if( top==</xsl:text>
						<xsl:value-of select="@stackTop"/>
						<xsl:text> ) {</xsl:text>
						<xsl:call-template name="CRLF"/>
						<xsl:for-each select="token">
							<xsl:text>				if( input==</xsl:text>
							<xsl:value-of select="@id"/>
							<xsl:text> ) return </xsl:text>
							<xsl:choose>
								<xsl:when test="count(rule)>1">
									<xsl:text>a</xsl:text>
									<xsl:value-of select="@no"/>
								</xsl:when>
								<xsl:otherwise>
									<!--
										if an action consists of only one rule, we don't use
										a separate field
									-->
									<xsl:text>rules[</xsl:text>
									<xsl:value-of select="rule/@no"/>
									<xsl:text>].selfArray</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:text>;</xsl:text>
							<xsl:call-template name="CRLF"/>
						</xsl:for-each>
						<xsl:if test="otherwise">
							<xsl:text>				return </xsl:text>
							<xsl:text>rules[</xsl:text>
							<xsl:value-of select="otherwise/rule/@no"/>
							<xsl:text>].selfArray;</xsl:text>
							<xsl:call-template name="CRLF"/>
						</xsl:if>
						<xsl:text>			}</xsl:text>
						<xsl:call-template name="CRLF"/>
					</xsl:when>
					
					<!--
						we can determine the action only from the stack top.
					-->
					<xsl:otherwise>
						<xsl:text>			if( top==</xsl:text>
						<xsl:value-of select="@stackTop"/>
						<xsl:text> ) return rules[</xsl:text>
						<xsl:value-of select="otherwise/rule/@no"/>
						<xsl:text>].selfArray;</xsl:text>
						<xsl:call-template name="CRLF"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
			
			<xsl:text>			return null;</xsl:text>
			<xsl:call-template name="CRLF"/>
			<xsl:text>		}</xsl:text>
			<xsl:call-template name="CRLF"/>
			<!-- end of the get method -->
			
			<xsl:text>	};</xsl:text>
			<xsl:call-template name="CRLF"/>
		</xsl:for-each>


	<xsl:text>
//
// utility methods
//
	public static </xsl:text><xsl:value-of select="rootType/@name"/><xsl:text> unmarshall( String source )
			throws IOException, SAXException, ParserConfigurationException {
		return unmarshall( new InputSource(source) );
	}
	
	public static </xsl:text><xsl:value-of select="rootType/@name"/><xsl:text> unmarshall( InputSource source )
			throws IOException, SAXException, ParserConfigurationException {
		return (</xsl:text><xsl:value-of select="rootType/@name"/><xsl:text>)
			com.sun.tahiti.runtime.ll.Unmarshaller.unmarshall( 
				</xsl:text><xsl:value-of select="name"/><xsl:text>.grammar, source );
	}
	
</xsl:text>
	
	<!--  footer           -->
	<!--===================-->
	<xsl:text>}</xsl:text>
	<xsl:call-template name="CRLF"/>
	
	</xsl:template>

<!--	
			<xsl:text></xsl:text>
-->
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
<!-- generate name class -->
<!--=====================-->
<!--=====================-->
	<xsl:template match="name">
		<xsl:apply-templates select="*" mode="nc"/>
	</xsl:template>
	
		<xsl:template match="name" mode="nc">
			<xsl:text>new SimpleNameClass("</xsl:text>
			<xsl:value-of select="@ns"/>
			<xsl:text>","</xsl:text>
			<xsl:value-of select="@local"/>
			<xsl:text>")</xsl:text>
		</xsl:template>
		
		<xsl:template match="choice" mode="nc">
			<xsl:text>new ChoiceNameClass(</xsl:text>
			<xsl:apply-templates select="*[1]" mode="nc"/>
			<xsl:text>,</xsl:text>
			<xsl:apply-templates select="*[2]" mode="nc"/>
			<xsl:text>)</xsl:text>
		</xsl:template>
		
		<xsl:template match="difference" mode="nc">
			<xsl:text>new DifferenceNameClass(</xsl:text>
			<xsl:apply-templates select="*[1]" mode="nc"/>
			<xsl:text>,</xsl:text>
			<xsl:apply-templates select="*[2]" mode="nc"/>
			<xsl:text>)</xsl:text>
		</xsl:template>
		
		<xsl:template match="anyName" mode="nc">
			<xsl:text>AnyNameClass.theInstance</xsl:text>
		</xsl:template>	
		
		<xsl:template match="nsName" mode="nc">
			<xsl:text>new NamespaceNameClass("</xsl:text>
			<xsl:value-of select="@ns"/>
			<xsl:text>")</xsl:text>
		</xsl:template>
		
		<xsl:template match="not" mode="nc">
			<xsl:text>new NotNameClass(</xsl:text>
			<xsl:apply-templates select="*" mode="nc"/>
			<xsl:text>)</xsl:text>
		</xsl:template>




<!-- generate expression -->
<!--=====================-->
<!--=====================-->
	<xsl:template match="choice" mode="exp">
		<xsl:text>pool.createChoice(</xsl:text>
		<xsl:apply-templates select="*[1]" mode="exp"/>
		<xsl:text>,</xsl:text>
		<xsl:apply-templates select="*[2]" mode="exp"/>
		<xsl:text>)</xsl:text>
	</xsl:template>
	
	<xsl:template match="interleave" mode="exp">
		<xsl:text>pool.createInterleave(</xsl:text>
		<xsl:apply-templates select="*[1]" mode="exp"/>
		<xsl:text>,</xsl:text>
		<xsl:apply-templates select="*[2]" mode="exp"/>
		<xsl:text>)</xsl:text>
	</xsl:template>
	
	<xsl:template match="group" mode="exp">
		<xsl:text>pool.createSequence(</xsl:text>
		<xsl:apply-templates select="*[1]" mode="exp"/>
		<xsl:text>,</xsl:text>
		<xsl:apply-templates select="*[2]" mode="exp"/>
		<xsl:text>)</xsl:text>
	</xsl:template>
	
	<xsl:template match="epsilon" mode="exp">
		<xsl:text>Expression.epsilon</xsl:text>
	</xsl:template>
	
	<xsl:template match="text" mode="exp">
		<xsl:text>Expression.anyString</xsl:text>
	</xsl:template>
	
	<xsl:template match="key" mode="exp">
		<xsl:text>pool.createKey(</xsl:text>
		<xsl:apply-templates select="*" mode="exp"/>
		<xsl:text>, new StringPair("</xsl:text>
		<xsl:value-of select="@ns"/>
		<xsl:text>","</xsl:text>
		<xsl:value-of select="@name"/>
		<xsl:text>"))</xsl:text>
	</xsl:template>
	
	<xsl:template match="keyref" mode="exp">
		<xsl:text>pool.createKeyref(</xsl:text>
		<xsl:apply-templates select="*" mode="exp"/>
		<xsl:text>, new StringPair("</xsl:text>
		<xsl:value-of select="@ns"/>
		<xsl:text>","</xsl:text>
		<xsl:value-of select="@name"/>
		<xsl:text>"))</xsl:text>
	</xsl:template>
	
	<xsl:template match="list" mode="exp">
		<xsl:text>pool.createList(</xsl:text>
		<xsl:apply-templates select="*" mode="exp"/>
		<xsl:text>)</xsl:text>
	</xsl:template>
	
	<xsl:template match="oneOrMore" mode="exp">
		<xsl:text>pool.createOneOrMore(</xsl:text>
		<xsl:apply-templates select="*" mode="exp"/>
		<xsl:text>)</xsl:text>
	</xsl:template>
	
	<xsl:template match="typedString" mode="exp">
		<xsl:text>pool.createData(</xsl:text>
		<xsl:value-of select="@dataSymbolRef"/>
		<xsl:text>,emptyName)</xsl:text>
	</xsl:template>
	
	<xsl:template match="element" mode="exp">
		<xsl:value-of select="@symbolRef"/>
	</xsl:template>
	
	<xsl:template match="attribute" mode="exp">
		<xsl:value-of select="@symbolRef"/>
	</xsl:template>
	
	<xsl:template match="ref" mode="exp">
		<xsl:value-of select="@particle"/>
	</xsl:template>
	
	
	
	
	
	
<!-- utility methods -->
<!--=================-->
<!--=================-->
	<xsl:template name="CRLF">
<xsl:text>
</xsl:text>
	</xsl:template>
</xsl:stylesheet>