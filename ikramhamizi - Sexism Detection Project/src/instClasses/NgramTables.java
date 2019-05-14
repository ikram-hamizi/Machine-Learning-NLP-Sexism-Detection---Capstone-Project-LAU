package instClasses;

import java.util.Hashtable;

public class NgramTables {

	private java.util.Hashtable<String, Hashtable<String, MutableInt>> ngram; //ngram
	private Hashtable<String, MutableInt> n_1gram; //n-1gram
	
	public NgramTables()
	{
		
	}
	public NgramTables(java.util.Hashtable<String, Hashtable<String, MutableInt>> ngram, Hashtable<String, MutableInt> n_1gram)
	{
		this.ngram = ngram;
		this.n_1gram = n_1gram;
	}
	
	public java.util.Hashtable<String, Hashtable<String, MutableInt>> getNgram() {
		return ngram;
	}
	
	public Hashtable<String, MutableInt> getN_1gram() {
		return n_1gram;
	}
	
	public void setNgram(java.util.Hashtable<String, Hashtable<String, MutableInt>> ngram) {
		this.ngram = ngram;
	}
	
	public void setN_1gram(Hashtable<String, MutableInt> n_1gram) {
		this.n_1gram = n_1gram;
	}
}
