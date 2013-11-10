package com.bryghts.kissjson.parser2.tools.fsb;

import java.util.Arrays;

public class FastStringBuffer
{
	private int l = 0;
	private char buffer[];

	private int limit;// =

    public FastStringBuffer (int initialCapacity)
    {
        buffer = new char[(initialCapacity < 10)? 10 : initialCapacity];
        limit = ((initialCapacity < 10)? 10 : initialCapacity) - 2;
    }

	public void clear() {l = 0;}

    public void revert() {
        l--;
    }
	public void append (char c)
	{

		if(l >= limit)
		{
			int s = buffer.length << 1;
			buffer = Arrays.copyOf(buffer, s);
			limit = s - 2;
		}

		buffer[l] = c;
		l ++;

	}

	@Override public String toString(){return new String(buffer, 0, l);}
}
