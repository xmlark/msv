package com.sun.tranquilo.datatype.test;


interface ErrorReceiver
{
	/** return true to abort test */
	boolean report( UnexpectedResultException exp );
}