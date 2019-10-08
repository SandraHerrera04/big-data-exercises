package nearsoft.academy.bigdata.recommendation;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
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
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class MovieRecommender {
    private String path;
    int information;

    //To save productID with productStringID
    BiMap<String, Long> products ;
    //To save UserID with userStringID
    BiMap<String, Long> users ;
    long totalReviews = 0;
    PrintWriter csvWriter ;
    String product;
    String user;

    public static void main(String[] args) throws IOException, TasteException {

    }
    public MovieRecommender(String path) throws IOException, TasteException {
        this.path = path;
        this.totalReviews = 0;
        this.information = 0;
        this.users = HashBiMap.create();
        this.products = HashBiMap.create();
        this.csvWriter= new PrintWriter("dataset.csv");
        BufferedReader reader;
        InputStream stream = new GZIPInputStream(new FileInputStream(path));
        reader = new BufferedReader(new InputStreamReader(stream, "US-ASCII"));
        String line = reader.readLine();
        long productID = 0;
        long userID = 0;
        while (line != null ) {
            if (line.startsWith("product/productId:")) {
                product = line.split(": ")[1];
                if (!products.containsKey(product)) {
                    products.put(product, productID);
                    productID++;
                }

            }else if (line.startsWith("review/userId:")) {
                user= line.split(": ")[1];
                if (!users.containsKey(user)) {
                    users.put(user, userID);
                    userID++;
                }

            }else if (line.startsWith("review/score:")) {
                this.totalReviews ++;
                //write into CSV to create model
                csvWriter.println(users.get(user) + "," + products.get(product) + "," + line.split(": ")[1]);

            }
            line = reader.readLine();
        }
        csvWriter.close();
    }

    public long getTotalReviews() {
        return this.totalReviews;
    }

    public long getTotalProducts() {
        return this.products.size();
    }

    public long getTotalUsers() {
        return this.users.size();
    }

    public List<String> getRecommendationsForUser(String userID) throws IOException,TasteException {
        DataModel model = new FileDataModel(new File("/Users/sandraherrera/Downloads/big-data-exercises/dataset.csv"));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        List <String> RecommendedProducts = new ArrayList<String>();
        List<RecommendedItem> recommendations = recommender.recommend(users.get(userID),3);
        for (RecommendedItem recomendation:recommendations)
        {
            long value = (long) recomendation.getItemID();
            RecommendedProducts.add(products.inverse().get(value)  );
        }

        return RecommendedProducts;
    }
}
