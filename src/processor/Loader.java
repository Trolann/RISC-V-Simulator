import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Loader {
    private final String filePath;
    //private final HashMap<String, String> localMap;       // hashmap used for?

    public Loader(String filePath) {
        this.filePath = filePath;
    }
    
    public void loadData() {
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(filePath))) {
            int count = 0;
            String instruct = "";
            while (dataInputStream.available() > 0) {
                if (count < 4) {
                    String line = dataInputStream.readLine();
                    instruct = line + instruct;
                    //System.out.println(instruct);
                    count++;
                }
                if (count == 4) {
                    System.out.println(instruct);
                    // set instruction to memory using setters here
                    instruct = dataInputStream.readLine();
                    count = 1;
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }
