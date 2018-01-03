package bufmgr;

import java.util.LinkedList;

import global.PageId;

/**
 * This class is used to track of the pages in the buffer pool.
 * 
 * @author Balamurugan Anandan
 *
 */
public class BufferHashTable {

	private static final int a = 2;
	private static final int b = 3;
	private int HTSIZE;
	private BufferHTEntry[] directory;

	/**
	 * Constructor to create the buffer table
	 * 
	 * @param size
	 *            size of the Hash table
	 */
	public BufferHashTable(int size) {
		HTSIZE = size;
		directory = new BufferHTEntry[HTSIZE];
	}

	/**
	 * this method computes the hash value given the page number
	 * 
	 * @param page_number
	 *            page number
	 * @return
	 */
	private int getHashValue(int page_number) {
		return (a * page_number + b) % HTSIZE;
	}

	/**
	 * This methods adds the tuple (page number, frame number) to the hash table
	 * 
	 * @param page_number
	 * @param frame_number
	 */
	public void addPageNumber(PageId page_number, int frame_number) {
		int bucket_no = getHashValue(page_number.pid);
		BufferHTEntry newEntry = new BufferHTEntry(page_number.pid,
				frame_number);
		BufferHTEntry entry = directory[bucket_no];
		if (entry == null) {
			directory[bucket_no] = newEntry;
		} else {
			while (entry.getNext() != null)
				entry = entry.getNext();
			entry.setNext(newEntry);
		}

	}

	/**
	 * This methods removes the tuple (page number, frame number) from the hash
	 * table
	 * 
	 * @param page_number
	 */
	public void removePageNumber(PageId page_number) {
		int bucket_no = getHashValue(page_number.pid);
		BufferHTEntry entry = directory[bucket_no];
		if (entry != null) {
			if (entry.getPageNumber() == page_number.pid) {
				directory[bucket_no] = entry.getNext();
			} else {
				while (entry.getNext() != null) {
					if (entry.getNext().getPageNumber() == page_number.pid) {
						entry.setNext(entry.getNext().getNext());
						break;
					}
					entry = entry.getNext();
				}
			}
		}

	}

	/**
	 * Return the frame number associated with a page number
	 * 
	 * @param page_number
	 * @return frame number
	 */
	public int getFrameNumber(PageId page_number) {
		int bucket_no = getHashValue(page_number.pid);
		BufferHTEntry entry = directory[bucket_no];
		int frameNumber = -1;
		while (entry != null) {
			if (entry.getPageNumber() == page_number.pid) {
				frameNumber = entry.getFrameNumber();
				break;
			}
			entry = entry.getNext();
		}
		return frameNumber;
	}

	/**
	 * @return all frames in the hash table
	 */
	public LinkedList<PageId> getAllFrames() {
		LinkedList<PageId> allFrames = new LinkedList<PageId>();
		BufferHTEntry entry;
		for (int i = 0; i < HTSIZE; i++) {
			entry = directory[i];
			while (entry != null) {
				PageId pageid = new PageId();
				pageid.pid = entry.getFrameNumber();
				allFrames.add(pageid);
				entry = entry.getNext();
			}
		}
		return allFrames;
	}

	public void printHashTable() {
		for (int i = 0; i < HTSIZE; i++) {
			System.out.println("Bucket: " + i);
			BufferHTEntry entry = directory[i];
			while (entry != null) {
				System.out.println(entry.getPageNumber() + " "
						+ entry.getFrameNumber());
				entry = entry.getNext();
			}
		}
	}

}
