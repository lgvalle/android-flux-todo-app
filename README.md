# Flux Architecture on Android

Finding a good architecture for Android applications is not easy. Google seems to not care much about it, so there is no official recommendation on patterns beyond Activities lifecycle management.

But defining an architecture for your application is important. Like it or not, **every application is going to have an architecture**. So you'd better be the one defining it then let it just emerge.

## Today: Clean Architecture

Current trend is to adapt **[Clean Architecture][clean-architecture]**, a 2012 Uncle Bob proposal for web applications.

I find Clean Architecture a little bit **over-engineered** for most of the Android apps out there.

Typically **mobile apps live shorter than web apps**. Mobile technology is evolving so fast that any app released today is going to be completely deprecated in twelve months. 

Mobile apps usually **do very little**. A very high percent of use cases are just for data consuming. Get data from API, show data to user. Lot of reads, very little writes. 

As a result it's **business logic is not complex**. At least not as complex as backend apps. Well you have to deal with platform issues: memory, storage, pause, resume, network, location, etc. But that is not your app business logic. You have all of that in every app.

So it seems that most of the apps out there will not benefit from things like  complex layer divisions or job priority execution queues.

They may just need a **simple way to organise code, work together efficiently and find bugs easily**.

## Introducing Flux Architecture

**[Flux Architecture][flux-arch]** is used by Facebook to build their client- side web applications. Like _Clean Architecture_ it is not intended for mobile apps, but its features and simplicity will allow us to adapt it very well to Android projects.

![flux-graph-simple]
*Flux Architecture*

There are two **key features** to understand Flux:

* The data flow is always **unidirectional**.

	An [unidirectional data flow][unidirectional] is the **core** of the Flux architecture and is what makes it so easy to learn. 
It also provides great advantages when testing the application as discussed below.

* The application is divided into **three main parts**:

	-  **View**: Application interface. It create actions in response to user interactions.
	- **Dispatcher**: Central hub through which pass all actions and whose responsibility is to make them arrive to every Store.
	- **Store**: Maintain the state for a particular application domain. They respond to actions according to current state, execute business logic and emit a _change_ event when they are done. This event is used by the view to update its interface.

This three parts communicate through **Actions**: Simple plain objects, identified by a type, containing the data related to that action.


## Flux Android Architecture

The main target of using Flux principles on Android development is to build an architecture with a good balance between simplicity and ease of scale and test. 

First step is to **map Flux elements with Android app components**.

Two of this elements are very easy to figure out and implement.

- **View**: Activity or Fragment
- **Dispatcher**: An event bus. I will use Otto in my examples but any other implementation should be fine. 

### Actions

Actions are not complex either. They will be implemented as simple POJOs with two main attributes:

- Type: a `String` identifying the type of event.
- Data: a `Map` with the payload for this action.

For example, a typical action to show some User details will look like this:

```java
Bundle data = new Bundle();
data.put("USER_ID", id);
Action action = new ViewAction("SHOW_USER", data);
```


### Stores

This is perhaps the **most difficult** to get Flux concep. 

Also If you have worked with Clean Architecture before it also will be uncomfortable to accept, because Stores will assume responsibilities that were previously separated into different layers.

Stores contain the **status of the application and it business logic**. They are similar to _rich data models_ but they can manage the status of **various objects**, not just one. 

Stores **react to Actions emitted by the Dispatcher**, execute business logic and emit a change event as result.

Stores only output is this single event: _change_. Any other component interested in a Store internal status must listen to this event and use it to get the data it needs. 

No other component of the system should need to know anything about the status of the application.

Finally, stores must **expose an interface** to obtain application Status. This way, view elements can query the Stores and update application UI in response.

![flux-graph-store]
*Flux Store overview*

For example, in a Pub Discovery App a SearchStore will be used to keep track of searched item, search results and the history of past searches. In the same application a ReviewedStore will contain a list of reviewed pubs and the necessary logic to, for example, sort by review.

