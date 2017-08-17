package ec;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import ec.ChineseRemainderTheorem;

public class ChineseRemainderTheoremTest {

	@Test
	public void test() {
		long [] a = {2, 3, 2};
		long [] n = {3, 5, 7};
		long x = ChineseRemainderTheorem.solve(a, n);
		// solution
		assertEquals(23, x);
	}

	
	@Test
	public void test2() {
		long [] a = {2, 3, 2, 4};
		long [] n = {3, 5, 7, 11};
		long x = ChineseRemainderTheorem.solve(a, n);
		// validate the equation holds
		for (int i = 0; i < n.length; i++)
			assertEquals(x % n[i], a[i]);
	}
	
	@Test
	public void test3() {
		long [] a = {20, 35, 42, 12};
		long [] n = {43, 57, 79, 97};
		long x = ChineseRemainderTheorem.solve(a, n);
		// validate the equation holds
		for (int i = 0; i < n.length; i++)
			assertEquals(x % n[i], a[i]);
	}
	
	@Test
	public void testBI() {
		BigInteger[] a = {BigInteger.valueOf(20), BigInteger.valueOf(35), BigInteger.valueOf(42), BigInteger.valueOf(12)};
		BigInteger [] n = {BigInteger.valueOf(43), BigInteger.valueOf(57), BigInteger.valueOf(79), BigInteger.valueOf(97)};
		BigInteger x = ChineseRemainderTheorem.solve(a, n);
		
		// validate the equation holds
		for (int i = 0; i < n.length; i++)
			assertEquals(x.mod(n[i]), a[i]);
	}
	
	@Test
	public void testBI2() {
		BigInteger[] a = new BigInteger[10];
		BigInteger [] n = new BigInteger[10];
		
		// fill with random numbers 160 bit
		Random r = new Random();
		for (int i = 0; i < n.length; i++) {
			// a prime
			n[i] = BigInteger.probablePrime(160, r);
			// lower than a prime
			a[i] = new BigInteger(160, r).mod(n[i]);
		}
	
		BigInteger x = ChineseRemainderTheorem.solve(a, n);
		// validate the equation holds
		for (int i = 0; i < n.length; i++)
			assertEquals(x.mod(n[i]), a[i]);
	}
}
