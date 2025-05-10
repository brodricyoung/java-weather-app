# Overview

I developed this weather application to learn and deepen my understanding of the Java programming language, especially in areas like HTTP networking, JSON parsing, file I/O, and working with third-party APIs. My goal was to apply object-oriented principles and Java’s standard libraries in a meaningful way while building a real-world utility.

This program allows users to input the name of a city and retrieve its current weather conditions using the OpenWeatherMap API. Users can select their preferred temperature units (Celsius, Fahrenheit, or Kelvin), and the app will display relevant information such as temperature, humidity, and a weather description. It also stores previously searched locations in a local file, giving users a sense of history and persistence.

By creating this project, I wanted to get experience with core Java syntax and practice working with JSON data structures and APIs. This process helped reinforce concepts like HTTP requests, exception handling, user input processing, and file operations.

[Software Demo Video](https://youtu.be/K9FRyqzju00)


# Development Environment

IDE: Visual Studio Code (with Java extensions)

Java Version: Java SE 17

Libraries Used:
    java.net.http for sending HTTP requests and receiving responses
    com.google.gson for parsing JSON responses from the OpenWeatherMap API


# Useful Websites

{Make a list of websites that you found helpful in this project}

- [W3Schools](https://www.w3schools.com/java/default.asp)
- [Oracle](https://docs.oracle.com/en/java/javase/17/index.html)
- [Beginners Book](https://beginnersbook.com/java-collections-tutorials/)
- [ChatGPT](https://chatgpt.com/)


# Future Work

- Add better error handling and user feedback for edge cases (e.g., invalid city names, network issues)
- Implement a GUI using JavaFX or Swing to make the app more user-friendly
- Allow users to search and compare multiple cities in one session
- Include more weather details like wind speed, sunrise/sunset times, and forecasts
- Encrypt or hide the API key using environment variables or a secure config file
