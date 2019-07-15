# The-Crowd-Protocol
The ability to engage in anonymous communication over networks is important.1 There are a number of approaches to doing this. One class of approaches is related to onion routing, where messages encapsulated in layers of encryption are transmitted through a series of network nodes (onion routers), each of which “peels away” a single layer to reveal the data’s next destination; Tor is an instance of this [Dingledine et al., 2004]. This project is to implement a simplified version of some of the functionality of the deterministic crowds protocols.

# Network Nodes
For the purposes of sending a message, there are three kinds of nodes: the initiator, intermediate forwarding nodes, and the receiver. The N nodes of a network will be identified by integers 0 . . . N − 1.

Initiator We will call the initiating node v0. There will be a function f : N → N, taking a non-negative integer and producing non-negative integer, that a node will use to decide which node it will send a message to. (Notationally, f −1 will be the inverse of this function.)

To transmit a message m to a receiver v, v0 first chooses a path length n and calls vn := v the receiver. It then goes on to choose a value rn ≡ vn mod N, and backward iterates rn−1 = f −1(rn) until it reaches a value r1 . The first node to transmit the payload to (where the payload consists of the message m, value r and function f) is thus
v1 = r1 mod N, and the initiator acts just as if it would have if it had computed r1 from the data being sent from elsewhere to v0.

Question: If there is such a function f−1 , how can the protocol prevent any node from tracking the message back? The solution is for f to be a trapdoor function, one whose inverse can only (easily) be calculated by using a trapdoor secret. §2.2 explains these and gives an example of how it is applied here. Forwarding Node Forwarding node vi , upon receiving the message m, along with a value ri and a function f, first checks if it is the designated receiver. As it is not, it computes ri+1 = f(ri) and thence the next hop as vi+1 = ri+1 mod N, and sends the message m together with ri+1 and f to vi+1. Receiver Node vn similarly checks if it is the designated receiver. As it is, the process stops.

## Trapdoor Functions
An Example of a Trapdoor Function Trapdoor functions are used widely in cryptography. The kind of function we’ll be using comes from RSA encryption.5 There are three values that we’ll be using to define our function f and its inverse f−1; we’ll call these e, d and K. e and K will be used to define f, which will be public; d will be the trapdoor secret.
Our public function will be f(x) = x^e mod K (1) and our inverting function will be
g(x) = x^d mod K. 
There is a process (in cryptography, a key generation algorithm) to find values of e, d, K such that f(g(x)) = x, i.e. g(x) = f−1(x).
Consider e = 3, d = 7, K = 33. Then, f(30) = 303 mod 33 = 6. To compute the inverse, f
−1(6) = g(6) =6^7 mod 33 = 30. This inverse can only (easily) be computed if d is known.
Applying a Trapdoor to Deterministic Crowds Consider a network (crowd) with nodes 0 . . . 19. Let the message initiator be node v0 = 1. v0 decides on a path length of n = 3 (i.e. via two intermediate forwarding nodes) to send its message to receiver node v = v3 = 6. It makes the following calculations:
1. r3 = 6.
2. r2 = f−1(6) = 67 mod 33 = 30.
This corresponds to node v2 = r2 mod N = 30 mod 20 = 10.
3. r1 = f−1(30) = 307 mod 33 = 24.
This corresponds to node v1 = r1 mod N = 24 mod 20 = 4.
So the node that v0 forwards the message to, along with the value r1 = 24 and function f (but not f−1 or the value d that would allow f−1 to be calculated), is node 4.
The full path will be 1 → 4 → 10 → 6.

## Code Structure

• NodeTransitionFunction: This instantiates the functions f(·) and g(·) from Equations (1) and (2) respectively.

• Node: This represents a node in the network; it is where the core functionality of the assignment is. Nodes receive messages, determine the next one to forward them to, and carry out the forwarding, among other functions. A node will have an integer ID 0 . . . N − 1, where N is the number of nodes a particular network.

• Network: This represents a network of nodes. The key component of the network is a lookup table that is accessible to all nodes, so that nodes can look up properties of other nodes. The table has the form Map<Integer,Node> lookup; It also contains functions that will be provided for reading network specifications and messages from files.

• MessageTrackCheck: This represents an encoded trail 
