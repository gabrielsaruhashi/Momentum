package shag.com.shag.Models;

import java.util.HashMap;

/**
 * Created by samrabelachew on 7/27/17.
 */

public class RecommendationValues {
    HashMap<String, Integer>  subCategoryMap;
    int categoryPoints;

    public HashMap<String, Integer> getSubCategoryMap() {
        return subCategoryMap;
    }

    public void setSubCategoryMap(HashMap<String, Integer> subCategoryMap) {
        this.subCategoryMap = subCategoryMap;
    }

    public int getCategoryPoints() {
        return categoryPoints;
    }

    public void setCategoryPoints(int categoryPoints) {
        this.categoryPoints = categoryPoints;
    }
}
