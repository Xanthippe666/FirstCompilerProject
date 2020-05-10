package InputSystem;

import java.io.*;

public class DiskFileHandler implements FileHandler {

	private String input_buffer = "";
	private int curPos = 0;
	//private File f;
	private BufferedReader br;
	
	//Use relative or absolute naming path
	public DiskFileHandler(String name) {
		FileInputStream f;
		try {
			f = new FileInputStream(name);
			br = new BufferedReader(new InputStreamReader(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	@Override
	public void Open() {
		String line;
		try {
			while(true) {
				line = br.readLine();
				if (line.contentEquals("end")) {
					break;
				}
				input_buffer += (line + '\n');
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int Close() {
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
