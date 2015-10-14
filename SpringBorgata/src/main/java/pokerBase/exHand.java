package pokerBase;
import pokerBase.Hand;

public class exHand extends Exception {
	private Hand tiedHand;
	
	public exHand(Hand exception) {
		this.tiedHand = exception;
	}

}
