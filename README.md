## Akkachat

Akkachat is slack like application written in scala to *learn* different aspect of scala programming language like functional programming, actor based concurrency, reactive streams

Its also an exercise to use one language for backend server, web and mobile fronted

# Frameworks used
- Akka
- Spray
- ScalaJS
- ScalaJS React Interface
- ScalaJS React Native Interface

# Features
1. Create Org
2. Create Channel within org
3. Add user to Channel
4. Add message in channel - plain, plain + emojis, markdown syntax text
5. Direct Message to any other user within Org
6. Tag any other user inside chat message

#Current status

Basic skeleton done with one simple REST service added without any persistence

*TODOs*
1. Add actor implementation and its for basic functions of chat as listed above
2. Add persistence for in-memory data so that when server is restarted it remembers all organizations, users and channels
3. Modify project and add SBT support for scalajs - web
4. Modify project and add SBT support for scalajs - react-native with basic skeleton app
5. Add module to start web interface within spray server
