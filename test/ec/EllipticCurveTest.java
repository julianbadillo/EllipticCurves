package ec;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Test;

import ec.EllipticCurve;
public class EllipticCurveTest {


	@Test
	public void testOrder() {
		EllipticCurve ec = new EllipticCurve(5, 7, 17);
		ec.setGenerator(new EllipticCurve.Point(2, 12), 23);
		assertEquals(23, ec.n);
	}

	@Test
	public void testInvert() {
		long p = 17;
		EllipticCurve ec = new EllipticCurve(5, 7, p);

		for (int i = 1; i < p; i++)
			assertEquals("Failed on " + i, 1, i * ec.inverse(i) % p);

		p = 37;
		for (int i = 1; i < p; i++)
			assertEquals("Failed on " + i, 1, i * ec.inverse(i, p) % p);
	}

	@Test
	public void testInvertBig() {
		long p = 10007;
		EllipticCurve ec = new EllipticCurve(5, 7, p);
		Random r = new Random();
		for (int i = 1; i < 1000; i++) {
			long n = 1 + r.nextInt((int) p - 1);
			long n_1 = ec.inverse(n);
			long prod = n * n_1 % p;
			assertEquals("Failed on " + n, 1, prod);
		}
	}

	@Test
	public void testInvertBigger() {
		long p = 15485863;
		EllipticCurve ec = new EllipticCurve(5, 7, p);
		Random r = new Random();
		for (int i = 1; i < 1000; i++) {
			long n = (p + r.nextLong() % p) % p;
			if(n == 0) continue;
			long n_1 = ec.inverse(n);
			long prod = n * n_1 % p;
			assertEquals("Failed case: n = " + n + ", n_1 = " + n_1, 1, prod);
		}
	}

	@Test
	public void testGetRandomPoint() {
		long p = 10007;
		EllipticCurve ec = new EllipticCurve(1041, 1242, p);
		for (int i = 0; i < 100; i++) {
			assertTrue(ec.isInCurve(ec.getRandomPoint()));
		}
	}

	@Test
	public void testPower() {

		EllipticCurve ec = new EllipticCurve(5, 7, 10007);
		assertEquals(125, ec.pow(5, 3));
		assertEquals(625, ec.pow(5, 4));
		assertEquals(722, ec.pow(12, 4));
		assertEquals(1024, ec.pow(2, 10));
		assertEquals(5081, ec.pow(7, 12));
	}

	@Test
	public void testEulerCriterion() {

		EllipticCurve ec = new EllipticCurve(5, 7, 10007);
		HashSet<Long> quadratic = new HashSet<>();

		// quadratic residues
		for (long i = 2; i < ec.prime; i++) {
			long p = i * i % ec.prime;
			assertEquals(1, ec.pow(p, (ec.prime - 1) / 2));
			quadratic.add(p);
		}

		// quadratic non residues
		for (long i = 2; i < ec.prime; i++) {
			if (!quadratic.contains(i))
				assertEquals(ec.prime - 1, ec.pow(i, (ec.prime - 1) / 2));
		}
	}

	@Test
	public void testSqrt() {

		EllipticCurve ec = new EllipticCurve(5, 7, 17);
		HashSet<Long> quadratic = new HashSet<>();

		// quadratic residues
		for (long i = 0; i < ec.prime; i++) {
			long p = i * i % ec.prime;
			long sq = ec.sqrt(p);
			// either of the two roots
			assertTrue("failed on p = " + p + " sq = " + sq, sq == i || sq == ec.prime - i);

			quadratic.add(p);
		}

		// quadratic non residues
		for (long i = 0; i < ec.prime; i++) {
			if (!quadratic.contains(i))
				assertEquals(-1, ec.sqrt(i));
		}
	}

	@Test
	public void testSqrtBig() {

		EllipticCurve ec = new EllipticCurve(5, 7, 10007);
		HashSet<Long> quadratic = new HashSet<>();

		// quadratic residues
		for (long i = 2; i < ec.prime; i++) {
			long p = i * i % ec.prime;
			long sq = ec.sqrt(p);
			// either of the two roots
			assertTrue(sq == i || sq == ec.prime - i);
			quadratic.add(p);
		}

		// quadratic non residues
		for (long i = 2; i < ec.prime; i++) {
			if (!quadratic.contains(i))
				assertEquals(-1, ec.sqrt(i));
		}
	}

