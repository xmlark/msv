package com.sun.tranquilo.schema;

/**
 * &lt;mixed /&gt; element
 * 
 * <p>
 * "mixed" is a HedgeModel because it can be used as a content model of elementRules.
 * However, "mixed" is not a Particle because it cannot be a child of "choice".
 * 
 * <p>
 * Also, "mixed" is a ModelParent because it contains exactly one HedgeModel
 * within it. Note that "mixed" can only contain Particle and thus cannot
 * contain other HedgeModel (like "mixed").
 */
public class Mixed extends ModelParent implements HedgeModel
{
	public Mixed( Particle contentModel )	{ super(contentModel); }
	
	public void setHedgeModel( HedgeModel hm )
	{// caller cannot set HedgeModel (they can only set Particle)
		if( hm instanceof Particle )	super.setHedgeModel(hm);
		else							throw new IllegalArgumentException();
	}

}
