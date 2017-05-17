import java.math.BigInteger;
import static java.math.BigInteger.*;
import java.util.Random;

import javax.management.RuntimeErrorException;
/***
 * A proof of concept for Elliptic curves over Fp. with equation
 *  y^2 = x^3 + ax + b (mod prime)
 * 
 * It is only meant to be used with educational purposes
 * to understand how point arithmetic within an EC works,
 * and how could one implements a diffie-hellman key exchange
 * and a digital signature.
 * For real cryptographic applications please use bouncy castle
 * or any other peer-reviewed implementation.
 * @author jbadillo
 *
 */
public class EllipticCurve2 {

	/**
	 * Curve parameters
	 */
	BigInteger a, b;
	BigInteger prime;
	boolean allowsCompression;
	/**
	 * Group parameter - > generator
	 */
	Point G;
	
	static final BigInteger _2 = valueOf(2);
	static final BigInteger _3 = valueOf(3);
	static final BigInteger _4 = valueOf(4);
	static final BigInteger _27 = valueOf(27);
	
	/**
	 * Order of the curve E(Fp)
	 */
	BigInteger  n;
	
	/**
	 * Creates the curve y^2 = x^3 + ax + b (mod prime)
	 * @param a
	 * @param b
	 * @param prime
	 */
	public EllipticCurve2(BigInteger a, BigInteger b, BigInteger prime) {
		this.a = a;
		this.b = b;
		this.prime = prime;
		// discriminant
		// 4*a^3 + 27*b^2 mod prime
		BigInteger d =_4.multiply(a.pow(3).mod(prime)).add(_27.multiply(b.pow(2)).mod(prime)).mod(prime); 
		if(d.equals(ZERO))
			throw new RuntimeErrorException(null, "Wrong parameters");
	}
	
	/**
	 * Sets a generator point, and its order
	 * @param g
	 * @param n
	 */
	public void setGenerator(Point g, BigInteger n){
		this.G = g;
		this.n = n;
		// verify order
		Point p = generate(n);
		// error if its not identity
		if(!p.isInf)
			throw new RuntimeErrorException(null, "Wrong order");
		/*
		this.n = 1;
		while(!g.isInf)
			g = generate(n++ + 1);*/
	}

	public static void main(String[] args) {
		
		
		
	}
	
	/**
	 * @param n
	 * @return x1 such that n^2 = x1 (mod prime)
	 * or null if not existent.
	 * The opposite root should be calculated as
	 * x2 = prime - x1
	 */
	public BigInteger sqrt(BigInteger n){
		if(n.equals(ZERO))
			return ZERO;
		if(n.equals(ONE))
			return ONE;

		BigInteger p_1 = prime.subtract(ONE);
		int s = 0;
		BigInteger q = p_1;
		
		// if doesn't pass euler's criterion
		// n ^ ((p-1)/2) == 1, has sqrt
		if(!n.modPow(p_1.shiftRight(1), prime).equals(ONE))
			return null;
		
		// factor out powers of 2 from p - 1;
		// q % 2 == 0
		while(q.testBit(0) == false)
		{
			q = q.shiftRight(1);
			s++;
		}
		
		// if p = 3 (mod 4) then the square root is easy, that's why we pick those primes
		// pick a quadratic non residue
		BigInteger z = _2;
		// while z^((p-1)/2) == 1
		while(z.modPow(p_1.shiftRight(1), prime).equals(ONE)) z = z.add(ONE);
		
		BigInteger c = z.modPow(q, prime);//pow(z, q);
		BigInteger r = n.modPow(q.add(ONE).shiftRight(1), prime);//pow(n, (q+1)/2);
		BigInteger t = n.modPow(q, prime);//pow(n, q);
		int m = s;
		while(!t.equals(ONE)){
			int i = 1;
			// find the lowest i st. t^(2^i) = 1 mod prime
			BigInteger e = t.multiply(t).mod(prime);
			while(!e.equals(ONE)){
				i++;
				// square
				e = e.multiply(e).mod(prime);
			}
			BigInteger b = c.modPow(valueOf(1l << (m-i-1)), prime); //pow(c, pow(2, m-i-1));
			r = b.multiply(r).mod(prime);//b*r % prime;
			t = t.multiply(b).mod(prime).multiply(b).mod(prime);//(t * b % prime) * b % prime;
			c = b.multiply(b).mod(prime);//b * b % prime;
			m = i;		
		}
		return r;
	}
	
