package in.com.bookmydoc.generator;

import java.util.Random;

public class IdGenerator {
    public static String generateUserId(String prefix, String num) {
//        String datePart = new SimpleDateFormat("ddHHmm", Locale.getDefault()).format(new Date());
//        String chars = "1234567890";

        StringBuilder randomPart = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i<=3; i++) {
            randomPart.append(num.charAt(random.nextInt(num.length())));
        }

        String id = "BMD" + prefix + "" + "" + randomPart;
        return id;
    }
}
