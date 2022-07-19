/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sun.msv.grammar.relax;

import java.util.Map;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;

/**
 * "Module" of RELAX Core.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXModule implements Grammar {
    
    final public class ElementRulesContainer extends ReferenceContainer {
        public ElementRules getOrCreate( String name ) {
            return (ElementRules)super._getOrCreate(name);
        }
        public ElementRules get( String name ) {
            return (ElementRules)super._get(name);
        }
        protected ReferenceExp createReference( String name ) {
            return new ElementRules(name,RELAXModule.this);
        }
    }
    /** map from label name to ElementRules object */
    public final ElementRulesContainer elementRules = new ElementRulesContainer();
    
    final public class HedgeRulesContainer extends ReferenceContainer {
        public HedgeRules getOrCreate( String name ) {
            return (HedgeRules)super._getOrCreate(name);
        }
        public HedgeRules get( String name ) {
            return (HedgeRules)super._get(name);
        }
        protected ReferenceExp createReference( String name ) {
            return new HedgeRules(name,RELAXModule.this);
        }
    }
    /** map from label name to HedgeRules object */
    public final HedgeRulesContainer hedgeRules = new HedgeRulesContainer();

    final public class TagContainer extends ReferenceContainer {
        public TagClause getOrCreate( String name ) {
            return (TagClause)super._getOrCreate(name);
        }
        public TagClause get( String name ) {
            return (TagClause)super._get(name);
        }
        protected ReferenceExp createReference( String name ) {
            return new TagClause(name);
        }
    }
    /** map from role name to TagClause object */
    public final TagContainer tags = new TagContainer();

    final public class AttPoolContainer extends ReferenceContainer {
        public AttPoolClause getOrCreate( String name ) {
            return (AttPoolClause)super._getOrCreate(name);
        }
        public AttPoolClause get( String name ) {
            return (AttPoolClause)super._get(name);
        }
        protected ReferenceExp createReference( String name ) {
            return new AttPoolClause(name);
        }
    }
    /** map from role name to AttPoolClause object */
    public final AttPoolContainer attPools = new AttPoolContainer();
//    /** map from role name to exported AttPoolClause object */
//    public final AttPoolContainer exportedAttPools = new AttPoolContainer();
    
    /*
        exported AttPool objects are treated differently because
        they have difference in validation semantics. Namely,
        when attPool is used from the same module, its attribute declarations
        validate themselves against default namespace(""),
        whereas when used from the external modules, they validate
        themselves against target namespace of the module.
    
        The difficult part is that we have to achieve this semantics even when
        we use the RELAX schema with com.sun.msv.verifier.trex.
    
        To do this, this class has separate difinition for all exported attPools.
    */
    
    /** target namespace URI */
    public final String targetNamespace;
    
    /** Datatypes. */
    public class DatatypeContainer {
        private final Map m = new java.util.HashMap();
        
        public XSDatatype get( String name ) {
            return (XSDatatype)m.get(name);
        }
        public void add( XSDatatype dt ) {
            if(dt.getName()==null)  throw new IllegalArgumentException();
            m.put(dt.getName(),dt);
        }
    };
    public final DatatypeContainer datatypes = new DatatypeContainer();
    
    
    /**
     * chioce of all exported elementRules and hedgeRules.
     * 
     * This can be used as the top-level expression when a module is used
     * to validate documents by itself.
     */
    public Expression topLevel;
    public Expression getTopLevel() { return topLevel; }
    
    /**
     * ExpressionPool object which was used to create this module.
     */
    public final ExpressionPool pool;
    public ExpressionPool getPool() { return pool; }
    
    public RELAXModule( ExpressionPool pool, String targetNamespace ) {
        // if you don't want to namespace, specify ""
        if( targetNamespace==null )        throw new NullPointerException();
        
        this.pool = pool;
        this.targetNamespace = targetNamespace;
        datatypes.add( EmptyStringType.theInstance );
        datatypes.add( NoneType.theInstance );
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
