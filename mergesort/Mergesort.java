package mergesort;

import java.util.Arrays;

public class Mergesort extends Thread {

    public int[] tablica;
    public int lewa;
    public int prawa;

    public static void main(String[] args) throws InterruptedException {
        int rozmiar_tablicy = 100000;
        int[] tablica = new int[rozmiar_tablicy];

        for (int i = 0; i < rozmiar_tablicy; i++) {
            tablica[i] = rozmiar_tablicy - i;
        }

        test(2, tablica);
        test(3, tablica);
        test(4, tablica);
        test(5, tablica);
        test(6, tablica);
        test(7, tablica);
        test(8, tablica);
        test(9, tablica);
        test(10, tablica);
    }

    public static void test(int iloscWatkow, int[] tablica) throws InterruptedException {
        int rozmiar_tablicy = tablica.length;

        for (int i = 0; i < rozmiar_tablicy; i++) {
            tablica[i] = rozmiar_tablicy - i;
        }

        int poczatek = 0;
        int przedzial = tablica.length / iloscWatkow;

        long startTime1 = System.currentTimeMillis();

        for (int i = 1; i < iloscWatkow; i++) {
            Mergesort watek = new Mergesort();
            watek.run(poczatek, poczatek + przedzial, tablica);
            poczatek = poczatek + przedzial + 1;
            if (i == iloscWatkow) {
                mergesort(poczatek, tablica.length - 1, tablica);
                for (int k = 0; k < iloscWatkow; k++) {
                    watek.join();
                }

            }
        }

        mergesort(0, tablica.length - 1, tablica);

        System.out.println(Arrays.toString(tablica));
        long stopTime1 = System.currentTimeMillis();
        System.out.println("Watki: " + iloscWatkow + " czas pracy: " + (stopTime1 - startTime1) + " milisekund");
    }

    /* Procedura sortowania tab[pocz...kon] */
    public static void mergesort(int pocz, int kon, int[] tablica) {
        int sr;
        if (pocz < kon) {
            sr = (pocz + kon) / 2;
            mergesort(pocz, sr, tablica);    // Dzielenie lewej części
            mergesort(sr + 1, kon, tablica);   // Dzielenie prawej części
            merge(pocz, sr, kon, tablica);   // Łączenie części lewej i prawej
        }
    }

    private static void merge(int pocz, int sr, int kon, int[] tab) {
        int i, j, q;
        int[] t = new int[tab.length];
        for (i = pocz; i <= kon; i++) {
            t[i] = tab[i];  // Skopiowanie danych do tablicy pomocniczej
        }
        i = pocz;
        j = sr + 1;
        q = pocz;                 // Ustawienie wskaźników tablic
        while (i <= sr && j <= kon) {         // Przenoszenie danych z sortowaniem ze zbiorów pomocniczych do tablicy głównej
            if (t[i] < t[j]) {
                tab[q++] = t[i++];
            } else {
                tab[q++] = t[j++];
            }
        }
        while (i <= sr) {
            tab[q++] = t[i++]; // Przeniesienie nie skopiowanych danych ze zbioru pierwszego w przypadku, gdy drugi zbiór się skończył
        }
    }

//    public Mergesort(int l, int p, int[] tab) {
//        this.lewa = l;
//        this.prawa = p;
//        this.tablica = tab;
//    }
    public void run(int l, int p, int[] tab) {
        mergesort(l, p, tab);
    }

}
