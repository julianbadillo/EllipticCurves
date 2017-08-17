package ec;
import static org.junit.Assert.*;

import java.math.BigInteger;
import static java.math.BigInteger.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Test;

import ec.EllipticCurve2;

public class EllipticCurve2Test {

	@Test
	public void testOrder() {
		EllipticCurve2 ec = new EllipticCurve2(valueOf(5), valueOf(7), valueOf(17));
		ec.setGenerator(new EllipticCurve2.Point(valueOf(2), valueOf(12)), valueOf(23));
		assertEquals(valueOf(23), ec.n);
	}

	@Test
	public void testEqualToOne() {
		BigInteger o = ONE;
		assertEquals(ONE, o);
		assertTrue(ONE == o);
		o = o.shiftLeft(10);
		o = o.shiftRight(10);
		assertEquals(ONE, o);
		// assertTrue(ONE == o);
	}

	@Test
	public void testIncludedInHashSet() {
		HashSet<BigInteger> s = new HashSet<>();
		BigInteger b = valueOf(101);
		s.add(b);

		BigInteger c = new BigInteger("101");
		assertTrue(s.contains(c));
		c = c.shiftLeft(5);
		c = c.shiftRight(5);
		assertTrue(s.contains(c));
	}

	@Test
	public void testPointEquality() {
		EllipticCurve2.Point p = new EllipticCurve2.Point(valueOf(17), valueOf(19));
		EllipticCurve2.Point q = new EllipticCurve2.Point(valueOf(17), valueOf(19));
		assertEquals(p, q);
	}

	@Test
	public void testInvertBig() {
		BigInteger p = valueOf(10007);
		Random r = new Random();
		for (int i = 1; i < 1000; i++) {
			BigInteger n = new BigInteger(p.bitLength(), r).mod(p);
			if (n.equals(ZERO))
				continue;
			BigInteger n_1 = n.modInverse(p);
			BigInteger prod = n.multiply(n_1).mod(p);
			assertEquals("Failed on " + n, BigInteger.ONE, prod);
		}
	}

	@Test
	public void testInvertBigger() {
		BigInteger p = valueOf(15485863);
		Random r = new Random();
		for (int i = 1; i < 1000; i++) {
			BigInteger n = new BigInteger(p.bitLength(), r).mod(p);
			if (n.equals(ZERO))
				continue;
			BigInteger n_1 = n.modInverse(p);
			BigInteger prod = n.multiply(n_1).mod(p);
			assertEquals("Failed on " + n, BigInteger.ONE, prod);
		}
	}

	@Test
	public void testIsInCurve() {
		BigInteger prime = valueOf(10007);
		// simple curve y^2 = x^3 + x + 6
		EllipticCurve2 ec = new EllipticCurve2(valueOf(1), valueOf(6), prime);

		// (2, 4)
		EllipticCurve2.Point p = new EllipticCurve2.Point(valueOf(2), valueOf(4));
		assertTrue(ec.isInCurve(p));
		// (3, 6)
		p = new EllipticCurve2.Point(valueOf(3), valueOf(6));
		assertTrue(ec.isInCurve(p));
		// (1, 5282)
		p = new EllipticCurve2.Point(valueOf(1), valueOf(5282));
		assertTrue(ec.isInCurve(p));

		// (2, 10007 - 4)
		p = new EllipticCurve2.Point(valueOf(2), valueOf(10007 - 4));
		assertTrue(ec.isInCurve(p));
		// (3, 6)
		p = new EllipticCurve2.Point(valueOf(3), valueOf(10007 - 6));
		assertTrue(ec.isInCurve(p));

	}

	@Test
	public void testGetRandomPoint() {
		BigInteger prime = valueOf(10007);
		EllipticCurve2 ec = new EllipticCurve2(valueOf(1041), valueOf(1242), prime);
		for (int i = 0; i < 100; i++) {
			EllipticCurve2.Point p = ec.getRandomPoint();
			assertNotNull(p);
			assertTrue(ec.isInCurve(p));
		}
	}

