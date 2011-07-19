package br.unb.cic.bionimbus.utils;

public class BioNimbusPair<F,S> {
	
	public final F first;
	public final S second;
	
	public BioNimbusPair(F first, S second){
		this.first = first;
		this.second = second;
	}
	
	public static <F,S> BioNimbusPair<F,S> of(F first, S second){
		return new BioNimbusPair<F,S>(first, second);
	}
	
	public boolean equals(Object object){
		if (this == object)
			return true;
		
		if (!(object instanceof BioNimbusPair)){
			return false;
		}
		
		BioNimbusPair<F,S> other = (BioNimbusPair<F,S>) object;
		
		return first.equals(other.first) && second.equals(other.second);
	}

}
