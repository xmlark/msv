package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.NameClass;

/**
 * interface that must be implemented by the parent state of NameClassState.
 * 
 * NameClassState notifies its parent by using this interface.
 */
interface NameClassOwner
{
	void onEndChild( NameClass p );
}
