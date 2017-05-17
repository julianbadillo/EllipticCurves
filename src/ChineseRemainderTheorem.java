import java.math.BigInteger;
import static java.math.BigInteger.*;
import java.util.Arrays;

import javax.management.RuntimeErrorException;

/**
 * An implementation of the Chinese Remainder Theorem
 * solution.
 * @author jbadillo
 */
public class ChineseRemainderTheorem {

	/***
	 * solves the equation system 
	 * x = a0 (mod n0)
	 * x = a1 (mod n1)
	 * ...
	 * when n0, n1, ... are coprimes
	 * @param a residues
	 * @param n moduli
	 * @return x such that 0 < x < n0*n1*...
	 * or 0 if no answer exists.
	 */
	public static long solve(long[] a, long[] n){
		
		long N = Arrays.stream(n).reduce(1L, (n1,n2) -> n1*n2);
		long x = 0;
		for (int i = 0; i < a.length; i++) {
			EuclidAlgorithm e = new EuclidAlgorithm(n[i], N/n[i]);
			// find integers   si, ti such that si*ni + ti*N/ni = 1
			if(e.r_old != 1)
				throw new RuntimeErrorException(null, "Not all n's are coprimes");
			// sum(ai*ti*N/n[i])
			x += a[i]*e.t_old*N/n[i];
		}
		
		return x;
	}
	
	/***
	 * solves the equation system 
	 * x = a0 (mod n0)
	 * x = a1 (mod n1)
	 * ...
	 * when n0, n1, ... are coprimes
	 * @param a residues
	 * @param n moduli
	 * @return x such that 0 < x < n0*n1*...
	 * or 0 if no answer exists.
	 */
	public static BigInteger solve(BigInteger[] a, BigInteger[] n){
		
		BigInteger N = Arrays.stream(n).reduce(ONE, (n1,n2) -> n1.multiply(n2));
		BigInteger x = ZERO;
		for (int i = 0; i < a.length; i++) {
			EuclidAlgorithmBI e = new EuclidAlgorithmBI(n[i], N.divide(n[i]));
			// find integers   si, ti such that si*ni + ti*N/ni = 1
			if(!e.r_old.equals(ONE))
				throw new RuntimeErrorException(null, "Not all n's are coprimes");
			// sum(ai*ti*N/n[i])
			x = x.add(a[i].multiply(e.t_old).multiply(N).divide(n[i]));
		}
		return x;
	}
	
}
