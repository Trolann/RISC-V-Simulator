package processor;

import java.util.HashMap;

public class Registers {

    private final HashMap<String, String> registerMap;

    public Registers() {
        this.registerMap = new HashMap<>(32);

        // Initialize general-purpose registers x1 to x31
        for (int i = 0; i <= 31; i++) {
            registerMap.put("x" + i, "00000000000000000000000000000000");
        }
        registerMap.put("pc", "00000000000000000000000000000110");
    }

    public void setRegisterValue(String registerKey, String value) {
        // Only set the value if the key exists (no new keys allowed)
        if (registerMap.containsKey(registerKey) && registerKey != "x0" && value.length() == 32) {
            registerMap.put(registerKey, value);
        }
    }


    public String getRegisterValue(String registerKey) {
        return registerMap.getOrDefault(registerKey, null);
    }

    @Override
    public String toString() {
        return registerMap.toString();
    }

}