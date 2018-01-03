package bufmgr;

/**
 * This class used to represent a  descriptor is maintained per frame to record page_number, pin_count and dirtybit.
 * @author Balamurugan Anandan
 *
 */
public class FrameDescriptor {
	
	private int page_number;
	private int pin_count;
	private boolean dirtybit;
	private boolean empty;
	
	public FrameDescriptor(){
		dirtybit = false;
		pin_count = 0;
		page_number = 0;
		empty = true;
	}
	
	public FrameDescriptor(int page_number, int pin_count, boolean dirtybit){
		this.dirtybit = dirtybit;
		this.pin_count = pin_count;
		this.page_number = page_number;
		empty = false;
	}
	
	public int getPinCount(){
		return pin_count;
	}
	
	public int getPageNumber(){
		return page_number;
	}
	
	public boolean getDirtyBit(){
		return dirtybit;
	}
	
	public void setDirtyBit(boolean dirtybit){
		this.dirtybit = dirtybit;
	}
	
	public void setPinCount(int pin_count){
		this.pin_count = pin_count;
	}
	
	public boolean isEmpty(){
	    return empty;
	}
}
