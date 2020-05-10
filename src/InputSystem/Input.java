package InputSystem;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Input {
	public static final int EOF = 0;
	
	private final int MAXLOOK = 16; //Lookahead at most 16 bytes
	private final int MAXLEX  = 1024; //Largest token size
	private final int BUFSIZE = (MAXLEX * 3) + (2*MAXLOOK);
	private int End_buf = BUFSIZE;   
	private int DANGER = (End_buf - MAXLOOK);
	private final byte[] Start_buf = new byte[BUFSIZE];
	private final int END = BUFSIZE;
	private int Next = END;
	private int sMark = END;
	private int eMark = END;
	private int pMark = END;
	private int pLineNo = 0;
	private int pLength = 0;
	
	private FileHandler fileHandler = null;
	
	private int LineNo = 1;
	private int Mline = 1;
	
	private boolean Eof_read = false;
	
	private boolean noMoreChars() {
		//Are there more bytes to compile?
		//IF it is the EOF, then require the next pointer
		//to reach beyond the end of the buffer
		return (Eof_read && Next >= End_buf);
	}
	
	private FileHandler getFileHandler(String fileName) {
		if (fileName != null) {
			return new DiskFileHandler(fileName);
		}
		else {
			return new StdInHandler();
		}
	}
	
	
	//Reset from a file
	public void ii_newFile(String fileName) {
		if(fileHandler!=null) {
			fileHandler.Close();
		}
		fileHandler = getFileHandler(fileName);
		fileHandler.Open();
		
		this.Eof_read = false;
		
		Eof_read = false;
		Next     = END;
		pMark    = END;
		sMark    = END;
		eMark    = END;
		End_buf  = END;
		LineNo   = 1;
		Mline    = 1;
	}
	
	//Get data
	public String ii_text() {
		byte[] str = Arrays.copyOfRange(Start_buf, sMark, sMark + ii_length());
		return new String(str, StandardCharsets.UTF_8);
	}
	
	//Current length of data to compile
	public int ii_length() {
		return eMark - sMark;
	}
	
	//Get line number
	public int ii_lineno() {
		return LineNo;
	}
	
	//Get previous 
	public String ii_ptext() {
		byte[] str = Arrays.copyOfRange(Start_buf, pMark, pMark+pLength);
		return new String(str, StandardCharsets.UTF_8);
	}
	
	public int ii_pLength() {
		return pLength;
	}
	
	public int ii_plineNo() {
		return pLineNo;
	}
		
	public int ii_mark_start() {
		Mline = LineNo;
		eMark = sMark = Next;
		return sMark;
	}
	
	public int ii_mark_end() {
		Mline = LineNo;
		eMark = Next;
		return eMark;
	}
	
	public int ii_move_start() {
		if(sMark >= eMark) {
			return -1;
		}
		else {
			sMark++;
			return sMark;
		}
	}
	
	//Inverse of ii_mark_end
	public int ii_to_mark() {
		LineNo = Mline;
		Next = eMark;
		return Next;
	}
	
	public int ii_mark_prev() {
		pMark = sMark;
		pLineNo = LineNo;
		pLength = eMark - sMark;
		return pMark;
	}
	
	public byte ii_advance() {
		
		if (noMoreChars()) {
			//Means we are done, no more to parse
			return 0;
		}
		
		if (Eof_read == false && ii_flush(false) < 0) {
			return -1;
		}
		
		if (Start_buf[Next] == '\n') {
			LineNo++;
		}
		
		return Start_buf[Next++];
	}

	public static int NO_MORE_CHARS_TO_READ = 0;
	public static int FLUSH_OK = 1;
	public static int FLUSH_FAIL = -1;
	
	public int ii_flush(boolean force) {
		System.out.println("flush!");
		int copy_amt, shift_amt, left_edge;
		if(noMoreChars()) {
			return NO_MORE_CHARS_TO_READ;
		}
		
		if(Eof_read) {
			//No more to flush!
			return FLUSH_OK;
		}
		
		if (Next > DANGER || force) {
			left_edge = (pMark < sMark? pMark: sMark);
			shift_amt = left_edge;
			
			//Flush not possible
			//In case the next lex is greater than MAXLEX ...
			if (shift_amt < MAXLEX) {
				if(!force) {
					return FLUSH_FAIL;
				}
				
				//Reset the whole buffer then
				left_edge = ii_mark_start();
				ii_mark_prev();
				shift_amt = left_edge;
			}
			
			copy_amt = End_buf - left_edge;
			//System.arraycopy(Start_buf, 0, Start_buf, left_edge, copy_amt);
			System.arraycopy(Start_buf, left_edge, Start_buf, 0, copy_amt);
			
			if(ii_fillbuf(copy_amt) == 0) {
				System.err.println("Internal error, ii_flush: Buffer full,"
						+ "can't read");
			}
			
			//Shift pointers
			if(pMark != 0) {
				pMark -= shift_amt;
			}
			sMark -= shift_amt;
			eMark -= shift_amt;
			Next -= shift_amt;
		}
		
		return FLUSH_OK;
	}

	private int ii_fillbuf(int starting_at) {
		int need;
		int got = 0;
		need = ((END - starting_at)/MAXLEX)*MAXLEX;
		if (need < 0) {
			System.err.println("Internal Error (ii_fillbuf): Bad read-request starting addr.");
		}
		if (need == 0) {
			return 0;
		}
		if ((got = fileHandler.Read(Start_buf, starting_at, need)) == -1) {
			System.err.println("Can't read input file");
		}
		End_buf = starting_at + got;
		if (got < need) {
			Eof_read = true;
		}
		
		return got;
	}

	
	//Inverse function of ii_advance
	public boolean ii_pushback(int n) {
		while(--n >= 0 && Next > sMark) {
			--Next;
			if(Start_buf[Next] == '\n' || Start_buf[Next] == '\0') {
				--LineNo;
			}
		}
		
		if(Next < eMark) {
			eMark = Next;
			Mline = LineNo;
		}
		
		
		return (Next > sMark);
	}
	
	public byte ii_lookahead(int n) {
		
		if(Eof_read && Next + n - 1 >= End_buf) {
			return EOF;
		}
		byte p = Start_buf[Next + n - 1];
		return (Next + n - 1 < 0 || Next + n - 1 >= End_buf) ? 0 : p;
	}
	
}
