package com.example.smart_basket_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Scan_QR_Activity extends AppCompatActivity {

    public static String basketID = "";
    private static final int RETRY_DELAY = 20000; // 20 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ξεκινήστε το σάρωση QR
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(MyCaptureActivity.class);
        integrator.setOrientationLocked(true);  // Επιτρέψτε τον περιστροφή της οθόνης
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Σάρωση ακυρώθηκε", Toast.LENGTH_SHORT).show();
            } else {
                basketID = result.getContents();
                // Ανάκτηση επιβεβαίωσης καλαθιού
                checkBasketConfirmation();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkBasketConfirmation() {
        // Δημιουργούμε ExecutorService για να εκτελούμε δικτυακές αιτήσεις σε ξεχωριστό νήμα
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Κάνουμε έλεγχο αν το καλάθι είναι επιβεβαιωμένο
                Data_Base_Manager data_base_manager = new Data_Base_Manager();
                data_base_manager.setBasketConfirmation();

                // Use AtomicInteger to allow thread-safe access and declare it final
                final AtomicInteger continueProcessing = new AtomicInteger(data_base_manager.getBasketConfirmation());

                // Επιστροφή στο κύριο νήμα για να ενημερώσουμε το UI
                updateUI(continueProcessing);
                data_base_manager.lockBasket();
            }
        });
    }

    // Separate method to handle UI updates and avoid inner class issues
    private void updateUI(final AtomicInteger continueProcessing) {
        Handler mainHandler = new Handler(getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (continueProcessing.get() == 0) {//1
                    // Αν το καλάθι είναι επιβεβαιωμένο, προχωράμε στην επόμενη οθόνη
                    startActivity(new Intent(Scan_QR_Activity.this, HomeActivity.class));

                } else {
                    // Αν το καλάθι δεν είναι επιβεβαιωμένο, κάνουμε επανάληψη κάθε 20 δευτερόλεπτα
                    Toast toast = Toast.makeText(Scan_QR_Activity.this, "Παρακαλώ πατήστε το κουμπί της εκκίνησης.\nΕπανέλεγχος σε 20 δευτερόλεπτα", Toast.LENGTH_LONG);
                    // Καθυστέρηση 20 δευτερολέπτων πριν την επανάληψη
                    View view = toast.getView();

                    // Βρίσκουμε το TextView μέσα στο Toast και αλλάζουμε το μέγεθος γραμματοσειράς
                    TextView text = view.findViewById(android.R.id.message);
                    text.setTextSize(16); // Αλλάζουμε το μέγεθος των γραμμάτων

                    toast.show();
                    Handler retryHandler = new Handler(getMainLooper());
                    retryHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkBasketConfirmation();  // Ξεκινάμε ξανά την αναζήτηση μετά από καθυστέρηση
                        }
                    }, RETRY_DELAY);
                }
            }
        });
    }
}
