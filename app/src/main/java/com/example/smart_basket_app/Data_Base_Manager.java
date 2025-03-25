package com.example.smart_basket_app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
public class Data_Base_Manager {

    private Integer basketConfirmation;
    private List<Product> productsList;
    private List<Product> basketList;
    private List<Offers> offersList;
    private List<Product> offerProductsList;
    private Float totalCost ;


    public Data_Base_Manager() {}
    public void setProductsList() {
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://192.168.1.12:8443/api/products");
            urlConnection = (HttpsURLConnection) url.openConnection();

            // Ρύθμιση SSL για να αποδεχτεί το self-signed certificate
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // Φόρτωση πιστοποιητικού από αρχείο (.crt)
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            try (InputStream certInput = getClass().getClassLoader().getResourceAsStream("res/raw/certificate.crt")) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate ca = cf.generateCertificate(certInput);
                keyStore.setCertificateEntry("ca", ca);
            }

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Προσθήκη custom HostnameVerifier για να παρακάμψουμε την επαλήθευση hostname
            urlConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals("192.168.1.12");  // Ελέγχει μόνο το IP που χρησιμοποιείς
                }
            });
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                Gson gson = new Gson();
                Type productListType = new TypeToken<List<Product>>() {}.getType();
                this.productsList = gson.fromJson(response.toString(), productListType);
                Collections.sort(this.productsList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return Integer.compare(p1.getCategoryId(), p2.getCategoryId()); // Αυξουσα ταξινόμηση
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setBasketConfirmation(){
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://192.168.1.12:8443/api/basketConfirmation/" + Scan_QR_Activity.basketID);
            urlConnection = (HttpsURLConnection) url.openConnection();

            // Ρύθμιση SSL για να αποδεχτεί το self-signed certificate
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // Φόρτωση πιστοποιητικού από αρχείο (.crt)
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            try (InputStream certInput = getClass().getClassLoader().getResourceAsStream("res/raw/certificate.crt")) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate ca = cf.generateCertificate(certInput);
                keyStore.setCertificateEntry("ca", ca);
            }

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Προσθήκη custom HostnameVerifier για να παρακάμψουμε την επαλήθευση hostname
            urlConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals("192.168.1.12");  // Ελέγχει μόνο το IP που χρησιμοποιείς
                }
            });

            // Ορισμός της μεθόδου αίτησης και σύνδεση
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Έλεγχος κωδικού απάντησης
            int responseCode = urlConnection.getResponseCode();
            System.out.println("response: "+responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // Ανάγνωση της απόκρισης από το InputStream
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                // Μετατροπή της απόκρισης σε ακέραιο
                this.basketConfirmation = Integer.parseInt(response.toString());
            } else {
                System.out.println("Error: Response code " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void lockBasket(){
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://192.168.1.12:8443/api/basket/confirm/4546/2");
            urlConnection = (HttpsURLConnection) url.openConnection();

            // Ρύθμιση SSL για να αποδεχτεί το self-signed certificate
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // Φόρτωση πιστοποιητικού από αρχείο (.crt)
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            try (InputStream certInput = getClass().getClassLoader().getResourceAsStream("res/raw/certificate.crt")) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate ca = cf.generateCertificate(certInput);
                keyStore.setCertificateEntry("ca", ca);
            }

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Προσθήκη custom HostnameVerifier για να παρακάμψουμε την επαλήθευση hostname
            urlConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals("192.168.1.12");  // Ελέγχει μόνο το IP που χρησιμοποιείς
                }
            });

            // Ορισμός της μεθόδου αίτησης και σύνδεση
            urlConnection.setRequestMethod("PUT");
            urlConnection.connect();
            // Έλεγχος κωδικού απάντησης
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                System.out.println("To aithma pragamtopoihthke kanonika");
            } else {
                System.out.println("Error: Response code " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setBasketList() {
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://192.168.1.12:8443/api/basket/" + Scan_QR_Activity.basketID);
            urlConnection = (HttpsURLConnection) url.openConnection();

            // Ρύθμιση SSL για να αποδεχτεί το self-signed certificate
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // Φόρτωση πιστοποιητικού από αρχείο (.crt)
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            try (InputStream certInput = getClass().getClassLoader().getResourceAsStream("res/raw/certificate.crt")) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate ca = cf.generateCertificate(certInput);
                keyStore.setCertificateEntry("ca", ca);
            }

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Προσθήκη custom HostnameVerifier για να παρακάμψουμε την επαλήθευση hostname
            urlConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals("192.168.1.12");  // Ελέγχει μόνο το IP που χρησιμοποιείς
                }
            });

            // Ορισμός της μεθόδου αίτησης και σύνδεση
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Έλεγχος κωδικού απάντησης
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // Ανάγνωση της απόκρισης από το InputStream
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Χρήση Gson για την ανάλυση της απόκρισης σε λίστα προϊόντων
                Gson gson = new Gson();
                Type productListType = new TypeToken<List<Product>>() {}.getType();
                this.basketList = gson.fromJson(response.toString(), productListType);
            } else {
                System.out.println("Error: Response code " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setOffersList() {
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://192.168.1.12:8443/api/offers");
            urlConnection = (HttpsURLConnection) url.openConnection();

            // Ρύθμιση SSL για να αποδεχτεί το self-signed certificate
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // Φόρτωση πιστοποιητικού από αρχείο (.crt)
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            try (InputStream certInput = getClass().getClassLoader().getResourceAsStream("res/raw/certificate.crt")) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate ca = cf.generateCertificate(certInput);
                keyStore.setCertificateEntry("ca", ca);
            }

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Προσθήκη custom HostnameVerifier για να παρακάμψουμε την επαλήθευση hostname
            urlConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals("192.168.1.12");  // Ελέγχει μόνο το IP που χρησιμοποιείς
                }
            });
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);

                }
                Gson gson = new Gson();
                Type offerListType = new TypeToken<List<Offers>>() {}.getType();
                this.offersList = gson.fromJson(response.toString(), offerListType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setOfferProductsList(List<Long> offersIds) {
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            StringBuilder offerIdsBuilder = new StringBuilder();
            for (Long id : offersIds) {
                offerIdsBuilder.append(id).append(",");
            }
            String offerIdsString = offerIdsBuilder.toString();
            if (offerIdsString.endsWith(",")) {
                offerIdsString = offerIdsString.substring(0, offerIdsString.length() - 1); // Αφαιρούμε το τελευταίο ','
            }

            URL url = new URL("https://192.168.1.12:8443/api/offers/products?offerIds=" + offerIdsString);
            urlConnection = (HttpsURLConnection) url.openConnection();

            // Ρύθμιση SSL για να αποδεχτεί το self-signed certificate
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // Φόρτωση πιστοποιητικού από αρχείο (.crt)
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            try (InputStream certInput = getClass().getClassLoader().getResourceAsStream("res/raw/certificate.crt")) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate ca = cf.generateCertificate(certInput);
                keyStore.setCertificateEntry("ca", ca);
            }

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Προσθήκη custom HostnameVerifier για να παρακάμψουμε την επαλήθευση hostname
            urlConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals("192.168.1.12");  // Ελέγχει μόνο το IP που χρησιμοποιείς
                }
            });
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                Gson gson = new Gson();
                Type productsListType = new TypeToken<List<Product>>() {}.getType();
                this.offerProductsList = gson.fromJson(response.toString(), productsListType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setTotalCost(){

        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://192.168.1.12:8443/api/totalCost/4546");
            urlConnection = (HttpsURLConnection) url.openConnection();

            // Ρύθμιση SSL για να αποδεχτεί το self-signed certificate
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // Φόρτωση πιστοποιητικού από αρχείο (.crt)
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            try (InputStream certInput = getClass().getClassLoader().getResourceAsStream("res/raw/certificate.crt")) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate ca = cf.generateCertificate(certInput);
                keyStore.setCertificateEntry("ca", ca);
            }

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Προσθήκη custom HostnameVerifier για να παρακάμψουμε την επαλήθευση hostname
            urlConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals("192.168.1.12");  // Ελέγχει μόνο το IP που χρησιμοποιείς
                }
            });
            // Ορισμός της μεθόδου αίτησης και σύνδεση
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            System.out.println("Connected to server");


            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                this.totalCost = Float.parseFloat(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Integer getBasketConfirmation(){
        return this.basketConfirmation;
    }
    public List<Product> getProductsList() {
        return productsList;
    }
    public List<Product> getBasketList() {
        return basketList;
    }
    public List<Offers> getoffersList() {
        return offersList;
    }
    public List<Product> getOfferProductsList(){return offerProductsList;}
    public float getTotalCost(){return totalCost;}
}
