package InputSystem;

import java.io.UnsupportedEncodingException;

public class InputSystem {
	private Input input = new Input();
	
	public void runStdinExample() {
		input.ii_newFile(".\\src\\InputSystem\\Sample1");
		
		input.ii_mark_start();
		printWord();
		input.ii_mark_end();
		input.ii_mark_prev();
		
		input.ii_mark_start();
		printWord();
		//printWord(Viva La France);
		input.ii_mark_end();
		input.ii_mark_prev();
		
		input.ii_mark_start();
		printWord();
		input.ii_mark_end();
    	
		System.out.println("prev word: " + input.ii_ptext());
		System.out.println("current word: " + input.ii_text());
	}

	private void printWord() {
		byte c;
		while((c = input.ii_advance()) != ' ' && (c != 0)) {
			byte[] buf = new byte[1];
			buf[0] = c;
			try {
				String s = new String(buf, "UTF8");
				System.out.print(s);
			}
			catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		System.out.println("");
	}
	
	public static void main(String[] args) {
		InputSystem is = new InputSystem();
		is.runStdinExample();
	}
}
