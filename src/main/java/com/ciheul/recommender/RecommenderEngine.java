package com.ciheul.recommender;

import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class RecommenderEngine implements Recommender {

    private final Recommender recommender;
    private final int numOfNeighbors = 7;

    public RecommenderEngine(DataModel dataModel) throws TasteException {
        // calculate all similarities among users
        UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);

        // get two top similarities
        UserNeighborhood neighborhood =
                new NearestNUserNeighborhood(
                        numOfNeighbors, similarity, dataModel);

        // create recommender instance
        recommender = new GenericUserBasedRecommender(
                dataModel, neighborhood, similarity);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany)
            throws TasteException {
        return recommender.recommend(userID, howMany);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany,
            IDRescorer rescorer)
            throws TasteException {
        return recommender.recommend(userID, howMany, rescorer);
    }

    @Override
    public float estimatePreference(long userID, long howMany)
            throws TasteException {
        return recommender.estimatePreference(userID, howMany);
    }

    @Override
    public void setPreference(long userID, long itemID, float value)
            throws TasteException {
        recommender.removePreference(userID, userID);
    }

    @Override
    public void removePreference(long userID, long itemID)
            throws TasteException {
        recommender.removePreference(userID, itemID);
    }

    @Override
    public DataModel getDataModel() {
        return recommender.getDataModel();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        recommender.refresh(alreadyRefreshed);
    }
}
