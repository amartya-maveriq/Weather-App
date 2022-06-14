# Weather-App

### What the app does...

Well, it tells the weather. Duh. Plus some more cool stuffs. Like,

1. Gives you a heads-up of 3 days for the upcoming weather (* 🚴🏻‍♂️ so that you can plan your bike rides better*)
2. Tells the UV Index for the current location (* ☀️ in case you're willing to carry the sunscreen along*)
3. Lets you know how fast the wind is blowing outside (*💃🏻 useful for girls deciding to wear skirts*)
4. Informs you of the humidity (* 🧴 you can decide on the moisturizer*)
5. And also the visibility outside (🤦‍♂️*...mmm don't know any possible use of it actually *)
6. You can search for another place/city 🌆 in case you're planning for a holiday 
7. And... You will get notified of the current weather when you're sleeping peacefully at 6 o'clock

-----

### Screenshots

<table>
  <tr>
    <td><img src="https://user-images.githubusercontent.com/81750154/173509134-690dd3ee-da4c-4262-918f-5562b01c5468.JPG" width="200" height="300" /></td>
    <td><img src="https://user-images.githubusercontent.com/81750154/173509152-e4f5e12a-e185-4ea5-aef2-ddc1a43a7bbb.JPG" width="200" height="300" /></td>
    <td> <img src="https://user-images.githubusercontent.com/81750154/173509173-a237a6f4-4b34-4a6d-a4b9-b57bc1f73aa8.JPG" width="200" height="300" /></td>
    <td> <img src="https://user-images.githubusercontent.com/81750154/173509196-e5544171-47d8-4970-92d7-caf510c0e78d.JPG" width="200" height="300" /></td>
  </tr>
</table>

----

### About the project

- This project is written in **Kotlin** and follows **MVVM** design pattern.
- The app runs on a singular activity and uses **Navigation** to switch between separate fragments.
- For network calls it uses **Retrofit** library & for image loading it uses **Glide**
- For saving/reading the favorite cities in-app and caching home page data **Room** has been used as Database Helper
- For better user privacy the app does not access Exact user location and uses Coarse location using **FusedLocationProvider** API
- And last but not the least, **Dagger + Hilt** has been used for Dependency Injection.

-----

### Screens in the app

There are 5 screens in total, namely:
1. Home page (where users see their current location's nearby weather)
2. Favorites page (where users can see/delete their saved locations)
3. Search page from where users can search for any location/city of their choice
4. Location details page where weather data for that location is found
5. Settings bottom sheet where users can change unit of measurement in app (metric to imperial & vice versa)

-----

### What could have been done also...

Well, the app could have 
- Some unit tests
- A CSV file exporter for weather data
- some more re-usability of code

👍👍 These stuffs will be included soon...
