package testPackage;

import static spark.Spark.get;
import static spark.Spark.port;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestSpark {
	public static ArrayList<Album> allAlbums = new ArrayList<Album>();
	public final static String ALBUMFILE = "C:\\My Data\\AlbumFile.txt";

	public static void main(String[] args) {

		port(3000);

		
		get("/album", (request, response) -> {
			System.out.println("request mode");

			int i = Integer.parseInt(request.queryParams("id"));
			Album matchedAlbum = (getAlbumById(i));
			if (matchedAlbum == null) {
				return "No match found";
			} else {
				return matchedAlbum.name + " " + matchedAlbum.artist + " " + matchedAlbum.year + " "
						+ matchedAlbum.genre;
			}
		});

		
		get("/album/create", (request, response) -> {
			String title = request.queryParams("title");
			String year = request.queryParams("year");
			String artist = request.queryParams("artist");
			String genre = request.queryParams("genre");

			String success = addAlbum(title, genre, artist, year);
			writeAlbumFile();
			return createHTML();
		});
		
		
		get("/", (request, response) -> {
			return createHTML();
		});
		
		get("/prettyjson", (req, res) -> {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();;
            System.out.println(gson.toJson(allAlbums));
            return gson.toJson(allAlbums);
            //return "{\"title\": \"Gone with the Wind\", \"year\": 1939}";
            
		});
       get("/json", (req, res) -> {
    	   
            //Gson gson = new GsonBuilder().setPrettyPrinting().create();;
            //System.out.println(gson.toJson(allAlbums));
          //  return gson.toJson(allAlbums);
            System.out.println("Here1");
            String html = "<!DOCTYPE html><html><head><title>Albums</title></head><body id = 'demo'><script>" +
            "console.log('here');" + 
            "var xhr = new XMLHttpRequest();\n" +
            "xhr.open('GET', '/prettyjson');" +
            "xhr.onload = function(evt) {" +
            "if (xhr.status === 200) {" +
              "var response = JSON.parse(xhr.responseText);"+
              "for(var i = 0; i < response.length; i++) {"+
                 "var div = document.createElement('div');" +
                 "div.innerHTML = response[i].name + ', ' + response[i].artist + ', ' + response[i].year;" +
                 "document.body.appendChild(div);" +
              "}" +

                   "console.log(response);" +
                "} else {" +
                    "alert('Request failed.  Returned status of ' + xhr.status);" +
                "}" +
            "};" +
            "xhr.send();" +
            "</script></body></html>";
            System.out.println(html);
            return html;
        });
	       
	       
	       
		try {
			createAlbums(allAlbums);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void createAlbums(ArrayList<Album> albums) throws IOException, ClassNotFoundException {

		FileInputStream fis = new FileInputStream(ALBUMFILE);
		ObjectInputStream ois = new ObjectInputStream(fis);

		allAlbums = (ArrayList<Album>) ois.readObject();

		ois.close();
		fis.close();
	}

	public static Album getAlbumById(int index) {
		for (int i = 0; i < allAlbums.size(); i++) {
			Album album = allAlbums.get(i);
			if (album.id == index) {
				return allAlbums.get(i);
			}
		}
		return null;
	}

	public static String addAlbum(String name, String genre, String artist, String year) {
		int max = 0;
		for (int i = 0; i < allAlbums.size(); i++) {
			Album album = allAlbums.get(i);
			if (album.id > max) {
				max = album.id;
			}
		}

		Album newAlbum = new Album(max + 1, name, genre, artist, year);
		allAlbums.add(newAlbum);
		return "Success";
	}

	public static void writeAlbumFile() throws Exception {

		FileOutputStream fos = new FileOutputStream(ALBUMFILE);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(allAlbums);

		fos.close();
		oos.close();
	}
	
	public static String createHTML(){
		JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/other.jTwig");
        JtwigModel model = JtwigModel.newModel().with("albums", allAlbums);

        return template.render(model);
		
	}
		//String html = "<!DOCTYPE html><html><head><title>Albums</title></head><body>";
//		String html = "<!DOCTYPE html><html><head><title>Albums</title>" + 
//			"<style>body{background: red;" +
//			"background: -webkit-repeating-radial-gradient(red, yellow 10%, green 15%);" +
//			"background: -o-repeating-radial-gradient(red, yellow 10%, green 15%);" +
//			"background: -moz-repeating-radial-gradient(red, yellow 10%, green 15%);" +
//			" background: repeating-radial-gradient(red, yellow 10%, green 15%);" +
//			"</style></head><body><h1>Dave and Nick's Favorite Albums</h1><table border='3'>" +
//			"<tr><th>Album</th><th>Artist</th><th>Year</th><th>Genre</th></tr>";
//		
//		for(int i = 0; i < allAlbums.size(); i++){
//			Album album = allAlbums.get(i);
//			html = html + "<tr><td>" + album.name + "</td><td>" + album.artist + "</td><td>" +
//					album.year + "</td><td>" + album.genre + "</td></tr>";
//		}
//		
//		html = html + "</table></body></html>";
//		return html;
//	}
}

class Album implements Serializable {
	int id;
	String name;
	String genre;
	String artist;
	String year;

	public Album(int id, String name, String genre, String artist, String year) {
		this.id = id;
		this.name = name;
		this.genre = genre;
		this.artist = artist;
		this.year = year;
	}
}