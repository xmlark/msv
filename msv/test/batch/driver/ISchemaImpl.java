package msv;

import org.relaxng.testharness.validator.ISchema;
import com.sun.msv.verifier.DocumentDeclaration;
	
class ISchemaImpl implements ISchema {
	DocumentDeclaration docDecl;
	
	ISchemaImpl( DocumentDeclaration docDecl ) {
		this.docDecl = docDecl;
	}
}
