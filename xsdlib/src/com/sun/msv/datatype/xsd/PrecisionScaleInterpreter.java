package com.sun.tranquilo.datatype;

public interface PrecisionScaleInterpreter
{
    int getScaleForValueObject( Object o );
    int getPrecisionForValueObject( Object o );
}

