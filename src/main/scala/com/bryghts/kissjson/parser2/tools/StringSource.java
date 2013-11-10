//package com.bryghts.kissjson.parser2.tools;
//
//
//public class StringSource
//{
//	private final int l;
//	private int n;
//    private final String src;
//
//    public char top = 0;
//
//    public StringSource(String src){
//        l = src.length() - 1;
//        n = -1;
//        this.src = src;
//
//        if(n >= l) top = 0x00;
//        else top = src.charAt(++n);
//    }
//
//	public void moveNext() {
//        if(n >= l) top = 0x00;
//        else top = src.charAt(++n);
//    }
//
//}
