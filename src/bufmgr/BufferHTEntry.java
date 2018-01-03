package bufmgr;

/**
 * This class specifies an entry of a hash table. It stores the page number,
 * frame number of a page.
 * 
 * @author Balamurugan Anandan
 *
 */
public class BufferHTEntry {

	private int page_number;
	private int frame_number;
	private BufferHTEntry next;

	/**
	 * @param page_number
	 *            page number
	 * @param frame_number
	 *            frame number
	 */
	public BufferHTEntry(int page_number, int frame_number) {
		this.page_number = page_number;
		this.frame_number = frame_number;
		this.next = null;
	}

	public void setPageNumber(int page_number) {
		this.page_number = page_number;
	}

	public void setFrameNumber(int frame_number) {
		this.frame_number = frame_number;
	}

	public void setNext(BufferHTEntry next) {
		this.next = next;
	}

	public int getPageNumber() {
		return page_number;
	}

	public int getFrameNumber() {
		return frame_number;
	}

	public BufferHTEntry getNext() {
		return next;
	}
}
