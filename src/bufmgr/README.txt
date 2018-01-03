Project Members
-----------------
Balamurugan Anandan
Jaewoo Lee

This project implements the Buffer Manager Layer without support for concurrency control or recovery. 

List of Files:
--------------
BufHTEntry.java - Implements the HashTable Buckets
BufHashTbl.java - A simple hash table to figure out what frame a given disk page occupies.
Clock.java - Implements the Clock Replacement Policy
BufMgr.java - Defines the main interface to the Buffer Manager Layer.
FrameDescriptor.java  - A descriptor is maintained per frame to record page_number, pin_count and dirtybit.

Exception Classes: 
------------------
BufferPoolExceededException.java
InvalidFrameSizeException.java
PageUnpinnedException.java
PageAllocationException.java
ReplacerException.java
BufMgrException.java
HashEntryNotFoundException.java	
PagePinnedException.java 

How to Compile/Run:
-------------------
*) cd to 'tests' directory
*) Type 'make bmtest' to test the Buffer Manager Layer
