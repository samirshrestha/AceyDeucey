package com.RotN.acdc.logic;

import java.io.Serializable;

public class Stats implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public int rolls = 0;
	public int ones = 0;
	public int twos = 0;
	public int threes = 0;
	public int fours = 0;
	public int fives = 0;
	public int sixes = 0;
	public int acdcs = 0;
	
	public void clear() {
		rolls = 0;
		ones = 0;
		twos = 0;
		threes = 0;
		fours = 0;
		fives = 0;
		sixes = 0;
		acdcs = 0;
	}
}
