import java.util.*;

public class CrosswordMaker {

    private static class Clue {
        String word;
        String hint;
        int row, col;
        boolean isHorizontal;
        int number;
        boolean solved = false;

        Clue(String word, String hint, int row, int col, boolean isHorizontal, int number) {
            this.word = word.toLowerCase();
            this.hint = hint;
            this.row = row;
            this.col = col;
            this.isHorizontal = isHorizontal;
            this.number = number;
        }
    }

    private int size;
    private char[][] grid;
    private char[][] userGrid;
    private List<Clue> clues = new ArrayList<>();
    private int clueCounter = 1;

    public CrosswordMaker(int size) {
        this.size = size;
        this.grid = new char[size][size];
        this.userGrid = new char[size][size];
        for (int i = 0; i < size; i++) {
            Arrays.fill(grid[i], '.');
            Arrays.fill(userGrid[i], '.');
        }
    }

    public boolean placeWord(String word, String clue) {
        if (clues.isEmpty()) {
            int row = size / 2;
            int col = (size - word.length()) / 2;
            if (canPlaceHorizontally(word, row, col)) {
                for (int i = 0; i < word.length(); i++) {
                    grid[row][col + i] = word.charAt(i);
                }
                clues.add(new Clue(word, clue, row, col, true, clueCounter++));
                return true;
            }
            return false;
        }

        for (Clue existing : clues) {
            String existingWord = existing.word;
            for (int i = 0; i < existingWord.length(); i++) {
                char c = existingWord.charAt(i);
                for (int j = 0; j < word.length(); j++) {
                    if (word.charAt(j) == c) {
                        int row = existing.row;
                        int col = existing.col;
                        if (existing.isHorizontal) {
                            int newRow = row - j;
                            int newCol = col + i;
                            if (canPlaceVertically(word, newRow, newCol)) {
                                for (int k = 0; k < word.length(); k++) {
                                    grid[newRow + k][newCol] = word.charAt(k);
                                }
                                clues.add(new Clue(word, clue, newRow, newCol, false, clueCounter++));
                                return true;
                            }
                        } else {
                            int newRow = row + i;
                            int newCol = col - j;
                            if (canPlaceHorizontally(word, newRow, newCol)) {
                                for (int k = 0; k < word.length(); k++) {
                                    grid[newRow][newCol + k] = word.charAt(k);
                                }
                                clues.add(new Clue(word, clue, newRow, newCol, true, clueCounter++));
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean canPlaceHorizontally(String word, int row, int col) {
        if (col < 0 || col + word.length() > size || row < 0 || row >= size) return false;
        if (col > 0 && grid[row][col - 1] != '.') return false;
        if (col + word.length() < size && grid[row][col + word.length()] != '.') return false;

        for (int i = 0; i < word.length(); i++) {
            char current = grid[row][col + i];
            if (current != '.' && current != word.charAt(i)) return false;
            if (current == '.') {
                if (row > 0 && grid[row - 1][col + i] != '.') return false;
                if (row < size - 1 && grid[row + 1][col + i] != '.') return false;
            }
        }
        return true;
    }

    private boolean canPlaceVertically(String word, int row, int col) {
        if (row < 0 || row + word.length() > size || col < 0 || col >= size) return false;
        if (row > 0 && grid[row - 1][col] != '.') return false;
        if (row + word.length() < size && grid[row + word.length()][col] != '.') return false;

        for (int i = 0; i < word.length(); i++) {
            char current = grid[row + i][col];
            if (current != '.' && current != word.charAt(i)) return false;
            if (current == '.') {
                if (col > 0 && grid[row + i][col - 1] != '.') return false;
                if (col < size - 1 && grid[row + i][col + 1] != '.') return false;
            }
        }
        return true;
    }

    public void printGameGrid() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (userGrid[i][j] != '.') {
                    System.out.print(userGrid[i][j] + " ");
                } else if (grid[i][j] != '.') {
                    System.out.print("# ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    public void playGame(Scanner scanner) {
    while (true) {
        printGameGrid();
        System.out.println("\nClues:");
        for (Clue clue : clues) {
            String dir;
            if (clue.isHorizontal) {
                dir = "Across";
            } else {
                dir = "Down";
            }

            String status;
            if (clue.solved) {
                status = "[Solved]";
            } else {
                status = "";
            }

            System.out.println(clue.number + " (" + clue.row + "," + clue.col + ") " + dir + ": " +
                    clue.hint + " (" + clue.word.length() + " letters) " + status);
        }

        System.out.print("\nEnter clue number to solve (0 to quit): ");
        int choice = scanner.nextInt();
        if (choice == 0) {
            break;
        }

        Clue clue = null;
        for (Clue c : clues) {
            if (c.number == choice) {
                clue = c;
                break;
            }
        }

        if (clue == null) {
            System.out.println("Invalid clue number.");
            continue;
        }

        if (clue.solved) {
            System.out.println("Already solved.\n");
            continue;
        }

        System.out.print("Enter your guess: ");
        String guess = scanner.next().toLowerCase();

        if (guess.equals(clue.word)) {
            System.out.println("Correct!\n");
            clue.solved = true;
            for (int i = 0; i < guess.length(); i++) {
                if (clue.isHorizontal) {
                    userGrid[clue.row][clue.col + i] = guess.charAt(i);
                } else {
                    userGrid[clue.row + i][clue.col] = guess.charAt(i);
                }
            }
        } else {
            System.out.println("Incorrect. Try again.\n");
        }
    }
}


    public static void main(String[] args) {
    CrosswordMaker game = new CrosswordMaker(30);
    Scanner scanner = new Scanner(System.in);

    String[] words = {
    "banana", "window", "market", "guitar", "planet",
    "cookie", "basket", "jungle", "rocket", "button",
    "mirror", "school", "animal", "camera", "pencil",
    "garden", "pillow", "castle", "tunnel", "summer"

    //"lantern", "anchor", "echoes", "meadow", "whistle", 
    //"cactus", "glacier", "pirate", "compass", "velvet",
    //"rocket", "goblin", "harbor", "tunnel", "nectar", 
    //"quiver", "planet", "flavor", "canvas", "saddle"

};

String[] clues = {
    "Yellow fruit monkeys love",      // banana
    "You look out of it",             // window
    "Place to shop for food",         // market
    "Instrument with strings",        // guitar
    "Earth is one of these",          // planet
    "Sweet baked treat",              // cookie
    "You carry things in it",         // basket
    "Thick forest",                   // jungle
    "Flies into space",               // rocket
    "Closes a shirt",                 // button
    "You see your reflection in it",  // mirror
    "Place for learning",             // school
    "Not a plant or person",          // animal
    "Used to take pictures",          // camera
    "Used for writing",               // pencil
    "Place with flowers",             // garden
    "You rest your head on it",       // pillow
    "A king lives here",              // castle
    "Underground passage",            // tunnel
    "Hot season of the year"          // summer

    //"A portable light source often used outdoors",          // lantern  
    //"Heavy object used to keep a ship in place",            // anchor  
    //"Repeated sound that bounces off surfaces",             // echoes  
    //"Open grassy field, often filled with wildflowers",     // meadow  
    //"High-pitched sound made by blowing air",               // whistle  
    //"Spiky plant adapted for dry deserts",                  // cactus  
    //"A massive, slow-moving river of ice",                  // glacier  
    //"Someone who sails the seas and steals treasure",       // pirate  
    //"Tool that shows direction using a magnetic needle",    // compass  
    //"Soft, smooth fabric often used in clothing",           // velvet  
    //"Vehicle used to travel into space",                    // rocket  
    //"Mischievous creature found in fantasy stories",        // goblin  
    //"Safe place along the coast where ships dock",          // harbor  
    //"Underground passage, often for cars or trains",        // tunnel  
    //"Sweet liquid that attracts bees and birds",            // nectar  
    //"Container used to carry arrows",                       // quiver  
    //"A large object that orbits a star",                    // planet  
    //"The taste of something you eat or drink",              // flavor  
    //"Sturdy fabric artists use for painting",               // canvas  
    //"Seat fastened to a horse’s back for riding"            // saddle
};

    for (int i = 0; i < words.length; i++) {
        game.placeWord(words[i], clues[i]);
    }

    game.playGame(scanner);
}

}

