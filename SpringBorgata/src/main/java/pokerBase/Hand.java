package pokerBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;

import pokerEnums.eCardNo;
import pokerEnums.eHandStrength;
import pokerEnums.eRank;
import pokerEnums.eSuit;
import pokerBase.Card;
import pokerBase.exHand;

public class Hand {
	private UUID playerID;
	@XmlElement
	private ArrayList<Card> CardsInHand;
	private ArrayList<Card> BestCardsInHand;

	@XmlElement
	private int HandStrength;
	@XmlElement
	private int HiHand;
	@XmlElement
	private int LoHand;
	@XmlElement
	private ArrayList<Card> Kickers = new ArrayList<Card>();

	private boolean bScored = false;

	private boolean Flush;
	private boolean Straight;
	private boolean Ace;
	private boolean Natural = true;
	private static Deck dJoker = new Deck();

	public Hand()
	{
		
	}
	public void  AddCardToHand(Card c)
	{
		if (this.CardsInHand == null)
		{
			CardsInHand = new ArrayList<Card>();
		}
		this.CardsInHand.add(c);
	}
	
	public Card  GetCardFromHand(int location)
	{
		return CardsInHand.get(location);
	}
	
	public Hand(Deck d) {
		ArrayList<Card> Import = new ArrayList<Card>();
		for (int x = 0; x < 5; x++) {
			Import.add(d.drawFromDeck());
		}
		CardsInHand = Import;
	}

	public Hand(ArrayList<Card> setCards) {
		this.CardsInHand = setCards;
	}

	public ArrayList<Card> getCards() {
		return CardsInHand;
	}

	public ArrayList<Card> getBestHand() {
		return BestCardsInHand;
	}

	public void setPlayerID(UUID playerID)
	{
		this.playerID = playerID;
	}
	public UUID getPlayerID()
	{
		return playerID;
	}
	public void setBestHand(ArrayList<Card> BestHand) {
		this.BestCardsInHand = BestHand;
	}

	public int getHandStrength() {
		return HandStrength;
	}


	public ArrayList<Card> getKicker() {
		return Kickers;
	}

	public int getHighPairStrength() {
		return HiHand;
	}

	public int getLowPairStrength() {
		return LoHand;
	}

	public boolean getAce() {
		return Ace;
	}

	public static Hand EvalHand(ArrayList<Card> SeededHand) {
		
		Deck d = new Deck();
		Hand h = new Hand(d);
		h.CardsInHand = SeededHand;

		return h;
	}