	/***
	 * @return a random point in the curve
	 */
	public Point getRandomPoint(){
		Random r = new Random();
		BigInteger x = null, y2 = null, y = null;
		do{	
			x = new BigInteger(prime.bitLength(), r).mod(prime);
			// y2 = x^3 + a*x + b mod prime
			y2 = x.pow(3).mod(prime).add(a.multiply(x)).add(b).mod(prime);
			y = sqrt(y2);
		}while(y == null);
		
		// pick one of the roots at random
		if(r.nextBoolean())		
			return new Point(x, y);
		else
			return new Point(x, prime.subtract(y));
	}

	/**
	 * @param p
	 * @return true if point is in the curve
	 */
	public boolean isInCurve(Point p) {
		return p.isInf || isInCurve(p.x, p.y);
	}

	/***
	 * @param x
	 * @param y
	 * @return true if point it in de curve
	 */
	public boolean isInCurve(BigInteger x, BigInteger y) {
		BigInteger left = y.pow(2).mod(prime);
		// x^3 + ax + b
		BigInteger right = x.pow(3).mod(prime).add(a.multiply(x)).add(b).mod(prime);
		return left.equals(right); 
	}

	/**
	 * Add two points according to definition of addition 
	 * @param p
	 * @param q
	 * @return
	 */
	public Point add(Point p, Point q) {
		Point r = new Point();
		// if one is the identity
		if (p.isInf)
			return q;
		if (q.isInf)
			return p;
		// if the same (doubling) and zero y
		if (p.equals(q) && p.y.equals(ZERO))
			r.isInf = true;
		// if the same (doubling)
		else if(p.equals(q)){
			//s = (3 * p.x ^ 2 + a) / (2 * p.y) % prime;
			BigInteger s = _3.multiply(p.x.pow(2)).add(a).mod(prime);
			s = s.multiply(p.y.shiftLeft(1).modInverse(prime));
			//r.x = (s^2 - 2 * p.x) % prime;
			r.x = s.pow(2).subtract(p.x.shiftLeft(1)).mod(prime);
			// r.y = s * (p.x - r.x) - p.y;
			r.y = s.multiply(p.x.subtract(r.x)).subtract(p.y).mod(prime);
		}
		// if conjugate return unit
		else if (p.x.equals(q.x)) {
			r.isInf = true;
		} else {
			// Get 3rd instersection point conjugate
			// s = (p.y - q.y) / (p.x - q.x) % prime;
			BigInteger s = p.y.subtract(q.y).multiply(p.x.subtract(q.x).modInverse(prime)) .mod(prime);
			// r.x = s^2 - p.x - q.x;
			r.x = s.pow(2).subtract(p.x).subtract(q.x).mod(prime);
			// r.y = s * (p.x - r.x) - p.y;
			r.y = s.multiply(p.x.subtract(r.x)).subtract(p.y).mod(prime);
		}
		return r;
	}
	
	/**
	 * Multiplies a point by a scalar
	 * @param k
	 * @param p
	 * @return
	 */
	public Point mult(BigInteger k, Point p){
		// bitwise multiplication (squares)
		Point n = p;
		Point r = new Point(true);
		while(k.compareTo(ZERO) > 0)
		{
			//if(k % 2 == 1)
			if(k.testBit(0) == true)
				r = add(r, n);
			// double
			n = add(n,n);
			k = k.shiftRight(1);
		}
		return r;
	}
	
	public BigInteger getOrder(Point g){
		// TODO calculate order -> Schoof Algorithm (Tricky)
		return null;
	}
	
	/**
	 * Multiplies G by a scalar
	 * @param k
	 * @return
	 */
	public Point generate(BigInteger k){
		return mult(k, this.G);
	}
	
	static class Point {
		BigInteger x, y;
		boolean isInf = false;

		public Point() {}
		public Point(boolean isInf) {
			this.isInf = isInf;
		}
		// TODO build a point from compressed form
		public Point(BigInteger x, BigInteger y){
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(Object o) {
			Point p = (Point) o;
			return (isInf && isInf == p.isInf)|| (x.equals(p.x) && y.equals(p.y));
		}
		
		@Override
		public String toString() {
			return !isInf ? String.format("(%d, %d)", x, y) : "(Inf, Inf)";
		}
	}
}


