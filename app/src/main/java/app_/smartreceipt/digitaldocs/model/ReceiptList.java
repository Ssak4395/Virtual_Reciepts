package app_.smartreceipt.digitaldocs.model;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import app_.smartreceipt.digitaldocs.activities.HomeActivity;
import app_.smartreceipt.digitaldocs.activities.ReceiptUpdate;

public class ReceiptList {

    enum Sorting {
        DATE,
        ALPHABETICAL,
        TOTAL
    }

    private static ArrayList<ReceiptEntity> receipts = new ArrayList<ReceiptEntity>();
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private static String userID = fAuth.getCurrentUser().getUid();
    private static CollectionReference databaseReceiptList = firebaseFirestore.collection("users").document(userID).collection("Receipts");
    private static Context context = HomeActivity.getAppContext();
    private static Sorting sorting = Sorting.DATE;
    private static boolean upload = false;
    private static Uri uri;


    public static void addForFirstTime(final ReceiptEntity newReceipt, final Uri newUri) {
        upload = true;
        uri = newUri;
        addReceipt(newReceipt);
    }

    private static void uploadImageToFirebase(Uri uri, ReceiptEntity receiptId) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();


        final StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/receipts/" + receiptId.getID() + ".png");

        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }


    public static void addReceipt(final ReceiptEntity newReceipt) {
        addToArray(receipts,newReceipt);

        for (ReceiptLineItem lineItem : newReceipt.getLineItems()) {
            lineItem.setReceiptOwner(null);
        }

        if (newReceipt.getID() != null) {
            databaseReceiptList.document(newReceipt.getID()).set(newReceipt);

        } else {
            databaseReceiptList.add(newReceipt).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    newReceipt.setID(documentReference.getId());
                    if (upload) {
                        upload = false;
                        uploadImageToFirebase(uri, newReceipt);
                    }
                }
            });
        }
        try {
            saveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ReceiptEntity> getReceipts() {
        return receipts;
    }

    public static void clearReceipts() {
        receipts.clear();
        try {
            saveData();
        } catch (Exception ignored) {ignored.printStackTrace();}
    }

    public static void updateUser() {
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        databaseReceiptList = firebaseFirestore.collection("users").document(userID).collection("Receipts");
    }

    public static void updateReceiptsList(final ReceiptUpdate o) {
        databaseReceiptList.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> databaseReceipts = queryDocumentSnapshots.getDocuments();
                            receipts.clear();

                            for (DocumentSnapshot receipt : databaseReceipts) {
                                String id = receipt.getId();
                                String name = receipt.getString("storeName");

                                double path = receipt.getDouble("imagePath");
                                int imagePath = (int) path;
                                String date = receipt.getString("date");
                                double totalPrize = receipt.getDouble("totalPrice");

                                //line item section
                                ArrayList<ReceiptLineItem> lineItems = new ArrayList<>();
                                try {
                                    ArrayList lineItemList = (ArrayList) receipt.get("lineItems");
                                    for (int i = 0; i < lineItemList.size(); i++) {
                                        HashMap items = (HashMap) lineItemList.get(i);
                                        String item = (String) items.get("item");
                                        double price = (Double) items.get("price");
                                        lineItems.add(new ReceiptLineItem(item, price));
                                    }
                                } catch (Exception ignored) {
                                }

                                ArrayList<Tag> tags = new ArrayList<>();
                                try {
                                    ArrayList tagList = (ArrayList) receipt.get("tags");

                                    for (int i = 0; i < tagList.size(); i++) {
                                        HashMap tag = (HashMap) tagList.get(i);
                                        String tagName = (String) tag.get("name");
                                        int tagNum = ((Long) tag.get("numInArray")).intValue();
                                        tags.add(new Tag(tagName, tagNum));
                                    }
                                } catch (Exception ignored) {
                                }

                                ReceiptEntity updateReceipt = new ReceiptEntity(name, date, totalPrize);
                                if (updateReceipt.getImagePath() == 0) {
                                    updateReceipt.setImagePath(imagePath);
                                }
                                updateReceipt.setID(id);
                                updateReceipt.setLineItems(lineItems);
                                updateReceipt.setTags(tags);
                                addToArray(receipts, updateReceipt);
                            }
                        } else {
                            //No data on databade clear receipt
                            receipts.clear();
                        }

                        //try saving data
                        try {
                            saveData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //update page
                        o.setReceiptsList();
                    }
                });
    }

    public static void saveData() throws IOException {
        if (context == null) {
            context = HomeActivity.getAppContext();
        }
        FileOutputStream outputStream = context.getApplicationContext().openFileOutput("receipts", Context.MODE_PRIVATE);
        for (ReceiptEntity receipt : receipts) {
            outputStream.write(receipt.toStirng().getBytes());
        }
        outputStream.close();
    }

    public static void loadData() throws IOException {
        FileInputStream fis = context.openFileInput("receipts");
        Scanner scanner = new Scanner(fis);
        scanner.useDelimiter("\\Z");
        while (scanner.hasNext()) {
            String content = scanner.nextLine().trim();

            String[] splitContent = content.split(",");
            String storeName = splitContent[0];
            String date = splitContent[1];
            double totalPrice = Double.parseDouble(splitContent[2]);
            int imagePath = Integer.parseInt(splitContent[3]);

            ReceiptEntity loadedReceipt = new ReceiptEntity(storeName, date, totalPrice);
            loadedReceipt.setImagePath(imagePath);

            ArrayList<ReceiptLineItem> lineItems = new ArrayList<ReceiptLineItem>();

            for (int i = 4; i < splitContent.length; i = i + 2) {
                String itemName = splitContent[i];
                double price;
                try {
                    price = Double.parseDouble(splitContent[i + 1]);
                } catch (Exception e) {
                    price = 0;
                }
                ReceiptLineItem loadedLineItem = new ReceiptLineItem(itemName, price);
                loadedLineItem.setReceiptOwner(loadedReceipt);
                lineItems.add(loadedLineItem);
            }

            loadedReceipt.setLineItems(lineItems);
            addToArray(receipts, loadedReceipt);
        }
        scanner.close();
    }

    public static void updateContext(Context applicationContext) {
        context = applicationContext;
    }

    public static void updateSorting(String s) {
        if (s.equals("By Date")) {
            sorting = Sorting.DATE;
        } else if (s.equals("Alphabetically")) {
            sorting = Sorting.ALPHABETICAL;
        } else {
            sorting = Sorting.TOTAL;
        }
    }

    public static void deleteReceipt(final ReceiptEntity receiptEntity, final ReceiptUpdate receiptView) {
        databaseReceiptList.document(receiptEntity.getID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                receipts.remove(receiptEntity);
                if (receiptView != null) {
                    receiptView.setReceiptsList();
                    Toast.makeText(context.getApplicationContext(), receiptEntity.getStoreName() + " was deleted successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context.getApplicationContext(), receiptEntity.getStoreName() + " was not deleted. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void updateList() {
        List<ReceiptEntity> receiptLists = new ArrayList<>();
        receiptLists.addAll(receipts);

        receipts.clear();
        for (ReceiptEntity receipt : receiptLists) {
            addToArray(receipts,receipt);
        }
    }

    public static int getSorting() {
        return sorting.ordinal();
    }

    public static void addToArray(ArrayList<ReceiptEntity> list,ReceiptEntity newReceipt) {
        if (sorting == Sorting.DATE) {
            dateSort(list, newReceipt);
        } else if (sorting == Sorting.ALPHABETICAL) {
            alphabeticalSort(list, newReceipt);
        } else {
            priceSort(list, newReceipt);
        }
    }

    private static void dateSort (ArrayList<ReceiptEntity> list, ReceiptEntity newReceipt) {
        //initial
        if (list.isEmpty()) {
            list.add(newReceipt);
            return;
        }

        //newReceiptDate conversion
        String newDateString = newReceipt.getDate();
        String[] newDateSplit = newDateString.split("/");
        int[] newDateInt = new int[3];
        for (int i = 0; i < newDateSplit.length; i++) {
            newDateInt[i] = Integer.parseInt(newDateSplit[i]);
        }


        //loop though to see where it goes
        for (ReceiptEntity receipt : list) {
            //get in list data infomation
            String receiptDate = receipt.getDate();
            String[] receiptDateSplit = receiptDate.split("/");
            int[] receiptDateInt = new int[3];
            for (int i = 0; i < receiptDateSplit.length; i++) {
                receiptDateInt[i] = Integer.parseInt(receiptDateSplit[i]);
            }


            //new receipt has a bigger year, automatically add it to the front
            if (newDateInt[0] > receiptDateInt[0]) {
                list.add(0, newReceipt);
                return;


                //receipt has the same year, now look at months
            } else if (newDateInt[0] == receiptDateInt[0]) {
                // date is bigger so automatically add it
                if (newDateInt[1] > receiptDateInt[1]) {
                    int index = list.indexOf(receipt);
                    list.add(index, newReceipt);
                    return;


                    //month is equal look at day
                } else if (newDateInt[1] == receiptDateInt[1]) {
                    //day is bigger or equal so add it there
                    if (newDateInt[2] >= receiptDateInt[2]) {
                        int index = list.indexOf(receipt);
                        list.add(index, newReceipt);
                        return;
                    }
                }
            }
        }
        list.add(newReceipt);
    }

    private static void alphabeticalSort (ArrayList<ReceiptEntity> list, ReceiptEntity newReceipt) {
        if (list.isEmpty()) {
            list.add(newReceipt);
            return;
        }

        String newStoreName = newReceipt.getStoreName();

        for (ReceiptEntity receipt : list) {
            String otherStoreName = receipt.getStoreName();

            int result = newStoreName.compareToIgnoreCase(otherStoreName);

            if (result <= 0) {
                int index = list.indexOf(receipt);
                list.add(index, newReceipt);
                return;
            }
        }
        list.add(newReceipt);
    }

    public static void priceSort (ArrayList<ReceiptEntity> list, ReceiptEntity newReceipt) {
        if (list.isEmpty()) {
            list.add(newReceipt);
            return;
        }

        for (ReceiptEntity receipt : list) {
            if (newReceipt.getTotalPrice() >= receipt.getTotalPrice()) {
                int index = list.indexOf(receipt);
                list.add(index, newReceipt);
                return;
            }
        }
        list.add(newReceipt);
    }
}