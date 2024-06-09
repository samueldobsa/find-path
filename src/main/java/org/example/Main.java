package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

// Maze class representing the maze grid
class Maze {
    private char[][] grid;
    private int rows;
    private int cols;

    public Maze(char[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
    }

    public char[][] getGrid() {
        return grid;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}

// Abstract class for input reading
abstract class AbstractFindPathInputReader {
    public abstract Maze readInput();
}

// Input reader for standard input
class FindPathInputReaderStdIn extends AbstractFindPathInputReader {
    @Override
    public Maze readInput() {
        try {
            Scanner scanner = new Scanner(System.in);
            int rows = scanner.nextInt();
            int cols = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            char[][] grid = new char[rows][cols];

            // Read and process the first line separately
            String firstLine = scanner.nextLine();
            System.out.println("Length of first line: " + firstLine.length());
            System.out.println("First line content: " + firstLine);
            if (firstLine.length() != cols) {
                System.err.println("Error: Incorrect number of columns in the first row.");
                return null;
            }
            grid[0] = firstLine.toCharArray();

            // Continue reading the remaining rows
            for (int i = 1; i < rows; i++) {
                String line = scanner.nextLine();
                if (line.length() != cols) {
                    System.err.println("Error: Incorrect number of columns in row " + (i + 1));
                    return null;
                }
                grid[i] = line.toCharArray();
            }

            scanner.close();
            return new Maze(grid);
        } catch (InputMismatchException e) {
            System.err.println("Error: Invalid input format. Please provide integers for rows and columns.");
            return null;
        }
    }
}





// Input reader for file
class FindPathInputReaderFile extends AbstractFindPathInputReader {
    private String fileName;

    public FindPathInputReaderFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Maze readInput() {
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            int rows = scanner.nextInt();
            int cols = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (rows <= 0 || cols <= 0) {
                System.err.println("Error: Invalid maze dimensions.");
                return null;
            }
            char[][] grid = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                String line = scanner.nextLine();
                if (line.length() != cols) {
                    System.err.println("Error: Incorrect number of columns in row " + (i + 1));
                    return null;
                }
                for (int j = 0; j < cols; j++) {
                    grid[i][j] = line.charAt(j);
                }
            }
            scanner.close();
            return new Maze(grid);
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found: " + fileName);
            return null;
        } catch (InputMismatchException e) {
            System.err.println("Error: Invalid input format in file: " + fileName);
            return null;
        } catch (Exception e) {
            System.err.println("Error: An unexpected error occurred while reading the file.");
            e.printStackTrace();
            return null;
        }
    }
}



// Main class containing the algorithm to find the path
public class Main {
    private static final int[] dx = {0, 0, -1, 1}; // Directions: up, down, left, right
    private static final int[] dy = {-1, 1, 0, 0};

    public static String findPath(Maze maze) {
        int startRow = -1, startCol = -1;
        int targetRow = -1, targetCol = -1;

        // Find start and target positions
        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getCols(); j++) {
                if (maze.getGrid()[i][j] == 'S') {
                    startRow = i;
                    startCol = j;
                } else if (maze.getGrid()[i][j] == 'X') {
                    targetRow = i;
                    targetCol = j;
                }
            }
        }

        // If start or target positions not found, return error
        if (startRow == -1 || startCol == -1 || targetRow == -1 || targetCol == -1) {
            return "Error: Start or target position not found.";
        }

        StringBuilder path = new StringBuilder();

        // Find path
        int currRow = startRow, currCol = startCol;
        while (currRow != targetRow || currCol != targetCol) {
            int minDist = Integer.MAX_VALUE;
            int nextRow = -1, nextCol = -1;
            for (int i = 0; i < 4; i++) {
                int newRow = currRow + dy[i];
                int newCol = currCol + dx[i];
                if (isValid(maze, newRow, newCol) && maze.getGrid()[newRow][newCol] != '#') {
                    int dist = Math.abs(newRow - targetRow) + Math.abs(newCol - targetCol);
                    if (dist < minDist) {
                        minDist = dist;
                        nextRow = newRow;
                        nextCol = newCol;
                    }
                }
            }
            if (nextRow == currRow - 1) path.append("u");
            else if (nextRow == currRow + 1) path.append("d");
            else if (nextCol == currCol - 1) path.append("l");
            else if (nextCol == currCol + 1) path.append("r");
            currRow = nextRow;
            currCol = nextCol;
        }

        return path.toString();
    }

    private static boolean isValid(Maze maze, int row, int col) {
        return row >= 0 && row < maze.getRows() && col >= 0 && col < maze.getCols();
    }

    public static void main(String[] args) {
        // Example usage
        AbstractFindPathInputReader inputReader = new FindPathInputReaderStdIn();
        Maze maze = inputReader.readInput();
        if (maze != null) {
            String path = findPath(maze);
            System.out.println("Output: " + path);
        }
    }
}
