import java.awt.Font;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Gracz {

    private final JFrame frame = new JFrame();

    private Pole[] pole = new Pole[9];
    private Pole aktualnepole;

    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    private final String IP = "localhost";
    private final int port = 56789;

    public Gracz() throws Exception {

        socket = new Socket(IP, port);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);

        JPanel planszaPanel = new JPanel();
        planszaPanel.setBackground(Color.black);
        planszaPanel.setLayout(new GridLayout(3, 3, 3, 3));
        for (int i = 0; i < pole.length; i++) {
            final int j = i;
            pole[i] = new Pole();
            pole[i].addMouseListener(new MouseAdapter() {
                //obsluga zdarzen myszy reaguje na wcisniecie przycisku
                @Override
                public void mousePressed(MouseEvent e) {
                    aktualnepole = pole[j];
                    out.println(j); //wyslanie wiadomosci do serwera z numerem pola kliknietym przez gracza
                }
            });
            planszaPanel.add(pole[i]);
        }
        frame.getContentPane().add(planszaPanel, BorderLayout.CENTER);
    }

    public void graj() throws Exception {
        String wiadomosc = in.nextLine(); // odczytanie pierwszej wiadomosci od serwera 
        char znak = wiadomosc.charAt(0); //pierwsza wiadomosc to znak gracza
        char znakPrzeciwnika = znak == 'X' ? 'O' : 'X';
        frame.setTitle("Kolko i krzyzyk " + znak);

        while (in.hasNextLine()) {
            wiadomosc = in.nextLine(); // odczytanie wiadomosci od serwera
            if (wiadomosc.startsWith("POPRAWNY")) {
                //wiadomosc zaczyna sie od "POPRAWNY" czyli gracz wykonal poprawny ruch
                aktualnepole.label.setText(znak + ""); // zmieniam pole na planszy na znak gracza
                aktualnepole.repaint(); // rysuje od nowa pole
            } else if (wiadomosc.startsWith("RUCH PRZECIWNIKA")) {
                int i = Integer.parseInt(wiadomosc.substring(16));
                pole[i].label.setText(znakPrzeciwnika + "");
                pole[i].repaint();
            } else if (wiadomosc.equals("WYGRANA")) {
                JOptionPane.showMessageDialog(frame, "WYGRALES!");
                //Wyswietlenie komunikatu o wygranej
                break;
            } else if (wiadomosc.equals("PRZEGRALES")) {
                JOptionPane.showMessageDialog(frame, "PRZEGRALES");
                //Wyswietlenie komunikatu o przegranej
                break;
            } else if (wiadomosc.equals("REMIS")) {
                JOptionPane.showMessageDialog(frame, "Remis");
                //Wyswietlenie komunikatu o remisie
                break;
            }
        }
        frame.dispose(); //Zamkniecie i wyczyszczenie okna

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Pole extends JPanel {

        JLabel label = new JLabel();

        public Pole() {
            //ustawienie rozlozenia elementow
            setLayout(new GridBagLayout());
            label.setFont(new Font("Arial", Font.BOLD, 40));
            add(label);
        }
        
    }

    public static void main(String[] args) throws Exception {
        Gracz gracz = new Gracz();
        gracz.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gracz.frame.setSize(400, 400);
        gracz.frame.setVisible(true);
        gracz.frame.setLocationRelativeTo(null);
        gracz.frame.setResizable(true);
        gracz.graj();
    }
}
