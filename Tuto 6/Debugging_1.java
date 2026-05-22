public class Debugging_1 {

    public static void main(String[] args) {
        String str = initializeString("Hello World!");

        int length = str.length();
        System.out.println("The length of the string is: " + length);

        // Use i < length because the last index is length - 1.
        for (int i = 0; i < length; i++) {
            System.out.println("Character at index " + i + ": " + str.charAt(i));
        }

        int originalLength = str.length();

        // Use the original length so the loop does not grow forever.
        for (int i = 0; i < originalLength; i++) {
            str += str.charAt(i);
        }

        System.out.println(str);
    }

    // Return the initialized string because Java does not change the original reference.
    public static String initializeString(String value) {
        if (value != null && !value.equals("")) {
            return value;
        }

        throw new RuntimeException("Value of the string is either null or an empty string.");
    }
}
