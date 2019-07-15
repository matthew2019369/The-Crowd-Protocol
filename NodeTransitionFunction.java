package functions;

import java.math.*;  // for BigInteger

public class NodeTransitionFunction {
	Integer exp;
	Integer KVal;
	public NodeTransitionFunction(Integer exp, Integer KVal) {
		// CONSTUCTOR: Sets the class to calculate f(x) = (x ^ exp) mod KVal 

		// TODO
		this.exp = exp;
		this.KVal = KVal;
	}
	

	
	
	public Integer apply(Integer val) {
		// PRE: -
		// POST: Implements f(val)
		
		// TODO
		Integer result= 1;
		for(int i=1;i<=exp;i++) {
			result= ((result%KVal)*(val%KVal))%KVal;
		}
		return result;
	}

	public BigInteger apply(BigInteger val) {
		// PRE: -
		// POST: Implements f(val), with val as a BigInteger

		// TODO
		
		return val.modPow(new BigInteger(String.valueOf(exp)), new BigInteger(String.valueOf(KVal)));
	}


	
	public static void main(String[] args) {
		NodeTransitionFunction f = new NodeTransitionFunction(3, 33);
		
		System.out.println(f.apply(5));
	}
	
}