	@Test
	public void testSqrtBigger() {
		EllipticCurve ec = new EllipticCurve(5, 7, 15485863);

		// quadratic residues
		Random r = new Random();
		for (long i = 0; i < 1000; i++) {
			long n = (r.nextLong() % ec.prime + ec.prime) % ec.prime;
			long sq = ec.sqrt(n);
			if (sq != -1)
				assertEquals(sq * sq % ec.prime, n);
		}
	}

	@Test
	public void testAddition() {
		EllipticCurve ec = new EllipticCurve(5, 7, 17);
		LinkedList<EllipticCurve.Point> points = new LinkedList<>();
		for (int i = 0; i < ec.prime; i++) {
			for (int j = i; j < ec.prime; j++) {
				EllipticCurve.Point p = new EllipticCurve.Point(i, j);
				if (ec.isInCurve(p))
					points.add(p);
			}
		}

		for (EllipticCurve.Point p : points)
			for (EllipticCurve.Point q : points) {
				if (!p.equals(q)) {
					EllipticCurve.Point r = ec.add(p, q);
					assertTrue(p + " + " + q + " = " + r + " not in curve", ec.isInCurve(r));
				}
			}

	}

	@Test
	public void testAdditionBig() {

		EllipticCurve ec = new EllipticCurve(5, 7, 10007);
		LinkedList<EllipticCurve.Point> points = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			EllipticCurve.Point p = ec.getRandomPoint();
			assertTrue(ec.isInCurve(p));
			points.add(p);
		}

		for (EllipticCurve.Point p : points)
			for (EllipticCurve.Point q : points) {
				if (!p.equals(q)) {
					EllipticCurve.Point r = ec.add(p, q);
					assertTrue(p + " + " + q + " = " + r + " not in curve", ec.isInCurve(r));
				}
			}
	}
