# Hubiquitus Android
This is the Hubiquitus client API for Android. It should be used with a hNode.
It currently allows to do pubsub with an xmpp server. For more information, please visit [hubiquitus.com](www.hubiquitus.com).

## Third party libraries
This project currently relies on:

* [`Java-WebSocket`](https://github.com/TooTallNate/Java-WebSocket)

## Integration

1. Download and import this project into Android Studio.
2. Launch the `assemble` and `uploadArchives` targets (from Android Studio via the Gradle view or a build configuration, or via the command line). The resulting archive will be deployed in your local Maven repository (.m2).
3. In your main project, import the Hubiquitus API by adding in your build.gradle:

* Your local Maven repository:

```
allprojects {
	repositories {
		...
		mavenLocal()
		...
	}
}
```

* The dependency to the Hubiquitus API:

```
dependencies {
	...
	compile 'com.hubiquitus:android-sdk:0.9.2'
	...
}
```

## Usage

TBD

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

