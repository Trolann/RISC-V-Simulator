package processor;

import java.util.HashMap;
import java.util.Map;

public class Registers {

    private final HashMap<String, String> registerMap;
    private final HashMap<String, String> aliasMap;
    //public int programCounter;

    public Registers() {
        //this.programCounter = 0;
        this.registerMap = new HashMap<>(32);
        this.aliasMap = new HashMap<>();

        // Initialize general-purpose registers x0 to x31 and their aliases
        initializeRegistersAndAliases();

        // Set program counter 'pc' to all zeros
        registerMap.put("pc", Utility.ALLZEROS);
    }

    private void initializeRegistersAndAliases() {
        String[] aliases = {"zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0/fp", "s1", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"};
        for (int i = 0; i <= 31; i++) {
            String registerKey = "x" + i;
            registerMap.put(registerKey, Utility.ALLZEROS);
            aliasMap.put(registerKey, aliases[i]);
        }
    }

    // Get register string based on integer
    public String getRegisterString(int registerInt) {
        return "x" + registerInt;
    }

    // Set register value based on integer
    public void setRegisterValue(int registerInt, String value) {
        String registerKey = getRegisterString(registerInt);
        setRegisterValue(registerKey, value);
    }

    // Get register value based on integer
    public String getRegisterValue(int registerInt) {
        String registerKey = getRegisterString(registerInt);
        return getRegisterValue(registerKey);
    }

    public void setRegisterValue(String registerKey, String value) {
        if (registerMap.containsKey(registerKey) && !registerKey.equals("x0") && value.length() == 32) {
            registerMap.put(registerKey, value);
        }
    }

    public String getRegisterValue(String registerKey) {
        return registerMap.getOrDefault(registerKey, Utility.ALLZEROS);
    }

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i <= 31; i++) {
            String key = "x" + i;
            String alias = aliasMap.get(key);
            returnString.append(key).append("(").append(alias).append("): ").append(registerMap.get(key)).append("\n");
        }
        return returnString.toString();
    }

    public String getProgramCounter() {
        return registerMap.get("pc");
    }

    public void setProgramCounter(int programCounter) {
        String localPcValue = Utility.StringCrement(Utility.ALLZEROS, programCounter);
        registerMap.put("pc", localPcValue);
    }

    public void setProgramCounter(String programCounter) {
        String localPcValue = programCounter;

        // If not 32 bits, padd left with 0s
        while (localPcValue.length() < 32) {
            localPcValue = "0" + localPcValue;
        }

        registerMap.put("pc", localPcValue);
    }
}
