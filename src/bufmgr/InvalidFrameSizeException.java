package bufmgr;

import chainexception.ChainException;

public class InvalidFrameSizeException extends ChainException {
	public InvalidFrameSizeException(Exception e, String name){
		super(e,name);
	}
}
