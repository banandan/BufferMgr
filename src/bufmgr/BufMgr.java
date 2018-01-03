package bufmgr;

import java.util.*;

import diskmgr.*;
import global.*;

/**
 * 
 * This class provides the interface for a client (a higher level program that
 * calls the Buffer Manager) to allocate/de-allocate pages on disk, to bring a
 * disk page into the buffer pool and pin it, and to unpin a page in the buffer
 * pool.
 * 
 * @author Balamurugan Anandan
 *
 */
public class BufMgr implements GlobalConst {
	private enum Policy {
		CLOCK, MRU, LRU, LH
	};

	private int bufferSize;
	private Policy replacePolicy;
	private byte[][] bufPool;
	private Clock replacement;
	private FrameDescriptor[] bufDescr;
	private BufferHashTable hashTbl;

	/**
	 * Create the BufMgr object. Allocate pages (frames) for the buffer pool in
	 * main memory and make the buffer manage aware that the replacement policy
	 * is specified by replacerArg (i.e. HL, Clock, LRU, MRU etc.).
	 *
	 * @param numbufs
	 *            number of buffers in the buffer pool.
	 * @param replacerArg
	 *            name of the buffer replacement policy.
	 * @throws ReplacerException
	 * @throws InvalidFrameSizeException
	 */
	public BufMgr(int numbufs, String replacerArg) throws ReplacerException,
			InvalidFrameSizeException {

		if (replacerArg.equals("Clock")) {
			this.replacePolicy = Policy.CLOCK;
		} else {
			throw new ReplacerException(null, "REPLACER_NOT_FOUND");
		}
		if (numbufs <= 0) {
			throw new InvalidFrameSizeException(null, "INVALID_FRAME_SIZE");
		} else {
			this.bufferSize = numbufs;
		}
		bufPool = new byte[numbufs][GlobalConst.MINIBASE_PAGESIZE];
		replacement = new Clock();
		bufDescr = new FrameDescriptor[numbufs];
		for (int i = 0; i < numbufs; i++) {
			replacement.addFrame(i);
			bufDescr[i] = new FrameDescriptor();
		}
		hashTbl = new BufferHashTable(43);

	}

	/**
	 * Pin a page. First check if this page is already in the buffer pool. If it
	 * is, increment the pin_count and return a pointer to this page. If the
	 * pin_count was 0 before the call, the page was a replacement candidate,
	 * but is no longer a candidate. If the page is not in the pool, choose a
	 * frame (from the set of replacement candidates) to hold this page, read
	 * the page (using the appropriate method from {\em diskmgr} package) and
	 * pin it. Also, must write out the old page in chosen frame if it is dirty
	 * before reading new page. (You can assume that emptyPage==false for this
	 * assignment.)
	 *
	 * @param Page_Id_in_a_DB
	 *            page number in the minibase.
	 * @param page
	 *            the pointer poit to the page.
	 * @param emptyPage
	 *            true (empty page); false (non-empty page)
	 */
	public void pinPage(PageId pin_pgid, Page page, boolean emptyPage)
			throws BufferPoolExceededException, Exception {
		int frameLocation = hashTbl.getFrameNumber(pin_pgid);
		if (frameLocation == -1) {
			int candidateFrame = replacement.findReplacement();
			if (candidateFrame == -1) {
				throw new BufferPoolExceededException(null,
						"BUFFER_POOL_EXCEEDED");
			}
			PageId oldPageId = new PageId();
			FrameDescriptor oldFrameDesc = bufDescr[candidateFrame];
			oldPageId.pid = oldFrameDesc.getPageNumber();
			if (bufDescr[candidateFrame].getDirtyBit()) {
				// if the dirty bit is set then flush the page
				flushPage(oldPageId);
			}
			Page temp = new Page();
			try {
				SystemDefs.JavabaseDB.read_page(pin_pgid, temp);
			} catch (Exception e) {
				throw new DiskMgrException(e, "DB.java: read_page() failed");
			}
			if (!oldFrameDesc.isEmpty())
				hashTbl.removePageNumber(oldPageId);
			PageId pageId = new PageId(pin_pgid.pid);
			bufDescr[candidateFrame] = new FrameDescriptor(pageId.pid, 1, false);
			byte[] data = temp.getpage();
			for (int i = 0; i < GlobalConst.MINIBASE_PAGESIZE; i++) {
				bufPool[candidateFrame][i] = data[i];
			}
			hashTbl.addPageNumber(pin_pgid, candidateFrame);
			page.setpage(bufPool[candidateFrame]);
		} else {
			int count = bufDescr[frameLocation].getPinCount();
			if (count == 0)
				replacement.removeCandidate(frameLocation);
			bufDescr[frameLocation].setPinCount(count + 1);
			page.setpage(bufPool[frameLocation]);
		}

	}

