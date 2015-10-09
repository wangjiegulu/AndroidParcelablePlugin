# AndroidParcelablePlugin
Intellij IDEA(Android Studio) Plugin for Android Parcelable.

##Implement Parcelable interface: 
```java
package com.wangjie.idea.plugin;
public class Person{
    private int id;
    private String name;
    private Float height;
    private Double weight;
    private Byte gender;
    private Boolean deleted;
    private Long birth;
}
```
== generate... ==>
```java
package com.wangjie.idea.plugin;
import android.os.*;

public class Person implements Parcelable {
    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }

        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }
    };
    private int id;
    private String name;
    private Float height;
    private Double weight;
    private Byte gender;
    private Boolean deleted;
    private Long birth;

    public Person(Parcel in) {
        id = in.readInt();
        name = in.readString();
        height = in.readFloat();
        weight = in.readDouble();
        gender = in.readByte();
        deleted = 1 == in.readByte();
        birth = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeFloat(height);
        out.writeDouble(weight);
        out.writeByte(gender);
        out.writeByte((byte) (deleted ? 1 : 0));
        out.writeLong(birth);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
```

## How to use
### 1. Install the "Parcelable Generator For Android" plugin.
Preferences --> Plugins --> Install Plugin from disk --> restart
<img src='screenshot/01.png' height='500px'/>
###2. Click "Generate Parcelable" in "Code" menu.
<img src='screenshot/02.png' height='500px'/>

License
=======

    Copyright 2015 Wang Jie

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing blacklist and
    limitations under the License.
