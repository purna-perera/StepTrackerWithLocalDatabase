# Step Tracker with Local Database

## Setup instructions
To install the app, please download the APK from the releases section or navigate to it in the repo
via .../app/release/app-release.apk. Once the apk is downloaded onto your Android device, simply
allow installation permission and it begin the installation.

Once the application is opened, there are five main available actions.
1. Toggle step counter: By clicking the switch in the bottom right labelled "Count steps" you can
   turn the step counter on and off. To start recording your steps in the app, toggle this on.
2. Mock steps: If you want to simulate a step being taken to experience the app without having to 
   physically take one, you can click the "Mock steps" button in the bottom left.
3. Reset step count: If you want to reset your step count back to 0, click the button labelled 
   "Reset" in the center of the screen.
4. View history: If you want to view your step history which is reported once per minute when the 
   step counter is toggled on, click the "Show history" button in the top left.
5. Clear history: Once the history tab is opened, you can clear your history by clicking the "Clear
   history" button on the top right.

## Device Requirements
Minimum version: API Level 24/Android 7.0, Nougat (2016)
Target version: API Level 34/Android 14, Upside Down Cake (2023)

- Please note that the application has not been test on versions below Android 12 and that therefore
  behaviour may be unpredictable on older versions.
- The application requires a step sensor which typically isn't available on emulators, tablets, TVs
  and some wearables. However, it should be available on most modern phones.

## Limitations and Assumptions
One thing to note is that the step count processing and writing to history happens in a foreground
service which start when the step counter is toggled on. Therefore, even if the app is killed, steps
will continue to be recorded. While convenient, this is constantly using CPU in the background and 
disk space to write the history which can grow unwieldly if the user forgets they've left it on.

Another limitation is that once the step counter has been toggled on, we must wait for the first
callback of the sensor to know where to start our offset from (since the sensor callback only
provides us with the total steps taken since boot rather than the steps taken in a given interval).
Therefore, we can't count any steps taken between the time in which the user activates the counter
and the first callback is received. The user is warned about this via the "Calibrating step 
counter..." message.

## Notes on architecture choices
### Persistent storage
While both Room and DataStore were considered, ultimately SharedPreferences was used for persistent
storage. Room while powerful, requires a lot of setup and since we don't need any advanced data
querying in this application, having a full SQLite ORM database seemed superfluous. DataStore, while
being a better alternative also required more setup than SharedPreferences. SharedPreferences' design
which includes asynchronous disk updates while having instant updates to the copy of the data in
memory, allowed us to have a simpler design than we would have with DataStore, since we didn't need
to manage out own local variable for UI relevant data and didn't have to worry about accidentally
triggering disk updates from the main thread.

### Use of singletons
While singletons often aren't used in applications due to issues with scoping and lifecycle, in this
application their use allowed us to simply our solution. Since this app only has a single activity
and page, managing the lifecycles of the singletons wasn't an issue. Secondly, since there was only
one module with inter-related functionality, scoping the singletons also wasn't an issue, allowing
them to reduce our boiler plate without any downsides.

### Lack of ViewModels
Typically, we would have ViewModels between the compose layouts and models (singletons in this
case). However, since most of the interactions between the models and views were directly collecting
state flows or calling context dependant functions, there weren't many concerns that could be
separated into the ViewModels.
