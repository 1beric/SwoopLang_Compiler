use standard.data_structures;
/* This is a comment */
class test02 : Default {

	private static final str DB_USER = "systems.application";
	private static final str DB_PASS = "90287309874520";

	private int loopCount = 0;
	private double accuracy = 1;
	protected double fireRate = .5;
	
	public test02(str name) {
		/* ERROR for now */
		/* loopCount = name.len(); */
		fireRate = 1 / (accuracy * 10);
	}

	protected str first(char firstLetter = 'a') return new String(firstLetter);

	public void main() {
		
		int int1 = 102;
		char char_ = '!', esc = '\'';
		str stringa = "\"Hello world!";
		str stringb = new String('a');
		stringb = first(char_);
		str stringc = new String(1.2);
		
		
		test02 obj = new test02("my name!");
		
		/* ERROR */
		/* obj.print(); */
	
		obj.first('p');

		/* ERROR for now */
		/* obj.first('p').print(); */
		
		++int1;
/*		System.out.println(stringa, char_, -(1233)).pow();
		
		System.out.println(stringa + char_ + "\"");
		System.out.println(first(char_));
*/		
		if (int1 > 101 && int1< 103 || int1 >= 102) {
		
		} else {
		
		}
		
		while (int1 > 0)
			int1--;

		for (int i = 100; i; i--)
			int1 += i;
		/* System.out.println(i); */
				
		for (char ch : stringa)
			stringb += ch;
		/*	System.out.println(ch); */
			
		return char_;
	}

	
	
	
	
	
	
}