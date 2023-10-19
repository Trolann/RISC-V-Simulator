package processor;

public class Utility {
    /**
     * Adds the given integer value to a 32-bit binary string and returns
     * the result as a 32-bit binary string.
     *
     * @param binaryString A 32-bit binary string.
     * @param value The value to add.
     * @return The resulting 32-bit binary string.
     */
    public static String StringCrement(String binaryString, int value) {
        // Convert the binary string to an integer
        int intValue = Integer.parseInt(binaryString, 2);

        // Add the given value
        intValue += value;

        // Convert back to binary and pad to 32 bits
        String result = Integer.toBinaryString(intValue);
        while (result.length() < 32) {
            result = "0" + result;
        }

        // Since the result can exceed 32 bits due to addition or subtraction,
        // we need to make sure it's truncated to 32 bits
        if (result.length() > 32) {
            result = result.substring(result.length() - 32);
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(StringCrement("00000000000000000000000000000001", 1)); // Should print "00000000000000000000000000000010"
        System.out.println(StringCrement("00000000000000000000000000000010", -1)); // Should print "00000000000000000000000000000001"
        System.out.println(StringCrement("11111111111111111111111111111111", 1)); // For demonstration, results in overflow and wraps around
    }
}
