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

package com.sun.msv.grammar.xmlschema;

import java.util.Map;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;

/**
 * XML Schema object.
 * 
 * <p>
 * A set of "schema components" that share the same target namespace.
 * It contains all global declarations.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XMLSchemaSchema implements java.io.Serializable {

    public static final String XMLSchemaInstanceNamespace =
        "http://www.w3.org/2001/XMLSchema-instance";

    public XMLSchemaSchema( String targetNamespace, XMLSchemaGrammar parent ) {
        this.pool = parent.pool;
        this.targetNamespace = targetNamespace;
        parent.schemata.put( targetNamespace, this );
    }
    
    /** target namespace URI of this schema. */
    public final String targetNamespace;
    
    /** pool object which was used to construct this grammar. */
    public final ExpressionPool pool;
    
    /** choice of all global element declarations. */
    public Expression topLevel;
    
    
    final public class SimpleTypeContainer extends ReferenceContainer {
        public SimpleTypeExp getOrCreate( String name ) {
            return (SimpleTypeExp)super._getOrCreate(name); }

        public SimpleTypeExp get( String name )
        { return (SimpleTypeExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new SimpleTypeExp(name); }
    }
    /** map from simple type name to SimpleTypeExp object */
    public final SimpleTypeContainer simpleTypes = new SimpleTypeContainer();
    
    
    final public class ComplexTypeContainer extends ReferenceContainer {
        public ComplexTypeExp getOrCreate( String name ) {
            return (ComplexTypeExp)super._getOrCreate(name); }

        public ComplexTypeExp get( String name )
        { return (ComplexTypeExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new ComplexTypeExp(XMLSchemaSchema.this,name); }
    }
    /** map from simple type name to SimpleTypeExp object */
    public final ComplexTypeContainer complexTypes = new ComplexTypeContainer();

    
    final public class AttributeGroupContainer extends ReferenceContainer {
        public AttributeGroupExp getOrCreate( String name ) {
            return (AttributeGroupExp)super._getOrCreate(name); }

        public AttributeGroupExp get( String name )
        { return (AttributeGroupExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new AttributeGroupExp(name); }
    }
    /** map from attribute group name to AttributeGroupExp object */
    public final AttributeGroupContainer attributeGroups = new AttributeGroupContainer();
    
    
    final public class AttributeDeclContainer extends ReferenceContainer {
        public AttributeDeclExp getOrCreate( String name ) {
            return (AttributeDeclExp)super._getOrCreate(name); }

        public AttributeDeclExp get( String name )
        { return (AttributeDeclExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new AttributeDeclExp(name); }
    }
    /** map from attribute declaration name to AttributeDeclExp object */
    public final AttributeDeclContainer attributeDecls = new AttributeDeclContainer();
    
    
    final public class ElementDeclContainer extends ReferenceContainer {
        public ElementDeclExp getOrCreate( String name ) {
            return (ElementDeclExp)super._getOrCreate(name); }

        public ElementDeclExp get( String name )
        { return (ElementDeclExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new ElementDeclExp(XMLSchemaSchema.this,name); }
    }
    /** map from attribute declaration name to AttributeDeclExp object */
    public final ElementDeclContainer elementDecls = new ElementDeclContainer();
    
    
    final public class GroupDeclContainer extends ReferenceContainer {
        public GroupDeclExp getOrCreate( String name ) {
            return (GroupDeclExp)super._getOrCreate(name); }

        public GroupDeclExp get( String name )
        { return (GroupDeclExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new GroupDeclExp(name); }
    }
    /** map from attribute declaration name to AttributeDeclExp object */
    public final GroupDeclContainer groupDecls = new GroupDeclContainer();
    
    
    final public class IdentityConstraintContainer implements java.io.Serializable {
        private final Map storage = new java.util.HashMap();
        public IdentityConstraint get( String name ) {
            return (IdentityConstraint)storage.get(name);
        }
        public void add( String name, IdentityConstraint idc ) {
            storage.put(name,idc);
        }
    }
    /** map from identity constraint name to IdentityConstraint object. */
    public final IdentityConstraintContainer identityConstraints = new IdentityConstraintContainer();
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
