// BLACKJACK Simulator, allow users to bet "money" and play an x number of games
// and compete against each other
// By Isaac Shin
// Last Updated 11/4/24
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;

public class Main {
    static Scanner scan = new Scanner(System.in);

    // patternHeader requires an even int that divisible by 4 as it's length
    // and displayed Text (if none enter "")
    // Displays a header with a pattern of red and yellow *s with the text in middle
    public static void patternHeader(int evenLength, String text) {
        //REF: synthax of colored text www.tutorialspoin.com/how-to-print-colored-text-in-java-console#:~:text=Step%2D1%3A%20Create%20ANSI%20escape,formatting%20to%20its%20original%20condition.
        System.out.println();
        //displaying left side of the pattern (red *, yellow *)
        for (int i = 0; i < evenLength / 4; i++) {
            System.out.print("\u001B[31m*\u001B[33m*");
        }
        //display text in default color
        System.out.print("\u001B[0m\u001B[1m" + text);
        //display right side of the pattern (yellow *, red *,)
        for (int j = 0; j < evenLength / 4; j++) {
            System.out.print("\u001B[33m*\u001B[31m*");
        }
        //reset to default color and spacing
        System.out.println("\u001B[0m");
    }

    // intScanString converts a string into an int
    public static int intScanString(String text){
        String intString = "";
        //loops until string is a valid int
        while (intString.isEmpty()){
            System.out.print(text);
            // user inputs a string
            intString = scan.next();
            // check if string is an int
            //REF: check if string is int syntax stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
            try{
                Integer.parseInt(intString);
            } catch(NumberFormatException | NullPointerException e){
                System.out.println(" Only Integers! Try Again");
                intString = "";
            }
        }
        //return string as a valid int
        return(Integer.parseInt(intString));
    }

    // names() lets users input # of players and player names
    // output player names as a string list
    public static String[] names() {
        //prompts the user to input the # of players as an int
        System.out.println("\nHow many players?");
        int numP = intScanString("- ");
        // if the number of players is greater than 25 or less than 1, it prompts the user to try again
        while (numP > 25 || numP < 1) {
            System.out.println("No less than 1 and more than 25 players");
            numP = intScanString("- ");
        }
        //generates a list that is the number of players long
        String[] pNames = new String[numP];
        // for each player prompt them to input their name
        for (int i = 0; i < numP; i++) {
            System.out.println("Who is player " + (i + 1) + "?");
            //use scan.next instead of scan.nextLine which would capture spaces, but it sometimes doesn't work so just scan.next
            System.out.print("- ");
            // changes player name to colored text based off coloredText procedure
            pNames[i] = coloredText(scan.next());
        }
        //returns the list player names
        return (pNames);
    }

    // coloredText converts each player's name to a color based off each player's choice
    public static String coloredText(String text) {
        //REF: Syntax for colored text www.tutorialspoint.com/how-to-print-colored-text-in-java-console#:~:text=Step%2D1%3A%20Create%20ANSI%20escape,formatting%20to%20its%20original%20condition.
        String reset = "\u001B[0m";
        String choice = "";
        // loops till user inputs a valid color
        while (choice.isEmpty()) {
            // prompts the player what color they want their name
            System.out.println("    " + text + ", what color would you like your name?\n      \033[3m(red, green, yellow, magenta, blue, lime, or none?)" + reset);
            System.out.print("    - ");
            choice = scan.next();
            // changes name to corresponding color
            if (choice.equalsIgnoreCase("red")) {
                choice = "\u001B[31m";
            } else if (choice.equalsIgnoreCase("green")) {
                choice = "\u001B[32m";
            } else if (choice.equalsIgnoreCase("yellow")) {
                choice = "\u001B[33m";
            } else if (choice.equalsIgnoreCase("magenta")) {
                choice = "\u001B[35m";
            } else if (choice.equalsIgnoreCase("blue")) {
                choice = "\u001B[34m";
            } else if (choice.equalsIgnoreCase("lime")) {
                System.out.println("\n\u001B[36m- \"Lime is objectively an ugly color, so let's set your name to \u001B[35mmagenta\u001B[36m.\"" + reset);
                choice = "\u001B[35m";
            // no color, so break to get out the loop
            } else if (choice.equalsIgnoreCase("none")){
                choice = "";
                break;
            // if user inputs invalid color, prompts user to try again
            } else {
                System.out.println("Not an option, please try again.");
                choice = "";
            }
        }
        // return new colored name
        return (choice + text + reset);
    }

