/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calkaoznaczona;

public abstract class CalkaOznaczona {

 
    public abstract double Funkcja(double x);

    public double LiczCalke(double a, double b) {
        if(a > b)
        {
            System.out.println("Wybrany niewlasciwy przedzial");
            return 0;
        }
        //Liczba na ile trapezow zostaje podzielone pole pod wykresem
        double n = 1e6;
        double h; // długosc jednego podziału
        double calka = 0;
        h = (b - a) / n;
        System.out.println("Przedzaił: " + h);
        //Suma trapezow bez pierwszego i ostatniego w punktach podzialu
        for (int i = 1; i < n; i++) {
            calka += Funkcja(a + i * h);
        }
        System.out.println("srodek:  " + calka);
        // Dodanie podzielonej przez 2 wartosci funkcji na poczatku i koncu przedzialu
        calka += (Funkcja(a) + Funkcja(b) / 2);
        System.out.println("srod plus brzegi: " + calka );
        // Pole figury zlozonej z trapezow
        calka *= h;
        System.out.println("wynik: " + calka);
        return calka;
    }

    public static void main(String[] args) {
        if(args.length < 2)
        {
            System.out.println("Brak argumentow");
        }
        CalkaOznaczona S = new Funkcja();

        System.out.println(S.LiczCalke(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
        
    }

}
