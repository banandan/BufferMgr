# BufferMgr

Buffer Manager is a course project done for CS541 at Purdue.

This project implements a Buffer Manager layer, without support for concurrency control or recovery.

The simplified Buffer Manager interface  allows a client (a higher level program that calls the Buffer Manager) to allocate/de-allocate pages on disk, to bring a disk page into the buffer pool and pin it, and to unpin a page in the buffer pool.

The interfaces supported by BufferMgr are as follows


public class BufMgr {

  /**
   * Create the BufMgr object.
   * Allocate pages (frames) for the buffer pool in main memory and
   * make the buffer manage aware that the replacement policy is
   * specified by replacerArg (i.e. LH, Clock, LRU, MRU etc.).
   *
   * @param numbufs number of buffers in the buffer pool.
   * @param replacerArg name of the buffer replacement policy.
   */

  public BufMgr(int numbufs, String replacerArg) {};

  /** 
   * Pin a page.
   * First check if this page is already in the buffer pool.  
   * If it is, increment the pin_count and return a pointer to this 
   * page.  If the pin_count was 0 before the call, the page was a 
   * replacement candidate, but is no longer a candidate.
   * If the page is not in the pool, choose a frame (from the 
   * set of replacement candidates) to hold this page, read the 
   * page (using the appropriate method from {\em diskmgr} package) and pin it.
   * Also, must write out the old page in chosen frame if it is dirty 
   * before reading new page.  (You can assume that emptyPage==false for
   * this assignment.)
   *
   * @param Page_Id_in_a_DB page number in the minibase.
   * @param page the pointer poit to the page.
   * @param emptyPage true (empty page); false (non-empty page)
   */

  public void pinPage(PageId pin_pgid, Page page, boolean emptyPage) {};

   /**
   * Unpin a page specified by a pageId.
   * This method should be called with dirty==true if the client has
   * modified the page.  If so, this call should set the dirty bit 
   * for this frame.  Further, if pin_count>0, this method should 
   * decrement it. If pin_count=0 before this call, throw an exception
   * to report error.  (For testing purposes, we ask you to throw
   * an exception named PageUnpinnedException in case of error.)
   *
   * @param globalPageId_in_a_DB page number in the minibase.
   * @param dirty the dirty bit of the frame
   */

  public void unpinPage(PageId PageId_in_a_DB, boolean dirty) {};

  /** 
   * Allocate new pages.
   * Call DB object to allocate a run of new pages and 
   * find a frame in the buffer pool for the first page
   * and pin it. (This call allows a client of the Buffer Manager
   * to allocate pages on disk.) If buffer is full, i.e., you 
   * can't find a frame for the first page, ask DB to deallocate 
   * all these pages, and return null.
   *
   * @param firstpage the address of the first page.
   * @param howmany total number of allocated new pages.
   *
   * @return the first page id of the new pages.  null, if error.
   */

  public PageId newPage(Page firstpage, int howmany) {};

  /**
   * This method should be called to delete a page that is on disk.
   * This routine must call the method in diskmgr package to 
   * deallocate the page. 
   *
   * @param globalPageId the page number in the data base.
   */

  public void freePage(PageId globalPageId) {};

  /**
   * Used to flush a particular page of the buffer pool to disk.
   * This method calls the write_page method of the diskmgr package.
   *
   * @param pageid the page number in the database.
   */

  public void flushPage(PageId pageid) {};

};

Dependencies:
----------
./lib/bufmgrAssign.jar

Test:
-----
./src/tests/BMTest.java