	public void EvalHand() {
		// Evaluates if the hand is a flush and/or straight then figures out
		// the hand's strength attributes

		ArrayList<Card> remainingCards = new ArrayList<Card>();
		
		// Sort the cards!
		Collections.sort(CardsInHand, Card.CardRank);
		
		
		if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == eRank.JOKER) {
			Natural = false;
			for(eRank rank : eRank.values() ) {
				for(eSuit suit: eSuit.values()) {
					//Remove the Joker and replace it with all possible values, and replace it with new cards
					CardsInHand.remove(eCardNo.FirstCard);
					CardsInHand.add(0, new Card(suit, rank, false) );
					Collections.sort(CardsInHand, Card.CardRank);
					// Once the Joker is replaced with a new card recheck the hand. Since eRank has been altered to go from highest to lowest
					// the highest score to be achieved will be the first one to trigger and return a value.
					
					// Ace Evaluation
					if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == eRank.ACE) {
						Ace = true;
					}
					

					// Flush Evaluation
					if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand
							.get(eCardNo.SecondCard.getCardNo()).getSuit()
							&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand
									.get(eCardNo.ThirdCard.getCardNo()).getSuit()
							&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand
									.get(eCardNo.FourthCard.getCardNo()).getSuit()
							&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand
									.get(eCardNo.FifthCard.getCardNo()).getSuit()) {
						Flush = true;
					} else {
						Flush = false;
					}

					// five of a Kind

					if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.FifthCard.getCardNo()).getRank()) {
						remainingCards = null;
						ScoreHand(eHandStrength.FiveOfAKind,
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(), 0, remainingCards);
					}

					// Straight Evaluation
					else if (Ace) {
						// Looks for Ace, King, Queen, Jack, 10
						if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == eRank.KING
								&& CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == eRank.QUEEN
								&& CardsInHand.get(eCardNo.FourthCard.getCardNo())
										.getRank() == eRank.JACK
								&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TEN) {
							Straight = true;
							// Looks for Ace, 2, 3, 4, 5
						} else if (CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TWO
								&& CardsInHand.get(eCardNo.FourthCard.getCardNo())
										.getRank() == eRank.THREE
								&& CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == eRank.FOUR
								&& CardsInHand.get(eCardNo.SecondCard.getCardNo())
										.getRank() == eRank.FIVE) {
							Straight = true;
						} else {
							Straight = false;
						}
						// Looks for straight without Ace
					} else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank() == CardsInHand.get(eCardNo.SecondCard.getCardNo())
							.getRank().getRank() + 1
							&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
									.getRank() == CardsInHand
									.get(eCardNo.ThirdCard.getCardNo()).getRank().getRank() + 2
							&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
									.getRank() == CardsInHand
									.get(eCardNo.FourthCard.getCardNo()).getRank()
									.getRank() + 3
							&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
									.getRank() == CardsInHand
									.get(eCardNo.FifthCard.getCardNo()).getRank().getRank() + 4) {
						Straight = true;
					} else {
						Straight = false;
					}

					// Evaluates the hand type
					if (Straight == true
							&& Flush == true
							&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TEN
							&& Ace) {
						// CHECKS IF THE ROYAL FLUSH IS NATURAL
						if(Natural) {
							ScoreHand(eHandStrength.NaturalRoyalFlush, 0, 0, null);
						} else {
							
						ScoreHand(eHandStrength.RoyalFlush, 0, 0, null);
						}
						}

					// Straight Flush
					else if (Straight == true && Flush == true) {
						remainingCards = null;
						ScoreHand(eHandStrength.StraightFlush,
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(), 0, remainingCards);
					}
					// Four of a Kind

					else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.SecondCard.getCardNo()).getRank()
							&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
									.get(eCardNo.ThirdCard.getCardNo()).getRank()
							&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
									.get(eCardNo.FourthCard.getCardNo()).getRank()) {
						
						// CHECKS IF ITS ALSO FIVE OF A KIND
						if(CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == 
								CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank()) {
							ScoreHand(eHandStrength.FiveOfAKind,0,0,null);
						}
						else {
						remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
						ScoreHand(eHandStrength.FourOfAKind,
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(), 0,
										remainingCards);
						}
					}

					else if (CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.SecondCard.getCardNo()).getRank()
							&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == CardsInHand
									.get(eCardNo.ThirdCard.getCardNo()).getRank()
							&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == CardsInHand
									.get(eCardNo.FourthCard.getCardNo()).getRank()) {
						
						remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
						ScoreHand(eHandStrength.FourOfAKind,
								CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank()
										.getRank(), 0,
										remainingCards);
					}

					// Full House
					else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.ThirdCard.getCardNo()).getRank()
							&& CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand
									.get(eCardNo.FifthCard.getCardNo()).getRank()) {
						remainingCards = null;
						ScoreHand(eHandStrength.FullHouse,
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(),
								CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()
										.getRank(), remainingCards);
					}

					else if (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.FifthCard.getCardNo()).getRank()
							&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
									.get(eCardNo.SecondCard.getCardNo()).getRank()) {
						remainingCards = null;
						ScoreHand(eHandStrength.FullHouse,
								CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
										.getRank(),
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(), remainingCards);
					}

					// Flush
					else if (Flush) {
						remainingCards = null;
						ScoreHand(eHandStrength.Flush,
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(), 0, remainingCards);
					}

					// Straight
					else if (Straight) {
						remainingCards = null;
						ScoreHand(eHandStrength.Straight,
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(), 0, remainingCards);
					}

					// Three of a Kind
					else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.ThirdCard.getCardNo()).getRank()) {
						
						remainingCards.add(CardsInHand.get(eCardNo.FourthCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));			
						ScoreHand(eHandStrength.ThreeOfAKind,
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(), 0,
										remainingCards);
					}

					else if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.FourthCard.getCardNo()).getRank()) {
						remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));			
						
						ScoreHand(eHandStrength.ThreeOfAKind,
								CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()
										.getRank(), 0,
										remainingCards);
					} else if (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.FifthCard.getCardNo()).getRank()) {
						remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.SecondCard.getCardNo()));				
						ScoreHand(eHandStrength.ThreeOfAKind,
								CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
										.getRank(), 0,
										remainingCards);
					}

					// Two Pair
					else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.SecondCard.getCardNo()).getRank()
							&& (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand
									.get(eCardNo.FourthCard.getCardNo()).getRank())) {
						
						remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
						
						ScoreHand(eHandStrength.TwoPair,
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(),
								CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
										.getRank(),
										remainingCards);
					} else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.SecondCard.getCardNo()).getRank()
							&& (CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand
									.get(eCardNo.FifthCard.getCardNo()).getRank())) {
						
						remainingCards.add(CardsInHand.get(eCardNo.ThirdCard.getCardNo()));
						
						ScoreHand(eHandStrength.TwoPair,
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(),
								CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()
										.getRank(),
										remainingCards);
					} else if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.ThirdCard.getCardNo()).getRank()
							&& (CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand
									.get(eCardNo.FifthCard.getCardNo()).getRank())) {
						
						remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
						ScoreHand(eHandStrength.TwoPair,
								CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()
										.getRank(),
								CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()
										.getRank(),
										remainingCards);
					}

					// Pair
					else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.SecondCard.getCardNo()).getRank()) {
						
						remainingCards.add(CardsInHand.get(eCardNo.ThirdCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.FourthCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
						ScoreHand(eHandStrength.Pair,
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(), 0,
										remainingCards);
					} else if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.ThirdCard.getCardNo()).getRank()) {
						remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.FourthCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
						ScoreHand(eHandStrength.Pair,
								CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()
										.getRank(), 0,
										remainingCards);
					} else if (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.FourthCard.getCardNo()).getRank()) {
						
						remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.SecondCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
						
						ScoreHand(eHandStrength.Pair,
								CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
										.getRank(), 0,
										remainingCards);
					} else if (CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand
							.get(eCardNo.FifthCard.getCardNo()).getRank()) {
						
						remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.SecondCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.ThirdCard.getCardNo()));
						
						ScoreHand(eHandStrength.Pair,
								CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()
										.getRank(), 0,
										remainingCards);
					}

					else {
						remainingCards.add(CardsInHand.get(eCardNo.SecondCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.ThirdCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.FourthCard.getCardNo()));
						remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
						
						ScoreHand(eHandStrength.HighCard,
								CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
										.getRank(), 0,
										remainingCards);
					}
				
					
				}
			}
			
		} else {
			

		// Ace Evaluation
		if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == eRank.ACE) {
			Ace = true;
		}
		

		// Flush Evaluation
		if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand
				.get(eCardNo.SecondCard.getCardNo()).getSuit()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand
						.get(eCardNo.ThirdCard.getCardNo()).getSuit()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand
						.get(eCardNo.FourthCard.getCardNo()).getSuit()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand
						.get(eCardNo.FifthCard.getCardNo()).getSuit()) {
			Flush = true;
		} else {
			Flush = false;
		}

		// five of a Kind

		if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.FifthCard.getCardNo()).getRank()) {
			remainingCards = null;
			ScoreHand(eHandStrength.FiveOfAKind,
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(), 0, remainingCards);
		}

		// Straight Evaluation
		else if (Ace) {
			// Looks for Ace, King, Queen, Jack, 10
			if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == eRank.KING
					&& CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == eRank.QUEEN
					&& CardsInHand.get(eCardNo.FourthCard.getCardNo())
							.getRank() == eRank.JACK
					&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TEN) {
				Straight = true;
				// Looks for Ace, 2, 3, 4, 5
			} else if (CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TWO
					&& CardsInHand.get(eCardNo.FourthCard.getCardNo())
							.getRank() == eRank.THREE
					&& CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == eRank.FOUR
					&& CardsInHand.get(eCardNo.SecondCard.getCardNo())
							.getRank() == eRank.FIVE) {
				Straight = true;
			} else {
				Straight = false;
			}
			// Looks for straight without Ace
		} else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
				.getRank() == CardsInHand.get(eCardNo.SecondCard.getCardNo())
				.getRank().getRank() + 1
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
						.getRank() == CardsInHand
						.get(eCardNo.ThirdCard.getCardNo()).getRank().getRank() + 2
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
						.getRank() == CardsInHand
						.get(eCardNo.FourthCard.getCardNo()).getRank()
						.getRank() + 3
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
						.getRank() == CardsInHand
						.get(eCardNo.FifthCard.getCardNo()).getRank().getRank() + 4) {
			Straight = true;
		} else {
			Straight = false;
		}

		// Evaluates the hand type
		if (Straight == true
				&& Flush == true
				&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TEN
				&& Ace) {
			// CHECKS IF THE ROYAL FLUSH IS NATURAL
			if(Natural) {
				ScoreHand(eHandStrength.NaturalRoyalFlush, 0, 0, null);
			} else {
				
			ScoreHand(eHandStrength.RoyalFlush, 0, 0, null);
			}
			}

		// Straight Flush
		else if (Straight == true && Flush == true) {
			remainingCards = null;
			ScoreHand(eHandStrength.StraightFlush,
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(), 0, remainingCards);
		}
		// Four of a Kind

		else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
						.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
						.get(eCardNo.FourthCard.getCardNo()).getRank()) {
			
			// CHECKS IF ITS ALSO FIVE OF A KIND
			if(CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == 
					CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank()) {
				ScoreHand(eHandStrength.FiveOfAKind,0,0,null);
			}
			else {
			remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
			ScoreHand(eHandStrength.FourOfAKind,
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(), 0,
							remainingCards);
			}
		}

		else if (CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == CardsInHand
						.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == CardsInHand
						.get(eCardNo.FourthCard.getCardNo()).getRank()) {
			
			remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
			ScoreHand(eHandStrength.FourOfAKind,
					CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank()
							.getRank(), 0,
							remainingCards);
		}

		// Full House
		else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand
						.get(eCardNo.FifthCard.getCardNo()).getRank()) {
			remainingCards = null;
			ScoreHand(eHandStrength.FullHouse,
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(),
					CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()
							.getRank(), remainingCards);
		}

		else if (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.FifthCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
						.get(eCardNo.SecondCard.getCardNo()).getRank()) {
			remainingCards = null;
			ScoreHand(eHandStrength.FullHouse,
					CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
							.getRank(),
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(), remainingCards);
		}

		// Flush
		else if (Flush) {
			remainingCards = null;
			ScoreHand(eHandStrength.Flush,
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(), 0, remainingCards);
		}

		// Straight
		else if (Straight) {
			remainingCards = null;
			ScoreHand(eHandStrength.Straight,
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(), 0, remainingCards);
		}

		// Three of a Kind
		else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.ThirdCard.getCardNo()).getRank()) {
			
			remainingCards.add(CardsInHand.get(eCardNo.FourthCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));			
			ScoreHand(eHandStrength.ThreeOfAKind,
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(), 0,
							remainingCards);
		}

		else if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.FourthCard.getCardNo()).getRank()) {
			remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));			
			
			ScoreHand(eHandStrength.ThreeOfAKind,
					CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()
							.getRank(), 0,
							remainingCards);
		} else if (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.FifthCard.getCardNo()).getRank()) {
			remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.SecondCard.getCardNo()));				
			ScoreHand(eHandStrength.ThreeOfAKind,
					CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
							.getRank(), 0,
							remainingCards);
		}

		// Two Pair
		else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand
						.get(eCardNo.FourthCard.getCardNo()).getRank())) {
			
			remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
			
			ScoreHand(eHandStrength.TwoPair,
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(),
					CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
							.getRank(),
							remainingCards);
		} else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& (CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand
						.get(eCardNo.FifthCard.getCardNo()).getRank())) {
			
			remainingCards.add(CardsInHand.get(eCardNo.ThirdCard.getCardNo()));
			
			ScoreHand(eHandStrength.TwoPair,
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(),
					CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()
							.getRank(),
							remainingCards);
		} else if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& (CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand
						.get(eCardNo.FifthCard.getCardNo()).getRank())) {
			
			remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
			ScoreHand(eHandStrength.TwoPair,
					CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()
							.getRank(),
					CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()
							.getRank(),
							remainingCards);
		}

		// Pair
		else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.SecondCard.getCardNo()).getRank()) {
			
			remainingCards.add(CardsInHand.get(eCardNo.ThirdCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.FourthCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
			ScoreHand(eHandStrength.Pair,
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(), 0,
							remainingCards);
		} else if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.ThirdCard.getCardNo()).getRank()) {
			remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.FourthCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
			ScoreHand(eHandStrength.Pair,
					CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()
							.getRank(), 0,
							remainingCards);
		} else if (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.FourthCard.getCardNo()).getRank()) {
			
			remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.SecondCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
			
			ScoreHand(eHandStrength.Pair,
					CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
							.getRank(), 0,
							remainingCards);
		} else if (CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand
				.get(eCardNo.FifthCard.getCardNo()).getRank()) {
			
			remainingCards.add(CardsInHand.get(eCardNo.FirstCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.SecondCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.ThirdCard.getCardNo()));
			
			ScoreHand(eHandStrength.Pair,
					CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()
							.getRank(), 0,
							remainingCards);
		}

		else {
			remainingCards.add(CardsInHand.get(eCardNo.SecondCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.ThirdCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.FourthCard.getCardNo()));
			remainingCards.add(CardsInHand.get(eCardNo.FifthCard.getCardNo()));
			
			ScoreHand(eHandStrength.HighCard,
					CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank()
							.getRank(), 0,
							remainingCards);
		}
		
		}	
	}


	private void ScoreHand(eHandStrength hST, int HiHand, int LoHand, ArrayList<Card> kickers) {
		this.HandStrength = hST.getHandStrength();
		this.HiHand = HiHand;
		this.LoHand = LoHand;
		this.Kickers = kickers;
		this.bScored = true;

	}

	/**
	 * Custom sort to figure the best hand in an array of hands
	 */
	public static Comparator<Hand> HandRank = new Comparator<Hand>() {

		public int compare(Hand h1, Hand h2) {

			int result = 0;

			result = h2.getHandStrength() - h1.getHandStrength();

			if (result != 0) {
				return result;
			}

			result = h2.getHighPairStrength() - h1.getHighPairStrength();
			if (result != 0) {
				return result;
			}

			result = h2.getLowPairStrength() - h1.getLowPairStrength();
			if (result != 0) {
				return result;
			}

		
			if (h2.getKicker().get(eCardNo.FirstCard.getCardNo()) != null)
			{
				if (h1.getKicker().get(eCardNo.FirstCard.getCardNo()) != null)
				{
					result = h2.getKicker().get(eCardNo.FirstCard.getCardNo()).getRank().getRank() - h1.getKicker().get(eCardNo.FirstCard.getCardNo()).getRank().getRank();
				}
				if (result != 0)
				{
					return result;
				}
			}
			
			if (h2.getKicker().get(eCardNo.SecondCard.getCardNo()) != null)
			{
				if (h1.getKicker().get(eCardNo.SecondCard.getCardNo()) != null)
				{
					result = h2.getKicker().get(eCardNo.SecondCard.getCardNo()).getRank().getRank() - h1.getKicker().get(eCardNo.SecondCard.getCardNo()).getRank().getRank();
				}
				if (result != 0)
				{
					return result;
				}
			}
			if (h2.getKicker().get(eCardNo.ThirdCard.getCardNo()) != null)
			{
				if (h1.getKicker().get(eCardNo.ThirdCard.getCardNo()) != null)
				{
					result = h2.getKicker().get(eCardNo.ThirdCard.getCardNo()).getRank().getRank() - h1.getKicker().get(eCardNo.ThirdCard.getCardNo()).getRank().getRank();
				}
				if (result != 0)
				{
					return result;
				}
			}
			
			if (h2.getKicker().get(eCardNo.FourthCard.getCardNo()) != null)
			{
				if (h1.getKicker().get(eCardNo.FourthCard.getCardNo()) != null)
				{
					result = h2.getKicker().get(eCardNo.FourthCard.getCardNo()).getRank().getRank() - h1.getKicker().get(eCardNo.FourthCard.getCardNo()).getRank().getRank();
				}
				if (result != 0)
				{
					return result;
				}
			}			
				return 0;
		}
	};
	
	public static Hand PickBestHand(ArrayList<Hand> Hands)
		throws exHand {
		int currentHighScore = 0;
		int currentWinner = 0;
		ArrayList<Integer> allScores = new ArrayList<Integer>();
		// Loops through each hand and sees if the score is higher than the current highest
		for(int i = 0; i < Hands.size();i++) {
			Hands.get(i).EvalHand();
			int score = Hands.get(i).getHandStrength();
			allScores.add(score);
			if (score > currentHighScore) {
				currentWinner = i;
				currentHighScore = score;
			}
			Collections.sort(allScores);
			// If the two highest scores are the same then there is a tie and the exception is thrown
			if (allScores.get(Hands.size() - 1) == allScores.get(Hands.size()-2))
				throw new exHand(Hands.get(currentWinner));
		}
		
		return Hands.get(currentWinner);
		
	}
}
