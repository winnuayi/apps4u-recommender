package com.ciheul.recommender;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.mongodb.DBObject;

@Path("/service")
public class RecommenderServlet {

  RecommenderEngine recommender;
  MongoDbSingleton db;

  @POST
  @Path("/recommendation")
  @Produces(MediaType.APPLICATION_JSON)
  public SurpriseItemsResponse getRecommendations(
      RecommendedItemsRequest userModel) throws Exception {

    System.out.println("RECOMMENDATION");
    System.out.println("androidId       : " + userModel.getAndroidID());
    System.out.println("  timeContext   : " + userModel.getTimeContext());

    db = MongoDbSingleton.getInstance();

    switch (userModel.getTimeContext()) {
    case RecommenderSingleton.PREF_N:
      recommender = RecommenderSingleton.getInstance()
        .getNightRecommender();
      break;
    case RecommenderSingleton.PREF_M:
      recommender = RecommenderSingleton.getInstance()
        .getMorningRecommender();
      break;
    case RecommenderSingleton.PREF_A:
      recommender = RecommenderSingleton.getInstance()
        .getAfternoonRecommender();
      break;
    case RecommenderSingleton.PREF_E:
      recommender = RecommenderSingleton.getInstance()
        .getEveningRecommender();
      break;
    case RecommenderSingleton.PREF_NO:
      recommender = RecommenderSingleton.getInstance()
        .getNoTimeContextRecommender();
      break;
    }

    // get user _id in database
    long userID;
    try {
      userID = db.getUserID(userModel.getAndroidID());
    } catch (NullPointerException e) {
      SurpriseItemsResponse errorResponse =
          new SurpriseItemsResponse(
            0,
            new ArrayList<Map<String, Object>>(),
            "androidID does not exist!");
      return errorResponse;
    }

    // CORE OF THIS PROJECT
    // execute user based recommendation
    List<RecommendedItem> recommendations =
        recommender.recommend(
          userID,
          userModel.getNumOfRecommendations());

    List<Map<String, Object>> recommendedApps = new ArrayList<Map<String, Object>>();
    for (RecommendedItem item : recommendations) {
      DBObject app = db.getItem(item.getItemID());
      if (app != null) {
        String uid = app.get("uid").toString();
        recommendedApps.add(app.toMap());

        DecimalFormat df = new DecimalFormat("###.##");
        System.out.println(
          "  recommendedApp: "
              + df.format(item.getValue()) + " | "
              + uid);
      }
    }
    System.out.println();

    SurpriseItemsResponse jsonRecommendations =
        new SurpriseItemsResponse(
          recommendedApps.size(),
          recommendedApps,
          "OK");

    return jsonRecommendations;
  }

