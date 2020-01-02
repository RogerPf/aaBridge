package com.rogerpf.aabridge.dds;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * <i>native declaration : line 207</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class solvedBoards extends Structure {
	public int noOfBoards;
	/** C type : futureTricks[200] */
	public futureTricks[] solvedBoard = new futureTricks[200];

	public solvedBoards() {
		super();
	}

	protected List<?> getFieldOrder() {
		return Arrays.asList("noOfBoards", "solvedBoard");
	}

	/** @param solvedBoard C type : futureTricks[200] */
	public solvedBoards(int noOfBoards, futureTricks solvedBoard[]) {
		super();
		this.noOfBoards = noOfBoards;
		if ((solvedBoard.length != this.solvedBoard.length))
			throw new IllegalArgumentException("Wrong array size !");
		this.solvedBoard = solvedBoard;
	}

	public solvedBoards(Pointer peer) {
		super(peer);
	}

	public static class ByReference extends solvedBoards implements Structure.ByReference {

	};

	public static class ByValue extends solvedBoards implements Structure.ByValue {

	};
}
