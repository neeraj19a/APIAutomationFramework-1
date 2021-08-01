package utilities;

import com.github.javafaker.Faker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FakerUtils {

    public static String generateFirstName() {
        Faker faker = new Faker();
        return faker.name().firstName();
    }

    public static String generateLastName() {
        Faker faker = new Faker();
        return faker.name().lastName();
    }

    public static int price() {
        Faker faker = new Faker();
        return faker.random().nextInt(100);
    }

    public static String additionalneeds() {
        Faker faker = new Faker();
        return faker.regexify("[ A-Za-z0-9_@./#&+-]{5}");
    }

    public static String checkinDate() {
        Faker faker = new Faker();
        Date d1 = new Date("Sat Dec 01 00:00:00 GMT 2021");
        Date d2 = new Date("Sat Dec 01 00:00:00 GMT 2021");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String checkinDate = formatter.format(faker.date().between(d1,d2));
        return checkinDate;
    }

    public static String checkoutDate() {
        Faker faker = new Faker();
        Date d1 = new Date("Sat Dec 01 00:00:00 GMT 2022");
        Date d2 = new Date("Sat Dec 01 00:00:00 GMT 2022");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String checkoutDate = formatter.format(faker.date().between(d1,d2));
        return checkoutDate;
    }
}
