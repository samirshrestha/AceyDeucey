package com.RotN.acdc;

import java.util.ArrayList;

import com.RotN.acdc.logic.Move;

public class AnimationThread extends Thread {
	
	private GammonBoard theBoard;
	private boolean running;
	private ArrayList<Move> moves;
	
	// desired fps
	private final static int 	MAX_FPS = 50;	
	// maximum number of frames to be skipped
	private final static int	MAX_FRAME_SKIPS = 5;	
	// the frame period
	private final static int	FRAME_PERIOD = 1000 / MAX_FPS;
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public AnimationThread(GammonBoard theBoard, ArrayList<Move> moves) {
		this.theBoard = theBoard;
		this.moves = moves;
	}
	
	@Override
	public void run() {
		
		
		long beginTime;		// the time when the cycle begun
		long timeDiff;		// the time it took for the cycle to execute
		int sleepTime;		// ms to sleep (<0 if we're behind)
		int framesSkipped;	// number of frames being skipped 
		
		sleepTime = 0;
		
		for (Move move : moves) {		
			while (false == this.theBoard.updateAnimatedPieces()) {
				beginTime = System.currentTimeMillis();
				framesSkipped = 0;	// resetting the frames skipped
				// render state to the screen
				// draws the canvas on the panel
				this.theBoard.render();			
				// calculate how long did the cycle take
				timeDiff = System.currentTimeMillis() - beginTime;
				// calculate sleep time
				sleepTime = (int)(FRAME_PERIOD - timeDiff);
				
				if (sleepTime > 0) {
					// if sleepTime > 0 we're OK
					try {
						// send the thread to sleep for a short period
						// very useful for battery saving
						Thread.sleep(sleepTime);	
					} catch (InterruptedException e) {}
				}
				
				while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
					// we need to catch up
					this.theBoard.updateAnimatedPieces(); // update without rendering
					sleepTime += FRAME_PERIOD;	// add frame period to check if in next frame
					framesSkipped++;
				}
			}	// end finally
		}
	}
}
