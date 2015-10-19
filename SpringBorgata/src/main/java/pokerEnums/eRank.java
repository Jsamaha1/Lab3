package pokerEnums;

public enum eRank {

	ACE(14),
	KING(13), 
	QUEEN(12),
	JACK(11), 
	TEN(10),
	NINE(9),
	EIGHT(8),
	SEVEN(7), 
	SIX(6), 
	FIVE(5), 
	FOUR(4), 
	THREE(3), 
	TWO(2), 
	JOKER(99);

	private eRank(final int rank){
		this.rank = rank;
	}

	private int rank;
	
	public int getRank(){
		return rank;
	}
	
}
