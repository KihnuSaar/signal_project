public class Refactoring_1 {

    private static final String RECTANGLE = "rectangle";
    private static final String TRIANGLE = "triangle";

    public static void main(String[] args) {
        printArea(RECTANGLE, calculateRectangleArea(50, 70));
        printArea(RECTANGLE, calculateRectangleArea(60, 80));

        printArea(TRIANGLE, calculateTriangleArea(40, 120));
        printArea(TRIANGLE, calculateTriangleArea(40, 120));
    }

    // Calculates the area of a rectangle using width * height.
    public static int calculateRectangleArea(int width, int height) {
        return width * height;
    }

    // Uses 0.5 as a double, because (int) 0.5 would become 0.
    public static double calculateTriangleArea(int base, int height) {
        return 0.5 * base * height;
    }

    // Prints the area in one reusable place.
    public static void printArea(String shape, double area) {
        System.out.println("The area of the " + shape + " is: " + area);
    }
}
