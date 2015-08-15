package simtraffic.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class RepositoryMongodb implements Repository {
    String host = null;
    int port;
    String databaseName = null;
    String collectionName = null;
    
    MongoClient mongoClient = null;
    MongoCollection<Document> collection = null;
    
    public RepositoryMongodb(String host, int port, String database, String collectionName){
	this.host = host;
	this.port = port;
	this.databaseName = database;
	this.collectionName = collectionName;
    }
    @Override
    public Repository open() {
	mongoClient = new MongoClient( host, port);
	MongoDatabase db = mongoClient.getDatabase(databaseName);
	this.collection = db.getCollection(collectionName);
	return this;
	
    }
    @Override
    public Repository drop() {
	collection.drop();
	return this;
    }

    @Override
    public Repository close() {
	mongoClient.close();
	return this;
    }
	
    @Override
    public void writeVehicle(Vehicle v) {
	try{
	    Document document = new Document("vid", v.getId()).append(
		    "behaviour", v.getBehaviour().toString());
	    List<Document> list = new ArrayList<Document>();
	    SortedMap<Integer, Position> journey = v.getJourney();
	    for (Map.Entry<Integer, Position> e : journey.entrySet()) {
		Integer timeCount = e.getKey();
		Position p = e.getValue();
		int y = p.getRowCoord();
		int x = p.getColumnCoord();
		int s = p.getSegment().getId();
		Document vDoc = new Document("x", x).append("y", y)
			.append("t", timeCount).append("segid", s);
		list.add(vDoc);
	    }
	    document.append("journey", list);
	    collection.insertOne(document);

	}catch(Exception e){
	    e.printStackTrace();
	}

    }
    @Override
    public void generateJson(File file){
	FindIterable<Document> result = collection.find();
	
	for(Document d : result){
	    
	    List<Document> journey = d.get("journey", List.class);
	 
	    for(Document x : journey){
		System.out.println(x.getInteger("t"));
		System.out.println(x.toJson());
	    }
	}
	
	
    }
  

  

}
