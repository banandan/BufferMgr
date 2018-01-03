package bufmgr;

import java.util.*;

/**
 * Implements the clock page replacement algorithm
 * 
 * @author Balamurugan Anandan
 *
 */
public class Clock {

	private LinkedList<Integer> frameList;

	public Clock() {
		frameList = new LinkedList<Integer>();
	}

	/**
	 * Adds a frame
	 * 
	 * @param frame_no
	 */
	public void addFrame(int frame_no) {
		frameList.add(new Integer(frame_no));
	}

	/**
	 * @return a candidate to be replaced
	 */
	public int findReplacement() {
		int frame_no = -1;
		if (frameList.size() > 0) {
			frame_no = frameList.get(0).intValue();
			frameList.remove(0);
		}
		return frame_no;
	}

	/**
	 * Removes a candidate frame specified
	 * 
	 * @param frame_no
	 */
	public void removeCandidate(int frame_no) {
		for (int i = 0; i < frameList.size(); i++) {
			if (frameList.get(i).intValue() == frame_no) {
				frameList.remove(i);
			}
		}
	}

	/**
	 * This method checks if a frame is present in the frame list
	 * 
	 * @param frame_no
	 * @return
	 */
	public boolean find(int frame_no) {
		boolean isPresent = false;
		for (int i = 0; i < frameList.size(); i++) {
			if (frameList.get(i).intValue() == frame_no) {
				isPresent = true;
				break;
			}
		}
		return isPresent;
	}

	/**
	 * @return size of the frame list
	 */
	public int size() {
		return frameList.size();
	}

}
