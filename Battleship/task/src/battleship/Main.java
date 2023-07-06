package battleship;


import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Main {
    public static final Object[][] SHIPS_TO_DRAW = {{5, "Aircraft Carrier"}, {4, "Battleship"}, {3, "Submarine"}, {3, "Cruiser"}, {2, "Destroyer"}};
    public static final int FIELD_HEIGHT = 10;
    public static final int FIELD_WIDTH = 10;
    public static final int PLAYERS = 2;
    static char[][][] battleField = new char[2][FIELD_HEIGHT][FIELD_WIDTH];
    static int[] shipsDrawn = {0, 0};
    static int[] shipsSank = {0, 0};
    static boolean gameOver = false;
    static int currentPlayer = 0;
    static Scanner scanner = new Scanner(System.in);

    protected Main() {
        for (int o = 0; o <= PLAYERS - 1; o++) {
            for (int i = 0; i < FIELD_HEIGHT; i++) {
                for (int j = 0; j < FIELD_WIDTH; j++) {
                    battleField[o][i][j] = '~';
                }
            }
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        for (int p = 1; p <= PLAYERS; p++) {
            System.out.printf("\nPlayer %d, place your ships on the game field\n\n", currentPlayer + 1);
            drawBattlefield(false, currentPlayer);
            while (shipsDrawn[currentPlayer] < SHIPS_TO_DRAW.length) {
                int i = shipsDrawn[currentPlayer];
                System.out.printf("\nEnter the coordinates of the %s (%d cells):\n", SHIPS_TO_DRAW[i][1], (Integer) SHIPS_TO_DRAW[i][0]);
                String inputCoordinates = scanner.nextLine();
                main.placeShip(reCalcCoordinates(inputCoordinates), i);
            }
            switchPlayer();
        }

        while (!gameOver) {
            drawBattlefield(true, currentPlayer == 1 ? 0 : 1);
            System.out.println("---------------------");
            drawBattlefield(false, currentPlayer);
            System.out.printf("\nPlayer %d, it's your turn:\n", currentPlayer + 1);
            shoot(scanner.nextLine());
            switchPlayer();
        }
    }

    protected static void switchPlayer() {
        System.out.println("Press Enter and pass the move to another player");
        scanner.nextLine();
        currentPlayer = currentPlayer == 0 ? 1 : 0;
    }

    protected static int[] reCalcCoordinates(String a) {
        String start = a.split(" ")[0];
        String end = a.split(" ")[1];
        return new int[]{(int) start.charAt(0) - 65, parseInt(start.substring(1)) - 1, (int) end.charAt(0) - 65, parseInt(end.substring(1)) - 1};
    }

    protected static void drawBattlefield(boolean foggy, int player) {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < FIELD_HEIGHT; i++) {
            System.out.print(Character.toString(i + 65));
            for (int j = 0; j < FIELD_WIDTH; j++) {
                System.out.print(" " + ((foggy && battleField[player][i][j] == 'O') ? "~" : battleField[player][i][j]));
            }
            System.out.println();
        }
    }

    protected static boolean shipSunk(int[] coordinates, int player) {
        int y = coordinates[0];
        int x = coordinates[1];
        int i = 1;

        while (y + i < 10 && battleField[player][y + i][x] != 'M' && battleField[player][y + i][x] != '~') {
            if (battleField[player][y + i][x] == 'O') {
                return false;
            }
            i++;
        }
        i = 1;
        while (x + i < 10 && battleField[player][y][x + i] != 'M' && battleField[player][y][x + i] != '~') {
            if (battleField[player][y][x + i] == 'O') {
                return false;
            }
            i++;
        }
        i = 1;
        while (y - i >= 0 && battleField[player][y - i][x] != 'M' && battleField[player][y - i][x] != '~') {
            if (battleField[player][y - i][x] == 'O') {
                return false;
            }
            i++;
        }
        i = 1;
        while (x - i >= 0 && battleField[player][y][x - i] != 'M' && battleField[player][y][x - i] != '~') {
            if (battleField[player][y][x - i] == 'O') {
                return false;
            }
            i++;
        }
        return true;
    }

    protected static boolean checkSurroundings(int[] coordinates) {
        int minY = Math.min(coordinates[0], coordinates[2]) == 0 ? 0 : Math.min(coordinates[0], coordinates[2]) - 1;
        int minX = Math.min(coordinates[1], coordinates[3]) == 0 ? 0 : Math.min(coordinates[1], coordinates[3]) - 1;
        int maxY = Math.max(coordinates[0], coordinates[2]) == FIELD_HEIGHT - 1 ? FIELD_HEIGHT - 1 : Math.max(coordinates[0], coordinates[2]) + 1;
        int maxX = Math.max(coordinates[1], coordinates[3]) == FIELD_WIDTH - 1 ? FIELD_WIDTH - 1 : Math.max(coordinates[1], coordinates[3]) + 1;

        for (int i = minY; i <= maxY; i++) {
            for (int j = minX; j <= maxX; j++) {
                if (battleField[currentPlayer][i][j] == 'O') {
                    return false;
                }
            }
        }
        return true;
    }

    protected void placeShip(int[] coordinates, int shipToDraw) {
        int calculatedLength = Math.max(Math.abs(coordinates[0] - coordinates[2]), Math.abs(coordinates[1] - coordinates[3])) + 1;
        if (coordinates[0] == coordinates[2] || coordinates[1] == coordinates[3]) {
            if (calculatedLength == (Integer) SHIPS_TO_DRAW[shipToDraw][0]) {
                if (checkSurroundings(coordinates)) {
                    for (int i = Math.min(coordinates[0], coordinates[2]); i <= Math.max(coordinates[0], coordinates[2]); i++) {
                        for (int j = Math.min(coordinates[1], coordinates[3]); j <= Math.max(coordinates[1], coordinates[3]); j++) {
                            battleField[currentPlayer][i][j] = 'O';
                        }
                    }
                    shipsDrawn[currentPlayer]++;
                    drawBattlefield(false, currentPlayer);
                } else {
                    System.out.println("Error! You placed it too close to another one. Try again:");
                }
            } else {
                System.out.printf("Error! Wrong length of the %s! Try again:\n", SHIPS_TO_DRAW[shipToDraw][1]);
            }
        } else {
            System.out.println("Error! Wrong ship location! Try again:");
        }
    }

    protected static void shoot(String coordinates) {
        int opponent = currentPlayer == 1 ? 0 : 1;
        int letter = coordinates.charAt(0) - 65;
        int num = parseInt(coordinates.substring(1)) - 1;
        int[] coordsArray = {letter, num};

        boolean overShotY = Math.max(0, letter) != Math.min(letter, FIELD_HEIGHT - 1);
        boolean overShotX = Math.max(0, num) != Math.min(num, FIELD_WIDTH - 1);

        if (!overShotY && !overShotX) {
            if (battleField[opponent][letter][num] == 'O' || battleField[opponent][letter][num] == 'X') {
                char previousValue = battleField[opponent][letter][num];
                battleField[opponent][letter][num] = 'X';
                if (!shipSunk(coordsArray, opponent) || previousValue == 'X') {
                    System.out.println("You hit a ship!");
                } else {
                    shipsSank[opponent]++;
                    if (shipsSank[opponent] == SHIPS_TO_DRAW.length) {
                        System.out.println("You sank the last ship. You won. Congratulations!");
                        gameOver = true;
                    } else {
                        System.out.println("You sank a ship! Specify a new target:");
                    }
                }
            } else if (battleField[opponent][letter][num] == '~' || battleField[opponent][letter][num] == 'M') {
                battleField[opponent][letter][num] = 'M';
                System.out.println("You missed!");
            }
        } else {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
        }
    }
}