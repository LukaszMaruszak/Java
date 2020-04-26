//rozwiazujac zadanie wiedze czerpalem ze strony https://cs.lmu.edu/~ray/notes/javanetexamples/?fbclid=IwAR0_z6idMxLkTN6G8wnea-HJP8d6Vu1D1njxT-kaMqlVWO89B6dd7PRuW8w
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Serwer {

    public static void main(String[] args) throws Exception {

        int port = 56789;
        //stworzone gniazdo czeka na połaczenie
        try (ServerSocket serversocket = new ServerSocket(port)) {
            System.out.println("Serwer czeka na graczy...");
            //stworzenie puli dwoch watkoch ktore beda obslugiwaly dwoch graczy
            ExecutorService pool = Executors.newFixedThreadPool(2);
            Gra gra = new Gra();
            //akceptacja polaczenia gracza z sererem i nadanie mu znaku
            pool.execute(gra.new Gracz(serversocket.accept(), 'X'));
            pool.execute(gra.new Gracz(serversocket.accept(), 'O'));
        }
    }
}

class Gra {

    private int zajetePola = 0; // bede liczyl zajete pola i jak bedzie 9 to remis
    private Gracz[] board = new Gracz[9]; //Tablica wypelniona nulami

    Gracz aktualnyGracz;
// sprawdzenie czy na planszy wystepuje wygrana kombinacja pol i nie sa to pola null
// najpierw wiersze, kolumny przekatne    

    public boolean Wygrana() {
        return (board[0] != null && board[0] == board[1] && board[0] == board[2])
                || (board[3] != null && board[3] == board[4] && board[3] == board[5])
                || (board[6] != null && board[6] == board[7] && board[6] == board[8])
                || (board[0] != null && board[0] == board[3] && board[0] == board[6])
                || (board[1] != null && board[1] == board[4] && board[1] == board[7])
                || (board[2] != null && board[2] == board[5] && board[2] == board[8])
                || (board[0] != null && board[0] == board[4] && board[0] == board[8])
                || (board[2] != null && board[2] == board[4] && board[2] == board[6]);
    }

// ruch od miejsca gdzie chce strzelic i gracza wykonujacego ruch
    public synchronized void ruch(int pozycja, Gracz gracz) {
        if (gracz != aktualnyGracz) { //ruch drugiego gracza
            throw new IllegalStateException("nie twoj ruch");
        } else if (gracz.przeciwnik == null) { //nie ma dwuch graczy 
            throw new IllegalStateException("nie ma graca");
        } else if (board[pozycja] != null) { //zajete miejsce na planszy
            throw new IllegalStateException("zajete pole");
        }
        //ustawienie gdzie strzelil gracz i zmiana gracza na przeciwnego
        board[pozycja] = aktualnyGracz;
        aktualnyGracz = aktualnyGracz.przeciwnik;
        zajetePola++; //zwiekszam po kazdym ruchu
    }

    class Gracz implements Runnable {

        char znak;
        Gracz przeciwnik;
        Socket socket; // gnaizdo do odbierania i wysylania danych
        Scanner input;
        PrintWriter output;

        public Gracz(Socket socket, char symbol) {
            this.socket = socket;
            this.znak = symbol;
        }

        @Override
        public void run() {
            try {
                input = new Scanner(socket.getInputStream());
                output = new PrintWriter(socket.getOutputStream(), true);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (znak == 'X') { //pierwszy gracz jest X
                aktualnyGracz = this;
            } else {
                przeciwnik = aktualnyGracz; // jezeli byl gracz o znaku O to czekam
                przeciwnik.przeciwnik = this;
            }
            output.println(znak);
            //Gracz w graj() odczytuje przeslany znak i wykorzystuje 
            //Jako jako znak gracza 

            //wczytywanie wyslanych wiadomosci od graczy
            while (input.hasNextLine()) {
                int strzal = 0;
                strzal = input.nextInt(); //strzal w pole na planszy od 0 do 8
                try {
                    ruch(strzal, this); //wywolanie ruch od miejsca gdzie strzelam i gracza
                    output.println("POPRAWNY");
                    przeciwnik.output.println("RUCH PRZECIWNIKA" + strzal);
                    if (Wygrana()) {
                        output.println("WYGRANA");
                        przeciwnik.output.println("PRZEGRALES");
                    } else if (zajetePola == 9) {
                        output.println("REMIS");
                        przeciwnik.output.println("REMIS");
                    }
                } catch (IllegalStateException e) {
                    output.println(e.getMessage());
                }
            }

        }

    }
}
