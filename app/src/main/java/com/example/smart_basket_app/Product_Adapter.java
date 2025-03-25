package com.example.smart_basket_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.List;

public class Product_Adapter extends RecyclerView.Adapter<Product_Adapter.ViewHolder> {

    private List<Product> productList;
    private List<Offers> offersList;
    private boolean virtualBasket;
    private boolean basket;
    private TextView virtualCost;
    private Context context;
    private Button emptyButton;
    static private List<Offers> List;

    public Product_Adapter(List<Product> productList , List<Offers> offersList, boolean basket , boolean virtualBasket , TextView virtualCost, Context context, Button emptyButton, List<Offers> List) {
        this.productList = productList;
        this.offersList = offersList;
        this.basket = basket;
        this.virtualBasket = virtualBasket;
        this.virtualCost = virtualCost;
        this.context = context;
        this.emptyButton = emptyButton;
        this.List = List;
    }
    private void saveBasketState(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("VirtualBasketPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply(); // Αποθηκεύει ασύγχρονα
    }
    public void updateProductList(List<Product> newProducts) {
        this.productList.clear();
        this.productList.addAll(newProducts);
        notifyDataSetChanged();
    }

    private String getBasketState(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("VirtualBasketPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, ""); // Επιστρέφει "" αν δεν βρει δεδομένα
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if(basket)
            virtualCost = this.virtualCost;
        String savedQuantity = getBasketState(context, "product_" + position);
        String savedVirtualCost = getBasketState(context, "virtualCost" );
        if (!savedQuantity.isEmpty())
            holder.editTextQuantity.setText(savedQuantity);
        if(!savedVirtualCost.isEmpty()&&!basket)
            virtualCost.setText(savedVirtualCost);

        Product product = productList.get(position);
        holder.textViewName.setText(product.getProductName());
        byte[] imageBytes = Base64.getDecoder().decode(product.getProductImage());
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.imageView.setImageBitmap(bitmap);


        String price = "Original Price: " + String.valueOf(product.getProductPrice()) + "$";
        holder.textViewPrice.setText(price);

        if (!List.isEmpty()) {
            for (int i = 0; i < List.size(); i++)
                if (product.getId().equals(List.get(i).getId())){
                    holder.textViewPrice.setText("Original Price: " + String.valueOf(List.get(i).getOfferPrice()) + "$");
                }

        }
        if (!offersList.isEmpty()) {
            for (int i = 0; i < offersList.size(); i++)
                if (product.getId().equals(offersList.get(i).getId())){
                    price = "Original Price: " + String.valueOf(product.getProductPrice()) + "$";
                    holder.textViewPrice.setText(price);
                    holder.textViewDiscount.setVisibility(View.VISIBLE);
                    holder.textViewDiscount.setText("Discount: "+String.valueOf(offersList.get(i).getOfferDiscount()));
                    holder.textViewFinalPrice.setVisibility(View.VISIBLE);
                    holder.textViewFinalPrice.setText("Final Price: "+Float.valueOf(offersList.get(i).getOfferPrice())+"$   ");
                    holder.textViewPrice.setPaintFlags(holder.textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

        }
        if (virtualBasket) {
            holder.textViewEmptyRow.setVisibility(View.GONE);
            holder.Virtual_Basket_Option.setVisibility(View.VISIBLE);
            final int[] last_Quantity = {0};

            // Listener όταν το EditText χάνει την εστίαση
            holder.editTextQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (hasFocus) {
                        last_Quantity[0] = Integer.parseInt(holder.editTextQuantity.getText().toString());
                        // Απενεργοποίηση κουμπιών όταν το EditText έχει εστίαση
                        holder.buttonPlus.setEnabled(false);
                        holder.buttonMinus.setEnabled(false);
                    } else { // Όταν το EditText χάνει την εστίαση
                        holder.buttonPlus.setEnabled(true);
                        holder.buttonMinus.setEnabled(true);
                        if (holder.editTextQuantity.getText().toString().isEmpty())
                            holder.editTextQuantity.setText("0");

                        int newQuantity = Integer.parseInt(holder.editTextQuantity.getText().toString());
                        BigDecimal currentVirtualCost = BigDecimal.valueOf(getCurrentVirtualCost());
                        BigDecimal finalPrice = BigDecimal.valueOf(getFinalPrice(holder, String.valueOf(holder.textViewPrice.getText())));

                        System.out.println("The last quantity is: " + last_Quantity[0]);

                        // Υπολογισμός νέου κόστους με ακριβείς πράξεις
                        BigDecimal newVirtualCost = currentVirtualCost.add(finalPrice.multiply(BigDecimal.valueOf(newQuantity - last_Quantity[0])));
                        newVirtualCost = newVirtualCost.setScale(2, RoundingMode.HALF_UP); // Στρογγυλοποίηση στα 2 δεκαδικά

                        System.out.println("The new virtual cost is: " + newVirtualCost);
                        virtualCost.setText("Total Cost: " + newVirtualCost.toString() + " $");

                        saveBasketState(context, "product_" + holder.getAdapterPosition(), holder.editTextQuantity.getText().toString());
                        saveBasketState(context, "virtualCost", virtualCost.getText().toString());
                    }
                }
            });

            // Listener για το κουμπί "+" (αύξηση ποσότητας)
            holder.buttonPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentValue = 0;
                    if (!holder.editTextQuantity.getText().toString().equals(""))
                        currentValue = Integer.parseInt(holder.editTextQuantity.getText().toString());
                    currentValue++;
                    holder.editTextQuantity.setText(String.valueOf(currentValue));

                    BigDecimal currentVirtualCost = BigDecimal.valueOf(getCurrentVirtualCost());
                    BigDecimal finalPrice = BigDecimal.valueOf(getFinalPrice(holder, String.valueOf(holder.textViewPrice.getText())));

                    BigDecimal newVirtualCost = currentVirtualCost.add(finalPrice);
                    newVirtualCost = newVirtualCost.setScale(2, RoundingMode.HALF_UP); // 2 δεκαδικά ψηφία

                    System.out.println("The new virtual cost is: " + newVirtualCost);
                    virtualCost.setText("Total Cost: " + newVirtualCost.toString() + " $");

                    last_Quantity[0] = currentValue;

                    saveBasketState(context, "product_" + holder.getAdapterPosition(), String.valueOf(currentValue));
                    saveBasketState(context, "virtualCost", virtualCost.getText().toString());
                }
            });

