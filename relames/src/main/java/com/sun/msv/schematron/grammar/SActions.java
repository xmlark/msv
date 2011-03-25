/*
 * @(#)$Id$
 *
 * Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schematron.grammar;

import java.util.Collection;

/**
 * Set of reports and asserts.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SActions {
    public final SAction[]    asserts;
    public final SAction[]    reports;
    
    public SActions( SAction[] _asserts, SAction[] _reports ) {
        this.asserts = _asserts;
        this.reports = _reports;
    }
    public SActions( Collection _asserts, Collection _reports ) {
        this(
            (SAction[]) _asserts.toArray(new SAction[_asserts.size()]),
            (SAction[]) _reports.toArray(new SAction[_reports.size()]) );
    }
}
