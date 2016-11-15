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

            // Check for special chars
            if (i == 0)
                return ReadUnicode(in);

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

    public static String ReadUnicode(InputStream in)
    {
        // TODO
        return "";
    }
}
