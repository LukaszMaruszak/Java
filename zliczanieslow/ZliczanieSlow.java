package zliczanieslow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class ZliczanieSlow {

    public static void main(String[] args) throws IOException {
        BufferedReader br = null;
        String slowo;
        int ds;
        int dlugoscSlow[] = new int[24];

        try {
            br = new BufferedReader(new FileReader("tekst.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, " ,.?!;:");
                while (st.hasMoreTokens()) {
                    slowo = st.nextToken();
                    ds = slowo.length();
                    for (int i = 0; i <= 23; i++) {
                        if (i == ds) {
                            dlugoscSlow[i]++;
                        }
                    }

                }

            }
        } finally {
            if (br != null) {
                br.close();
            }

        }
        //wypisanie histogramu
        for (int i = 1; i <= 23; i++) {
            if (dlugoscSlow[i] != 0) {
                System.out.print(i + " ");
                System.out.println(dlugoscSlow[i]);
            }
        }
    }
}
