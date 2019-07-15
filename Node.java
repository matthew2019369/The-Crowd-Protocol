package functions;

import java.util.*;
import java.io.IOException;
import java.math.*;

/*
 *  
 * For Distinction-level tasks only
 * */
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Node {
	Integer nodeID;
	Integer expF;
	Integer expG;
	Integer divisor;
	Boolean isEncrypted;
	Boolean isBIUsed;
	Map<Integer,Node> nodeMap;
	MessageTrackCheck messageTrackCheck;
	String messageRetrieved =""; 
	boolean transmittedMSG;
	
	//this is for distinction part 1
	boolean isCorrupted;
	Stack<Integer> lastSenders;
	
	//this is for distinction part 2
	KeyPair keyPair;
	SecureRandom secureRandom;
	Cipher cipher;
	
	public Node(Integer n, Integer e, Integer d, Integer K, Boolean encrypt, Boolean useBI, Map<Integer,Node> m, MessageTrackCheck t) {
		// CONSTRUCTOR: 
		//      n is node ID,
		//      e is the exponent for the function f()
		//      d is the exponent for the function g()
		//      K is the divisor in f() and g()
		//      encrypt is true if messages are encrypted, false otherwise
		//      useBI is true if BigInteger should be used for NodeTransitionFunction, false otherwise
		//      m is a non-null map of node IDs to node objects
		//      t is an instance of MessageTrackCheck

		// TODO
		nodeID = n;
		expF = e;
		expG = d;
		divisor = K;
		isEncrypted = encrypt;
		isBIUsed = useBI;
		nodeMap = m;
		messageTrackCheck = t;
		transmittedMSG = false;
		
		//distinction part 1
		isCorrupted = false;
		lastSenders = new Stack<Integer>();
		
		//distinction part 2
		if(isEncrypted) {
			try {
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
				KeyPairGenerator generator = KeyPairGenerator.getInstance("ELGamal", "BC");
				secureRandom =new SecureRandom();
				generator.initialize(1024, secureRandom);
				keyPair = generator.generateKeyPair();
				cipher = Cipher.getInstance("ElGamal/None/NoPadding", "BC");
				
			} catch(Exception exception1) {
				System.out.println(exception1);
			}
		}
	}
	
	public Boolean hasMsgEncryption() {
		// PRE: -
		// POST: Returns true if messages are encrypted, false otherwise

		// TODO
		
		return isEncrypted;
	}
	
	public Boolean isDestinationNode(String msg) {
		// PRE: msg is an augmented message (i.e. containing 3 characters at the end indicating destination node)
		// POST: Returns true if this is the destination node, false otherwise
		//       E.g. For node 6, will return true for "hello006"

		// TODO
		
		return nodeID==Integer.parseInt(msg.substring(msg.length()-3));
	}


	public Boolean transmittedMessage() {
		// PRE: -
		// POST: Return true if this node has transmitted a message, false otherwise

		// TODO
		
		return transmittedMSG;
	}

	public void sendMsgToNode(Node n, String msg, Integer r, NodeTransitionFunction f) {
		// PRE: n is a non-null node,
		//      msg is a message,
		//      r is the current value of r from the forward transition function.
		//      f is the forward transition function
		// POST: invokes receiveMsgFromNode on node n

		// TODO
		
		//this is for distinction part 1
		if(n.isCorrupted) {
			
			n.lastSenders.add(nodeID);
		}
//		if (f==null)f =createForwardNodeTransitionFunction();
//		System.out.println("sendMSG to: node"+n.nodeID+" msg:"+msg+" r: "+r+" f: "+f.exp+" "+f.KVal);
		receiveMsgFromNode(msg, n.nodeID, r,  f);
		
	}
	
	public void sendMsgToNode(Node n, String msg, BigInteger r, NodeTransitionFunction f) {
		// PRE: n is a non-null node,
		//      msg is a message,
		//      r is the current value of r from the forward transition function.
		//      f is the forward transition function
		// POST: invokes receiveMsgFromNode on node n

		// TODO
		
		//this is for distinction part 1
		if(n.isCorrupted) {
			
			n.lastSenders.add(nodeID);
		}
//		if (f==null)f =createForwardNodeTransitionFunction();
		receiveMsgFromNode(msg, n.nodeID, r,  f);
		
		
	}
	
	public void receiveMsgFromNode(String msg, Integer id, Integer r, NodeTransitionFunction f) {
		// PRE: msg is a message, 
		//      id is the ID of the sending node,
		//      r is the current value of r from the forward transition function,
		//      f is the forward transition function
		// POST: If this is the destination node, stop;
		//       otherwise, send the message onwards.
		//       Add id to local MessageTrackCheck

		// TODO
		messageTrackCheck.add(id);
//		System.out.println("ch: "+messageTrackCheck.check());
		if(nodeMap.get(id).isDestinationNode(msg)) {
			nodeMap.get(id).messageRetrieved=msg;
			return;
		}
		int nextR =f.apply(r);
		
		sendMsgToNode(nodeMap.get(nextR%nodeMap.size()), msg, nextR, f);
		nodeMap.get(id).transmittedMSG=true;
//		System.out.println(nodeMap.get(id).transmittedMSG);

		
	}

	public void receiveMsgFromNode(String msg, Integer id, BigInteger r, NodeTransitionFunction f) {
		// PRE: msg is a message, 
		//      id is the ID of the sending node,
		//      r is the current value of r from the forward transition function,
		//      f is the forward transition function
		// POST: If this is the destination node, stop;
		//       otherwise, send the message onwards.
		//       Add id to local MessageTrackCheck

		// TODO
		
		messageTrackCheck.add(id);
//		System.out.println("ch: "+messageTrackCheck.check());
		if(nodeMap.get(id).isDestinationNode(msg)) {
			nodeMap.get(id).messageRetrieved=msg;
			return;
		}
		BigInteger nextR =f.apply(r);
		
		sendMsgToNode(nodeMap.get(nextR.mod(new BigInteger(String.valueOf(nodeMap.size()))).intValue()), msg, nextR, f);
		nodeMap.get(id).transmittedMSG=true;
//		System.out.println(nodeMap.get(id).transmittedMSG);
	}
	
	public String getMsg() {
		// PRE: -
		// POST: Returns the current received (non-augmented) message, null if no received message

		// TODO
		if(!messageRetrieved.equals((""))) {
			return messageRetrieved.substring(0,messageRetrieved.length()-3);
		}
		return null;
	}

	/*
	*/
	

	/*
	 * Initiator 
	 */
	
	public NodeTransitionFunction createForwardNodeTransitionFunction() {
		// PRE: -
		// POST: Creates a NodeTransitionFunction using this node's public function f() with parameters e, K
		
		// TODO

		return new NodeTransitionFunction(expF, divisor);
	}
	
	public String addDestIDToMsg(String msg, Integer v) {
		// PRE: msg is a message, v is a node ID
		// POST: Returns a string that concatenates v as a 3-character string to the end of msg.
		//       E.g. for msg="hello", v=6, returns "hello006"
		
		// TODO
		String result = String.valueOf(v);
		while(result.length()<3) {
			result="0"+result;
		}
		return msg+result;
	}

	public Integer firstRForInitiatingMessage(Integer k, Integer v) {
		// PRE: v is destination node ID, k is number of steps
		// POST: Uses the trapdoor function inverse, applied to destination node v with number of steps k, to calculate the node path;
		//       returns value of r that determines first step on node path

		// TODO
		
		Integer r = v;
		NodeTransitionFunction inverseFunction = new NodeTransitionFunction(expG, divisor);
		System.out.println("starting node: "+r);
		for(int i = 1;i<k;i++) {
			r = inverseFunction.apply(r);
			
		}
		

		return r;
	}

	public BigInteger firstRForInitiatingMessage(Integer k, BigInteger v) {
		// PRE: v is destination node ID, k is number of steps as a BigInteger
		// POST: Uses the trapdoor function inverse, applied to destination node v with number of steps k, to calculate the node path;
		//       returns value of r that determines first step on node path

		// TODO
		
		BigInteger r = v;
		System.out.println("starting node: "+r);
		NodeTransitionFunction inverseFunction = new NodeTransitionFunction(expG, divisor);
		for(int i=1;i<k;i++) {
			r = inverseFunction.apply(r);
			System.out.println("trial "+i+" : r= "+r+" node: "+r.mod(new BigInteger(String.valueOf(20))).intValue());
		}
		return r;
	}
	

	public void initiateMessage(String msg, Integer k, Integer v) {
		// PRE: msg is an original message, v is destination node ID, k is number of steps
		// POST: Adds destination ID to msg; 
		//       sends augmented msg to the next node, as determined by firstRForInitiatingMessage(k, v), 
		//       along with forward transition function
		
		// TODO
		this.messageTrackCheck.add(this.nodeID);
		
		String augmentedMSG = addDestIDToMsg(msg,v);
		if(isBIUsed) {
			BigInteger nextNode = firstRForInitiatingMessage(k, new BigInteger(String.valueOf(v)));
//			System.out.println("initial ch is : "+this.messageTrackCheck.check());
			Integer id = nextNode.mod(new BigInteger(String.valueOf(20))).intValue();
			sendMsgToNode(nodeMap.get(id),  augmentedMSG, nextNode, createForwardNodeTransitionFunction());
		} else {
			Integer nextNode = firstRForInitiatingMessage(k, v);
//			System.out.println("initial ch is : "+this.messageTrackCheck.check());
			sendMsgToNode(nodeMap.get(nextNode%nodeMap.size()), augmentedMSG, nextNode, createForwardNodeTransitionFunction());
		}
	}
	
	public Integer getID() {
		// PRE: -
		// POST: Returns node ID

		// TODO
		
		return nodeID;
	}
	
	public Integer getE() {
		// PRE: -
		// POST: Returns value of e in this node's function f()
		
		// TODO

		return expF;
	}
	
	public Integer getK() {
		// PRE: -
		// POST: Returns value of K in this node's function f()
		
		// TODO

		return divisor;
	}
	

	// DISTINCTION: guess initiator
	
	public Integer guessInitiator() {
		// PRE: -
		// POST: Guesses a node to be the initiator if it can track back through corrupted nodes
		//       along two separate paths
		//      returns this node ID, or -1 if no guess

		// TODO
		int[] counter = new int[nodeMap.size()];
		
		Stack<Integer> lastSenders2 = (Stack<Integer>) this.lastSenders.clone();
		while(!lastSenders2.isEmpty()) {
			Integer n = lastSenders2.pop();
			ArrayList<Integer> tmp = DFS(n, new ArrayList<Integer>(),new boolean[nodeMap.size()]);
			for(int i=0;i<tmp.size();i++) {
				counter[tmp.get(i)]++;
			}
		}
		for(int i=0;i<counter.length;i++) {
			if(counter[i]>=2) {
				return i;
			}
		}

		return -1;
	}
	
	public ArrayList<Integer> DFS(Integer v, ArrayList<Integer> arr,boolean[] visited) {
		if(!nodeMap.get(v).isCorrupted) {
			arr.add(v);
		}
		visited[v]=true;
		Stack<Integer> lastSenderStack = (Stack<Integer>) nodeMap.get(v).lastSenders.clone();
		while(!lastSenderStack.isEmpty()) {
			Integer n = lastSenderStack.pop();
			if(!visited[n]) {
				arr=DFS(n, arr,visited);
			}
		}
		
		return arr;
	}

	public void setCorrupt() {
		// PRE: -
		// POST: Sets a node to be corrupt
		
		// TODO
		isCorrupted =true;
	}
	
	public Integer lastSender() {
		// PRE: -
		// POST: If a node is not corrupt, returns -1;
		//       if a node is corrupt, returns ID of node that last sent it a message,
		//       -1 if it has not been sent any messages

		// TODO
		if(isCorrupted) {
//			NodeTransitionFunction inverseFunction = new NodeTransitionFunction(expG, divisor);
//			if(!isBIUsed) {
//				Integer r = inverseFunction.apply(this.nodeID);
//				return r%nodeMap.size();
//			} else {
//				BigInteger r = inverseFunction.apply(new BigInteger(String.valueOf(this.nodeID)));
//				return r.mod(new BigInteger(String.valueOf(20))).intValue();
//			}
			if(this.lastSenders.isEmpty()) return -1;
			return this.lastSenders.peek();
		}
			
		return -1;
	}
	

	// DISTINCTION: encryption
	
	/**/
	public String basicHashFunction (String m) {
		// PRE: -
		// POST: Sums the numeric value of each character, using Character.getNumericValue(), 
		//       takes mod 100 of the total; returns as a 3-char string
		
		// TODO
		int sum = 0;
		for(int i = 0;i<m.length();i++) {
			sum+=Character.getNumericValue(m.charAt(i));
		}
		sum =sum%100;
		return '0'+Integer.toString(sum);		
	}
	
	public Key getPublicKey() {
		// PRE:
		// POST: Returns the node's public key (null if hasMsgEncryption() is false)
		
		// TODO
		if(!this.hasMsgEncryption()) {
			return null;
		}
		
		return keyPair.getPublic();
	}

	public byte[] encryptedMsg(String msg, Key chosenPubKey) {
		// PRE: msg is a message, chosenPubKey is a public key
		// POST: Returns msg encrypted with chosenPubKey (null if hasMsgEncryption() is false or chosenPubKey is null)
		
		// TODO
		if(!hasMsgEncryption()||chosenPubKey==null) {
			return null;
		}
		try {
			
			cipher.init(Cipher.ENCRYPT_MODE, chosenPubKey, secureRandom);
			return cipher.doFinal(msg.getBytes());
		} catch (Exception e1) {
			System.out.println(e1);
		}
		
		return null;
	}
	
	public byte[] decryptedMsg(byte[] msg) {
		// PRE: msg is an encrypted message as a byte array
		// POST: Returns msg decrypted using node's private key (null if hasMsgEncryption() is false)

		// TODO
		if(!hasMsgEncryption()) {
			return null;
		}
		
		try {
			cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
			return cipher.doFinal(msg);
		} catch (Exception e){
			System.out.println(e);
		} 
		return null;
	}

	public String addCheckToMsg(String msg) {
		// PRE: msg is a message
		// POST: Returns a string that concatenates the basicHashFunction of msg
		//       E.g. for msg="hello", v=6, returns "hello006"
		
		// TODO

		return msg+basicHashFunction(msg);
	}


	public Boolean isDestinationNode(byte[] msg) {
		// PRE: msg is an augmented encrypted message (i.e. containing 3 check digits at the end)
		// POST: Returns true if this is the destination node, false otherwise
		//       Determines if this is the destination by decrypting msg, 
		//       and comparing the hashed decrypted core msg (i.e. up to the last 3 characters) 
		//       against the last 3 chars of the decrypted msg 
		
		// TODO
		byte[] decryptedMSG = decryptedMsg(msg);
		String str = new String(decryptedMSG);
		System.out.println(str);
		String last3Digits = str.substring(str.length()-3);
		String message = str.substring(0,str.length()-3);
		return basicHashFunction(message).equals(last3Digits);
	}
	 

	public static void main(String[] args) {
		Node my = new Node(0, 0, 0, 0, false, false, null, null);
		System.out.println(my.basicHashFunction("hello"));
		
		
	}

}