            // Listener για το κουμπί "-" (μείωση ποσότητας)
            holder.buttonMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentValue = 0;
                    if (!holder.editTextQuantity.getText().toString().equals(""))
                        currentValue = Integer.parseInt(holder.editTextQuantity.getText().toString());

                    if (currentValue > 0) {
                        currentValue--;
                        holder.editTextQuantity.setText(String.valueOf(currentValue));

                        BigDecimal currentVirtualCost = BigDecimal.valueOf(getCurrentVirtualCost());
                        BigDecimal finalPrice = BigDecimal.valueOf(getFinalPrice(holder, String.valueOf(holder.textViewPrice.getText())));

                        BigDecimal newVirtualCost = currentVirtualCost.subtract(finalPrice);
                        newVirtualCost = newVirtualCost.setScale(2, RoundingMode.HALF_UP); // Στρογγυλοποίηση σε 2 δεκαδικά

                        System.out.println("The new virtual cost is: " + newVirtualCost);
                        virtualCost.setText("Total Cost: " + newVirtualCost.toString() + " $");

                        last_Quantity[0] = currentValue;

                        saveBasketState(context, "product_" + holder.getAdapterPosition(), String.valueOf(currentValue));
                        saveBasketState(context, "virtualCost", virtualCost.getText().toString());
                    }
                }
            });
            this.emptyButton.setVisibility(View.VISIBLE);
            this.emptyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < productList.size(); i++) {
                        saveBasketState(context, "product_" + i, "0"); // Αποθήκευση ποσότητας 0 στο SharedPreferences
                    }
                    saveBasketState(context, "virtualCost", "Total Cost: 0.0 $");
                    virtualCost.setText("Total Cost: 0.0 $");
                    notifyDataSetChanged();
                }
            });
        }
    }
// Μέθοδος για τον υπολογισμό του τρέχοντος Virtual Cost
        private float getCurrentVirtualCost() {
            float currentVirtualCost = 0.0F;
            String decimalRegex = "Total Cost: (\\d+\\.\\d+) \\$";
            if (virtualCost.getText().toString().matches(decimalRegex)) {
                String decimalPart = virtualCost.getText().toString().replaceAll(decimalRegex, "$1");
                currentVirtualCost = Float.parseFloat(decimalPart);
            }
            return currentVirtualCost;
        }

// Μέθοδος για τον υπολογισμό της τιμής του προϊόντος
        private float getFinalPrice(ViewHolder holder, String text) {
            String[] parts = text.split(":"); // Διασπά το string με βάση το ":"
            String numericValue = parts[1].replace("$", "").trim(); // Αφαιρεί το "$" και τα κενά
            System.out.println("Numeric value: " + numericValue); // Εκτυπώνει "1.25"
            return Float.parseFloat(numericValue);
        }


        @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;
        TextView textViewDiscount;
        TextView textViewFinalPrice;
        TextView textViewEmptyRow;
        LinearLayout Virtual_Basket_Option;
        Button buttonMinus;
        Button buttonPlus;
        EditText editTextQuantity;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewDiscount = itemView.findViewById(R.id.textViewDiscount);
            textViewFinalPrice = itemView.findViewById(R.id.textViewFinalPrice);
            textViewEmptyRow = itemView.findViewById(R.id.textViewEmptyRow);
            Virtual_Basket_Option =  itemView.findViewById(R.id.Virtual_Basket_Option);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            editTextQuantity = itemView.findViewById(R.id.editTextQuantity);
        }
    }
}

