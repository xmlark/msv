package com.sun.tranquilo.relaxns.grammar;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ElementExp;
import com.sun.tranquilo.grammar.NameClass;
import com.sun.tranquilo.grammar.NamespaceNameClass;
import com.sun.tranquilo.grammar.ExpressionPool;
import com.sun.tranquilo.grammar.ReferenceExp;
import org.iso_relax.dispatcher.Rule;
import org.xml.sax.Locator;

/**
 * place holder for imported rule.
 * 
 * This class derives ElementExp because "rule" is a constraint over one element.
 * This class also provides stub methods so that programs who are not aware to
 * divide&validate can gracefully degrade.
 */
public class ExternalElementExp extends ElementExp
{
	public NameClass getNameClass() { return nameClass; }

	/** NamespaceNameClass object that matchs this namespace. */
	private final NamespaceNameClass nameClass;
	
	/** namespace URI that this ExternalElementExp belongs to. */
	public final String namespaceURI;
	
	/** name of the imported Rule */
	public final String ruleName;
	
	/** where did this reference is written in the source file.
	 * 
	 * can be set to null (to reduce memory usage) at anytime.
	 */
	public Locator source;
	
	/**
	 * imported Rule object that actually validates this element.
	 * this variable is set during binding phase.
	 */
	public Rule rule;

	public ExternalElementExp(
		ExpressionPool pool, String namespaceURI, String ruleName,
		Locator loc )
	{
		// set content model to nullSet
		// to make this elementExp accept absolutely nothing.
		// "ignoreUndeclaredAttributes" flag is also meaningless here
		// because actual validation of this element will be done by a different
		// IslandVerifier.
		super(Expression.nullSet,false);
		
		this.ruleName = ruleName;
		this.namespaceURI = namespaceURI;
		this.nameClass = new NamespaceNameClass(namespaceURI);
		this.source = loc;
		
		/* provide dummy content model
		
			<mixed>
				<zeroOrMore>
					<choice>
						<attribute>
							<nsName/>
						</attribute>
						<<<< this >>>>
					</choice>
				</zeroOrMore>
			</mixed>
		*/
		this.contentModel = pool.createZeroOrMore(
			pool.createMixed(
				pool.createChoice(
					pool.createAttribute(nameClass),
					this ) ) );
	}
	
}
