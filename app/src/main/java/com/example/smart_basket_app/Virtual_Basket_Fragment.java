package com.example.smart_basket_app;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Virtual_Basket_Fragment extends Fragment {


    private RecyclerView recyclerView;
    private Product_Adapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new FetchProductsTask().execute();
        return view;
    }

    private class FetchProductsTask extends AsyncTask<Void, Void, List<Product>> {
        Data_Base_Manager Manager = new Data_Base_Manager();

        @Override
        protected List<Product> doInBackground(Void... voids) {
            Manager.setProductsList();
            Manager.setOffersList();
            return Manager.getProductsList();
        }

        @Override
        protected void onPostExecute(List<Product> productList) {
            super.onPostExecute(productList);
            // Ορίστε τον προσαρμογέα για τη λίστα
            TextView textViewTotalCost = getActivity().findViewById(R.id.textViewTotalCost);
            Button emptyButton = getActivity().findViewById(R.id.clearBasketButton);
            textViewTotalCost.setVisibility(View.VISIBLE);
            List<Offers> offers = new ArrayList<>();
            adapter = new Product_Adapter(productList, offers ,false ,true , textViewTotalCost , getContext(), emptyButton , Manager.getoffersList());
            recyclerView.setAdapter(adapter);
        }
    }

}
