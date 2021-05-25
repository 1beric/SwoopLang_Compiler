use standard.data_structures;
/* This is a comment */
public class App {

	private static final str DB_USER = "systems.application";
	private static final str DB_PASS = "90287309874520";

	private int loopCount = 0;
	private double accuracy = 1;
	protected double fireRate = .5;

	public void main() {
		
		int int1 = 102;
		char char_ = '!', esc = '\'';
		str stringa = "\"Hello world!";
		str stringb = new String('a');
		str stringc = new String(1.2);
		
		System.out.println(stringa, char_, -(1233)).pow();
		++int1;
		
		System.out.println(stringa + char_ + "\"");
		
		if (int1 > 101 && int1< 103 || int1 >= 102) {
		
		} else {
		
		}
		
		while (int1 > 0)
			int1--;

		for (int i = 100; i; i--)
			System.out.println(i);
				
		for (char ch : stringa)
			System.out.println(ch);
			
		return char_;
	}
}