//15485863
	@Test
	public void testAdditionBigger() {

		EllipticCurve ec = new EllipticCurve(5, 7, 15485863);
		LinkedList<EllipticCurve.Point> points = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			EllipticCurve.Point p = ec.getRandomPoint();
			assertTrue(ec.isInCurve(p));
			points.add(p);
		}

		for (EllipticCurve.Point p : points)
			for (EllipticCurve.Point q : points) {
				if (!p.equals(q)) {
					EllipticCurve.Point r = ec.add(p, q);
					assertTrue(p + " + " + q + " = " + r + " not in curve", ec.isInCurve(r));
				}
			}
	}	
	/**
	 * 
	 */
	@Test
	public void testDoubling() {
		EllipticCurve ec = new EllipticCurve(5, 7, 17);
		LinkedList<EllipticCurve.Point> points = new LinkedList<>();
		for (int i = 0; i < ec.prime; i++) {
			for (int j = i; j < ec.prime; j++) {
				EllipticCurve.Point p = new EllipticCurve.Point(i, j);
				if (ec.isInCurve(p))
					points.add(p);
			}
		}

		for (EllipticCurve.Point p : points) {
			EllipticCurve.Point r = ec.add(p, p);
			assertTrue(ec.isInCurve(r));
		}
	}

	/**
	 * 
	 */
	@Test
	public void testDoublingBig() {
		EllipticCurve ec = new EllipticCurve(5, 7, 10007);
		LinkedList<EllipticCurve.Point> points = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			EllipticCurve.Point p = ec.getRandomPoint();
			assertTrue(ec.isInCurve(p));
			points.add(p);
		}

		for (EllipticCurve.Point p : points) {
			EllipticCurve.Point r = ec.add(p, p);
			assertTrue(ec.isInCurve(r));
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void testDoublingBigger() {
		EllipticCurve ec = new EllipticCurve(5, 7, 15485863);
		LinkedList<EllipticCurve.Point> points = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			EllipticCurve.Point p = ec.getRandomPoint();
			assertTrue(ec.isInCurve(p));
			points.add(p);
		}

		for (EllipticCurve.Point p : points) {
			EllipticCurve.Point r = ec.add(p, p);
			assertTrue(ec.isInCurve(r));
		}
	}

	
	/**
	 * 
	 */
	@Test
	public void testAdditionWithInfinite() {
		EllipticCurve ec = new EllipticCurve(5, 7, 17);
		LinkedList<EllipticCurve.Point> points = new LinkedList<>();
		for (int i = 0; i < ec.prime; i++) {
			for (int j = i; j < ec.prime; j++) {
				EllipticCurve.Point p = new EllipticCurve.Point(i, j);
				if (ec.isInCurve(p))
					points.add(p);
			}
		}
		EllipticCurve.Point o = new EllipticCurve.Point(true);
		for (EllipticCurve.Point p : points) {
			EllipticCurve.Point r = ec.add(p, o);
			assertTrue(ec.isInCurve(r));
			assertEquals(p, r);
		}
	}


	/**
	 * 
	 */
	@Test
	public void testMultiplication() {
		EllipticCurve ec = new EllipticCurve(5, 7, 17);
		LinkedList<EllipticCurve.Point> points = new LinkedList<>();
		for (int i = 0; i < ec.prime; i++) {
			for (int j = i; j < ec.prime; j++) {
				EllipticCurve.Point p = new EllipticCurve.Point(i, j);
				if (ec.isInCurve(p))
					points.add(p);
			}
		}

		for (EllipticCurve.Point p : points) {
			for (int i = 1; i < ec.prime; i++) {
				EllipticCurve.Point r = ec.mult(i, p);
				assertTrue(ec.isInCurve(r));
			}
		}
	}
	
	@Test
	public void testMultiplicationBig() {
		EllipticCurve ec = new EllipticCurve(5, 7, 10007);
		LinkedList<EllipticCurve.Point> points = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			EllipticCurve.Point p = ec.getRandomPoint();
			assertTrue(ec.isInCurve(p));
			points.add(p);
		}

		Random rand = new Random();
		for (EllipticCurve.Point p : points) {
			for (int i = 0; i < 1000; i++) {
				long f = ((rand.nextLong() % ec.prime) + ec.prime) % ec.prime;
				EllipticCurve.Point r = ec.mult(f, p);
				assertTrue(ec.isInCurve(r));
			}
		}
	}

	@Test
	public void testMultiplicationBigger() {
		EllipticCurve ec = new EllipticCurve(5, 7, 15485863);
		LinkedList<EllipticCurve.Point> points = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			EllipticCurve.Point p = ec.getRandomPoint();
			assertTrue(ec.isInCurve(p));
			points.add(p);
		}

		Random rand = new Random();
		for (EllipticCurve.Point p : points) {
			for (int i = 0; i < 1000; i++) {
				long f = ((rand.nextLong() % ec.prime) + ec.prime) % ec.prime;
				EllipticCurve.Point r = ec.mult(f, p);
				assertTrue(ec.isInCurve(r));
			}
		}
	}
	/**
	 * 
	 */
	@Test
	public void testDiffieHellman() {

		EllipticCurve ec = new EllipticCurve(5, 7, 17);
		ec.setGenerator(new EllipticCurve.Point(2, 12), 23);

		// pick a private key
		long qa = 19;
		// calculate the public key
		EllipticCurve.Point QA = ec.generate(qa);
		// an attacker has to log QA to guess qa
		Random r = new Random();

		for (int i = 0; i < 10; i++) {
			// pick any secret number in (1, n)
			long s = 1 + r.nextInt((int) ec.n - 1);

			// Get the shared secret from the public key
			EllipticCurve.Point S1 = ec.mult(s, QA);

			// Calculate message to transmit from the curve
			EllipticCurve.Point R = ec.generate(s);

			// transmit R - an attacker has to log R to guess S.

			// Calculate shared secret with the private key
			EllipticCurve.Point S2 = ec.mult(qa, R);

			assertEquals(S1, S2);
		}

	}

	@Test
	public void testPublicSignature() {
		EllipticCurve ec = new EllipticCurve(5, 7, 17);
		ec.setGenerator(new EllipticCurve.Point(2, 12), 23);

		// Digital signature
		// create private-public key pair
		long qa = 19;
		EllipticCurve.Point QA = ec.generate(qa);

		Random rand = new Random();
		// hash of the message
		long h = 18;

		for (int i = 0; i < 10; i++) {

			// pick a random k / temp key
			long k = 0, r = 0, s = 0;
			do {
				k = 1 + rand.nextInt((int) ec.n - 1);
				EllipticCurve.Point R = ec.generate(k);

				r = R.x % ec.n;
				// signature
				s = (h + qa * r) * ec.inverse(k, ec.n) % ec.n;
				// System.out.println("signature = " + r + ", "+ s);
			} while (r == 0 || s == 0);
			// if or n == 0 s == 0, pick another k

			// Transmit signature and hash (h, r, s)

			// I want to verify signature, I recalculate the hash
			// and from it I try to build up R using public Key
			long w = ec.inverse(s, ec.n);
			long u1 = h * w % ec.n;
			long u2 = r * w % ec.n;
			// P = u1*G + u2*QA
			EllipticCurve.Point p = ec.add(ec.generate(u1), ec.mult(u2, QA));
			assertEquals(r, p.x);
		}

	}

}
