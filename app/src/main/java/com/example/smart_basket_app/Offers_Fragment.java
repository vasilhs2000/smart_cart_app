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
public class Offers_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private Product_Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new Offers_Fragment.FetchProductsTask().execute();
        return view;
    }

    private class FetchProductsTask extends AsyncTask<Void, Void, List<Product>> {
        Data_Base_Manager Manager = new Data_Base_Manager();
        List<Offers> offers = new ArrayList<>();
        @Override
        protected List<Product> doInBackground(Void... voids) {

            Manager.setOffersList();
            for(int j=0;j<Manager.getoffersList().size();j++)
                offers.add(Manager.getoffersList().get(j));
            List <Long> offersIds = new ArrayList<>();
            for(int i=0;i<Manager.getoffersList().size();i++)
                offersIds.add(Manager.getoffersList().get(i).getId());

            Manager.setOfferProductsList(offersIds);
            return Manager.getOfferProductsList();
        }


        @Override
        protected void onPostExecute(List<Product> productList) {
            super.onPostExecute(productList);
            // Ορίστε τον προσαρμογέα για τη λίστα
            TextView textViewTotalCost = getActivity().findViewById(R.id.textViewTotalCost);
            Button emptyButton = getActivity().findViewById(R.id.clearBasketButton);
            adapter = new Product_Adapter(productList , offers , false ,false , textViewTotalCost , getContext() , emptyButton , Manager.getoffersList() );
            recyclerView.setAdapter(adapter);
        }
    }

}
