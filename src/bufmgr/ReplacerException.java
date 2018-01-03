package bufmgr;

import chainexception.ChainException;

public class ReplacerException extends ChainException{

	private static final long serialVersionUID = 1L;

	public ReplacerException(Exception e, String name){
		super(e,name);
	}

}
