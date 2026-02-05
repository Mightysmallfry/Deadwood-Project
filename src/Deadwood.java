public class Deadwood {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java Deadwood [PlayerCount]");
            System.exit(0);
        }
        int playerCount = Integer.parseInt(args[0]);

        System.out.println("Hello and welcome to Deadwood gunslinger!");
        System.out.printf("You have %d players!", playerCount);

    }
}