# Google-Awareness-Demo

##What is Awarness API?
A unified sensing platform enabling applications to be aware of multiple aspects of a users context, while managing battery and memory health.

####There are two types of awarness APIs. 
  1. Snapshot APIs.
  2. Fence  APIs.

##Snapshot APIs:
* This app demo icludes usage of _7 diffrent types of apis_.
  1. **User Activity** - Returns user's current activity. (e.g. Walking, still, in vehicle etc.)
  2. **Time** - Returns user's current local time
  3. **Headphones** - Status of headphones.
  4. **Location** - Current location
  5. **Places** - Near by places
  6. **Beacons** - Nearby beacons
  7. **Weather** - Weather condition at current location.
* The `SnapshotApiActivity.java` contains all these APIs and their demos.
* For more details visit this [link](https://developers.google.com/awareness/android-api/snapshot-api-overview).

##Fence Apis:
* Available fences:
  1. **DetectedActivityFence** - Detect condition based on user's activity
  2. **HeadphoneFence** - Detect condition based on headphones plugging in state.
  3. **TimeFence** - Detect condition based on local time at user's location.
  4. **LocationFence** - Detect condition based on user's location.
  5. **BeaconFence** - Detect condition based on nearby Beacons' state.
* [`HeadphoneFenceApiActivity.java`](https://github.com/CommonUtils/Google-Awareness-Demo/blob/master/app/src/main/java/example/awarnessapi/HeadphoneFenceApiActivity.java) and [`ActivityFenceApiActivity.java`](https://github.com/CommonUtils/Google-Awareness-Demo/blob/master/app/src/main/java/example/awarnessapi/ActivityFanceApiDemo.java) will demostarate how to use this fences without combining them with other fences.
* For more details visit this [link](https://developers.google.com/awareness/android-api/fence-api-overview).

####Combining diffrent feces: 
* There are _3 operators_ to combine diffrent fences and generate desire conditons: 
  1. **OR condition**- Performs _or_ conditons between two fences unsing [`AwarenessFence.or()`](https://developers.google.com/android/reference/com/google/android/gms/awareness/fence/AwarenessFence.html#or(com.google.android.gms.awareness.fence.AwarenessFence...)).
  2. **AND condition**- Performs _and_ conditons between two fences unsing [`AwarenessFence.and()`](https://developers.google.com/android/reference/com/google/android/gms/awareness/fence/AwarenessFence.html#and(com.google.android.gms.awareness.fence.AwarenessFence...)).
  3. **NOT condition**- Performs _not_ conditons between two fences unsing [`AwarenessFence.not()`](https://developers.google.com/android/reference/com/google/android/gms/awareness/fence/AwarenessFence.html#not(com.google.android.gms.awareness.fence.AwarenessFence)).
* [`CombineFenceApiActivity.java`](https://github.com/CommonUtils/Google-Awareness-Demo/blob/master/app/src/main/java/example/awarnessapi/CombineFenceApiActivity.java) demostarates how you can combine diffrent fences and generate desire conditions.

##### You can download demo apk from [here](https://github.com/CommonUtils/Google-Awareness-Demo/blob/master/Demo.apk).
