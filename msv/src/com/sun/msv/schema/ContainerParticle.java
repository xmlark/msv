package com.sun.tranquilo.schema;

import java.util.Iterator;
import java.util.List;

/**
 * Particle that can contain other particles as children
 * 
 * Only "sequence" and "choice" are the container particles.
 * This class provides child-care functionality.
 */
public abstract class ContainerParticle extends RepetableParticle
{
	/** stores all child particles */
	protected class Children
	{
		/** actual storage  */
		private final List impl = new java.util.LinkedList();
		
		public void appendChild( Particle newChild )	{ impl.add(newChild); }
		public Iterator iterator()						{ return impl.iterator(); }
		public int size()								{ return impl.size(); }
		public Particle get( int index )				{ return (Particle)impl.get(index); }
	}
	
	/** child particles */
	public final Children children = new Children();
	
	protected ContainerParticle( Particle initialChild )
	{
		children.appendChild(initialChild);
	}
	
	protected ContainerParticle() {}
}
