package batch.driver;

import batch.model.ISchema;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.relaxng.RELAXNGGrammar;
    
public class ISchemaImpl implements ISchema {
    
    private final Grammar grammar;
    public Grammar asGrammar() { return grammar; }
    
    ISchemaImpl( Grammar grammar ) {
        if(grammar==null)    throw new Error("grammar is null");
        this.grammar = grammar;
    }
    
    private Boolean getBool( boolean v ) {
        if(v)    return Boolean.TRUE;
        else    return Boolean.FALSE;
    }
    
    public Boolean isAnnotationCompatible() {
        if(!(grammar instanceof RELAXNGGrammar))    return null;
        return getBool(((RELAXNGGrammar)grammar).isAnnotationCompatible);
    }
    
    public Boolean isIdIdrefCompatible() {
        if(!(grammar instanceof RELAXNGGrammar))    return null;
        return getBool(((RELAXNGGrammar)grammar).isIDcompatible);
    }

    public Boolean isDefaultValueCompatible() {
        if(!(grammar instanceof RELAXNGGrammar))    return null;
        return getBool(((RELAXNGGrammar)grammar).isDefaultAttributeValueCompatible);
    }
}
