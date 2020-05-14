import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

public class Spiderbot {
    private static final int MAX_DEPTH_LVL = 1; //stały poziom wgłebiania się bot'a
    static int amountOfAddresses = 0;
    static int amountOfErrors = 0;
    private final HashSet<String> addresses; //z'hash'owana tabela stringów zawierająca adresy stron

    public Spiderbot() {
        addresses = new HashSet<String>(); //inicjalizacja klasy HashSet
    }

    public void getAddresses(String address, int depthLvl) {
        if (!addresses.contains(address) && (depthLvl <= MAX_DEPTH_LVL)) {
            System.out.println(">> Depth level: " + depthLvl + " : " +address);
            try {
                addresses.add(address);
                amountOfAddresses++;

                Document doc = Jsoup.connect(address).get(); //łączenie się ze stroną z której będziemy pobierać linki
                Elements addressesOnWebsite = doc.select("a[href]"); // szukanie i wybieranie linków na ww. stronie

                depthLvl++;
                for (Element website : addressesOnWebsite) {
                    getAddresses(website.attr("abs:href"), depthLvl); // rekurencja metody getAddresses
                    // ".attr" to metoda pobierająca wartość atrybutu dla elementu website
                    // "abs:" to prefix atrybutu pobierający całkowity URL
                }

            } catch (IOException e) {
                System.err.println("Error for address: " + address + "\nMessage info: " + e.getMessage());
                amountOfErrors++;
            }
        }
    }

    public static void main(String[] args) {
        new Spiderbot().getAddresses("https://www.google.pl/", 0);
        System.out.println("Amount of web addresses: " + amountOfAddresses);
        System.out.println("Amount of errors " + amountOfErrors);

    }

}
