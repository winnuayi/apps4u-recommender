package com.ciheul.recommender;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ciheul.recommender.TimeUsagesRequest.TimeUsageModel;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoDbSingleton {

  private static MongoDbSingleton instance;
  private final Mongo conn;

  DBCollection coll_user;
  DBCollection coll_item;
  DBCollection coll_item_buffer;
  DBCollection coll_counter;
  DBCollection coll_timeusage;

  // mongodb information
  public static final String HOST = "127.0.0.1";
  public static final String DATABASE = "googleplayrecommender";

  // collections
  public static final String COLL_USER = "users";
  public static final String COLL_ITEM = "items";
  public static final String COLL_ITEM_BUFFER = "items_buffer";
  public static final String COLL_COUNTER = "counters";
  public static final String COLL_TIMEUSAGE = "timeusage";

  private MongoDbSingleton() throws UnknownHostException, MongoException {
    conn = new Mongo(HOST);
    DB db = conn.getDB(DATABASE);

    coll_user = db.getCollection(COLL_USER);
    coll_item = db.getCollection(COLL_ITEM);
    coll_item_buffer = db.getCollection(COLL_ITEM_BUFFER);
    coll_counter = db.getCollection(COLL_COUNTER);
    coll_timeusage = db.getCollection(COLL_TIMEUSAGE);
  }

  public static synchronized MongoDbSingleton getInstance()
      throws UnknownHostException, MongoException {
    if (instance == null) {
      instance = new MongoDbSingleton();
      System.out.println("[ciheul] Initialize Mongodb.");
    }

    return instance;
  }

  public long getUserID(
      String androidID) {
    // query to find specified 'androidID'
    BasicDBObject query = new BasicDBObject();
    query.put("androidID", androidID);

    // get userID that is long value from ObjectId in mongodb
    DBObject result = coll_user.findOne(query);
    long userId = Long.parseLong(result.get("_id").toString());
    return result != null ? userId : null;
  }

  public DBObject getItem(
      long itemID) {
    // query to find specified 'androidID'
    BasicDBObject query = new BasicDBObject();
    query.put("_id", itemID);

    // SELECT clause
    BasicDBObject select = new BasicDBObject();
    select.put("uid", 1);
    select.put("name", 1);
    select.put("rating_value", 1);
    select.put("rating_count", 1);
    select.put("category", 1);
    select.put("img_src", -1);
    select.put("_id", 0);

    // get userId that is long value from ObjectId in mongodb
    DBObject item = coll_item.findOne(query, select);
    // if (item == null) {
    // item = coll_item_buffer.findOne(query);
    // }
    return item != null ? item : null;
  }

  public int registerNewUser(
      UserRegistrationRequest newUser) {
    BasicDBObject query = new BasicDBObject();
    query.put("androidID", newUser.getAndroidID());

    // DBCursor cursor = coll_user.find(query);
    DBObject alreadyUser = coll_user.findOne(query);
    // if (cursor.size() == 0) {

    // -1 == null
    int status = -1;
    if (alreadyUser == null) {
      BasicDBObject queryCounter = new BasicDBObject();
      queryCounter.put("_id", "users");
      DBObject counter = coll_counter.findOne(queryCounter);

      // TODO the data type in mongodb _id looks not consistent
      // need to investigate.
      // Object as Double occurs when sending data from real smartphone
      // Object as Integer occurs looks like from simulator
      int lastID = -100;
      Object o = counter.get("last_id");
      try {
        lastID = ((Double) o).intValue();
      } catch (ClassCastException e) {
        lastID = ((Integer) o).intValue();
      }
      int newlastID = lastID + 1;

      // maybe in the future, there would be more fields
      // that is why query and user are two different objects
      BasicDBObject user = new BasicDBObject();
      user.put("_id", newlastID);
      user.put("androidID", newUser.getAndroidID());
      user.put("timeOffset", newUser.getTimeOffset());
      coll_user.insert(user);

      BasicDBObject updateSet = new BasicDBObject(
        "$set",
        new BasicDBObject("last_id", newlastID));
      coll_counter.update(queryCounter, updateSet);

      // registration successful
      status = 1;
    } else
      // already registered
      status = 0;

    return status;
  }

  public void insertTimeusages(
      String androidID,
      List<TimeUsageModel> listTimeUsages) {

    // display current time receiving data
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Calendar now = Calendar.getInstance();

    // display to console
    System.out.println("TIMEUSAGE");
    System.out.println("current    : " + dateFormat.format(now.getTime()));
    System.out.println("androidID  : " + androidID);
    System.out.println("timeUsage  : " + listTimeUsages.size());

    for (TimeUsageModel timeUsage : listTimeUsages) {
      // in seconds
      long startEpoch = (long) timeUsage.getStartEpoch();

      // convert to milliseconds
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(startEpoch * 1000);

      // app's duration
      int duration = timeUsage.getEndEpoch() - timeUsage.getStartEpoch();

      // construct the model
      BasicDBObject doc = new BasicDBObject();
      doc.put("androidID", androidID);
      doc.put("startEpoch", timeUsage.getStartEpoch());
      doc.put("endEpoch", timeUsage.getEndEpoch());
      doc.put("hourOfDay", c.get(Calendar.HOUR_OF_DAY));
      doc.put("dayOfWeek", c.get(Calendar.DAY_OF_WEEK)); // sunday = 1;
                                                         // saturday = 7
      doc.put("duration", duration);
      doc.put("appName", timeUsage.getAppName());

      // insert to database
      coll_timeusage.insert(doc);

      System.out.printf(" - %d | %d | %5d | %s\n",
        c.get(Calendar.DAY_OF_WEEK),
        startEpoch,
        duration,
        timeUsage.getAppName());
    }
    System.out.println();
  }

  public List<Map<String, Object>> findSearchItems(
      RandomItemsRequest request) {
    // set up WHERE
    // min < rating_value < max
    // min < rating_count < max
    // installs > min
    // category == nameCategory
    BasicDBObject where = new BasicDBObject();

    // BasicDBObject rangeRatingValue = new BasicDBObject();
    // rangeRatingValue.put("$gte", request.getMinRatingValue());
    // rangeRatingValue.put("$lte", request.getMaxRatingValue());

    BasicDBObject rangeRatingCount = new BasicDBObject();
    rangeRatingCount.put("$gte", request.getMinRatingCount());
    rangeRatingCount.put("$lte", request.getMaxRatingCount());

    // if (!request.getMinRatingValue().equals("0.0")) {
    where.put("rating_value", Double.parseDouble(request.getMinRatingValue()));
    // }

    where.put("rating_count", rangeRatingCount);
    where.put(
      "installs_min", new BasicDBObject("$gte", request.getMinInstalls()));

    if (!request.getCategory().equals("All")) {
      where.put("category", request.getCategory());
    }

    BasicDBObject select = new BasicDBObject();
    select.put("uid", 1);
    select.put("name", 1);
    select.put("rating_value", 1);
    select.put("rating_count", 1);
    select.put("category", 1);
    select.put("img_src", 1);
    select.put("_id", 0);

    DBCursor cursor = coll_item.find(where, select);

    // display current time receiving data
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Calendar now = Calendar.getInstance();

    // display to console
    System.out.println("SEARCH APPS");
    System.out.println("current      : " + dateFormat.format(now.getTime()));
    System.out.println("androidID    : " + request.getAndroidID());

    List<Map<String, Object>> randomItems = new ArrayList<Map<String, Object>>();

    while (cursor.hasNext()) {
      DBObject app = cursor.next();
      randomItems.add(app.toMap());
    }

    return randomItems;
  }

  public List<Map<String, Object>>
      findSurpriseItems(RecommendedItemsRequest request) {
    // display current time receiving data
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Calendar now = Calendar.getInstance();

    // display to console
    System.out.println("SURPRISE ME!");
    System.out.println("current    : " + dateFormat.format(now.getTime()));
    System.out.println("androidID  : " + request.getAndroidID());
    System.out.println("randomItems: ");

    // get random numbers. hard code from 0 to coll.getCount()
    Random random = new Random();
    List<Integer> randomIds = new ArrayList<Integer>();
    for (int i = 0; i < request.getNumOfRecommendations(); i++) {
      randomIds.add(random.nextInt((int) coll_item.getCount()));
    }

    // a container for our surprise items
    List<Map<String, Object>> surpriseItems =
        new ArrayList<Map<String, Object>>();

    // retrieve a number of random apps from database
    // and push to the container
    for (int i = 0; i < randomIds.size(); i++) {
      // WHERE clause
      BasicDBObject where = new BasicDBObject();
      where.put("_id", randomIds.get(i));

      // SELECT clause
      BasicDBObject select = new BasicDBObject();
      select.put("uid", 1);
      select.put("name", 1);
      select.put("rating_value", 1);
      select.put("rating_count", 1);
      select.put("category", 1);
      select.put("img_src", -1);
      select.put("_id", 0);

      DBObject app = coll_item.findOne(where, select);

      // surpriseItems.add(app.get("uid").toString());
      surpriseItems.add(app.toMap());
      System.out.println("  " + app.get("uid").toString());
      // System.out.println("  " + app.toString());
    }
    System.out.println();

    return surpriseItems;
  }

}
