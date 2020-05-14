import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Spiderbot {
    private static final int MAX_DEPTH_LVL = 1; //stały poziom zagłębiania się bot'a
    static int amountOfAddresses = 0;
    static int amountOfErrors = 0;
    private final HashSet<String> addresses; //z'hash'owana tabela stringów zawierająca adresy stron
    public final List<List<String>> articles;

    public Spiderbot() {
        addresses = new HashSet<>(); //inicjalizacja klasy HashSet
        articles = new ArrayList<>();
    }

    public void getAddresses(String address, int depthLvl) {
        if (!addresses.contains(address) && (depthLvl <= MAX_DEPTH_LVL)) {
            System.out.println(">> Depth level: " + depthLvl + " : " +address);
            try {
                addresses.add(address);
                amountOfAddresses++;

                Document doc = Jsoup.connect(address).get(); //łączenie się ze stroną z której będziemy pobierać linki
                Elements addressesOnWebsite = doc.select("a[href^=\"https://pl.ign.com\"]"); // szukanie i wybieranie linków na ww. stronie

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

    public void getSpecificAddresses(String searchedPhrase) {
        addresses.forEach(x -> {
            Document doc;
            try {

                doc = Jsoup.connect(x).get();
                Elements specificAddresses = doc.select("h3 a[href^=\"https://pl.ign.com\"]");

                for (Element specificAddress : specificAddresses) {
                    if (specificAddress.text().matches("^.*?("+ searchedPhrase +").*$")) { //dopasowywanie frazy w adresie strony
                        System.out.println(specificAddress.attr("abs:href"));

                        ArrayList<String> temporary = new ArrayList<>();
                        temporary.add(specificAddress.text());
                        temporary.add(specificAddress.attr("abs:href"));
                        articles.add(temporary);
                    }
                }

            } catch (IOException e) {
                System.err.println(e.getMessage());
                amountOfErrors++;
            }
        });
    }

    public static void main(String[] args) {

        Spiderbot spiderbot = new Spiderbot();

        spiderbot.getAddresses("https://pl.ign.com", 0);
        spiderbot.getSpecificAddresses("gameplay|Gameplay|GAMEPLAY"); // znak | wykorzystany do wykluczenia braku wyszukań elementów pisanych dużymi literami

        System.out.println("Amount of web addresses: " + amountOfAddresses);
        System.out.println("Amount of errors: " + amountOfErrors);

    }

}
