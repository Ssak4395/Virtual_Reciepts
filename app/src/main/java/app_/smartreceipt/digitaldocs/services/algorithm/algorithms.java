package app_.smartreceipt.digitaldocs.services.algorithm;

import com.google.android.gms.common.api.ApiException;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Vertex;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class algorithms {

    int delta = 60;
    long rawABN = 0;
    public double startTotalStrategy(List<EntityAnnotation> entities) throws IOException {
        /*
        Some assumption have been made, I have decided to search semantically for the total, although a relatively naive
        approach, one needs to consider, that the word "Total" can only have so many synonyms linguistically speaking, and there is a very high chance of
        those synonyms only "Sum" would be found on a reciept

        Motivation: Given an OCR annotation determine the Total price of the items purchased

        Approach 1: We assume the word "Total" is followed by the Total sum of all purchases, this is our base case(relatively trivial).

        NOTE THIS METHOD ITSELF IS NOT PERFECT AND WILL DEFINITELY NOT FIND THE TOTAL PRICE ALL OF THE TIME

        Semantic searching can be further finetuned to find other elements from a reciept such as Store manager served, date, phone number etc.

         */
        List<EntityAnnotation> ents = entities;

        String s = "";
        Vertex v1;
        Vertex v2;
        double isConvertableValue = 0.0;
        double realTotal = -1;

        if (entities == null) {
            return realTotal;
        }
        for (int i = 1; i < ents.size(); ++i) {

            if (ents.get(i).getDescription().equalsIgnoreCase("Total") | ents.get(i).getDescription().contains("Total") | ents.get(i).getDescription().contains("SUBTOTAL") | ents.get(i).getDescription().equalsIgnoreCase("Subtotal")) {
                v1 = ents.get(i).getBoundingPoly().getVertices().get(0);

                for (int j = i + 1; j < ents.size(); ++j) {
                    v2 = ents.get(j).getBoundingPoly().getVertices().get(0);
                    if (v1.getY() <= v2.getY() + delta && v1.getY() >= v2.getY() - delta) { // Use the delta to get surronding elements
                        try {
                            s = ents.get(j).getDescription();
                            isConvertableValue = Double.parseDouble(s.replace("$", "").trim());  // Remove the dollar sign if such exists
                            if (s.contains(".")) {
                                if (realTotal < isConvertableValue) {
                                    realTotal = isConvertableValue;
                                }
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }
        return realTotal;
    }

    public String startABNStrategy(List<EntityAnnotation> entities) throws IOException {
        List<EntityAnnotation> ents = entities;

        Vertex v1;
        Vertex v2;

        String ABN = "";
        if (entities == null) {
            return "Algorithm could not detect a valid ABN";
        }
        for (int i = 1; i < ents.size(); ++i) {
            if (ents.get(i).getDescription().equalsIgnoreCase("ABN") | ents.get(i).getDescription().equalsIgnoreCase("ABN:")) {
                v1 = ents.get(i).getBoundingPoly().getVertices().get(0);
                for (int j = i + 1; j < ents.size(); ++j) {
                    v2 = ents.get(j).getBoundingPoly().getVertices().get(0);

                    if (v1.getY() <= v2.getY() + 30 && v1.getY() >= v2.getY() - 30) {
                        ABN += ents.get(j).getDescription();
                        String[] split = ABN.split("");

                        String newABN = "";
                        for (String chars : split) {
                            if (chars.matches("[0-9]")) {
                                newABN += chars;
                            }
                        }
                        ABN = newABN;

                        if (ABN.toCharArray().length == 11) {
                            try {
                                rawABN = Long.parseLong(ABN);
                                return Long.toString(rawABN);
                            } catch (NumberFormatException ignored) {

                            }
                        }
                    }
                }
            }
        }
        if (!Long.toString(rawABN).matches( "[0-9]+")|Long.toString(rawABN).toCharArray().length != 11) {
            return "Algorithm could not detect a valid ABN";
        }
        return Long.toString(rawABN);
    }

    public ArrayList<ArrayList<String>> generateLineItems(List<EntityAnnotation> entityAnnotations) throws IOException {
        System.out.println("line items");
        // We dont need to touch bounding box coordinates because we are going on the assumption that the user will accurately
        List<EntityAnnotation> ents = entityAnnotations;
        ArrayList<String> lineItems = new ArrayList<>();
        ArrayList<String> linePrices = new ArrayList<>();
        ArrayList<ArrayList<String>> lineAndPrice = new ArrayList<>();

        for (int i = 0; i < ents.size(); i++) {
            System.out.println(ents.get(i));
        }

        if (ents == null || ents.size() == 0) {
            return lineAndPrice;
        }

        String description = ents.get(0).getDescription(); //this line can make the app crash
        String[] arr = description.split("\\r?\\n");
        ArrayList<String> sentence = new ArrayList<String>(Arrays.asList(arr));

        //Lets save and store the prices for all the items.

        //Remove any miscellanous words, usually these consist of line items that have less than 6 words, since the probability of this being a item in a reciept is extremely low
        // Rather it could be an OCR malfunction,



        for(int i = 0; i<sentence.size(); ++i) {
            try {
                //add more logic
                double price = Double.parseDouble(sentence.get(i).replace("$",""));
                if (sentence.get(i).contains(".")) {
                    linePrices.add(sentence.get(i));
                }

            } catch (NumberFormatException e) {
            }
        }

        System.out.println("start of prices");
        for (String price : linePrices) {
            System.out.println(price);
        }
        System.out.println("end of prices");


        for(int i = 0; i<sentence.size(); ++i)
        {
            if(sentence.get(i).contains("@") | sentence.get(i).contains("Quantity") | sentence.get(i).contains("NET")|sentence.get(i).contains("each") | sentence.get(i).toLowerCase().equals("qty") | sentence.get(i).toLowerCase().equals("qtu") | sentence.get(i).matches("%\\d+") | sentence.get(i).matches(".*\\d ea"))
            {
                sentence.remove(i);
            }
//            if(sentence.get(i).length() < 6)
//            {
//                sentence.remove(i);
//            }

        }

        System.out.println("start of sentence");
        for (String price : sentence) {
            System.out.println(price);
        }
        System.out.println("end of sentence");

        // Stage 2 we now have removed all the prices from our line items, we are not left with Line Items only, however further processing still needs to be done.
        String lastPrice = "";
        for(int i = 0; i<sentence.size(); ++i)
        {
            for(int j = 0; j<linePrices.size(); ++j)
            {
                System.out.println("before try");
                try {
                    System.out.println("after try");
                    System.out.println(sentence.size());
                    System.out.println(i);
                    System.out.println("i: " + sentence.get(i) + " j: " + linePrices.get(j));

                    if(sentence.get(i).equals(linePrices.get(j))) {

                        System.out.println("equals price");

                        if (sentence.size() != linePrices.size()*2) {

                            System.out.println("not equal");

                            for (int k = i; k > 0; k--) {
                                if (!sentence.get(k).equals(linePrices.get(j))) {

                                    System.out.println("not equal: again");

                                    if (!sentence.get(k).equals(lastPrice)) {

                                        System.out.println("last price");

                                        String item = sentence.get(k-1) + " " + sentence.get(k);
                                        sentence.remove(k);
                                        sentence.set(k-1, item);
                                        i--;
                                    } else {
                                        break;
                                    }
                                }
                            }
                            lastPrice = linePrices.get(j);
                        }
                        //sentence.remove(i);
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("crashed");

                    break;
                }

            }

        }

        System.out.println("start of sentence");
        for (String price : sentence) {
            System.out.println(price);
        }
        System.out.println("end of sentence");

        int pricePos = linePrices.size() - 1;

        if (sentence.size() != linePrices.size() * 2) {
            for (int i = sentence.size() - 1; i > 0; i--) {
                if (!sentence.get(i).equals(linePrices.get(pricePos))) {
                    sentence.remove(sentence.get(i));
                } else {
                    break;
                }
            }
        }

        for(int i = 0; i<sentence.size(); ++i)
        {
            for(int j = 0; j<linePrices.size(); ++j) {
                if(sentence.get(i).equals(linePrices.get(j))) {
                    sentence.remove(sentence.get(i));
                }
            }
        }

        System.out.println("start of sentence");
        for (String price : sentence) {
            System.out.println(price);
        }
        System.out.println("end of sentence");

        System.out.println("start of prices");
        for (String price : linePrices) {
            if (price.contains("$")) {
                linePrices.set(linePrices.indexOf(price), price.replace("$", ""));
            }
            System.out.println(price);
        }
        System.out.println("end of prices");


        if (isParseable(sentence.size(), linePrices.size())) {
            lineItems = sentence;
        }

        lineAndPrice.add(lineItems);
        lineAndPrice.add(linePrices);

        return lineAndPrice;

    }

    boolean isParseable(int lineItems, int priceItems)
    {
        return lineItems == priceItems;
    }


    public String resolveTotalDate(List<EntityAnnotation> annotations) throws IOException {

        List<EntityAnnotation> ents =  annotations;
        String[] arr = ents.get(0).getDescription().split("\\r?\\n");

        for(int i = 0; i<arr.length; ++i)
        {
            for(int j = 0; j<arr[i].split(" ").length; ++j)
            {
                try{

                    Date sdf = new SimpleDateFormat("dd/MM/yy").parse(arr[i].split(" ")[j]);
                    DateFormat df5 = new SimpleDateFormat("E, MMM dd yyyy");
                    String meDate = df5.format(sdf);
                    return  arr[i].split(" ")[j];

                } catch (ParseException ignored) {}

                try {
                    Date sdf = new SimpleDateFormat("dd-MMM-yy").parse(arr[i].split(" ")[j]);
                    DateFormat df5 = new SimpleDateFormat("E, MMM dd yyyy");
                    String meDate = df5.format(sdf);

                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    String date = df.format(sdf);
                    return  date;

                } catch (ParseException ignore) {}

                try {
                    Date sdf = new SimpleDateFormat("MMMMM dd, yy").parse(arr[i].split(" ")[j]);
                    DateFormat df5 = new SimpleDateFormat("E, MMM dd yyyy");
                    String meDate = df5.format(sdf);

                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    String date = df.format(sdf);
                    return  date;

                } catch (ParseException ignore) {}
            }
        }
        // Shouldnt reach here
        return "";
    }



    public HashMap getWooliesLatLongCoords(List<EntityAnnotation> mENTS) throws IOException, InterruptedException, ApiException, com.google.maps.errors.ApiException {
        List<EntityAnnotation> ents = mENTS;
        String[] arr = ents.get(0).getDescription().split("\\r?\\n");

        String address = arr[2] + " " + arr[3];

        GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyD2p2Yc95RZ01oFNI9ox9yE2BhXR5X3rTw").build();
        GeocodingResult[] results =  GeocodingApi.geocode(context,address).await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();



        HashMap latNLong = new HashMap();
        latNLong.put("lat",results[0].geometry.location.lat);
        latNLong.put("long",results[0].geometry.location.lng);
        context.shutdown();
        return latNLong;
    }
}
