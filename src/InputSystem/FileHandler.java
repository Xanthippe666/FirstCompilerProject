package InputSystem;

public interface FileHandler {
	public void Open();
	
	public int Close();
	
	public int Read(byte[] buf, int begin, int len);
}
