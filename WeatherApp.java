/*
 * WeatherApp.java
 *
 * This program retrieves and displays the current weather for a user-specified city 
 * using the OpenWeatherMap API. It supports multiple unit systems (Celsius, Fahrenheit, Kelvin),
 * provides the ability to view previously searched locations stored in a file, and logs new 
 * location searches to the same file named "locations.txt".
 *
 * Major Features:
 * - Prompts user for a city and desired temperature units
 * - Retrieves latitude and longitude via the OpenWeatherMap Geocoding API
 * - Gets current weather conditions (temperature, humidity, description) using coordinates
 * - Saves location info to a text file
 * - Optionally loads and displays a history of previously searched locations
 *
 * Required Libraries:
 * - java.net.http for HTTP requests
 * - com.google.gson for parsing JSON responses
 */

 import java.io.IOException;
 import java.net.URI;
 import java.net.http.HttpClient;
 import java.net.http.HttpRequest;
 import java.net.http.HttpResponse;
 import java.util.Scanner;
 
 import com.google.gson.JsonArray;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonParser;
 
 import java.io.BufferedWriter;
 import java.io.FileWriter;
 import java.io.FileReader;
 import java.io.BufferedReader;
 import java.io.File;
 
 public class WeatherApp {
 
     // API endpoints and key
     private static final String GEO_API = "http://api.openweathermap.org/geo/1.0/direct";
     private static final String WEATHER_API = "https://api.openweathermap.org/data/2.5/weather";
     private static final String API_KEY = "";
 
     @SuppressWarnings("resource")
     public static void main(String[] args) {
         Scanner scanner = new Scanner(System.in);
 
         // Ask user if they want to view location history
         System.out.print("Load location history? (y/n) ");
         String menuChoice = scanner.nextLine().trim();
 
         if (menuChoice.equalsIgnoreCase("y")) {
             loadHistoryFromFile();
         }
 
         // Prompt user to enter a city
         System.out.print("\nEnter city (e.g., 'Rexburg,ID,US' or 'London,GB'): ");
         String city = scanner.nextLine().trim();
 
         // Ask for preferred temperature unit
         System.out.print("Choose units (C = Celsius, F = Fahrenheit, K = Kelvin): ");
         String unitChoice = scanner.nextLine().trim().toUpperCase();
 
         // Convert user choice into OpenWeatherMap-compatible units string
         String units = switch (unitChoice) {
             case "C" -> "metric";
             case "F" -> "imperial";
             case "K" -> "";
             default -> {
                 System.out.println("Invalid unit, defaulting to Celsius.");
                 yield "metric";
             }
         };
 
         // Try to retrieve coordinates and weather data
         try {
             double[] coords = getCoordinates(city);
             if (coords != null) {
                 getCurrentWeather(coords[0], coords[1], units);
             } else {
                 System.out.println("Could not find that city. Try again.");
             }
         } catch (Exception e) {
             System.out.println("An error occurred: " + e.getMessage());
         }
     }


 
     /**
      * Queries the OpenWeatherMap Geocoding API to get the latitude and longitude of a city.
      * Prints location details and saves them to a local file.
      *
      * Parameter: city - Name of the city to look up (e.g., "Rexburg,ID,US")
      * returns an array containing [latitude, longitude], or null if not found
      */
     private static double[] getCoordinates(String city) throws IOException, InterruptedException {
         String url = GEO_API + "?q=" + city + "&limit=1&appid=" + API_KEY;
 
         // Create HTTP client and send request
         HttpClient client = HttpClient.newHttpClient();
         HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
         HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
 
         // Check for errors
         if (response.statusCode() != 200) {
             System.out.println("Error fetching coordinates. Status code: " + response.statusCode());
             return null;
         }
 
         // Parse JSON response
         JsonArray result = JsonParser.parseString(response.body()).getAsJsonArray();
 
         if (result.size() > 0) {
             JsonObject location = result.get(0).getAsJsonObject();
 
             // Extract relevant fields
             String name = location.get("name").getAsString();
             double lat = location.get("lat").getAsDouble();
             double lon = location.get("lon").getAsDouble();
             String country = location.get("country").getAsString();
             String state = location.has("state") ? location.get("state").getAsString() : "N/A";
 
             // Print location information
             System.out.println("\n-----------------------------------------------------------");
             System.out.println("\t" + name + ", " + state + ", " + country);
             System.out.println("\t" + lat + ", " + lon);
 
             // Save location to file
             saveToFile(name, state, country, lat, lon);
 
             return new double[]{lat, lon};
         } else {
             System.out.println("No results found.");
             return null;
         }
     }
 


     /**
      * Fetches and displays current weather information for the given coordinates.
      *
      * Parameters:
      *     lat   Latitude
      *     lon   Longitude
      *     units Units format ("metric", "imperial", or "")
      */
     private static void getCurrentWeather(double lat, double lon, String units) throws IOException, InterruptedException {
         String url = WEATHER_API + "?lat=" + lat + "&lon=" + lon + "&units=" + units + "&appid=" + API_KEY;
 
         // Create and send HTTP request
         HttpClient client = HttpClient.newHttpClient();
         HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
         HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
 
         // Handle response errors
         if (response.statusCode() != 200) {
             System.out.println("Error fetching weather data. Status code: " + response.statusCode());
             return;
         }
 
         // Parse weather data
         JsonObject weatherJson = JsonParser.parseString(response.body()).getAsJsonObject();
 
         double temperature = weatherJson.getAsJsonObject("main").get("temp").getAsDouble();
         int humidity = weatherJson.getAsJsonObject("main").get("humidity").getAsInt();
         String description = weatherJson.getAsJsonArray("weather")
                 .get(0).getAsJsonObject()
                 .get("description").getAsString();
 
         // Select symbol for temperature unit
         String unitSymbol = switch (units) {
             case "metric" -> "°C";
             case "imperial" -> "°F";
             case "" -> "K";
             default -> {
                 System.out.println("Invalid unit, defaulting to Celsius.");
                 yield "°C";
             }
         };
 
         // Display weather results
         System.out.println("\n\tTemperature: " + temperature + " " + unitSymbol);
         System.out.println("\tConditions: " + description);
         System.out.println("\tHumidity: " + humidity + "%");
         System.out.println("-----------------------------------------------------------");
     }
 


     /**
      * Appends a location entry to the "locations.txt" file.
      * Parameters:
      *     city    City name
      *     state   State (or "N/A")
      *     country Country code
      *     lat     Latitude
      *     lon     Longitude
      */
     private static void saveToFile(String city, String state, String country, double lat, double lon) {
         String entry = city + ", " + state + ", " + country + ", " + lat + ", " + lon;
         try (BufferedWriter writer = new BufferedWriter(new FileWriter("locations.txt", true))) {
             writer.write(entry);
             writer.newLine();
         } catch (IOException e) {
             System.out.println("Error writing to file: " + e.getMessage());
         }
     }
 


     /**
      * Reads and prints the history of searched locations from "locations.txt", if available.
      */
     private static void loadHistoryFromFile() {
         System.out.println("\n-----------------------------------------------------------");
         File file = new File("locations.txt");
 
         // Check if the file exists
         if (!file.exists()) {
             System.out.println("No previous history found.");
             System.out.println("-----------------------------------------------------------");
             return;
         }
 
         // Read and print each line of the file
         System.out.println("Location History:");
         try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
             String line;
             while ((line = reader.readLine()) != null) {
                 System.out.println("\t" + line);
             }
         } catch (IOException e) {
             System.out.println("Error reading from file: " + e.getMessage());
         }
 
         System.out.println("-----------------------------------------------------------");
     }
 }
 
