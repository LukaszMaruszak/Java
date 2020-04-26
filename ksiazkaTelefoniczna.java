package ksiazkaTelefoniczna;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ksiazkaTelefoniczna extends JFrame implements ActionListener {

	public static Connection c;
	public JButton pokaz;
	public JButton dodaj;
	public JButton usun;
	static JTextField tfname;
	static JTextField tfsurname;
	static JTextField tfnumber;

	public ksiazkaTelefoniczna() {

		JFrame f = new JFrame("Ksiazka telefoniczna");
		tfname = new JTextField("Imie", 16);
		tfsurname = new JTextField("Nazwisko", 16);
		tfnumber = new JTextField("Numer telefonu", 16);
		f.add(tfname);
		f.add(tfsurname);
		f.add(tfnumber);
		f.setVisible(true);
		f.setLayout(new FlowLayout());
		pokaz = new JButton("Pokaz kontakt");
		pokaz.addActionListener(this);
		dodaj = new JButton("Dodaj kontakt");
		dodaj.addActionListener(this);
		usun = new JButton("Usun kontakt");
		usun.addActionListener(this);
		f.add(pokaz);
		f.add(dodaj);
		f.add(usun);
		f.setSize(600, 100);
		f.setResizable(false);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == pokaz) {
			pokazKontakt();
		} else if (source == dodaj) {
			dodajKontakt();
		} else if (source == usun) {
			usunKontakt();
		}
	}

	public static Connection connect() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("ERROR: Cannot to connect JDBCDriver");
			e.printStackTrace();
			return null;
		}

		try {
			c = DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");
		} catch (SQLException e) {
			System.err.println("ERROR: Cannot to connect jdbc:hsqldb:file:testdb");
			e.printStackTrace();
		}
		return c;
	}

//Na pocz¹tku nale¿y stworzyc baze danych,
//	public static void createTable() throws SQLException {
//		Connection c = connect();
//		Statement stmt = c.createStatement();
//		stmt.executeUpdate("Create TABLE tabela(id INTEGER, name VARCHAR(64), surname VARCHAR(64))");
//		c.close();
//	}

	public static void pokazKontakt() {

		try {
			String name = null;
			String number = null;
			c = connect();
			String szukanyKontakt = tfsurname.getText();
			if (szukanyKontakt.equals("Nazwisko") || szukanyKontakt.equals("")) {
				JOptionPane.showMessageDialog(null, " Nie podano w³asciwego nazwiska");
			} else {
				String sql = "SELECT id, name, surname FROM tabela WHERE surname = ?";
				PreparedStatement dane = c.prepareStatement(sql);
				dane.setString(1, szukanyKontakt);
				ResultSet rs = dane.executeQuery();
				while (rs.next()) {
					name = rs.getString("name");
					number = Integer.toString(rs.getInt("id"));
				}
			}
			if (name == null && number == null) {
				JOptionPane.showMessageDialog(null, "Nie istnieje taki kontakt");
			} else {
				tfname.setText(name);
				tfnumber.setText(number);
				JOptionPane.showMessageDialog(null, "Znaleziono kontakt");
			}
			c.close();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public static void dodajKontakt() {
		try {
			String name = tfname.getText();
			String surname = tfsurname.getText();
			String number = tfnumber.getText();
			if (name.equals("") || surname.equals("") || number.equals("") || name.equals("Imie")
					|| surname.equals("Nazwisko") || number.equals("Numer")) {
				JOptionPane.showMessageDialog(null, "Nie wype³niono ka¿dego pola lub zrobiono to b³êdnie");
			} else {
				c = connect();
				String sql = "INSERT INTO tabela VALUES (?,?,?)";
				PreparedStatement dane = c.prepareStatement(sql);
				dane.setInt(1, Integer.parseInt(number));
				dane.setString(2, name);
				dane.setString(3, surname);
				dane.executeUpdate();
				JOptionPane.showMessageDialog(null, "Dodano kontakt: " + " " + number + " " + name + " " + surname);
				c.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public static void usunKontakt() {
		try {
			String surname = tfsurname.getText();
			if (surname.equals("") || surname.equals("Nazwisko")) {
				JOptionPane.showMessageDialog(null, "Podaj nazwisko kontaktu, który chcesz usun¹æ");
			} else {
				c = connect();
				String sql = "DELETE FROM tabela WHERE surname = ?";
				PreparedStatement dane = c.prepareStatement(sql);
				dane.setString(1, surname);
				dane.executeUpdate();
				c.close();
				JOptionPane.showMessageDialog(null, "Usuniêto kontakt o nawisku: " + surname);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public static void main(String[] args) throws SQLException {
		c = connect();
		
//Stworzenie bazy danych 
//		try {
//			createTable();
//		} catch (SQLException e) {
//			System.err.println("PROBLEM");
//			e.printStackTrace();
//			try {
//				c.close();
//			} catch (SQLException ex) {
//			}
//			ex.printStackTrace();
//		}
//		return;
//	}
		
		ksiazkaTelefoniczna k = new ksiazkaTelefoniczna();

		//Wyswietlenie ca³ej ksiazki telefonicznej w terminalu
		
		Statement stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM tabela");

		while (rs.next()) {
			System.out.println(rs.getInt("id") + ", " + rs.getString("name") + " " + rs.getString("surname"));

		}

		c.close();
	}

}