    // Main game procedure, looped for the number of games they play
    // takes current player name and funds, and updates funds based off
    // winnings and losses
    public static int[] game(int[] funds, String[] pNames) {
        int numP = pNames.length;
        // declares lists that are number of players long for their earnings, betting amount, and card hand total
        int[] earnings = new int[numP + 1];
        int[] betting = new int[numP];
        int[] total = new int[numP];
        // int card is the INDEX of the card in the deck list
        int card;
        // declare a list with "" for each player as their string hand,
        // allowing for manipulation
        String[] hand = new String[numP];
        Arrays.fill(hand, "");
        int dealerTotal = 0;
        // declares 4 lists of false booleans for each player
        // stand if true means player skips turn
        boolean[] stand = new boolean[numP];
        // natBlackJack means if true end turn but multiply money by 1.5
        boolean[] natBlackJack = new boolean[numP];
        // aceEleven means ace is counted as 11 currently if true
        boolean[] aceEleven = new boolean[numP + 1];
        // onlyOnce makes sure the player's ace in hand is only counted 11 only once
        boolean[] onlyOnce = new boolean[numP + 1];
        // creates a deck as an int list of 4 for each possible card type
        int[] deck = new int[13]; // A,2,3,4,5,6,7,8,9,10,J,Q,K
        Arrays.fill(deck, 4);
        // prompts each player to input their bet
        for (int v = 0; v < numP; v++) {
            betting[v] = -1;
            System.out.println("How much is " + pNames[v] + " betting?");
            // loop until user inputs a valid amount
            while (betting[v] < 0) {
                // exception: if player has funds less than 1, auto set bet amount as 1
                if (funds[v] < 1) {
                    System.out.println("    The Dealer has given " + pNames[v] + " a pity dollar to bet since " + pNames[v] + " are broke.\n");
                    betting[v] = 1;
                    break;
                }
                //prompts user to input bet
                betting[v] = intScanString("- $");
                //prompts user to re input amount if value is greater than their funds or a negative number
                if (betting[v] < 0) {
                    System.out.println("No negative Numbers! Try again");
                } else if (betting[v] > funds[v]) {
                    System.out.println("That's more money than you have! Try again");
                    betting[v] = -1;
                }
            }
        }
        patternHeader(8, "GAME START");
        System.out.println("\u001B[36mDealer\u001B[0m has Dealt all Players and Himself 2 Cards");
        // dealing starting hand
        // loops twice, adds a card to each player (which includes an int to their total and to their string hand)
        for (int r = 0; r < 2; r++) {
            for (int y = 0; y < numP; y++) {
                // randomizes an index of the deck list and -1 from deck list at index card in the procedure
                card = draw(deck);
                deck[card] -= 1;
                // sets true if last card was an ace and worth 11 but only performs this once
                if (!onlyOnce[y + 1] && card == 0) {
                    aceEleven[y + 1] = 11 == correctValue(card, 0);
                    onlyOnce[y + 1] = true;
                }
                // adds the correct value of the card to the hand and to the hand as its string value
                // ex. 11 index value = J string value = 10 card value
                total[y] += correctValue(card, total[y]);
                hand[y] = cardToFaces(card) + "&" + hand[y];
            }
        }
        // display string art of each players' hand
        for (int v = 0; v < numP; v++) {
            System.out.println(pNames[v] + "'s Initial Hand: ");
            displayHand(hand[v]);
            // if player gets 21 total in the starting hand, end player's turn and give 1.5x betting amount
            if (total[v] == 21) {
                patternHeader(8, "BLACKJACK");
                System.out.println("\n" + pNames[v] + " gains 1.5x what they bet");
                System.out.println("+ $" + (betting[v] * 1.5 + "\n"));
                stand[v] = true;
                natBlackJack[v] = true;
                // 1 second pause
                wait(2000);
            }
        }
        // for the dealer, draw a card and add it to dealer's string hand
        card = draw(deck);
        deck[card] -= 1;
        // draw a hidden card (shown to players as ?) and it's int total for the dealer
        int hiddenCard = draw(deck);
        deck[hiddenCard] -= 1;
        // sets onlyOnce for the player true if hiddenCard or visible card is an ace worth 11
        if (!onlyOnce[0]) {
            aceEleven[0] = 11 == correctValue(card, 0) || 11 == correctValue(hiddenCard, 0);
            onlyOnce[0] = true;
        }
        // add card to string hand with hidden card
        StringBuilder dealerHand = new StringBuilder(cardToFaces(card) + "&?");
        dealerTotal = correctValue(card, dealerTotal) + correctValue(hiddenCard, dealerTotal);
        // Display Dealer's hand
        System.out.println("\u001B[36mDealer\u001B[0m's Initial Hand:");
        displayHand(dealerHand.toString());
        // Prompts each player if they want to double their betting amount for each player
        System.out.println("\nWould anyone like to Double Down?");
        String choice = " ";
        for (int s = 0; s < numP; s++) {
            // only prompts player if they hadn't got a natural blackjack (21)
            if (!stand[s]) {
                System.out.println(pNames[s] + "? \u001B[3m(type yes or no)\u001B[0m");
                // loops until player enters a valid amount
                while (choice.equals(" ")) {
                    System.out.print("- ");
                    choice = scan.next();
                    if (choice.equalsIgnoreCase("yes")) {
                        // checks if user has enough funds to double their bet
                        if (funds[s] >= betting[s] * 2) {
                            betting[s] = betting[s] * 2;
                            System.out.println(pNames[s] + " is now betting $" + betting[s]);
                        // if doubled bet amount is over player's total, denies bet
                        } else if (funds[s] < betting[s] * 2) {
                            System.out.println("Not enough funds!");
                        }
                    // if player inputs an invalid input (not yes or no), prompts to try again
                    } else if (!choice.equalsIgnoreCase("no")) {
                        System.out.println("Please enter a valid answer");
                        choice = " ";
                    }
                }
                choice = " ";
            }
        }
        // Main game loop
        System.out.println("\u001B[36m\n- \"Start\"\u001B[0m");
        int loop = numP;
        int turnCount = 0;
        // loops until all players stand
        while (loop > 0) {
            // add 1 to the turn count and displays the current turn number
            turnCount += 1;
            System.out.println("\n---- TURN " + turnCount + " ----\n");
            // prompts player to hit (draw a card) or stand (stop looping) for each player
            for (int i = 0; i < numP; i++) {
                choice = " ";
                if (!stand[i]) {
                    // display's user's hand in string art form and denotes which player's hand it is
                    System.out.println(pNames[i] + "'s hand: ");
                    displayHand(hand[i]);
                    System.out.println(pNames[i] + ", Hit or Stand?");
                    // player inputs move until valid move entered
                    while (choice.equalsIgnoreCase(" ")) {
                        // player inputs move
                        System.out.print("- ");
                        choice = scan.next();
                        // player draws on move hit, adding to total, sets stand flag on stand to skip player
                        if (choice.equalsIgnoreCase("hit")) {
                            // checks the deck and prevents an infinite loop of drawing from an empty deck
                            deck = refillDeck(deck, numP);
                            // selects a random int 0-14 as long as slot in list isn't 0
                            card = draw(deck);
                            // sets onlyOnce true if last card was an ace and worth 11 but only once per game
                            if (!onlyOnce[i + 1] && card == 0) {
                                aceEleven[i + 1] = 11 == correctValue(card, total[i]);
                                onlyOnce[i + 1] = true;
                            }
                            // adds card to the player int total and string hand
                            total[i] += correctValue(card, total[i]);
                            hand[i] = cardToFaces(card) + "&" + hand[i];
                            // displays drawn card in string art form
                            System.out.println(pNames[i] + " drew a:");
                            displayHand(cardToFaces(card));
                            // if player wouldn't bust but if their Ace was 1 not 11, -10
                            // from the total instead and don't bust
                            if (total[i] > 21 && aceEleven[i + 1]) {
                                aceEleven[i + 1] = false;
                                total[i] -= 10;
                            }
                            // if player's int total went over 20, end turn by standing
                            stand[i] = lived(total[i]);
                        // ends turn if stand
                        } else if (choice.equalsIgnoreCase("stand")) {
                            // sets off the flag that ends player's turn
                            stand[i] = true;
                            break;
                        } else {
                            // prompts user to re input a valid choice
                            System.out.println("Please enter a valid option");
                            choice = " ";
                        }
                    }
                }
            }
            // creates int that is the # of players long and checks the # who stood == # of players
            loop = numP;
            for (int y = 0; y < numP; y++) {
                if (stand[y]) { loop -= 1; }
            }
        }
        // dealer's turn
        System.out.println("\u001B[36m\nDealer's Turn:\u001B[0m");
        // reveals the ? to be the hidden card
        dealerHand = new StringBuilder(dealerHand.toString().replace('?', cardToFaces(hiddenCard).charAt(0)));
        // if the hidden card is 10, add 0 to list to compensate for 10 being 2 char long
        if (hiddenCard == 9) {
            dealerHand.append("0");
        }
        // Display dealer's card
        System.out.println("\u001B[36mDeaelr\u001B[0m's Initial Hand:");
        displayHand(dealerHand.toString());
        // displays dealer got a natural blackjack if his total is 21
        if (dealerTotal == 21) {
            patternHeader(4, "\u001B[36mDealer\u001B[0m Got a Natural BlackJack");
        }
        // pause 1 second
        wait(1000);
        // displays to player's the dealer's totals and that the dealer stood if his total is above 16
        if (dealerTotal > 16) {
            System.out.println("\u001B[36mDealer\u001B[0m Stands with a total of " + dealerTotal);
        }
        // dealer draws until total is above 16
        while (dealerTotal < 17) {
            // draw a card from a deck
            deck = refillDeck(deck, numP);
            card = draw(deck);
            // sets true if last card was an ace and worth 11 but only performs this once
            if (!onlyOnce[0] && card == 0) {
                aceEleven[0] = 11 == correctValue(card, dealerTotal);
                onlyOnce[0] = true;
            }
            // add the card to dealer's int total and string hand (in string art form)
            dealerTotal += correctValue(card, dealerTotal);
            dealerHand.append("&").append(cardToFaces(card));
            System.out.println("\u001B[36mDealer\u001B[0m drew a ");
            displayHand(cardToFaces(card));
            // if dealer wouldn't bust if an Ace was 1 not 11, subtract 10 from total
            if (dealerTotal > 21 && aceEleven[0]) {
                aceEleven[0] = false;
                dealerTotal -= 10;
            }
            // display appropriate text if dealer's total is above 16 or 21
            if (dealerTotal > 21) {
                System.out.println("\n\u001B[36mDealer \u001B[0mBUSTS");
            } else if (dealerTotal > 16) {
                System.out.println("\u001B[36mDealer \u001B[0mStands with a total of " + dealerTotal);
            }
            // pause 1 second
            wait(1000);
        }
        // pause 1 second
        wait(1000);
        // Scoring
        earnings[0] = 0;
        patternHeader(28, "EARNINGS");
        System.out.println();
        // for each player, if player tied, lost, or won against the dealer, adds or subtracts money to their earnings
        // and vise versa to the dealer's earnings
        for (int g = 0; g < numP; g++) {
            // display player's ending card total
            System.out.println(pNames[g] + " ended with a total of " + total[g]);
            // skips players with natural blackjack
            if (!natBlackJack[g]) {
                // if both player and dealer's total is above 21 (bust) or they share the same total
                if (((total[g] > 21) && (dealerTotal > 21)) || (total[g] == dealerTotal)) {
                    System.out.println("   " + pNames[g] + " tied with dealer! Nothing Gained\n");
                // if player's total 21 is over 21 (bust) or their total is lower than the dealer's given
                // he didn't exceed 21 (bust)
                } else if ((total[g] > 21) || (total[g] < dealerTotal && dealerTotal < 22)) {
                    System.out.println("   " + pNames[g] + " lost against the dealer! Lost $" + betting[g] + "\n");
                    earnings[g + 1] = -betting[g];
                    earnings[0] += betting[g];
                } else if ((total[g] < 22) && ((total[g] > dealerTotal) || dealerTotal > 21)) {
                    System.out.println("   " + pNames[g] + " won against the dealer! Won $" + betting[g] + "\n");
                    earnings[g + 1] = betting[g];
                    earnings[0] -= betting[g];
                }
            // if player earns a natural blackjack, multiplies bet by 1.5
            } else {
                System.out.println("   " + pNames[g] + " got a Natural Blackjack! Won $" + (betting[g] * 1.5) + "\n");
                earnings[g + 1] += (int) (betting[g] * 0.5);
                // add 1 if betting amount was odd
                if (betting[g] % 2 == 1) {
                    earnings[g + 1] += 1;
                }
                // takes the amount from house
                earnings[0] -= (int) (betting[g] * 0.5);
            }
        }
        System.out.println("\u001B[36mThe House\u001B[0m made $" + earnings[0]);
        // 1 second pause
        wait(1000);
        // return list of players and dealer's earnings
        return (earnings);
    }