However there is one important concept to keep in mind: **Stores are not Repositories**. Their responsibility is *not* to get data from an external source (API or DB) but only keep track of data provided by actions. 

So how Flux application obtain data?

## Network requests and asynchronous calls

In the initial Flux graph I intentionally skipped one part: **network calls**. Next graph completes first one adding more details:

![flux-graph-complete]

Asynchronous network calls are triggered from an **Actions Creator**.
A Network Adapter makes the asynchronous call to the corresponding API and returns the result to the Actions Creator. 

Finally the Actions Creator dispatch the corresponding typed Action with returned data.

Having all the network and asynchronous work out of the Stores has has **two main advantages**:

- **Your Stores are completely synchronous**: This makes the logic inside a Store very easy to follow. Bugs will be much easier to trace. And since **all state changes will be synchronous** testing a Store becomes an easy job: launch actions and assert expected final state.

- **All actions are triggered from an Action Creator**: Having a single point at which you create and launch all user actions greatly simplifies finding errors. 
Forget about digging into classes to find out where an action is originated. **Everything starts here**. And because asynchronous calls occur _before_, everything that comes out of ActionCreator is synchronous. This is a huge win that significantly improves traceability and testability of the code.

## Show me the code: To-Do App


[In this example][android-app] you will find a classical **To-Do App** implemented on Android using a Flux Architecture. 


I tried to keep the project as simple as possible just to show how this architecture can produce **very well organised apps**. 

Some comments about implementation:

  * The `Dispatcher` is implemented using Otto Bus. Any bus implementation will mostly work. There is a **Flux restriction** on events I’m not applying here. On original Flux definition dispatching an event before previous one has finish is forbidden and will throw an exception. To keep the project simple I’m not implementing that here.

  * There is an `ActionsCreator` class to help creating `Actions` and posting them into the `Dispatcher`. Is a pretty common pattern in Flux which keeps things organised.
  
  *  `Actions` types are just `String` constants. Is probably not the best implementation but is quick and helps keeping things simple.
    
  Same thing with `Actions` data: they are just a `HashMap` with a `String` key and `Object` as a value. This forces ugly castings on Stores to extract actual data. Of course, this is not type safe but again, keeps the example easy to understand.

## Conclusion

There is **no such thing as the Best Architecture for an Android app**. 
There _is_ the Best Architecture for your current app. And it is the one that let you collaborate with your teammates easily, finish the project on time, with quality and as less bugs as possible.

I believe Flux is very good for all of that.

## Sample source code

**[https://github.com/lgvalle/android-flux-todo-app][android-app]**


## Further Reading:
* [Facebook Flux Overview][flux-arch]
* [Testing Flux Applications](https://facebook.github.io/flux/docs/testing-flux-applications.html#content)
* [Flux architecture Step by Step](http://blogs.atlassian.com/2014/08/flux-architecture-step-by-step/)
* [Async Requests and Flux] (http://www.code-experience.com/async-requests-with-react-js-and-flux-revisited/)
* [Flux and Android](http://armueller.github.io/android/2015/03/29/flux-and-android.html)


[clean-architecture]: https://blog.8thlight.com/uncle-bob/2012/08/13/the-clean-architecture.html
[flux-arch]: https://facebook.github.io/flux/docs/overview.html
[unidirectional]: https://www.youtube.com/watch?v=i__969noyAM

[flux-graph-simple]: https://raw.githubusercontent.com/lgvalle/lgvalle.github.io/master/public/images/flux-graph-simple.png 

[flux-graph-complete]: https://raw.githubusercontent.com/lgvalle/lgvalle.github.io/master/public/images/flux-graph-complete.png

[flux-graph-store]: https://raw.githubusercontent.com/lgvalle/lgvalle.github.io/master/public/images/flux-store.png

[android-app]: https://github.com/lgvalle/android-flux-todo-app



## Thanks
Special thanks to my colleague [Michele Bertoli](https://twitter.com/MicheleBertoli) for taking the time to introduce me to Flux and for reviewing this post.