	@Test
	public void testPiEndomorphism() {
		BigInteger prime = valueOf(10007);
		EllipticCurve2 ec = new EllipticCurve2(valueOf(1041), valueOf(1242), prime);
		for (int i = 0; i < 100; i++) {
			EllipticCurve2.Point p = ec.getRandomPoint();
			assertNotNull(p);
			assertTrue(ec.isInCurve(p));
			// transform
			EllipticCurve2.Point p2 = new EllipticCurve2.Point(p.x.modPow(ec.prime, ec.prime) , p.y.modPow(ec.prime, ec.prime));
			assertTrue(ec.isInCurve(p2));
			assertEquals(p, p2);
		}
	}

	
	@Test
	public void testEulerCriterion() {
		BigInteger prime = valueOf(10007);
		BigInteger prime_1 = prime.subtract(ONE);
		HashSet<BigInteger> quadratic = new HashSet<>();

		// quadratic residues
		for (long i = 2; i < 10007; i++) {
			BigInteger x = valueOf(i).pow(2).mod(prime);
			assertEquals(ONE, x.modPow(prime_1.shiftRight(1), prime));
			quadratic.add(x);
		}

		// quadratic non residues
		for (long i = 2; i < 10007; i++) {
			if (!quadratic.contains(valueOf(i)))
				assertEquals(prime_1, valueOf(i).modPow(prime_1.shiftRight(1), prime));
		}
	}

	@Test
	public void testSqrt() {
		BigInteger prime = valueOf(10007);
		EllipticCurve2 ec = new EllipticCurve2(valueOf(1041), valueOf(1242), prime);
		HashSet<BigInteger> quadratic = new HashSet<>();

		// quadratic residues
		for (long i = 0; i < 10007; i++) {
			BigInteger p = valueOf(i).pow(2).mod(ec.prime);
			BigInteger sq = ec.sqrt(p);
			assertNotNull(sq);

			// either of the two roots
			assertTrue("failed on p = " + p + " sq = " + sq,
					sq.equals(valueOf(i)) || sq.equals(ec.prime.subtract(valueOf(i))));
			quadratic.add(p);
		}

		// quadratic non residues
		for (long i = 0; i < 10007; i++)
			if (!quadratic.contains(valueOf(i)))
				assertEquals(null, ec.sqrt(valueOf(i)));
	}

	@Test
	public void testSqrtBigger() {
		EllipticCurve2 ec = new EllipticCurve2(valueOf(1041), valueOf(1242), valueOf(15485863));

		// quadratic residues
		Random r = new Random();
		for (long i = 0; i < 1000; i++) {
			BigInteger n = new BigInteger(ec.prime.bitLength(), r).mod(ec.prime);
			if (n.equals(ZERO))
				continue;

			BigInteger sq = ec.sqrt(n);
			if (sq != null)
				assertEquals(sq.pow(2).mod(ec.prime), n);
		}
	}

	// 15485863
	@Test
	public void testAdditionBigger() {

		EllipticCurve2 ec = new EllipticCurve2(valueOf(102893), valueOf(192938), valueOf(15485863));
		LinkedList<EllipticCurve2.Point> points = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			EllipticCurve2.Point p = ec.getRandomPoint();
			assertTrue(ec.isInCurve(p));
			points.add(p);
		}