	/**
	 * Unpin a page specified by a pageId. This method should be called with
	 * dirty==true if the client has modified the page. If so, this call should
	 * set the dirty bit for this frame. Further, if pin_count&gt;0, this method
	 * should decrement it. If pin_count=0 before this call, throw an exception
	 * to report error. (For testing purposes, we ask you to throw an exception
	 * named PageUnpinnedException in case of error.)
	 *
	 * @param globalPageId_in_a_DB
	 *            page number in the minibase.
	 * @param dirty
	 *            the dirty bit of the frame
	 */
	public void unpinPage(PageId PageId_in_a_DB, boolean dirty)
			throws PageUnpinnedException, HashEntryNotFoundException {
		int frameLocation = hashTbl.getFrameNumber(PageId_in_a_DB);
		if (frameLocation == -1) {
			throw new HashEntryNotFoundException(null,
					"BUFMGR:HASH_ENTRY_NOT_FOUND");
		} else {
			int count = bufDescr[frameLocation].getPinCount();
			if (count == 0) {
				throw new PageUnpinnedException(null, "BUFMGR:PAGE_NOT_PINNED");
			} else {
				bufDescr[frameLocation].setPinCount(count - 1);
				if (dirty) {
					bufDescr[frameLocation].setDirtyBit(true);
				}
				if (bufDescr[frameLocation].getPinCount() == 0) {
					replacement.addFrame(frameLocation);
				}
			}
		}
	}

	/**
	 * Unpin a page specified by a pageId. This method should be called with
	 * dirty==true if the client has modified the page. If so, this call should
	 * set the dirty bit for this frame. Further, if pin_count&gt;0, this method
	 * should decrement it. If pin_count=0 before this call, throw an exception
	 * to report error. (For testing purposes, we ask you to throw an exception
	 * named PageUnpinnedException in case of error.)
	 *
	 * @param firstpage
	 *            the address of the first page.
	 * @param howmany
	 *            total number of allocated new pages.
	 * @return the first page id of the new pages.
	 * @throws DiskMgrException
	 */
	public PageId newPage(Page firstpage, int howmany)
			throws PageAllocationException, DiskMgrException {

		Page tpage = new Page();
		PageId tpageid = new PageId();
		try {
			SystemDefs.JavabaseDB.allocate_page(tpageid, howmany);
			pinPage(tpageid, tpage, false);
		} catch (Exception e) {
			try {
				SystemDefs.JavabaseDB.deallocate_page(tpageid, howmany);
			} catch (Exception e1) {
				throw new DiskMgrException(e1,
						"DB.java: deallocate_page() failed");
			}
			tpageid = null;
		}
		firstpage.setpage(tpage.getpage());
		return tpageid;
	}

	/**
	 * This method should be called to delete a page that is on disk. This
	 * routine must call the method in diskmgr package to deallocate the page.
	 *
	 * @param globalPageId
	 *            the page number in the data base.
	 * @throws DiskMgrException
	 * @throws BufMgrException
	 */
	public void freePage(PageId globalPageId) throws PagePinnedException,
			DiskMgrException, BufMgrException {

		int frameLocation = hashTbl.getFrameNumber(globalPageId);
		boolean entered = false;
		if (frameLocation != -1 && bufDescr[frameLocation].getPinCount() == 1) {
			try {
				unpinPage(globalPageId, false);
				SystemDefs.JavabaseDB.deallocate_page(globalPageId);
				entered = true;
			} catch (Exception e) {
				throw new BufMgrException(e, "UnpinPage Failed()");
			}
		}
		if (frameLocation != -1 && bufDescr[frameLocation].getPinCount() > 1) {
			throw new PagePinnedException(null,
					"BUFMGR: CANNOT DEALLOCATE A PINNED PAGE");
		}
		if (!entered) {
			Page tpage = new Page();
			try {
				pinPage(globalPageId, tpage, false);
				SystemDefs.JavabaseDB.deallocate_page(globalPageId);
				unpinPage(globalPageId, false);
				frameLocation = hashTbl.getFrameNumber(globalPageId);
				bufDescr[frameLocation] = new FrameDescriptor();
				hashTbl.removePageNumber(globalPageId);
			} catch (Exception e) {
				throw new DiskMgrException(e,
						"DB.java: deallocate_page() failed");
			}
		}
	}

	/**
	 * Used to flush a particular page of the buffer pool to disk. This method
	 * calls the write_page method of the diskmgr package.
	 * 
	 * @param pageid
	 *            the page number in the database.
	 * @throws DiskMgrException
	 */
	public void flushPage(PageId pageid) throws HashEntryNotFoundException,
			DiskMgrException {
		int frameLocation = hashTbl.getFrameNumber(pageid);
		if (frameLocation == -1) {
			throw new HashEntryNotFoundException(null,
					"BUFMGR:HASH_ENTRY_NOT_FOUND");
		} else {
			Page tempPage = new Page(bufPool[frameLocation]);
			try {
				SystemDefs.JavabaseDB.write_page(pageid, tempPage);
				bufDescr[frameLocation].setDirtyBit(false);
			} catch (Exception e) {
				throw new DiskMgrException(e, "DB.java: write_page() failed");
			}
		}
	}

	/**
	 * Flushes all pages of the buffer pool to disk
	 * 
	 * @throws DiskMgrException
	 */
	public void flushAllPages() throws DiskMgrException {
		LinkedList<PageId> pids = hashTbl.getAllFrames();
		try {
			for (int i = 0; i < pids.size(); i++) {
				flushPage(pids.get(i));
			}
		} catch (Exception e) {
			throw new DiskMgrException(e, "BUFMGR: flush_page() failed");
		}
	}

	/**
	 * Gets the total number of buffers.
	 *
	 * @return total number of buffer frames.
	 */
	public int getNumBuffers() {
		return bufferSize;
	}

	/**
	 * Gets the total number of unpinned buffer frames.
	 *
	 * @return total number of unpinned buffer frames.
	 */
	public int getNumUnpinnedBuffers() {
		return (replacement.size());
	}
}
