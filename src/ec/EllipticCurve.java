package ec;
import java.util.Random;

import javax.management.RuntimeErrorException;


/**
 * An elliptic Curve implementation
 * with long.
 * @author jbadillo
 *
 */
public class EllipticCurve {

	long a, b;
	long prime;
	Point G;
	
	// order and cofactor
	long  n, h;
	public EllipticCurve(long a, long b, long prime) {
		this.a = a;
		this.b = b;
		this.prime = prime;
		// discriminant
		if((4*a*a*a + 27*b*b) % prime == 0)
			throw new RuntimeErrorException(null, "Wrong parameters");
	}
	
	public void setGenerator(Point g, long n){
		this.G = g;
		// TODO calculate order -> Schoof Algorithm (Tricky)
		this.n = n;
		/*
		this.n = 1;
		while(!g.isInf)
			g = generate(n++ + 1);*/
	}

	public static void main(String[] args) {
		EllipticCurve ec = new EllipticCurve(5, 7, 17);
		
		/*
		for (int x = 0; x < ec.prime; x++)
			for (int y = 0; y < ec.prime; y++)
				if (ec.isInCurve(x, y))
					System.out.println(x + "\t" + y);
		//*/
		ec.printAllPoints();
		
	}
	
	public void printAllPoints(){
		for (long x = 0; x < prime; x++)
		{
			long y2 = (x*x*x + a*x+ b) % prime;
			long y = sqrt(y2);
			if(y != -1){
				System.out.println(x+"\t"+y+" "+isInCurve(x, y));
				y = prime - y;
				System.out.println(x+"\t"+y+" "+isInCurve(x, y));
			}
		}
	}
	
	public long sqrt(long n){
		if(n == 0)
			return 0;
		if(n == 1)
			return 1;
		
		long p_1 = prime - 1;
		long s = 0, q = p_1;
		
		// if doesn't pass euler's criterion
		if(pow(n, p_1 / 2) != 1)
			return -1;
		
		// factor out powers of 2 from p - 1;
		while(q % 2 == 0)
		{
			q /= 2;
			s++;
		}
		
		// if p = 3 (mod 4) then the square root is easy, that's why we pick those primes
		// pick a quadratic non residue
		long z = 2;
		while(pow(z, p_1 / 2) == 1) z++;
		
		long c = pow(z, q);
		long r = pow(n, (q+1)/2);
		long t = pow(n, q);
		long m = s;
		while(t != 1){
			long i = 1;
			// find the lowest i
			long e = t*t %prime;
			while(e != 1){
				i++;
				e = e*e % prime;
			}
			long b = pow(c, pow(2, m-i-1));
			r = b*r % prime;
			t = (t * b % prime) * b % prime;
			c = b * b % prime;
			m = i;		
		}
		return r;
	}
	
	public long pow(long a, long e){
		long pow = 1;
		long sq = a;
		while(e > 0){
			if(e % 2 == 1){
				pow *= sq;
				pow %= prime;
			}
			sq = sq * sq % prime;
			e /= 2;
		}
		return pow;
	}
	
	public Point getRandomPoint(){
		Random r = new Random();
		long x = 1, y2 = 1, y = 1;
		do{	
			x = (r.nextLong() % prime + prime) % prime;
			y2 = ((x * x % prime) * x % prime + a * x % prime + b) % prime;
			y = sqrt(y2);
		}while(y == -1);
		
		// pick one of the roots at random
		if(r.nextBoolean())		
			return new Point(x, y);
		else
			return new Point(x, prime - y);
	}

	public boolean isInCurve(Point p) {
		return p.isInf || isInCurve(p.x, p.y);
	}

	public boolean isInCurve(long x, long y) {
		long left = y * y % prime;
		long right = (((x * x % prime) * x % prime + a * x % prime + b) % prime + prime) % prime; 
		return left == right; 
	}

	public Point add(Point p, Point q) {
		Point r = new Point();
		// if one is the identity
		if (p.isInf)
			return q;
		if (q.isInf)
			return p;
		// if the same (doubling) and zero y
		if (p.equals(q) && p.y == 0)
			r.isInf = true;
		// if the same (doubling)
		else if(p.equals(q)){
			long s = (3 * p.x * p.x % prime + this.a) % prime;
			s = s * inverse(2 * p.y) % prime;
			r.x = (s * s % prime - 2 * p.x) % prime;
			r.y = s * (p.x - r.x) % prime - p.y;
		}
		// if conjugate return unit
		else if (p.x == q.x) {
			r.isInf = true;
		} else {
			// Get 3rd instersection point conjugate
			long s = (p.y - q.y) *inverse(p.x - q.x) % prime;
			r.x = s * s % prime - p.x - q.x;
			r.y = s * (p.x - r.x) % prime - p.y;
		}
		r.x = ((r.x % prime) + prime) % prime;
		r.y = ((r.y % prime) + prime) % prime;
		return r;
	}
	
	public Point mult(long k, Point p){
		// bitwise multiplication (squares)
		Point n = p;
		Point r = new Point(true);
		while(k > 0)
		{
			if(k % 2 == 1)
				r = add(r, n);
			// double
			n = add(n,n);
			k = k >> 1;
		}
		return r;
	}
	
	public Point generate(long k){
		return mult(k, this.G);
	}
	
	/**
	 * Invert modulo prime (the curve)
	 * @param m
	 * @return
	 */
	public long inverse(long n) {
		return inverse(n, prime);
	}
	
	public long inverse(long n, long mod){
		n = (n % mod + mod) % mod;
		long r = new EuclidAlgorithm(n, mod).s_old;
		return (r % mod + mod) % mod;
	}
	
	static class Point {
		long x, y;
		boolean isInf = false;

		public Point() {}
		public Point(boolean isInf) {
			this.isInf = isInf;
		}
		public Point(long x, long y){
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(Object o) {
			Point p = (Point) o;
			return x == p.x && y == p.y && isInf == p.isInf;
		}
		
		@Override
		public String toString() {
			return !isInf ? String.format("(%d, %d)", x, y) : "(Inf, Inf)";
		}
	}

}

