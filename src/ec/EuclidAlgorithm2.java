package ec;
import java.math.BigInteger;
import static java.math.BigInteger.*;

/***
 * Solves the equation a*s + b*t = gcd(a,b)
 * using the extended version of Euclid's algorithm.
 * @author jbadillo
 */
public class EuclidAlgorithm2 {

	BigInteger a;
	BigInteger b;
	BigInteger s, s_old;// where mod inverse is
	BigInteger r, r_old;// where the GCD is
	BigInteger t, t_old;
	BigInteger q;
	BigInteger gcd;
	
	/**
	 * @param a
	 * @param b
	 */
	public EuclidAlgorithm2(BigInteger a, BigInteger b) {
		this.a = a;
		this.b = b;
		run();
	}
	
	private void run(){
		s_old = ONE;
		s = ZERO;
		
		r_old = a;
		r = b;
		
		t_old = ZERO;
		t = ONE;
		
		BigInteger temp;
		while(!r.equals(ZERO)){
			q = r_old.divide(r); //r_old / r;
			temp = r;
			r = r_old.subtract(q.multiply(r));//r_old - q*r;
			r_old = temp;
			
			temp = s;
			s = s_old.subtract(q.multiply(s));//s_old - q*s;
			s_old = temp;
			
			temp = t;
			t = t_old.subtract(q.multiply(t));//t_old - q*t;
			t_old = temp;
		}
		gcd = r_old;
	}
	

}
