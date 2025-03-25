package com.example.smart_basket_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class Products_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private Product_Adapter adapter;
    private List<Product> allProducts = new ArrayList<>(); // Αρχική λίστα προϊόντων
    private List<Product> filteredProducts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.findViewById(R.id.searchView).setVisibility(View.VISIBLE);
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });
        new FetchProductsTask().execute();
        return view;
    }
    private void filterProducts(String query) {
        filteredProducts.clear();
        for (Product product : allProducts) {
            if (product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                filteredProducts.add(product);
            }
        }
        adapter.updateProductList(filteredProducts); // Νέα μέθοδος στον adapter
    }

    private class FetchProductsTask extends AsyncTask<Void, Void, List<Product>> {
        Data_Base_Manager manager = new Data_Base_Manager();
        @Override
        protected List<Product> doInBackground(Void... voids) {

            manager.setProductsList();
            manager.setOffersList();
            return manager.getProductsList();
        }

        @Override
        protected void onPostExecute(List<Product> productList) {
            super.onPostExecute(productList);
            allProducts = new ArrayList<>(productList);
            filteredProducts = new ArrayList<>(allProducts);
            // Ορίστε τον προσαρμογέα για τη λίστα
            Button emptyButton = getActivity().findViewById(R.id.clearBasketButton);
            TextView textViewTotalCost = getActivity().findViewById(R.id.textViewTotalCost);
            List<Offers> offers = new ArrayList<>();
            adapter = new Product_Adapter(productList, offers , false , false , textViewTotalCost , getContext() , emptyButton , manager.getoffersList());
            recyclerView.setAdapter(adapter);
        }
    }
}
