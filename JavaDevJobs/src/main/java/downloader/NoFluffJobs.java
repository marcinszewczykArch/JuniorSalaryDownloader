package downloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class NoFluffJobs {
    public static void main(String[] args) throws IOException {

        //oferty pracy noFluffJobs z filtrami:
//            wymagania: java;
//            wynagrodzenie: na umowę o pracę;
//            doświadczenie: junior;

        String URL1 = "https://nofluffjobs.com/pl/praca-it/java?criteria=employment%3Dpermanent%20seniority%3Djunior";
        String URL2 = "https://nofluffjobs.com/pl/praca-it/java?criteria=employment%3Dpermanent%20seniority%3Djunior&page=2";

        ArrayList<String> offers = getOffers(URL1, URL2);
//        printJavaOffers(offers);
        ArrayList<Integer> averageSalaryList = getAverageSalaryList(offers);
        int averageSalary = getAverageSalary(averageSalaryList);
        int averageNettoSalary = getSalaryNetto(averageSalary);
        System.out.println("average salary brutto: " + averageSalary + " PLN");
        System.out.println("average salary netto: " + averageNettoSalary + " PLN");
    }

    private static int getSalaryNetto(int bruttoSalary) throws IOException {
        String URL = "https://www.money.pl/podatki/kalkulatory/plac/?rok_podatkowy=2021&pensja=" + bruttoSalary + "&typ_kalkulatora=0&typ_wynagrodzenia=0&koszty_autorskie=0&koszty_autorskie_procent=0&poza_miejscem_zamieszkania=0&wspolne_rozliczanie=0&uwzglednij_kwote_wolna=1&ppk=0&ppk_pracownik=2&ppk_pracodawca=1.5&pit_26=0&zus=0&dobrowolne_chorobowe=0&zwiekszone_koszty_uzyskania=0";
        Document document = Jsoup.connect(URL).get();
        Element element = document.select("td.npeb8d-1").get(1);
        String elementText = element.text();
        elementText = elementText.replace(" ", "");
        elementText = elementText.replace("zł", "");
        return Integer.valueOf(elementText);
    }

    private static int getAverageSalary(ArrayList<Integer> averageSalaryList) {
        int sum = 0;
        for (Integer salary : averageSalaryList) {
            sum = sum + salary;
        }
        int averageSalary = sum / averageSalaryList.size();
        return averageSalary;
    }

    private static ArrayList<Integer> getAverageSalaryList(ArrayList<String> offers) {
        ArrayList<Integer> averageSalary = new ArrayList<>();

        for (String offer : offers) {
            String salaryString = offer;
            salaryString = salaryString.replace(" ", "");
            salaryString = salaryString.replace("PLN", "");
            salaryString = salaryString.replace("java", "");

            if(salaryString.contains("-")){
                String[] minMaxSalary = salaryString.split("-");
                int minSalary = Integer. valueOf(minMaxSalary[0]);
                int maxSalary = Integer. valueOf(minMaxSalary[1]);
                int salary = (minSalary + maxSalary) / 2;
                averageSalary.add(salary);
            } else {
                int salary = Integer. valueOf(salaryString);
                averageSalary.add(salary);
            }
        }
        return averageSalary;
    }

    private static void printJavaOffers(ArrayList<String> offers) {
        for (String offert : offers) {
            System.out.println(offert);
        }
    }

    private static ArrayList<String> getOffers(String...args) throws IOException {
        ArrayList<String> offerts = new ArrayList<String>();
        String selector = "nfj-posting-item-tags";

        for (String arg : args) {
            Document document = Jsoup.connect(arg).get();
            Elements nfjElements = document.select(selector);
            for (Element element : nfjElements) {
                String elementText = element.text();
                if(elementText.contains("java")) {
                    offerts.add(elementText);
                }
            }
        }

        return offerts;
    }
}