
package com.vg.http;


/**
 * 
 * @author luopeng (luopeng@staff.sina.com.cn)
 */
public class VGException extends Exception {

	private static final long serialVersionUID = 475022994858770424L;
	private int statusCode = -1;
	
    public VGException(String msg) {
        super(msg);
    }

    public VGException(Exception cause) {
        super(cause);
    }

    public VGException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public VGException(String msg, Exception cause) {
        super(msg, cause);
    }

    public VGException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
    
    
	public VGException() {
		super(); 
	}

	public VGException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public VGException(Throwable throwable) {
		super(throwable);
	}

	public VGException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
