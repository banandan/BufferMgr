package bufmgr;

import chainexception.ChainException;

public class BufMgrException extends ChainException {

	private static final long serialVersionUID = 1L;

	public BufMgrException(Exception e, String name) {
		super(e, name);
	}

}
