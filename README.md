# Hubiquitus Android
Hubiquitus client API for Android. Should be used with a hNode.
Currently allows to do pubsub with an xmpp server. More information on [hubiquitus](www.hubiquitus.com)

## Third party libraries
This project currently relies on
* Socket.io-java-client : Socket io. Using this [fork](https://github.com/Gottox/socket.io-java-client)
* PhoneGap : PhoneGap for android. See [here](http://phonegap.com/) for more informations

## How to use it

### As an Android project
Coming soon (with first stable release)

### As a PhoneGap Plugin
Coming soon (with first stable release)

## How to test it

#### 1) clone git project

    git clone git://github.com/hubiquitus/hubiquitus-android.git
    
#### 2) Import examples
For android: 

You will find hAPI-android-v0.5.0.jar in the hAPI-android/target.
You can import the file jar directly to your project.

For android PhoneGap:
* Copy the directory hapiPhoneGapPlusin to your project. Add the link source to the directory.
* Copy the directory examples/PhoneGapTest/assets/www/js/HClientPhoneGap to yourproject/assets/www/


#### 3) config AndroidManifest.xml
Add "android:name="org.hubiquitus.hapi.util.MyApplication" in "Application".

## Licensing
(The MIT License)

Copyright (c) Novedia Group 2012.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
'Software'), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

You should have received a copy of the MIT License along with Hubiquitus.
If not, see <http://opensource.org/licenses/mit-license.php>.

