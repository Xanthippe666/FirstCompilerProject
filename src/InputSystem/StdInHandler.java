package InputSystem;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;


public class StdInHandler implements FileHandler {

	private String input_buffer = "";
	private int curPos = 0;
	Scanner s;
	
	@Override
	public void Open() {
		s = new Scanner(System.in);
		while(true) {
			String line = s.nextLine();
			if(line.contentEquals("end")) {
				break;
			}
			input_buffer += (line + '\n');
		}
		
		//s.close();
	}

	@Override
	public int Close() {
		//s.close();
		return 0;
		
	}

	@Override
	public int Read(byte[] buf, int begin, int len) {
		if(curPos >= input_buffer.length()) {
			return 0;
		}
		
		int readCnt = 0;
		try {
			byte[] inputBuf = input_buffer.getBytes("UTF8");
			while(curPos + readCnt < input_buffer.length() && readCnt < len) {
				buf[begin+readCnt] = inputBuf[curPos + readCnt];
				readCnt++;
			}
		}
		catch(UnsupportedEncodingException e ) {
			e.printStackTrace();
		}
		
		return readCnt;
	}

}
