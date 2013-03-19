package com.RotN.acdc.logic;

import java.util.ArrayList;

public class AIMoves implements Cloneable {
	ArrayList<Move> moves = new ArrayList<Move>();
	Integer value = -9000;
	
	@Override
	public AIMoves clone() {
		try {
			return (AIMoves)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}