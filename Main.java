import java.util.Scanner;
import java.util.Random;

public class Main {
    static Scanner scan = new Scanner(System.in);

    //requires an even int that divisible by 4 as it's length and displayed Text (if none enter "")
    //outputs a header with a pattern of red and yellow *s
    public static void patternHeader(int evenlength, String text) {
        //REF: synthax of colored text www.tutorialspoin.com/how-to-print-colored-text-in-java-console#:~:text=Step%2D1%3A%20Create%20ANSI%20escape,formatting%20to%20its%20original%20condition.
        System.out.println();
        //displaying half side of the pattern (red *, yellow *)
        for (int i = 0; i < evenlength / 4; i++) {
            System.out.print("\u001B[31m*\u001B[33m*");
        }
        //display text in default color
        System.out.print("\u001B[0m\u001B[1m" + text);
        //display other half side of the pattern (yellow *, red *,)
        for (int j = 0; j < evenlength / 4; j++) {
            System.out.print("\u001B[33m*\u001B[31m*");
        }
        //reset to default and spacing
        System.out.println("\u001B[0m");
    }

    //requires a string; outputs an integer
    public static int intScanString(String text) {
        String intString = "";
        //loops until string is an int
        while (intString.isEmpty()) {
            System.out.print(text);
            //scan a string
            intString = scan.next();
            //repeat if string is not a int
            //REF: check if string is int syntax stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
            try {
                Integer.parseInt(intString);
            } catch (NumberFormatException e) {
                System.out.println("Only Integers! Try Again");
                intString = "";
            } catch (NullPointerException e) {
                System.out.println("Only Integers! Try Again");
                intString = "";
            }
        }
        //return the int of the string
        return (Integer.valueOf(intString));
    }

    //outputs a string list of all player names
    public static String[] names() {
        //prompts the user to input an int
        System.out.println("\nHow many players?");
        int numP = intScanString("- ");
        // if the int is greater than 25 or less than 1, it prompts the user to try again
        while (numP > 25 || numP < 1) {
            System.out.println("No less than 1 and more than 25 players");
            numP = intScanString("- ");
        }
        //generates a list that is the number of players long, and loops for each player it prompts for their name
        String[] pNames = new String[numP];
        for (int i = 0; i < numP; i++) {
            System.out.println("Who is player " + (i + 1) + "?");
            //use scan.next instead of scan.nextLine which would capture spaces, but it sometimes doesn't work so just scan.next
            System.out.print("- ");
            pNames[i] = coloredText(scan.next());
        }
        //returns a list player names
        return (pNames);
    }

