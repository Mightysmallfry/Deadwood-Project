package mothman.utils;

import java.util.Random;

public class Dice {

    // Members
    public static Dice instance;
    private static final int DEFAULT_SIDES = 6;
    private static final int DEFAULT_COUNT = 1;

    private Random randomNumberGenerator;

    // Constructors
    private Dice() {
        randomNumberGenerator = new Random();
    }

    // Methods
    public static Dice GetInstance(){
        if (instance == null) {
            instance = new Dice();
        }

        return instance;
    }

    public int Roll()
    {
        // Add 1 due to include the upper bounds
        int sum = 0;
        for (int i = 0; i < DEFAULT_COUNT; i++) {
            sum += randomNumberGenerator.nextInt(DEFAULT_SIDES) + 1;
        }
        return sum;
    }

    public int Roll(int numberOfDice){
        int sum = 0;
        // Add 1 due to include the upper bounds
        for (int i = 0; i < numberOfDice; i++) {
            sum += randomNumberGenerator.nextInt(DEFAULT_SIDES) + 1;
        }
        return sum;
    }

    /**
     * Rolls a number of dice following the 1d6 format.
     * If no number of dice is given, will default to a single die.
     * If no number of sides is given, will default to 6 sides.
     * @return sum : a summation of all rolls performed by the dice
     */
    public int Roll(int numberOfDice, int numberOfSides){
        int sum = 0;
        for (int i = 0; i < numberOfDice; i++) {
            sum += randomNumberGenerator.nextInt(numberOfSides) + 1;
        }
        return sum;
    }

    public int[] RollAsArray(){
        int[] array = new int[DEFAULT_COUNT];
        array[0] = randomNumberGenerator.nextInt(DEFAULT_SIDES) + 1;
        return array;
    }

    public int[] RollAsArray(int numberOfDice){
        int[] array = new int[numberOfDice];
        for (int i = 0; i < numberOfDice; i++) {
            array[i] = randomNumberGenerator.nextInt(DEFAULT_SIDES) + 1;
        }
        return array;
    }

    /**
     *
     * @param numberOfDice
     * @param numberOfSides
     * @return array : with each index a singular roll of the dice
     */
    public int[] RollAsArray(int numberOfDice, int numberOfSides){
        int[] array = new int[numberOfDice];
        for (int i = 0; i < numberOfDice; i++) {
            array[i] = randomNumberGenerator.nextInt(numberOfSides) + 1;
        }
        return array;
    }
}
