package com.sun.tahiti.compiler.ll;

import com.sun.msv.grammar.*;

public class Util
{
	public static boolean isTerminalSymbol( Expression exp ) {
		return	exp instanceof TypedStringExp
			||	exp==Expression.anyString
			||	exp==Expression.epsilon;
	}
}
