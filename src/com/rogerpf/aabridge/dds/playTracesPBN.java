package com.rogerpf.aabridge.dds;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * <i>native declaration : line 320</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class playTracesPBN extends Structure {
	public int noOfBoards;
	/** C type : playTracePBN[200] */
	public playTracePBN[] plays = new playTracePBN[200];

	public playTracesPBN() {
		super();
	}

	protected List<?> getFieldOrder() {
		return Arrays.asList("noOfBoards", "plays");
	}

	/** @param plays C type : playTracePBN[200] */
	public playTracesPBN(int noOfBoards, playTracePBN plays[]) {
		super();
		this.noOfBoards = noOfBoards;
		if ((plays.length != this.plays.length))
			throw new IllegalArgumentException("Wrong array size !");
		this.plays = plays;
	}

	public playTracesPBN(Pointer peer) {
		super(peer);
	}

	public static class ByReference extends playTracesPBN implements Structure.ByReference {

	};

	public static class ByValue extends playTracesPBN implements Structure.ByValue {

	};
}
