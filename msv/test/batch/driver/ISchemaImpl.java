package msv;

import org.relaxng.testharness.validator.ISchema;
import com.sun.msv.grammar.Grammar;
	
class ISchemaImpl implements ISchema {
	Grammar grammar;
	
	ISchemaImpl( Grammar grammar ) {
		if(grammar==null)	throw new Error("grammar is null");
		this.grammar = grammar;
	}
}
