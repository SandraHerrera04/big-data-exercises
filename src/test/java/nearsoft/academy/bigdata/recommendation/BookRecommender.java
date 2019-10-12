package nearsoft.academy.bigdata.recommendation;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookRecommender {
    List <String> RecommendedProducts ;
    BiMap<String, Long> products ;
    BiMap<String, Long> users ;

    private String path;
    public BookRecommender(String path, BiMap<String, Long> users, BiMap<String, Long> products)
    {
        this.RecommendedProducts = new ArrayList<String>();
        this.path = path;
        this.users = users;
        this.products = products;
    }

    public List<String> getRecommendationsForUser( String userID) throws IOException, TasteException {
        DataModel model = new FileDataModel(new File(this.path));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        List <String> RecommendedProducts = new ArrayList<String>();
        List<RecommendedItem> recommendations = recommender.recommend(this.users.get(userID),3);
        for (RecommendedItem recomendation:recommendations)
        {
            long value = (long) recomendation.getItemID();
            this.RecommendedProducts.add(this.products.inverse().get(value)  );
        }

        return this.RecommendedProducts;
    }


}
