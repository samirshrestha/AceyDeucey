package com.RotN.acdc.logic;

import java.util.ArrayList;

public class AIMoves {
	ArrayList<Move> moves = new ArrayList<Move>();
	Integer value = -9000;
	
	public AIMoves() {
		
	}
	
	public AIMoves(AIMoves rhs) {
		for (Move move: rhs.moves) {
			this.moves.add(new Move(move));
		}
		
		this.value = rhs.value;
	}
}