    // draw() takes the deck (an int list) and draws a random card (index) until that card is not empty (0).
    // Then returns the card number (index)
    public static int draw(int[] deck) {
        Random random = new Random();
        boolean loop = true;
        int cardNum = 0;
        // loops until the random index 0-12 has > 0 in the deck list at that index
        while (loop) {
            cardNum = random.nextInt(0, 13);
            if (deck[cardNum] > 0) {
                loop = false;
            }
        }
        // returns the index of the deck list drawn
        return (cardNum);
    }

    // lived() checks if player's card total is 21 or above, ending their turn/standing
    public static boolean lived(int total) {
        // if the player's total is above 20, ends turn, and display corresponding message
        if (total > 21) {
            patternHeader(12, "BUST");
            // pause 1 second
            wait(1000);
        } else if (total == 21) {
            patternHeader(8, "BLACKJACK");
        }
        // returns true if player total is 21 or above
        return (total >= 21);
    }

    // correctValue takes the card number (an index number of the deck list) and returns it as the correct number value
    // (+1 from the card, >9 (face cards) are worth 10, and 0 (aces) are worth 1 or 11)
    public static int correctValue(int card, int total) {
        // if card is 10 and above, change to 9 (everything above 10 is a face card and thus worth 10)
        if (card > 9) {
            card = 9;
            // if card is a 0 (an Ace) and it won't cause the player to lose by making the total over 21, set card to 10
        } else if (card == 0 && total < 11) {
            card = 10;
        }
        // returns the card (the deck's index) and add 1 to make it the correct value of the card
        return (card + 1);
    }

