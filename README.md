# Mental Survival
Survival game powered by Java & LibGDX! Available for Android devices.

You can either play the game on your Android device by downloading it from Google Play or build your own version of the game.

[![](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=fi.tuni.mentalsurvival)

## Screenshots
![](https://play-lh.googleusercontent.com/RYh7u70fS6k8UNGhsdS5sPXQaOER5nsdPYXo86SaPFBqBn6B4rzuS70YsWeQATuDGA=w720-h310-rw)
![](https://play-lh.googleusercontent.com/eGkM5ZvzCvM1oAfQFx-o5AWOmdHhkuT3qikR1Nqs0vegt-KQPSd8qKk4HAn8LS6k1Iw=w720-h310-rw)

## Requirements
* Java (JDK14+ recommended)
* Git

## Building
1. Clone the repo
```shell 
git clone https://github.com/joonaramo/mental-survival.git
```
```shell
cd mental-survival
```
2. You can build for preferably Android or desktop.
```shell
./gradlew android:assembleRelease
```
or
```shell
./gradlew desktop:dist
```

3. Done! 

Android build folder: `android/build/outputs/apk/*.apk`

Desktop build folder: `desktop/build/libs/*.jar`

## Playing from build
To play the game, you can run the generated jar-file on a desktop. 

To play it on mobile, you have to download it to your phone and run the apk-file to install it. You should have Unknown sources enabled from Andoird security settings!
