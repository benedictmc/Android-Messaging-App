# Android Messaging app using Firebase
This is my messaging application I made for android. It implements google firebase to handle user authentication, using Google sign in as messaging profile. I have plans in the future to add Facebook login and email and password as authentication methods. 
Messages are saved on persisting firebase database, messages will be updated on each client’s view as messages are sent. A recyclerview was used as opposed to a listview to display messages to improve overall performance.
Future features for this app include:
*	Friends list/Address book feature
*	Group chat functionality
*	Display profile picture beside messages
*	Allow user to update and change profile picture

### Prerequisites

* Android SDK min 17
* Android Support Repository
* Google Play Services
* Firebase Core


### Installing

Here is how to install my app.
Download or clone the main repository folder. Run 
```
gradlew build
```
or import project into Android Studio.

## Built With

Google Firebase: https://firebase.google.com/
Android Studio: https://developer.android.com/studio/index.html

## Acknowledgments

Thanks to AndroidHive and code.tutsplus for reference tutorials.
* https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
* https://code.tutsplus.com/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397

## Authors

Built my me, Benedict McGovern 2017


