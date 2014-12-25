package com.ciheul.recommender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

public class RecommenderSingleton {

    private static String RESOURCES_PATH; // = "/Users/hadoop/Documents/googleplay-recommender/operational/resources";

    // model
    public static String CSV_PREF_NO; // = RESOURCES_PATH + "/pref_no.csv";
    public static String CSV_PREF_N; // = RESOURCES_PATH + "/pref_n.csv";
    public static String CSV_PREF_M; // = RESOURCES_PATH + "/pref_m.csv";
    public static String CSV_PREF_A; // = RESOURCES_PATH + "/pref_a.csv";
    public static String CSV_PREF_E; // = RESOURCES_PATH + "/pref_e.csv";

    public static final int PREF_N = 0;
    public static final int PREF_M = 1;
    public static final int PREF_A = 2;
    public static final int PREF_E = 3;
    public static final int PREF_NO = -1;

    private static RecommenderSingleton instance;
    
    private final RecommenderEngine nightRecommender;
    private final RecommenderEngine morningRecommender;
    private final RecommenderEngine afternoonRecommender;
    private final RecommenderEngine eveningRecommender;
    private final RecommenderEngine noTimeContextRecommender;
    
    DataModel nightDataModel;
    DataModel morningDataModel;
    DataModel afternoonDataModel;
    DataModel eveningDataModel;
    DataModel noTimeContextDataModel;

    private RecommenderSingleton()
            throws IOException, URISyntaxException, TasteException {
        System.out.println("[ciheul] Initialize recommender.");

        String hostname = InetAddress.getLocalHost().getHostName();
        if (hostname.equals("bontang.local"))
            RESOURCES_PATH = "/Users/hadoop/Documents/googleplay-recommender/operational/resources";
        else if (hostname.equals("localhost.localdomain"))
            RESOURCES_PATH = "/home/winnuayi/Documents/apps4u/resources";
        
        System.out.println("hostname     : " + hostname);
        System.out.println("Resource path: " + RESOURCES_PATH);
        
        CSV_PREF_NO = RESOURCES_PATH + "/pref_no.csv";
        CSV_PREF_N = RESOURCES_PATH + "/pref_n.csv";
        CSV_PREF_M = RESOURCES_PATH + "/pref_m.csv";
        CSV_PREF_A = RESOURCES_PATH + "/pref_a.csv";
        CSV_PREF_E = RESOURCES_PATH + "/pref_e.csv";
        
        // create recommender without time context
        // URL url = getClass().getClassLoader().getResource(CSV_PREF_NO);
        // DataModel dataModel = new FileDataModel(new File(url.toURI()));

        try {
            // dataModel = new FileDataModel(new File(RESOURCES_PATH +
            // "/intro.csv"));
            nightDataModel = new FileDataModel(new File(CSV_PREF_N));
            morningDataModel = new FileDataModel(new File(CSV_PREF_M));
            afternoonDataModel = new FileDataModel(new File(CSV_PREF_A));
            eveningDataModel = new FileDataModel(new File(CSV_PREF_E));
            noTimeContextDataModel = new FileDataModel(new File(CSV_PREF_NO));

            System.out.println("[ciheul] Num of Users: "
                    + nightDataModel.getNumUsers());
            System.out.println("[ciheul] Num of Items: "
                    + nightDataModel.getNumItems());
        } catch (NullPointerException e) {
            System.out.println("[ciheul] Null pointer.");
        } catch (FileNotFoundException e) {
            System.out.println("[ciheul] Wrong resource path.");
        }

        nightRecommender = new RecommenderEngine(nightDataModel);
        morningRecommender = new RecommenderEngine(morningDataModel);
        afternoonRecommender = new RecommenderEngine(afternoonDataModel);
        eveningRecommender = new RecommenderEngine(eveningDataModel);
        noTimeContextRecommender = new RecommenderEngine(noTimeContextDataModel);
    }

    public static synchronized RecommenderSingleton getInstance()
            throws IOException, URISyntaxException, TasteException {
        if (instance == null) {
            instance = new RecommenderSingleton();
        }

        return instance;
    }

    public RecommenderEngine getNightRecommender() {
        return nightRecommender;
    }

    public RecommenderEngine getMorningRecommender() {
        return morningRecommender;
    }

    public RecommenderEngine getAfternoonRecommender() {
        return afternoonRecommender;
    }

    public RecommenderEngine getEveningRecommender() {
        return eveningRecommender;
    }

    public RecommenderEngine getNoTimeContextRecommender() {
        return noTimeContextRecommender;
    }

}