    //requires player's name
    //outputs colored name of player's choice
    public static String coloredText(String text) {
        //REF: Syntax for colored text www.tutorialspoint.com/how-to-print-colored-text-in-java-console#:~:text=Step%2D1%3A%20Create%20ANSI%20escape,formatting%20to%20its%20original%20condition.
        String reset = "\u001B[0m";
        String choice = "";
        //loops till user inputs a valid answer
        while (choice.isEmpty()) {
            //prompts the player what color they want their name
            System.out.println("    " + text + ", what color would you like your name?\n      \033[3m(red, green, yellow, magenta, blue, lime, or none?)" + reset);
            System.out.print("    - ");
            choice = scan.next();
            //changes name to corresonding color
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
                //if user inputs invalid input, prompts user to try again
            } else if (!choice.equalsIgnoreCase("none")) {
                System.out.println("Not an option, please try again.");
                choice = "";
            }
        }
        if (choice.equalsIgnoreCase("none")) {
            choice = "";
        }
        //return new colored name
        return (choice + text + reset);
    }

    //This is the main game procedure, requiring what funds they each have and their names
    //outputs player's earnings from the game
    public static int[] game(int[] funds, String[] pNames) {
        int numP = pNames.length;
        //declares lists that are number of players long for their earnings, betting amount, and card hand total
        int[] earnings = new int[numP + 1];
        int[] betting = new int[numP];
        int[] total = new int[numP];
        // int card is the INDEX of the card in the deck list
        int card = 0;
        //declare a list with "" for each player as their string hand
        String[] hand = new String[numP];
        for (int w = 0; w < numP; w++) {
            hand[w] = "";
        }
        int dealerTotal = 0;
        //declares 2 lists of false booleans for each player
        boolean[] stand = new boolean[numP];
        boolean[] natBlackJack = new boolean[numP];
        boolean[] aceEleven = new boolean[numP + 1];
        boolean[] onlyOnce = new boolean[numP + 1];
        //declares a list of 13 spaces with 4 for each one
        int[] deck = new int[13]; // A,2,3,4,5,6,7,8,9,10,J,Q,K
        for (int t = 0; t < deck.length; t++) {
            deck[t] = 4;
        }
        //prompts a player to input their bet amount for each player
        for (int v = 0; v < numP; v++) {
            betting[v] = -1;
            System.out.println("How much is " + pNames[v] + " betting?");
            //loop to make sure user inputs valid amount
            while (betting[v] < 0) {
                //exception: if player has funds less than 1, auto sets bet amount as 1
                if (funds[v] < 1) {
                    System.out.println("    The Dealer has given " + pNames[v] + " a pity dollar to bet since " + pNames[v] + " are broke.\n");
                    betting[v] = 1;
                    break;
                }
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
        // loops twice, adds an int to player's total and to string hand for each player
        for (int r = 0; r < 2; r++) {
            for (int y = 0; y < numP; y++) {
                //randomizes an index of the deck list and -1 from deck list at index card
                card = draw(deck);
                deck[card] -= 1;
                //sets true if last card was an ace and worth 11 but only once
                if (!onlyOnce[y + 1] && card == 0) {
                    aceEleven[y + 1] = 11 == correctValue(card, 0);
                    onlyOnce[y + 1] = true;
                }
                //adds the correct value of the card to the hand and correct card to the hand
                total[y] += correctValue(card, total[y]);
                hand[y] = cardToFaces(card) + "&" + hand[y];
            }
        }
        //display string art of player's hand for each player
        for (int v = 0; v < numP; v++) {
            System.out.println(pNames[v] + "'s Initial Hand: ");
            displayHand(hand[v]);
            //if player gets 21 total, end player's turn and give 1.5x betting amount
            if (total[v] == 21) {
                patternHeader(8, "BLACKJACK");
                System.out.println("\n" + pNames[v] + " gains 1.5x what they bet");
                System.out.println("+ $" + (betting[v] * 1.5 + "\n"));
                stand[v] = true;
                natBlackJack[v] = true;
                // 1 second pause
                wait(1000);
            }
        }
        //draw a card and a hidden card and add it to dealer's string hand (? is the hidden card) and total
        card = draw(deck);
        deck[card] -= 1;
        int hiddenCard = draw(deck);
        deck[hiddenCard] -= 1;
        //sets true if hiddenCard or visible card is an ace worth 11
        if (!onlyOnce[0]) {
            aceEleven[0] = 11 == correctValue(card, 0) || 11 == correctValue(hiddenCard, 0);
            onlyOnce[0] = true;
        }
        String dealerHand = cardToFaces(card) + "&?";
        dealerTotal = correctValue(card, dealerTotal) + correctValue(hiddenCard, dealerTotal);
        System.out.println("\u001B[36mDealer\u001B[0m's Initial Hand:");
        displayHand(dealerHand);
        //prompts players if they want to double their betting amount for each player
        System.out.println("\nWould anyone like to Double Down?");
        String choice = " ";
        for (int s = 0; s < numP; s++) {
            //if the player hasn't already scored a 21 total
            if (!stand[s]) {
                System.out.println(pNames[s] + "? \u001B[3m(type yes or no)\u001B[0m");
                //loop to make sure user inputs valid option
                while (choice.equals(" ")) {
                    System.out.print("- ");
                    choice = scan.next();
                    if (choice.equalsIgnoreCase("yes")) {
                        //decks if user has enough funds to double their bet, else it denies
                        if (funds[s] >= betting[s] * 2) {
                            betting[s] = betting[s] * 2;
                            System.out.println(pNames[s] + " is now betting $" + betting[s]);
                        } else if (funds[s] < betting[s] * 2) {
                            System.out.println("Not enough funds!");
                        }
                    } else if (!choice.equalsIgnoreCase("no")) {
                        //prompts user to input a valid input and try again
                        System.out.println("Please enter a valid answer");
                        choice = " ";
                    }
                }
                choice = " ";
            }
        }
        //main game loop
        System.out.println("\u001B[36m\n- \"Start\"\u001B[0m");
        int loop = numP;
        int turnCount = 0;
        //loops until stand is true for each player
        while (loop > 0) {
            //add 1 to the turn count and displays the current turn
            turnCount += 1;
            System.out.println("\n---- TURN " + turnCount + " ----\n");
            //prompts player to hit (draw a card) or stand (stop looping) for each player
            for (int i = 0; i < numP; i++) {
                choice = " ";
                if (!stand[i]) {
                    //display's user's hand in string art form and denotes which player's hand it is
                    System.out.println(pNames[i] + "'s hand: ");
                    displayHand(hand[i]);
                    System.out.println(pNames[i] + ", Hit or Stand?");
                    //loops to prevent invalid input
                    while (choice.equalsIgnoreCase(" ")) {
                        //user inputs and draws on hit, adding to total, sets stand flag on stand to skip player
                        System.out.print("- ");
                        choice = scan.next();
                        if (choice.equalsIgnoreCase("hit")) {
                            //checks the deck and prevents an infinite loop
                            deck = refillDeck(deck, numP);
                            //a random int 0-14
                            card = draw(deck);
                            //sets true if last card was an ace and worth 11 but only once per game
                            if (!onlyOnce[i + 1] && card == 0) {
                                aceEleven[i + 1] = 11 == correctValue(card, total[i]);
                                onlyOnce[i + 1] = true;
                            }
                            //card to the player total and string hand
                            total[i] += correctValue(card, total[i]);
                            hand[i] = cardToFaces(card) + "&" + hand[i];
                            //displays drawn card in string art form
                            System.out.println(pNames[i] + " drew a:");
                            displayHand(cardToFaces(card));
                            //if player would bust but wouldnt if an A is less than 10, -10 from the total
                            if (total[i] > 21 && aceEleven[i + 1]) {
                                aceEleven[i + 1] = false;
                                total[i] -= 10;
                            }
                            //if player went over 20, end turn
                            stand[i] = lived(total[i]);
                        } else if (choice.equalsIgnoreCase("stand")) {
                            //sets off the flag that ends player's turn
                            stand[i] = true;
                            break;
                        } else {
                            //prompts user to re input a valid choice
                            System.out.println("Please enter a valid option");
                            choice = " ";
                        }
                    }
                }
            }
            //loops for each player if they stood, if so, -1 to int total amount of players (loop)
            loop = numP;
            for (int y = 0; y < numP; y++) {
                if (stand[y]) {
                    loop -= 1;
                }
            }
        }
        //dealer's turn
        System.out.println("\u001B[36m\nDealer's Turn:\u001B[0m");
        //replaces the ? in the dealer's hand to the true value of the hidden card and display dealer's hand
        dealerHand = dealerHand.replace('?', cardToFaces(hiddenCard).charAt(0));
        //if the hidden card is 10, add 0 to list to compensate for 10 being 2 char long
        if (hiddenCard == 9) {
            dealerHand = dealerHand + "0";
        }
        System.out.println("\u001B[36mDeaelr\u001B[0m's Initial Hand:");
        displayHand(dealerHand);
        //displays dealer got a natural blackjack if his total is 21
        if (dealerTotal == 21) {
            patternHeader(4, "\u001B[36mDealer\u001B[0m Got a Natural BlackJack\n");
        }
        //pause 1 second
        wait(1000);
        //displays to players the dealer's total and that dealer stands if his total is above 16
        if (dealerTotal > 16) {
            System.out.println("\u001B[36mDealer\u001B[0m Stands with a total of " + dealerTotal);
        }
        //loops until the dealer's total is above 16
        while (dealerTotal < 17) {
            //draw a card from a deck
            deck = refillDeck(deck, numP);
            card = draw(deck);
            //sets true if last card was an ace and worth 11 but only once
            if (!onlyOnce[0] && card == 0) {
                aceEleven[0] = 11 == correctValue(card, dealerTotal);
                onlyOnce[0] = true;
            }
            //add it to dealer total and string hand (in string art as well)
            dealerTotal += correctValue(card, dealerTotal);
            dealerHand = dealerHand + "&" + cardToFaces(card);
            System.out.println("\u001B[36mDealer\u001B[0m drew a ");
            displayHand(cardToFaces(card));
            //if dealer would bust but wouldnt if an A is less than 10, -10 from total
            if (dealerTotal > 21 && aceEleven[0]) {
                aceEleven[0] = false;
                dealerTotal -= 10;
            }
            //display appropriate text if dealer's total is above 16 or 21
            if (dealerTotal > 21) {
                System.out.println("\n\u001B[36mDealer \u001B[0mBUSTS");
            } else if (dealerTotal > 16) {
                System.out.println("\u001B[36mDealer \u001B[0mStands with a total of " + dealerTotal);
            }
            //pause 1 second
            wait(1000);
        }
        //pause 1 second
        wait(1000);
        //scoring
        earnings[0] = 0;
        patternHeader(28, "EARNINGS");
        System.out.println();
        //if player tied, lost, or won against the dealer, adds money to their earnings and takes from dealer's earnings for each player
        for (int g = 0; g < numP; g++) {
            System.out.println(pNames[g] + " ended with a total of " + total[g]);
            //skips players with natural blackjack
            if (!natBlackJack[g]) {
                //if both player and dealer's total is above 21 (bust) or they share the same total
                if (((total[g] > 21) && (dealerTotal > 21)) || (total[g] == dealerTotal)) {
                    System.out.println("   " + pNames[g] + " tied with dealer! Nothing Gained\n");
                    //if player's total 21 is over 21 (bust)  or their total is lower than the dealer's given he didn't exceed 21 (bust)
                } else if ((total[g] > 21) || (total[g] < dealerTotal && dealerTotal < 22)) {
                    System.out.println("   " + pNames[g] + " lost against the dealer! Lost $" + betting[g] + "\n");
                    earnings[g + 1] = -betting[g];
                    earnings[0] += betting[g];
                } else if ((total[g] < 22) && ((total[g] > dealerTotal) || dealerTotal > 21)) {
                    System.out.println("   " + pNames[g] + " won against the dealer! Won $" + betting[g] + "\n");
                    earnings[g + 1] = betting[g];
                    earnings[0] -= betting[g];
                }
            } else {
                System.out.println("   " + pNames[g] + " got a Natural Blackjack! Won $" + (betting[g] * 1.5) + "\n");
                earnings[g + 1] += (betting[g] * 0.5);
                //add 1 if betting amount was odd
                if (betting[g] % 2 == 1) {
                    earnings[g + 1] += 1;
                }
                //taking that ammount from house
                earnings[0] -= betting[g] * 0.5;
            }
        }
        System.out.println("\u001B[36mThe House\u001B[0m made $" + earnings[0]);
        //1 second pause
        wait(1000);
        //return list of players and dealer's earnings
        return (earnings);
    }

    //requires the current game's deck list
    //outputs a random int index of the deck list
    public static int draw(int[] deck) {
        Random random = new Random();
        boolean loop = true;
        int cardNum = 0;
        //loop until the random index 0-12 has > 0 in the deck list at that index
        while (loop) {
            cardNum = random.nextInt(0, 13);
            if (deck[cardNum] > 0) {
                loop = false;
            }
        }
        //returns the index of the deck list
        return (cardNum);
    }

    //requires player's card total
    //outputs a boolean if the player ends turn or not
    public static boolean lived(int total) {
        //if player's total is above 20, ends turn, and display corresponding message
        if (total > 21) {
            patternHeader(12, "BUST");
            wait(1000);
            return (true);
        } else if (total == 21) {
            patternHeader(8, "BLACKJACK");
            return (true);
        }
        //returns if the player's turn ended
        return (false);
    }

    //requires the index of the deck (card) and the player's total
    //outputs the correct value of that card at that index (+1 from the card, >9 are worth 10, and 0 are worth 1 or 11)
    public static int correctValue(int card, int total) {
        //if card is 10 and above, change to 9 (everything above 10 is a face card and thus worth 10)
        if (card > 9) {
            card = 9;
            //if card is a 0 (an Ace) and it won't cause the player to lose by making the total over 21, set card to 10
        } else if (card == 0 && total < 11) {
            card = 10;
        }
        //returns the card (the deck's index) and add 1 to make it the correct value of the card
        return (card + 1);
    }

    //requires the card, the index of the deck list
    //outputs the corresponding letter/number of the deck at the index card
    public static String cardToFaces(int card) {
        String cardStr = "";
        //if the card is above 10, convert it to the corresponding face card string
        if (card == 10) {
            cardStr = "J";
        } else if (card == 11) {
            cardStr = "Q";
        } else if (card == 12) {
            cardStr = "K";
        } else if (card == 0) {
            cardStr = "A";
            //if the card is less than 10 and not an 0, convert the int + 1 to a string
        } else {
            cardStr = String.valueOf(card + 1);
        }
        //return the new string of the card
        return (cardStr);
    }

    //requires the player's string hand (it includes & symbols)
    //outputs string art of the whole player's hand (or just 1 card)
    public static void displayHand(String hand) {
        //declares a list that is a split of the string hand at every & symbol
        String[] splitHand = hand.split("&");
        //displays a string art card side to side for each string is splitHand
        for (int i = 0; i < splitHand.length; i++) {
            System.out.print("---------  ");
        }
        System.out.println();
        for (int h = 0; h < splitHand.length; h++) {
            System.out.print("|       |  ");
        }
        System.out.println();
        //exception: if the card value is 10, it removes on space to prevent string art from being misaligned
        for (int y = 0; y < splitHand.length; y++) {
            if (splitHand[y].equals("10")) {
                System.out.print("|   10  |  ");
            } else {
                System.out.print("|   " + splitHand[y] + "   |  ");
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

    //requires deck list (the list the cards that are index are referring to) and the number of players
    //outputs if empty a fully filled list
    public static int[] refillDeck(int[] deck, int numP) {
        int total = 0;
        //count the total of the whole deck list
        for (int i = 0; i < deck.length; i++) {
            total += deck[i];
        }
        //if there aren't at least 2 cards, add 4 to deck
        if (total < 2) {
            //REF: syntax for italicized text stackoverflow.com/questions/30310147/how-to-print-an-string-variable-as-italicized-text
            System.out.println("\033[3mAdding Another Deck due to Lack of Cards\033[0m");
            //add 4 to card in the deck for each type of card
            for (int g = 0; g < deck.length; g++) {
                deck[g] += 4;
            }
        }
        //return the deck list
        return (deck);
    }

    //REF: stackoverflow.com/questions/24104313/how-do-i-make-a-delay-in-java
    //required: int of miliseconds and pauses for that long
    public static void wait(int ms) {
        try {
            //pause ms / 1000 seconds
            Thread.sleep(ms);
            //catch exception
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    //required the list of player names and players' funds
    //prints the rankings of all players, names, and funds
    public static void rank(String[] pNames, int[] funds) {
        System.out.println();
        String spacer = "  ";
        boolean[] doesContain = new boolean[pNames.length];
        //display the rank, money, and name of each player in order
        for (int i = 0; i < pNames.length; i++) {
            int greatest = -1;
            //if the player's fund is greater than the "greatest" fund and not yet used, replace it with the "greatest" for each player
            for (int g = 0; g < pNames.length; g++) {
                if (!doesContain[g] && (greatest == -1 || funds[g] > funds[greatest])) {
                    greatest = g;
                }
            }
            doesContain[greatest] = true;
            //display the rank, player, and funds for each player in order
            System.out.println(spacer + "#" + (i + 1) + " " + pNames[greatest] + ": $" + funds[greatest]);
            spacer = spacer + " ";
            //pause for 1 seconds
            wait(1000);
        }
    }

    //start and inbetween blackjack games, comes right after main menu
    public static void blackJackStart() {
        String[] pNames = names();
        //declare 1000 for funds for each player
        int[] funds = new int[pNames.length];
        patternHeader(8, "Initial Funds");
        System.out.println();
        for (int v = 0; v < pNames.length; v++) {
            funds[v] = 1000;
            System.out.println("  " + pNames[v] + ": $" + funds[v]);
        }
        patternHeader(20, "*");
        //prompt the player to enter number of rounds
        System.out.println("\n How many games are they playing?");
        int rounds = intScanString("- ");
        int[] earnings = {};
        //loop blackjack game for inputted amount rounds
        for (int o = 0; o < rounds; o++) {
            String text = " GAME " + (o + 1) + " OUT OF " + rounds + " ";
            patternHeader(8, text);
            System.out.println("Now Shuffling the Deck.");
            //set new earnings gained from each blackjack game into an earnings list
            earnings = game(funds, pNames);
            //add earnings gained to player's funds for each player
            for (int x = 0; x < pNames.length; x++) {
                funds[x] += earnings[x + 1];
            }
            //display player rankings if this isn't the final round
            if (rounds - o != 1) {
                patternHeader(24, "LEADER BOARD");
                rank(pNames, funds);
            }
            patternHeader(36, "");
            //5-second timer that displays at each second remaining time
            System.out.println("\nContinuing in: ");
            for (int j = 0; j < 5; j++) {
                System.out.print((5 - j) + " ");
                wait(1000);
            }
            System.out.println();
        }
        //if user types for no rounds, display special message
        if (rounds < 1) {
            System.out.println("\u001B[36m\"No rounds? Well okay.\"\u001B[0m");
        }
        //display final rank of all players
        patternHeader(16, "FINAL RESULTS");
        rank(pNames, funds);
        patternHeader(28, "*");
        System.out.println();
    }

    //prints answers to predeterermined answers
    public static void blackJackHelp() {
        String choice = "";
        patternHeader(8, "RULES");
        while (choice.length() >= 0) {
            //loop until user quits procedure by typing 5
            System.out.println("\nWhat would you like to know? (input corresponding number)\n1.How to win\n2.How to win a BlackJack round\n3.Value of each card");
            System.out.println("4.What moves players can make\n5.What the Dealer do\n6.Return to main menu");
            System.out.print("- ");
            choice = scan.next();
            //if user inputs 1 < x < 6, display following text, else user re inputs again
            if (choice.equals("1")) {
                System.out.println("\nPlayer with the most amount of money wins! You gain money by betting money for each round,\nearning double your bet if you win! 1.5x for a natural BlackJack");
            } else if (choice.equals("2")) {
                System.out.println("\nPlayers must compete against the dealer to get as close to 21 as your card total but not over.");
                System.out.println("If anyone goes over 21 (BUST), you lose your bet or tie if the dealer also BUST.");
                System.out.println("If the dealer BUST and you don't, you win. If you and the dealer both don't bust, whoever is closer to 21 wins (same total is a tie)");
                System.out.println("Player or dealer that is closer to 21 without a BUST wins, tying losing nothing");
            } else if (choice.equals("3")) {
                System.out.println("\nCards 2-10 are worth whatever their number is. All faces cards (J,Q,K) are worth 10.");
                System.out.println("Aces are worth 11 but if that would cause your total to go over 21 (BUST), it's worth 1.");
            } else if (choice.equals("4")) {
                System.out.println("\nAfter the initial dealing, players can choose to Double Down, which doubles their original bet.");
                System.out.println("During the playing phase players can either HIT or STAND during their turn.");
                System.out.println("HIT is drawing a card, adding the drawn card value to your total");
                System.out.println("STAND is passing and basically finalizing their total");
            } else if (choice.equals("5")) {
                System.out.println("\nThe dealer will draw at the start a face up card, and and face down card.");
                System.out.println("After all players have gone, the dealer will lift up the card, and hit till his total is above 16, than he will stand.");
            } else if (choice.equals("6")) {
                System.out.println("Returning to main menu...");
                break;
            } else {
                System.out.println("Please input a valid answer");
            }
        }
    }

    //main procedure
    public static void main(String[] args) {
        String answer = "";
        //loops till user enters a valid option or quits by inputting end
        while (answer.isEmpty()) {
            patternHeader(40, "BLACKJACK");
            //REF: syntax for italicized text stackoverflow.com/questions/30310147/how-to-print-an-string-variable-as-italicized-text
            //Display Title, choices of starting game, how to play game, or end game
            System.out.print("              BLACKJACK PAYS 3 TO 1\n\033[3m   Dealer must stand on 17 and must draw to 16\033[0m");
            //REF: Syntax for colored text www.tutorialspoint.com/how-to-print-colored-text-in-java-console#:~:text=Step%2D1%3A%20Create%20ANSI%20escape,formatting%20to%20its%20original%20condition.
            //green "start"
            System.out.println("\n\n  Type \u001B[32m\"Start\"\u001B[0m to start the game");
            //yellow "help"
            System.out.println("  Type \u001B[33m\"Help\"\u001B[0m to open up the rules of the game");
            //red "end"
            System.out.println("  Type \u001B[31m\"End\"\u001B[0m to close the game");
            patternHeader(48, "*");
            System.out.print("- ");
            //player inputs choice
            answer = scan.next();
            //start game procedure if user inputs start
            if (answer.equalsIgnoreCase("start")) {
                blackJackStart();
                answer = "";
                // starts help procedure if help is inputted
            } else if (answer.equalsIgnoreCase("Help")) {
                blackJackHelp();
                answer = "";
                //end program
            } else if (answer.equalsIgnoreCase("End")) {
                System.out.println("Ending Program...");
            } else {
                System.out.println("Please input a valid option");
                answer = "";
            }
        }
    }
}
