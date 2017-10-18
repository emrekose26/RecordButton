# Record Button
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)]()
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RecordButton-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/6351)

![](https://raw.githubusercontent.com/emrekose26/RecordButton/master/art/recordbutton.gif)




 This is a library which can you create a record button view in android


# Download
#### 1.Add this in your root `build.gradle` at the end of repositories:
    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
  
#### 2.Add this dependency in your app level `build.gradle`:
    dependencies {
        ...
       compile 'com.github.emrekose26:RecordButton:1.0'
    }


# Usage
### 1. In your layout XML file:
```xml
<com.emrekose.recordbutton.RecordButton
    android:id="@+id/recordBtn"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:buttonGap="20"
    app:buttonRadius="100"
    app:maxMilisecond="10000"
    app:progressColor="@color/colorPrimary"
    app:progressStroke="15"
    app:recordIcon="@drawable/ic_keyboard_voice_white_36dp" />
```
  
### 2. In your class file:

```java
RecordButton recordButton = (RecordButton) findViewById(R.id.recordBtn);

        recordButton.setRecordListener(new OnRecordListener() {
            @Override
            public void onRecord() {
                Log.e(TAG, "onRecord: ");
            }

            @Override
            public void onRecordFinish() {
                Log.e(TAG, "onRecordFinish: ");
            }
        });
```

# License
    Copyright 2017 Emre Köse

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.