  // TODO class name "SurpriseItemsResponse" needs to be generic
  // because it is also used by Search service
  @POST
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON)
  public SurpriseItemsResponse searchApps(
      RandomItemsRequest request) throws Exception {
    db = MongoDbSingleton.getInstance();

    List<Map<String, Object>> temp = db.findSearchItems(request);
    List<Map<String, Object>> searchItems = new ArrayList<Map<String, Object>>();

    System.out.println("category     : " + request.getCategory());
    System.out.println("rating value : " + request.getMinRatingValue());
    System.out.println("rating count : " + request.getMinRatingCount());
    System.out.println("installs min : " + request.getMinInstalls());
    
    if (temp.size() != 0) {
      // randomize the query
      Collections.shuffle(temp);

      // to slice the list, first decide the length
      int total = 0;
      if (request.getNumOfRecommendations() < temp.size())
        total = request.getNumOfRecommendations();
      else
        total = temp.size();

      // slice the result as user requests
      for (int i = 0; i < total; i++) {
//        Map<String, Object> app = temp.get(i);
//        String uid = app.get("name").toString();
//        System.out.println("  " + uid);
        searchItems.add(temp.get(i));
      }
    }

    System.out.println("result       : " + searchItems.size() + " of " + temp.size());
    System.out.println();

    SurpriseItemsResponse jsonSearchItems =
        new SurpriseItemsResponse(
          temp.size(),
          searchItems,
          "OK");

    return jsonSearchItems;
  }

  @POST
  @Path("/surpriseme")
  @Produces(MediaType.APPLICATION_JSON)
  public SurpriseItemsResponse getSurpriseApps(
      RecommendedItemsRequest request) throws Exception {
    db = MongoDbSingleton.getInstance();

    List<Map<String, Object>> surpriseItems = db.findSurpriseItems(request);

    SurpriseItemsResponse jsonSurpriseItems =
        new SurpriseItemsResponse(
          surpriseItems.size(),
          surpriseItems,
          "OK");

    return jsonSurpriseItems;
  }

  @POST
  @Path("/registration")
  @Consumes(MediaType.APPLICATION_JSON)
  public UserRegistrationResponse
      registerFirstTime(UserRegistrationRequest newUser)
          throws Exception {
    db = MongoDbSingleton.getInstance();

    int status = db.registerNewUser(newUser);

    UserRegistrationResponse response;
    if (status == 1) {
      System.out.println("NEW USER REGISTRATION");
      System.out.println("androidID      : " + newUser.getAndroidID());
      System.out.println("  timeOffset   : " + newUser.getTimeOffset());
      System.out.println();

      response = new UserRegistrationResponse("OK");
    } else {
      response =
          new UserRegistrationResponse("User has been registered.");
    }

    return response;
  }

  @POST
  @Path("/timeusage")
  @Consumes(MediaType.APPLICATION_JSON)
  public void getTimeUsages(TimeUsagesRequest timeUsages) throws Exception {
    db = MongoDbSingleton.getInstance();

    db.insertTimeusages(
      timeUsages.getAndroidID(),
      timeUsages.getTimeUsages());
  }

  // /**
  // * Get time context for user in different location based on time offset.
  // */
  // private int getTimeContext(int timeOffset) {
  // // interval between time context
  // final int T1 = 0;
  // final int T2 = 6;
  // final int T3 = 12;
  // final int T4 = 18;
  // final int T5 = 24;
  // // get current epoch
  // Calendar c = getCurrentUTC();
  // long nowUtcEpoch = c.getTimeInMillis();
  //
  // // get localtime epoch and set to Calendar instance
  // long nowLocalEpoch = nowUtcEpoch + timeOffset * 1000;
  // c.setTimeInMillis(nowLocalEpoch);
  //
  // // get hour localtime
  // int hourLocal = c.get(Calendar.HOUR_OF_DAY);
  // System.out.println("hourLocal   : " + hourLocal);
  //
  // // get time context name
  // int pref_coll = -1;
  // if (hourLocal >= T1 && hourLocal < T2)
  // pref_coll = RecommenderSingleton.PREF_N;
  // else if (hourLocal >= T2 && hourLocal < T3)
  // pref_coll = RecommenderSingleton.PREF_M;
  // else if (hourLocal >= T3 && hourLocal < T4)
  // pref_coll = RecommenderSingleton.PREF_A;
  // else if (hourLocal >= T4 && hourLocal < T5)
  // pref_coll = RecommenderSingleton.PREF_E;
  //
  // return pref_coll;
  // }
  //
  // /**
  // * Get current time in UTC
  // */
  // public Calendar getCurrentUTC() {
  // Calendar c = Calendar.getInstance();
  //
  // // get offset between UTC and localtime
  // // calculate DST too
  // TimeZone tz = c.getTimeZone();
  // int offset = tz.getRawOffset();
  // if (tz.inDaylightTime(new Date())) {
  // offset = offset + tz.getDSTSavings();
  // }
  //
  // // if we don't set the calendar, it will take curent localtime
  // long epoch = c.getTimeInMillis();
  // c.setTimeInMillis(epoch - offset);
  // return c;
  // }

}