    // cardToFaces() takes the card number (index number of the deck list) and returns it as the correct string value
    // (ex. 0 are A, 1 are 2, 11 are Jacks, 13 are kings)
    public static String cardToFaces(int card) {
        String cardStr;
        // if the card is above 10, convert it to the corresponding face card string
        if (card == 10) {
            cardStr = "J";
        } else if (card == 11) {
            cardStr = "Q";
        } else if (card == 12) {
            cardStr = "K";
        } else if (card == 0) {
            cardStr = "A";
        // if the card is less than 10 and not an 0, convert the int + 1 to a string
        } else {
            cardStr = String.valueOf(card + 1);
        }
        // return the string value of the card
        return (cardStr);
    }

    // displayHands converts player hand string into string art form and displays it
    public static void displayHand(String hand) {
        // declares a list that is a split of the string hand at every & symbol
        String[] splitHand = hand.split("&");
        // displays a string art card side to side for each string is splitHand
        for (int i = 0; i < splitHand.length; i++) {
            System.out.print("---------  ");
        }
        System.out.println();
        for (int h = 0; h < splitHand.length; h++) {
            System.out.print("|       |  ");
        }
        System.out.println();
        // exception: if the card value is 10, it removes on space to prevent string art from being misaligned
        for (String s : splitHand) {
            if (s.equals("10")) {
                System.out.print("|   10  |  ");
            } else {
                System.out.print("|   " + s + "   |  ");
            }
        }
        System.out.println();
        for (int g = 0; g < splitHand.length; g++) {
            System.out.print("|       |  ");
        }
        System.out.println();
        for (int t = 0; t < splitHand.length; t++) {
            System.out.print("---------  ");
        }
        System.out.println();
    }

