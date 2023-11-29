package processor;

import java.util.HashMap;
import java.util.Objects;

public class Registers {

    private final HashMap<String, String> registerMap;
    public int programCounter;

    public Registers() {
        this.programCounter = 0;
        this.registerMap = new HashMap<>(32);

        // Initialize general-purpose registers x1 to x31
        for (int i = 0; i <= 31; i++) {
            registerMap.put("x" + i, Utility.ALLZEROS);
        }
        registerMap.put("pc", Utility.ALLZEROS);
    }

    public static String getRegisterString(String registerKey) {
        //Convert from binary string to decimal integer
        int registerInt = Integer.parseInt(registerKey, 2);
        String returnString = "";
        if(registerInt == 5) returnString = "t0";
        if(registerInt == 6) returnString = "t1";
        if(registerInt == 7) returnString = "t2";
        if(registerInt == 28) returnString = "t3";
        if(registerInt == 29) returnString = "t4";
        if(registerInt == 30) returnString = "t5";
        if(registerInt == 31) returnString = "t6";
        if(returnString.equals("")) returnString = "x" + registerInt;
        return returnString;
    }

    public void setRegisterValue(String registerKey, String value) {
        // Only set the value if the key exists (no new keys allowed)
        if (registerMap.containsKey(registerKey) && !Objects.equals(registerKey, "x0") && value.length() == 32) {
            registerMap.put(registerKey, value);
        }
    }


    public String getRegisterValue(String registerKey) {
        return registerMap.getOrDefault(registerKey, null);
    }

    @Override
    public String toString() {
        // For each register, print the register name and value on its own line
        StringBuilder returnString = new StringBuilder();
        for (String key : registerMap.keySet()) {
            returnString.append(key).append(": ").append(registerMap.get(key)).append("\n");
        }
        return returnString.toString();
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

}
