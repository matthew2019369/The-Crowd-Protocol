package functions;

public class MessageTrackCheck {
	int runningSum;
	
	public MessageTrackCheck(Integer offset) {
		// Constructor, with given offset to initialise the running sum
		runningSum=offset;
		
		// TODO
	}
	
	public void add(Integer n) {
		// PRE: -
		// POST: Adds n to the running sum
		runningSum+=n;
		// TODO
	}
	
	public char check() {
		// PRE: -
		// POST: Returns the character that corresponds to the running sum mod 26;
		//       0..25 correspond to a..z
		
		// TODO
		
		return (char)((runningSum%26)+97);
	}
	
	public void reset(Integer offset) {
		// PRE: -
		// POST: Re-initialises the running sum to the given offset

		// TODO
		runningSum=offset;
		
	}

	public static void main(String[] args) {
		MessageTrackCheck m = new MessageTrackCheck(0);
		
		System.out.println(m.check());
		m.add(24);
		System.out.println(m.check());
	}
	
}
