package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.relax.RELAXModule;
import com.sun.tranquilo.reader.*;

/**
 * Used to parse module.
 * 
 * This class checks consistency between targetNamespace attribute
 * and the namespace specified by its caller (grammar/module).
 */
public class ModuleMergeState extends DivInModuleState
{
	protected ModuleMergeState( String expectedTargetNamespace )
	{
		this.expectedTargetNamespace = expectedTargetNamespace;
	}
	
	/** expected targetNamespace for this module.
	 * 
	 * null indicates that module must have targetNamespace attribute.
	 * 
	 * <p>
	 * If RELAX module has 'targetNamespace' attribute, then its value
	 * must be equal to this value, or this value must be null.
	 * 
	 * <p>
	 * If RELAX module doesn't have the attribute, then this value is
	 * used as the target namespace. If this value is null, then it is
	 * an error.
	 */
	protected final String expectedTargetNamespace;

	protected void startSelf()
	{
		super.startSelf();
		
		{// check relaxCoreVersion
			final String coreVersion = startTag.getAttribute("relaxCoreVersion");
			if( coreVersion==null )
				reader.reportWarning( RELAXReader.ERR_MISSING_ATTRIBUTE, "module", "relaxCoreVersion" );
			else
			if(!"1.0".equals(coreVersion))
				reader.reportWarning( RELAXReader.WRN_ILLEGAL_RELAXCORE_VERSION, coreVersion );
		}
		
		String targetNamespace = startTag.getAttribute("targetNamespace");
		
		// TODO: make sure that this handling is correct.
		if(targetNamespace!=null)
		{
			// check accordance with expected namespace
			if( expectedTargetNamespace!=null
			&&  !expectedTargetNamespace.equals(targetNamespace) )
			{// error
				reader.reportError( RELAXReader.ERR_INCONSISTENT_TARGET_NAMESPACE,
									targetNamespace, expectedTargetNamespace );
				// recover by ignoring one specified in the module
				targetNamespace = expectedTargetNamespace;
			}
		}
		else
		{// no targetnamespace attribute is given.
			if( expectedTargetNamespace==null )
			{
				reader.reportError( RELAXReader.ERR_MISSING_TARGET_NAMESPACE );
				targetNamespace = "";	// recover by assuming the default namespace
			}
			else
				targetNamespace = expectedTargetNamespace;
		}
		
		RELAXModule m = getReader().getOrCreateModule(targetNamespace);
		
		onModuleDetermined(m);
	}
	
	/**
	 * this method is called after checking targetNamespace attribute.
	 */
	protected void onModuleDetermined( RELAXModule m ) {}
}
