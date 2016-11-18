package gui.client;

import java.io.IOException;
import java.io.InputStream;

/**
 * Lukas Franke
 */
public class Utils
{
    public static String TextFromInputstream(InputStream in)
    {
        String result = "";

        try
        {
            if (in.available() <= 0)
                return result;

            int i = in.read();

            while (in.available() > 0)
            {
                result += (char) i;
                i = in.read();
            }

            // Append the last read char before we leave
            result += (char) i;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }
    
    public static String[] SplitInput(String message)
    {
        String[] orders = message.split("\n");
        return RemoveBOM(orders);
    }
    
    public static String[] RemoveBOM(String[] input)
    {
        for (int i = 0; i < input.length; i++)
        {
            // Remove the \n BOM
            input[i] = input[i].substring(0, input[i].length() - 1);
        }
        return input;
    }
}