		for (EllipticCurve2.Point p : points)
			for (EllipticCurve2.Point q : points)
				if (!p.equals(q)) {
					EllipticCurve2.Point r = ec.add(p, q);
					assertTrue(p + " + " + q + " = " + r + " not in curve", ec.isInCurve(r));
				}

	}

	@Test
	public void testDoubling() {
		EllipticCurve2 ec = new EllipticCurve2(valueOf(5), valueOf(7), valueOf(17));
		LinkedList<EllipticCurve2.Point> points = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			EllipticCurve2.Point p = ec.getRandomPoint();
			assertTrue(ec.isInCurve(p));
			points.add(p);
		}

		for (EllipticCurve2.Point p : points) {
			EllipticCurve2.Point r = ec.add(p, p);
			assertTrue("not in curve " + r + " = 2*" + p, ec.isInCurve(r));
		}
	}

	@Test
	public void testDoublingBigger() {
		EllipticCurve2 ec = new EllipticCurve2(valueOf(102893), valueOf(192938), valueOf(15485863));
		LinkedList<EllipticCurve2.Point> points = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			EllipticCurve2.Point p = ec.getRandomPoint();
			assertTrue(ec.isInCurve(p));
			points.add(p);
		}

		for (EllipticCurve2.Point p : points) {
			EllipticCurve2.Point r = ec.add(p, p);
			assertTrue("not in curve " + r + " = 2*" + p, ec.isInCurve(r));
		}
	}

	@Test
	public void testAdditionWithInfinite() {
		EllipticCurve2 ec = new EllipticCurve2(valueOf(5), valueOf(7), valueOf(15485863));
		LinkedList<EllipticCurve2.Point> points = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			EllipticCurve2.Point p = ec.getRandomPoint();
			assertTrue(ec.isInCurve(p));
			points.add(p);
		}

		EllipticCurve2.Point o = new EllipticCurve2.Point(true);
		for (EllipticCurve2.Point p : points) {
			EllipticCurve2.Point r = ec.add(p, o);
			assertTrue(ec.isInCurve(r));
			assertEquals(p, r);
		}
	}

	@Test
	public void testMultiplicationBigger() {
		EllipticCurve2 ec = new EllipticCurve2(valueOf(5), valueOf(7), valueOf(15485863));
		LinkedList<EllipticCurve2.Point> points = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			EllipticCurve2.Point p = ec.getRandomPoint();
			assertTrue(ec.isInCurve(p));
			points.add(p);
		}

		Random rand = new Random();
		for (EllipticCurve2.Point p : points) {
			for (int i = 0; i < 10; i++) {
				BigInteger f = new BigInteger(ec.prime.bitLength(), rand).mod(ec.prime);
				EllipticCurve2.Point r = ec.mult(f, p);
				assertTrue(ec.isInCurve(r));
			}
		}
	}

	@Test
	public void testSetGenerator() {
		EllipticCurve2 ec = new EllipticCurve2(valueOf(5), valueOf(7), valueOf(17));
		ec.setGenerator(new EllipticCurve2.Point(valueOf(2), valueOf(12)), valueOf(23));

		try {
			ec.setGenerator(new EllipticCurve2.Point(valueOf(2), valueOf(12)), valueOf(17));
			fail("Error not caught");
		} catch (Exception ex) {

		}
	}

	@Test
	public void testGetOrder() {
		EllipticCurve2 ec = new EllipticCurve2(valueOf(5), valueOf(7), valueOf(17));
		BigInteger q = ec.getOrder(new EllipticCurve2.Point(valueOf(2), valueOf(12)));
		assertEquals(valueOf(23), q);

		ec = new EllipticCurve2(valueOf(5), valueOf(7), valueOf(10007));
		q = ec.getOrder(new EllipticCurve2.Point(valueOf(4841), valueOf(7334)));
		assertEquals(valueOf(5098), q);

		// TODO calculate order
		// ec.setGenerator(, valueOf(23));

		/*
		 * EllipticCurve2.Point G = ec.getRandomPoint();
		 * 
		 * BigInteger n = ONE; EllipticCurve2.Point p = G; while(!p.isInf){ n =
		 * n.add(ONE); p = ec.mult(n, G); } System.out.println(G+" "+ n);
		 */

	}

	@Test
	public void testLoad_brainpoolP160r1() {
		/*
		 * Curve-ID: brainpoolP160r1 p: E95E4A5F737059DC60DFC7AD95B3D8139515620F
		 * A: 340E7BE2A280EB74E2BE61BADA745D97E8F7C300 B:
		 * 1E589A8595423412134FAA2DBDEC95C8D8675E58 x(P_0):
		 * BED5AF16EA3F6A4F62938C4631EB5AF7BDBCDBC3 y(P_0):
		 * 1667CB477A1A8EC338F94741669C976316DA6321 q:
		 * E95E4A5F737059DC60DF5991D45029409E60FC09 i: 1
		 */
		EllipticCurve2 ec = new EllipticCurve2(new BigInteger("340E7BE2A280EB74E2BE61BADA745D97E8F7C300", 16),
				new BigInteger("1E589A8595423412134FAA2DBDEC95C8D8675E58", 16),
				new BigInteger("E95E4A5F737059DC60DFC7AD95B3D8139515620F", 16));

		EllipticCurve2.Point g = new EllipticCurve2.Point(
				new BigInteger("BED5AF16EA3F6A4F62938C4631EB5AF7BDBCDBC3", 16),
				new BigInteger("1667CB477A1A8EC338F94741669C976316DA6321", 16));
		ec.setGenerator(g, new BigInteger("E95E4A5F737059DC60DF5991D45029409E60FC09", 16));
	}

	@Test
	public void testDiffieHellman() {
		EllipticCurve2 ec = new EllipticCurve2(valueOf(5), valueOf(7), valueOf(17));
		ec.setGenerator(new EllipticCurve2.Point(valueOf(2), valueOf(12)), valueOf(23));

		// pick a private key
		BigInteger qa = valueOf(19);
		// calculate the public key
		EllipticCurve2.Point QA = ec.generate(qa);
		// an attacker has to log QA to guess qa
		Random r = new Random();

		for (int i = 0; i < 10; i++) {
			// pick any secret number in (1, n)
			BigInteger s = new BigInteger(ec.n.bitLength(), r).mod(ec.n);
			if (s.equals(ZERO))
				continue;

			// Get the shared secret from the public key
			EllipticCurve2.Point S1 = ec.mult(s, QA);

			// Calculate message to transmit from the curve
			EllipticCurve2.Point R = ec.generate(s);

			// transmit R - an attacker has to log R to guess S.

			// Calculate shared secret with the private key
			EllipticCurve2.Point S2 = ec.mult(qa, R);

			assertEquals(S1, S2);
		}
	}

	@Test
	public void testDiffieHellmanBigger() {
		EllipticCurve2 ec = new EllipticCurve2(valueOf(5), valueOf(7), valueOf(10007));

		// (4841, 7334) 5098 -> q not prime and has small primes dividing it
		// is weaker and cannot be used in digital signature (no inverses mod q)
		ec.setGenerator(new EllipticCurve2.Point(valueOf(4841), valueOf(7334)), valueOf(5098));

		// test that order is correct
		EllipticCurve2.Point o = new EllipticCurve2.Point(true);
		assertEquals(o, ec.generate(ec.n));

		// pick a private key
		BigInteger qa = valueOf(100);
		// calculate the public key
		EllipticCurve2.Point QA = ec.generate(qa);
		// an attacker has to log QA to guess qa
		Random r = new Random();

		for (int i = 0; i < 10; i++) {
			// pick any secret number in (1, n)
			BigInteger s = new BigInteger(ec.n.bitLength(), r).mod(ec.n);
			if (s.equals(ZERO))
				continue;

			// Get the shared secret from the public key
			EllipticCurve2.Point S1 = ec.mult(s, QA);

			// Calculate message to transmit from the curve
			EllipticCurve2.Point R = ec.generate(s);

			// transmit R - an attacker has to log R to guess S.

			// Calculate shared secret with the private key
			EllipticCurve2.Point S2 = ec.mult(qa, R);

			assertEquals(S1, S2);
		}
	}

	@Test
	public void testDiffieHellman_brainpoolP160r1() {
		/*
		 * Curve-ID: brainpoolP160r1 p: E95E4A5F737059DC60DFC7AD95B3D8139515620F
		 * A: 340E7BE2A280EB74E2BE61BADA745D97E8F7C300 B:
		 * 1E589A8595423412134FAA2DBDEC95C8D8675E58 x(P_0):
		 * BED5AF16EA3F6A4F62938C4631EB5AF7BDBCDBC3 y(P_0):
		 * 1667CB477A1A8EC338F94741669C976316DA6321 q:
		 * E95E4A5F737059DC60DF5991D45029409E60FC09 i: 1
		 */
		EllipticCurve2 ec = new EllipticCurve2(new BigInteger("340E7BE2A280EB74E2BE61BADA745D97E8F7C300", 16),
				new BigInteger("1E589A8595423412134FAA2DBDEC95C8D8675E58", 16),
				new BigInteger("E95E4A5F737059DC60DFC7AD95B3D8139515620F", 16));

		EllipticCurve2.Point g = new EllipticCurve2.Point(
				new BigInteger("BED5AF16EA3F6A4F62938C4631EB5AF7BDBCDBC3", 16),
				new BigInteger("1667CB477A1A8EC338F94741669C976316DA6321", 16));
		ec.setGenerator(g, new BigInteger("E95E4A5F737059DC60DF5991D45029409E60FC09", 16));

		// pick a private key
		BigInteger qa = valueOf(0xFFFFFF00000L);
		// calculate the corresponding public key
		EllipticCurve2.Point QA = ec.generate(qa);
		// an attacker has to log QA to guess qa

		Random r = new Random();

		for (int i = 0; i < 10; i++) {
			// pick any secret number in (1, n)
			BigInteger s = new BigInteger(ec.n.bitLength(), r).mod(ec.n);
			while (s.equals(ZERO))
				s = new BigInteger(ec.n.bitLength(), r).mod(ec.n);

			// Get the shared secret from the public key
			EllipticCurve2.Point S1 = ec.mult(s, QA);

			// Calculate message to transmit from the curve
			EllipticCurve2.Point R = ec.generate(s);

			// transmit R - an attacker has to log R to guess s -> to deduce S1

			// Calculate shared secret with the private key
			EllipticCurve2.Point S2 = ec.mult(qa, R);

			// System.out.println("S1: "+S1);
			// System.out.println("S2: "+S2);
			assertEquals(S1, S2);
		}
	}

	@Test
	public void testDigitalSignature() {

		EllipticCurve2 ec = new EllipticCurve2(valueOf(5), valueOf(7), valueOf(17));
		ec.setGenerator(new EllipticCurve2.Point(valueOf(2), valueOf(12)), valueOf(23));

		// Digital signature
		// create private-public key pair
		BigInteger qa = valueOf(19);
		EllipticCurve2.Point QA = ec.generate(qa);

		Random rand = new Random();
		// hash of the message
		BigInteger h = valueOf(18);

		for (int i = 0; i < 10; i++) {

			// pick a random k / temp key in (1, n)
			BigInteger k = null, r = null, s = null;
			EllipticCurve2.Point R1;
			do {
				k = new BigInteger(ec.n.bitLength(), rand).mod(ec.n);
				while (k.equals(ZERO))
					k = new BigInteger(ec.n.bitLength(), rand).mod(ec.n);

				R1 = ec.generate(k);
				// r = R.x % ec.n;
				r = R1.x.mod(ec.n);

				// calculate signature with private key
				// s = (h + qa * r) * ec.inverse(k, ec.n) % ec.n;
				s = h.add(qa.multiply(r)).multiply(k.modInverse(ec.n)).mod(ec.n);

				// System.out.println("signature = " + r + ", "+ s);
			} while (r.equals(ZERO) || s.equals(ZERO));
			// if or n == 0 s == 0, pick another k

			// Transmit signature and hash (h, r, s)
			// an attacker must log R to calculate k -> to calculate qa
			// TODO what do they need to forge a signature (h',r',s')

			// Recipient wants to verify signature, recalculates hash
			// and from it tries to build up "r" using public Key
			BigInteger w = s.modInverse(ec.n); // ec.inverse(s, ec.n);
			BigInteger u1 = h.multiply(w).mod(ec.n); // u1 = h * w % ec.n;
			BigInteger u2 = r.multiply(w).mod(ec.n); // u2 = r * w % ec.n;
			// P = u1*G + u2*QA
			EllipticCurve2.Point R2 = ec.add(ec.generate(u1), ec.mult(u2, QA));

			// verify that r generated is the same - same point R
			assertEquals(r, R2.x);
			assertEquals(R1, R2);
		}
	}

	@Test
	public void testDigitalSignature_brainpoolP160r1() {
		/*
		 * Curve-ID: brainpoolP160r1 p: E95E4A5F737059DC60DFC7AD95B3D8139515620F
		 * A: 340E7BE2A280EB74E2BE61BADA745D97E8F7C300 B:
		 * 1E589A8595423412134FAA2DBDEC95C8D8675E58 x(P_0):
		 * BED5AF16EA3F6A4F62938C4631EB5AF7BDBCDBC3 y(P_0):
		 * 1667CB477A1A8EC338F94741669C976316DA6321 q:
		 * E95E4A5F737059DC60DF5991D45029409E60FC09 i: 1
		 */
		EllipticCurve2 ec = new EllipticCurve2(new BigInteger("340E7BE2A280EB74E2BE61BADA745D97E8F7C300", 16),
				new BigInteger("1E589A8595423412134FAA2DBDEC95C8D8675E58", 16),
				new BigInteger("E95E4A5F737059DC60DFC7AD95B3D8139515620F", 16));

		EllipticCurve2.Point g = new EllipticCurve2.Point(
				new BigInteger("BED5AF16EA3F6A4F62938C4631EB5AF7BDBCDBC3", 16),
				new BigInteger("1667CB477A1A8EC338F94741669C976316DA6321", 16));
		ec.setGenerator(g, new BigInteger("E95E4A5F737059DC60DF5991D45029409E60FC09", 16));

		// pick a private key
		BigInteger qa = valueOf(0xFFFFFF00000L);
		// calculate the corresponding public key
		EllipticCurve2.Point QA = ec.generate(qa);
		// an attacker has to log QA to guess qa

		Random rand = new Random();
		// hash of the message
		BigInteger h = valueOf(0x0123456789AFL);

		for (int i = 0; i < 10; i++) {

			// pick a random k / temp key in (1, n)
			BigInteger k = null, r = null, s = null;
			EllipticCurve2.Point R1;
			do {
				k = new BigInteger(ec.n.bitLength(), rand).mod(ec.n);
				while (k.equals(ZERO))
					k = new BigInteger(ec.n.bitLength(), rand).mod(ec.n);

				R1 = ec.generate(k);
				// r = R.x % ec.n;
				r = R1.x.mod(ec.n);

				// calculate signature with private key
				// s = (h + qa * r) * ec.inverse(k, ec.n) % ec.n;
				s = h.add(qa.multiply(r)).multiply(k.modInverse(ec.n)).mod(ec.n);

				// System.out.println("signature = " + r + ", "+ s);
			} while (r.equals(ZERO) || s.equals(ZERO));
			// if or n == 0 s == 0, pick another k

			// Transmit signature and hash (h, r, s)
			// an attacker must log R to calculate k -> to calculate qa
			// TODO what do they need to forge a signature (h', r', s')

			// Recipient wants to verify signature, recalculates hash
			// and from it tries to build up "r" using public Key
			BigInteger w = s.modInverse(ec.n); // ec.inverse(s, ec.n);
			BigInteger u1 = h.multiply(w).mod(ec.n); // u1 = h * w % ec.n;
			BigInteger u2 = r.multiply(w).mod(ec.n); // u2 = r * w % ec.n;
			// P = u1*G + u2*QA <- public key
			EllipticCurve2.Point R2 = ec.add(ec.generate(u1), ec.mult(u2, QA));

			// verify that r generated is the same - same point R
			// System.out.println("R1: "+R1);
			// System.out.println("R2: "+R2);
			assertEquals(r, R2.x);
			assertEquals(R1, R2);
			
		}
	}
}
