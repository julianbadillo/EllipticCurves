import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import static java.math.BigInteger.*;
import java.util.Random;

import org.junit.Test;

public class EuclidAlgorithmTest {

	@Test
	public void testEuclidAlgorithm() {
		EuclidAlgorithm ec = new EuclidAlgorithm(7, 9);
		
		// validate equation
		assertEquals(ec.r_old, 7*ec.s_old + 9*ec.t_old);
		
		Random r = new Random();
		// several gcds random
		for (int i = 0; i < 10; i++) {	
			int a = r.nextInt(100);
			int b = r.nextInt(100);
			if(a== 0 || b == 0) continue;
			ec = new EuclidAlgorithm(a, b);
			
			// equation holds
			assertEquals(ec.r_old, a*ec.s_old + b*ec.t_old);
		}
	}
	

	@Test
	public void testEuclidAlgorithmGcd() {
		EuclidAlgorithm ec = new EuclidAlgorithm(12, 8);
		assertEquals(4, ec.r_old);

		ec = new EuclidAlgorithm(144, 64);
		assertEquals(16, ec.r_old);

		ec = new EuclidAlgorithm(111 * 8, 111 * 17);
		assertEquals(111, ec.r_old);
		
		Random r = new Random();
		
		// several gcds random
		for (int i = 0; i < 10; i++) {	
			int a = r.nextInt(100);
			int b = r.nextInt(100);
			if(a== 0 || b == 0) continue;
			ec = new EuclidAlgorithm(a, b);
			// divides both
			assertEquals(0, a % ec.gcd);
			assertEquals(0, b % ec.gcd);
		}
	}

	@Test
	public void testEuclidAlgorithmBigger() {

		// two primes
		EuclidAlgorithm a = new EuclidAlgorithm(5915587277L, 1500450271L);
		assertEquals(1, a.r_old);

		// two composed
		a = new EuclidAlgorithm(3267000013L * 16, 3267000013L * 15);
		assertEquals(3267000013L, a.r_old);

		// prime + composed
		a = new EuclidAlgorithm(3267000013L * 118, 3267000013L);
		assertEquals(3267000013L, a.r_old);
	}

	@Test
	public void testEuclidInvert() {
		long p = 17;
		EuclidAlgorithm a = new EuclidAlgorithm(6, p);
		assertEquals(1, a.r_old); // gcd
		assertEquals(3, a.s_old); // mod inverse
		long j;
		for (int i = 1; i < p; i++) {
			a = new EuclidAlgorithm(i, p);
			j = (a.s_old % p + p) % p;
			assertEquals("Failed on " + i, 1, i * j % p);// check // works
		}

		p = 37;
		for (int i = 1; i < p; i++) {
			a = new EuclidAlgorithm(i, p);
			j = (a.s_old % p + p) % p;
			assertEquals("Failed on " + i, 1, i * j % p);// check // works
		}
	}

	@Test
	public void testEuclidInvertBig() {
		long p = 10007;
		// Long.MAX_VALUE = 9223372036854775807L;
		EuclidAlgorithm a = new EuclidAlgorithm(6, p);

		long n_1, prod;
		Random r = new Random();
		for (int i = 0; i < 100; i++) {
			long n = (r.nextLong() % p + p) % p;
			if (n == 0)
				continue;
			a = new EuclidAlgorithm(n, p);
			n_1 = (a.s_old % p + p) % p;
			prod = (n * n_1) % p;
			assertEquals("Failed on " + n, 1, prod);// check
													// inverse
													// works
		}
	}

	/**
	 * 
	 */
	@Test
	public void testEuclidInvertBigger() {
		long p = 15485863;
		// Long.MAX_VALUE = 9223372036854775807L;
		EuclidAlgorithm a;

		long n_1, prod;
		Random r = new Random();
		for (int i = 0; i < 1000; i++) {
			long n = (r.nextLong() % p + p) % p;
			if (n == 0)
				continue;
			a = new EuclidAlgorithm(n, p);
			n_1 = (a.s_old % p + p) % p;
			prod = (n * n_1) % p;
			assertEquals("Failed on n = " + n + " n_1 = " + n_1, 1, prod);// check
			// inverse
			// works
		}
	}
	
	@Test
	public void testEuclidAlgorithmBI() {
		EuclidAlgorithmBI ec = new EuclidAlgorithmBI(new BigInteger("173"), new BigInteger("351"));
		
		// validate equation
		//ec.r_old = a*ec.s_old + b*ec.t_old
		assertEquals(ec.r_old, ec.a.multiply(ec.s_old).add(ec.b.multiply(ec.t_old)));
		
		Random r = new Random();
		// number pairs at random
		for (int i = 0; i < 100; i++) {	
			BigInteger a = new BigInteger(128, r);
			BigInteger b = new BigInteger(128, r);
			if(a.equals(ZERO) || b.equals(ZERO)) continue;
			ec = new EuclidAlgorithmBI(a, b);
			
			// equation holds
			// ec.r_old = a*ec.s_old + b*ec.t_old
			assertEquals(ec.r_old, ec.a.multiply(ec.s_old).add(ec.b.multiply(ec.t_old)));
		}
	}
	

	@Test
	public void testEuclidAlgorithmBIGcd() {
		EuclidAlgorithmBI ec = new EuclidAlgorithmBI(valueOf(12), valueOf(8));
		assertEquals(valueOf(4), ec.r_old);

		ec = new EuclidAlgorithmBI(valueOf(144), valueOf(64));
		assertEquals(valueOf(16), ec.r_old);

		ec = new EuclidAlgorithmBI(valueOf(111 * 8), valueOf(111 * 17));
		assertEquals(valueOf(111), ec.r_old);
		
		Random r = new Random();
		
		// several gcds random
		for (int i = 0; i < 100; i++) {	
			BigInteger a = new BigInteger(128, r);
			BigInteger b = new BigInteger(128, r);
			if(a.equals(ZERO) || b.equals(ZERO)) continue;
			ec = new EuclidAlgorithmBI(a, b);
			// divides both
			assertEquals(ZERO, a.mod(ec.gcd));
			assertEquals(ZERO, a.mod(ec.gcd));
		}
	}
	
	@Test
	public void testEuclidBIInvert() {
		BigInteger p = valueOf(17);
		EuclidAlgorithmBI ec = new EuclidAlgorithmBI(valueOf(6), p);
		assertEquals(valueOf(1), ec.r_old); // gcd
		assertEquals(valueOf(3), ec.s_old); // mod inverse
		
		Random r = new Random();
		// a known 160 bit prime
		p = new BigInteger("E95E4A5F737059DC60DFC7AD95B3D8139515620F", 16);
		
		// several ints
		for (int i = 0; i < 100; i++) {	
			BigInteger a = new BigInteger(p.bitLength(), r).mod(p);
			
			if(a.equals(ZERO)) continue;
			
			ec = new EuclidAlgorithmBI(a, p);
			// GCD = 1
			assertEquals(ONE, ec.gcd);
			// invert
			BigInteger prod = a.multiply(ec.s_old).mod(p);
			assertEquals(ONE, prod);
		}
		
	}

}
