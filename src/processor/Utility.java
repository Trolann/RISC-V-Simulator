package processor;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Utility {
    /**
     * Adds the given integer value to a 32-bit binary string and returns
     * the result as a 32-bit binary string.
     *
     * @param binaryString A 32-bit binary string.
     * @param value The value to add.
     * @return The resulting 32-bit binary string.
     */

    public static final String ALLZEROS = "00000000000000000000000000000000";
    public static final String DATA_MEMORY_ADDRESS = "00010000000000010000000000000000";

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

    public static String leftPadSigned(int result) {
        String resultBinary = Utility.leftPad(Integer.toBinaryString(result));
        if (result >= 0 ) {
            resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));
        }
        return resultBinary;
    }

    public static String leftPad(String binaryString) {
        int firstChar = Integer.parseInt(binaryString.substring(0, 1));
        while (binaryString.length() < 32) {
            binaryString = firstChar + binaryString;
        }
        
        // Return the last 32 bits
        return binaryString.substring(binaryString.length() - 32);
    }

    public static void main(String[] args) {
        System.out.println("leftPad with a 0: " + leftPad("0111"));
        System.out.println("leftPad with a 1: " + leftPad("1001"));
        System.out.println(Integer.toBinaryString(-10));
    }
}
