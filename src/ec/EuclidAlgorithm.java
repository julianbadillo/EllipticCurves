package ec;

/**
 * An implementation of extended Euclid's algorithm
 * for long.
 * @author jbadillo
 *
 */
public class EuclidAlgorithm{
	long a;
	long b;
	long s, s_old;// where mod inverse is
	long r, r_old;// where the GCD is
	long t, t_old;
	long q;
	long gcd;
	/***
	 * Solves the equation
	 * a*s + b*t = gcd(a,b)
	 * @param a
	 * @param b
	 */
	public EuclidAlgorithm(long a, long b) {
		this.a = a;
		this.b = b;
		run();
	}
	
	
	private void run(){
		s_old = 1;
		s = 0;
		
		r_old = a;
		r = b;
		
		t_old = 0;
		t = 1;
		
		long temp;
		while(r != 0){
			q = r_old / r;
			temp = r;
			r = r_old - q*r;
			r_old = temp;
			
			temp = s;
			s = s_old - q*s;
			s_old = temp;
			
			temp = t;
			t = t_old - q*t;
			t_old = temp;
		}
		gcd = r_old;
	}
}
