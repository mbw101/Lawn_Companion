# Lawn Companion
An android app written in Kotlin that will assist with optimal lawn care needs!

Published on the Google Play Store: https://play.google.com/store/apps/details?id=com.mbw101.lawn_companion&hl=en

# Setup:
Create a gradle.properties file and copy the OpenWeatherMap api key into the file in Android Studio in the format of:
```open_weather_map_api_key="API KEY HERE"```

Create the gradle.properties file by clicking File->New->New File
Type "gradle.properties"

These 2 other lines are needed in the gradle.properties file for the project to build and work properly:

```
android.useAndroidX=true
android.enableJetifier=true
```

# Testing:
I have included unit tests and Espresso automated UI tests for various parts of the app. They are located in the androidTest and test directories in ```Lawn_Companion/app/src/```.