    // refillDeck takes the deck and number of players and updates the deck with 4 more for each slot if not enough
    // cards for each player
    public static int[] refillDeck(int[] deck, int numP) {
        int total = 0;
        // count the total of the whole deck list
        for (int j : deck) { total += j; }
        // if there aren't at least 2 cards total in the deck, add 4 to possible card type in the deck
        if (total < 2 * numP) {
            // REF: syntax for italicized text stackoverflow.com/questions/30310147/how-to-print-an-string-variable-as-italicized-text
            System.out.println("\033[3mAdding Another Deck due to Lack of Cards\033[0m");
            // add 4 to card in the deck for each type of card
            for (int g = 0; g < deck.length; g++) { deck[g] += 4; }
        }
        // return the deck list
        return (deck);
    }

    // REF: stackoverflow.com/questions/24104313/how-do-i-make-a-delay-in-java
    // wait() pauses the game for time milliseconds
    public static void wait(int ms) {
        try {
            //pause ms / 1000 seconds
            Thread.sleep(ms);
            //catch exception
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    // rank() displays a ranking of all player's earnings/fund highest to lowest utilizing each player's name and funds
    public static void rank(String[] pNames, int[] funds) {
        System.out.println();
        StringBuilder spacer = new StringBuilder("  ");
        int greatest = 0;
        // flags if the player has already been ranked
        boolean[] doesContain = new boolean[pNames.length];
        // display the rank, money, and name of each player in order
        for (int i = 0; i < pNames.length; i++) {
            greatest = -1;
            // if the player's fund is greater than the "greatest" fund and not yet used (flagged in doesContain),
            // replace the greatest int with their index for each player
            for (int g = 0; g < pNames.length; g++) {
                if (!doesContain[g] && (greatest == -1 || funds[g] > funds[greatest])) {
                    greatest = g;
                }
            }
            // flags that the player has been ranked and removes them from being ranked again
            doesContain[greatest] = true;
            // display the rank, player, and funds of the player
            System.out.println(spacer + "#" + (i + 1) + " " + pNames[greatest] + ": $" + funds[greatest]);
            spacer.append(" ");
            // pause for 1 seconds
            wait(1000);
        }
        // chastises the lowest ranked player
        System.out.println("\n- " + pNames[greatest] + ", I'm sorely disappointed in you.");
    }

    // executed once user hits starts on the main menu. Allows for setting player names, # of games, and showing
    // the final ending leaderboard
    public static void blackJackStart() {
        // declare player names list and funds list
        String[] pNames = names();
        int[] funds = new int[pNames.length];
        patternHeader(8, "Initial Funds");
        System.out.println();
        // declare 1000 for funds for each player and displaying it
        for (int v = 0; v < pNames.length; v++) {
            funds[v] = 1000;
            System.out.println("  " + pNames[v] + ": $" + funds[v]);
        }
        patternHeader(20, "*");
        // prompt the player to enter number of rounds
        System.out.println("\n How many games are they playing?");
        int rounds = intScanString("- ");
        int[] earnings;
        // loops blackjack game for the number of rounds inputted
        for (int o = 0; o < rounds; o++) {
            // display which game they are on out of total
            String text = " GAME " + (o + 1) + " OUT OF " + rounds + " ";
            patternHeader(8, text);
            System.out.println("Now Shuffling the Deck.");
            // set new earnings gained from each blackjack game into an earnings list
            earnings = game(funds, pNames);
            // add earnings gained to player's funds for each player
            for (int x = 0; x < pNames.length; x++) {
                funds[x] += earnings[x + 1];
            }
            // display player rankings if this isn't the final round
            if (rounds - o != 1) {
                patternHeader(24, "LEADER BOARD");
                rank(pNames, funds);
            }
            patternHeader(36, "");
            // 5-second countdown that displays at each second remaining time
            System.out.println("\nContinuing in: ");
            for (int j = 0; j < 5; j++) {
                System.out.print((5 - j) + " ");
                wait(1000);
            }
            System.out.println();
        }
        // if user inputs for no rounds, display special message
        if (rounds < 1) {
            System.out.println("\u001B[36m\"No rounds? Well okay.\"\u001B[0m");
        }
        // display final ranking of all players
        patternHeader(16, "FINAL RESULTS");
        rank(pNames, funds);
        patternHeader(28, "*");
        System.out.println();
    }

    // prints answers to predeterermined answers
    public static void blackJackHelp() {
        String choice = "";
        patternHeader(8, "RULES");
        label:
        while (choice.isEmpty()) {
            // loops until user quits procedure by typing 5
            System.out.println("What would you like to know? (input corresponding number)\n1.How to win\n2.How to win a BlackJack round\n3.Value of each card\n4.What moves players can make\n5.What the Dealer do\n6.Return to main menu");
            System.out.print("- ");
            choice = scan.next();
            // if user inputs 1 < x < 6, display following text, else prompts user to re input again
            switch (choice) {
                case "1":
                    System.out.println("\nPlayer with the most amount of money wins! You gain money by betting money for each round,\nearning double your bet if you win! 1.5x for a natural BlackJack");
                    break;
                case "2":
                    System.out.println("\nPlayers must compete against the dealer to get as close to 21 as your card total but not over.");
                    System.out.println("If anyone goes over 21 (BUST), you lose your bet or tie if the dealer also BUST.\nIf the dealer BUST and you don't, you win. If you and the dealer both don't bust, whoever is closer to 21 wins (same total is a tie)");
                    System.out.println("Player or dealer that is closer to 21 without a BUST wins, tying losing nothing");
                    break;
                case "3":
                    System.out.println("\nCards 2-10 are worth whatever their number is. All faces cards (J,Q,K) are worth 10.\nAces are worth 11 but if that would cause your total to go over 21 (BUST), it's worth 1.");
                    break;
                case "4":
                    System.out.println("\nAfter the initial dealing, players can choose to Double Down, which doubles their original bet.\nDuring the playing phase players can either HIT or STAND during their turn\nHIT is drawing a card, adding the drawn card value to your total\nSTAND is passing and basically finalizing their total");
                    break;
                case "5":
                    System.out.println("\nThe dealer will draw at the start a face up card, and and face down card.\nAfter all players have gone, the dealer will lift up the card, and hit till his total is above 16, than he will stand.");
                    break;
                case "6":
                    System.out.println("Returning to main menu...");
                    break label;
                default:
                    System.out.println("Please input a valid answer");
                    break;
            }
        }
    }

    // main procedure; main menu
    public static void main(String[] args) {
        String answer = "";
        // loops till user enters a valid option or quits by inputting end
        while (answer.isEmpty()) {
            patternHeader(40, "BLACKJACK");
            // REF: syntax for italicized text stackoverflow.com/questions/30310147/how-to-print-an-string-variable-as-italicized-text
            // Display Title, choices of starting game, how to play game, or end game
            System.out.print("              BLACKJACK PAYS 3 TO 1\n\033[3m   Dealer must stand on 17 and must draw to 16\033[0m");
            // REF: Syntax for colored text www.tutorialspoint.com/how-to-print-colored-text-in-java-console#:~:text=Step%2D1%3A%20Create%20ANSI%20escape,formatting%20to%20its%20original%20condition.
            // green "start"
            System.out.println("\n\n  Type \u001B[32m\"Start\"\u001B[0m to start the game");
            // yellow "help"
            System.out.println("  Type \u001B[33m\"Help\"\u001B[0m to open up the rules of the game");
            // red "end"
            System.out.println("  Type \u001B[31m\"End\"\u001B[0m to close the game");
            patternHeader(48, "*");
            System.out.print("- ");
            // player inputs choice
            answer = scan.next();
            // start game procedure if user inputs "start"
            if (answer.equalsIgnoreCase("start")) {
                blackJackStart();
                answer = "";
            // starts help procedure if typed "help"
            } else if (answer.equalsIgnoreCase("Help")) {
                blackJackHelp();
                answer = "";
            // end program if typed "end"
            } else if (answer.equalsIgnoreCase("End")) {
                System.out.println("Ending Program...");
            } else {
                System.out.println("Please input a valid option");
                answer = "";
            }
        }
    }
}
