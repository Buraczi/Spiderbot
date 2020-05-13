import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

public class Spiderbot {

    private HashSet<String> addresses; //z'hash'owana tabela stringów zawierająca adresy stron

    public Spiderbot() {
        addresses = new HashSet<String>(); //inicjalizacja klasy HashSet
    }

    public void getAddresses(String address) {
        if (!addresses.contains(address)) {
            try {
                if (addresses.add(address)) {
                    System.out.println(address);
                }

                Document doc = Jsoup.connect(address).get(); //łączenie się ze stroną z której będziemy pobierać linki
                Elements addressesOnWebsite = doc.select("a[href]"); // szukanie i wybieranie linków na ww. stronie

                for (Element website : addressesOnWebsite) {
                    getAddresses(website.attr("abs:href")); // rekurencja metody getAddresses
                    // ".attr" to metoda pobierająca wartość atrybutu dla elementu website
                    // "abs:" to prefix atrybutu pobierający całkowity URL
                }
            } catch (IOException e) {
                System.err.println("Error for address: " + address + "\nMessage info: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new Spiderbot().getAddresses("https://www.google.pl/");
    }

}
