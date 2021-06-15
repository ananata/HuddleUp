# HuddleUp! - Sports Team Management

<img src="https://github.com/ananata/HuddleUp/blob/main/Desktop/HuddleUp/App_Screens/implemented_splash_screen.PNG" alt="HuddleUp Splash Screen" width="45%"/>

Created an app that assists coaches with the remote/virtual management of their team(s). This document will attempt to provide instructions on how to use our app, as well as describe all the functions we were able to implement into our app. Additional screenshots can be viewed at the following [link](https://github.com/ananata/HuddleUp/tree/main/Desktop/HuddleUp/App_Screens). 

## Implementations
### General Requirements
|General Requirements | Implementation | 
|-------------|----------------|
|Users can create an account with a password | Our app has registration and login screens that allow a user to create and access a personal account.|
|Coaches can create teams that athletes can subscribe to only with approval from the coach|Teams can be joined by selecting the "Join" button on a team page and entering the username and role of a registered user. Pressing submit sends a notification to the team's coach/owner (players cannot block this notification from being sent).|
|Coaches can schedule events for a team that notifies all his/her players.| Player notification is built into team creation. As soon as a new event is created, a new message is written to all associated players.|
|The app should be designed to support any possible team configuration.|Custom sports can be created on the app by selecting the "mystery" app icon and entering details about the new sport in the team form.|
|Login is required to interact with any part of the app| No features of the app can be accessed without a successful login and/or registration from the user.|
|The app homescreen should include a user profile, user calendar, team search, and settings | The homescreen has links that navigate to different screens that handle each requirement individually. Also, we created a tool bar that enables the convenient access of any of these screens from the anywhere in the app.|
|User interface is visually pleasing|UI design was well thought out.|
|Password recovery options are available and provide some security |Our password recovery screen is fully implemented and functional.|
|Users (both coaches and athletes) should be able to hide their account from public search.|Users can specify their privacy settings on the settings screen. However, by default, all users are public.|
|Athletes can view profiles of other members in their team.|Players can view all members of their team from the team screen.|

### Login and Account Requirements
|Login Requirements | Implementation | 
|-------------|----------------|
|Account creation requires verification via email | A verification email is sent to users immediately following registrations. |
|Accounts are stored to allow data persistence| All user information is saved to a database.|
|Login will require username or email with password |Login screen enables this requirement.|
|Passwords must be 6+ characters in length, contain at least 1 character and 1 number|Passwords follow all guidelines + 1 special character|
|The app shouldn't carry user's sensitive information|Sensitive information is protected.|
|Only one email allowed per account|Users are unable to access a single account from multiple password and/or usernames.|
|Passwords must remain hidden| Passwords are only accessed during login, registration, and recovery - they cannot be viewed or accessed at any other time.|

### Profile Requirements
|Profile Requirements | Implementation | 
|-------------|----------------|
|Profiles for coaches and players should contain user's name, profile image, email address, user bio/coach accreditations, and affiliated teams| Profile screens contain all required features.|
|There is no limit on how many teams a person can create or join|Users can join and or create as many teams as they would like. As well as add as many players to their teams as they desire (players must be entered one at a time)|
|There is no strict qualification to be a coach|Any user can create a team where they can assign themselves as coach.|
|Teams are created by only one coach|Teams can be owned by only one user who is assigned as head coach by default, however, multiple coaches can be added to a team. |
|Teams have both players and coaches and coaches are in charge of specifying the positions/roles atheletes play.|Player roles can be specified by coaches.|

## Authors

* **Nana Afia Twumasi-Ankrah**, Programmer, App Designer
* **Cade Weiskopf**, Programmer
* **Christopher "Dillon" DeVore**, Programmer

## Acknowledgments

* All illustrations used in the app were obtained from [undraw.co](https://undraw.co/), an open source illustration website.
* Unoriginal code obtained from external sources were acknowledged in-app. 
